package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertSame

/** Getters with an `out` array fill and return it; without one they allocate. Both must hold the same value. */
class OutParameterTest : FilamentTestFixture() {
    private fun doubles(size: Int, get: (DoubleArray?) -> DoubleArray) {
        val out = DoubleArray(size) { 42.0 }
        assertSame(out, get(out))
        assertContentEquals(get(null), out)
    }

    private fun floats(size: Int, get: (FloatArray?) -> FloatArray) {
        val out = FloatArray(size) { 42f }
        assertSame(out, get(out))
        assertContentEquals(get(null), out)
    }

    @Test
    fun camera() {
        val entity = EntityManager.get().create()
        val cam = engine.createCamera(entity)
        // Defaults: up is +Y, the field of view is vertical.
        cam.setProjection(45.0, 2.0, 0.1, 100.0)
        assertEquals(45f, cam.getFieldOfViewInDegrees(Camera.Fov.VERTICAL), 1e-3f)
        cam.lookAt(0.0, 0.0, 10.0, 0.0, 0.0, 0.0)
        assertContentEquals(doubleArrayOf(0.0, 0.0, 10.0), cam.getPosition())
        assertContentEquals(floatArrayOf(0f, 1f, 0f), cam.getUpVector())
        assertContentEquals(floatArrayOf(0f, 0f, -1f), cam.getForwardVector())
        cam.setScaling(1.5, 2.5)
        cam.setShift(0.25, 0.5)

        doubles(4) { cam.getScaling(it) }
        doubles(2) { cam.getShift(it) }
        doubles(16) { cam.getProjectionMatrix(0, it) }
        doubles(16) { cam.getCullingProjectionMatrix(it) }
        doubles(16) { cam.getModelMatrix(it) }
        doubles(16) { cam.getViewMatrix(it) }
        doubles(16) { cam.getEyeFromViewMatrix(0, it) }
        doubles(3) { cam.getPosition(it) }
        floats(3) { cam.getLeftVector(it) }
        floats(3) { cam.getUpVector(it) }
        floats(3) { cam.getForwardVector(it) }

        val lens = Camera.projection(50.0, 1.5, 0.1, 100.0)
        doubles(16) { Camera.projection(Camera.Fov.HORIZONTAL, 60.0, 1.5, 0.1, 100.0, it) }
        doubles(16) { Camera.projection(50.0, 1.5, 0.1, 100.0, it) }
        doubles(16) { Camera.inverseProjection(lens, it) }
        floats(16) { Camera.inverseProjection(FloatArray(16) { i -> lens[i].toFloat() }, it) }

        engine.destroyCameraComponent(entity)
        EntityManager.get().destroy(entity)
    }

    @Test
    fun light() {
        val entity = EntityManager.get().create()
        LightManager.Builder(LightManager.Type.SPOT)
            .position(1f, 2f, 3f)
            .direction(0f, -1f, 0f)
            .color(0.25f, 0.5f, 0.75f)
            .build(engine, entity)
        val lm = engine.lightManager
        val light = lm.getInstance(entity)
        assertContentEquals(floatArrayOf(1f, 2f, 3f), lm.getPosition(light))
        assertContentEquals(floatArrayOf(0f, -1f, 0f), lm.getDirection(light))
        assertContentEquals(floatArrayOf(0.25f, 0.5f, 0.75f), lm.getColor(light))
        floats(3) { lm.getPosition(light, it) }
        floats(3) { lm.getDirection(light, it) }
        floats(3) { lm.getColor(light, it) }
        engine.destroy(entity)
        EntityManager.get().destroy(entity)
    }

    @Test
    fun indirectLight() {
        val sh = FloatArray(27).also { it[0] = 1f; it[1] = 0.5f; it[2] = 0.25f; it[4] = 0.5f }
        val light = IndirectLight.Builder().irradiance(3, sh).build(engine)
        floats(3) { light.getDirectionEstimate(it) }
        floats(4) { light.getColorEstimate(0f, 1f, 0f, it) }
        floats(3) { IndirectLight.getDirectionEstimate(sh, it) }
        floats(4) { IndirectLight.getColorEstimate(sh, 0f, 1f, 0f, it) }
        engine.destroy(light)
    }

    @Test
    fun toneMapper() {
        val mapper = ToneMapper.Linear()
        floats(3) { mapper(floatArrayOf(0.25f, 0.5f, 0.75f), it) }
        assertContentEquals(floatArrayOf(0.25f, 0.5f, 0.75f), mapper(floatArrayOf(0.25f, 0.5f, 0.75f)))
        mapper.close()
    }
}
