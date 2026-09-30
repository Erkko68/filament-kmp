package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.utils.testutils.UtilsTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals

class Ktx2ReaderTest : UtilsTestFixture() {
    @Test
    fun testRequestFormat() {
        Ktx2Reader(engine, quiet = true).use { reader ->
            assertEquals(Ktx2Reader.Result.SUCCESS, reader.requestFormat(Texture.InternalFormat.RGBA8))
            assertEquals(Ktx2Reader.Result.FORMAT_ALREADY_REQUESTED, reader.requestFormat(Texture.InternalFormat.RGBA8))
            reader.unrequestFormat(Texture.InternalFormat.RGBA8)
            assertEquals(Ktx2Reader.Result.SUCCESS, reader.requestFormat(Texture.InternalFormat.RGBA8))
        }
    }
}
