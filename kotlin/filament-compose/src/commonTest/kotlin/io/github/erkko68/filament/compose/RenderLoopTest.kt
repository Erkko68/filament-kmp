package io.github.erkko68.filament.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import io.github.erkko68.filament.compose.internal.FilamentRenderLoop
import io.github.erkko68.filament.compose.internal.rememberPausedFrameGate
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

    // A surface with frames in flight, like the desktop targets: what reaches the screen was rendered earlier.
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun pausedLoopRunsUntilAPausedFrameIsOnScreen() = runComposeUiTest {
        mainClock.autoAdvance = false
        var enabled by mutableStateOf(false) // starts paused: there is no last frame to hold yet
        var targets by mutableStateOf(0)
        var frames = 0
        var gpuBusy = true
        setContent {
            val gate = rememberPausedFrameGate(enabled, framesToSettle = 2, targets)
            FilamentRenderLoop(gate.loopEnabled(enabled)) {
                frames++
                if (!gpuBusy) gate.delivered(paused = !enabled)
            }
        }

        // Nothing reaches the screen while the GPU is busy: the paused loop goes on
        repeat(5) { mainClock.advanceTimeByFrame() }
        val whileBusy = frames
        assertTrue(whileBusy >= 3, "a paused surface must keep rendering until its scene is on screen, got $whileBusy")

        // Two frames shown while paused: the loop stops
        gpuBusy = false
        repeat(5) { mainClock.advanceTimeByFrame() }
        val settled = frames
        assertTrue(settled - whileBusy in 2..3, "the loop should stop once it has settled, ran ${settled - whileBusy} more")
        repeat(5) { mainClock.advanceTimeByFrame() }
        assertEquals(settled, frames, "once a paused frame is on screen the loop must stop")

        // New targets (a resize) have shown nothing: it settles again, then stops
        targets++
        repeat(6) { mainClock.advanceTimeByFrame() }
        assertTrue(frames > settled, "new targets must get the paused scene")
        val resized = frames
        repeat(5) { mainClock.advanceTimeByFrame() }
        assertEquals(resized, frames)

        // Resumed, then paused again: the frames shown while rendering don't count
        enabled = true
        repeat(4) { mainClock.advanceTimeByFrame() }
        enabled = false
        mainClock.advanceTimeByFrame()
        val paused = frames
        repeat(6) { mainClock.advanceTimeByFrame() }
        assertTrue(frames - paused in 1..3, "pausing again should settle again, ran ${frames - paused} more")
    }
}
