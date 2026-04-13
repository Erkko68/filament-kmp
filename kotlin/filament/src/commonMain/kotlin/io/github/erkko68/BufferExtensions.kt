package io.github.erkko68

/**
 * Extension functions to convert primitive arrays to ByteArray for Filament buffer uploads.
 */

fun FloatArray.toBytes(): ByteArray {
    val result = ByteArray(this.size * 4)
    for (i in this.indices) {
        val bits = this[i].toRawBits()
        result[i * 4] = (bits and 0xFF).toByte()
        result[i * 4 + 1] = ((bits shr 8) and 0xFF).toByte()
        result[i * 4 + 2] = ((bits shr 16) and 0xFF).toByte()
        result[i * 4 + 3] = ((bits shr 24) and 0xFF).toByte()
    }
    return result
}

fun ShortArray.toBytes(): ByteArray {
    val result = ByteArray(this.size * 2)
    for (i in this.indices) {
        val v = this[i].toInt()
        result[i * 2] = (v and 0xFF).toByte()
        result[i * 2 + 1] = ((v shr 8) and 0xFF).toByte()
    }
    return result
}

fun IntArray.toBytes(): ByteArray {
    val result = ByteArray(this.size * 4)
    for (i in this.indices) {
        val v = this[i]
        result[i * 4] = (v and 0xFF).toByte()
        result[i * 4 + 1] = ((v shr 8) and 0xFF).toByte()
        result[i * 4 + 2] = ((v shr 16) and 0xFF).toByte()
        result[i * 4 + 3] = ((v shr 24) and 0xFF).toByte()
    }
    return result
}
