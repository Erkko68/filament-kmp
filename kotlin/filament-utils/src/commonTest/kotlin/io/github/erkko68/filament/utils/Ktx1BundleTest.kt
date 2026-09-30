package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.utils.testutils.UtilsTestFixture
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

    /** A 1x1 RGBA8 KTX1 file with [metadata]. */
    private fun ktx1(metadata: Map<String, String>): ByteArray {
        val out = ArrayList<Byte>()
        fun u32(v: Int) = repeat(4) { out += (v shr (8 * it)).toByte() }
        fun pad() { while (out.size % 4 != 0) out += 0 }
        byteArrayOf(0xAB.toByte(), 0x4B, 0x54, 0x58, 0x20, 0x31, 0x31, 0xBB.toByte(), 0x0D, 0x0A, 0x1A, 0x0A).forEach { out += it }
        val keyValues = metadata.map { (k, v) -> (k + "\u0000" + v + "\u0000").encodeToByteArray() }
        // endianness, glType UNSIGNED_BYTE, typeSize, glFormat RGBA, internal RGBA8, base RGBA, 1x1, depth, array, faces, mips
        listOf(0x04030201, 0x1401, 1, 0x1908, 0x8058, 0x1908, 1, 1, 0, 0, 1, 1).forEach(::u32)
        u32(keyValues.sumOf { 4 + (it.size + 3) / 4 * 4 })
        keyValues.forEach { kv -> u32(kv.size); kv.forEach { out += it }; pad() }
        u32(4)
        byteArrayOf(1, 2, 3, 4).forEach { out += it }
        return out.toByteArray()
    }
}
