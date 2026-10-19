package io.github.erkko68.filament

import io.github.erkko68.filament.interop.toInterop
import io.github.erkko68.filament.interop.InteropScope
import io.github.erkko68.filament.testutils.FilamentTestFixture
import io.github.erkko68.filament.testsupport.ReadbackFlag
import io.github.erkko68.filament.testsupport.pumpUntil
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RendererTest : FilamentTestFixture() {
    @Test
    fun testRendererLifecycleAndMethods() {
        val renderer = engine.createRenderer()
        assertTrue(engine.isValid(renderer))
        assertEquals(engine, renderer.engine)

        renderer.setDisplayInfo(Renderer.DisplayInfo().apply { refreshRate = 120f })
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

        renderer.setPresentationTime(1000000L)
        renderer.setDesiredPresentationTime(1000000L)
        renderer.setRenderingDeadline(1000000L)
        renderer.setVsyncTime(1000000L)
        renderer.skipFrame(1000000L)
        renderer.setFrameScheduleTime(Engine.steadyClockTimeNano)
        renderer.pauseRenderThread(1_000L)
        assertFalse(renderer.hasGpuFallenBehind)

        assertTrue(renderer.shouldRenderFrame())
        renderer.skipNextFrames(3)
        assertEquals(3, renderer.frameToSkipCount)

        engine.destroy(renderer)
    }

    /** A consumer's frame loop: render, read back both ways, render off-screen and mirror a frame. */
    @Test
    fun framesRenderAndEveryReadbackCompletes() {
        val w = 8
        val h = 8
        val swapChain = engine.createSwapChain(w, h, SwapChain.CONFIG_READABLE)
        val mirror = engine.createSwapChain(w, h)
        val renderer = engine.createRenderer()
        val scene = engine.createScene()
        val camera = engine.createCamera(engine.entityManager.create())
        val view = engine.createView().apply {
            this.scene = scene
            this.camera = camera
            this.viewport = Viewport(0, 0, w, h)
        }
        val color = Texture.Builder().width(w).height(h).format(Texture.InternalFormat.RGBA8)
            .usage(Texture.Usage.COLOR_ATTACHMENT or Texture.Usage.BLIT_SRC).build(engine)
        val target = RenderTarget.Builder().texture(RenderTarget.AttachmentPoint.COLOR, color).build(engine)

        val memory = InteropScope()
        val address = memory.toInterop(IntArray(w * h), w * h)
        val onScreen = ReadbackFlag()
        val native = ReadbackFlag()
        val offScreen = ReadbackFlag()
        val pixels = { done: ReadbackFlag ->
            Texture.PixelBufferDescriptor(ByteArray(w * h * 4), w * h * 4, Texture.Format.RGBA, Texture.Type.UBYTE) { done.done = true }
        }

        repeat(3) {
            assertTrue(renderer.beginFrame(swapChain, 0L), "frame $it was skipped")
            renderer.render(view)
            renderer.endFrame()
        }
        assertTrue(renderer.beginFrame(swapChain, 0L))
        renderer.render(view)
        renderer.readPixels(0, 0, w, h, pixels(onScreen))
        renderer.readPixels(0, 0, w, h, address, w * h * 4, Texture.Format.RGBA, Texture.Type.UBYTE, 0) { native.done = true }
        renderer.copyFrame(mirror, Viewport(0, 0, w, h), Viewport(0, 0, w, h), Renderer.COMMIT)
        renderer.endFrame()

        view.renderTarget = target
        renderer.renderStandaloneView(view)
        renderer.readPixels(target, 0, 0, w, h, pixels(offScreen))
        engine.pumpUntil { onScreen.done && native.done && offScreen.done }
        memory.release()

        engine.destroy(view)
        engine.destroy(target)
        engine.destroy(color)
        engine.destroyCameraComponent(camera.entity)
        engine.entityManager.destroy(camera.entity)
        engine.destroy(scene)
        engine.destroy(renderer)
        engine.destroy(mirror)
        engine.destroy(swapChain)
    }
}
