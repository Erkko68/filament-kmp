package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.interop.NativePointer
import io.github.erkko68.filament.interop.interopScope
import io.github.erkko68.filament.interop.stringFromInterop
import io.github.erkko68.filament.interop.useCString
import io.github.erkko68.filament.interop.usePinned
import io.github.erkko68.filament.jni.FilaJni

internal actual fun <R> String.useFilamatCString(block: (NativePointer) -> R): R = useCString(block)

internal actual fun readFilamatBytes(ptr: NativePointer, size: Int): ByteArray = ByteArray(size).also { FilaJni.buffer(ptr, size).get(it) }

internal actual fun <R> ByteArray.useFilamatPinned(block: (NativePointer) -> R): R = usePinned(block)

internal actual fun <R> IntArray.useFilamatPinned(block: (NativePointer) -> R): R = usePinned(block)

internal actual fun <R> List<NativePointer>.useFilamatPointers(block: (NativePointer) -> R): R = interopScope { block(toInterop(this@useFilamatPointers)) }

internal actual fun filamatString(ptr: NativePointer): String? = stringFromInterop(ptr)
