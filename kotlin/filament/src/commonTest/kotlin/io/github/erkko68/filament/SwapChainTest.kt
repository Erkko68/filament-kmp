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
        swap.isFrameRateChangeSupported
        swap.setFrameRate(60.0f)
        swap.setFrameRate(0.0f, SwapChain.FrameRateCompatibility.FIXED_SOURCE, SwapChain.ChangeFrameRateStrategy.ALWAYS)

        engine.destroySwapChain(swap)
    }

    /**
     * Re-setting a frame callback must swap it in place, not free the stub the backend may
     * still hold for an in-flight frame, and destroying the swapchain must release whatever
     * the last callback held. Nothing renders here, so what is checkable everywhere is the
     * set/replace/clear contract and that the teardown is safe.
     */
    @Test
    fun testFrameCallbacksReplaceInPlace() {
        val swap = engine.createSwapChain(64, 64, 0L)
        assertFalse(swap.isFrameScheduledCallbackSet)

        repeat(8) { i ->
            swap.setFrameScheduledCallback { }
            swap.setFrameCompletedCallback { }
            assertTrue(swap.isFrameScheduledCallbackSet, "callback lost on re-set #$i")
        }

        swap.setFrameScheduledCallback(null)
        swap.setFrameCompletedCallback(null)
        assertFalse(swap.isFrameScheduledCallbackSet, "null did not detach the callback")

        // Re-arming after a detach has to work — the stub is reused, not resurrected.
        swap.setFrameScheduledCallback { }
        assertTrue(swap.isFrameScheduledCallbackSet)

        // Teardown must release whatever the last callback held, and must not double-free
        // the stub the earlier re-sets deliberately left alive.
        engine.destroySwapChain(swap)
    }
}
