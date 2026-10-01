package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InstanceBufferTest : FilamentTestFixture() {
    private fun translation(x: Float) = FloatArray(16) { if (it % 5 == 0) 1f else 0f }.also { it[12] = x }

    @Test
    fun testLocalTransforms() {
        val initial = (0 until 3).map { translation(it.toFloat()) }
        val buffer = InstanceBuffer.Builder(3)
            .localTransforms(initial.reduce(FloatArray::plus))
            .name("instances")
            .build(engine)
        assertTrue(engine.isValid(buffer))
        assertEquals(3, buffer.instanceCount)
        initial.forEachIndexed { i, m -> assertContentEquals(m, buffer.getLocalTransform(i)) }

        // One transform at offset 2 leaves the others alone.
        buffer.setLocalTransforms(translation(7f), offset = 2)
        assertContentEquals(initial[1], buffer.getLocalTransform(1))
        assertContentEquals(translation(7f), buffer.getLocalTransform(2))
        assertTrue(engine.destroy(buffer))

        val identity = InstanceBuffer.Builder(1).build(engine)
        assertContentEquals(translation(0f), identity.getLocalTransform(0))
        engine.destroy(identity)
    }

    @Test
    fun testRenderableInstances() {
        val vb = VertexBuffer.Builder()
            .vertexCount(3)
            .bufferCount(1)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 12)
            .build(engine)
        vb.setBufferAt(engine, 0, ByteArray(36))
        val ib = IndexBuffer.Builder().indexCount(3).bufferType(IndexBuffer.IndexType.USHORT).build(engine)
        ib.setBuffer(engine, byteArrayOf(0, 0, 1, 0, 2, 0))
        val buffer = InstanceBuffer.Builder(3).build(engine)

        val entity = EntityManager.get().create()
        RenderableManager.Builder(1)
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib)
            .boundingBox(Box(floatArrayOf(0f, 0f, 0f), floatArrayOf(1f, 1f, 1f)))
            .instances(3, buffer)
            .build(engine, entity)
        val rm = engine.renderableManager
        assertEquals(3, rm.getInstanceCount(rm.getInstance(entity)))

        engine.destroy(entity)
        EntityManager.get().destroy(entity)
        engine.destroy(buffer)
        engine.destroy(vb)
        engine.destroy(ib)
    }
}
