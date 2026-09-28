package io.github.erkko68.filament.interop

import io.github.erkko68.filament.wasm.fila
import io.github.erkko68.filament.wasm.readInts
import io.github.erkko68.filament.wasm.writeInts

// Web: externals resolve to the `Fila*` globals :web installs from the wasm module's exports;
// arrays are copied into the wasm heap.

actual typealias NativePointer = Int

actual val NullPointer: NativePointer = 0

actual class InteropScope actual constructor() {
    private val allocations = ArrayList<Int>(2)

    actual fun toInterop(array: IntArray?): NativePointer {
        if (array == null || array.isEmpty()) return NullPointer
        val ptr = fila._malloc(array.size * 4)
        check(ptr != 0) { "wasm malloc(${array.size * 4}) failed" }
        allocations += ptr
        fila.writeInts(ptr, array)
        return ptr
    }

    actual fun NativePointer.fromInterop(result: IntArray) {
        if (this != NullPointer) fila.readInts(this, result.size, result)
    }

    actual fun release() {
        allocations.forEach { fila._free(it) }
        allocations.clear()
    }
}
