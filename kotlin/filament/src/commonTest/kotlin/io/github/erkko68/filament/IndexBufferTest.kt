package io.github.erkko68.filament

import io.github.erkko68.filament.testsupport.IgnoreJs
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
            .bufferType(IndexBuffer.IndexType.USHORT)
            .build(engine)

        assertNotNull(buffer)
        assertTrue(engine.isValid(buffer))

        assertEquals(100, buffer.indexCount)

        val data = byteArrayOf(0, 0, 1, 0, 2, 0) // 3 USHORTs
        buffer.setBuffer(engine, data)
        buffer.setBuffer(engine, data, 0, 6)

        var callbackFired = false
        buffer.setBuffer(engine, data, 0, 6) {
            callbackFired = true
        }

        engine.destroy(buffer)
    }

    @Test
    @IgnoreJs // flushAndWait can't block on single-threaded wasm.
    fun uploadCallbacksFireOnceConsumed() {
        val buffer = IndexBuffer.Builder()
            .indexCount(64)
            .bufferType(IndexBuffer.IndexType.USHORT)
            .build(engine)
        val data = ByteArray(128)

        var fired = 0
        repeat(8) { buffer.setBuffer(engine, data, 0, data.size) { fired++ } }
        engine.flushAndWait()
        assertEquals(8, fired)

        engine.destroy(buffer)
    }
}
