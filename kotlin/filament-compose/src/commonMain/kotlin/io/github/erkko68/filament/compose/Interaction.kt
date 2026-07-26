package io.github.erkko68.filament.compose

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.onSizeChanged
import io.github.erkko68.filament.View
import io.github.erkko68.filament.compose.scene.CameraState
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.utils.Manipulator
import io.github.erkko68.filament.utils.radians
import kotlin.math.ln

// ── Shared internals ──────────────────────────────────────────────────────────

/**
 * Common surface shared by [OrbitCameraController], [MapCameraController] and [FlightCameraController] — the
 * operations that make sense for any manipulator-driven camera, so generic UI (a "reset view"
 * button, save/restore of viewpoints) can drive whichever controller is active without caring
 * about the control scheme. Mode-specific extras — flight's per-frame [FlightCameraController.update] —
 * live on the concrete types, not here.
 *
 * All three are created by their `remember*CameraController` factory; you never implement this yourself.
 */
interface CameraController {
    /** Viewport size in px. Kept in sync automatically by the matching gesture modifier; call
     *  manually only when driving the manipulator without one. */
    fun setViewport(width: Int, height: Int)

    /** Snap the camera back to the pose it had when this controller was created. */
    fun resetToHome()

    /** Capture the current camera pose as a bookmark to restore later with [jumpToBookmark]. */
    fun saveBookmark(): Manipulator.Bookmark

    /** Restore a pose previously captured with [saveBookmark]. */
    fun jumpToBookmark(bookmark: Manipulator.Bookmark)
}

private fun Manipulator.syncTo(cameraState: CameraState) {
    val e = FloatArray(3); val t = FloatArray(3); val u = FloatArray(3)
    getLookAt(e, t, u)
    cameraState.eye    = Position(e[0], e[1], e[2])
    cameraState.target = Position(t[0], t[1], t[2])
    cameraState.up     = Direction(u[0], u[1], u[2])
}

// Shared drag + pinch-zoom logic. [strafe] controls whether drag pans or orbits. Also keeps
// the manipulator's viewport in sync with the element's size, so callers don't have to wire
// onSizeChanged/setViewport by hand.
private fun Modifier.manipulatorDragGestures(
    manipulator: Manipulator,
    alwaysStrafe: Boolean,
    onSync: () -> Unit,
): Modifier = this
    .onSizeChanged { manipulator.setViewport(it.width, it.height) }
    .pointerInput(manipulator) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            val strafe = alwaysStrafe || currentEvent.buttons.isSecondaryPressed
            var pinchMode = false
            var prevPinchDistance = 0f

            manipulator.grabBegin(down.position.x.toInt(), down.position.y.toInt(), strafe)

            while (true) {
                val event = awaitPointerEvent()
                if (event.changes.none { it.pressed }) break
                val pressed = event.changes.filter { it.pressed }

                when {
                    pressed.size >= 2 -> {
                        if (!pinchMode) {
                            pinchMode = true
                            manipulator.grabEnd()
                            prevPinchDistance =
                                (pressed[0].position - pressed[1].position).getDistance()
                        } else {
                            val dist = (pressed[0].position - pressed[1].position).getDistance()
                            val center = (pressed[0].position + pressed[1].position) / 2f
                            manipulator.scroll(
                                center.x.toInt(),
                                center.y.toInt(),
                                (prevPinchDistance - dist) * 0.005f,
                            )
                            prevPinchDistance = dist
                            onSync()
                            event.changes.forEach { it.consume() }
                        }
                    }
                    pressed.size == 1 && !pinchMode -> {
                        val change = pressed.first()
                        if (change.positionChanged()) {
                            manipulator.grabUpdate(change.position.x.toInt(), change.position.y.toInt())
                            onSync()
                            change.consume()
                        }
                    }
                }
            }

            if (!pinchMode) {
                manipulator.grabEnd()
                onSync()
            }
        }
    }
    .pointerInput(manipulator) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                if (event.type == PointerEventType.Scroll) {
                    val change = event.changes.firstOrNull() ?: continue
                    manipulator.scroll(
                        change.position.x.toInt(),
                        change.position.y.toInt(),
                        change.scrollDelta.y * 0.1f,
                    )
                    onSync()
                    event.changes.forEach { it.consume() }
                }
            }
        }
    }

// ── Orbit camera ──────────────────────────────────────────────────────────────

/**
 * Drives a [CameraState] using a Filament [Manipulator] in ORBIT mode.
 *
 * Create with [rememberOrbitCameraController], pass the same [CameraState] to
 * [FilamentView], and attach [Modifier.orbitGestures] to the view's surface. The gesture
 * modifier keeps the manipulator's viewport in sync with the element's size automatically.
 */
class OrbitCameraController internal constructor(
    internal val manipulator: Manipulator,
    private val cameraState: CameraState,
) : CameraController {
    override fun setViewport(width: Int, height: Int) = manipulator.setViewport(width, height)

    override fun resetToHome() { manipulator.jumpToBookmark(manipulator.getHomeBookmark()); sync() }

    override fun saveBookmark(): Manipulator.Bookmark = manipulator.getCurrentBookmark()

    override fun jumpToBookmark(bookmark: Manipulator.Bookmark) { manipulator.jumpToBookmark(bookmark); sync() }

    internal fun sync() = manipulator.syncTo(cameraState)
}

/**
 * Creates and remembers an [OrbitCameraController] that drives [cameraState].
 *
 * The manipulator's home position is taken from [cameraState]'s current `eye`/`target` at
 * creation time. The manipulator pushes its computed eye/target/up back into [cameraState]
 * whenever the user interacts.
 *
 * The tuning parameters ([zoomSpeed], [orbitSpeedX], [orbitSpeedY], [panningEnabled]) are
 * reactive — changing them at runtime rebuilds the manipulator while keeping the current
 * camera pose and the original home position, so nothing jumps.
 *
 * ```kotlin
 * val cameraState = rememberCameraState(initialEye = Position(0f, 2f, 5f))
 * val orbit = rememberOrbitCameraController(cameraState)
 * FilamentView(scene = scene, cameraState = cameraState, modifier = Modifier.orbitGestures(orbit))
 * ```
 *
 * @param zoomSpeed      Scroll / pinch zoom sensitivity.
 * @param orbitSpeedX    Horizontal orbit drag sensitivity.
 * @param orbitSpeedY    Vertical orbit drag sensitivity.
 * @param panningEnabled Allow right-click / secondary-button drag to pan.
 */
@Composable
fun rememberOrbitCameraController(
    cameraState: CameraState,
    zoomSpeed: Float = 0.01f,
    orbitSpeedX: Float = 0.01f,
    orbitSpeedY: Float = 0.01f,
    panningEnabled: Boolean = true,
): OrbitCameraController {
    // Home is fixed at the pose the cameraState had when this state was first created, so a
    // tuning-parameter rebuild doesn't silently redefine what resetToHome() means.
    val home = remember(cameraState) { cameraState.eye to cameraState.target }
    val previous = remember(cameraState) { arrayOfNulls<Manipulator>(1) }
    val state = remember(cameraState, zoomSpeed, orbitSpeedX, orbitSpeedY, panningEnabled) {
        val (eye, target) = home
        val manipulator = Manipulator.Builder()
            .orbitHomePosition(eye.x, eye.y, eye.z)
            .targetPosition(target.x, target.y, target.z)
            .zoomSpeed(zoomSpeed)
            .orbitSpeed(orbitSpeedX, orbitSpeedY)
            .panning(panningEnabled)
            .build(Manipulator.Mode.ORBIT)
        // Carry the current pose across a tuning rebuild so the camera doesn't move.
        previous[0]?.let { manipulator.jumpToBookmark(it.getCurrentBookmark()) }
        previous[0] = manipulator
        OrbitCameraController(manipulator, cameraState).also { it.sync() }
    }
    DisposableEffect(state) { onDispose { state.manipulator.destroy() } }
    return state
}

/**
 * Attaches orbit gesture handling to this element.
 *
 * | Input                          | Action |
 * |--------------------------------|--------|
 * | Single touch / left-click drag | Orbit  |
 * | Two-finger pinch               | Zoom   |
 * | Right-click drag               | Pan    |
 * | Scroll wheel                   | Zoom   |
 */
fun Modifier.orbitGestures(controller: OrbitCameraController): Modifier =
    manipulatorDragGestures(controller.manipulator, alwaysStrafe = false) { controller.sync() }

// ── Map camera ────────────────────────────────────────────────────────────────

/**
 * Drives a [CameraState] using a Filament [Manipulator] in MAP mode (top-down 2-D pan/zoom).
 * Pair with `Projection.Orthographic` on the [CameraState].
 */
class MapCameraController internal constructor(
    internal val manipulator: Manipulator,
    private val cameraState: CameraState,
) : CameraController {
    override fun setViewport(width: Int, height: Int) = manipulator.setViewport(width, height)
    override fun resetToHome() { manipulator.jumpToBookmark(manipulator.getHomeBookmark()); sync() }
    override fun saveBookmark(): Manipulator.Bookmark = manipulator.getCurrentBookmark()
    override fun jumpToBookmark(bookmark: Manipulator.Bookmark) { manipulator.jumpToBookmark(bookmark); sync() }

    internal fun sync() = manipulator.syncTo(cameraState)
}

/**
 * Creates and remembers a [MapCameraController] driving [cameraState] in MAP mode.
 *
 * Drag pans the view; scroll wheel and pinch zoom in/out. No rotation.
 *
 * The tuning parameters are reactive — changing them at runtime rebuilds the manipulator
 * while keeping the current camera pose, so nothing jumps.
 *
 * @param mapWidth    Pannable world extent along X.
 * @param mapHeight   Pannable world extent along Z.
 * @param minDistance Minimum zoom distance (closest the camera can get).
 * @param zoomSpeed   Scroll / pinch zoom sensitivity.
 */
@Composable
fun rememberMapCameraController(
    cameraState: CameraState,
    mapWidth: Float = 512f,
    mapHeight: Float = 512f,
    minDistance: Float = 1f,
    zoomSpeed: Float = 0.01f,
): MapCameraController {
    val home = remember(cameraState) { cameraState.target }
    val previous = remember(cameraState) { arrayOfNulls<Manipulator>(1) }
    val state = remember(cameraState, mapWidth, mapHeight, minDistance, zoomSpeed) {
        val manipulator = Manipulator.Builder()
            .targetPosition(home.x, home.y, home.z)
            .upVector(0f, 1f, 0f)
            .zoomSpeed(zoomSpeed)
            .mapExtent(mapWidth, mapHeight)
            .mapMinDistance(minDistance)
            .build(Manipulator.Mode.MAP)
        // Carry the current pose across a tuning rebuild so the camera doesn't move.
        previous[0]?.let { manipulator.jumpToBookmark(it.getCurrentBookmark()) }
        previous[0] = manipulator
        MapCameraController(manipulator, cameraState).also { it.sync() }
    }
    DisposableEffect(state) { onDispose { state.manipulator.destroy() } }
    return state
}

/**
 * Attaches map gesture handling to this element.
 *
 * | Input               | Action |
 * |---------------------|--------|
 * | Single touch / drag | Pan    |
 * | Two-finger pinch    | Zoom   |
 * | Scroll wheel        | Zoom   |
 */
fun Modifier.mapGestures(controller: MapCameraController): Modifier =
    manipulatorDragGestures(controller.manipulator, alwaysStrafe = true) { controller.sync() }

// ── Flight camera ─────────────────────────────────────────────────────────────

/**
 * Drives a [CameraState] using a Filament [Manipulator] in FLIGHT mode (first-person).
 *
 * The per-frame simulation is driven automatically by [rememberFlightCameraController] (via [OnFrame]) —
 * you don't need a separate loop composable.
 */
class FlightCameraController internal constructor(
    internal val manipulator: Manipulator,
    private val cameraState: CameraState,
    // Owned here so [Modifier.flightGestures] can stay a plain (non-composable) modifier like
    // the orbit/map ones; the keyboard focus is requested once from rememberFlightCameraController.
    internal val focusRequester: FocusRequester,
    // Absolute wheel position, shared across tuning rebuilds so the chosen speed survives one.
    private val speedWheel: FloatArray,
    private val speedSteps: Int,
) : CameraController {
    override fun setViewport(width: Int, height: Int) = manipulator.setViewport(width, height)

    /** Step the movement speed along the `initialMoveSpeed`…`maxMoveSpeed` curve; one unit of
     *  [steps] is one of `speedSteps`. Driven by the scroll wheel in [Modifier.flightGestures]. */
    fun adjustSpeed(steps: Float) {
        val half = speedSteps / 2f
        speedWheel[0] = (speedWheel[0] + steps).coerceIn(-half, half)
        manipulator.scroll(0, 0, steps) // clamps the same way internally
    }

    /** Advance the camera simulation by [deltaTime] seconds. Flight-only; driven automatically
     *  per frame by [rememberFlightCameraController], so callers rarely call it directly. */
    fun update(deltaTime: Float) { manipulator.update(deltaTime); sync() }

    /** Snap back to the flight start position/orientation set at creation. */
    override fun resetToHome() { manipulator.jumpToBookmark(manipulator.getHomeBookmark()); sync() }

    override fun saveBookmark(): Manipulator.Bookmark = manipulator.getCurrentBookmark()

    override fun jumpToBookmark(bookmark: Manipulator.Bookmark) { manipulator.jumpToBookmark(bookmark); sync() }

    internal fun sync() = manipulator.syncTo(cameraState)
}

/**
 * Creates and remembers a [FlightCameraController] driving [cameraState] in FLIGHT mode.
 *
 * The manipulator's start position is taken from [cameraState.eye] at creation time.
 *
 * The tuning parameters are reactive — changing them at runtime rebuilds the manipulator
 * while keeping the current camera pose and the current scroll-wheel speed, so nothing jumps;
 * [startPitch]/[startYaw] only define the initial (and
 * [resetToHome][FlightCameraController.resetToHome]) orientation.
 *
 * Speed sits on an exponential curve spanning [speedSteps] scroll notches: the camera starts at
 * [initialMoveSpeed], full scroll-up reaches [maxMoveSpeed], full scroll-down `1 / maxMoveSpeed`.
 *
 * @param startPitch       Initial pitch in degrees (positive = look up).
 * @param startYaw         Initial yaw in degrees.
 * @param maxMoveSpeed     Movement speed in world units per second at full scroll-up.
 * @param initialMoveSpeed Movement speed before any scrolling; must be > 0.
 * @param speedSteps       Scroll notches spanning the whole speed range (fewer = coarser steps).
 * @param moveDamping      Movement damping (higher = snappier stop).
 * @param panSpeedX        Mouse-look horizontal sensitivity.
 * @param panSpeedY        Mouse-look vertical sensitivity.
 */
@Composable
fun rememberFlightCameraController(
    cameraState: CameraState,
    startPitch: Float = 0f,
    startYaw: Float = 0f,
    maxMoveSpeed: Float = 10f,
    initialMoveSpeed: Float = 1f,
    speedSteps: Int = 20,
    moveDamping: Float = 15f,
    panSpeedX: Float = 0.01f,
    panSpeedY: Float = 0.01f,
): FlightCameraController {
    val focusRequester = remember { FocusRequester() }
    val previous = remember(cameraState) { arrayOfNulls<Manipulator>(1) }
    // Absolute wheel position; seeded from initialMoveSpeed, then owned by adjustSpeed().
    val speedWheel = remember(cameraState) { FloatArray(1) { Float.NaN } }
    val state = remember(
        cameraState, startPitch, startYaw,
        maxMoveSpeed, initialMoveSpeed, speedSteps, moveDamping, panSpeedX, panSpeedY,
    ) {
        val eye = cameraState.eye
        val manipulator = Manipulator.Builder()
            .flightStartPosition(eye.x, eye.y, eye.z)
            .flightStartOrientation(radians(startPitch), radians(startYaw))
            .flightMaxMoveSpeed(maxMoveSpeed)
            .flightSpeedSteps(speedSteps)
            .flightMoveDamping(moveDamping)
            .flightPanSpeed(panSpeedX, panSpeedY)
            .build(Manipulator.Mode.FLIGHT)
        // The manipulator's speed is maxMoveSpeed^(wheel/half), i.e. always 1.0 at wheel 0 —
        // maxMoveSpeed alone has no effect until you scroll. Seed the wheel so the camera
        // actually starts at initialMoveSpeed, and re-apply it after a tuning rebuild.
        val half = speedSteps / 2f
        if (speedWheel[0].isNaN()) {
            val ratio = if (maxMoveSpeed > 0f && maxMoveSpeed != 1f) {
                ln(initialMoveSpeed.coerceAtLeast(1e-6f)) / ln(maxMoveSpeed)
            } else 0f
            speedWheel[0] = (half * ratio).coerceIn(-half, half)
        }
        speedWheel[0] = speedWheel[0].coerceIn(-half, half) // speedSteps may have changed
        manipulator.scroll(0, 0, speedWheel[0])
        // Carry the current pose across a tuning rebuild so the camera doesn't move (same as
        // orbit/map); without this, orientation would snap back to startPitch/startYaw.
        previous[0]?.let { manipulator.jumpToBookmark(it.getCurrentBookmark()) }
        previous[0] = manipulator
        FlightCameraController(manipulator, cameraState, focusRequester, speedWheel, speedSteps)
            .also { it.sync() }
    }
    DisposableEffect(state) { onDispose { state.manipulator.destroy() } }
    // Self-driving: advance the flight simulation every frame so callers never need a separate loop.
    OnFrame { state.update(it.deltaSeconds) }
    // Grab keyboard focus once so WASD works without a click. runCatching guards the case where
    // Modifier.flightGestures (which attaches this requester to a node) was never applied.
    LaunchedEffect(focusRequester) { runCatching { focusRequester.requestFocus() } }
    return state
}

/**
 * Attaches flight camera gesture and keyboard handling to this element.
 *
 * | Input              | Action           |
 * |--------------------|------------------|
 * | Drag / mouse move  | Look around      |
 * | W / Arrow Up       | Move forward     |
 * | S / Arrow Down     | Move backward    |
 * | A / Arrow Left     | Strafe left      |
 * | D / Arrow Right    | Strafe right     |
 * | E / Space          | Move up          |
 * | Q / Shift          | Move down        |
 * | Scroll up / down   | Faster / slower  |
 */
fun Modifier.flightGestures(controller: FlightCameraController): Modifier =
    this
        .onSizeChanged { controller.setViewport(it.width, it.height) }
        .focusRequester(controller.focusRequester)
        .focusable()
        .onKeyEvent { event ->
            val manipKey = when (event.key) {
                Key.W, Key.DirectionUp    -> Manipulator.Key.FORWARD
                Key.S, Key.DirectionDown  -> Manipulator.Key.BACKWARD
                Key.A, Key.DirectionLeft  -> Manipulator.Key.LEFT
                Key.D, Key.DirectionRight -> Manipulator.Key.RIGHT
                Key.E, Key.Spacebar       -> Manipulator.Key.UP
                Key.Q, Key.ShiftLeft, Key.ShiftRight -> Manipulator.Key.DOWN
                else -> return@onKeyEvent false
            }
            when (event.type) {
                KeyEventType.KeyDown -> controller.manipulator.keyDown(manipKey)
                KeyEventType.KeyUp   -> controller.manipulator.keyUp(manipKey)
            }
            true
        }
        .pointerInput(controller) {
            // Mouse look
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                controller.manipulator.grabBegin(down.position.x.toInt(), down.position.y.toInt(), false)
                while (true) {
                    val event = awaitPointerEvent()
                    if (event.changes.none { it.pressed }) break
                    val change = event.changes.firstOrNull { it.pressed } ?: break
                    if (change.positionChanged()) {
                        controller.manipulator.grabUpdate(change.position.x.toInt(), change.position.y.toInt())
                        change.consume()
                    }
                }
                controller.manipulator.grabEnd()
            }
        }
        .pointerInput(controller) {
            // Scroll to adjust movement speed. Scrolling up (negative delta) speeds up.
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    if (event.type == PointerEventType.Scroll) {
                        val change = event.changes.firstOrNull() ?: continue
                        controller.adjustSpeed(-change.scrollDelta.y)
                        event.changes.forEach { it.consume() }
                    }
                }
            }
        }

// ── Picking ───────────────────────────────────────────────────────────────────

/**
 * Issues a Filament picking query at every tap position and delivers the result to [onPick].
 *
 * [onPick] is called on the render thread. [View.PickingQueryResult.renderable] is 0
 * if no renderable exists at the tapped position.
 *
 * ```kotlin
 * val viewState = rememberFilamentViewState()
 * var selected by remember { mutableStateOf(NULL_ENTITY) }
 * FilamentView(
 *     scene = scene,
 *     viewState = viewState,
 *     modifier = Modifier.pickOnTap(viewState) { result -> selected = result.renderable },
 * )
 * ```
 */
fun Modifier.pickOnTap(
    viewState: FilamentViewState,
    onPick: (View.PickingQueryResult) -> Unit,
): Modifier = pointerInput(viewState, onPick) {
    detectTapGestures { offset ->
        viewState.pick(offset.x.toInt(), offset.y.toInt(), onPick)
    }
}
