package io.github.erkko68.filament

import io.github.erkko68.filament.testsupport.TestEnv
import io.github.erkko68.filament.testsupport.TestTarget
import io.github.erkko68.filament.testutils.FilamentTestFixture
import io.github.erkko68.filament.testutils.pumpUntil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TextureTest : FilamentTestFixture() {
    @Test
    fun testTextureUsageFlags() {
        val flags = with(Texture.Usage) {
            listOf(COLOR_ATTACHMENT, DEPTH_ATTACHMENT, STENCIL_ATTACHMENT, UPLOADABLE, SAMPLEABLE, SUBPASS_INPUT, BLIT_SRC, BLIT_DST, PROTECTED, GEN_MIPMAPPABLE)
        }
        assertEquals(flags.size, flags.distinct().size)
        flags.forEach { assertEquals(1, it.countOneBits()) }
        assertEquals(Texture.Usage.UPLOADABLE or Texture.Usage.SAMPLEABLE, Texture.Usage.DEFAULT)
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
            .build(engine)
        assertTrue(engine.isValid(tex))

        assertEquals(64, tex.getWidth(0))
        assertEquals(64, tex.getHeight(0))
        assertEquals(1, tex.getDepth(0))
        assertEquals(1, tex.levels)
        assertEquals(Texture.Sampler.SAMPLER_2D, tex.target)
        assertEquals(Texture.InternalFormat.RGBA8, tex.format)

        engine.destroy(tex)
    }

    @Test
    fun testTextureCompanionMethods() {
        assertTrue(Texture.isTextureFormatSupported(engine, Texture.InternalFormat.RGBA8))
        assertTrue(Texture.validatePixelFormatAndType(Texture.InternalFormat.RGBA8, Texture.Format.RGBA, Texture.Type.UBYTE))
        assertTrue(Texture.getMaxTextureSize(engine, Texture.Sampler.SAMPLER_2D) > 0)
        assertTrue(Texture.getMaxArrayTextureLayers(engine) >= 0)
        assertTrue(Texture.isTextureFormatCompressed(Texture.InternalFormat.ETC2_RGB8))
        assertFalse(Texture.isTextureFormatCompressed(Texture.InternalFormat.RGBA8))
        assertEquals(100 * 100 * 4, Texture.computeTextureDataSize(Texture.Format.RGBA, Texture.Type.UBYTE, 100, 100, 1))
    }

    @Test
    fun testBuilderOptions() {
        val named = Texture.Builder().width(4).height(4).format(Texture.InternalFormat.RGBA8).name("named").apply {
            // Filament's wasm build rejects swizzling whatever the backend reports.
            if (Texture.isTextureSwizzleSupported(engine) && TestEnv.target != TestTarget.JS) {
                swizzle(Texture.Swizzle.CHANNEL_2, Texture.Swizzle.CHANNEL_1, Texture.Swizzle.CHANNEL_0, Texture.Swizzle.SUBSTITUTE_ONE)
            }
        }.build(engine)
        val imported = Texture.Builder().width(4).height(4).format(Texture.InternalFormat.RGBA8).import(1L).build(engine)
        val external = Texture.Builder().width(4).height(4).format(Texture.InternalFormat.RGBA8).external().build(engine)
        assertTrue(engine.isValid(named) && engine.isValid(imported) && engine.isValid(external))
        engine.destroy(named)
        engine.destroy(imported)
        engine.destroy(external)
    }

    @Test
    fun testTextureSetImageAndMipmaps() {
        val tex = Texture.Builder()
            .width(64)
            .height(64)
            .depth(1)
            .samples(1)
            .sampler(Texture.Sampler.SAMPLER_2D)
            .levels(4)
            .format(Texture.InternalFormat.RGBA8)
            .usage(Texture.Usage.DEFAULT or Texture.Usage.GEN_MIPMAPPABLE)
            .build(engine)
        assertTrue(engine.isValid(tex))

        var uploaded = 0
        val full = Texture.PixelBufferDescriptor(ByteArray(64 * 64 * 4), 64 * 64 * 4, Texture.Format.RGBA, Texture.Type.UBYTE) { uploaded++ }
        tex.setImage(engine, 0, full)

        // Sub-region uploads need an explicit row stride (= region width); Android's
        // SDK reads per-row and overflows a stride-0 buffer.
        val region = Texture.PixelBufferDescriptor(ByteArray(32 * 32 * 4), 32 * 32 * 4, Texture.Format.RGBA, Texture.Type.UBYTE, 1, 0, 0, 32) { uploaded++ }
        tex.setImage(engine, 0, 0, 0, 32, 32, region)

        val region3d = Texture.PixelBufferDescriptor(ByteArray(32 * 32 * 4), 32 * 32 * 4, Texture.Format.RGBA, Texture.Type.UBYTE, 1, 0, 0, 32) { uploaded++ }
        tex.setImage(engine, 0, 0, 0, 0, 32, 32, 1, region3d)

        tex.generateMipmaps(engine)
        engine.pumpUntil { uploaded == 3 }
        engine.destroy(tex)
    }
}
