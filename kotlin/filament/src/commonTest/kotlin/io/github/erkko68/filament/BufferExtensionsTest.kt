package io.github.erkko68.filament

import kotlin.test.Test
import kotlin.test.assertEquals

class BufferExtensionsTest {
    @Test
    fun floatArrayToBytesLittleEndian() {
        val bytes = floatArrayOf(1f, -2f).toBytes()
        assertEquals(8, bytes.size)
        // 1.0f = 0x3F800000, little-endian
        assertEquals(0x00.toByte(), bytes[0])
        assertEquals(0x00.toByte(), bytes[1])
        assertEquals(0x80.toByte(), bytes[2])
        assertEquals(0x3F.toByte(), bytes[3])
        // -2.0f = 0xC0000000, little-endian
        assertEquals(0x00.toByte(), bytes[4])
        assertEquals(0x00.toByte(), bytes[5])
        assertEquals(0x00.toByte(), bytes[6])
        assertEquals(0xC0.toByte(), bytes[7])
    }

    @Test
    fun shortArrayToBytesLittleEndian() {
        val bytes = shortArrayOf(0x0102, 0x00FF).toBytes()
        assertEquals(4, bytes.size)
        assertEquals(0x02.toByte(), bytes[0])
        assertEquals(0x01.toByte(), bytes[1])
        assertEquals(0xFF.toByte(), bytes[2])
        assertEquals(0x00.toByte(), bytes[3])
    }

    @Test
    fun intArrayToBytesLittleEndian() {
        val bytes = intArrayOf(0x04030201).toBytes()
        assertEquals(4, bytes.size)
        assertEquals(0x01.toByte(), bytes[0])
        assertEquals(0x02.toByte(), bytes[1])
        assertEquals(0x03.toByte(), bytes[2])
        assertEquals(0x04.toByte(), bytes[3])
    }

    @Test
    fun emptyArraysProduceEmptyBytes() {
        assertEquals(0, FloatArray(0).toBytes().size)
        assertEquals(0, ShortArray(0).toBytes().size)
        assertEquals(0, IntArray(0).toBytes().size)
    }
}
