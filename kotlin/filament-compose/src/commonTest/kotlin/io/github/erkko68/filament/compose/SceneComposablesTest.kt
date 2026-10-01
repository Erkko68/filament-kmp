package io.github.erkko68.filament.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.compose.testutils.withFilamentScene
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** [FilamentEffect], [rememberSceneClock] and [rememberFilamentScene] — scene-level, so NOOP is enough. */
class SceneComposablesTest : ComposeTestFixture() {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun effectRunsPerKeyTicksPerFrameAndDisposes() = withFilamentScene(engine, scene) { setContent ->
        var key by mutableStateOf(0)
        var runs = 0
        var frames = 0
        var disposals = 0
        setContent {
            FilamentEffect(key) {
                runs++
                onFrame { frames++ }
                onDispose { disposals++ }
            }
        }
        waitForIdle()
        assertEquals(1, runs)
        repeat(3) { mainClock.advanceTimeByFrame() }
        assertTrue(frames > 0, "onFrame should run every frame")

        key = 1
        mainClock.advanceTimeByFrame()
        waitForIdle()
        assertEquals(2, runs, "a new key re-runs the block")
        assertEquals(1, disposals, "after disposing the previous run")

        setContent {}
        waitForIdle()
        assertEquals(2, disposals)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun sceneClockAdvancesWithFrames() = withFilamentScene(engine, scene) { setContent ->
        var seconds = 0f
        setContent { seconds = rememberSceneClock().value }
        repeat(5) { mainClock.advanceTimeByFrame() }
        waitForIdle()
        assertTrue(seconds > 0f, "elapsed time should grow, got $seconds")
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun rememberedSceneHoldsItsContentAndIsDestroyedWithIt() = runComposeUiTest {
        mainClock.autoAdvance = false
        var show by mutableStateOf(true)
        var handle: FilamentScene? = null
        setContent {
            if (show) handle = rememberFilamentScene(engine) { DirectionalLight() }
        }
        mainClock.advanceTimeByFrame()
        waitForIdle()
        val scene = assertNotNull(handle).scene
        assertEquals(engine, handle?.engine)
        assertEquals(1, scene.lightCount)

        show = false
        mainClock.advanceTimeByFrame()
        waitForIdle()
        assertFalse(engine.isValid(scene), "the scene leaves with its composition")
        mainClock.autoAdvance = true
    }
}
