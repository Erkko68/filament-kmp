package io.github.erkko68.filament.compose

import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.compose.scene.CameraState
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.Exposure
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.compose.testutils.withFilamentScene
import io.github.erkko68.filament.utils.Float2
import io.github.erkko68.filament.utils.Manipulator
import kotlin.math.sqrt
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Coverage for the shared [CameraController] surface on [FlightCameraController] — flight now exposes
 * `resetToHome`/`saveBookmark`/`jumpToBookmark` like the orbit/map controllers (Filament's
 * [Manipulator] supports bookmarks in FLIGHT mode). Verifies both restore the camera to a stored
 * pose after it has been flown away. The manipulator is CPU-only, so this runs on the NOOP fixture.
 */
class InteractionTest : ComposeTestFixture() {

    private fun newCameraState(eye: Position) = CameraState(
        initialEye = eye,
        initialTarget = Position(0f, 0f, -1f),
        initialUp = Direction(0f, 1f, 0f),
        initialProjection = Projection.Perspective(),
        initialExposure = Exposure(),
        initialFocusDistance = 10f,
        initialShift = Float2(0f, 0f),
        initialScaling = Float2(1f, 1f),
    )

    private fun CameraState.eyeDistanceTo(x: Float, y: Float, z: Float): Float {
        val dx = eye.x - x; val dy = eye.y - y; val dz = eye.z - z
        return sqrt(dx * dx + dy * dy + dz * dz)
    }

    // Fly forward for a while by holding the FORWARD key and integrating the manipulator.
    private fun FlightCameraController.flyForward(steps: Int = 30) {
        manipulator.keyDown(Manipulator.Key.FORWARD)
        repeat(steps) { update(0.1f) }
        manipulator.keyUp(Manipulator.Key.FORWARD)
    }

    // TODO: flaky on Linux (x86 + arm), fine on macOS/Windows. Root cause is upstream Filament:
    //  FreeFlightManipulator::mEyeVelocity is never initialized and is read-before-write in the
    //  damped update() path, so a garbage/NaN start value corrupts the pose (NaN > 0.1f is false).
    //  Fix = zero-init mEyeVelocity in FreeFlightManipulator.h; re-enable once the prebuilt is rebuilt.
    @Ignore
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun flightResetToHomeAndBookmarkRestorePose() = run {
        val startEye = Position(0f, 0f, 5f)
        val cam = newCameraState(startEye)
        lateinit var state: FlightCameraController

        withFilamentScene(engine, scene) { setContent ->
            setContent { state = rememberFlightCameraController(cam) }
            waitForIdle()
            mainClock.advanceTimeByFrame() // mount + first (no-key) OnFrame tick; pose stays at start

            // Home is the flight start pose; a fresh bookmark captures the same pose.
            assertTrue(cam.eyeDistanceTo(0f, 0f, 5f) < 1e-3f, "camera should start at the home pose")
            val home = state.saveBookmark()

            // Fly away, then restore the saved bookmark → back at the start pose.
            state.flyForward()
            assertTrue(cam.eyeDistanceTo(0f, 0f, 5f) > 0.1f, "holding FORWARD should move the camera")
            state.jumpToBookmark(home)
            assertEquals(0f, cam.eyeDistanceTo(0f, 0f, 5f), 1e-2f)

            // Fly away again, then resetToHome() → back at the start pose.
            state.flyForward()
            assertTrue(cam.eyeDistanceTo(0f, 0f, 5f) > 0.1f)
            state.resetToHome()
            assertEquals(0f, cam.eyeDistanceTo(0f, 0f, 5f), 1e-2f)
        }
    }

    // Undamped so the upstream mEyeVelocity read-before-write (see above) can't bite: with
    // moveDamping = 0 the velocity is assigned, not accumulated.
    @OptIn(ExperimentalTestApi::class)
    private fun distanceFlown(
        maxMoveSpeed: Float,
        initialMoveSpeed: Float,
        scrollSteps: Float = 0f,
    ): Float {
        val cam = newCameraState(Position(0f, 0f, 0f))
        lateinit var state: FlightCameraController
        var flown = 0f
        withFilamentScene(engine, scene) { setContent ->
            setContent {
                state = rememberFlightCameraController(
                    cam,
                    maxMoveSpeed = maxMoveSpeed,
                    initialMoveSpeed = initialMoveSpeed,
                    moveDamping = 0f,
                )
            }
            waitForIdle()
            mainClock.advanceTimeByFrame()
            if (scrollSteps != 0f) state.adjustSpeed(scrollSteps)
            state.manipulator.keyDown(Manipulator.Key.FORWARD)
            repeat(10) { state.update(0.1f) } // 1 second of flight
            state.manipulator.keyUp(Manipulator.Key.FORWARD)
            flown = cam.eyeDistanceTo(0f, 0f, 0f)
        }
        return flown
    }

    /** initialMoveSpeed is the speed the camera actually flies at — before this, the manipulator
     *  always started at 1 unit/s no matter what maxMoveSpeed said. */
    @Test
    fun flightInitialMoveSpeedSetsActualSpeed() {
        assertEquals(1f, distanceFlown(maxMoveSpeed = 10f, initialMoveSpeed = 1f), 0.05f)
        assertEquals(4f, distanceFlown(maxMoveSpeed = 10f, initialMoveSpeed = 4f), 0.2f)
        // maxMoveSpeed is only the ceiling of the scroll curve; it must not change the start speed.
        assertEquals(4f, distanceFlown(maxMoveSpeed = 100f, initialMoveSpeed = 4f), 0.2f)
    }

    /** Scrolling up speeds the camera up, and reaches maxMoveSpeed at the top of the curve. */
    @Test
    fun flightScrollAdjustsSpeedUpToMax() {
        val base = distanceFlown(maxMoveSpeed = 10f, initialMoveSpeed = 1f)
        val faster = distanceFlown(maxMoveSpeed = 10f, initialMoveSpeed = 1f, scrollSteps = 5f)
        assertTrue(faster > base * 1.5f, "scrolling up should noticeably speed the camera up")
        // speedSteps defaults to 20, so +10 notches from the 1 u/s midpoint tops out at maxMoveSpeed.
        assertEquals(10f, distanceFlown(10f, 1f, scrollSteps = 10f), 0.5f)
        assertEquals(10f, distanceFlown(10f, 1f, scrollSteps = 50f), 0.5f) // clamped
    }
}
