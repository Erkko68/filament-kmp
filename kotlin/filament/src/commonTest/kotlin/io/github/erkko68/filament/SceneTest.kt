package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SceneTest : FilamentTestFixture() {
    @Test
    fun testSceneLifecycle() {
        val scene = engine.createScene()
        assertNotNull(scene)
        assertTrue(engine.isValidScene(scene))

        assertNull(scene.skybox)
        assertNull(scene.indirectLight)

        val entity = EntityManager.get().create()
        assertFalse(scene.hasEntity(entity))

        scene.addEntity(entity)
        assertTrue(scene.hasEntity(entity))
        assertEquals(1, scene.entityCount)

        var count = 0
        scene.forEach { e ->
            assertEquals(entity, e)
            count++
        }
        assertEquals(1, count)

        val entities = scene.getEntities()
        assertEquals(1, entities.size)
        assertEquals(entity, entities[0])

        val entitiesBuffer = IntArray(5)
        val entitiesFilled = scene.getEntities(entitiesBuffer)
        assertEquals(entity, entitiesFilled[0])

        scene.removeEntity(entity)
        assertFalse(scene.hasEntity(entity))
        assertEquals(0, scene.entityCount)

        // Multiple entities
        val e1 = EntityManager.get().create()
        val e2 = EntityManager.get().create()
        scene.addEntities(intArrayOf(e1, e2))
        assertEquals(2, scene.entityCount)
        assertTrue(scene.hasEntity(e1))
        assertTrue(scene.hasEntity(e2))

        scene.remove(e1)
        assertFalse(scene.hasEntity(e1))
        assertTrue(scene.hasEntity(e2))

        scene.removeEntities(intArrayOf(e2))
        assertFalse(scene.hasEntity(e2))
        assertEquals(0, scene.entityCount)

        EntityManager.get().destroy(entity)
        EntityManager.get().destroy(e1)
        EntityManager.get().destroy(e2)
        engine.destroyScene(scene)
    }
}
