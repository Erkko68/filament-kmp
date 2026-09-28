@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.github.erkko68.filament.interop

import kotlinx.cinterop.Pinned
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.pin
import kotlinx.cinterop.toLong

// Kotlin/Native: externals bind straight to the C symbols; arrays are pinned, not copied.

actual typealias NativePointer = Long

actual val NullPointer: NativePointer = 0L

actual val singleThreaded: Boolean = false

actual typealias ExternalSymbolName = kotlin.native.SymbolName

actual class InteropScope actual constructor() {
    private val pinned = ArrayList<Pinned<*>>(2)

    private fun <T : Any> keep(p: Pinned<T>): Pinned<T> = p.also { pinned += it }

    actual fun toInterop(array: ByteArray?): NativePointer = if (array == null || array.isEmpty()) NullPointer else keep(array.pin()).addressOf(0).toLong()
    actual fun toInterop(array: ShortArray?): NativePointer = if (array == null || array.isEmpty()) NullPointer else keep(array.pin()).addressOf(0).toLong()
    actual fun toInterop(array: IntArray?): NativePointer = if (array == null || array.isEmpty()) NullPointer else keep(array.pin()).addressOf(0).toLong()
    actual fun toInterop(array: LongArray?): NativePointer = if (array == null || array.isEmpty()) NullPointer else keep(array.pin()).addressOf(0).toLong()
    actual fun toInterop(array: FloatArray?): NativePointer = if (array == null || array.isEmpty()) NullPointer else keep(array.pin()).addressOf(0).toLong()
    actual fun toInterop(array: DoubleArray?): NativePointer = if (array == null || array.isEmpty()) NullPointer else keep(array.pin()).addressOf(0).toLong()

    // C wrote straight into the pinned array.
    actual fun NativePointer.fromInterop(result: ByteArray) {}
    actual fun NativePointer.fromInterop(result: ShortArray) {}
    actual fun NativePointer.fromInterop(result: IntArray) {}
    actual fun NativePointer.fromInterop(result: LongArray) {}
    actual fun NativePointer.fromInterop(result: FloatArray) {}
    actual fun NativePointer.fromInterop(result: DoubleArray) {}

    actual fun release() {
        pinned.forEach { it.unpin() }
        pinned.clear()
    }
}
