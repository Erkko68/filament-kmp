package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertContentEquals
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
            interval = 1
            headRoomRatio = 0.05f
            scaleRate = 0.5f
            history = 8
        }
        assertEquals(1, fro.interval)
        assertEquals(0.05f, fro.headRoomRatio)
        assertEquals(0.5f, fro.scaleRate)
        assertEquals(8, fro.history)

        val co = Renderer.ClearOptions().apply {
            clearColor = doubleArrayOf(0.1, 0.2, 0.3, 1.0)
            clear = true
            discard = true
        }
        assertEquals(0.1, co.clearColor[0], 1e-9)
        assertEquals(0.2, co.clearColor[1], 1e-9)
        assertTrue(co.clear)
        assertTrue(co.discard)
    }

    @Test
    fun testRendererLifecycleAndMethods() {
        val renderer = engine.createRenderer()
        assertNotNull(renderer)
        assertTrue(engine.isValid(renderer))
        assertEquals(engine, renderer.engine)

        // Set options
        val di = Renderer.DisplayInfo().apply { refreshRate = 120f }
        renderer.setDisplayInfo(di)
        renderer.setFrameRateOptions(Renderer.FrameRateOptions().apply { interval = 2 })

        renderer.clearOptions = Renderer.ClearOptions().apply {
            clearColor = doubleArrayOf(0.1, 0.2, 0.3, 0.4); clearStencil = 7; clear = false; discard = false
        }
        renderer.clearOptions.run {
            assertContentEquals(doubleArrayOf(0.1, 0.2, 0.3, 0.4), clearColor)
            assertEquals(7, clearStencil)
            assertFalse(clear)
            assertFalse(discard)
        }

        assertTrue(renderer.maxFrameHistorySize > 0)
        assertTrue(renderer.getFrameInfoHistory(renderer.maxFrameHistorySize).size <= renderer.maxFrameHistorySize)
        assertTrue(renderer.getFrameInfoHistory().size <= 1)

        // Timing
        renderer.setPresentationTime(1000000L)
        renderer.setDesiredPresentationTime(1000000L)
        renderer.setRenderingDeadline(1000000L)
        renderer.setVsyncTime(1000000L)
        renderer.skipFrame(1000000L)
        
        renderer.setFrameScheduleTime(Engine.steadyClockTimeNano)
        renderer.pauseRenderThread(1_000L)
        assertFalse(renderer.hasGpuFallenBehind)
        renderer.setMaterialTimeEpoch(Engine.steadyClockTimeNano)
        assertTrue(renderer.materialTime >= 0.0)

        // Frame skipping (beginFrame/readPixels panic under NOOP; RendererRenderingTest covers them)
        assertTrue(renderer.shouldRenderFrame())
        renderer.skipNextFrames(3)
        assertEquals(3, renderer.frameToSkipCount)

        renderer.resetUserTime()
        assertTrue(renderer.userTime >= 0.0)

        // Cleanup
        engine.destroy(renderer)
    }
}
