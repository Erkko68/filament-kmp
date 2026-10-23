package io.github.erkko68.filament.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import io.github.erkko68.filament.compose.scene.CameraState
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.LensScaling
import io.github.erkko68.filament.compose.scene.LensShift
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.anchorTo
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.testutils.IgnoreWithoutViewInterop
import io.github.erkko68.filament.compose.testutils.withEngine
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** [CameraState.worldToScreen] and [anchorTo] against the projection maths, on NOOP. */
@IgnoreWithoutViewInterop
class WorldToScreenTest {

    private val ortho = Projection.Orthographic(left = -1.0, right = 1.0, bottom = -1.0, top = 1.0, near = 0.1, far = 100.0)

    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.mount(engine: io.github.erkko68.filament.Engine, setContent: (@androidx.compose.runtime.Composable () -> Unit) -> Unit,
                                    projection: Projection, label: Position? = null): CameraState {
        var cam: CameraState? = null
        setContent {
            val state = rememberCameraState(initialEye = Position(0f, 0f, 10f), initialProjection = projection).also { cam = it }
            Box(Modifier.size(200.dp, 100.dp)) {
                FilamentSceneView(Modifier.fillMaxSize(), engine = engine, cameraState = state) { DirectionalLight() }
                if (label != null) Box(Modifier.anchorTo(state, label).testTag("label").size(10.dp))
            }
        }
        repeat(3) { mainClock.advanceTimeByFrame() }
        return assertNotNull(cam)
    }

    /** Lets a state change reach the camera: recompose, apply, lay out. */
    @OptIn(ExperimentalTestApi::class)
    private fun ComposeUiTest.settle() {
        repeat(2) { mainClock.advanceTimeByFrame() }
        waitForIdle()
    }

    private fun assertNear(expected: Offset, actual: Offset?, what: String) {
        assertNotNull(actual, "$what should be on screen")
        assertTrue(abs(expected.x - actual.x) < 0.5f && abs(expected.y - actual.y) < 0.5f, "$what: expected $expected, got $actual")
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun orthographicMapsTheBoundsOntoTheView() = withEngine { engine, setContent ->
        val cam = mount(engine, setContent, ortho)
        val (w, h) = cam.layoutSize.let { it.width.toFloat() to it.height.toFloat() }
        assertNear(Offset(0.75f * w, 0.25f * h), cam.worldToScreen(Position(0.5f, 0.5f, 0f)), "(0.5, 0.5)")
        assertNear(Offset(0f, h), cam.worldToScreen(Position(-1f, -1f, 0f)), "the bottom-left bound")
    }

    /** Filament adds the lens scaling and shift at render time, outside the projection matrix it hands out. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun lensScalingAndShiftApply() = withEngine { engine, setContent ->
        val cam = mount(engine, setContent, ortho)
        val (w, h) = cam.layoutSize.let { it.width.toFloat() to it.height.toFloat() }

        cam.scaling = LensScaling(2f)
        settle()
        assertNear(Offset(w, 0.5f * h), cam.worldToScreen(Position(0.5f, 0f, 0f)), "scaled by 2")

        // Filament's shift is a fraction of the viewport (Camera.h: pixels / viewport size): 1 is a full width.
        cam.scaling = LensScaling.Identity
        cam.shift = LensShift(0.25f, 0f)
        settle()
        val shifted = assertNotNull(cam.worldToScreen(Position(0f)))
        assertTrue(abs(abs(shifted.x - 0.5f * w) - 0.25f * w) < 0.5f, "a shift of 0.25 moves the image by a quarter of the width, got $shifted")
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun perspectiveCentresTheTargetAndDropsPointsBehind() = withEngine { engine, setContent ->
        val cam = mount(engine, setContent, Projection.Perspective())
        val (w, h) = cam.layoutSize.let { it.width.toFloat() to it.height.toFloat() }
        assertNear(Offset(0.5f * w, 0.5f * h), cam.worldToScreen(Position(0f)), "the target")
        assertTrue(assertNotNull(cam.worldToScreen(Position(0f, 1f, 0f))).y < 0.5f * h, "a point above the target is higher on screen")
        assertNull(cam.worldToScreen(Position(0f, 0f, 20f)), "a point behind the camera has no screen position")
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun anchorCentresTheElementAndFollowsTheCamera() = withEngine { engine, setContent ->
        val cam = mount(engine, setContent, ortho, label = Position(0.5f, 0.5f, 0f))
        val (w, h) = cam.layoutSize.let { it.width.toFloat() to it.height.toFloat() }
        fun labelCentre() = onNodeWithTag("label").fetchSemanticsNode().boundsInRoot.center
        assertNear(Offset(0.75f * w, 0.25f * h), labelCentre(), "the label")

        // Pan right by half a unit: the point moves a quarter of the width to the left.
        cam.eye = Position(0.5f, 0f, 10f)
        cam.target = Position(0.5f, 0f, 0f)
        settle()
        assertNear(Offset(0.5f * w, 0.25f * h), labelCentre(), "the label after the camera moved")
    }
}
