@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*
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

    private class BufferPinWrapper(val pinned: Pinned<*>, val callback: (() -> Unit)?)

    actual fun setBuffer(engine: Engine, data: ByteArray) {
        setBuffer(engine, data, 0, 0, null)
    }

    actual fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int) {
        setBuffer(engine, data, destOffsetInBytes, count, null)
    }

    actual fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int, callback: (() -> Unit)?) {
        val pinned = data.pin()
        val ptr = pinned.addressOf(0).reinterpret<ByteVar>()
        val sizeInBytes = if (count > 0) count.toULong() else data.size.toULong()

        val wrapper = BufferPinWrapper(pinned, callback)
        val stableRef = StableRef.create(wrapper)
        val callbackWrapper = staticCFunction { _: COpaquePointer?, _: ULong, user: COpaquePointer? ->
            val ref = user!!.asStableRef<BufferPinWrapper>()
            val wrap = ref.get()
            wrap.callback?.invoke()
            wrap.pinned.unpin()
            ref.dispose()
        }
        FilaBufferObject_setBuffer(nativeHandle, engine.nativeHandle, ptr, sizeInBytes, destOffsetInBytes.toUInt(), null, callbackWrapper, stableRef.asCPointer())
    }
}
