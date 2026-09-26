package io.github.erkko68.filament

import io.github.erkko68.filament.wasm.*

actual class BufferObject @InternalFilamentApi constructor(internal var nativeHandle: Int) {
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
            FilaBufferObjectBuilder_size(nativeBuilder, byteCount)
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

    actual val byteCount: Int get() = FilaBufferObject_getByteCount(nativeHandle).toInt()

    actual fun setBuffer(engine: Engine, data: ByteArray) {
        setBuffer(engine, data, 0, 0, null)
    }

    actual fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int) {
        setBuffer(engine, data, destOffsetInBytes, count, null)
    }

    actual fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int, callback: (() -> Unit)?) {
        val upload = fila.upload(data, if (count > 0) count else data.size, callback)
        FilaBufferObject_setBuffer(nativeHandle, engine.nativeHandle, upload.ptr, upload.size, destOffsetInBytes, 0, upload.callback, upload.userData)
    }
}
