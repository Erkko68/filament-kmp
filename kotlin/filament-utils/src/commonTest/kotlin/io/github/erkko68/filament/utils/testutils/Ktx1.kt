package io.github.erkko68.filament.utils.testutils

/** A 1x1 RGBA8 KTX1 file with [metadata]. */
fun ktx1(metadata: Map<String, String> = emptyMap()): ByteArray {
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
