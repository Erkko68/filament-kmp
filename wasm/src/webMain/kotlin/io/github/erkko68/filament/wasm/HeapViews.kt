package io.github.erkko68.filament.wasm

// Views over filament-kmp.wasm heap memory, shaped like cinterop's so actuals port from nativeMain:
// struct array fields (`opts.color[i]`), `allocArray<FloatVar>(n)` and `alloc<IntVar>().value`.

class F32Array(val ptr: Int) {
    operator fun get(i: Int): Float = fila.getF32(ptr + i * 4)
    operator fun set(i: Int, value: Float) = fila.setF32(ptr + i * 4, value)
}

class F64Array(val ptr: Int) {
    operator fun get(i: Int): Double = fila.getF64(ptr + i * 8)
    operator fun set(i: Int, value: Double) = fila.setF64(ptr + i * 8, value)
}

class I32Array(val ptr: Int) {
    operator fun get(i: Int): Int = fila.getI32(ptr + i * 4)
    operator fun set(i: Int, value: Int) = fila.setI32(ptr + i * 4, value)
}

class U8Array(val ptr: Int) {
    operator fun get(i: Int): Int = fila.getU8(ptr + i)
    operator fun set(i: Int, value: Int) = fila.setU8(ptr + i, value)
}

class BoolArray(val ptr: Int) {
    operator fun get(i: Int): Boolean = fila.getBool(ptr + i)
    operator fun set(i: Int, value: Boolean) = fila.setBool(ptr + i, value)
}

class IntVar(val ptr: Int) {
    var value: Int get() = fila.getI32(ptr); set(v) = fila.setI32(ptr, v)
}

class FloatVar(val ptr: Int) {
    var value: Float get() = fila.getF32(ptr); set(v) = fila.setF32(ptr, v)
}

class DoubleVar(val ptr: Int) {
    var value: Double get() = fila.getF64(ptr); set(v) = fila.setF64(ptr, v)
}

class BooleanVar(val ptr: Int) {
    var value: Boolean get() = fila.getBool(ptr); set(v) = fila.setBool(ptr, v)
}

class LongVar(val ptr: Int) {
    var value: Long get() = fila.getI64(ptr); set(v) = fila.setI64(ptr, v)
}

/** Zeroed heap memory that outlives any scope (cinterop's `nativeHeap.alloc`); free with `_free`. */
fun FilamentModule.allocZeroed(size: Int): Int {
    val ptr = _malloc(maxOf(size, 1))
    check(ptr != 0) { "wasm malloc($size) failed" }
    zero(ptr, size)
    return ptr
}
