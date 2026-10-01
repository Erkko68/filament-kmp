package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.utils.testutils.UtilsRenderingTestFixture
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Real-backend coverage for the IBLPrefilter filters, driven by a synthetic
 * in-memory equirectangular texture (no external HDR asset needed).
 */
class IBLPrefilterRenderingTest : UtilsRenderingTestFixture() {
    @Test
    fun testFiltersInvoke() {
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
        val cubemap: Texture
        val filtered: Texture
        val irradianceOut: Texture
        IBLPrefilterContext.EquirectangularToCubemap(context).use { toCubemap ->
            // Few samples: default 1024 overruns Mocha's 30s timeout under CI's SwiftShader.
            IBLPrefilterContext.SpecularFilter(context, IBLPrefilterContext.SpecularFilter.Config(sampleCount = 16)).use { specular ->
                IBLPrefilterContext.IrradianceFilter(context, IBLPrefilterContext.IrradianceFilter.Config(sampleCount = 16)).use { irradiance ->
                    cubemap = toCubemap(equirect)
                    assertTrue(engine.isValid(cubemap))

                    filtered = specular(IBLPrefilterContext.SpecularFilter.Options(lodOffset = 2f), cubemap)
                    assertTrue(engine.isValid(filtered))
                    engine.destroy(specular(cubemap))
                    // Given an output texture, the filters write into it and hand it back.
                    irradianceOut = irradiance(cubemap)
                    assertSame(irradianceOut, irradiance(cubemap, irradianceOut))
                    val options = IBLPrefilterContext.IrradianceFilter.Options(lodOffset = 1f, generateMipmap = false)
                    assertSame(irradianceOut, irradiance(options, cubemap, irradianceOut))
                    engine.flushAndWait()
                }
            }
        }
        context.destroy()
        engine.destroy(irradianceOut)
        engine.destroy(filtered)
        engine.destroy(cubemap)
        engine.destroy(equirect)
    }
}
