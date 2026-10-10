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
import io.github.erkko68.filament.compose.scene.Bloom
import io.github.erkko68.filament.compose.scene.CameraState
import io.github.erkko68.filament.compose.scene.ColorGrade
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.Environment
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.PostProcessing
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
        renderFrames(engine, 10)
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
        renderFrames(engine, 10)
        assertEquals(1, assertNotNull(viewState.view).visibleRenderableCount)

        // Paused together with a change: that change still gets on screen.
        enabled = false
        visible = false
        renderFrames(engine, 10)
        assertEquals(0, assertNotNull(viewState.view).visibleRenderableCount, "the change made with the pause should be rendered")

        // A later change, well after the pause, is not.
        visible = true
        renderFrames(engine, 10)
        assertEquals(0, assertNotNull(viewState.view).visibleRenderableCount, "a settled paused view should not render")

        enabled = true
        renderFrames(engine, 5)
        assertEquals(1, assertNotNull(viewState.view).visibleRenderableCount, "resuming renders again")
    }

    /** Filament answers a picking query by rendering: a settled paused view has to render for it, then settle again. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun pickOnAPausedViewIsAnswered() = withEngine { engine, setContent ->
        val viewState = FilamentViewState()
        var visible by mutableStateOf(true)
        setContent {
            FilamentSceneView(Modifier.size(64.dp), engine = engine, viewState = viewState, renderingEnabled = false) {
                Cube(
                    rememberUnlitColorMaterialInstance(LinearColor(1f, 1f, 1f)), size = 50f,
                    position = Position(0f, 0f, -50f), visible = visible,
                )
            }
        }
        renderFrames(engine, 30)
        val view = assertNotNull(viewState.view)
        // Settled: a change is not rendered.
        visible = false
        renderFrames(engine, 10)
        assertEquals(1, view.visibleRenderableCount, "a settled paused view should not render")

        var result: View.PickingQueryResult? = null
        runOnUiThread { viewState.pick(view.viewport.width / 2, view.viewport.height / 2) { result = it } }
        repeat(30) {
            if (result != null) return@repeat
            mainClock.advanceTimeByFrame()
            runOnUiThread { engine.flushAndWait() }
        }
        assertNotNull(result, "a paused view should answer a pick")
        assertEquals(0, view.visibleRenderableCount, "answering it renders the scene as it is now")
        assertEquals(0, viewState.pendingPicks)

        // Answered: it settles again.
        renderFrames(engine, 10)
        visible = true
        renderFrames(engine, 10)
        assertEquals(0, view.visibleRenderableCount, "the view should settle again once the pick is answered")
    }

    /** Desktop debounces a resize, stretching the old surface over the new layout: a pick is given in layout pixels. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun pickFollowsTheLayoutWhileTheSurfaceLags() = withEngine { engine, setContent ->
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

    /** A view keeps the scene it was created for alive, so one handed another scene has to be a new view. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun viewFollowsASceneChange() = withEngine { engine, setContent ->
        val viewState = FilamentViewState()
        var second by mutableStateOf(false)
        var firstScene: FilamentScene? = null
        setContent {
            val a = if (!second) rememberFilamentScene(engine) { DirectionalLight() }.also { firstScene = it } else null
            val b = if (second) rememberFilamentScene(engine) { DirectionalLight() } else null
            FilamentView(a ?: b!!, Modifier.size(64.dp), viewState = viewState)
        }
        repeat(3) { mainClock.advanceTimeByFrame() }
        val firstView = assertNotNull(viewState.view)
        val replaced = assertNotNull(firstScene).scene

        second = true
        repeat(5) { mainClock.advanceTimeByFrame() }
        assertFalse(engine.isValid(replaced), "the scene that left should not be kept by the view")
        assertFalse(engine.isValid(firstView))
        assertTrue(engine.isValid(assertNotNull(viewState.view)), "the view is rebuilt for the new scene")
        assertEquals(0, assertNotNull(viewState.view).visibleRenderableCount, "and renders it")
    }

    /** Baking a ColorGrading's LUT is expensive: only a new grade rebuilds it, not the options around it. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun colorGradingIsRebuiltForANewGradeOnly() = withEngine { engine, setContent ->
        val viewState = FilamentViewState()
        var postProcessing by mutableStateOf(PostProcessing(colorGrade = ColorGrade(contrast = 1.2f), bloom = Bloom(strength = 0.1f)))
        setContent {
            FilamentSceneView(Modifier.size(64.dp), engine = engine, viewState = viewState, postProcessing = postProcessing) {}
        }
        repeat(3) { mainClock.advanceTimeByFrame() }
        val view = assertNotNull(viewState.view)
        val grading = assertNotNull(view.colorGrading)

        postProcessing = postProcessing.copy(bloom = Bloom(strength = 0.5f))
        repeat(2) { mainClock.advanceTimeByFrame() }
        assertEquals(0.5f, view.bloomOptions.strength)
        assertTrue(view.colorGrading === grading, "another option should not rebuild the ColorGrading")

        postProcessing = postProcessing.copy(colorGrade = ColorGrade(contrast = 1.5f))
        repeat(2) { mainClock.advanceTimeByFrame() }
        val regraded = assertNotNull(view.colorGrading)
        assertTrue(engine.isValid(regraded) && !engine.isValid(grading), "a new grade replaces and destroys the old one")

        postProcessing = postProcessing.copy(colorGrade = null)
        repeat(2) { mainClock.advanceTimeByFrame() }
        assertNull(view.colorGrading)
        assertFalse(engine.isValid(regraded))
    }
}

/** Advances [frames] frames, waiting for the GPU before each: a readback still in flight holds back the next render. */
@OptIn(ExperimentalTestApi::class)
internal fun ComposeUiTest.renderFrames(engine: Engine, frames: Int) = repeat(frames) {
    runOnUiThread { engine.flushAndWait() }
    mainClock.advanceTimeByFrame()
}

/**
 * Runs [body] with a DEFAULT engine created and destroyed on the UI thread, like rememberFilamentEngine.
 * Content goes through the handed `setContent`, which is cleared before the engine is destroyed.
 */
@OptIn(ExperimentalTestApi::class)
internal fun withEngine(body: ComposeUiTest.(Engine, setContent: (@Composable () -> Unit) -> Unit) -> Unit) =
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
