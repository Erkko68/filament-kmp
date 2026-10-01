package io.github.erkko68.filament.compose

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.compose.scene.LensScaling
import io.github.erkko68.filament.compose.scene.LensShift
import io.github.erkko68.filament.compose.scene.CameraState
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.Exposure
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.compose.testutils.SetSceneContent
import io.github.erkko68.filament.compose.testutils.withFilamentScene
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
        initialShift = LensShift.None,
        initialScaling = LensScaling.Identity,
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

    /**
     * Mounts a fresh flight controller at the origin and flies FORWARD for one simulated second,
     * returning the distance covered (= the effective move speed in units/s).
     *
     * Undamped so the upstream mEyeVelocity read-before-write (see above) can't bite: with
     * moveDamping = 0 the velocity is assigned, not accumulated. Takes the harness's `setContent`
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
        controller.manipulator.keyDown(Manipulator.Key.FORWARD)
        repeat(10) { controller.update(0.1f) } // 1 second of flight
        controller.manipulator.keyUp(Manipulator.Key.FORWARD)
        return cam.eyeDistanceTo(0f, 0f, 0f)
    }

    /** initialMoveSpeed is the speed the camera actually flies at — before this, the manipulator
     *  always started at 1 unit/s no matter what maxMoveSpeed said. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun flightInitialMoveSpeedSetsActualSpeed() = run {
        withFilamentScene(engine, scene) { setContent ->
            assertEquals(1f, flyForOneSecond(setContent, maxMoveSpeed = 10f, initialMoveSpeed = 1f), 0.05f)
            assertEquals(4f, flyForOneSecond(setContent, maxMoveSpeed = 10f, initialMoveSpeed = 4f), 0.2f)
            // maxMoveSpeed is only the ceiling of the scroll curve; it must not move the start speed.
            assertEquals(4f, flyForOneSecond(setContent, maxMoveSpeed = 100f, initialMoveSpeed = 4f), 0.2f)
        }
    }

    /** Scrolling up speeds the camera up, and reaches maxMoveSpeed at the top of the curve. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun flightScrollAdjustsSpeedUpToMax() = run {
        withFilamentScene(engine, scene) { setContent ->
            val base = flyForOneSecond(setContent, maxMoveSpeed = 10f, initialMoveSpeed = 1f)
            val faster = flyForOneSecond(setContent, maxMoveSpeed = 10f, initialMoveSpeed = 1f, scrollSteps = 5f)
            assertTrue(faster > base * 1.5f, "scrolling up should noticeably speed the camera up")
            // speedSteps defaults to 20, so +10 notches from the 1 u/s midpoint tops out at maxMoveSpeed.
            assertEquals(10f, flyForOneSecond(setContent, 10f, 1f, scrollSteps = 10f), 0.5f)
            assertEquals(10f, flyForOneSecond(setContent, 10f, 1f, scrollSteps = 50f), 0.5f) // clamped
        }
    }

    // Zoom out by scrolling, then check that a saved bookmark and resetToHome() both restore the start pose.
    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.assertBookmarksRestorePose(cam: CameraState, controller: CameraController, zoom: () -> Unit) {
        controller.setViewport(100, 100)
        val start = cam.eye
        controller.saveBookmark().use { home ->
            zoom()
            assertTrue(cam.eyeDistanceTo(start.x, start.y, start.z) > 0.1f, "zooming should move the camera")
            controller.jumpToBookmark(home)
            assertEquals(0f, cam.eyeDistanceTo(start.x, start.y, start.z), 1e-2f)
        }
        zoom()
        controller.resetToHome()
        assertEquals(0f, cam.eyeDistanceTo(start.x, start.y, start.z), 1e-2f)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun orbitAndMapBookmarksRestorePose() = run {
        withFilamentScene(engine, scene) { setContent ->
            val orbitCam = newCameraState(Position(0f, 0f, 5f))
            lateinit var orbit: OrbitCameraController
            setContent { orbit = rememberOrbitCameraController(orbitCam) }
            waitForIdle()
            assertBookmarksRestorePose(orbitCam, orbit) { orbit.manipulator.scroll(50, 50, 20f); orbit.sync() }

            val mapCam = newCameraState(Position(0f, 10f, 0f))
            lateinit var map: MapCameraController
            setContent { map = rememberMapCameraController(mapCam) }
            waitForIdle()
            assertTrue(mapCam.eye.y > mapCam.target.y + 1f, "map looks down on its target: ${mapCam.eye}")
            assertBookmarksRestorePose(mapCam, map) { map.manipulator.scroll(50, 50, 20f); map.sync() }
        }
    }
}
