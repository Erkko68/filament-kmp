package io.github.erkko68.filament

import io.github.erkko68.filament.interop.NullPointer
import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Calls that lean on default or null arguments, which the explicit-argument tests never take. */
class DefaultArgumentsTest : FilamentTestFixture() {
    @Test
    fun transformGettersAllocate() {
        val tm = engine.transformManager
        val entity = EntityManager.get().create()
        tm.create(entity)
        val inst = tm.getInstance(entity)
        val translation = FloatArray(16) { if (it % 5 == 0) 1f else 0f }.also { it[12] = 2f; it[13] = 3f; it[14] = 4f }
        tm.setTransform(inst, translation)
        assertContentEquals(translation, tm.getTransform(inst))
        assertContentEquals(translation, tm.getWorldTransform(inst))
        tm.destroy(entity)
        EntityManager.get().destroy(entity)
    }

    @Test
    fun morphTargetBufferFlagsDefaultToEnabled() {
        val buffer = MorphTargetBuffer.Builder().vertexCount(4).count(1)
            .withPositions().withTangents().enableCustomMorphing()
            .build(engine)
        assertTrue(buffer.hasPositions)
        assertTrue(buffer.hasTangents)
        assertTrue(buffer.isCustomMorphingEnabled)
        engine.destroy(buffer)
    }

    @Test
    fun surfaceOrientationDefaultsToTightlyPackedInputs() {
        val orientation = SurfaceOrientation.Builder()
            .vertexCount(4)
            .positions(floatArrayOf(-1f, -1f, 0f, 1f, -1f, 0f, -1f, 1f, 0f, 1f, 1f, 0f))
            .normals(floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f))
            .tangents(floatArrayOf(1f, 0f, 0f, 1f, 1f, 0f, 0f, 1f, 1f, 0f, 0f, 1f, 1f, 0f, 0f, 1f))
            .uvs(floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 1f, 1f))
            .triangleCount(2)
            .triangles(intArrayOf(0, 1, 2, 2, 1, 3))
            .build()
        assertNotNull(orientation).use {
            assertNotEquals(NullPointer, it.nativeObject)
            val quats = FloatArray(16)
            it.getQuats(quats, 4)
            // Normal +Z with tangent +X is the identity frame.
            for (v in 0 until 4) assertEquals(1f, kotlin.math.abs(quats[v * 4 + 3]), 1e-4f, "vertex $v: ${quats.toList()}")
        }
    }

    @Test
    fun fenceWaitsForEverByDefault() {
        assertEquals(Fence.FenceStatus.CONDITION_SATISFIED, engine.createFence().also { it.wait() }.let { Fence.waitAndDestroy(it) })
    }

    @Test
    fun skinningBufferTakesEveryBone() {
        val buffer = SkinningBuffer.Builder().boneCount(2).build(engine)
        // The default count is the whole array: 2 bones fit, a miscounted 32 would panic.
        buffer.setBones(engine, FloatArray(32) { if (it % 16 % 5 == 0) 1f else 0f })
        assertEquals(2, buffer.boneCount)
        engine.destroy(buffer)
    }

    @Test
    fun instanceBufferRejectsShortArrays() {
        val buffer = InstanceBuffer.Builder(2).build(engine)
        assertFailsWith<IllegalArgumentException> { buffer.setLocalTransforms(FloatArray(16), count = 2) }
        engine.destroy(buffer)
    }

    @Test
    fun viewAssociationsCanBeCleared() {
        val view = engine.createView()
        val scene = engine.createScene()
        val entity = EntityManager.get().create()
        val camera = engine.createCamera(entity)

        view.name = "named"
        assertEquals("named", view.name)
        view.name = null
        assertTrue(view.name.isNullOrEmpty())

        view.scene = scene
        assertEquals(scene, view.scene)
        view.scene = null
        assertNull(view.scene)

        view.camera = camera
        assertTrue(view.hasCamera)
        view.camera = null
        assertFalse(view.hasCamera)
        assertNull(view.camera)

        engine.destroy(view)
        engine.destroy(scene)
        engine.destroyCameraComponent(entity)
        EntityManager.get().destroy(entity)
    }

    @Test
    fun cameraComponentLookup() {
        val entity = EntityManager.get().create()
        assertNull(engine.getCameraComponent(entity))
        engine.createCamera(entity)
        assertEquals(entity, assertNotNull(engine.getCameraComponent(entity)).entity)
        engine.destroyCameraComponent(entity)
        EntityManager.get().destroy(entity)
    }

    @Test
    fun indirectLightTexturesCanBeUnset() {
        val light = IndirectLight.Builder()
            .reflections(null)
            .irradiance(null)
            .irradiance(3, FloatArray(27).also { it[0] = 1f })
            .build(engine)
        assertNull(light.reflectionsTexture)
        assertNull(light.irradianceTexture)
        engine.destroy(light)
    }

    @Test
    fun destroyingNoEngineIsANoOp() {
        Engine.destroy(null)
        assertTrue(engine.isValid)
    }
}
