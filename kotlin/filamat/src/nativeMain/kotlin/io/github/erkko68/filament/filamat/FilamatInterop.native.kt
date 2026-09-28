@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.interop.NativePointer
import io.github.erkko68.filament.interop.useCString
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.toCPointer

internal actual fun <R> String.useFilamatCString(block: (NativePointer) -> R): R = useCString(block)

internal actual fun readFilamatBytes(ptr: NativePointer, size: Int): ByteArray = ptr.toCPointer<ByteVar>()!!.readBytes(size)
