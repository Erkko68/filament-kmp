package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TextureTest : FilamentTestFixture() {
    @Test
    fun testTextureUsageFlags() {
        val c = Texture.Usage.COLOR_ATTACHMENT
        val d = Texture.Usage.DEPTH_ATTACHMENT
        val s = Texture.Usage.STENCIL_ATTACHMENT
        val u = Texture.Usage.UPLOADABLE
        val sa = Texture.Usage.SAMPLEABLE
        val sub = Texture.Usage.SUBPASS_INPUT
        val src = Texture.Usage.BLIT_SRC
        val dst = Texture.Usage.BLIT_DST
        val pr = Texture.Usage.PROTECTED
        val gen = Texture.Usage.GEN_MIPMAPPABLE
        val def = Texture.Usage.DEFAULT

        assertTrue(c != 0)
    }

    @Test
    fun testPixelBufferDescriptor() {
        val pbd = Texture.PixelBufferDescriptor(byteArrayOf(0, 1, 2, 3), 4, Texture.Format.RGBA, Texture.Type.UBYTE, 1, 0, 0, 0) {
            // Callback
        }
        assertEquals(4, pbd.sizeInBytes)
        assertEquals(Texture.Format.RGBA, pbd.format)
        assertEquals(Texture.Type.UBYTE, pbd.type)
        assertEquals(1, pbd.alignment)
        assertEquals(0, pbd.left)
        assertEquals(0, pbd.top)
        assertEquals(0, pbd.stride)
        assertNotNull(pbd.callback)
    }

    @Test
    fun testTextureLifecycle() {
        val tex = Texture.Builder()
            .width(64)
            .height(64)
            .depth(1)
            .samples(1)
            .sampler(Texture.Sampler.SAMPLER_2D)
            .levels(1)
            .format(Texture.InternalFormat.RGBA8)
            .usage(Texture.Usage.SAMPLEABLE)
            //.swizzle(Texture.Swizzle.CHANNEL_0, Texture.Swizzle.CHANNEL_1, Texture.Swizzle.CHANNEL_2, Texture.Swizzle.CHANNEL_3)
            .build(engine)

        assertNotNull(tex)
        assertTrue(engine.isValidTexture(tex))

        assertEquals(64, tex.getWidth(0))
        assertEquals(64, tex.getHeight(0))
        assertEquals(1, tex.getDepth(0))
        assertEquals(1, tex.getLevels())
        assertEquals(Texture.Sampler.SAMPLER_2D, tex.getTarget())
        assertEquals(Texture.InternalFormat.RGBA8, tex.getFormat())

        val pbd = Texture.PixelBufferDescriptor(ByteArray(64 * 64 * 4), 64 * 64 * 4, Texture.Format.RGBA, Texture.Type.UBYTE)
        
        // setImage overloads
        // TODO: The following setImage/generateMipmaps calls cause driver-specific precondition panics under the NOOP backend driver
        // tex.setImage(engine, 0, pbd)
        // tex.setImage(engine, 0, 0, 0, 32, 32, pbd)
        // tex.setImage(engine, 0, 0, 0, 0, 32, 32, 1, pbd)

        // tex.generateMipmaps(engine)

        engine.destroyTexture(tex)
    }

    @Test
    fun testTextureCompanionMethods() {
        assertTrue(Texture.isTextureFormatSupported(engine, Texture.InternalFormat.RGBA8))
        Texture.isTextureFormatMipmappable(engine, Texture.InternalFormat.RGBA8)
        Texture.isTextureSwizzleSupported(engine)
        assertTrue(Texture.validatePixelFormatAndType(Texture.InternalFormat.RGBA8, Texture.Format.RGBA, Texture.Type.UBYTE))
        assertTrue(Texture.getMaxTextureSize(engine, Texture.Sampler.SAMPLER_2D) > 0)
        assertTrue(Texture.getMaxArrayTextureLayers(engine) >= 0)
        assertTrue(Texture.computeDataSize(Texture.Format.RGBA, Texture.Type.UBYTE, 100, 100, 1) > 0)
    }

    private fun compileOnlyVerifications(tex: Texture, engine: Engine, stream: Stream, pbd: Texture.PixelBufferDescriptor) {
        tex.setImage(engine, 0, 0, 0, 0, 32, 32, 1, pbd)
        tex.generateMipmaps(engine)
        tex.setExternalStream(engine, stream)
        
        Texture.Builder()
            .importTexture(12345L)
            .external()
            .swizzle(Texture.Swizzle.CHANNEL_0, Texture.Swizzle.CHANNEL_1, Texture.Swizzle.CHANNEL_2, Texture.Swizzle.CHANNEL_3)
    }
}
