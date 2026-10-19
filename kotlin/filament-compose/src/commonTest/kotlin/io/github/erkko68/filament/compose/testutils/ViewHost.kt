package io.github.erkko68.filament.compose.testutils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.testsupport.TestEnv

/**
 * Skips a [withEngine] test where Compose's test host can't embed the view's platform view (an HTML canvas on web,
 * a UIView on iOS: "LocalInteropContainer not provided"); runs on JVM and Android. Their real views are covered by
 * [surfaceTest]'s own hosts.
 */
expect annotation class IgnoreWithoutViewInterop()

/** Advances [frames] frames, waiting for the GPU before each: a readback still in flight holds back the next render. */
@OptIn(ExperimentalTestApi::class)
fun ComposeUiTest.renderFrames(engine: Engine, frames: Int) = repeat(frames) {
    runOnUiThread { engine.flushAndWait() }
    mainClock.advanceTimeByFrame()
}

/**
 * Runs [body] with a [backend] engine created and destroyed on the UI thread, like rememberFilamentEngine.
 * Content goes through the handed `setContent`, which is cleared before the engine is destroyed.
 * A real backend skips without a GPU.
 */
@OptIn(ExperimentalTestApi::class)
fun withEngine(
    backend: Engine.Backend = Engine.Backend.NOOP,
    body: ComposeUiTest.(Engine, setContent: (@Composable () -> Unit) -> Unit) -> Unit,
) = if (backend != Engine.Backend.NOOP && !TestEnv.gpuBackendAvailable) skippedComposeTest() else
    runComposeUiTest {
        // The render loop's frame callbacks never let the clock go idle: drive frames by hand.
        mainClock.autoAdvance = false
        var engine: Engine? = null
        runOnUiThread {
            Filament.init()
            engine = Engine.create(backend)
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
