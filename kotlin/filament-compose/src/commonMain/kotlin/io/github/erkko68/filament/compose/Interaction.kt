package io.github.erkko68.filament.compose

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
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
import io.github.erkko68.filament.View
import io.github.erkko68.filament.compose.scene.CameraState
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.utils.Manipulator

// ── Shared internals ──────────────────────────────────────────────────────────

private fun Manipulator.syncTo(cameraState: CameraState) {
    val e = FloatArray(3); val t = FloatArray(3); val u = FloatArray(3)
    getLookAt(e, t, u)
    cameraState.eye    = Position(e[0], e[1], e[2])
    cameraState.target = Position(t[0], t[1], t[2])
    cameraState.up     = Direction(u[0], u[1], u[2])
}

// Shared drag + pinch-zoom logic. [strafe] controls whether drag pans or orbits.
private fun Modifier.manipulatorDragGestures(
    manipulator: Manipulator,
    alwaysStrafe: Boolean,
    onSync: () -> Unit,
): Modifier = this
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
 * Create with [rememberOrbitCameraState], pass the same [CameraState] to
 * [FilamentView], and attach [Modifier.orbitGestures] to the view's surface.
 * Call [setViewport] whenever the surface size changes via `Modifier.onSizeChanged`.
 */
class OrbitCameraState internal constructor(
    internal val manipulator: Manipulator,
    private val cameraState: CameraState,
) {
    /** Update whenever the render surface is resized. */
    fun setViewport(width: Int, height: Int) = manipulator.setViewport(width, height)

    /** Snap back to the initial eye/target position. */
    fun resetToHome() { manipulator.jumpToBookmark(manipulator.getHomeBookmark()); sync() }

    /** Save the current camera position as a bookmark to restore later. */
    fun saveBookmark(): Manipulator.Bookmark = manipulator.getCurrentBookmark()

    /** Restore a previously saved bookmark. */
    fun jumpToBookmark(bookmark: Manipulator.Bookmark) { manipulator.jumpToBookmark(bookmark); sync() }

    internal fun sync() = manipulator.syncTo(cameraState)
}

/**
 * Creates and remembers an [OrbitCameraState] that drives [cameraState].
 *
 * The manipulator's home position is taken from [cameraState]'s current `eye`/`target`
 * at creation time. The manipulator pushes its computed eye/target/up back into
 * [cameraState] whenever the user interacts.
 *
 * ```kotlin
 * val cameraState = rememberCameraState(eye = Position(0f, 2f, 5f))
 * val orbit = rememberOrbitCameraState(cameraState)
 * FilamentView(
 *     cameraState = cameraState,
 *     modifier = Modifier
 *         .onSizeChanged { orbit.setViewport(it.width, it.height) }
 *         .orbitGestures(orbit),
 * ) { ... }
 * ```
 *
 * @param zoomSpeed     Scroll / pinch zoom sensitivity.
 * @param orbitSpeedX   Horizontal orbit drag sensitivity.
 * @param orbitSpeedY   Vertical orbit drag sensitivity.
 * @param enablePanning Allow right-click / secondary-button drag to pan.
 */
@Composable
fun rememberOrbitCameraState(
    cameraState: CameraState,
    zoomSpeed: Float = 0.01f,
    orbitSpeedX: Float = 0.01f,
    orbitSpeedY: Float = 0.01f,
    enablePanning: Boolean = true,
): OrbitCameraState {
    val state = remember(cameraState, zoomSpeed, orbitSpeedX, orbitSpeedY, enablePanning) {
        val eye = cameraState.eye
        val target = cameraState.target
        val manipulator = Manipulator.Builder()
            .orbitHomePosition(eye.x, eye.y, eye.z)
            .targetPosition(target.x, target.y, target.z)
            .zoomSpeed(zoomSpeed)
            .orbitSpeed(orbitSpeedX, orbitSpeedY)
            .panning(enablePanning)
            .build(Manipulator.Mode.ORBIT)
        OrbitCameraState(manipulator, cameraState).also { it.sync() }
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
fun Modifier.orbitGestures(state: OrbitCameraState): Modifier =
    manipulatorDragGestures(state.manipulator, alwaysStrafe = false) { state.sync() }

// ── Map camera ────────────────────────────────────────────────────────────────

/**
 * Drives a [CameraState] using a Filament [Manipulator] in MAP mode (top-down 2-D pan/zoom).
 * Pair with `Projection.Orthographic` on the [CameraState].
 */
class MapCameraState internal constructor(
    internal val manipulator: Manipulator,
    private val cameraState: CameraState,
) {
    fun setViewport(width: Int, height: Int) = manipulator.setViewport(width, height)
    fun resetToHome() { manipulator.jumpToBookmark(manipulator.getHomeBookmark()); sync() }
    fun saveBookmark(): Manipulator.Bookmark = manipulator.getCurrentBookmark()
    fun jumpToBookmark(bookmark: Manipulator.Bookmark) { manipulator.jumpToBookmark(bookmark); sync() }

    internal fun sync() = manipulator.syncTo(cameraState)
}

/**
 * Creates and remembers a [MapCameraState] driving [cameraState] in MAP mode.
 *
 * Drag pans the view; scroll wheel and pinch zoom in/out. No rotation.
 *
 * @param mapWidth    Pannable world extent along X.
 * @param mapHeight   Pannable world extent along Z.
 * @param minDistance Minimum zoom distance (closest the camera can get).
 * @param zoomSpeed   Scroll / pinch zoom sensitivity.
 */
@Composable
fun rememberMapCameraState(
    cameraState: CameraState,
    mapWidth: Float = 512f,
    mapHeight: Float = 512f,
    minDistance: Float = 1f,
    zoomSpeed: Float = 0.01f,
): MapCameraState {
    val state = remember(cameraState, mapWidth, mapHeight, minDistance, zoomSpeed) {
        val target = cameraState.target
        val manipulator = Manipulator.Builder()
            .targetPosition(target.x, target.y, target.z)
            .upVector(0f, 1f, 0f)
            .zoomSpeed(zoomSpeed)
            .mapExtent(mapWidth, mapHeight)
            .mapMinDistance(minDistance)
            .build(Manipulator.Mode.MAP)
        MapCameraState(manipulator, cameraState).also { it.sync() }
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
fun Modifier.mapGestures(state: MapCameraState): Modifier =
    manipulatorDragGestures(state.manipulator, alwaysStrafe = true) { state.sync() }

// ── Flight camera ─────────────────────────────────────────────────────────────

/**
 * Drives a [CameraState] using a Filament [Manipulator] in FLIGHT mode (first-person).
 *
 * **Requires a per-frame update loop.** Place [FlightCameraLoop] inside `FilamentView`
 * (or run a `LaunchedEffect` that calls [update] each frame).
 */
class FlightCameraState internal constructor(
    internal val manipulator: Manipulator,
    private val cameraState: CameraState,
) {
    fun setViewport(width: Int, height: Int) = manipulator.setViewport(width, height)

    /**
     * Advance the camera simulation by [deltaTime] seconds.
     * Must be called every frame — use [FlightCameraLoop] or a `LaunchedEffect`.
     */
    fun update(deltaTime: Float) { manipulator.update(deltaTime); sync() }

    internal fun sync() = manipulator.syncTo(cameraState)
}

/**
 * Creates and remembers a [FlightCameraState] driving [cameraState] in FLIGHT mode.
 *
 * The manipulator's start position is taken from [cameraState.eye] at creation time.
 *
 * @param startPitch     Initial pitch in degrees (positive = look up).
 * @param startYaw       Initial yaw in degrees.
 * @param maxMoveSpeed   Maximum movement speed in world units per second.
 * @param moveDamping    Movement damping (higher = snappier stop).
 * @param panSpeedX      Mouse-look horizontal sensitivity.
 * @param panSpeedY      Mouse-look vertical sensitivity.
 */
@Composable
fun rememberFlightCameraState(
    cameraState: CameraState,
    startPitch: Float = 0f,
    startYaw: Float = 0f,
    maxMoveSpeed: Float = 10f,
    moveDamping: Float = 15f,
    panSpeedX: Float = 0.01f,
    panSpeedY: Float = 0.01f,
): FlightCameraState {
    val state = remember(cameraState, startPitch, startYaw, maxMoveSpeed, moveDamping, panSpeedX, panSpeedY) {
        val eye = cameraState.eye
        val manipulator = Manipulator.Builder()
            .flightStartPosition(eye.x, eye.y, eye.z)
            .flightStartOrientation(startPitch, startYaw)
            .flightMaxMoveSpeed(maxMoveSpeed)
            .flightMoveDamping(moveDamping)
            .flightPanSpeed(panSpeedX, panSpeedY)
            .build(Manipulator.Mode.FLIGHT)
        FlightCameraState(manipulator, cameraState).also { it.sync() }
    }
    DisposableEffect(state) { onDispose { state.manipulator.destroy() } }
    return state
}

/**
 * Drives the per-frame [FlightCameraState.update] loop.
 *
 * Place this inside `FilamentView` content alongside your other scene composables.
 */
@Composable
fun FlightCameraLoop(state: FlightCameraState) {
    LaunchedEffect(state) {
        var lastNanos = -1L
        while (true) {
            withFrameNanos { nanos ->
                if (lastNanos >= 0L) {
                    state.update((nanos - lastNanos) / 1_000_000_000f)
                }
                lastNanos = nanos
            }
        }
    }
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
 * | Scroll wheel       | Change speed     |
 */
@Composable
fun Modifier.flightGestures(state: FlightCameraState): Modifier {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(focusRequester) { focusRequester.requestFocus() }

    return this
        .focusRequester(focusRequester)
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
                KeyEventType.KeyDown -> state.manipulator.keyDown(manipKey)
                KeyEventType.KeyUp   -> state.manipulator.keyUp(manipKey)
            }
            true
        }
        .pointerInput(state) {
            // Mouse look
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                state.manipulator.grabBegin(down.position.x.toInt(), down.position.y.toInt(), false)
                while (true) {
                    val event = awaitPointerEvent()
                    if (event.changes.none { it.pressed }) break
                    val change = event.changes.firstOrNull { it.pressed } ?: break
                    if (change.positionChanged()) {
                        state.manipulator.grabUpdate(change.position.x.toInt(), change.position.y.toInt())
                        change.consume()
                    }
                }
                state.manipulator.grabEnd()
            }
        }
        .pointerInput(state) {
            // Scroll to adjust movement speed
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    if (event.type == PointerEventType.Scroll) {
                        val change = event.changes.firstOrNull() ?: continue
                        state.manipulator.scroll(
                            change.position.x.toInt(),
                            change.position.y.toInt(),
                            change.scrollDelta.y,
                        )
                        event.changes.forEach { it.consume() }
                    }
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
 * var selected by remember { mutableStateOf(NULL_ENTITY) }
 * FilamentView(
 *     modifier = Modifier.pickOnTap { result -> selected = result.renderable }
 * ) { ... }
 * ```
 */
@Composable
fun Modifier.pickOnTap(onPick: (View.PickingQueryResult) -> Unit): Modifier {
    val view = LocalFilamentView.current
    return pointerInput(view, onPick) {
        detectTapGestures { offset ->
            // Filament uses OpenGL viewport conventions: origin at the bottom-left.
            // Compose offsets are top-left, so flip Y against the current viewport height.
            val y = view.viewport.height - offset.y.toInt()
            view.pick(offset.x.toInt(), y, onPick)
        }
    }
}
