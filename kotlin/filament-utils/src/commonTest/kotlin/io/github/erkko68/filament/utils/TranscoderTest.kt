package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Filament
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class TranscoderTest {
    @Test
    fun testNormalizedBytes() {
        Filament.init()
        Transcoder(Transcoder.Config(ComponentType.BYTE, normalized = true, componentCount = 3)).use { transcode ->
            val source = byteArrayOf(127, -127, 0, 0, 127, -127)
            assertEquals(2 * 3 * 4, transcode(null, source, 2))
            val target = FloatArray(6)
            assertEquals(2 * 3 * 4, transcode(target, source, 2))
            assertContentEquals(floatArrayOf(1f, -1f, 0f, 0f, 1f, -1f), target)
        }
    }
}
