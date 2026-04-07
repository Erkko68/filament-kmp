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

    private class BufferPinWrapper(val pinned: Pinned<*>, val callback: (() -> Unit)?)

    actual fun setBuffer(engine: Engine, buffer: Any) {
        setBuffer(engine, buffer, 0, 0, null, null)
    }

    actual fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int) {
        setBuffer(engine, buffer, destOffsetInBytes, count, null, null)
    }

    actual fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int, handler: Any?, callback: (() -> Unit)?) {
        var ptr: CPointer<out CPointed>? = null
        var sizeInBytes: ULong = 0.toULong()
        var pinned: Pinned<*>? = null

        when (buffer) {
            is FloatArray -> {
                val p = buffer.pin()
                pinned = p
                ptr = p.addressOf(0)
                sizeInBytes = (buffer.size * 4).toULong()
            }
            is ByteArray -> {
                val p = buffer.pin()
                pinned = p
                ptr = p.addressOf(0)
                sizeInBytes = buffer.size.toULong()
            }
            is ShortArray -> {
                val p = buffer.pin()
                pinned = p
                ptr = p.addressOf(0)
                sizeInBytes = (buffer.size * 2).toULong()
            }
            is IntArray -> {
                val p = buffer.pin()
                pinned = p
                ptr = p.addressOf(0)
                sizeInBytes = (buffer.size * 4).toULong()
            }
        }

        val finalCount = if (count <= 0) sizeInBytes.toUInt() else count.toUInt()

        if (pinned == null && callback == null) {
            FilaBufferObject_setBuffer(nativeHandle, engine.nativeHandle, ptr, sizeInBytes, destOffsetInBytes.toUInt(), null, null, null)
        } else {
            // Even if pin failed (e.g. unsupported type), we still want to handle the callback if it exists
            val wrapper = BufferPinWrapper(pinned ?: ByteArray(0).pin(), callback)
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
}
