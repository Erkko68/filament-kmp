package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class VertexBufferTest : FilamentTestFixture() {
    @Test
    fun testVertexBufferLifecycle() {
        val buffer = VertexBuffer.Builder()
            .vertexCount(100)
            .bufferCount(1)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 16)
            .attribute(VertexBuffer.VertexAttribute.COLOR, 0, VertexBuffer.AttributeType.UBYTE4, 12, 16)
            .normalized(VertexBuffer.VertexAttribute.COLOR, true)
            .build(engine)

        assertNotNull(buffer)
        assertTrue(engine.isValidVertexBuffer(buffer))

        assertEquals(100, buffer.vertexCount)

        val bytes = ByteArray(300 * 4)

        buffer.setBufferAt(engine, 0, bytes)
        buffer.setBufferAt(engine, 0, bytes, 0, bytes.size)

        var callbackFired = false
        buffer.setBufferAt(engine, 0, bytes, 0, bytes.size) {
            callbackFired = true
        }

        // Test BufferObject connection with a separate buffer that enables buffer objects
        val bufferWithObj = VertexBuffer.Builder()
            .vertexCount(100)
            .enableBufferObjects(true)
            .bufferCount(1)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 16)
            .build(engine)

        val bufferObj = BufferObject.Builder()
            .size(1024)
            .bindingType(BufferObject.BindingType.VERTEX)
            .build(engine)
        
        bufferWithObj.setBufferObjectAt(engine, 0, bufferObj)

        engine.destroyVertexBuffer(buffer)
        engine.destroyVertexBuffer(bufferWithObj)
    }
}
