package io.github.erkko68.filament.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.View
import io.github.erkko68.filament.compose.scene.CameraState
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.Environment
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.primitives.Cube
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberIndirectLightState
import io.github.erkko68.filament.compose.scene.rememberSkyboxState
import io.github.erkko68.filament.compose.scene.rememberUnlitColorMaterialInstance
import io.github.erkko68.filament.testsupport.TestEnv
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * [FilamentSceneView] / [FilamentView] on a real engine: the view and camera attach while composed and
 * detach after, and [pickOnTap] delivers a picking result once frames render. Skips without a GPU.
 */
class FilamentViewTest {

    /**
     * Runs [body] with a DEFAULT engine created and destroyed on the UI thread, like rememberFilamentEngine.
     * Content goes through the handed `setContent`, which is cleared before the engine is destroyed.
     */
    @OptIn(ExperimentalTestApi::class)
    private fun withEngine(body: ComposeUiTest.(Engine, setContent: (@Composable () -> Unit) -> Unit) -> Unit) =
        runComposeUiTest {
            if (!TestEnv.gpuBackendAvailable) return@runComposeUiTest
            // The render loop's frame callbacks never let the clock go idle: drive frames by hand.
            mainClock.autoAdvance = false
            var engine: Engine? = null
            runOnUiThread {
                Filament.init()
                engine = Engine.create(Engine.Backend.DEFAULT)
            }
            val e = engine ?: return@runComposeUiTest
            var slot by mutableStateOf<@Composable () -> Unit>({})
            setContent { slot() }
            try {
                body(e) { slot = it }
            } finally {
                slot = {}
                mainClock.advanceTimeByFrame()
                mainClock.autoAdvance = true
                runOnUiThread {
                    e.flushAndWait()
                    Engine.destroy(e)
                }
            }
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun viewAttachesWhileComposedAndPicksOnTap() = withEngine { engine, setContent ->
        var show by mutableStateOf(true)
        val viewState = FilamentViewState()
        var cam: CameraState? = null
        var picked: View.PickingQueryResult? = null
        setContent {
            if (show) {
                Box(Modifier.testTag("view").size(64.dp).pickOnTap(viewState) { picked = it }) {
                    FilamentSceneView(
                        Modifier.size(64.dp),
                        engine = engine,
                        cameraState = rememberCameraState().also { cam = it },
                        viewState = viewState,
                    ) { DirectionalLight() }
                }
            }
        }
        repeat(3) { mainClock.advanceTimeByFrame() }
        assertNotNull(viewState.view)
        assertNotNull(viewState.renderer)
        assertNotNull(cam?.viewMatrix, "the camera state is attached")

        onNodeWithTag("view").performClick()
        repeat(30) {
            if (picked != null) return@repeat
            mainClock.advanceTimeByFrame()
            runOnUiThread { engine.flushAndWait() }
        }
        assertNotNull(picked, "a tap should deliver a picking result")

        show = false
        mainClock.advanceTimeByFrame()
        assertNull(viewState.view, "the view detaches with its composition")
        assertNull(cam?.viewMatrix)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun environmentOverloadsWireItsStates() = withEngine { engine, setContent ->
        var viewState: FilamentViewState? = null
        var scene: FilamentScene? = null
        setContent {
            val environment = Environment(rememberIndirectLightState(), rememberSkyboxState())
            scene = rememberFilamentScene(engine, environment) {}
            FilamentSceneView(engine, environment, Modifier.size(32.dp), viewState = rememberFilamentViewState().also { viewState = it }) {}
        }
        repeat(3) { mainClock.advanceTimeByFrame() }
        assertNotNull(scene)
        assertTrue(viewState?.view?.scene != null, "the scene renders through the environment overload")
    }

    /** Compose rows run top-down, Filament's bottom-up: the top and bottom rows are both inside the viewport. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun pickReachesTheFirstAndLastRows() = withEngine { engine, setContent ->
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

    /** A view handed another engine moves its surface over too, instead of rendering into the old engine's target. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun viewFollowsAnEngineChange() = withEngine { first, setContent ->
        var second: Engine? = null
        runOnUiThread { second = Engine.create(Engine.Backend.DEFAULT) }
        val other = assertNotNull(second)
        try {
            var engine by mutableStateOf(first)
            val viewState = FilamentViewState()
            setContent {
                FilamentSceneView(Modifier.size(64.dp), engine = engine, viewState = viewState) { DirectionalLight() }
            }
            repeat(3) { mainClock.advanceTimeByFrame() }
            val firstView = assertNotNull(viewState.view)

            engine = other
            repeat(5) { mainClock.advanceTimeByFrame() }
            assertTrue(viewState.view != null && viewState.view !== firstView, "the view is rebuilt on the new engine")
            assertTrue(other.isValid(assertNotNull(viewState.view)), "the view belongs to the new engine")

            setContent {}
            mainClock.advanceTimeByFrame()
        } finally {
            runOnUiThread {
                other.flushAndWait()
                Engine.destroy(other)
            }
        }
    }

    /**
     * `renderingEnabled = false` holds the last frame, so there has to be one: a view that starts paused, or is
     * paused over a change, still renders until that scene is on screen, and only then stops.
     */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun pausedViewStillRendersItsScene() = withEngine { engine, setContent ->
        val viewState = FilamentViewState()
        setContent {
            FilamentSceneView(Modifier.size(64.dp), engine = engine, viewState = viewState, renderingEnabled = false) {
                Cube(rememberUnlitColorMaterialInstance(LinearColor(1f, 1f, 1f)), size = 50f, position = Position(0f, 0f, -50f))
            }
        }
        repeat(10) { mainClock.advanceTimeByFrame() }
        // -1 until the view has been rendered.
        assertEquals(1, assertNotNull(viewState.view).visibleRenderableCount, "a view that starts paused should render its scene")

        // Then it holds that frame: hiding the cube is not rendered until rendering resumes.
        var visible by mutableStateOf(true)
        var enabled by mutableStateOf(true)
        setContent {
            FilamentSceneView(Modifier.size(64.dp), engine = engine, viewState = viewState, renderingEnabled = enabled) {
                Cube(
                    rememberUnlitColorMaterialInstance(LinearColor(1f, 1f, 1f)), size = 50f,
                    position = Position(0f, 0f, -50f), visible = visible,
                )
            }
        }
        repeat(10) { mainClock.advanceTimeByFrame() }
        assertEquals(1, assertNotNull(viewState.view).visibleRenderableCount)

        // Paused together with a change: that change still gets on screen.
        enabled = false
        visible = false
        repeat(10) { mainClock.advanceTimeByFrame() }
        assertEquals(0, assertNotNull(viewState.view).visibleRenderableCount, "the change made with the pause should be rendered")

        // A later change, well after the pause, is not.
        visible = true
        repeat(10) { mainClock.advanceTimeByFrame() }
        assertEquals(0, assertNotNull(viewState.view).visibleRenderableCount, "a settled paused view should not render")

        enabled = true
        repeat(5) { mainClock.advanceTimeByFrame() }
        assertEquals(1, assertNotNull(viewState.view).visibleRenderableCount, "resuming renders again")
    }
}
