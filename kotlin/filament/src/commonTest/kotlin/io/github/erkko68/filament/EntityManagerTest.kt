package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EntityManagerTest : FilamentTestFixture() {
    @Test
    fun testEntityManagerLifecycle() {
        val em = EntityManager.get()
        assertNotNull(em)

        val entity = em.create()
        assertTrue(entity != 0)
        assertTrue(em.isAlive(entity))

        val entities = em.create(5)
        assertEquals(5, entities.size)
        for (e in entities) {
            assertTrue(em.isAlive(e))
        }

        val customEntities = em.create(intArrayOf(10, 20))
        assertEquals(2, customEntities.size)

        em.destroy(entity)
        assertFalse(em.isAlive(entity))

        em.destroy(entities)
        for (e in entities) {
            assertFalse(em.isAlive(e))
        }
    }
}
