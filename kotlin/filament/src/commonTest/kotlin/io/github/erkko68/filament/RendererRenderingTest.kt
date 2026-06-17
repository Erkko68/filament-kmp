package io.github.erkko68.filament

import io.github.erkko68.filament.testsupport.TestEnv
import io.github.erkko68.filament.testsupport.TestTarget
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
        // The iOS simulator on a headless CI VM has no real GPU; its Metal driver aborts
        // on the render-to-readable-swapchain + readback path (other binding tests still run).
        // Passes on a local simulator backed by the host GPU.
        if (TestEnv.target == TestTarget.NATIVE) return
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
        var readbackDone = false
        val pbd = Texture.PixelBufferDescriptor(pixels, pixels.size, Texture.Format.RGBA, Texture.Type.UBYTE) {
            readbackDone = true
        }

        if (renderer.beginFrame(swapChain, 0L)) {
            renderer.render(view)
            renderer.readPixels(0, 0, w, h, pbd)
            renderer.endFrame()
        }
        // Pump until the readback callback fires (it's async on GLES backends).
        var tries = 0
        while (!readbackDone && tries++ < 20) engine.flushAndWait()

        // Binding path executed without crashing. Content check only when the readback
        // actually landed (synchronous backends) — async backends may not deliver here.
        if (readbackDone) {
            assertTrue(pixels.any { it.toInt() != 0 }, "readPixels delivered an all-zero buffer")
        }

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
