package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Every completion callback handed to Filament holds a registry entry plus the shared arena
 * backing that call's off-heap buffer, so one that never fires leaks both. Filament consumes
 * buffer descriptors on flush; this pins that the registry actually drains.
 */
class CompletionsDrainTest : FilamentTestFixture() {
    @Test
    fun bufferUploadCallbacksDrainTheRegistry() {
        val baseline = Completions.pending
        val buffer = IndexBuffer.Builder()
            .indexCount(64)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        val data = ByteArray(128)

        var fired = 0
        repeat(8) { buffer.setBuffer(engine, data, 0, data.size) { fired++ } }
        assertEquals(baseline + 8, Completions.pending, "uploads were not registered")

        engine.flushAndWait()
        assertEquals(8, fired, "buffer callbacks did not all fire")
        assertEquals(baseline, Completions.pending, "registry did not drain after flushAndWait")

        engine.destroyIndexBuffer(buffer)
    }
}
