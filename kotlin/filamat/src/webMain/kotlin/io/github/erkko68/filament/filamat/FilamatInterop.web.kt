package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.interop.NativePointer
import io.github.erkko68.filament.interop.NullPointer
import io.github.erkko68.filament.wasm.readBytes
import io.github.erkko68.filament.wasm.readInts
import io.github.erkko68.filament.wasm.readString
import io.github.erkko68.filament.wasm.writeBytes
import io.github.erkko68.filament.wasm.writeInts

internal actual fun <R> String.useFilamatCString(block: (NativePointer) -> R): R = (encodeToByteArray() + 0).useFilamatPinned(block)

internal actual fun readFilamatBytes(ptr: NativePointer, size: Int): ByteArray = filamatWasm.readBytes(ptr, size)

internal actual fun <R> ByteArray.useFilamatPinned(block: (NativePointer) -> R): R =
    filamatMemory(size) { p -> filamatWasm.writeBytes(p, this); block(p).also { if (p != NullPointer) filamatWasm.readBytes(p, size).copyInto(this) } }

internal actual fun <R> IntArray.useFilamatPinned(block: (NativePointer) -> R): R =
    filamatMemory(size * 4) { p -> filamatWasm.writeInts(p, this); block(p).also { if (p != NullPointer) filamatWasm.readInts(p, size, this) } }

// wasm32 pointers are Ints.
internal actual fun <R> List<NativePointer>.useFilamatPointers(block: (NativePointer) -> R): R = toIntArray().useFilamatPinned(block)

internal actual fun filamatString(ptr: NativePointer): String? = filamatWasm.readString(ptr)

/** [bytes] of filamat-kmp.wasm's heap for [block] (NullPointer for none). */
private inline fun <R> filamatMemory(bytes: Int, block: (NativePointer) -> R): R {
    if (bytes == 0) return block(NullPointer)
    val ptr = filamatWasm._malloc(bytes)
    check(ptr != 0) { "filamat wasm malloc($bytes) failed" }
    try {
        return block(ptr)
    } finally {
        filamatWasm._free(ptr)
    }
}
