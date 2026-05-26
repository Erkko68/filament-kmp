package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IndirectLightTest : FilamentTestFixture() {
    @Test
    fun testIndirectLightLifecycle() {
        // Spherical harmonics for 3 bands: 9 coefficients, 3 floats per coefficient = 27 floats
        val sh = FloatArray(27)
        sh[0] = 1f // Ambient band red
        sh[1] = 1f // Ambient band green
        sh[2] = 1f // Ambient band blue

        val light = IndirectLight.Builder()
            .irradiance(3, sh)
            .intensity(20000f)
            .rotation(floatArrayOf(
                1f, 0f, 0f,
                0f, 1f, 0f,
                0f, 0f, 1f
            ))
            .build(engine)

        assertNotNull(light)
        assertTrue(engine.isValidIndirectLight(light))

        assertEquals(20000f, light.intensity)
        light.intensity = 15000f
        assertEquals(15000f, light.intensity)

        val rot = light.rotation
        assertEquals(1f, rot[0])
        assertEquals(1f, rot[4])

        light.rotation = floatArrayOf(
            0f, 1f, 0f,
            -1f, 0f, 0f,
            0f, 0f, 1f
        )
        assertEquals(9, light.rotation.size)
        assertEquals(1f, light.rotation[8])

        assertNull(light.reflectionsTexture)
        assertNull(light.irradianceTexture)

        engine.destroyIndirectLight(light)
    }

    @Test
    fun testIndirectLightCompanionEstimates() {
        val sh = FloatArray(27)
        sh[0] = 1f

        val dir = IndirectLight.getDirectionEstimate(sh)
        assertEquals(3, dir.size)

        val col = IndirectLight.getColorEstimate(sh, 0.0, 1.0, 0.0)
        assertEquals(4, col.size)
    }

    @Test
    fun testIndirectLightWithTextures() {
        // Use a slightly larger cubemap with mip-mappable usage: upstream IBL's
        // builder calls generateMipmaps when IBL_INTEGRATION_IMPORTANCE_SAMPLING
        // is on (Filament's JS build), which requires both > 1 mip level *and*
        // the GEN_MIPMAPPABLE | COLOR_ATTACHMENT usage flags.
        val cubemap = Texture.Builder()
            .width(4)
            .height(4)
            .levels(3)
            .sampler(Texture.Sampler.SAMPLER_CUBEMAP)
            .usage(Texture.Usage.SAMPLEABLE or Texture.Usage.COLOR_ATTACHMENT)
            .format(Texture.InternalFormat.RGBA8)
            .build(engine)

        val light = IndirectLight.Builder()
            .reflections(cubemap)
            .irradiance(cubemap)
            .build(engine)

        assertNotNull(light)
        assertNotNull(light.reflectionsTexture)

        engine.destroyIndirectLight(light)
        engine.destroyTexture(cubemap)
    }
}
