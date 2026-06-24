package io.github.erkko68.filament

import io.github.erkko68.filament.testsupport.IgnoreJs
import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@IgnoreJs // Stream is not bound in the web wrapper; Builder.build throws there.
class StreamTest : FilamentTestFixture() {
    @Test
    fun testStreamLifecycle() {
        val stream = Stream.Builder()
            .width(640)
            .height(480)
            .build(engine)
        
        assertNotNull(stream)
        assertNotNull(stream.streamType)
        
        stream.setDimensions(320, 240)
        
        val ts = stream.timestamp
        assertTrue(ts >= 0L)
        
        engine.destroyStream(stream)
    }
}
