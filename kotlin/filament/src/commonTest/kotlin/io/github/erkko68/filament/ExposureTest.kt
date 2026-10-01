package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.math.log2
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals

class ExposureTest : FilamentTestFixture() {
    @Test
    fun testCameraMatchesItsParameters() {
        val entity = EntityManager.get().create()
        val camera = engine.createCamera(entity)
        camera.setExposure(16f, 1f / 125f, 100f)

        // EV100 = log2(N² / t * 100 / S)
        val ev100 = log2(16f * 16f * 125f)
        assertEquals(ev100, Exposure.ev100(camera), 1e-4f)
        assertEquals(ev100, Exposure.ev100(16f, 1f / 125f, 100f), 1e-4f)
        assertEquals(Exposure.exposure(ev100), Exposure.exposure(camera), 1e-9f)
        assertEquals(Exposure.exposure(ev100), Exposure.exposure(16f, 1f / 125f, 100f), 1e-9f)
        assertEquals(1f / (1.2f * 2f.pow(ev100)), Exposure.exposure(ev100), 1e-9f)
        assertEquals(Exposure.luminance(ev100), Exposure.luminance(camera), 1f)
        assertEquals(Exposure.luminance(ev100), Exposure.luminance(16f, 1f / 125f, 100f), 1f)
        assertEquals(Exposure.illuminance(ev100), Exposure.illuminance(camera), 10f)
        assertEquals(Exposure.illuminance(ev100), Exposure.illuminance(16f, 1f / 125f, 100f), 10f)

        engine.destroyCameraComponent(entity)
        EntityManager.get().destroy(entity)
    }

    @Test
    fun testLuminanceAndIlluminanceRoundTrip() {
        assertEquals(1000f, Exposure.luminance(Exposure.ev100FromLuminance(1000f)), 1e-1f)
        assertEquals(1000f, Exposure.illuminance(Exposure.ev100FromIlluminance(1000f)), 1e-1f)
    }
}
