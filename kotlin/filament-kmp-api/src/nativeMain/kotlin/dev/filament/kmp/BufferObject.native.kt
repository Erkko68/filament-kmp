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
    
    actual fun setBuffer(engine: Engine, buffer: Any) {
        // FIXME: sizeInBytes should be passed or calculated
        FilaBufferObject_setBuffer(nativeHandle, engine.nativeHandle, buffer as? CPointer<*>, 0u.toULong(), 0u, null, null, null)
    }

    actual fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int) {
        FilaBufferObject_setBuffer(nativeHandle, engine.nativeHandle, buffer as? CPointer<*>, 0u.toULong(), destOffsetInBytes.toUInt(), null, null, null)
    }
}
