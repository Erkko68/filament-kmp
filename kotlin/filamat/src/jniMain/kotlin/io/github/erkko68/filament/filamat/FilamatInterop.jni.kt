package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.interop.NativePointer
import io.github.erkko68.filament.interop.useCString
import io.github.erkko68.filament.jni.FilaJni

internal actual fun <R> String.useFilamatCString(block: (NativePointer) -> R): R = useCString(block)

internal actual fun readFilamatBytes(ptr: NativePointer, size: Int): ByteArray = ByteArray(size).also { FilaJni.buffer(ptr, size).get(it) }
