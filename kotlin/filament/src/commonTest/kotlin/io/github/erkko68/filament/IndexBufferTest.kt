package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class IndexBufferTest : FilamentTestFixture() {
    @Test
    fun testIndexBufferLifecycle() {
        val buffer = IndexBuffer.Builder()
            .indexCount(100)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)

        assertNotNull(buffer)
        assertTrue(engine.isValidIndexBuffer(buffer))

        assertEquals(100, buffer.indexCount)

        val data = byteArrayOf(0, 0, 1, 0, 2, 0) // 3 USHORTs
        buffer.setBuffer(engine, data)
        buffer.setBuffer(engine, data, 0, 6)

        var callbackFired = false
        buffer.setBuffer(engine, data, 0, 6) {
            callbackFired = true
        }

        engine.destroyIndexBuffer(buffer)
    }
}
