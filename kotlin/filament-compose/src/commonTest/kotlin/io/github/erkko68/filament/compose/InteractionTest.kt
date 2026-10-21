package io.github.erkko68.filament.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.compose.scene.LensScaling
import io.github.erkko68.filament.compose.scene.LensShift
import io.github.erkko68.filament.compose.scene.CameraState
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.Exposure
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.Viewpoint
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.compose.testutils.SetSceneContent
import io.github.erkko68.filament.compose.testutils.withFilamentScene
import io.github.erkko68.filament.utils.Manipulator
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The camera controllers on the NOOP fixture (flight's manipulator is CPU-only): flight speed, and every controller
 * carrying on from a pose the app wrote. Expected poses follow camutils' OrbitManipulator/MapManipulator::scroll,
 * which orbit and map reproduce, and FreeFlightManipulator::update.
 */
class InteractionTest : ComposeTestFixture() {

    private fun newCameraState(eye: Position, target: Position = Position(0f, 0f, -1f)) = CameraState(
        initialEye = eye,
        initialTarget = target,
        initialUp = Direction(0f, 1f, 0f),
        initialProjection = Projection.Perspective(),
        initialExposure = Exposure(),
        initialFocusDistance = 10f,
        initialShift = LensShift.None,
        initialScaling = LensScaling.Identity,
    )

    private fun assertNear(expected: Position, actual: Position, what: String) {
        assertEquals(0f, (expected - actual).length, 1e-3f, "$what: expected $expected, got $actual")
    }

    // Hold FORWARD for one simulated second.
    private fun FlightCameraController.flyForOneSecond() {
        takeOver().keyDown(Manipulator.Key.FORWARD)
        repeat(10) { update(0.1f) }
        manipulator.keyUp(Manipulator.Key.FORWARD)
    }

    /**
     * Mounts a fresh flight controller at the origin and flies FORWARD for one simulated second,
     * returning the distance covered (= the effective move speed in units/s).
     *
     * Undamped so the upstream mEyeVelocity read-before-write can't bite: FreeFlightManipulator never initialises it,
     * and with moveDamping = 0 the velocity is assigned, not accumulated. Takes the harness's `setContent`
     * instead of opening its own scene: `withFilamentScene` returns a `TestResult` that only
     * completes asynchronously on web, so a caller-side harness would read the camera before the
     * body ever ran and report a distance of 0 on js/wasm.
     */
    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.flyForOneSecond(
        setContent: SetSceneContent,
        maxMoveSpeed: Float,
        initialMoveSpeed: Float,
        scrollSteps: Float = 0f,
    ): Float {
        val cam = newCameraState(Position(0f, 0f, 0f))
        lateinit var controller: FlightCameraController
        setContent {
            controller = rememberFlightCameraController(
                cam,
                maxMoveSpeed = maxMoveSpeed,
                initialMoveSpeed = initialMoveSpeed,
                moveDamping = 0f,
            )
        }
        waitForIdle()
        mainClock.advanceTimeByFrame() // mount + first (no-key) OnFrame tick; pose stays at start
        if (scrollSteps != 0f) controller.adjustSpeed(scrollSteps)
        controller.flyForOneSecond()
        return (cam.eye - Position(0f)).length
    }

    /** initialMoveSpeed is the speed the camera actually flies at — before this, the manipulator
     *  always started at 1 unit/s no matter what maxMoveSpeed said. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun flightInitialMoveSpeedSetsActualSpeed() = withFilamentScene(engine, scene) { setContent ->
        assertEquals(1f, flyForOneSecond(setContent, maxMoveSpeed = 10f, initialMoveSpeed = 1f), 0.05f)
        assertEquals(4f, flyForOneSecond(setContent, maxMoveSpeed = 10f, initialMoveSpeed = 4f), 0.2f)
        // maxMoveSpeed is only the ceiling of the scroll curve; it must not move the start speed.
        assertEquals(4f, flyForOneSecond(setContent, maxMoveSpeed = 100f, initialMoveSpeed = 4f), 0.2f)
    }

    /** Scrolling up speeds the camera up, and reaches maxMoveSpeed at the top of the curve. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun flightScrollAdjustsSpeedUpToMax() = withFilamentScene(engine, scene) { setContent ->
        val base = flyForOneSecond(setContent, maxMoveSpeed = 10f, initialMoveSpeed = 1f)
        val faster = flyForOneSecond(setContent, maxMoveSpeed = 10f, initialMoveSpeed = 1f, scrollSteps = 5f)
        assertTrue(faster > base * 1.5f, "scrolling up should noticeably speed the camera up")
        // speedSteps defaults to 20, so +10 notches from the 1 u/s midpoint tops out at maxMoveSpeed.
        assertEquals(10f, flyForOneSecond(setContent, 10f, 1f, scrollSteps = 10f), 0.5f)
        assertEquals(10f, flyForOneSecond(setContent, 10f, 1f, scrollSteps = 50f), 0.5f) // clamped
    }

    /** Flight looks towards the state's target from the start, and flies on from a viewpoint the app sets. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun flightFollowsTheState() = withFilamentScene(engine, scene) { setContent ->
        val cam = newCameraState(Position(0f), target = Position(1f, 1f, -1f))
        lateinit var flight: FlightCameraController
        setContent { flight = rememberFlightCameraController(cam, moveDamping = 0f) }
        waitForIdle()
        flight.update(0.1f)
        assertNear(Position(1f, 1f, -1f).let { Position(it.x / sqrt(3f), it.y / sqrt(3f), it.z / sqrt(3f)) }, cam.target, "looks at the target")

        cam.viewpoint = Viewpoint(Position(3f, 0f, 0f), Position(3f, 0f, -5f))
        flight.flyForOneSecond()
        assertNear(Position(3f, 0f, -1f), cam.eye, "one unit along -Z from the new eye")
    }

    /** The orbit target is its pivot: a viewpoint the app sets is orbited and zoomed around, not snapped away from. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun orbitFollowsTheState() = withFilamentScene(engine, scene) { setContent ->
        val cam = newCameraState(Position(0f, 0f, 5f), target = Position(0f))
        lateinit var orbit: OrbitCameraController
        setContent { orbit = rememberOrbitCameraController(cam) }
        waitForIdle()
        cam.viewpoint = Viewpoint(Position(5f, 0f, 0f), Position(1f, 0f, 0f))
        // As OrbitManipulator::scroll, the eye moves zoomSpeed × -delta along the gaze: 0.01 × 100 = 1 unit closer.
        orbit.zoom(-100f, Offset(50f, 50f), IntSize(100, 100))
        assertNear(Position(4f, 0f, 0f), cam.eye, "zoomed from the app's eye")
        assertEquals(Position(1f, 0f, 0f), cam.target, "the target stays the pivot")
    }

    /** Zooming in stops at the near plane short of the pivot instead of flying through and turning round. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun orbitZoomStopsBeforeThePivot() = withFilamentScene(engine, scene) { setContent ->
        val cam = newCameraState(Position(0f, 0f, 5f), target = Position(0f))
        lateinit var orbit: OrbitCameraController
        setContent { orbit = rememberOrbitCameraController(cam) }
        waitForIdle()
        orbit.zoom(-100_000f, Offset(50f, 50f), IntSize(100, 100))
        // Projection.Perspective's near plane is 0.1.
        assertNear(Position(0f, 0f, 0.1f), cam.eye, "at the near plane")
        assertEquals(Position(0f), cam.target)
    }

    /** Map zooms from the state's height (camutils' started at its 512-unit default extent) and from a viewpoint the app sets. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun mapFollowsTheState() = withFilamentScene(engine, scene) { setContent ->
        val cam = newCameraState(Position(0f, 10f, 0f), target = Position(0f))
        lateinit var map: MapCameraController
        setContent { map = rememberMapCameraController(cam) }
        waitForIdle()
        // As MapManipulator::scroll, the eye moves zoomSpeed × -delta of the way to the point under the pointer.
        map.zoom(-10f, Offset(50f, 50f), IntSize(100, 100))
        assertNear(Position(0f, 9f, 0f), cam.eye, "zoomed from the starting height")
        assertNear(Position(0f), cam.target, "the target is the ground point below")

        cam.viewpoint = Viewpoint(Position(4f, 20f, 2f), Position(4f, 0f, 2f), Direction(0f, 0f, -1f))
        map.zoom(-10f, Offset(50f, 50f), IntSize(100, 100))
        assertNear(Position(4f, 18f, 2f), cam.eye, "zoomed from the app's viewpoint")
        assertNear(Position(4f, 0f, 2f), cam.target, "the target is the ground point below")
    }

    /** An orthographic map zooms by scaling its extents (moving closer shows the same), towards the pointer. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun orthographicMapZoomScalesItsExtents() = withFilamentScene(engine, scene) { setContent ->
        val cam = newCameraState(Position(0f, 10f, 0f), target = Position(0f))
        cam.up = Direction(0f, 0f, -1f)
        cam.projection = Projection.Orthographic(-5.0, 5.0, -5.0, 5.0, 0.0, 100.0)
        lateinit var map: MapCameraController
        setContent { map = rememberMapCameraController(cam) }
        waitForIdle()
        // A tenth of the way to the pointer, 50 px right of centre on a view 10 units across: x = 5, so 0.5. The point
        // under the pointer stays put: 0.5 plus half the new 9-unit width is 5.
        map.zoom(-10f, Offset(100f, 50f), IntSize(100, 100))
        val ortho = cam.projection as Projection.Orthographic
        for ((expected, actual) in listOf(-4.5 to ortho.left, 4.5 to ortho.right, -4.5 to ortho.bottom, 4.5 to ortho.top)) {
            assertEquals(expected, actual, 1e-6, "extents scaled by 0.9: $ortho")
        }
        assertNear(Position(0.5f, 0f, 0f), cam.target, "slid towards the pointer")
        assertNear(Position(0.5f, 10f, 0f), cam.eye, "at the same height")
    }

    /** A pan keeps the point at the target's depth under the pointer, at the camera's own field of view. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun panFollowsThePointerAtTheFieldOfView() = withFilamentScene(engine, scene) { setContent ->
        val cam = newCameraState(Position(0f, 0f, 10f), target = Position(0f))
        cam.projection = Projection.Perspective(fovDegrees = 90.0)
        lateinit var orbit: OrbitCameraController
        setContent { orbit = rememberOrbitCameraController(cam) }
        waitForIdle()
        // 10 away at 90°, a 100 px tall view spans 2 × 10 × tan 45° = 20 units: 0.2 a pixel, so 10 px right is 2 units.
        orbit.drag(secondary = true, Offset(10f, 0f), IntSize(100, 100))
        assertNear(Position(-2f, 0f, 10f), cam.eye, "the camera slides left as the scene follows the pointer right")
        assertNear(Position(-2f, 0f, 0f), cam.target, "with its target")
    }
}
