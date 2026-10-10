package io.github.erkko68.filament.compose

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.unit.dp
import io.github.erkko68.filament.View
import io.github.erkko68.filament.compose.scene.CameraState
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.GltfAsset
import io.github.erkko68.filament.compose.scene.GltfInstance
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.primitives.Cube
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberGltfAsset
import io.github.erkko68.filament.compose.scene.rememberUnlitColorMaterialInstance
import io.github.erkko68.filament.compose.testutils.TestGlb
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * What an app with several screens puts a view through: screens swapping on an engine hoisted above them,
 * state holders, a scene or an asset outliving the screen that showed them, and views that move, overlap in a
 * transition or scroll away. Skips without a GPU.
 */
@OptIn(ExperimentalTestApi::class)
class ScreensTest {

    /** Fills the view from the default camera: a view that rendered it counts one visible renderable. */
    @Composable
    private fun FilamentSceneScope.FillingCube() =
        Cube(rememberUnlitColorMaterialInstance(LinearColor(1f, 1f, 1f)), size = 50f, position = Position(0f, 0f, -50f))

    /** A camera and view state kept above the screens are handed from the screen leaving to the one arriving. */
    @Test
    fun statesHoistedAboveTheScreensFollowTheScreenOnShow() = withEngine { engine, setContent ->
        var screen by mutableStateOf(0)
        val viewState = FilamentViewState()
        var cam: CameraState? = null
        setContent {
            val camera = rememberCameraState().also { cam = it }
            when (screen) {
                0 -> FilamentSceneView(Modifier.size(64.dp), engine = engine, cameraState = camera, viewState = viewState) { FillingCube() }
                else -> FilamentSceneView(Modifier.size(64.dp), engine = engine, cameraState = camera, viewState = viewState) { DirectionalLight() }
            }
        }
        renderFrames(engine, 5)
        val camera = assertNotNull(cam)
        val left = mutableListOf<View>()
        // Back and forth, as tapping between two tabs does.
        repeat(6) { visit ->
            val view = assertNotNull(viewState.view, "visit $visit: the state should be attached to the screen on show")
            assertTrue(left.none { engine.isValid(it) }, "visit $visit: the views of the screens that left should be destroyed")
            assertEquals(if (screen == 0) 1 else 0, view.visibleRenderableCount, "visit $visit: the screen should render its own scene")
            assertNotNull(camera.viewMatrix, "visit $visit: the camera state should be attached")

            // The pose set on one screen is the one the next screen's camera starts from.
            camera.eye = Position(visit.toFloat(), 1f, 10f)
            left += view
            screen = 1 - screen
            renderFrames(engine, 5)
            assertEquals(visit.toDouble(), assertNotNull(viewState.view).camera!!.getPosition()[0], 1e-4)
        }
    }

    /** A transition composes both screens at once: each renders through its own view until the old one leaves. */
    @Test
    fun crossfadingScreensBothRenderUntilTheOldOneLeaves() = withEngine { engine, setContent ->
        var screen by mutableStateOf(0)
        val states = List(2) { FilamentViewState() }
        setContent {
            Crossfade(screen) { shown ->
                FilamentSceneView(Modifier.size(64.dp), engine = engine, viewState = states[shown]) { if (shown == 0) FillingCube() }
            }
        }
        renderFrames(engine, 5)
        val first = assertNotNull(states[0].view)

        screen = 1
        renderFrames(engine, 5)
        val second = assertNotNull(states[1].view, "the arriving screen should have its view mid-transition")
        assertTrue(engine.isValid(first), "the leaving screen should keep its view mid-transition")
        assertEquals(1, first.visibleRenderableCount)
        assertEquals(0, second.visibleRenderableCount)

        renderFrames(engine, 40)
        assertNull(states[0].view, "the screen that faded out should have left")
        assertFalse(engine.isValid(first))
        assertTrue(engine.isValid(second))
    }

    /** A scene kept above the screens stays whole while the views onto it come, go and double up. */
    @Test
    fun aSceneHoistedAboveItsScreensOutlivesTheirViews() = withEngine { engine, setContent ->
        var views by mutableStateOf(1)
        val states = List(2) { FilamentViewState() }
        var hoisted: FilamentScene? = null
        setContent {
            val scene = rememberFilamentScene(engine) { FillingCube() }.also { hoisted = it }
            repeat(views) { key(it) { FilamentView(scene, Modifier.size(64.dp), viewState = states[it]) } }
        }
        renderFrames(engine, 5)
        val scene = assertNotNull(hoisted).scene
        val first = assertNotNull(states[0].view)
        assertEquals(1, first.visibleRenderableCount)

        views = 0
        renderFrames(engine, 5)
        assertFalse(engine.isValid(first), "the view goes with its screen")
        assertTrue(engine.isValid(scene), "the scene stays with its owner")
        assertEquals(1, scene.renderableCount)

        views = 2
        renderFrames(engine, 5)
        for (state in states) assertEquals(1, assertNotNull(state.view).visibleRenderableCount, "each view should render the shared scene")

        views = 1
        renderFrames(engine, 5)
        assertNull(states[1].view)
        assertEquals(1, assertNotNull(states[0].view).visibleRenderableCount, "one view leaving should not stop the other")
        assertSame(hoisted?.scene, scene, "the scene should not have been rebuilt")
    }

    /** An asset loaded once above the screens is instanced into each screen's own scene, and outlives them all. */
    @Test
    fun anAssetHoistedAboveTheScreensIsInstancedByEachInTurn() = withEngine { engine, setContent ->
        val bytes = TestGlb.getAnimatedMorphCubeGlbBytes()
        var screen by mutableStateOf<Int?>(0)
        val viewState = FilamentViewState()
        var loaded: GltfAsset? = null
        setContent {
            val asset = rememberGltfAsset(engine = engine) { bytes }.also { loaded = it }
            screen?.let { key(it) { FilamentSceneView(Modifier.size(64.dp), engine = engine, viewState = viewState) { GltfInstance(asset = asset) } } }
        }
        repeat(60) { if (loaded?.isReady != true) renderFrames(engine, 1) }
        val asset = assertNotNull(loaded, "the asset should load")
        renderFrames(engine, 5)
        val instanced = assertNotNull(viewState.view?.scene).renderableCount
        assertTrue(instanced > 0, "the first screen should have instanced the asset")

        repeat(4) {
            val leaving = assertNotNull(viewState.view?.scene)
            screen = 1 - screen!!
            renderFrames(engine, 5)
            val arrived = assertNotNull(viewState.view?.scene)
            assertFalse(engine.isValid(leaving), "the screen that left should take its scene")
            assertEquals(instanced, arrived.renderableCount, "the next screen should instance the same asset")
        }

        screen = null
        renderFrames(engine, 5)
        assertNull(viewState.view)
        assertSame(asset, loaded, "the asset should not have reloaded")
        assertTrue(engine.entityManager.isAlive(asset.filamentAsset.root), "the asset stays with its owner")
    }

    /** Moved between layouts (a pane becoming a page), a view keeps its Filament view and goes on rendering. */
    @Test
    fun aViewMovedBetweenLayoutsKeepsItsViewAndScene() = withEngine { engine, setContent ->
        var inRow by mutableStateOf(false)
        val viewState = FilamentViewState()
        setContent {
            val view = remember {
                movableContentOf { FilamentSceneView(Modifier.size(64.dp), engine = engine, viewState = viewState) { FillingCube() } }
            }
            if (inRow) Row { view() } else Column { view() }
        }
        renderFrames(engine, 5)
        val before = assertNotNull(viewState.view)

        inRow = true
        renderFrames(engine, 5)
        assertSame(before, viewState.view, "moving the view should not rebuild it")
        assertTrue(engine.isValid(before))
        assertEquals(1, before.visibleRenderableCount)
    }

    /** A view that starts collapsed, as under an expanding container, renders once it has a size. */
    @Test
    fun aViewLaidOutEmptyRendersOnceItHasASize() = withEngine { engine, setContent ->
        var size by mutableStateOf(0.dp)
        val viewState = FilamentViewState()
        setContent {
            FilamentSceneView(Modifier.size(size), engine = engine, viewState = viewState) { FillingCube() }
        }
        renderFrames(engine, 5)
        val view = assertNotNull(viewState.view, "an empty view is still attached")

        size = 64.dp
        renderFrames(engine, 10)
        assertEquals(1, view.visibleRenderableCount)

        // Collapsed again, past the resize debounce, and back.
        size = 0.dp
        renderFrames(engine, 15)
        size = 32.dp
        renderFrames(engine, 15)
        assertSame(view, viewState.view)
        assertEquals(32, view.viewport.width)
    }

    /** A list of views on one engine: the ones scrolled away are destroyed, and none is left once the list goes. */
    @Test
    fun scrollingAListOfViewsOnOneEngine() = withEngine { engine, setContent ->
        val list = LazyListState()
        val views = mutableListOf<View>()
        setContent {
            LazyColumn(Modifier.size(64.dp, 128.dp), list) {
                items(30) {
                    val viewState = rememberFilamentViewState()
                    FilamentSceneView(Modifier.size(64.dp), engine = engine, viewState = viewState) { FillingCube() }
                    viewState.view?.let { view -> remember(view) { views += view } }
                }
            }
        }
        renderFrames(engine, 5)
        for (delta in listOf(40f, -40f)) repeat(40) {
            runOnUiThread { list.dispatchRawDelta(delta) }
            renderFrames(engine, 1)
        }
        val onScreen = views.count { engine.isValid(it) }
        assertTrue(views.size > 4, "scrolling should have brought new views in, got ${views.size}")
        assertTrue(onScreen in 1..4, "only the views near the viewport should be alive, got $onScreen of ${views.size}")

        setContent {}
        renderFrames(engine, 2)
        assertTrue(views.none { engine.isValid(it) }, "no view should outlive the list")
    }
}
