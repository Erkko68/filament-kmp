package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SkyboxTest : FilamentTestFixture() {
    @Test
    fun testSkyboxLifecycle() {
        val sky = Skybox.Builder()
            .color(0.1f, 0.2f, 0.3f, 1.0f)
            .showSun(true)
            .intensity(1000f)
            .priority(2)
            .build(engine)
        
        assertNotNull(sky)
        assertTrue(engine.isValidSkybox(sky))

        // Getters / Setters
        assertEquals(1000f, sky.intensity)
        assertNull(sky.texture)

        sky.setColor(0.5f, 0.5f, 0.5f, 1.0f)
        
        sky.setLayerMask(0xFF, 0x01)
        assertEquals(0x01, sky.layerMask)

        engine.destroySkybox(sky)
    }

    @Test
    fun testSkyboxWithEnvironment() {
        val cubemap = Texture.Builder()
            .width(4)
            .height(4)
            .levels(1)
            .sampler(Texture.Sampler.SAMPLER_CUBEMAP)
            .format(Texture.InternalFormat.RGBA8)
            .usage(Texture.Usage.SAMPLEABLE or Texture.Usage.COLOR_ATTACHMENT)
            .build(engine)

        val sky = Skybox.Builder()
            .environment(cubemap)
            .build(engine)

        assertNotNull(sky)
        assertNotNull(sky.texture)

        engine.destroySkybox(sky)
        engine.destroyTexture(cubemap)
    }
}
