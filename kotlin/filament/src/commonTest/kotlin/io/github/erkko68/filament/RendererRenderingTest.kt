package io.github.erkko68.filament

import io.github.erkko68.filament.interop.InteropScope
import io.github.erkko68.filament.interop.readInts
import io.github.erkko68.filament.testsupport.IgnoreJs
import io.github.erkko68.filament.testsupport.TestEnv
import io.github.erkko68.filament.testsupport.TestTarget
import io.github.erkko68.filament.testutils.ReadbackFlag
import io.github.erkko68.filament.testutils.RenderingTestFixture
import io.github.erkko68.filament.testutils.pumpUntil
import kotlin.test.Test
import kotlin.test.assertContentEquals
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
        val camera = engine.createCamera(engine.entityManager.create())
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
        val readbackDone = io.github.erkko68.filament.testutils.ReadbackFlag()
        val pbd = Texture.PixelBufferDescriptor(pixels, pixels.size, Texture.Format.RGBA, Texture.Type.UBYTE) {
            readbackDone.done = true
        }

        if (renderer.beginFrame(swapChain, 0L)) {
            renderer.render(view)
            renderer.readPixels(0, 0, w, h, pbd)
            renderer.endFrame()
        }
        // Pump until the readback callback fires (it's async on GLES backends).
        var tries = 0
        while (!readbackDone.done && tries++ < 20) engine.flushAndWait()

        // On web the readback lands only after the browser runs more frames, which this
        // synchronous loop never yields to; everywhere else it must actually land.
        if (TestEnv.target != TestTarget.JS) {
            assertTrue(readbackDone.done, "readPixels callback never fired")
            assertTrue(pixels.any { it.toInt() != 0 }, "readPixels delivered an all-zero buffer")
        }

        engine.destroy(view)
        engine.destroyCameraComponent(camera.entity)
        engine.entityManager.destroy(camera.entity)
        engine.destroy(scene)
        engine.destroy(renderer)
        engine.destroy(swapChain)
    }

    @Test
    fun testStandaloneViewReadbackAndCopyFrame() {
        val engine = engine ?: return
        val w = 8
        val h = 8
        val color = Texture.Builder().width(w).height(h).format(Texture.InternalFormat.RGBA8)
            .usage(Texture.Usage.COLOR_ATTACHMENT or Texture.Usage.BLIT_SRC).build(engine)
        val target = RenderTarget.Builder().texture(RenderTarget.AttachmentPoint.COLOR, color).build(engine)
        val renderer = engine.createRenderer()
        val scene = engine.createScene()
        val camera = engine.createCamera(engine.entityManager.create())
        val view = engine.createView().apply {
            this.scene = scene
            this.camera = camera
            this.viewport = Viewport(0, 0, w, h)
            this.renderTarget = target
        }

        // Off-screen: outside beginFrame/endFrame, then read the target back.
        renderer.renderStandaloneView(view)
        val pixels = ByteArray(w * h * 4)
        val readbackDone = io.github.erkko68.filament.testutils.ReadbackFlag()
        renderer.readPixels(target, 0, 0, w, h, Texture.PixelBufferDescriptor(pixels, pixels.size, Texture.Format.RGBA, Texture.Type.UBYTE) {
            readbackDone.done = true
        })
        var tries = 0
        while (!readbackDone.done && tries++ < 20) engine.flushAndWait()
        if (TestEnv.target != TestTarget.JS) assertTrue(readbackDone.done, "render target readPixels callback never fired")

        // On-screen, mirrored into a second swap chain.
        view.renderTarget = null
        val swapChain = engine.createSwapChain(w, h)
        val mirror = engine.createSwapChain(w, h)
        if (renderer.beginFrame(swapChain, 0L)) {
            renderer.render(view)
            renderer.copyFrame(mirror, Viewport(0, 0, w, h), Viewport(0, 0, w, h), Renderer.COMMIT)
            renderer.endFrame()
        }
        engine.flushAndWait()

        engine.destroy(view)
        engine.destroyCameraComponent(camera.entity)
        engine.entityManager.destroy(camera.entity)
        engine.destroy(scene)
        engine.destroy(renderer)
        engine.destroy(mirror)
        engine.destroy(swapChain)
        engine.destroy(target)
        engine.destroy(color)
    }

    // The zero-copy overload must deliver the same frame as the ByteArray one.
    @IgnoreJs // a web readback only lands once the browser gets a frame, which a synchronous test never yields
    @Test
    fun testReadPixelsIntoNativeMemory() {
        val engine = engine ?: return
        val w = 4
        val h = 4
        val swapChain = engine.createSwapChain(w, h, SWAP_CHAIN_CONFIG_READABLE)
        val renderer = engine.createRenderer()
        val scene = engine.createScene()
        val camera = engine.createCamera(engine.entityManager.create())
        val view = engine.createView().apply {
            this.scene = scene
            this.camera = camera
            this.viewport = Viewport(0, 0, w, h)
        }
        renderer.clearOptions = Renderer.ClearOptions().apply {
            clearColor = doubleArrayOf(1.0, 0.0, 0.0, 1.0)
            clear = true
        }

        // The memory has to outlive the readback, so the scope is only released once it landed.
        val memory = InteropScope()
        val address = memory.toInterop(IntArray(w * h))
        val nativeDone = ReadbackFlag()
        val bytes = ByteArray(w * h * 4)
        val bytesDone = ReadbackFlag()
        assertTrue(renderer.beginFrame(swapChain, 0L))
        renderer.render(view)
        renderer.readPixels(0, 0, w, h, address, bytes.size, Texture.Format.RGBA, Texture.Type.UBYTE, 0) { nativeDone.done = true }
        renderer.readPixels(0, 0, w, h, Texture.PixelBufferDescriptor(bytes, bytes.size, Texture.Format.RGBA, Texture.Type.UBYTE) { bytesDone.done = true })
        renderer.endFrame()
        engine.pumpUntil { nativeDone.done && bytesDone.done }

        val ints = readInts(address, w * h)
        memory.release()
        assertTrue(bytes.any { it.toInt() != 0 }, "readPixels delivered an all-zero buffer")
        assertContentEquals(bytes, ByteArray(bytes.size) { (ints[it / 4] ushr (8 * (it % 4))).toByte() })

        engine.destroy(view)
        engine.destroyCameraComponent(camera.entity)
        engine.entityManager.destroy(camera.entity)
        engine.destroy(scene)
        engine.destroy(renderer)
        engine.destroy(swapChain)
    }

    companion object {
        private const val SWAP_CHAIN_CONFIG_READABLE = 0x2L
    }
}
