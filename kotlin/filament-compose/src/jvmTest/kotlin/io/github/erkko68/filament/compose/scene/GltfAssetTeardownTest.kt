package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.erkko68.filament.compose.rememberFilamentEngine
import io.github.erkko68.filament.testsupport.TestEnv
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Regression: an asset leaving composition mid-load, together with the engine it was created on.
 * The loader used to be destroyed in the loading coroutine's `finally`, which runs after every
 * onDispose — so after the engine was gone — and crashed the process (SIGSEGV or a Filament panic).
 */
class GltfAssetTeardownTest {

    // Textured, so the ResourceLoader needs several frames and disposal lands mid-load.
    private val duck by lazy { File("../gltfio/src/commonTest/glb/Duck.glb").readBytes() }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun disposeWhileLoadingWithItsEngine() {
        if (!TestEnv.gpuBackendAvailable) return
        runComposeUiTest {
            mainClock.autoAdvance = false
            repeat(5) {
                var shown by mutableStateOf(true)
                var asset: GltfAsset? = null
                setContent {
                    if (shown) {
                        val engine = rememberFilamentEngine()
                        asset = rememberGltfAsset(engine = engine) { duck }
                    }
                }
                var frames = 0
                while (asset == null && frames++ < 60) {
                    mainClock.advanceTimeByFrame()
                    waitForIdle()
                }
                assertTrue(asset?.isReady == false, "the asset should still be loading when it is disposed")
                shown = false
                repeat(4) {
                    mainClock.advanceTimeByFrame()
                    waitForIdle()
                }
            }
        }
    }
}
