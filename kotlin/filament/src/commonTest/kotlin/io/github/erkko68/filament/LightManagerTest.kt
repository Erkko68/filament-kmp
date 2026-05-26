package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LightManagerTest : FilamentTestFixture() {
    @Test
    fun testShadowOptionsAndCascades() {
        val options = LightManager.ShadowOptions().apply {
            mapSize = 1024
            shadowCascades = 2
            cascadeSplitPositions = floatArrayOf(0.1f)
            constantBias = 0.05f
            normalBias = 0.4f
            shadowFar = 100f
            shadowNearHint = 1f
            shadowFarHint = 100f
            stable = true
            lispsm = true
            screenSpaceContactShadows = false
            stepCount = 16
            maxShadowDistance = 100f
            elvsm = false
            blurWidth = 2.0f
            shadowBulbRadius = 0.05f
            transform = floatArrayOf(1f, 0f, 0f, 0f)
        }
        assertEquals(1024, options.mapSize)
        assertEquals(2, options.shadowCascades)
        assertEquals(0.1f, options.cascadeSplitPositions[0], 1e-6f)
        assertEquals(0.05f, options.constantBias)
        assertEquals(0.4f, options.normalBias)
        assertEquals(100f, options.shadowFar)
        assertEquals(1f, options.shadowNearHint)
        assertEquals(100f, options.shadowFarHint)
        assertTrue(options.stable)
        assertTrue(options.lispsm)
        assertFalse(options.screenSpaceContactShadows)
        assertEquals(16, options.stepCount)
        assertEquals(100f, options.maxShadowDistance)
        assertFalse(options.elvsm)
        assertEquals(2.0f, options.blurWidth)
        assertEquals(0.05f, options.shadowBulbRadius)
        assertEquals(1f, options.transform[0])

        val splits = FloatArray(4)
        LightManager.ShadowCascades.computeUniformSplits(splits, 4)
        LightManager.ShadowCascades.computeLogSplits(splits, 4, 1f, 100f)
        LightManager.ShadowCascades.computePracticalSplits(splits, 4, 1f, 100f, 0.5f)
    }

    @Test
    fun testSunLightLifecycle() {
        val lm = engine.getLightManager()
        assertNotNull(lm)

        val entity = EntityManager.get().create()
        assertFalse(lm.hasComponent(entity))

        val options = LightManager.ShadowOptions()
        LightManager.Builder(LightManager.Type.SUN)
            .lightChannel(0, true)
            .castShadows(true)
            .shadowOptions(options)
            .castLight(true)
            .direction(0f, -1f, 0f)
            .color(1f, 0.9f, 0.8f)
            .intensity(50000f)
            .sunAngularRadius(0.6f)
            .sunHaloSize(12f)
            .sunHaloFalloff(3f)
            .build(engine, entity)

        assertTrue(lm.hasComponent(entity))
        val inst = lm.getInstance(entity)
        assertTrue(inst != 0)

        assertEquals(LightManager.Type.SUN, lm.getType(inst))

        // Direction
        lm.setDirection(inst, 1f, 0f, 0f)
        val dir = lm.getDirection(inst, FloatArray(3))
        assertEquals(1f, dir[0])

        // Color
        lm.setColor(inst, 0.5f, 0.5f, 0.5f)
        val col = lm.getColor(inst, FloatArray(3))
        assertEquals(0.5f, col[0])

        // Intensity
        lm.setIntensity(inst, 10000f)
        assertEquals(10000f, lm.getIntensity(inst))

        // Sun options
        lm.setSunAngularRadius(inst, 0.8f)
        assertEquals(0.8f, lm.getSunAngularRadius(inst), 1e-6f)
        lm.setSunHaloSize(inst, 5f)
        assertEquals(5f, lm.getSunHaloSize(inst))
        lm.setSunHaloFalloff(inst, 1.5f)
        assertEquals(1.5f, lm.getSunHaloFalloff(inst))

        // Shadows & channels
        lm.setShadowCaster(inst, true)
        assertTrue(lm.isShadowCaster(inst))
        lm.setLightChannel(inst, 1, true)
        assertTrue(lm.getLightChannel(inst, 1))

        lm.destroy(entity)
        assertFalse(lm.hasComponent(entity))
        EntityManager.get().destroy(entity)
    }

    @Test
    fun testSpotLightLifecycle() {
        val lm = engine.getLightManager()
        val entity = EntityManager.get().create()
        
        LightManager.Builder(LightManager.Type.SPOT)
            .position(1f, 2f, 3f)
            .direction(0f, -1f, 0f)
            .intensity(100f, 1f) // efficiency
            .intensityCandela(1000f)
            .falloff(20f)
            .spotLightCone(0.2f, 0.5f)
            .build(engine, entity)

        val inst = lm.getInstance(entity)
        assertTrue(inst != 0)
        assertEquals(LightManager.Type.SPOT, lm.getType(inst))

        // Position
        lm.setPosition(inst, 10f, 20f, 30f)
        val pos = lm.getPosition(inst, FloatArray(3))
        assertEquals(10f, pos[0])
        assertEquals(20f, pos[1])
        assertEquals(30f, pos[2])

        // Falloff
        lm.setFalloff(inst, 15f)
        assertTrue(lm.getFalloff(inst) > 0f)

        // Spot Cone
        lm.setSpotLightCone(inst, 0.1f, 0.4f)
        assertTrue(lm.getInnerConeAngle(inst) > 0f)
        assertTrue(lm.getOuterConeAngle(inst) > 0f)

        lm.destroy(entity)
        EntityManager.get().destroy(entity)
    }
}
