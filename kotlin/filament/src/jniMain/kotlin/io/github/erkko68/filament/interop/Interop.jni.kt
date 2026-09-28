package io.github.erkko68.filament.interop

import io.github.erkko68.filament.jni.FilaJni
import io.github.erkko68.filament.jni.upload as jniUpload
import io.github.erkko68.filament.jni.Callbacks as JniCallbacks
import java.nio.ByteBuffer

// JVM + Android: externals are JNI methods; the forwarders are generated from their declarations.

actual typealias NativePointer = Long

actual val NullPointer: NativePointer = 0L

actual val singleThreaded: Boolean = false

actual class InteropScope actual constructor() {
    private val allocations = ArrayList<Long>(2)

    private inline fun copyIn(count: Int, width: Int, put: (ByteBuffer) -> Unit): NativePointer {
        if (count == 0) return NullPointer
        val ptr = FilaJni.alloc(count.toLong() * width)
        check(ptr != 0L) { "calloc(${count * width}) failed" }
        allocations += ptr
        put(FilaJni.buffer(ptr, count * width))
        return ptr
    }

    actual fun toInterop(array: ByteArray?): NativePointer = copyIn(array?.size ?: 0, 1) { it.put(array!!) }
    actual fun toInterop(array: ShortArray?): NativePointer = copyIn(array?.size ?: 0, 2) { it.asShortBuffer().put(array!!) }
    actual fun toInterop(array: IntArray?): NativePointer = copyIn(array?.size ?: 0, 4) { it.asIntBuffer().put(array!!) }
    actual fun toInterop(array: LongArray?): NativePointer = copyIn(array?.size ?: 0, 8) { it.asLongBuffer().put(array!!) }
    actual fun toInterop(array: FloatArray?): NativePointer = copyIn(array?.size ?: 0, 4) { it.asFloatBuffer().put(array!!) }
    actual fun toInterop(array: DoubleArray?): NativePointer = copyIn(array?.size ?: 0, 8) { it.asDoubleBuffer().put(array!!) }

    private inline fun NativePointer.copyOut(count: Int, width: Int, get: (ByteBuffer) -> Unit) {
        if (this != NullPointer && count > 0) get(FilaJni.buffer(this, count * width))
    }

    actual fun NativePointer.fromInterop(result: ByteArray) = copyOut(result.size, 1) { it.get(result) }
    actual fun NativePointer.fromInterop(result: ShortArray) = copyOut(result.size, 2) { it.asShortBuffer().get(result) }
    actual fun NativePointer.fromInterop(result: IntArray) = copyOut(result.size, 4) { it.asIntBuffer().get(result) }
    actual fun NativePointer.fromInterop(result: LongArray) = copyOut(result.size, 8) { it.asLongBuffer().get(result) }
    actual fun NativePointer.fromInterop(result: FloatArray) = copyOut(result.size, 4) { it.asFloatBuffer().get(result) }
    actual fun NativePointer.fromInterop(result: DoubleArray) = copyOut(result.size, 8) { it.asDoubleBuffer().get(result) }

    actual fun release() {
        allocations.forEach(FilaJni::free)
        allocations.clear()
    }
}

actual fun upload(data: ByteArray, size: Int, onRelease: (() -> Unit)?): Upload =
    jniUpload(data, size, onRelease).let { Upload(it.ptr, it.size, it.callback, it.userData) }

actual fun stringFromInterop(ptr: NativePointer): String? = FilaJni.readString(ptr)

actual object Callbacks {
    actual fun register(once: Boolean, fn: (arg: NativePointer) -> Unit): NativePointer = JniCallbacks.register(once) { a, _ -> fn(a) }
    actual fun release(userData: NativePointer) = JniCallbacks.release(userData)
    actual val userOnly: NativePointer get() = JniCallbacks.userOnly
    actual val argUser: NativePointer get() = JniCallbacks.argUser
    actual val keepBuffer: NativePointer get() = JniCallbacks.keepBuffer
}

actual fun FloatArray.readF32(index: Int): Float = this[index]
