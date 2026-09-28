package io.github.erkko68.filament.interop

import io.github.erkko68.filament.jni.FilaJni

// JVM + Android: externals are JNI methods; the forwarders are generated from their declarations.

actual typealias NativePointer = Long

actual val NullPointer: NativePointer = 0L

actual class InteropScope actual constructor() {
    private val allocations = ArrayList<Long>(2)

    actual fun toInterop(array: IntArray?): NativePointer {
        if (array == null || array.isEmpty()) return NullPointer
        val ptr = FilaJni.alloc(array.size * 4L)
        allocations += ptr
        FilaJni.buffer(ptr, array.size * 4).asIntBuffer().put(array)
        return ptr
    }

    actual fun NativePointer.fromInterop(result: IntArray) {
        if (this != NullPointer) FilaJni.buffer(this, result.size * 4).asIntBuffer().get(result)
    }

    actual fun release() {
        allocations.forEach(FilaJni::free)
        allocations.clear()
    }
}
