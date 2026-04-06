@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaBufferObject

actual class BufferObject internal constructor(internal var nativeHandle: CPointer<FilaBufferObject>?) {
    actual class Builder actual constructor() {
        private val nativeBuilder = FilaBufferObjectBuilder_create()

        actual enum class BindingType { VERTEX }

        actual fun size(byteCount: Int): Builder {
            FilaBufferObjectBuilder_size(nativeBuilder, byteCount.toUInt())
            return this
        }
        actual fun bindingType(bindingType: BindingType): Builder {
            FilaBufferObjectBuilder_bindingType(nativeBuilder, bindingType.ordinal.toUInt())
            return this
        }
        actual fun build(engine: Engine): BufferObject {
            val handle = FilaBufferObjectBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaBufferObjectBuilder_destroy(nativeBuilder)
            return BufferObject(handle)
        }
    }

    actual fun getByteCount(): Int = FilaBufferObject_getByteCount(nativeHandle).toInt()
    
    actual fun setBuffer(engine: Engine, buffer: Any, sizeInBytes: Int) {
        val ptr = buffer as? CPointer<*>
        FilaBufferObject_setBuffer(nativeHandle, engine.nativeHandle, ptr, sizeInBytes.toULong(), 0u, null, null, null)
    }

    actual fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, sizeInBytes: Int) {
        val ptr = buffer as? CPointer<*>
        FilaBufferObject_setBuffer(nativeHandle, engine.nativeHandle, ptr, sizeInBytes.toULong(), destOffsetInBytes.toUInt(), null, null, null)
    }
}
