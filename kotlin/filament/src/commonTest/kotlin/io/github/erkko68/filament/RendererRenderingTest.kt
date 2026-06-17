package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.RenderingTestFixture
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Real-backend coverage for [Renderer] frame + readPixels bindings.
 * Renders an empty scene cleared to a known colour into a readable headless
 * swapchain and reads it back — a binding/round-trip smoke check, not a golden.
 */
class RendererRenderingTest : RenderingTestFixture() {
    @Test
    fun testBeginEndFrameAndReadPixels() {
        val engine = engine ?: return
        val w = 16
        val h = 16

        val swapChain = engine.createSwapChain(w, h, SWAP_CHAIN_CONFIG_READABLE)
        val renderer = engine.createRenderer()
        val scene = engine.createScene()
        val camera = engine.createCamera()
        camera.setProjection(45.0, w.toDouble() / h, 0.1, 100.0, Camera.Fov.VERTICAL)
        camera.lookAt(0.0, 0.0, 3.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0)

        val view = engine.createView().apply {
            this.scene = scene
            this.camera = camera
            this.viewport = Viewport(0, 0, w, h)
        }
        // Opaque red clear so the readback is non-zero and proves the pipeline ran.
        renderer.clearOptions = Renderer.ClearOptions().apply {
            clearColor = doubleArrayOf(1.0, 0.0, 0.0, 1.0)
            clear = true
        }

        val pixels = ByteArray(w * h * 4)
        val pbd = Texture.PixelBufferDescriptor(pixels, pixels.size, Texture.Format.RGBA, Texture.Type.UBYTE)

        if (renderer.beginFrame(swapChain, 0L)) {
            renderer.render(view)
            renderer.readPixels(0, 0, w, h, pbd)
            renderer.endFrame()
        }
        engine.flushAndWait() // let the readback land

        assertTrue(pixels.any { it.toInt() != 0 }, "readPixels filled nothing — binding/pipeline broken")

        engine.destroyView(view)
        engine.destroyCamera(camera)
        engine.destroyScene(scene)
        engine.destroyRenderer(renderer)
        engine.destroySwapChain(swapChain)
    }

    companion object {
        private const val SWAP_CHAIN_CONFIG_READABLE = 0x2L
    }
}
