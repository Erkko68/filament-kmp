package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import io.github.erkko68.filament.testsupport.pumpUntil
import kotlin.test.Test
import kotlin.test.assertEquals

class VertexBufferTest : FilamentTestFixture() {
    @Test
    fun interleavedAttributesUploadAndReportBack() {
        val buffer = VertexBuffer.Builder()
            .vertexCount(100)
            .bufferCount(1)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 16)
            .attribute(VertexBuffer.VertexAttribute.COLOR, 0, VertexBuffer.AttributeType.UBYTE4, 12, 16)
            .normalized(VertexBuffer.VertexAttribute.COLOR, true)
            .build(engine)
        assertEquals(100, buffer.vertexCount)
        val bytes = ByteArray(100 * 16)
        buffer.setBufferAt(engine, 0, bytes)
        var consumed = false
        buffer.setBufferAt(engine, 0, bytes, 0, bytes.size) { consumed = true }
        engine.pumpUntil { consumed }
        engine.destroy(buffer)
    }

    @Test
    fun bufferObjectsBackTheAttributes() {
        val buffer = VertexBuffer.Builder()
            .vertexCount(100)
            .enableBufferObjects(true)
            .bufferCount(1)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 12)
            .build(engine)
        val positions = BufferObject.Builder().size(1200).bindingType(BufferObject.BindingType.VERTEX).build(engine)
        positions.setBuffer(engine, ByteArray(1200))
        buffer.setBufferObjectAt(engine, 0, positions)
        engine.destroy(buffer)
        engine.destroy(positions)
    }
}
