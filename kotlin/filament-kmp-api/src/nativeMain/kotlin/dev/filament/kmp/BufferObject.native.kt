@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaBufferObject

actual class BufferObject internal constructor(internal var nativeHandle: CPointer<FilaBufferObject>?) {
    actual enum class BindingType {
        VERTEX,
        UNIFORM,
        SHADER_STORAGE;
        internal fun toNative(): FilaBufferObjectBindingType {
            return when (this) {
                VERTEX -> FILA_BUFFER_OBJECT_BINDING_TYPE_VERTEX
                UNIFORM -> FILA_BUFFER_OBJECT_BINDING_TYPE_UNIFORM
                SHADER_STORAGE -> FILA_BUFFER_OBJECT_BINDING_TYPE_SHADER_STORAGE
            }
        }
    }

    actual class Builder actual constructor() {
        private val nativeBuilder = FilaBufferObjectBuilder_create()

        actual fun size(byteCount: Int): Builder {
            FilaBufferObjectBuilder_size(nativeBuilder, byteCount.toUInt())
            return this
        }

        actual fun bindingType(bindingType: BindingType): Builder {
            FilaBufferObjectBuilder_bindingType(nativeBuilder, bindingType.toNative())
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
        setBuffer(engine, buffer, 0, 0)
    }

    actual fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int) {
        var ptr: CPointer<out CPointed>? = null
        var sizeInBytes: ULong = 0.toULong()

        (buffer as? FloatArray)?.usePinned { pinned ->
            ptr = pinned.addressOf(0)
            sizeInBytes = (buffer.size * 4).toULong()
            FilaBufferObject_setBuffer(nativeHandle, engine.nativeHandle, ptr, sizeInBytes, destOffsetInBytes.toUInt(), null, null, null)
        } ?: (buffer as? ByteArray)?.usePinned { pinned ->
            ptr = pinned.addressOf(0)
            sizeInBytes = buffer.size.toULong()
            FilaBufferObject_setBuffer(nativeHandle, engine.nativeHandle, ptr, sizeInBytes, destOffsetInBytes.toUInt(), null, null, null)
        } ?: (buffer as? ShortArray)?.usePinned { pinned ->
            ptr = pinned.addressOf(0)
            sizeInBytes = (buffer.size * 2).toULong()
            FilaBufferObject_setBuffer(nativeHandle, engine.nativeHandle, ptr, sizeInBytes, destOffsetInBytes.toUInt(), null, null, null)
        } ?: (buffer as? IntArray)?.usePinned { pinned ->
            ptr = pinned.addressOf(0)
            sizeInBytes = (buffer.size * 4).toULong()
            FilaBufferObject_setBuffer(nativeHandle, engine.nativeHandle, ptr, sizeInBytes, destOffsetInBytes.toUInt(), null, null, null)
        }
    }
}
