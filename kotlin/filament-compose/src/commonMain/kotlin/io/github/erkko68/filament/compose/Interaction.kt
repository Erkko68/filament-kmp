package io.github.erkko68.filament.compose

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import io.github.erkko68.filament.View
import io.github.erkko68.filament.compose.scene.CameraState
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.Viewpoint
import io.github.erkko68.filament.compose.scene.tanHalfVertical
import io.github.erkko68.filament.utils.Manipulator
import io.github.erkko68.filament.utils.Mode
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

// ── Shared internals ──────────────────────────────────────────────────────────

/**
 * What every camera controller can do, so generic UI (a "reset view" button) can drive whichever is active.
 * The camera itself lives in the [CameraState]: read or set [CameraState.viewpoint], or fly with
 * [CameraState.animateTo], and the controller carries on from there.
 *
 * All three are created by their `remember*CameraController` factory.
 */
sealed interface CameraController {
    /** Snap the camera back to the pose it had when this controller was first created. */
    fun resetToHome()
}

// Like foundation's awaitFirstDown, minus its "mouse down means primary button only" rule
// (firstDownRefersToPrimaryMouseButtonOnly() is true on skiko: desktop, web, iOS) — that rule
// swallows right-click presses entirely, so secondary-button panning never started.
private suspend fun AwaitPointerEventScope.awaitFirstDownAnyButton(): PointerInputChange {
    var event: PointerEvent
    do {
        event = awaitPointerEvent()
    } while (!event.changes.all { it.changedToDownIgnoreConsumed() })
    return event.changes[0]
}

private fun cross(a: Direction, b: Direction) = Direction(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x)

private fun Projection.near(): Float = when (this) {
    is Projection.Perspective -> near
    is Projection.Lens -> near
    is Projection.Orthographic -> near
}.toFloat()

// The point [offset] px from the view's centre at the target's depth, for a view of [size] px.
private fun CameraState.pointAt(offset: Offset, size: IntSize): Position {
    if (size.width <= 0 || size.height <= 0) return target
    val gaze = (target - eye).normalized()
    val right = cross(gaze, up).normalized()
    val upward = cross(right, gaze)
    val (perPixelX, perPixelY) = when (val p = projection) {
        is Projection.Orthographic -> ((p.right - p.left) / size.width).toFloat() to ((p.top - p.bottom) / size.height).toFloat()
        else -> {
            val tan = p.tanHalfVertical(size.width.toDouble() / size.height) ?: 0.0
            (2f * (target - eye).length * tan.toFloat() / size.height).let { it to it }
        }
    }
    return target + right * (offset.x * perPixelX) - upward * (offset.y * perPixelY)
}

// Slides eye and target across the view so the point at the target's depth follows the pointer.
private fun CameraState.pan(delta: Offset, size: IntSize) {
    val shift = target - pointAt(delta, size)
    eye += shift
    target += shift
}

// An orthographic view looks the same from any distance, so it zooms by scaling its extents.
private fun CameraState.scaleExtents(factor: Float) {
    val p = projection as Projection.Orthographic
    val f = factor.coerceAtLeast(1e-3f).toDouble()
    projection = p.copy(left = p.left * f, right = p.right * f, bottom = p.bottom * f, top = p.top * f)
}

private fun IntSize.centre() = Offset(width / 2f, height / 2f)

// Drag, pinch and wheel input for the orbit and map controllers, in Compose pixels: [drag] gets whether the
// secondary button is down and the pointer's move, [zoom] a scroll delta and where the pointer is. Each gesture
// stops a running camera move first.
private fun Modifier.cameraGestures(
    key: Any,
    cameraState: CameraState,
    drag: (secondary: Boolean, delta: Offset, size: IntSize) -> Unit,
    zoom: (delta: Float, at: Offset, size: IntSize) -> Unit,
): Modifier = this
    .pointerInput(key) {
        awaitEachGesture {
            val down = awaitFirstDownAnyButton()
            val secondary = currentEvent.buttons.isSecondaryPressed
            // The grab starts past the touch slop, from the press: a finger's jitter stays a tap (onTap) and the camera stays still.
            var grabbing = false
            var last = down.position
            var pinchMode = false
            var prevPinchDistance = 0f

            while (true) {
                val event = awaitPointerEvent()
                if (event.changes.none { it.pressed }) break
                val pressed = event.changes.filter { it.pressed }

                when {
                    pressed.size >= 2 -> {
                        val dist = (pressed[0].position - pressed[1].position).getDistance()
                        if (!pinchMode) {
                            pinchMode = true
                        } else {
                            cameraState.stopMove()
                            zoom((prevPinchDistance - dist) * 0.005f, (pressed[0].position + pressed[1].position) / 2f, size)
                            event.changes.forEach { it.consume() }
                        }
                        prevPinchDistance = dist
                    }
                    pressed.size == 1 && !pinchMode -> {
                        val change = pressed.first()
                        if (!grabbing && (change.position - down.position).getDistance() > viewConfiguration.touchSlop) {
                            grabbing = true
                            cameraState.stopMove()
                        }
                        if (grabbing && change.positionChanged()) {
                            drag(secondary, change.position - last, size)
                            last = change.position
                            change.consume()
                        }
                    }
                }
            }
        }
    }
    .pointerInput(key) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                if (event.type == PointerEventType.Scroll) {
                    val change = event.changes.firstOrNull() ?: continue
                    cameraState.stopMove()
                    zoom(change.scrollDelta.y * 0.1f, change.position, size)
                    event.changes.forEach { it.consume() }
                }
            }
        }
    }

// ── Orbit camera ──────────────────────────────────────────────────────────────

// camutils' orbit stops this short of straight up or down (OrbitManipulator.h MAX_PHI).
private const val MAX_PHI = (PI / 2 - 0.001).toFloat()

/**
 * Orbits a [CameraState] around its `target`, the way Filament's camutils orbit manipulator does, working on the
 * state itself. Create with [rememberOrbitCameraController], pass the same [CameraState] to [FilamentView], and
 * attach [Modifier.orbitGestures] to the view's surface. Up stays the state's `up`; the orbit turns around +Y.
 */
class OrbitCameraController internal constructor(
    internal val cameraState: CameraState,
    private val home: Viewpoint,
    private val zoomSpeed: Float,
    private val orbitSpeedX: Float,
    private val orbitSpeedY: Float,
    private val panningEnabled: Boolean,
) : CameraController {
    override fun resetToHome() { cameraState.viewpoint = home }

    // As camutils: the heading turns by -dx × orbitSpeedX and the height by dy × orbitSpeedY, in pixels.
    internal fun drag(secondary: Boolean, delta: Offset, size: IntSize) {
        if (secondary && panningEnabled) return cameraState.pan(delta, size)
        val offset = cameraState.eye - cameraState.target
        val distance = offset.length
        if (distance == 0f) return
        val theta = atan2(offset.x, offset.z) - delta.x * orbitSpeedX
        val phi = (asin((offset.y / distance).coerceIn(-1f, 1f)) + delta.y * orbitSpeedY).coerceIn(-MAX_PHI, MAX_PHI)
        cameraState.eye = cameraState.target + Direction(sin(theta) * cos(phi), sin(phi), cos(theta) * cos(phi)) * distance
    }

    // As camutils, zoomSpeed × delta along the gaze, but stopping at the near plane short of the target.
    internal fun zoom(delta: Float, at: Offset, size: IntSize) {
        val offset = cameraState.eye - cameraState.target
        val distance = offset.length
        if (distance == 0f) return
        val wanted = distance + zoomSpeed * delta
        if (cameraState.projection is Projection.Orthographic) return cameraState.scaleExtents(wanted / distance)
        val closest = min(max(cameraState.projection.near(), 1e-3f), distance)
        cameraState.eye = cameraState.target + offset * (wanted.coerceAtLeast(closest) / distance)
    }
}

/**
 * Creates and remembers an [OrbitCameraController] that drives [cameraState], orbiting around its `target`. Home
 * ([CameraController.resetToHome]) is the state's viewpoint when this is first called.
 *
 * The tuning parameters ([zoomSpeed], [orbitSpeedX], [orbitSpeedY], [panningEnabled]) are reactive and take effect
 * on the next gesture.
 *
 * ```kotlin
 * val cameraState = rememberCameraState(initialEye = Position(0f, 2f, 5f))
 * val orbit = rememberOrbitCameraController(cameraState)
 * FilamentView(scene = scene, cameraState = cameraState, modifier = Modifier.orbitGestures(orbit))
 * ```
 *
 * @param cameraState    Camera this controller drives.
 * @param zoomSpeed      Scroll / pinch zoom sensitivity, in world units per scroll unit.
 * @param orbitSpeedX    Horizontal orbit drag sensitivity, in radians per pixel; negative inverts that axis.
 * @param orbitSpeedY    Vertical orbit drag sensitivity, in radians per pixel; negative inverts that axis.
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
    val home = remember(cameraState) { cameraState.viewpoint }
    return remember(cameraState, zoomSpeed, orbitSpeedX, orbitSpeedY, panningEnabled) {
        OrbitCameraController(cameraState, home, zoomSpeed, orbitSpeedX, orbitSpeedY, panningEnabled)
    }
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
    cameraGestures(controller, controller.cameraState, controller::drag, controller::zoom)

// ── Map camera ────────────────────────────────────────────────────────────────

/**
 * Pans and zooms a [CameraState] over the scene, the way Filament's camutils map manipulator does, working on the
 * state itself: drag slides the view, scroll and pinch zoom towards the pointer, no rotation. Set the state up
 * looking down on the ground (an `up` of -Z puts north at the top); with `Projection.Orthographic`, zoom scales its
 * extents.
 */
class MapCameraController internal constructor(
    internal val cameraState: CameraState,
    private val home: Viewpoint,
    private val zoomSpeed: Float,
) : CameraController {
    override fun resetToHome() { cameraState.viewpoint = home }

    internal fun drag(@Suppress("UNUSED_PARAMETER") secondary: Boolean, delta: Offset, size: IntSize) = cameraState.pan(delta, size)

    // As camutils, zoomSpeed × -delta of the way towards the point under the pointer, stopping at the near plane.
    internal fun zoom(delta: Float, at: Offset, size: IntSize) {
        val point = cameraState.pointAt(at - size.centre(), size)
        val toward = zoomSpeed * -delta
        if (cameraState.projection is Projection.Orthographic) {
            val shift = (point - cameraState.target) * toward
            cameraState.eye += shift
            cameraState.target += shift
            return cameraState.scaleExtents(1f - toward)
        }
        val distance = (point - cameraState.eye).length
        if (distance == 0f) return
        val f = min(toward, max(1f - cameraState.projection.near() / distance, 0f))
        cameraState.eye += (point - cameraState.eye) * f
        cameraState.target += (point - cameraState.target) * f
    }
}

/**
 * Creates and remembers a [MapCameraController] driving [cameraState]. Home ([CameraController.resetToHome]) is
 * the state's viewpoint when this is first called; [zoomSpeed] is reactive.
 *
 * ```kotlin
 * val cameraState = rememberCameraState(
 *     initialEye = Position(0f, 40f, 0f), initialTarget = Position(0f), initialUp = Direction(0f, 0f, -1f),
 * )
 * val map = rememberMapCameraController(cameraState)
 * ```
 *
 * @param cameraState Camera this controller drives.
 * @param zoomSpeed   Scroll / pinch zoom sensitivity: the fraction of the way to the pointer per scroll unit.
 */
@Composable
fun rememberMapCameraController(
    cameraState: CameraState,
    zoomSpeed: Float = 0.01f,
): MapCameraController {
    val home = remember(cameraState) { cameraState.viewpoint }
    return remember(cameraState, zoomSpeed) { MapCameraController(cameraState, home, zoomSpeed) }
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
    cameraGestures(controller, controller.cameraState, controller::drag, controller::zoom)

// ── Flight camera ─────────────────────────────────────────────────────────────

// Filament's manipulator reads viewport coordinates bottom-left-origin (GL convention, same as
// its own sample apps' `y = height - y`); Compose is top-left-origin. Without this flip vertical
// mouse-look runs the wrong way.
private fun AwaitPointerEventScope.viewportY(y: Float): Int = size.height - y.toInt()

// A flight manipulator at [view], looking from its eye towards its target.
private fun Manipulator.Builder.flightStart(view: Viewpoint): Manipulator.Builder {
    val look = (view.target - view.eye).normalized()
    // camutils looks along eulerZYX(0, yaw, pitch) · -Z = (-sin yaw cos pitch, sin pitch, -cos yaw cos pitch).
    return flightStartPosition(view.eye.x, view.eye.y, view.eye.z)
        .flightStartOrientation(asin(look.y.coerceIn(-1f, 1f)), if (look.length > 0f) atan2(-look.x, -look.z) else 0f)
}

/**
 * Drives a [CameraState] using a Filament [Manipulator] in FLIGHT mode (first-person), for its damped movement and
 * speed curve. A viewpoint written to the state (by the app or [CameraState.animateTo]) is where it flies on from.
 *
 * The per-frame simulation is driven automatically by [rememberFlightCameraController] (via [OnFrame]) —
 * you don't need a separate loop composable.
 */
// @Stable: `speedWheel` is a read-only config array; the rest is stable or snapshot-backed.
@Stable
class FlightCameraController internal constructor(
    internal val manipulator: Manipulator,
    private val cameraState: CameraState,
    private val home: Viewpoint,
    // Owned here so [Modifier.flightGestures] can stay a plain (non-composable) modifier like
    // the orbit/map ones; the keyboard focus is requested once from rememberFlightCameraController.
    internal val focusRequester: FocusRequester,
    // Absolute wheel position, shared across tuning rebuilds so the chosen speed survives one.
    private val speedWheel: FloatArray,
    private val speedSteps: Int,
) : CameraController {
    // The pose this last wrote; anything else in the state came from the app or a camera move.
    private var written = cameraState.viewpoint

    override fun resetToHome() { cameraState.viewpoint = home }

    /** Step the movement speed along the `initialMoveSpeed`…`maxMoveSpeed` curve; one unit of
     *  [steps] is one of `speedSteps`. Driven by the scroll wheel in [Modifier.flightGestures]. */
    fun adjustSpeed(steps: Float) {
        val half = speedSteps / 2f
        speedWheel[0] = (speedWheel[0] + steps).coerceIn(-half, half)
        manipulator.scroll(0, 0, steps) // clamps the same way internally
    }

    /** Advance the camera simulation by [deltaTime] seconds. Driven automatically per frame by
     *  [rememberFlightCameraController], so callers rarely call it directly. */
    fun update(deltaTime: Float) {
        // A running camera move owns the state; the manipulator catches up once, when it ends.
        if (cameraState.isMoving) return
        catchUp()
        manipulator.update(deltaTime)
        val e = FloatArray(3); val t = FloatArray(3); val u = FloatArray(3)
        manipulator.getLookAt(e, t, u)
        cameraState.place(Viewpoint(Position(e[0], e[1], e[2]), Position(t[0], t[1], t[2]), Direction.Up))
        written = cameraState.viewpoint
    }

    /** Input is starting: stops a running camera move and carries on from where it left the camera. */
    internal fun takeOver(): Manipulator {
        cameraState.stopMove()
        catchUp()
        return manipulator
    }

    // A jump to a throwaway manipulator's pose rather than a rebuild: camutils leaves a new one's velocity
    // uninitialised (FreeFlightManipulator::mEyeVelocity), and only this one has been flying.
    private fun catchUp() {
        val view = cameraState.viewpoint
        if (view == written) return
        Manipulator.Builder().flightStart(view).build(Mode.FREE_FLIGHT).use { it.currentBookmark.use(manipulator::jumpToBookmark) }
        written = view
    }
}

/**
 * Creates and remembers a [FlightCameraController] driving [cameraState] in FLIGHT mode, starting at its `eye` and
 * looking towards its `target`. Home ([CameraController.resetToHome]) is that first viewpoint.
 *
 * The tuning parameters are reactive — changing them at runtime keeps the camera's pose and the current
 * scroll-wheel speed, so nothing jumps.
 *
 * Speed sits on an exponential curve spanning [speedSteps] scroll notches: the camera starts at
 * [initialMoveSpeed], full scroll-up reaches [maxMoveSpeed], full scroll-down `1 / maxMoveSpeed`.
 *
 * @param cameraState      Camera this controller drives.
 * @param maxMoveSpeed     Movement speed in world units per second at full scroll-up.
 * @param initialMoveSpeed Movement speed before any scrolling; must be > 0.
 * @param speedSteps       Scroll notches spanning the whole speed range (fewer = coarser steps).
 * @param moveDamping      Movement damping (higher = snappier stop).
 * @param panSpeedX        Mouse-look horizontal sensitivity; negative inverts that axis.
 * @param panSpeedY        Mouse-look vertical sensitivity; negative inverts that axis
 *                         (the classic "inverted look" — cursor up looks down).
 */
@Composable
fun rememberFlightCameraController(
    cameraState: CameraState,
    maxMoveSpeed: Float = 10f,
    initialMoveSpeed: Float = 1f,
    speedSteps: Int = 20,
    moveDamping: Float = 15f,
    panSpeedX: Float = 0.01f,
    panSpeedY: Float = 0.01f,
): FlightCameraController {
    val focusRequester = remember { FocusRequester() }
    val home = remember(cameraState) { cameraState.viewpoint }
    // Absolute wheel position; seeded from initialMoveSpeed, then owned by adjustSpeed().
    val speedWheel = remember(cameraState) { FloatArray(1) { Float.NaN } }
    val controller = remember(cameraState, maxMoveSpeed, initialMoveSpeed, speedSteps, moveDamping, panSpeedX, panSpeedY) {
        val manipulator = Manipulator.Builder()
            .flightStart(cameraState.viewpoint)
            .flightMaxMoveSpeed(maxMoveSpeed)
            .flightSpeedSteps(speedSteps)
            .flightMoveDamping(moveDamping)
            .flightPanSpeed(panSpeedX, panSpeedY)
            .build(Mode.FREE_FLIGHT)
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
        FlightCameraController(manipulator, cameraState, home, focusRequester, speedWheel, speedSteps)
    }
    DisposableEffect(controller) { onDispose { controller.manipulator.destroy() } }
    // Self-driving: advance the flight simulation every frame so callers never need a separate loop.
    OnFrame { controller.update(it.deltaSeconds) }
    // Grab keyboard focus once so WASD works without a click. runCatching guards the case where
    // Modifier.flightGestures (which attaches this requester to a node) was never applied.
    LaunchedEffect(focusRequester) { runCatching { focusRequester.requestFocus() } }
    return controller
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
        .onSizeChanged { controller.manipulator.setViewport(it.width, it.height) }
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
                KeyEventType.KeyDown -> controller.takeOver().keyDown(manipKey)
                KeyEventType.KeyUp   -> controller.manipulator.keyUp(manipKey)
            }
            true
        }
        .pointerInput(controller) {
            // Mouse look
            awaitEachGesture {
                val down = awaitFirstDownAnyButton()
                // Past the touch slop only, as in cameraGestures: a tap stays a tap.
                var grabbing = false
                while (true) {
                    val event = awaitPointerEvent()
                    if (event.changes.none { it.pressed }) break
                    val change = event.changes.firstOrNull { it.pressed } ?: break
                    if (!grabbing && (change.position - down.position).getDistance() > viewConfiguration.touchSlop) {
                        grabbing = true
                        controller.takeOver().grabBegin(down.position.x.toInt(), viewportY(down.position.y), false)
                    }
                    if (grabbing && change.positionChanged()) {
                        controller.manipulator.grabUpdate(change.position.x.toInt(), viewportY(change.position.y))
                        change.consume()
                    }
                }
                if (grabbing) controller.manipulator.grabEnd()
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
