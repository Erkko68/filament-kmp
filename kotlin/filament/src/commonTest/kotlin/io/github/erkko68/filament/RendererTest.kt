package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import io.github.erkko68.filament.testutils.createTestSurface
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RendererTest : FilamentTestFixture() {
    @Test
    fun testOptionsAndProperties() {
        val di = Renderer.DisplayInfo().apply {
            refreshRate = 60f
        }
        assertEquals(60f, di.refreshRate)

        val fro = Renderer.FrameRateOptions().apply {
            interval = 1f
            headRoomRatio = 0.05f
            scaleRate = 0.5f
            history = 8
        }
        assertEquals(1f, fro.interval)
        assertEquals(0.05f, fro.headRoomRatio)
        assertEquals(0.5f, fro.scaleRate)
        assertEquals(8, fro.history)

        val co = Renderer.ClearOptions().apply {
            clearColor = floatArrayOf(0.1f, 0.2f, 0.3f, 1f)
            clear = true
            discard = true
        }
        assertEquals(0.1f, co.clearColor[0], 1e-6f)
        assertEquals(0.2f, co.clearColor[1], 1e-6f)
        assertTrue(co.clear)
        assertTrue(co.discard)
    }

    @Test
    fun testRendererLifecycleAndMethods() {
        val renderer = engine.createRenderer()
        assertNotNull(renderer)
        assertTrue(engine.isValidRenderer(renderer))
        assertEquals(engine, renderer.engine)

        // Set options
        val di = Renderer.DisplayInfo().apply { refreshRate = 120f }
        renderer.displayInfo = di
        assertEquals(120f, renderer.displayInfo.refreshRate)

        val fro = Renderer.FrameRateOptions().apply { interval = 2f }
        renderer.frameRateOptions = fro
        assertEquals(2f, renderer.frameRateOptions.interval)

        val co = Renderer.ClearOptions().apply { clear = false }
        renderer.clearOptions = co
        assertFalse(renderer.clearOptions.clear)

        // Timing
        renderer.setPresentationTime(1000000L)
        renderer.setVsyncTime(1000000L)
        renderer.skipFrame(1000000L)
        
        val swap = engine.createSwapChain(100, 100, 0L)
        
        // TODO: The following beginFrame/endFrame and readPixels calls cause driver-specific precondition panics under the NOOP backend driver
        // val begun = renderer.beginFrame(swap, 1000000L)
        // if (begun) {
        //     renderer.endFrame()
        // }

        // Frame skipping
        renderer.skipNextFrames(3)
        assertEquals(3, renderer.frameToSkipCount)

        // val buffer = Texture.PixelBufferDescriptor(ByteArray(100), 100, Texture.Format.RGBA, Texture.Type.UBYTE)
        // renderer.readPixels(0, 0, 5, 5, buffer)

        renderer.resetUserTime()
        assertTrue(renderer.userTime >= 0.0)

        // Cleanup
        engine.destroySwapChain(swap)
        engine.destroyRenderer(renderer)
    }
}
