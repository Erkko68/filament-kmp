package io.github.erkko68.filament.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import io.github.erkko68.filament.compose.scene.CameraState
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.Exposure
import io.github.erkko68.filament.compose.scene.LensScaling
import io.github.erkko68.filament.compose.scene.LensShift
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.compose.testutils.withFilamentScene
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Keyboard, wheel and pinch input of the camera controllers, and their bookmarks. */
@OptIn(ExperimentalTestApi::class)
class CameraInputTest : ComposeTestFixture() {
    private fun cameraAt(eye: Position, target: Position) = CameraState(
        initialEye = eye, initialTarget = target, initialUp = Direction(0f, 1f, 0f),
        initialProjection = Projection.Perspective(), initialExposure = Exposure(), initialFocusDistance = 10f,
        initialShift = LensShift.None, initialScaling = LensScaling.Identity,
    )

    // The orbit pivot is the origin in these tests; zooming dollies the eye (and its look-at point) along the view axis.
    private fun CameraState.distance() = sqrt(eye.x * eye.x + eye.y * eye.y + eye.z * eye.z)

    private fun ComposeUiTest.frames(count: Int) = repeat(count) { mainClock.advanceTimeByFrame() }

    /** Each movement key flies along its own axis while held, and the camera stops when it's released. */
    @Test
    fun flightKeysMoveAlongTheirAxes() {
        val cam = cameraAt(Position(0f, 0f, 0f), Position(0f, 0f, -1f))
        withFilamentScene(engine, scene) { _ ->
            setContent {
                val flight = rememberFlightCameraController(cam, moveDamping = 0f)
                Box(Modifier.testTag("view").size(400.dp).flightGestures(flight))
            }
            waitForIdle()
            // Looking down -Z: forward is -Z, right is +X.
            val moves = listOf(
                Key.W to Position(0f, 0f, -1f), Key.DirectionUp to Position(0f, 0f, -1f),
                Key.S to Position(0f, 0f, 1f), Key.DirectionDown to Position(0f, 0f, 1f),
                Key.A to Position(-1f, 0f, 0f), Key.DirectionLeft to Position(-1f, 0f, 0f),
                Key.D to Position(1f, 0f, 0f), Key.DirectionRight to Position(1f, 0f, 0f),
                Key.E to Position(0f, 1f, 0f), Key.Spacebar to Position(0f, 1f, 0f),
                Key.Q to Position(0f, -1f, 0f), Key.ShiftLeft to Position(0f, -1f, 0f), Key.ShiftRight to Position(0f, -1f, 0f),
            )
            for ((key, axis) in moves) {
                val from = cam.eye
                onNodeWithTag("view").performKeyInput { keyDown(key) }
                frames(10)
                val moved = Position(cam.eye.x - from.x, cam.eye.y - from.y, cam.eye.z - from.z)
                onNodeWithTag("view").performKeyInput { keyUp(key) }
                frames(2)
                val along = moved.x * axis.x + moved.y * axis.y + moved.z * axis.z
                val across = abs(moved.x) + abs(moved.y) + abs(moved.z) - abs(along)
                assertTrue(along > 0.01f && across < 1e-3f, "$key should move along $axis, moved $moved")

                val rest = cam.eye
                frames(5)
                assertEquals(rest, cam.eye, "releasing $key should stop the camera")
            }

            // A key that isn't bound does nothing.
            val before = cam.eye
            onNodeWithTag("view").performKeyInput { keyDown(Key.X) }
            frames(5)
            onNodeWithTag("view").performKeyInput { keyUp(Key.X) }
            assertEquals(before, cam.eye)
        }
    }

    /** Scrolling up flies faster and down slower. */
    @Test
    fun flightWheelChangesTheSpeed() {
        val cam = cameraAt(Position(0f, 0f, 0f), Position(0f, 0f, -1f))
        withFilamentScene(engine, scene) { _ ->
            setContent {
                val flight = rememberFlightCameraController(cam, moveDamping = 0f)
                Box(Modifier.testTag("view").size(400.dp).flightGestures(flight))
            }
            waitForIdle()
            fun flown(): Float {
                val from = cam.eye.z
                onNodeWithTag("view").performKeyInput { keyDown(Key.W) }
                frames(10)
                onNodeWithTag("view").performKeyInput { keyUp(Key.W) }
                frames(2)
                return from - cam.eye.z
            }
            val normal = flown()
            onNodeWithTag("view").performMouseInput { moveTo(Offset(200f, 200f)); scroll(-3f) }
            waitForIdle()
            val fast = flown()
            onNodeWithTag("view").performMouseInput { scroll(6f) }
            waitForIdle()
            val slow = flown()
            assertTrue(slow < normal && normal < fast, "expected slow < normal < fast, got $slow, $normal, $fast")
        }
    }

    /** A bookmark brings the pose back, and home is where the controller started. */
    @Test
    fun bookmarksRestoreThePose() {
        val cam = cameraAt(Position(1f, 2f, 3f), Position(1f, 2f, 2f))
        withFilamentScene(engine, scene) { _ ->
            var flight: FlightCameraController? = null
            setContent {
                flight = rememberFlightCameraController(cam, moveDamping = 0f).also {
                    Box(Modifier.testTag("view").size(400.dp).flightGestures(it))
                }
            }
            waitForIdle()
            val controller = flight!!
            fun fly(key: Key) {
                onNodeWithTag("view").performKeyInput { keyDown(key) }
                frames(10)
                onNodeWithTag("view").performKeyInput { keyUp(key) }
                frames(2)
            }
            fun assertAt(expected: Position, what: String) {
                for ((e, a) in listOf(expected.x to cam.eye.x, expected.y to cam.eye.y, expected.z to cam.eye.z)) assertEquals(e, a, 1e-3f, what)
            }
            val home = cam.eye
            fly(Key.W)
            val marked = cam.eye
            controller.saveBookmark().use { bookmark ->
                fly(Key.D)
                assertTrue(abs(cam.eye.x - marked.x) > 0.01f)
                runOnUiThread { controller.jumpToBookmark(bookmark) }
                assertAt(marked, "jumpToBookmark")
            }
            runOnUiThread { controller.resetToHome() }
            assertAt(home, "resetToHome")
        }
    }

    /** The wheel zooms the orbit camera in and out along its view direction. */
    @Test
    fun orbitWheelZooms() {
        val cam = cameraAt(Position(0f, 0f, 10f), Position(0f, 0f, 0f))
        withFilamentScene(engine, scene) { _ ->
            setContent {
                Box(Modifier.testTag("view").size(400.dp).orbitGestures(rememberOrbitCameraController(cam, zoomSpeed = 0.1f)))
            }
            waitForIdle()
            val start = cam.distance()
            onNodeWithTag("view").performMouseInput { moveTo(Offset(200f, 200f)); scroll(-5f) }
            waitForIdle()
            val afterUp = cam.distance()
            onNodeWithTag("view").performMouseInput { scroll(10f) }
            waitForIdle()
            val afterDown = cam.distance()
            assertTrue(afterUp != start && afterDown != afterUp, "the wheel should zoom: $start, $afterUp, $afterDown")
            assertTrue((afterUp - start) * (afterDown - afterUp) < 0f, "opposite scrolls should zoom opposite ways")
            assertEquals(0f, abs(cam.eye.x) + abs(cam.eye.y), 1e-3f, "zooming stays on the view axis")
        }
    }

    /** Two fingers zoom by their spread, and lifting one doesn't turn the rest of the gesture into an orbit. */
    @Test
    fun pinchZoomsWithoutOrbiting() {
        val cam = cameraAt(Position(0f, 0f, 10f), Position(0f, 0f, 0f))
        withFilamentScene(engine, scene) { _ ->
            setContent {
                Box(Modifier.testTag("view").size(400.dp).orbitGestures(rememberOrbitCameraController(cam, zoomSpeed = 0.1f)))
            }
            waitForIdle()
            val start = cam.distance()
            onNodeWithTag("view").performTouchInput {
                down(0, Offset(180f, 200f))
                down(1, Offset(220f, 200f))
                moveTo(1, Offset(260f, 200f))
                moveTo(1, Offset(320f, 200f))
            }
            waitForIdle()
            val spread = cam.distance()
            assertTrue(spread < start, "spreading the fingers should zoom in: $start -> $spread")
            assertEquals(0f, abs(cam.eye.x) + abs(cam.eye.y), 1e-3f, "a pinch doesn't orbit")

            onNodeWithTag("view").performTouchInput {
                moveTo(1, Offset(230f, 200f))
            }
            waitForIdle()
            assertTrue(cam.distance() > spread, "closing the fingers should zoom back out")

            val pinched = cam.eye
            onNodeWithTag("view").performTouchInput {
                up(1)
                moveTo(0, Offset(100f, 120f))
                up(0)
            }
            waitForIdle()
            assertEquals(pinched, cam.eye, "the finger left over from a pinch must not orbit the camera")
        }
    }
}
