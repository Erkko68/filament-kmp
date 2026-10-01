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
        assertTrue(engine.isValid(scene))

        assertNull(scene.skybox)
        assertNull(scene.indirectLight)

        val entity = EntityManager.get().create()
        assertFalse(scene.hasEntity(entity))

        scene.addEntity(entity)
        assertTrue(scene.hasEntity(entity))
        // Strict: a C bool must come back as a real Boolean (web returns it as a JS number).
        assertEquals(true, scene.hasEntity(entity))
        assertEquals(false, scene.hasEntity(EntityManager.get().create()))
        assertEquals(1, scene.entityCount)

        var count = 0
        scene.forEach { e ->
            assertEquals(entity, e)
            count++
        }
        assertEquals(1, count)

        scene.remove(entity)
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

        scene.addEntities(intArrayOf(e1, e2))
        scene.removeAllEntities()
        assertEquals(0, scene.entityCount)

        EntityManager.get().destroy(entity)
        EntityManager.get().destroy(e1)
        EntityManager.get().destroy(e2)
        engine.destroy(scene)
    }

    @Test
    fun testEnvironmentAndComponentCounts() {
        val scene = engine.createScene()
        val sky = Skybox.Builder().color(0f, 0f, 0f, 1f).build(engine)
        val ibl = IndirectLight.Builder().radiance(1, floatArrayOf(1f, 1f, 1f)).build(engine)
        scene.skybox = sky
        scene.indirectLight = ibl
        assertEquals(sky, scene.skybox)
        assertEquals(ibl, scene.indirectLight)
        scene.skybox = null
        scene.indirectLight = null
        assertNull(scene.skybox)

        val em = EntityManager.get()
        val light = em.create()
        LightManager.Builder(LightManager.Type.DIRECTIONAL).build(engine, light)
        // Default material: building a renderable needs no material under NOOP.
        val vb = VertexBuffer.Builder().vertexCount(3).bufferCount(1)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 12)
            .build(engine)
        val renderable = em.create()
        RenderableManager.Builder(1)
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, 0, 3)
            .boundingBox(Box(floatArrayOf(0f, 0f, 0f), floatArrayOf(1f, 1f, 1f)))
            .build(engine, renderable)
        scene.addEntities(intArrayOf(light, renderable, em.create()))
        assertEquals(1, scene.lightCount)
        assertEquals(1, scene.renderableCount)

        engine.destroy(light)
        engine.destroy(renderable)
        engine.destroy(scene)
        engine.destroy(vb)
        engine.destroy(sky)
        engine.destroy(ibl)
    }
}
