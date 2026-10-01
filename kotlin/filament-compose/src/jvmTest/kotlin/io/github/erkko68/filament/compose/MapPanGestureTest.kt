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
import io.github.erkko68.filament.compose.scene.LensScaling
import io.github.erkko68.filament.compose.scene.LensShift
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.compose.testutils.withFilamentScene
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * [Modifier.mapGestures]: the map looks straight down on the XZ plane with -Z up, and a drag of
 * either button pans it so the ground follows the cursor.
 */
class MapPanGestureTest : ComposeTestFixture() {

    @OptIn(ExperimentalTestApi::class)
    private fun drag(button: MouseButton, to: Offset): CameraState {
        val cam = CameraState(
            initialEye = Position(0f, 10f, 0f),
            initialTarget = Position(0f, 0f, 0f),
            initialUp = Direction(0f, 0f, -1f),
            initialProjection = Projection.Perspective(),
            initialExposure = Exposure(),
            initialFocusDistance = 10f,
            initialShift = LensShift.None,
            initialScaling = LensScaling.Identity,
        )
        withFilamentScene(engine, scene) { _ ->
            setContent {
                Box(Modifier.testTag("view").size(400.dp).mapGestures(rememberMapCameraController(cam)))
            }
            waitForIdle()
            onNodeWithTag("view").performMouseInput {
                moveTo(Offset(200f, 200f))
                press(button)
                moveTo(Offset((200f + to.x) / 2f, (200f + to.y) / 2f))
                moveTo(to)
                release(button)
            }
            waitForIdle()
        }
        return cam
    }

    @Test
    fun dragPansWithTheGroundUnderTheCursor() {
        val right = drag(MouseButton.Primary, to = Offset(300f, 200f))
        assertTrue(right.eye.x < -0.1f, "dragging right should move the camera to -X, got ${right.eye}")
        assertTrue(abs(right.eye.x - right.target.x) < 1e-3f && right.eye.y > right.target.y,
            "panning keeps looking straight down, got eye=${right.eye} target=${right.target}")

        val up = drag(MouseButton.Primary, to = Offset(200f, 100f))
        assertTrue(up.eye.z > 0.1f, "dragging up (towards -Z) should move the camera to +Z, got ${up.eye}")
    }

    @Test
    fun secondaryDragPansToo() {
        val cam = drag(MouseButton.Secondary, to = Offset(300f, 200f))
        assertTrue(cam.eye.x < -0.1f, "right-drag should pan the map as well, got ${cam.eye}")
    }
}
