package io.github.erkko68.filament.wasm

import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.khronos.webgl.set

// Typed access to the module's linear memory. Views are re-read on every access because any
// allocating call can grow memory and detach the previous ones.

fun FilamentModule.getI32(ptr: Int): Int = HEAP32[ptr ushr 2]
fun FilamentModule.setI32(ptr: Int, value: Int) { HEAP32[ptr ushr 2] = value }
fun FilamentModule.getU32(ptr: Int): Long = HEAPU32[ptr ushr 2].toLong() and 0xFFFFFFFFL
fun FilamentModule.getF32(ptr: Int): Float = normalizeF32(HEAPF32[ptr ushr 2])
fun FilamentModule.setF32(ptr: Int, value: Float) { HEAPF32[ptr ushr 2] = value }
fun FilamentModule.getF64(ptr: Int): Double = HEAPF64[ptr ushr 3]
fun FilamentModule.setF64(ptr: Int, value: Double) { HEAPF64[ptr ushr 3] = value }
fun FilamentModule.getU8(ptr: Int): Int = HEAPU8[ptr].toInt() and 0xFF
fun FilamentModule.setU8(ptr: Int, value: Int) { HEAPU8[ptr] = value.toByte() }
fun FilamentModule.getU16(ptr: Int): Int = HEAPU16[ptr ushr 1].toInt() and 0xFFFF
fun FilamentModule.setU16(ptr: Int, value: Int) { HEAPU16[ptr ushr 1] = value.toShort() }
fun FilamentModule.getBool(ptr: Int): Boolean = HEAPU8[ptr].toInt() != 0
fun FilamentModule.setBool(ptr: Int, value: Boolean) { HEAPU8[ptr] = if (value) 1 else 0 }
fun FilamentModule.getI64(ptr: Int): Long = (getI32(ptr + 4).toLong() shl 32) or (getI32(ptr).toLong() and 0xFFFFFFFFL)
fun FilamentModule.setI64(ptr: Int, value: Long) { setI32(ptr, value.toInt()); setI32(ptr + 4, (value ushr 32).toInt()) }

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
internal expect fun normalizeF32(value: Float): Float

/** Copies [count] bytes of [bytes] (from [offset]) into the heap at [ptr]. */
expect fun FilamentModule.writeBytes(ptr: Int, bytes: ByteArray, offset: Int = 0, count: Int = bytes.size - offset)

/** Copies [count] bytes from the heap at [ptr] into a new ByteArray. */
expect fun FilamentModule.readBytes(ptr: Int, count: Int): ByteArray

fun FilamentModule.zero(ptr: Int, size: Int) = fill(HEAPU8, ptr, size)
private fun fill(heap: Uint8Array, ptr: Int, size: Int): Unit = js("heap.fill(0, ptr, ptr + size)")

/**
 * The web analogue of cinterop's `memScoped`: everything allocated in [block] is freed when it
 * returns. Allocations are zeroed, so structs only need the fields they actually set.
 */
inline fun <R> FilamentModule.heapScoped(block: HeapScope.() -> R): R {
    val scope = HeapScope(this)
    try {
        return scope.block()
    } finally {
        scope.freeAll()
    }
}

class HeapScope(val module: FilamentModule) {
    private val allocations = ArrayList<Int>(4)

    fun alloc(size: Int): Int {
        val ptr = module._malloc(maxOf(size, 1))
        check(ptr != 0) { "wasm malloc($size) failed" }
        module.zero(ptr, size)
        allocations.add(ptr)
        return ptr
    }

    /** NUL-terminated UTF-8 copy of [value]; returns 0 for null. */
    fun cString(value: String?): Int {
        if (value == null) return 0
        val size = module.lengthBytesUTF8(value) + 1
        val ptr = alloc(size)
        module.stringToUTF8(value, ptr, size)
        return ptr
    }

    fun bytes(values: ByteArray): Int = alloc(values.size).also { module.writeBytes(it, values) }
    fun floats(values: FloatArray): Int = alloc(values.size * 4).also { module.writeFloats(it, values) }
    fun doubles(values: DoubleArray): Int = alloc(values.size * 8).also { module.writeDoubles(it, values) }
    fun ints(values: IntArray): Int = alloc(values.size * 4).also { module.writeInts(it, values) }
    fun shorts(values: ShortArray): Int = alloc(values.size * 2).also { p -> values.forEachIndexed { i, v -> module.setU16(p + 2 * i, v.toInt()) } }

    fun freeAll() {
        allocations.forEach { module._free(it) }
        allocations.clear()
    }
}

/** Reads a NUL-terminated UTF-8 string, or null for a null pointer. */
fun FilamentModule.readString(ptr: Int): String? = if (ptr == 0) null else UTF8ToString(ptr)
