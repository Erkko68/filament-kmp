package io.github.erkko68.filament

import io.github.erkko68.filament.testsupport.IgnoreJs
import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@IgnoreJs // FStream waits on a fence internally (setDimensions), which panics on single-threaded wasm.
class StreamTest : FilamentTestFixture() {
    @Test
    fun aStreamWithoutANativeSourceIsAcquired() {
        val stream = Stream.Builder().width(640).height(480).name("stream").build(engine)
        assertTrue(engine.isValid(stream))
        assertEquals(Stream.StreamType.ACQUIRED, stream.streamType)
        stream.setDimensions(320, 240)
        engine.destroy(stream)
    }
}
