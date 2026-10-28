package io.github.erkko68.filament

import io.github.erkko68.filament.testsupport.RealBackendFixture
import java.awt.Canvas
import java.awt.Frame
import java.awt.GraphicsEnvironment
import org.junit.Assume
import kotlin.test.Test
import kotlin.test.assertTrue

/** The one test that hands Filament a live desktop window: an AWT canvas, cleared to red and read back. */
class AwtSurfaceTest : RealBackendFixture() {
    // Metal drops a frame it has no drawable for, as while the window comes up, instead of panicking.
    override fun createEngine(): Engine? =
        Engine.create(config = Engine.Config().apply { metalDisablePanicOnDrawableFailure = true })

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
            fun isRed(c: List<Int>) = c[0] > 200 && c[1] < 50 && c[2] < 50
            // Frames are skipped or dropped while the window comes up: render until one reads back.
            var rgb: List<Int>? = null
            for (attempt in 0 until 50) {
                if (renderer.beginFrame(swapChain, 0L)) {
                    val pixels = ByteArray(4)
                    var read = false
                    val pbd = Texture.PixelBufferDescriptor(pixels, pixels.size, Texture.Format.RGBA, Texture.Type.UBYTE) { read = true }
                    renderer.render(view)
                    renderer.readPixels(width / 2, height / 2, 1, 1, pbd)
                    renderer.endFrame()
                    var tries = 0
                    while (!read && tries++ < 20) engine.flushAndWait()
                    if (read) rgb = pixels.take(3).map { it.toInt() and 0xFF }
                    if (rgb != null && isRed(rgb)) break
                }
                Thread.sleep(20)
            }
            assertTrue(rgb != null, "no frame was read back from the window's swap chain")
            assertTrue(isRed(rgb), "expected the red clear colour, got $rgb")
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
