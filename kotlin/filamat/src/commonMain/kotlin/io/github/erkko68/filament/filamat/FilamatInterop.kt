package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.interop.NativePointer

// On web filamat is its own wasm module with its own heap, so its strings and package bytes can't go
// through the common interop copies (those land in filament-kmp.wasm). Elsewhere they're the same thing.

internal expect fun <R> String.useFilamatCString(block: (NativePointer) -> R): R

internal expect fun readFilamatBytes(ptr: NativePointer, size: Int): ByteArray
