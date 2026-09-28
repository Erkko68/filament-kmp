package io.github.erkko68.filament.wasm

import org.khronos.webgl.get
import org.khronos.webgl.set

// Typed access to the module's linear memory. Views are re-read on every access because any
// allocating call can grow memory and detach the previous ones.

fun FilamentModule.getU16(ptr: Int): Int = HEAPU16[ptr ushr 1].toInt() and 0xFFFF
fun FilamentModule.setU16(ptr: Int, value: Int) { HEAPU16[ptr ushr 1] = value.toShort() }
fun FilamentModule.getI64(ptr: Int): Long = (HEAP32[(ptr + 4) ushr 2].toLong() shl 32) or (HEAP32[ptr ushr 2].toLong() and 0xFFFFFFFFL)
fun FilamentModule.setI64(ptr: Int, value: Long) {
    HEAP32[ptr ushr 2] = value.toInt()
    HEAP32[(ptr + 4) ushr 2] = (value ushr 32).toInt()
}

fun FilamentModule.readFloats(ptr: Int, count: Int, out: FloatArray = FloatArray(count)): FloatArray {
    val heap = HEAPF32; val base = ptr ushr 2
    for (i in 0 until count) out[i] = normalizeF32(heap[base + i])
    return out
}
fun FilamentModule.writeFloats(ptr: Int, values: FloatArray, count: Int = values.size) {
    val heap = HEAPF32; val base = ptr ushr 2
    for (i in 0 until count) heap[base + i] = values[i]
}
fun FilamentModule.readDoubles(ptr: Int, count: Int, out: DoubleArray = DoubleArray(count)): DoubleArray {
    val heap = HEAPF64; val base = ptr ushr 3
    for (i in 0 until count) out[i] = heap[base + i]
    return out
}
fun FilamentModule.writeDoubles(ptr: Int, values: DoubleArray, count: Int = values.size) {
    val heap = HEAPF64; val base = ptr ushr 3
    for (i in 0 until count) heap[base + i] = values[i]
}
fun FilamentModule.readInts(ptr: Int, count: Int, out: IntArray = IntArray(count)): IntArray {
    val heap = HEAP32; val base = ptr ushr 2
    for (i in 0 until count) out[i] = heap[base + i]
    return out
}
fun FilamentModule.writeInts(ptr: Int, values: IntArray, count: Int = values.size) {
    val heap = HEAP32; val base = ptr ushr 2
    for (i in 0 until count) heap[base + i] = values[i]
}

/**
 * Kotlin/JS keeps Float as a double, so an f32 read back (0.05000000074505806) wouldn't equal the
 * literal it was set from (0.05f). On js this returns the shortest decimal with the same f32 value;
 * on wasmJs Float is a real f32 and it's the identity.
 */
expect fun normalizeF32(value: Float): Float

/** Copies [count] bytes of [bytes] (from [offset]) into the heap at [ptr]. */
expect fun FilamentModule.writeBytes(ptr: Int, bytes: ByteArray, offset: Int = 0, count: Int = bytes.size - offset)

/** Copies [count] bytes from the heap at [ptr] into a new ByteArray. */
expect fun FilamentModule.readBytes(ptr: Int, count: Int): ByteArray

/** Reads a NUL-terminated UTF-8 string, or null for a null pointer. */
fun FilamentModule.readString(ptr: Int): String? = if (ptr == 0) null else UTF8ToString(ptr)

/** A heap copy handed to an asynchronous Filament upload, released through [callback]. */
class Upload(val ptr: Int, val size: Int, val callback: Int, val userData: Int)

/**
 * Copies [size] bytes of [data] into the heap for a `set*Buffer`/`setImage` call. Pass [Upload.callback]
 * and [Upload.userData] as the C release callback: Filament frees the copy once consumed, then
 * [onRelease] runs.
 */
fun FilamentModule.upload(data: ByteArray, size: Int = data.size, onRelease: (() -> Unit)? = null): Upload {
    val ptr = _malloc(maxOf(size, 1))
    check(ptr != 0) { "wasm malloc($size) failed" }
    writeBytes(ptr, data, 0, size)
    val userData = if (onRelease != null) Callbacks.register(once = true) { _, _ -> onRelease() } else 0
    return Upload(ptr, size, Callbacks.freeBuffer, userData)
}
