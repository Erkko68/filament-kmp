package io.github.erkko68.filament.wasm

import org.khronos.webgl.Int8Array

// On js a ByteArray is an Int8Array, so both directions are a single native copy.

actual fun FilamentModule.writeBytes(ptr: Int, bytes: ByteArray, offset: Int, count: Int) {
    val source = bytes.unsafeCast<Int8Array>()
    HEAP8.set(if (offset == 0 && count == bytes.size) source else source.subarray(offset, offset + count), ptr)
}

actual fun FilamentModule.readBytes(ptr: Int, count: Int): ByteArray =
    Int8Array(HEAP8.buffer, ptr, count).slice(0, count).unsafeCast<ByteArray>()

private fun Int8Array.slice(start: Int, end: Int): Int8Array = asDynamic().slice(start, end).unsafeCast<Int8Array>()

internal actual fun normalizeF32(value: Float): Float = shortestF32(value)

private fun shortestF32(v: Float): Float = js("""{
    for (let p = 1; p < 10; p++) { const d = Number(v.toPrecision(p)); if (Math.fround(d) === v) return d; }
    return v;
}""")
