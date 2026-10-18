package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import io.github.erkko68.filament.testutils.pumpUntil
import kotlin.test.Test
import kotlin.test.assertEquals

class IndexBufferTest : FilamentTestFixture() {
    @Test
    fun uploadCallbacksFireOnceConsumed() {
        val buffer = IndexBuffer.Builder().indexCount(64).bufferType(IndexBuffer.IndexType.USHORT).build(engine)
        assertEquals(64, buffer.indexCount)
        val data = ByteArray(128)
        buffer.setBuffer(engine, data)
        var consumed = 0
        repeat(8) { buffer.setBuffer(engine, data, 0, data.size) { consumed++ } }
        engine.pumpUntil { consumed == 8 }
        engine.destroy(buffer)
    }
}
