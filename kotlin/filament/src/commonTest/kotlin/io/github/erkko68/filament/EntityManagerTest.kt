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

        val entities = IntArray(5).also { em.create(it) }
        for (e in entities) {
            assertTrue(em.isAlive(e))
        }
        assertTrue(em.entityCount >= 6)
        assertTrue(EntityManager.getIndex(entities[0]) > 0) // index 0 is the null entity

        em.destroy(entity)
        assertFalse(em.isAlive(entity))

        em.destroy(entities)
        for (e in entities) {
            assertFalse(em.isAlive(e))
        }

        assertTrue(EntityManager.maxEntityCount > 0)
        val epoch = em.latestEpochID
        em.advanceEpoch()
        assertEquals(epoch + 1, em.latestEpochID)
        em.flushNotifications()
    }
}
