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
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Mouse-button coverage for [Modifier.orbitGestures]. Right-drag pans, left-drag orbits — the
 * distinction matters because foundation's `awaitFirstDown` ignores non-primary mouse buttons on
 * skiko (desktop/web), which silently killed panning until the gesture used its own down-await.
 */
class OrbitPanGestureTest : ComposeTestFixture() {

    private fun newCameraState() = CameraState(
        initialEye = Position(0f, 0f, 10f),
        initialTarget = Position(0f, 0f, 0f),
        initialUp = Direction(0f, 1f, 0f),
        initialProjection = Projection.Perspective(),
        initialExposure = Exposure(),
        initialFocusDistance = 10f,
        initialShift = Float2(0f, 0f),
        initialScaling = Float2(1f, 1f),
    )

    @OptIn(ExperimentalTestApi::class)
    private fun drag(
        button: MouseButton,
        panningEnabled: Boolean = true,
        to: Offset = Offset(300f, 260f),
        orbitSpeedY: Float = 0.01f,
    ): CameraState {
        val cam = newCameraState()
        withFilamentScene(engine, scene) { _ ->
            setContent {
                val orbit = rememberOrbitCameraController(
                    cam,
                    orbitSpeedY = orbitSpeedY,
                    panningEnabled = panningEnabled,
                )
                Box(Modifier.testTag("view").size(400.dp).orbitGestures(orbit))
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

    /** Right-drag translates the camera: eye and target move together, distance to target is kept. */
    @Test
    fun secondaryDragPans() {
        val cam = drag(MouseButton.Secondary)
        assertTrue(cam.eye.x != 0f || cam.eye.y != 0f, "right-drag should move the camera, got ${cam.eye}")
        assertTrue(abs(cam.eye.x - cam.target.x) < 1e-3f && abs(cam.eye.y - cam.target.y) < 1e-3f,
            "panning moves eye and target together, got eye=${cam.eye} target=${cam.target}")
    }

    /**
     * The scene follows the cursor in both axes. The camera starts at +Z looking at the origin
     * with +Y up, so screen-right is +X: dragging right moves the camera to -X (content shifts
     * right with the cursor) and dragging up moves it to -Y. Filament's manipulator takes
     * bottom-left-origin viewport coordinates, so without flipping Compose's y only the vertical
     * axis came out backwards.
     */
    @Test
    fun panFollowsTheCursorInBothAxes() {
        val right = drag(MouseButton.Secondary, to = Offset(300f, 200f))
        assertTrue(right.eye.x < -0.1f, "dragging right should move the camera to -X, got ${right.eye}")

        val up = drag(MouseButton.Secondary, to = Offset(200f, 100f))
        assertTrue(up.eye.y < -0.1f, "dragging up should move the camera to -Y, got ${up.eye}")

        val down = drag(MouseButton.Secondary, to = Offset(200f, 300f))
        assertTrue(down.eye.y > 0.1f, "dragging down should move the camera to +Y, got ${down.eye}")
    }

    /** Orbit is flipped the same way: dragging up swings the eye below the target. */
    @Test
    fun orbitFollowsTheCursorVertically() {
        val up = drag(MouseButton.Primary, to = Offset(200f, 100f))
        assertTrue(up.eye.y < -0.1f, "dragging up should swing the eye below the target, got ${up.eye}")
    }

    /** A negative orbitSpeedY inverts the vertical axis for players who want inverted look. */
    @Test
    fun negativeOrbitSpeedInvertsTheVerticalAxis() {
        val normal = drag(MouseButton.Primary, to = Offset(200f, 100f))
        val inverted = drag(MouseButton.Primary, to = Offset(200f, 100f), orbitSpeedY = -0.01f)
        assertTrue(normal.eye.y < -0.1f && inverted.eye.y > 0.1f,
            "negative orbitSpeedY should flip the vertical orbit, got ${normal.eye} vs ${inverted.eye}")
    }

    /** Left-drag orbits: the target stays put while the eye swings around it. */
    @Test
    fun primaryDragOrbits() {
        val cam = drag(MouseButton.Primary)
        assertTrue(cam.eye.z != 10f, "left-drag should orbit the camera, got ${cam.eye}")
    }

    /** panningEnabled = false makes right-drag orbit instead of pan. */
    @Test
    fun secondaryDragOrbitsWhenPanningDisabled() {
        val noPan = drag(MouseButton.Secondary, panningEnabled = false)
        val orbited = drag(MouseButton.Primary)
        assertTrue(abs(noPan.eye.x - orbited.eye.x) < 1e-3f && abs(noPan.eye.z - orbited.eye.z) < 1e-3f,
            "with panning disabled right-drag should orbit like left-drag, got ${noPan.eye} vs ${orbited.eye}")
    }
}
