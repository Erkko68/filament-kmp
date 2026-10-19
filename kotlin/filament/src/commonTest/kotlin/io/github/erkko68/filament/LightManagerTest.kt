package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LightManagerTest : FilamentTestFixture() {
    @Test
    fun cascadeSplitsAreOrderedWithinTheFrustum() {
        val uniform = FloatArray(3).also { LightManager.ShadowCascades.computeUniformSplits(it, 4) }
        assertContentEquals(floatArrayOf(0.25f, 0.5f, 0.75f), uniform)
        val log = FloatArray(3).also { LightManager.ShadowCascades.computeLogSplits(it, 4, 1f, 100f) }
        val practical = FloatArray(3).also { LightManager.ShadowCascades.computePracticalSplits(it, 4, 1f, 100f, 0.5f) }
        for (split in listOf(log, practical)) {
            assertTrue(split.toList().zipWithNext().all { (a, b) -> a < b } && split.all { it > 0f && it < 1f }, split.toList().toString())
        }
        // Log splits crowd the near plane; the practical scheme sits between the two.
        for (i in 0 until 3) assertTrue(log[i] < practical[i] && practical[i] < uniform[i], "split $i")
    }

    @Test
    fun testShadowOptionsDefaults() {
        // A default-constructed ShadowOptions must match Filament's own defaults
        // (include/filament/LightManager.h). The native/JVM structs are hand-allocated, so any
        // field left uninitialized would be garbage (native) or zero (JVM) and silently break
        // shadows — this guards that regression. `transform` is the identity quaternion (0,0,0,1):
        // a zero/garbage value collapses the directional shadow frustum.
        val o = LightManager.ShadowOptions()
        assertEquals(1024, o.mapSize)
        assertEquals(1, o.shadowCascades)
        assertEquals(0.125f, o.cascadeSplitPositions[0], 1e-6f)
        assertEquals(0.25f, o.cascadeSplitPositions[1], 1e-6f)
        assertEquals(0.50f, o.cascadeSplitPositions[2], 1e-6f)
        assertEquals(0.001f, o.constantBias, 1e-6f)
        assertEquals(1.0f, o.normalBias, 1e-6f)
        assertEquals(1.0f, o.shadowNearHint, 1e-6f)
        assertEquals(100.0f, o.shadowFarHint, 1e-6f)
        assertEquals(0.3f, o.maxShadowDistance, 1e-6f)
        assertEquals(-1.0f, o.shadowBulbRadius, 1e-6f)
        assertFalse(o.stable)
        assertTrue(o.lispsm)
        assertEquals(0f, o.transform[0], 1e-6f)
        assertEquals(0f, o.transform[1], 1e-6f)
        assertEquals(0f, o.transform[2], 1e-6f)
        assertEquals(1f, o.transform[3], 1e-6f)
        // 0 means "defer to the View-wide SoftShadowOptions" — Filament's own default.
        assertEquals(0.5f, o.polygonOffsetConstant, 1e-6f)
        assertEquals(2.0f, o.polygonOffsetSlope, 1e-6f)
        assertEquals(1f, o.penumbraScale, 1e-6f)
        assertEquals(1f, o.penumbraRatioScale, 1e-6f)
        // 0 means "defer to the View-wide SoftShadowOptions" — Filament's own default.
        assertEquals(0f, o.maxPenumbraRatio, 1e-6f)
        assertEquals(0f, o.maxSearchRadius, 1e-6f)
    }

    @Test
    fun testSunLightLifecycle() {
        val lm = engine.lightManager

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
        assertTrue(lm.isDirectional(inst))
        assertFalse(lm.isSpotLight(inst))
        assertEquals(entity, lm.getEntity(inst))
        assertTrue(entity in lm.allEntities)

        // Shadow options round-trip through the native struct
        lm.setShadowOptions(inst, LightManager.ShadowOptions().apply {
            mapSize = 512
            lispsm = false
            vsm.blurWidth = 3f
            cascadeSplitPositions = floatArrayOf(0.2f, 0.4f, 0.6f)
        })
        val read = lm.getShadowOptions(inst)
        assertEquals(512, read.mapSize)
        assertFalse(read.lispsm)
        assertEquals(3f, read.vsm.blurWidth)
        assertEquals(0.4f, read.cascadeSplitPositions[1], 1e-6f)
        assertEquals(1f, read.transform[3])

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
        lm.setIntensity(inst, 100f, 0.5f) // watts × 683 lm/W × efficiency
        assertEquals(34150f, lm.getIntensity(inst), 0.5f)
        assertFalse(lm.empty())

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
        val lm = engine.lightManager
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
        assertTrue(lm.isSpotLight(inst))
        assertFalse(lm.isPointLight(inst))

        // Position
        lm.setPosition(inst, 10f, 20f, 30f)
        val pos = lm.getPosition(inst, FloatArray(3))
        assertEquals(10f, pos[0])
        assertEquals(20f, pos[1])
        assertEquals(30f, pos[2])

        lm.setIntensityCandela(inst, 500f)
        assertEquals(500f, lm.getIntensity(inst), 1e-3f)

        lm.setFalloff(inst, 15f)
        assertEquals(15f, lm.getFalloff(inst), 1e-4f)

        lm.setSpotLightCone(inst, 0.1f, 0.4f)
        assertEquals(0.1f, lm.getSpotLightInnerCone(inst), 0.01f) // recomputed, not stored
        assertEquals(0.4f, lm.getSpotLightOuterCone(inst), 1e-6f)

        lm.destroy(entity)
        EntityManager.get().destroy(entity)
    }
}
