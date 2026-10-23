package io.github.erkko68.filament.compose

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.click
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.unit.dp
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.View
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.CameraState
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.primitives.Cube
import io.github.erkko68.filament.compose.scene.rememberUnlitColorMaterialInstance
import io.github.erkko68.filament.compose.testutils.renderFrames
import io.github.erkko68.filament.compose.testutils.withEngine
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * What needs a real backend: the frame a view shows, and what a pick hits (NOOP answers every pick with no
 * renderable). Skips without a GPU.
 */
class FilamentViewRealBackendTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun viewShowsItsScene() = withEngine(Engine.Backend.DEFAULT) { engine, setContent ->
        setContent {
            FilamentSceneView(Modifier.testTag("view").size(64.dp), engine = engine) {
                Cube(rememberUnlitColorMaterialInstance(LinearColor(1f, 0f, 0f)), size = 50f, position = Position(0f, 0f, -50f))
            }
        }
        renderFrames(engine, 10)
        val pixels = onNodeWithTag("view").captureToImage().toPixelMap()
        val centre = pixels[pixels.width / 2, pixels.height / 2]
        assertTrue(centre.red > 0.6f && centre.green < 0.2f && centre.blue < 0.2f, "the red cube should be on screen, got $centre")
    }

    /** capture() is what the view shows, rows in screen order: red above the centre, blue below, as on screen. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun captureMatchesTheViewTopToBottom() = withEngine(Engine.Backend.DEFAULT) { engine, setContent ->
        val viewState = FilamentViewState()
        setContent {
            FilamentSceneView(Modifier.testTag("view").size(64.dp), engine = engine, viewState = viewState,
                cameraState = rememberCameraState(initialEye = Position(0f, 0f, 10f))) {
                Cube(rememberUnlitColorMaterialInstance(LinearColor(1f, 0f, 0f)), size = 2f, position = Position(0f, 2f, 0f))
                Cube(rememberUnlitColorMaterialInstance(LinearColor(0f, 0f, 1f)), size = 2f, position = Position(0f, -2f, 0f))
            }
        }
        renderFrames(engine, 10)
        var image: ImageBitmap? = null
        runOnUiThread { CoroutineScope(Dispatchers.Unconfined).launch { image = viewState.capture() } }
        repeat(30) {
            if (image != null) return@repeat
            mainClock.advanceTimeByFrame()
            runOnUiThread { engine.flushAndWait() }
        }
        val captured = assertNotNull(image, "the view should answer a capture").toPixelMap()
        val screen = onNodeWithTag("view").captureToImage().toPixelMap()

        // 10 away under the default 45° vertical lens the view spans ±4.14 in y: the cubes' centres sit at 26% and 74%.
        for (pixels in listOf(captured, screen)) {
            val top = pixels[pixels.width / 2, pixels.height * 26 / 100]
            val bottom = pixels[pixels.width / 2, pixels.height * 74 / 100]
            assertTrue(top.red > 0.6f && top.blue < 0.2f, "the red cube should be above the centre, got $top")
            assertTrue(bottom.blue > 0.6f && bottom.red < 0.2f, "the blue cube should be below the centre, got $bottom")
        }
    }

    /** Compose rows run top-down, Filament's bottom-up: the top and bottom rows are both inside the viewport. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun pickReachesTheFirstAndLastRows() = withEngine(Engine.Backend.DEFAULT) { engine, setContent ->
        val viewState = FilamentViewState()
        setContent {
            FilamentSceneView(Modifier.size(64.dp), engine = engine, viewState = viewState) {
                // Fills the view: every pixel picks it.
                Cube(rememberUnlitColorMaterialInstance(LinearColor(1f, 1f, 1f)), size = 50f, position = Position(0f, 0f, -50f))
            }
        }
        repeat(3) { mainClock.advanceTimeByFrame() }
        val viewport = assertNotNull(viewState.view).viewport

        fun pick(y: Int): Int {
            var result: View.PickingQueryResult? = null
            runOnUiThread { viewState.pick(viewport.width / 2, y) { result = it } }
            repeat(30) {
                if (result != null) return@repeat
                mainClock.advanceTimeByFrame()
                runOnUiThread { engine.flushAndWait() }
            }
            return assertNotNull(result, "no picking result for row $y").renderable
        }
        assertNotEquals(0, pick(viewport.height / 2), "the cube should fill the view")
        assertNotEquals(0, pick(0), "top row")
        assertNotEquals(0, pick(viewport.height - 1), "bottom row")

        // Outside the viewport: ignored, where Filament would abort.
        var outside = false
        runOnUiThread {
            viewState.pick(viewport.width, 0) { outside = true }
            viewState.pick(0, viewport.height) { outside = true }
            viewState.pick(-1, -1) { outside = true }
        }
        repeat(5) {
            mainClock.advanceTimeByFrame()
            runOnUiThread { engine.flushAndWait() }
        }
        assertFalse(outside, "a pick outside the viewport should be ignored")
    }

    /** Desktop debounces a resize, stretching the old surface over the new layout: a pick is given in layout pixels. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun pickFollowsTheLayoutWhileTheSurfaceLags() = withEngine(Engine.Backend.DEFAULT) { engine, setContent ->
        val viewState = FilamentViewState()
        var size by mutableStateOf(64.dp)
        setContent {
            FilamentSceneView(Modifier.size(size), engine = engine, viewState = viewState) {
                Cube(rememberUnlitColorMaterialInstance(LinearColor(1f, 1f, 1f)), size = 50f, position = Position(0f, 0f, -50f))
            }
        }
        repeat(3) { mainClock.advanceTimeByFrame() }
        val before = assertNotNull(viewState.view).viewport

        size = 128.dp
        repeat(2) { mainClock.advanceTimeByFrame() }
        val layout = viewState.layoutSize
        assertEquals(before.width, assertNotNull(viewState.view).viewport.width, "the surface should still be the old size")
        assertEquals(before.width * 2, layout.width)

        var result: View.PickingQueryResult? = null
        runOnUiThread { viewState.pick(layout.width - 1, layout.height - 1) { result = it } }
        repeat(30) {
            if (result != null) return@repeat
            mainClock.advanceTimeByFrame()
            runOnUiThread { engine.flushAndWait() }
        }
        assertNotEquals(0, assertNotNull(result, "a pick in the grown layout should be answered").renderable)
    }

    /** A click reaches the hit object's onTap through orbit gestures on the same view; a drag only orbits. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun clickReachesOnTapAndDragDoesNot() = withEngine(Engine.Backend.DEFAULT) { engine, setContent ->
        var tapped = 0
        setContent {
            val cam = rememberCameraState()
            FilamentSceneView(Modifier.testTag("view").size(64.dp).orbitGestures(rememberOrbitCameraController(cam)),
                engine = engine, cameraState = cam) {
                Cube(rememberUnlitColorMaterialInstance(LinearColor(1f, 1f, 1f)), size = 50f, position = Position(0f, 0f, -50f),
                    onTap = { tapped++ })
            }
        }
        repeat(3) { mainClock.advanceTimeByFrame() }
        fun settle() = repeat(30) {
            mainClock.advanceTimeByFrame()
            runOnUiThread { engine.flushAndWait() }
        }

        onNodeWithTag("view").performMouseInput { click(center) }
        settle()
        assertEquals(1, tapped, "a click on the cube should reach its onTap")

        onNodeWithTag("view").performMouseInput {
            moveTo(center); press(); moveTo(center + Offset(20f, 0f)); moveTo(center + Offset(40f, 0f)); release()
        }
        settle()
        assertEquals(1, tapped, "a drag should orbit, not tap")
    }

    /** onTap gets the world point it hit: a click at the centre lands on the middle of the cube's front face. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun onTapGetsTheWorldPointItHit() = withEngine(Engine.Backend.DEFAULT) { engine, setContent ->
        var hit: Position? = null
        setContent {
            FilamentSceneView(Modifier.testTag("view").size(64.dp), engine = engine,
                cameraState = rememberCameraState(initialEye = Position(0f, 0f, 10f))) {
                Cube(rememberUnlitColorMaterialInstance(LinearColor(1f, 1f, 1f)), size = 4f, onTap = { hit = it })
            }
        }
        repeat(3) { mainClock.advanceTimeByFrame() }
        onNodeWithTag("view").performMouseInput { click(center) }
        repeat(30) {
            mainClock.advanceTimeByFrame()
            runOnUiThread { engine.flushAndWait() }
        }
        val at = assertNotNull(hit, "a click on the cube should reach its onTap")
        // The face is flat at z = 2; x and y are off the centre by at most a pixel, ~0.1 units at that depth.
        assertTrue(abs(at.z - 2f) < 0.05f && abs(at.x) < 0.2f && abs(at.y) < 0.2f, "expected about (0, 0, 2), got $at")
    }

    /** A finger moves a pixel or two during a tap: under the touch slop it stays a tap, and the camera stays put. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun touchTapThatJittersReachesOnTap() = withEngine(Engine.Backend.DEFAULT) { engine, setContent ->
        var tapped = 0
        var cam: CameraState? = null
        setContent {
            val state = rememberCameraState().also { cam = it }
            FilamentSceneView(Modifier.testTag("view").size(64.dp).orbitGestures(rememberOrbitCameraController(state)),
                engine = engine, cameraState = state) {
                Cube(rememberUnlitColorMaterialInstance(LinearColor(1f, 1f, 1f)), size = 50f, position = Position(0f, 0f, -50f),
                    onTap = { tapped++ })
            }
        }
        repeat(3) { mainClock.advanceTimeByFrame() }
        val eye = assertNotNull(cam).eye

        onNodeWithTag("view").performTouchInput { down(center); moveBy(Offset(2f, 1f)); up() }
        repeat(30) {
            mainClock.advanceTimeByFrame()
            runOnUiThread { engine.flushAndWait() }
        }
        assertEquals(1, tapped, "a tap that moves under the touch slop should reach onTap")
        assertEquals(eye, assertNotNull(cam).eye, "a tap shouldn't move the camera")
    }
}
