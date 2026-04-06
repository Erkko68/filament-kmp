@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import platform.posix.*

actual abstract class Buffer(
    internal val pointer: CPointer<out CPointed>,
    internal val elementSize: Int,
    val initialCapacity: Int
) {
    actual var limit: Int = initialCapacity
    actual var position: Int = 0

    actual fun capacity(): Int = initialCapacity
    actual fun limit(): Int = limit
    actual fun limit(newLimit: Int): Buffer {
        limit = newLimit
        return this
    }
    actual fun position(): Int = position
    actual fun position(newPosition: Int): Buffer {
        position = newPosition
        return this
    }
    actual fun remaining(): Int = limit - position
    actual fun hasRemaining(): Boolean = position < limit

    actual fun clear(): Buffer {
        position = 0
        limit = initialCapacity
        return this
    }

    actual fun flip(): Buffer {
        limit = position
        position = 0
        return this
    }

    actual fun rewind(): Buffer {
        position = 0
        return this
    }
    
    // Internal helper for C-interop: returns pointer to current position
    internal fun readDirect(): CPointer<out CPointed>? {
        val bytePtr = pointer.reinterpret<ByteVar>()
        return bytePtr.plus(position * elementSize)
    }

    internal fun byteSize(): Long = (limit * elementSize).toLong()
    internal fun remainingByteSize(): Long = ((limit - position) * elementSize).toLong()
}

actual class ByteBuffer(pointer: CPointer<ByteVar>, capacity: Int) : Buffer(pointer, 1, capacity) {
    actual companion object {
        actual fun allocateDirect(capacity: Int): ByteBuffer {
            val ptr = nativeHeap.allocArray<ByteVar>(capacity)
            return ByteBuffer(ptr, capacity)
        }
    }
}

actual class ShortBuffer(pointer: CPointer<ShortVar>, capacity: Int) : Buffer(pointer, 2, capacity)
actual class IntBuffer(pointer: CPointer<IntVar>, capacity: Int) : Buffer(pointer, 4, capacity)
actual class FloatBuffer(pointer: CPointer<FloatVar>, capacity: Int) : Buffer(pointer, 4, capacity)
