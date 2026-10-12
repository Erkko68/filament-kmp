package io.github.erkko68.filament.compose.testutils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.erkko68.filament.testsupport.TestEnv
import kotlinx.coroutines.test.TestResult

/**
 * Shows [content] for [frames] frames in a host where the platform's real surface works, handing it the frame
 * number, then removes it. Skips without a GPU.
 */
expect fun surfaceTest(frames: Int = 120, content: @Composable (frame: Int) -> Unit): TestResult

/** [surfaceTest] on Compose's test host. [afterFrame] gives a platform's own surface callbacks time to run. */
@OptIn(ExperimentalTestApi::class)
internal fun composeUiSurfaceTest(frames: Int, afterFrame: () -> Unit = {}, content: @Composable (frame: Int) -> Unit) =
    if (!TestEnv.gpuBackendAvailable) skippedComposeTest() else runComposeUiTest {
        mainClock.autoAdvance = false
        var frame by mutableIntStateOf(0)
        var composed by mutableStateOf(true)
        setContent { if (composed) content(frame) }
        repeat(frames) {
            frame++
            mainClock.advanceTimeByFrame()
            afterFrame()
        }
        composed = false
        mainClock.advanceTimeByFrame()
        afterFrame()
        mainClock.autoAdvance = true
    }
