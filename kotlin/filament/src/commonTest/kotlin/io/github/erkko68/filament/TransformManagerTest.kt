package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TransformManagerTest : FilamentTestFixture() {
    @Test
    fun testTransformManagerLifecycle() {
        val tm = engine.transformManager
        assertNotNull(tm)

        val entity = EntityManager.get().create()
        assertFalse(tm.hasComponent(entity))

        tm.create(entity)
        assertTrue(tm.hasComponent(entity))
        val inst = tm.getInstance(entity)
        assertTrue(inst != 0)
        assertEquals(entity, tm.getEntity(inst))
        assertFalse(tm.empty())
        assertTrue(entity in tm.allEntities)
        assertEquals(tm.componentCount, tm.allEntities.size)

        // Set & Get Local transforms (both float and double overloads)
        val testFloat = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            2f, 3f, 4f, 1f
        )
        tm.setTransform(inst, testFloat)
        val readFloat = tm.getTransform(inst, FloatArray(16))
        assertEquals(2f, readFloat[12])
        assertEquals(3f, readFloat[13])
        assertEquals(4f, readFloat[14])

        val testDouble = doubleArrayOf(
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            5.0, 6.0, 7.0, 1.0
        )
        tm.setTransform(inst, testDouble)
        val readDouble = tm.getTransformAccurate(inst)
        assertEquals(5.0, readDouble[12])

        // World transform checks
        val worldFloat = tm.getWorldTransform(inst, FloatArray(16))
        assertEquals(5f, worldFloat[12])

        val worldDouble = tm.getWorldTransformAccurate(inst)
        assertEquals(5.0, worldDouble[12])

        // Parent / Child
        val childEntity = EntityManager.get().create()
        tm.create(childEntity, inst, testFloat)
        val childInst = tm.getInstance(childEntity)
        
        assertEquals(inst, tm.getInstance(entity))
        assertEquals(1, tm.getChildCount(inst))
        
        val children = IntArray(5)
        assertEquals(1, tm.getChildren(inst, children))
        assertEquals(childEntity, children[0])

        assertEquals(entity, tm.getParent(childInst))

        // Reparent under a second root created from a double transform, then detach.
        val otherEntity = EntityManager.get().create()
        tm.create(otherEntity, 0, testDouble)
        val otherInst = tm.getInstance(otherEntity)
        assertEquals(5.0, tm.getTransformAccurate(otherInst)[12])
        tm.setParent(childInst, otherInst)
        assertEquals(otherEntity, tm.getParent(childInst))
        assertEquals(0, tm.getChildCount(inst))
        tm.setParent(childInst, 0)
        assertEquals(0, tm.getChildCount(otherInst))

        // Transactions & Settings
        tm.openLocalTransformTransaction()
        tm.commitLocalTransformTransaction()

        tm.isAccurateTranslationsEnabled = true
        assertTrue(tm.isAccurateTranslationsEnabled)
        tm.isAccurateTranslationsEnabled = false
        assertFalse(tm.isAccurateTranslationsEnabled)

        // Cleanup
        tm.destroy(entity)
        assertFalse(tm.hasComponent(entity))
        
        EntityManager.get().destroy(entity)
        EntityManager.get().destroy(childEntity)
        tm.destroy(otherEntity)
        EntityManager.get().destroy(otherEntity)
    }
}
