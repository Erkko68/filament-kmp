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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Coverage for the shared [CameraController] surface on [FlightCameraState] — flight now exposes
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
    private fun FlightCameraState.flyForward(steps: Int = 30) {
        manipulator.keyDown(Manipulator.Key.FORWARD)
        repeat(steps) { update(0.1f) }
        manipulator.keyUp(Manipulator.Key.FORWARD)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun flightResetToHomeAndBookmarkRestorePose() = run {
        val startEye = Position(0f, 0f, 5f)
        val cam = newCameraState(startEye)
        lateinit var state: FlightCameraState

        withFilamentScene(engine, scene) { setContent ->
            setContent { state = rememberFlightCameraState(cam) }
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
}
