@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.github.erkko68.filament.interop

import io.github.erkko68.filament.upcall
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.COpaquePointerVar
import kotlinx.cinterop.FloatVar
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.get
import kotlinx.cinterop.CPointed
import kotlinx.cinterop.Pinned
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.pin
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.toKString
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
    // Apple's targets are all 64-bit.
    actual fun toInterop(pointers: List<NativePointer>): NativePointer = toInterop(pointers.toLongArray())

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

private class PinnedUpload(val pinned: Pinned<ByteArray>, val onRelease: (() -> Unit)?)

// FilaBufferCallback: Filament is done with the pinned array.
private val unpinUpload = staticCFunction { _: COpaquePointer?, _: ULong, userData: COpaquePointer? ->
    val ref = userData!!.asStableRef<PinnedUpload>()
    val upload = ref.get()
    upload.pinned.unpin()
    ref.dispose()
    upcall { upload.onRelease?.invoke() }
}

actual fun upload(data: ByteArray, size: Int, onRelease: (() -> Unit)?): Upload {
    val pinned = data.pin()
    val ref = StableRef.create(PinnedUpload(pinned, onRelease))
    return Upload(pinned.addressOf(0).toLong(), size, unpinUpload.toLong(), ref.asCPointer().toLong())
}

actual fun stringFromInterop(ptr: NativePointer): String? = ptr.toCPointer<ByteVar>()?.toKString()

actual fun readInts(ptr: NativePointer, count: Int): IntArray =
    if (count == 0) IntArray(0) else ptr.toCPointer<IntVar>()!!.let { p -> IntArray(count) { p[it] } }

actual fun readFloats(ptr: NativePointer, count: Int): FloatArray =
    if (count == 0) FloatArray(0) else ptr.toCPointer<FloatVar>()!!.let { p -> FloatArray(count) { p[it] } }

actual fun readPointers(ptr: NativePointer, count: Int): List<NativePointer> =
    if (count == 0) emptyList() else ptr.toCPointer<COpaquePointerVar>()!!.let { p -> List(count) { p[it].toLong() } }

private class Callback(val once: Boolean, val fn: (NativePointer) -> Unit)

private fun dispatch(userData: COpaquePointer?, arg: NativePointer) {
    val ref = userData!!.asStableRef<Callback>()
    val callback = ref.get()
    if (callback.once) ref.dispose()
    upcall { callback.fn(arg) }
}

actual object Callbacks {
    actual fun register(once: Boolean, fn: (arg: NativePointer) -> Unit): NativePointer =
        StableRef.create(Callback(once, fn)).asCPointer().toLong()

    actual fun release(userData: NativePointer) {
        userData.toCPointer<CPointed>()?.asStableRef<Callback>()?.dispose()
    }

    actual val userOnly: NativePointer = staticCFunction { userData: COpaquePointer? -> dispatch(userData, NullPointer) }.toLong()
    actual val argUser: NativePointer = staticCFunction { arg: COpaquePointer?, userData: COpaquePointer? -> dispatch(userData, arg.toLong()) }.toLong()
    actual val keepBuffer: NativePointer = staticCFunction { buffer: COpaquePointer?, _: ULong, userData: COpaquePointer? -> dispatch(userData, buffer.toLong()) }.toLong()
}

actual fun FloatArray.readF32(index: Int): Float = this[index]
