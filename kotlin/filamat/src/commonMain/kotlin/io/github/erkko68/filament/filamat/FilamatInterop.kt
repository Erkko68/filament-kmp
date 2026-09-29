package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.interop.NativePointer

// On web filamat is its own wasm module with its own heap, so what its C API reads and writes can't go
// through the common interop copies (those land in filament-kmp.wasm). Elsewhere they're the same thing.

internal expect fun <R> String.useFilamatCString(block: (NativePointer) -> R): R

internal expect fun readFilamatBytes(ptr: NativePointer, size: Int): ByteArray

/** The array's address for [block]; what C wrote there is in the array afterwards. */
internal expect fun <R> ByteArray.useFilamatPinned(block: (NativePointer) -> R): R

internal expect fun <R> IntArray.useFilamatPinned(block: (NativePointer) -> R): R

/** An array of these handles, for [block]. */
internal expect fun <R> List<NativePointer>.useFilamatPointers(block: (NativePointer) -> R): R

/** A NUL-terminated string C returned, or null for NullPointer. */
internal expect fun filamatString(ptr: NativePointer): String?
