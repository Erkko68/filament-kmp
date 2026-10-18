package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.RealBackendFixture
import java.awt.Canvas
import java.awt.Frame
import java.awt.GraphicsEnvironment
import org.junit.Assume
import kotlin.test.Test
import kotlin.test.assertTrue

/** The one test that hands Filament a live desktop window: an AWT canvas, cleared to red and read back. */
class AwtSurfaceTest : RealBackendFixture() {
    @Test
    fun rendersIntoAnAwtCanvas() {
        Assume.assumeFalse("no display", GraphicsEnvironment.isHeadless())

        val canvas = Canvas()
        val frame = Frame().apply {
            add(canvas)
            setSize(320, 240)
            isVisible = true
        }
        val scale = canvas.graphicsConfiguration.defaultTransform.scaleX
        val width = (canvas.width * scale).toInt()
        val height = (canvas.height * scale).toInt()

        val swapChain = engine.createSwapChain(NativeSurface(canvas), SwapChain.CONFIG_READABLE)
        val renderer = engine.createRenderer().apply {
            clearOptions = Renderer.ClearOptions().apply {
                clearColor = doubleArrayOf(1.0, 0.0, 0.0, 1.0)
                clear = true
            }
        }
        val scene = engine.createScene()
        val cameraEntity = engine.entityManager.create()
        val camera = engine.createCamera(cameraEntity)
        val view = engine.createView().apply {
            this.scene = scene
            this.camera = camera
            viewport = Viewport(0, 0, width, height)
        }
        try {
            val pixels = ByteArray(4)
            var read = false
            val pbd = Texture.PixelBufferDescriptor(pixels, pixels.size, Texture.Format.RGBA, Texture.Type.UBYTE) { read = true }
            // The first frames can be skipped while the window comes up.
            var rendered = false
            for (attempt in 0 until 50) {
                if (renderer.beginFrame(swapChain, 0L)) {
                    renderer.render(view)
                    renderer.readPixels(width / 2, height / 2, 1, 1, pbd)
                    renderer.endFrame()
                    rendered = true
                    break
                }
                Thread.sleep(20)
            }
            assertTrue(rendered, "no frame was accepted by the window's swap chain")
            var tries = 0
            while (!read && tries++ < 20) engine.flushAndWait()
            assertTrue(read, "the frame was never read back")
            val (r, g, b) = pixels.map { it.toInt() and 0xFF }
            assertTrue(r > 200 && g < 50 && b < 50, "expected the red clear colour, got ($r, $g, $b)")
        } finally {
            engine.destroy(swapChain)
            engine.flushAndWait()
            engine.destroy(renderer)
            engine.destroy(view)
            engine.destroy(scene)
            engine.destroyCameraComponent(cameraEntity)
            engine.entityManager.destroy(cameraEntity)
            frame.dispose()
        }
    }
}
