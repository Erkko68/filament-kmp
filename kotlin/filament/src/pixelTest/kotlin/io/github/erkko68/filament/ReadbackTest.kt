package io.github.erkko68.filament

import io.github.erkko68.filament.interop.toInterop
import io.github.erkko68.filament.interop.InteropScope
import io.github.erkko68.filament.interop.readInts
import io.github.erkko68.filament.testsupport.IgnoreJs
import io.github.erkko68.filament.testutils.ReadbackFlag
import io.github.erkko68.filament.testutils.RealBackendFixture
import io.github.erkko68.filament.testutils.pumpUntil
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

/** The clear colour reaches both readPixels overloads. */
@IgnoreJs // a web readback only lands once the browser gets a frame, which a synchronous test never yields
class ReadbackTest : RealBackendFixture() {
    @Test
    fun clearColourReachesBothReadbackOverloads() {
        val w = 4
        val h = 4
        val swapChain = engine.createSwapChain(w, h, SwapChain.CONFIG_READABLE)
        val renderer = engine.createRenderer().apply {
            clearOptions = Renderer.ClearOptions().apply {
                clearColor = doubleArrayOf(1.0, 0.0, 0.0, 1.0)
                clear = true
            }
        }
        val scene = engine.createScene()
        val camera = engine.createCamera(engine.entityManager.create())
        val view = engine.createView().apply {
            this.scene = scene
            this.camera = camera
            this.viewport = Viewport(0, 0, w, h)
        }

        // The memory has to outlive the readback, so the scope is only released once it landed.
        val memory = InteropScope()
        val address = memory.toInterop(IntArray(w * h), w * h)
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

        engine.destroy(view)
        engine.destroyCameraComponent(camera.entity)
        engine.entityManager.destroy(camera.entity)
        engine.destroy(scene)
        engine.destroy(renderer)
        engine.destroy(swapChain)

        for (px in bytes.indices step 4) {
            val (r, g, b) = (0..2).map { bytes[px + it].toInt() and 0xFF }
            assertTrue(r > 200 && g < 50 && b < 50, "pixel ${px / 4} is not the red clear colour: ($r, $g, $b)")
        }
        assertContentEquals(bytes, ByteArray(bytes.size) { (ints[it / 4] ushr (8 * (it % 4))).toByte() })
    }
}
