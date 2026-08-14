package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.erkko68.filament.gltfio.Animator

/**
 * Hoisted, observable playback state for a single glTF [Animator], created with
 * [rememberAnimationState] and passed to [GltfInstance]. It auto-advances on every frame and
 * drives the animator each frame in one of two modes:
 *
 * **Single-track (the convenience path).** Set [animationIndex] and the active clip plays, looping
 * at its length; assigning a new index cross-fades from the outgoing clip over [crossFadeDuration].
 * This covers the common "play idle, blend into walk" case without touching the mixer.
 *
 * **Multi-track [mixer].** Add tracks with [rememberAnimationTrack] and the state blends them by
 * [AnimationTrack.weight] every frame — a blend tree (e.g. idle↔walk↔run driven by a speed parameter).
 * While the [mixer] has tracks it takes precedence and [animationIndex] is ignored. Filament blends two
 * clips per call, but weighted cross-fades chain, so an arbitrary number of weighted clips compose into
 * one pose.
 *
 * For an imperative game loop (an animation state machine outside composition), use [AnimationMixer]
 * directly instead of this hoisted state — see [rememberAnimationMixer].
 *
 * Per-bone masks and additive layers are *not* expressible — the native animator blends the whole
 * skeleton. For that, or any fully manual control, use [GltfInstance]'s `onUpdate` and call the
 * [Animator] directly.
 *
 * ```kotlin
 * // Single-track with auto cross-fade:
 * val anim = rememberAnimationState(initialAnimationIndex = idle, initialCrossFadeDuration = 0.25f)
 * GltfInstance(asset = character, animationState = anim)
 * anim.animationIndex = walk   // cross-fades
 *
 * // Blend tree — hold both clips and drive the weight from a parameter:
 * val anim = rememberAnimationState(initialAnimationIndex = null)
 * rememberAnimationTrack(anim, walkIndex, weight = 1f - speed)
 * rememberAnimationTrack(anim, runIndex,  weight = speed)
 * ```
 */
// @Stable: the observable surface is all snapshot-backed; currentIndex/currentTime/previousIndex/
// previousTime/blend are per-frame mixer scratch touched only from OnFrame.
@Stable
class AnimationState internal constructor(
    initialIndex: Int?,
    initialSpeed: Float,
    initialCrossFadeDuration: Float,
    initialLoop: Boolean,
) {
    /** The animation to play in single-track mode. Assigning cross-fades from the current one;
     *  null pauses. Ignored while the [mixer] has tracks. */
    var animationIndex: Int? by mutableStateOf(initialIndex)

    /** Playback rate multiplier for the single-track path (1 = real time, negative = backwards). */
    var speed: Float by mutableStateOf(initialSpeed)

    /** Cross-fade length in seconds applied when [animationIndex] changes. 0 = hard cut. */
    var crossFadeDuration: Float by mutableStateOf(initialCrossFadeDuration)

    /** Whether the single-track animation wraps at its end (true) or holds the final frame (false). */
    var loop: Boolean by mutableStateOf(initialLoop)

    /** Pauses advancement of every track and the single-track clock while true. */
    var isPaused: Boolean by mutableStateOf(false)

    private val _time = mutableFloatStateOf(0f)
    /** Current playback time of the single-track animation in seconds. Read-only, updates per frame. */
    val time: Float get() = _time.floatValue

    private val _duration = mutableFloatStateOf(0f)
    /** Normalized progress (0..1) of the single-track animation. 0 when the clip has no duration. */
    val progress: Float
        get() = if (_duration.floatValue > 0f) (_time.floatValue / _duration.floatValue).coerceIn(0f, 1f) else 0f

    private val _transitioning = mutableStateOf(false)
    /** True while a single-track cross-fade transition is in progress. Read-only. */
    val isTransitioning: Boolean get() = _transitioning.value

    /** The weighted track mixer for the blend-tree path. Add tracks with [rememberAnimationTrack];
     *  while it has tracks it drives playback and [animationIndex] is inert. */
    val mixer: AnimationMixer = AnimationMixer()

    /** Resets the single-track clock to [time] seconds. */
    fun seek(time: Float) { _time.floatValue = time }

    // Single-track bookkeeping — mutated only from the frame callback, never read during
    // composition, so plain fields (not snapshot state) are correct and cheaper.
    private var currentIndex: Int? = initialIndex
    private var currentTime = 0f
    private var previousIndex: Int? = null
    private var previousTime = 0f
    private var blend = 1f  // 1 = fully on the current clip

    /** Advances playback by [dtSeconds] and pushes the result onto [animator]. Frame-thread only. */
    internal fun apply(animator: Animator, dtSeconds: Float) {
        val dt = if (isPaused) 0f else dtSeconds
        if (mixer.tracks.isNotEmpty()) mixer.apply(animator, dt)
        else applySingle(animator, animator.getAnimationCount(), dt)
    }

    private fun applySingle(animator: Animator, count: Int, dt: Float) {
        // A change to animationIndex starts a cross-fade from the outgoing clip.
        if (animationIndex != currentIndex) {
            previousIndex = currentIndex
            previousTime = currentTime
            currentIndex = animationIndex
            currentTime = 0f
            blend = if (crossFadeDuration > 0f && previousIndex != null) 0f else 1f
        }

        val ci = currentIndex ?: return
        if (ci !in 0 until count) return

        val duration = animator.getAnimationDuration(ci)
        currentTime = wrap(currentTime + dt * speed, duration, loop)
        animator.applyAnimation(ci, currentTime)

        if (blend < 1f) {
            blend = (blend + dt / crossFadeDuration).coerceAtMost(1f)
            val pi = previousIndex
            if (pi != null && pi in 0 until count) {
                previousTime = wrap(previousTime + dt * speed, animator.getAnimationDuration(pi), loop)
                // alpha is the weight of the *previous* clip: 1 → fully previous, 0 → fully current.
                animator.applyCrossFade(pi, previousTime, 1f - blend)
            }
            if (blend >= 1f) previousIndex = null
        }

        animator.updateBoneMatrices()

        _time.floatValue = currentTime
        _duration.floatValue = duration
        _transitioning.value = blend < 1f
    }
}

/**
 * A weighted, auto-advancing blend of several glTF clips — a blend tree. Each [AnimationTrack]
 * contributes its pose by [AnimationTrack.weight]; weights are normalized so they need not sum to 1.
 *
 * This is plain state with no `@Composable` entry point, so it suits an **imperative game loop**:
 * hold a mixer (e.g. via [rememberAnimationMixer]), mutate track weights from your own animation
 * state machine, and call [apply] every frame with the instance's [Animator] and the frame delta:
 *
 * ```kotlin
 * val mixer = rememberAnimationMixer()
 * val walk = remember { mixer.addTrack(walkIndex) }
 * val run  = remember { mixer.addTrack(runIndex, weight = 0f) }
 * GltfInstance(asset = character, onCreate = { animator = instance.getAnimator() })
 * OnFrame { frame ->
 *     walk.weight = 1f - moveSpeed; run.weight = moveSpeed
 *     animator?.let { mixer.apply(it, frame.deltaSeconds) }
 * }
 * ```
 *
 * In Compose, [AnimationState] already wraps one of these as [AnimationState.mixer] and drives [apply]
 * for you; [rememberAnimationTrack] is declarative sugar over [addTrack].
 *
 * Filament blends the whole skeleton per call, so per-bone masks and additive layers are not
 * expressible — drive the [Animator] yourself for that.
 */
@Stable
class AnimationMixer {
    /** Pauses advancement of every track while true; poses hold in place. Observable, like
     *  [AnimationState.isPaused]. */
    var isPaused: Boolean by mutableStateOf(false)

    private val _tracks = mutableStateListOf<AnimationTrack>()
    /** The active tracks, in blend order. */
    val tracks: List<AnimationTrack> get() = _tracks

    /** Creates a weighted track, adds it, and returns it for later mutation. */
    fun addTrack(
        index: Int,
        weight: Float = 1f,
        speed: Float = 1f,
        loop: Boolean = true,
        time: Float = 0f,
    ): AnimationTrack = AnimationTrack(index, weight, speed, loop, time).also { _tracks.add(it) }

    internal fun addTrack(track: AnimationTrack) { _tracks.add(track) }

    /** Removes a previously added track. */
    fun removeTrack(track: AnimationTrack) { _tracks.remove(track) }

    /** Removes every track. */
    fun clearTracks() { _tracks.clear() }

    // Weighted blend of every active track. Each contributing clip is folded into the running pose
    // with alpha = accumulatedWeight / (accumulatedWeight + thisWeight), the incremental form of a
    // normalized weighted average — so N clips compose without pre-normalizing weights.
    /** Advances every track by [dtSeconds] and pushes the blended pose onto [animator]. */
    fun apply(animator: Animator, dtSeconds: Float) {
        val dt = if (isPaused) 0f else dtSeconds
        val count = animator.getAnimationCount()
        var accum = 0f
        var applied = false
        for (t in _tracks) {
            val i = t.index
            if (i !in 0 until count) continue
            t.advance(dt, animator.getAnimationDuration(i))  // advance even at weight 0 to keep clocks in sync
            if (t.weight <= 0f) continue
            if (!applied) {
                animator.applyAnimation(i, t.time)
                accum = t.weight
                applied = true
            } else {
                animator.applyCrossFade(i, t.time, accum / (accum + t.weight))
                accum += t.weight
            }
        }
        if (applied) animator.updateBoneMatrices()
    }
}

/**
 * One weighted clip in an [AnimationMixer], created with [AnimationMixer.addTrack] (or
 * [rememberAnimationTrack]). Mutate [weight] to blend it in or out, [index] to swap the clip, or
 * [speed]/[loop] for playback. The read-only [time], [progress] and [isFinished] update every frame.
 */
class AnimationTrack internal constructor(
    index: Int,
    weight: Float,
    speed: Float,
    loop: Boolean,
    initialTime: Float,
) {
    /** Zero-based animation index this track samples. */
    var index: Int by mutableStateOf(index)

    /** Blend weight, relative to the other tracks. 0 mutes the track but keeps its clock running. */
    var weight: Float by mutableStateOf(weight)

    /** Playback rate multiplier (1 = real time, negative plays backwards). */
    var speed: Float by mutableStateOf(speed)

    /** Whether this track wraps at its end (true) or holds the final frame (false). */
    var loop: Boolean by mutableStateOf(loop)

    private val _time = mutableFloatStateOf(initialTime)
    /** Current playback time of this track in seconds. Read-only. */
    val time: Float get() = _time.floatValue

    private val _duration = mutableFloatStateOf(0f)
    /** Normalized progress (0..1) of this track. 0 when the clip has no duration. */
    val progress: Float
        get() = if (_duration.floatValue > 0f) (_time.floatValue / _duration.floatValue).coerceIn(0f, 1f) else 0f

    private val _finished = mutableStateOf(false)
    /** True once a non-looping track has reached the end of its clip. Always false while [loop]. */
    val isFinished: Boolean get() = _finished.value

    /** Resets this track's clock to [time] seconds. */
    fun seek(time: Float) { _time.floatValue = time }

    internal fun advance(dt: Float, duration: Float) {
        _duration.floatValue = duration
        val next = _time.floatValue + dt * speed
        _time.floatValue = wrap(next, duration, loop)
        _finished.value = !loop && duration > 0f && next >= duration
    }
}

private fun wrap(t: Float, duration: Float, loop: Boolean): Float = when {
    duration <= 0f -> t
    loop -> ((t % duration) + duration) % duration
    else -> t.coerceIn(0f, duration)
}

/**
 * Creates and remembers an [AnimationState]. The `initial*` values seed the state on first
 * composition only; afterwards mutate the returned object's fields to drive playback.
 *
 * @param initialAnimationIndex Animation to start playing in single-track mode. Null to start paused.
 * @param initialSpeed Playback rate multiplier.
 * @param initialCrossFadeDuration Seconds to blend over when [AnimationState.animationIndex] changes.
 * @param initialLoop Whether the active animation loops.
 */
@Composable
fun rememberAnimationState(
    initialAnimationIndex: Int? = 0,
    initialSpeed: Float = 1f,
    initialCrossFadeDuration: Float = 0.3f,
    initialLoop: Boolean = true,
): AnimationState = remember {
    AnimationState(initialAnimationIndex, initialSpeed, initialCrossFadeDuration, initialLoop)
}

/**
 * Adds a weighted track to [state]'s [AnimationState.mixer] for as long as this call stays in
 * composition, and keeps its [weight]/[index]/[speed]/[loop] in sync with the arguments. The track is
 * created once and removed automatically when the call leaves composition, so build a blend tree by
 * calling this once per clip and driving [weight] from your own state. Its clock keeps running across
 * [index]/[weight] changes; call [AnimationTrack.seek] to rewind.
 *
 * For an imperative (non-Compose) game loop, add tracks with [AnimationMixer.addTrack] instead.
 *
 * ```kotlin
 * val anim = rememberAnimationState(initialAnimationIndex = null)
 * rememberAnimationTrack(anim, walkIndex, weight = 1f - moveSpeed)
 * rememberAnimationTrack(anim, runIndex,  weight = moveSpeed)
 * ```
 *
 * @return The track, e.g. to read its [AnimationTrack.progress] or [AnimationTrack.isFinished].
 */
@Composable
fun rememberAnimationTrack(
    state: AnimationState,
    index: Int,
    weight: Float = 1f,
    speed: Float = 1f,
    loop: Boolean = true,
): AnimationTrack {
    val track = remember(state) { AnimationTrack(index, weight, speed, loop, 0f) }
    SideEffect {
        track.index = index
        track.weight = weight
        track.speed = speed
        track.loop = loop
    }
    DisposableEffect(state, track) {
        state.mixer.addTrack(track)
        onDispose { state.mixer.removeTrack(track) }
    }
    return track
}

/**
 * Creates and remembers a standalone [AnimationMixer] for imperative use — drive it from an `OnFrame`
 * callback with the instance's [Animator], mutating track weights from your own animation logic.
 * Prefer [AnimationState] + [rememberAnimationTrack] for the declarative path.
 *
 * ```kotlin
 * val mixer = rememberAnimationMixer()
 * val walk = remember { mixer.addTrack(walkIndex) }
 * var animator by remember { mutableStateOf<Animator?>(null) }
 * GltfInstance(asset = character, onCreate = { animator = instance.getAnimator() })
 * OnFrame { frame -> animator?.let { mixer.apply(it, frame.deltaSeconds) } }
 * ```
 */
@Composable
fun rememberAnimationMixer(): AnimationMixer = remember { AnimationMixer() }

/**
 * The clip names of [asset], indexed by animation index, or empty until the asset is ready. Look up
 * an index by name — `names.indexOf("Walk")` — instead of hard-coding integers. Entries are null
 * for unnamed clips.
 *
 * ```kotlin
 * val names = rememberAnimationNames(character)
 * val anim = rememberAnimationState(initialAnimationIndex = names.indexOf("Idle"))
 * ```
 */
@Composable
fun rememberAnimationNames(asset: GltfAsset?): List<String?> =
    remember(asset, asset?.isReady) {
        if (asset == null || !asset.isReady) emptyList()
        else asset.filamentAsset.getInstance().getAnimator().let { animator ->
            List(animator.getAnimationCount()) { animator.getAnimationName(it) }
        }
    }
