@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.github.erkko68.filament.interop

import kotlinx.cinterop.Pinned
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.pin
import kotlinx.cinterop.toLong

// Kotlin/Native: externals bind straight to the C symbols; arrays are pinned, not copied.

actual typealias NativePointer = Long

actual val NullPointer: NativePointer = 0L

actual typealias ExternalSymbolName = kotlin.native.SymbolName

actual class InteropScope actual constructor() {
    private val pinned = ArrayList<Pinned<*>>(2)

    actual fun toInterop(array: IntArray?): NativePointer {
        if (array == null || array.isEmpty()) return NullPointer
        return array.pin().also { pinned += it }.addressOf(0).toLong()
    }

    actual fun NativePointer.fromInterop(result: IntArray) {}

    actual fun release() {
        pinned.forEach { it.unpin() }
        pinned.clear()
    }
}
