package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.erkko68.filament.gltfio.Animator

/**
 * Hoisted, observable playback state for a single glTF [Animator], created with
 * [rememberAnimationState] and passed to [GltfInstance]. It auto-advances on every frame and
 * cross-fades smoothly whenever [animationIndex] changes, covering the common
 * "play idle, blend into walk" case without dropping to the `onUpdate` escape hatch.
 *
 * Filament's animator blends exactly two clips at a time (the incoming clip plus the outgoing
 * one it is fading from), so this drives one active animation with an automatic transition
 * rather than an arbitrary N-track mixer. For full manual control use [GltfInstance]'s
 * `onUpdate` and call the [Animator] directly.
 *
 * ```kotlin
 * val anim = rememberAnimationState(animationIndex = 0)
 * GltfInstance(asset = character, animationState = anim)
 *
 * // later, from a click handler — cross-fades over crossFadeDuration seconds:
 * anim.animationIndex = walkIndex
 * ```
 */
class AnimationState internal constructor(
    initialIndex: Int?,
    initialSpeed: Float,
    initialCrossFadeDuration: Float,
    initialLoop: Boolean,
) {
    /** The animation to play. Assigning a new value cross-fades from the current one. Null pauses. */
    var animationIndex: Int? by mutableStateOf(initialIndex)

    /** Playback rate multiplier (1 = real time, negative plays backwards). */
    var speed: Float by mutableStateOf(initialSpeed)

    /** Cross-fade length in seconds applied when [animationIndex] changes. 0 = hard cut. */
    var crossFadeDuration: Float by mutableStateOf(initialCrossFadeDuration)

    /** Whether the active animation wraps at its end (true) or holds the final frame (false). */
    var loop: Boolean by mutableStateOf(initialLoop)

    private val _time = mutableFloatStateOf(0f)
    /** Current playback time of the active animation in seconds. Read-only, updates per frame. */
    val time: Float get() = _time.floatValue

    private val _transitioning = mutableStateOf(false)
    /** True while a cross-fade transition is in progress. Read-only. */
    val isTransitioning: Boolean get() = _transitioning.value

    // Playback bookkeeping — mutated only from the frame callback in GltfInstance, never read
    // during composition, so plain fields (not snapshot state) are correct and cheaper.
    private var currentIndex: Int? = initialIndex
    private var currentTime = 0f
    private var previousIndex: Int? = null
    private var previousTime = 0f
    private var blend = 1f  // 1 = fully on the current clip

    /** Advances playback by [dtSeconds] and pushes the result onto [animator]. Frame-thread only. */
    internal fun apply(animator: Animator, dtSeconds: Float) {
        val count = animator.getAnimationCount()

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

        currentTime = wrap(currentTime + dtSeconds * speed, animator.getAnimationDuration(ci))
        animator.applyAnimation(ci, currentTime)

        if (blend < 1f) {
            blend = (blend + dtSeconds / crossFadeDuration).coerceAtMost(1f)
            val pi = previousIndex
            if (pi != null && pi in 0 until count) {
                previousTime = wrap(previousTime + dtSeconds * speed, animator.getAnimationDuration(pi))
                // alpha is the weight of the *previous* clip: 1 → fully previous, 0 → fully current.
                animator.applyCrossFade(pi, previousTime, 1f - blend)
            }
            if (blend >= 1f) previousIndex = null
        }

        animator.updateBoneMatrices()

        _time.floatValue = currentTime
        _transitioning.value = blend < 1f
    }

    private fun wrap(t: Float, duration: Float): Float = when {
        duration <= 0f -> t
        loop -> ((t % duration) + duration) % duration
        else -> t.coerceIn(0f, duration)
    }
}

/**
 * Creates and remembers an [AnimationState]. Initial values seed the state on first
 * composition; afterwards mutate the returned object's fields to drive playback.
 *
 * @param animationIndex Animation to start playing. Null to start paused.
 * @param speed Playback rate multiplier.
 * @param crossFadeDuration Seconds to blend over when [AnimationState.animationIndex] changes.
 * @param loop Whether the active animation loops.
 */
@Composable
fun rememberAnimationState(
    animationIndex: Int? = 0,
    speed: Float = 1f,
    crossFadeDuration: Float = 0.3f,
    loop: Boolean = true,
): AnimationState = remember { AnimationState(animationIndex, speed, crossFadeDuration, loop) }
