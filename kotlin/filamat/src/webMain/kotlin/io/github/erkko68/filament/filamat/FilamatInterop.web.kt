package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.interop.NativePointer
import io.github.erkko68.filament.wasm.readBytes
import io.github.erkko68.filament.wasm.writeBytes

internal actual fun <R> String.useFilamatCString(block: (NativePointer) -> R): R {
    val bytes = encodeToByteArray() + 0
    val ptr = filamatWasm._malloc(bytes.size)
    check(ptr != 0) { "filamat wasm malloc(${bytes.size}) failed" }
    try {
        filamatWasm.writeBytes(ptr, bytes)
        return block(ptr)
    } finally {
        filamatWasm._free(ptr)
    }
}

internal actual fun readFilamatBytes(ptr: NativePointer, size: Int): ByteArray = filamatWasm.readBytes(ptr, size)
