package io.github.erkko68.filament.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import io.github.erkko68.filament.compose.internal.FilamentRenderLoop
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** `renderingEnabled` gates the per-view render loop: no frame callbacks while it is false. */
class RenderLoopTest : ComposeTestFixture() {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun disabledLoopSchedulesNoFramesAndResumes() = runComposeUiTest {
        mainClock.autoAdvance = false
        var enabled by mutableStateOf(true)
        var frames = 0
        setContent { FilamentRenderLoop(enabled) { frames++ } }

        repeat(3) { mainClock.advanceTimeByFrame() }
        assertTrue(frames > 0, "enabled loop should render")

        enabled = false
        mainClock.advanceTimeByFrame() // let the effect restart with enabled = false
        val paused = frames
        repeat(5) { mainClock.advanceTimeByFrame() }
        assertEquals(paused, frames, "disabled loop must not render")

        enabled = true
        repeat(3) { mainClock.advanceTimeByFrame() }
        assertTrue(frames > paused, "re-enabled loop should resume")
    }
}
