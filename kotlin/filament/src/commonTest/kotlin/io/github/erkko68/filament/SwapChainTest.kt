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

        engine.destroySwapChain(swap)
    }
}
