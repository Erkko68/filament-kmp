package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import io.github.erkko68.filament.testutils.createTestSurface
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SwapChainTest : FilamentTestFixture() {
    @Test
    fun testSwapChainCompanion() {
        // Query capability methods on Engine
        SwapChain.isProtectedContentSupported(engine)
        SwapChain.isSRGBSwapChainSupported(engine)
        SwapChain.isMSAASwapChainSupported(engine, 4)
    }

    @Test
    fun testSwapChainLifecycle() {
        val swap = engine.createSwapChain(100, 100, 0L)
        assertNotNull(swap)
        assertTrue(engine.isValidSwapChain(swap))

        val win = swap.nativeWindow
        val obj = swap.nativeObject
        assertTrue(obj != 0L)

        assertFalse(swap.isFrameScheduledCallbackSet)
        
        swap.setFrameCompletedCallback {
            // Frame completed
        }
        swap.setFrameScheduledCallback {
            // Frame scheduled
        }
        assertTrue(swap.isFrameScheduledCallbackSet)

        // 1.73.0 frame rate API — headless/NOOP surfaces don't support rate changes,
        // but the calls must be safe no-ops.
        swap.isFrameRateChangeSupported()
        swap.setFrameRate(60.0f)
        swap.setFrameRate(0.0f, SwapChain.FrameRateCompatibility.FIXED_SOURCE, SwapChain.ChangeFrameRateStrategy.ALWAYS)

        engine.destroySwapChain(swap)
    }
}
