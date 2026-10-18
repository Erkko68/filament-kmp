package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class IndirectLightTest : FilamentTestFixture() {
    // 3 bands, rgb per coefficient. Coefficient 1 is the y term: light arriving from above.
    private val fromAbove = FloatArray(27).also { sh ->
        for (c in 0 until 3) { sh[c] = 1f; sh[3 + c] = 1f }
    }

    @Test
    fun irradianceAndRotationRoundTrip() {
        val rotation = floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
        val light = IndirectLight.Builder().irradiance(3, fromAbove).intensity(20000f).rotation(rotation).build(engine)
        assertEquals(20000f, light.intensity)
        light.intensity = 15000f
        assertEquals(15000f, light.intensity)
        assertContentEquals(rotation, light.rotation)
        val quarterTurn = floatArrayOf(0f, 1f, 0f, -1f, 0f, 0f, 0f, 0f, 1f)
        light.rotation = quarterTurn
        assertContentEquals(quarterTurn, light.rotation)
        assertNull(light.reflectionsTexture)
        assertNull(light.irradianceTexture)
        engine.destroy(light)
    }

    @Test
    fun theDominantLightComesFromTheBrightestDirection() {
        val down = IndirectLight.getDirectionEstimate(fromAbove)
        listOf(0f, -1f, 0f).forEachIndexed { i, v -> assertEquals(v, down[i], 1e-4f, "direction ${down.toList()}") }
        assertTrue(IndirectLight.getColorEstimate(fromAbove, down[0], down[1], down[2])[3] > 0f)

        val light = IndirectLight.Builder().irradiance(3, fromAbove).build(engine)
        assertContentEquals(down, light.getDirectionEstimate())
        engine.destroy(light)
    }

    @Test
    fun cubemapsAreHeldByTheLight() {
        // Mip-mappable: Filament's wasm build generates mipmaps for importance-sampled IBLs.
        val cubemap = Texture.Builder()
            .width(4)
            .height(4)
            .levels(3)
            .sampler(Texture.Sampler.SAMPLER_CUBEMAP)
            .usage(Texture.Usage.SAMPLEABLE or Texture.Usage.COLOR_ATTACHMENT)
            .format(Texture.InternalFormat.RGBA8)
            .build(engine)
        val light = IndirectLight.Builder().reflections(cubemap).irradiance(cubemap).build(engine)
        // Each getter call wraps the native texture anew; destroying a wrapper clears its handle.
        val expected = cubemap.nativeObject to light.nativeObject
        val reflections = light.reflectionsTexture?.nativeObject
        val irradiance = light.irradianceTexture?.nativeObject
        val scene = engine.createScene()
        scene.indirectLight = light
        val attached = scene.indirectLight?.nativeObject
        engine.destroy(scene)
        engine.destroy(light)
        engine.destroy(cubemap)
        assertEquals(expected.first, reflections)
        assertEquals(expected.first, irradiance)
        assertEquals(expected.second, attached)
    }
}
