package io.github.erkko68.filament.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.MouseButton
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.unit.dp
import io.github.erkko68.filament.compose.scene.CameraState
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.Exposure
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.compose.testutils.withFilamentScene
import io.github.erkko68.filament.utils.Float2
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Vertical direction of [Modifier.flightGestures] mouse-look. Filament's manipulator reads
 * bottom-left-origin viewport coordinates, so the gesture layer flips Compose's y; these pin the
 * resulting direction (cursor up looks up) and the negative-[panSpeedY] inverted-look opt-in.
 */
class FlightLookGestureTest : ComposeTestFixture() {

    @OptIn(ExperimentalTestApi::class)
    private fun lookDrag(panSpeedY: Float, to: Offset): CameraState {
        val cam = CameraState(
            initialEye = Position(0f, 0f, 0f),
            initialTarget = Position(0f, 0f, -1f),
            initialUp = Direction(0f, 1f, 0f),
            initialProjection = Projection.Perspective(),
            initialExposure = Exposure(),
            initialFocusDistance = 10f,
            initialShift = Float2(0f, 0f),
            initialScaling = Float2(1f, 1f),
        )
        withFilamentScene(engine, scene) { _ ->
            setContent {
                val flight = rememberFlightCameraController(cam, panSpeedY = panSpeedY, moveDamping = 0f)
                Box(Modifier.testTag("view").size(400.dp).flightGestures(flight))
            }
            waitForIdle()
            onNodeWithTag("view").performMouseInput {
                moveTo(Offset(200f, 200f))
                press(MouseButton.Primary)
                moveTo(to)
                release(MouseButton.Primary)
            }
            waitForIdle()
            // flightGestures only rotates the manipulator; the pose reaches CameraState through the
            // controller's per-frame update(), and the harness clock does not auto-advance.
            mainClock.advanceTimeByFrame()
        }
        return cam
    }

    /** Dragging up looks up — the standard, non-inverted first-person convention. */
    @Test
    fun draggingUpLooksUp() {
        val up = lookDrag(panSpeedY = 0.01f, to = Offset(200f, 100f))
        assertTrue(up.target.y > 0.1f, "cursor up should raise the look target, got ${up.target}")

        val down = lookDrag(panSpeedY = 0.01f, to = Offset(200f, 300f))
        assertTrue(down.target.y < -0.1f, "cursor down should lower the look target, got ${down.target}")
    }

    /** A negative panSpeedY gives the classic inverted look. */
    @Test
    fun negativePanSpeedInvertsTheLook() {
        val inverted = lookDrag(panSpeedY = -0.01f, to = Offset(200f, 100f))
        assertTrue(inverted.target.y < -0.1f,
            "negative panSpeedY should make cursor-up look down, got ${inverted.target}")
    }
}
