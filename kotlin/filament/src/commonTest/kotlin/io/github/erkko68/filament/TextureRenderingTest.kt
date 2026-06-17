package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.RenderingTestFixture
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Real-backend coverage for [Texture] upload/mipmap bindings. See [RenderingTestFixture]. */
class TextureRenderingTest : RenderingTestFixture() {
    @Test
    fun testTextureSetImageAndMipmaps() {
        val engine = engine ?: return

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
        assertNotNull(tex)
        assertTrue(engine.isValidTexture(tex))

        val full = Texture.PixelBufferDescriptor(ByteArray(64 * 64 * 4), 64 * 64 * 4, Texture.Format.RGBA, Texture.Type.UBYTE)
        tex.setImage(engine, 0, full)

        val region = Texture.PixelBufferDescriptor(ByteArray(32 * 32 * 4), 32 * 32 * 4, Texture.Format.RGBA, Texture.Type.UBYTE)
        tex.setImage(engine, 0, 0, 0, 32, 32, region)

        val region3d = Texture.PixelBufferDescriptor(ByteArray(32 * 32 * 4), 32 * 32 * 4, Texture.Format.RGBA, Texture.Type.UBYTE)
        tex.setImage(engine, 0, 0, 0, 0, 32, 32, 1, region3d)

        tex.generateMipmaps(engine)

        engine.flushAndWait()
        engine.destroyTexture(tex)
    }
}
