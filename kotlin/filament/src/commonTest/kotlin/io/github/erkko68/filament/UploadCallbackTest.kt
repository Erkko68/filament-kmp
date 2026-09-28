package io.github.erkko68.filament

import io.github.erkko68.filament.testsupport.IgnoreJs
import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals

@IgnoreJs // flushAndWait can't block on single-threaded wasm.
class UploadCallbackTest : FilamentTestFixture() {
    @Test
    fun bufferUploadCallbacksFireOnceConsumed() {
        val buffer = IndexBuffer.Builder()
            .indexCount(64)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        val data = ByteArray(128)

        var fired = 0
        repeat(8) { buffer.setBuffer(engine, data, 0, data.size) { fired++ } }
        engine.flushAndWait()
        assertEquals(8, fired)

        engine.destroyIndexBuffer(buffer)
    }
}
