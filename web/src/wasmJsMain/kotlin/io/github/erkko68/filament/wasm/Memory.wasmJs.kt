package io.github.erkko68.filament.wasm

import org.khronos.webgl.get
import org.khronos.webgl.set

// ponytail: per-byte copy across the Kotlin/Wasm ↔ JS boundary; switch to a bulk copy
// (Kotlin/Wasm linear memory → HEAP8) if large uploads show up in profiles.

actual fun FilamentModule.writeBytes(ptr: Int, bytes: ByteArray, offset: Int, count: Int) {
    val heap = HEAP8
    for (i in 0 until count) heap[ptr + i] = bytes[offset + i]
}

actual fun FilamentModule.readBytes(ptr: Int, count: Int): ByteArray {
    val heap = HEAP8
    return ByteArray(count) { heap[ptr + it] }
}

@Suppress("NOTHING_TO_INLINE")
internal actual inline fun normalizeF32(value: Float): Float = value
