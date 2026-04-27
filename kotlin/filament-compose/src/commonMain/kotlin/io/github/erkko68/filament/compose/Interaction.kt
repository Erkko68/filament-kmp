package io.github.erkko68.filament.compose

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.utils.Manipulator

// ── Shared internals ──────────────────────────────────────────────────────────

private fun Manipulator.readLookAt(): Triple<Position, Position, Direction> {
    val e = FloatArray(3); val t = FloatArray(3); val u = FloatArray(3)
    getLookAt(e, t, u)
    return Triple(Position(e[0], e[1], e[2]), Position(t[0], t[1], t[2]), Direction(u[0], u[1], u[2]))
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
 * Reactive state for an orbit camera driven by a Filament [Manipulator].
 *
 * Read [eye], [target], and [up] inside a [scene.Camera] composable.
 * Call [setViewport] whenever the surface size changes via `Modifier.onSizeChanged`.
 * Use [resetToHome] for a "reset camera" button.
 *
 * Create with [rememberOrbitCameraState].
 */
class OrbitCameraState internal constructor(internal val manipulator: Manipulator) {
    var eye: Position by mutableStateOf(Position(0f))
        internal set
    var target: Position by mutableStateOf(Position(0f))
        internal set
    var up: Direction by mutableStateOf(Direction(0f, 1f, 0f))
        internal set

    /** Update whenever the render surface is resized. */
    fun setViewport(width: Int, height: Int) = manipulator.setViewport(width, height)

    /** Snap back to the initial eye/target position. */
    fun resetToHome() { manipulator.jumpToBookmark(manipulator.getHomeBookmark()); sync() }

    /** Save the current camera position as a bookmark to restore later. */
    fun saveBookmark(): Manipulator.Bookmark = manipulator.getCurrentBookmark()

    /** Restore a previously saved bookmark. */
    fun jumpToBookmark(bookmark: Manipulator.Bookmark) { manipulator.jumpToBookmark(bookmark); sync() }

    internal fun sync() {
        val (e, t, u) = manipulator.readLookAt()
        eye = e; target = t; up = u
    }
}

/**
 * Creates and remembers an [OrbitCameraState].
 *
 * Changing [eye] or [target] resets the orbit to those values — useful as a programmatic
 * "reset camera." For a reset button, prefer [OrbitCameraState.resetToHome] instead.
 *
 * ```kotlin
 * val orbit = rememberOrbitCameraState(
 *     eye    = Position(0f, 2f, 5f),
 *     target = Position(0f, 0f, 0f),
 * )
 * FilamentView(
 *     modifier = Modifier
 *         .onSizeChanged { orbit.setViewport(it.width, it.height) }
 *         .orbitGestures(orbit),
 * ) {
 *     Camera(eye = orbit.eye, target = orbit.target, up = orbit.up)
 * }
 * ```
 *
 * @param eye           Initial camera position in world space.
 * @param target        Point the camera looks at.
 * @param zoomSpeed     Scroll / pinch zoom sensitivity.
 * @param orbitSpeedX   Horizontal orbit drag sensitivity.
 * @param orbitSpeedY   Vertical orbit drag sensitivity.
 * @param enablePanning Allow right-click / secondary-button drag to pan.
 */
@Composable
fun rememberOrbitCameraState(
    eye: Position = Position(0f, 2f, 5f),
    target: Position = Position(0f, 0f, 0f),
    zoomSpeed: Float = 0.01f,
    orbitSpeedX: Float = 0.01f,
    orbitSpeedY: Float = 0.01f,
    enablePanning: Boolean = true,
): OrbitCameraState {
    val state = remember(eye, target, zoomSpeed, orbitSpeedX, orbitSpeedY, enablePanning) {
        val manipulator = Manipulator.Builder()
            .orbitHomePosition(eye.x, eye.y, eye.z)
            .targetPosition(target.x, target.y, target.z)
            .zoomSpeed(zoomSpeed)
            .orbitSpeed(orbitSpeedX, orbitSpeedY)
            .panning(enablePanning)
            .build(Manipulator.Mode.ORBIT)
        OrbitCameraState(manipulator).also { it.sync() }
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
 * Reactive state for a top-down map camera driven by a Filament [Manipulator].
 *
 * Pair with [scene.IsometricProjection] or [scene.Projection.Orthographic].
 * Create with [rememberMapCameraState].
 */
class MapCameraState internal constructor(internal val manipulator: Manipulator) {
    var eye: Position by mutableStateOf(Position(0f))
        internal set
    var target: Position by mutableStateOf(Position(0f))
        internal set
    var up: Direction by mutableStateOf(Direction(0f, 1f, 0f))
        internal set

    /** Update whenever the render surface is resized. */
    fun setViewport(width: Int, height: Int) = manipulator.setViewport(width, height)

    /** Snap back to the initial center position. */
    fun resetToHome() { manipulator.jumpToBookmark(manipulator.getHomeBookmark()); sync() }

    /** Save the current pan/zoom as a bookmark to restore later. */
    fun saveBookmark(): Manipulator.Bookmark = manipulator.getCurrentBookmark()

    /** Restore a previously saved bookmark. */
    fun jumpToBookmark(bookmark: Manipulator.Bookmark) { manipulator.jumpToBookmark(bookmark); sync() }

    internal fun sync() {
        val (e, t, u) = manipulator.readLookAt()
        eye = e; target = t; up = u
    }
}

/**
 * Creates and remembers a [MapCameraState] backed by a Filament [Manipulator] in MAP mode.
 *
 * Drag pans the view; scroll wheel and pinch zoom in/out. No rotation.
 *
 * ```kotlin
 * val map = rememberMapCameraState(center = Position(0f, 0f, 0f))
 * FilamentView(
 *     modifier = Modifier
 *         .onSizeChanged { map.setViewport(it.width, it.height) }
 *         .mapGestures(map),
 * ) {
 *     Camera(eye = map.eye, target = map.target, up = map.up,
 *            projection = IsometricProjection(scale = 10.0))
 * }
 * ```
 *
 * @param center      World-space point the map is initially centered on.
 * @param mapWidth    Pannable world extent along X.
 * @param mapHeight   Pannable world extent along Z.
 * @param minDistance Minimum zoom distance (closest the camera can get).
 * @param zoomSpeed   Scroll / pinch zoom sensitivity.
 */
@Composable
fun rememberMapCameraState(
    center: Position = Position(0f, 0f, 0f),
    mapWidth: Float = 512f,
    mapHeight: Float = 512f,
    minDistance: Float = 1f,
    zoomSpeed: Float = 0.01f,
): MapCameraState {
    val state = remember(center, mapWidth, mapHeight, minDistance, zoomSpeed) {
        val manipulator = Manipulator.Builder()
            .targetPosition(center.x, center.y, center.z)
            .upVector(0f, 1f, 0f)
            .zoomSpeed(zoomSpeed)
            .mapExtent(mapWidth, mapHeight)
            .mapMinDistance(minDistance)
            .build(Manipulator.Mode.MAP)
        MapCameraState(manipulator).also { it.sync() }
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
 *
 * No rotation — the camera always looks toward the target.
 */
fun Modifier.mapGestures(state: MapCameraState): Modifier =
    manipulatorDragGestures(state.manipulator, alwaysStrafe = true) { state.sync() }

// ── Flight camera ─────────────────────────────────────────────────────────────

/**
 * Reactive state for a first-person flight camera driven by a Filament [Manipulator].
 *
 * **Requires a per-frame update loop.** Place [FlightCameraLoop] inside `FilamentView`
 * (or run a `LaunchedEffect` that calls [update] each frame) — without it the camera
 * will not move smoothly.
 *
 * Create with [rememberFlightCameraState].
 */
class FlightCameraState internal constructor(internal val manipulator: Manipulator) {
    var eye: Position by mutableStateOf(Position(0f))
        internal set
    var target: Position by mutableStateOf(Position(0f))
        internal set
    var up: Direction by mutableStateOf(Direction(0f, 1f, 0f))
        internal set

    /** Update whenever the render surface is resized. */
    fun setViewport(width: Int, height: Int) = manipulator.setViewport(width, height)

    /**
     * Advance the camera simulation by [deltaTime] seconds.
     * Must be called every frame — use [FlightCameraLoop] or a `LaunchedEffect`.
     */
    fun update(deltaTime: Float) { manipulator.update(deltaTime); sync() }

    internal fun sync() {
        val (e, t, u) = manipulator.readLookAt()
        eye = e; target = t; up = u
    }
}

/**
 * Creates and remembers a [FlightCameraState] backed by a Filament [Manipulator] in FLIGHT mode.
 *
 * ```kotlin
 * val flight = rememberFlightCameraState(startPosition = Position(0f, 2f, 0f))
 * FilamentView(
 *     modifier = Modifier
 *         .onSizeChanged { flight.setViewport(it.width, it.height) }
 *         .flightGestures(flight),
 * ) {
 *     FlightCameraLoop(flight)           // per-frame update
 *     Camera(eye = flight.eye, target = flight.target, up = flight.up)
 * }
 * ```
 *
 * @param startPosition  Initial camera world position.
 * @param startPitch     Initial pitch in degrees (positive = look up).
 * @param startYaw       Initial yaw in degrees.
 * @param maxMoveSpeed   Maximum movement speed in world units per second.
 * @param moveDamping    Movement damping (higher = snappier stop).
 * @param panSpeed       Mouse-look sensitivity (x, y).
 */
@Composable
fun rememberFlightCameraState(
    startPosition: Position = Position(0f, 2f, 0f),
    startPitch: Float = 0f,
    startYaw: Float = 0f,
    maxMoveSpeed: Float = 10f,
    moveDamping: Float = 15f,
    panSpeedX: Float = 0.01f,
    panSpeedY: Float = 0.01f,
): FlightCameraState {
    val state = remember(startPosition, startPitch, startYaw, maxMoveSpeed, moveDamping, panSpeedX, panSpeedY) {
        val manipulator = Manipulator.Builder()
            .flightStartPosition(startPosition.x, startPosition.y, startPosition.z)
            .flightStartOrientation(startPitch, startYaw)
            .flightMaxMoveSpeed(maxMoveSpeed)
            .flightMoveDamping(moveDamping)
            .flightPanSpeed(panSpeedX, panSpeedY)
            .build(Manipulator.Mode.FLIGHT)
        FlightCameraState(manipulator).also { it.sync() }
    }
    DisposableEffect(state) { onDispose { state.manipulator.destroy() } }
    return state
}

/**
 * Drives the per-frame [FlightCameraState.update] loop.
 *
 * Place this inside `FilamentView` content alongside your `Camera` composable.
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
 *
 * The element is made focusable automatically and requests focus on composition.
 * Requires [FlightCameraLoop] to drive the update loop.
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
            view.pick(offset.x.toInt(), offset.y.toInt(), onPick)
        }
    }
}
