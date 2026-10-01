package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.utils.testutils.UtilsTestFixture
import io.github.erkko68.filament.utils.testutils.ktx1
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class Ktx1BundleTest : UtilsTestFixture() {
    @Test
    fun testParseAndSerialize() {
        val sh = FloatArray(9 * 3) { it * 0.5f }
        Ktx1Bundle(ktx1(mapOf("sh" to sh.joinToString(" ")))).use { bundle ->
            assertEquals(1, bundle.numMipLevels)
            assertEquals(1, bundle.arrayLength)
            assertFalse(bundle.isCubemap)
            val result = FloatArray(9 * 3)
            assertTrue(bundle.getSphericalHarmonics(result))
            assertContentEquals(sh, result)

            bundle.setMetadata("KTXorientation", "S=r,T=u")
            assertEquals("S=r,T=u", bundle.getMetadata("KTXorientation"))
            assertNull(bundle.getMetadata("missing"))
            val bytes = ByteArray(bundle.serializedLength)
            assertTrue(bundle.serialize(bytes))
            assertFalse(bundle.serialize(ByteArray(1)))
            result.fill(0f)
            Ktx1Bundle(bytes).use { assertTrue(it.getSphericalHarmonics(result)) }
            assertContentEquals(sh, result)
        }
    }

    @Test
    fun testSrgbFormats() {
        assertTrue(Ktx1Reader.isSrgbTextureFormat(Texture.InternalFormat.SRGB8_A8))
        assertFalse(Ktx1Reader.isSrgbTextureFormat(Texture.InternalFormat.RGBA8))
    }
}
