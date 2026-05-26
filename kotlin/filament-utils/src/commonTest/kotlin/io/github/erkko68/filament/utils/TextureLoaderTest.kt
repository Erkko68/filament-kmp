package io.github.erkko68.filament.utils

import io.github.erkko68.filament.utils.testutils.UtilsTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals

class TextureLoaderTest : UtilsTestFixture() {

    // Ordinal checks verify that the Kotlin enum layout matches the native enum layout.
    @Test
    fun testTextureTypeEnumOrdinals() {
        assertEquals(0, TextureLoader.TextureType.COLOR.ordinal)
        assertEquals(1, TextureLoader.TextureType.NORMAL.ordinal)
        assertEquals(2, TextureLoader.TextureType.DATA.ordinal)
    }

    // NOTE: TextureLoader.loadTexture requires valid image bytes; the native
    // parser does not gracefully reject malformed input.
}
