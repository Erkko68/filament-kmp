package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.utils.testutils.UtilsRenderingTestFixture
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Real-backend coverage for IBLPrefilter run() bindings, driven by a synthetic
 * in-memory equirectangular texture (no external HDR asset needed).
 */
@IgnoreJs // IBLPrefilter run() is not wired in the web wrapper.
class IBLPrefilterRenderingTest : UtilsRenderingTestFixture() {
    @Test
    fun testEquirectangularToCubemapAndSpecularFilterRun() {
        val engine = engine ?: return

        // 16x8 (2:1) RGBA16F equirectangular source, filled with mid-grey half-floats.
        // EquirectangularToCubemap requires width == 2*height, SAMPLEABLE, and ALL
        // mip levels allocated (16x8 -> 5 levels).
        val w = 16
        val h = 8
        val equirect = Texture.Builder()
            .width(w).height(h).levels(5)
            .sampler(Texture.Sampler.SAMPLER_2D)
            .format(Texture.InternalFormat.RGBA16F)
            .usage(Texture.Usage.DEFAULT or Texture.Usage.GEN_MIPMAPPABLE)
            .build(engine)

        val half = 0x3800.toShort() // 0.5 in IEEE half-float
        val bytes = ByteArray(w * h * 4 * 2)
        var i = 0
        while (i < bytes.size) {
            bytes[i] = (half.toInt() and 0xFF).toByte()
            bytes[i + 1] = ((half.toInt() shr 8) and 0xFF).toByte()
            i += 2
        }
        equirect.setImage(engine, 0, Texture.PixelBufferDescriptor(bytes, bytes.size, Texture.Format.RGBA, Texture.Type.HALF))
        equirect.generateMipmaps(engine)
        engine.flushAndWait()

        val context = IBLPrefilterContext(engine)
        val cubemap = EquirectangularToCubemap(context).run(equirect)
        assertNotNull(cubemap)
        assertTrue(engine.isValidTexture(cubemap))

        val filtered = SpecularFilter(context).run(cubemap)
        assertNotNull(filtered)

        engine.flushAndWait()
        context.destroy()
        engine.destroyTexture(filtered)
        engine.destroyTexture(cubemap)
        engine.destroyTexture(equirect)
    }
}
