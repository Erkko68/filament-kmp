package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.utils.testutils.UtilsRenderingTestFixture
import io.github.erkko68.filament.utils.testutils.ktx1
import kotlin.io.encoding.Base64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Ktx1Reader, Ktx2Reader and TextureLoader create GPU textures, so they need a real backend. */
class TextureReadersRenderingTest : UtilsRenderingTestFixture() {

    @Test
    fun ktx1ReaderCreatesTextures() {
        val engine = engine ?: return
        Ktx1Bundle(numMipLevels = 1, arrayLength = 1, isCubemap = false).use { empty ->
            assertEquals(1, empty.numMipLevels)
            assertFalse(empty.isCubemap)
        }

        val texture = assertNotNull(Ktx1Reader.createTexture(engine, Ktx1Bundle(ktx1()), srgb = false))
        assertEquals(1, texture.getWidth(0))
        assertEquals(Texture.InternalFormat.RGBA8, texture.format)

        var uploaded = false
        val withCallback = assertNotNull(Ktx1Reader.createTexture(engine, Ktx1Bundle(ktx1()), srgb = false) { uploaded = true })
        // Callbacks arrive as the backend advances, which on web means rendering frames.
        val swapChain = engine.createSwapChain(1, 1)
        val renderer = engine.createRenderer()
        repeat(50) {
            if (uploaded) return@repeat
            if (renderer.beginFrame(swapChain, 0L)) renderer.endFrame()
            engine.flushAndWait()
            engine.pumpMessageQueues()
        }
        engine.destroy(renderer)
        engine.destroy(swapChain)
        assertTrue(uploaded, "the callback runs once the upload is done")

        engine.destroy(texture)
        engine.destroy(withCallback)
    }

    @Test
    fun ktx2ReaderLoadsSynchronouslyAndAsynchronously() {
        val engine = engine ?: return
        Ktx2Reader(engine, quiet = true).use { reader ->
            reader.requestFormat(Texture.InternalFormat.SRGB8_A8) // basisu tags color input as sRGB
            val texture = assertNotNull(reader.load(RED_4X4_UASTC, Ktx2Reader.TransferFunction.sRGB))
            assertEquals(4, texture.getWidth(0))
            engine.destroy(texture)

            val async = assertNotNull(reader.asyncCreate(RED_4X4_UASTC, Ktx2Reader.TransferFunction.sRGB))
            val asyncTexture = async.texture
            assertEquals(Ktx2Reader.Result.SUCCESS, async.doTranscoding())
            async.uploadImages()
            reader.asyncDestroy(async)
            assertEquals(4, asyncTexture.getWidth(0))
            engine.flushAndWait()
            engine.destroy(asyncTexture)
        }
    }

    @Test
    fun textureLoaderDecodesAPng() {
        val engine = engine ?: return
        val texture = assertNotNull(TextureLoader.loadTexture(engine, ONE_PIXEL_PNG, TextureLoader.TextureType.COLOR))
        assertEquals(1, texture.getWidth(0))
        engine.flushAndWait()
        engine.destroy(texture)
    }

    private companion object {
        /** 4×4 red, UASTC, sRGB-tagged; from Filament 1.77.1's `basisu -ktx2 -uastc`. */
        val RED_4X4_UASTC = Base64.decode(
            "q0tUWCAyMLsNChoKAAAAAAEAAAAEAAAABAAAAAAAAAAAAAAAAQAAAAEAAAACAAAAaAAAACwAAACUAAAAJAAAAAAAAAAAAAAAAAAAAAAAAAC4" +
                "AAAAAAAAABYAAAAAAAAAEAAAAAAAAAAsAAAAAAAAAAIAKACmAQIAAwMAABAAAAAAAAAAAAB/AAAAAAAAAAAA/////x8AAABLVFh3cml0ZXIA" +
                "QmFzaXMgVW5pdmVyc2FsIDIuMTAAACi1L/0gEG0AADj3HwDgH3oAAQAFMAI=",
        )

        /** A 1×1 opaque red RGBA PNG. */
        val ONE_PIXEL_PNG = byteArrayOf(
            -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 1, 0, 0, 0, 1, 8, 6, 0, 0, 0, 31, 21,
            -60, -119, 0, 0, 0, 13, 73, 68, 65, 84, 120, -100, 99, -8, -49, -64, -16, 31, 0, 5, 0, 1, -1, -119, -103, 61,
            29, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126,
        )
    }
}
