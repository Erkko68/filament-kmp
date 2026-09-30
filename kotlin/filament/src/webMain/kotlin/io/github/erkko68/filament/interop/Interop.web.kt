package io.github.erkko68.filament.interop

import io.github.erkko68.filament.wasm.fila
import io.github.erkko68.filament.wasm.normalizeF32
import io.github.erkko68.filament.wasm.getI64
import io.github.erkko68.filament.wasm.getU16
import io.github.erkko68.filament.wasm.readBytes
import io.github.erkko68.filament.wasm.readDoubles
import io.github.erkko68.filament.wasm.readFloats
import io.github.erkko68.filament.wasm.readInts
import io.github.erkko68.filament.wasm.setI64
import io.github.erkko68.filament.wasm.setU16
import io.github.erkko68.filament.wasm.upload as wasmUpload
import io.github.erkko68.filament.wasm.readString
import io.github.erkko68.filament.wasm.Callbacks as WasmCallbacks
import io.github.erkko68.filament.wasm.writeBytes
import io.github.erkko68.filament.wasm.writeDoubles
import io.github.erkko68.filament.wasm.writeFloats
import io.github.erkko68.filament.wasm.writeInts

// Web: externals resolve to the `Fila*` globals :web installs from the wasm module's exports;
// arrays are copied into the wasm heap.

actual typealias NativePointer = Int

actual val NullPointer: NativePointer = 0

actual val singleThreaded: Boolean = true

actual class InteropScope actual constructor() {
    private val allocations = ArrayList<Int>(2)

    private inline fun copyIn(count: Int, width: Int, write: (ptr: Int) -> Unit): NativePointer {
        if (count == 0) return NullPointer
        val ptr = fila._malloc(count * width)
        check(ptr != 0) { "wasm malloc(${count * width}) failed" }
        allocations += ptr
        write(ptr)
        return ptr
    }

    actual fun toInterop(array: ByteArray?): NativePointer = copyIn(array?.size ?: 0, 1) { fila.writeBytes(it, array!!) }
    actual fun toInterop(array: ShortArray?): NativePointer = copyIn(array?.size ?: 0, 2) { p -> array!!.forEachIndexed { i, v -> fila.setU16(p + 2 * i, v.toInt()) } }
    actual fun toInterop(array: IntArray?): NativePointer = copyIn(array?.size ?: 0, 4) { fila.writeInts(it, array!!) }
    actual fun toInterop(array: LongArray?): NativePointer = copyIn(array?.size ?: 0, 8) { p -> array!!.forEachIndexed { i, v -> fila.setI64(p + 8 * i, v) } }
    actual fun toInterop(array: FloatArray?): NativePointer = copyIn(array?.size ?: 0, 4) { fila.writeFloats(it, array!!) }
    actual fun toInterop(array: DoubleArray?): NativePointer = copyIn(array?.size ?: 0, 8) { fila.writeDoubles(it, array!!) }
    // wasm32 pointers are Ints.
    actual fun toInterop(pointers: List<NativePointer>): NativePointer = toInterop(pointers.toIntArray())

    actual fun NativePointer.fromInterop(result: ByteArray) { if (this != NullPointer) fila.readBytes(this, result.size).copyInto(result) }
    actual fun NativePointer.fromInterop(result: ShortArray) { if (this != NullPointer) for (i in result.indices) result[i] = fila.getU16(this + 2 * i).toShort() }
    actual fun NativePointer.fromInterop(result: IntArray) { if (this != NullPointer) fila.readInts(this, result.size, result) }
    actual fun NativePointer.fromInterop(result: LongArray) { if (this != NullPointer) for (i in result.indices) result[i] = fila.getI64(this + 8 * i) }
    actual fun NativePointer.fromInterop(result: FloatArray) { if (this != NullPointer) fila.readFloats(this, result.size, result) }
    actual fun NativePointer.fromInterop(result: DoubleArray) { if (this != NullPointer) fila.readDoubles(this, result.size, result) }

    actual fun release() {
        allocations.forEach { fila._free(it) }
        allocations.clear()
    }
}

actual fun upload(data: ByteArray, size: Int, onRelease: (() -> Unit)?): Upload =
    fila.wasmUpload(data, size, onRelease).let { Upload(it.ptr, it.size, it.callback, it.userData) }

actual fun stringFromInterop(ptr: NativePointer): String? = fila.readString(ptr)

actual fun readInts(ptr: NativePointer, count: Int): IntArray = if (count == 0) IntArray(0) else fila.readInts(ptr, count)

actual fun readFloats(ptr: NativePointer, count: Int): FloatArray = if (count == 0) FloatArray(0) else fila.readFloats(ptr, count)

actual fun readPointers(ptr: NativePointer, count: Int): List<NativePointer> = readInts(ptr, count).toList()

actual object Callbacks {
    actual fun register(once: Boolean, fn: (arg: NativePointer) -> Unit): NativePointer = WasmCallbacks.register(once) { a, _ -> fn(a) }
    actual fun release(userData: NativePointer) = WasmCallbacks.release(userData)
    actual val userOnly: NativePointer get() = WasmCallbacks.userOnly
    actual val argUser: NativePointer get() = WasmCallbacks.argUser
    actual val keepBuffer: NativePointer get() = WasmCallbacks.keepBuffer
}

actual fun FloatArray.readF32(index: Int): Float = normalizeF32(this[index])
