@file:OptIn(ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaBufferObject
import cnames.structs.FilaBufferObjectBuilder
import kotlin.native.internal.NativePtr

actual class BufferObject internal constructor(internal var nativePtr: CPointer<FilaBufferObject>?) {
    actual class Builder {
        private var builderPtr: CPointer<FilaBufferObjectBuilder>? = null

        init {
            builderPtr = FilaBufferObjectBuilder_create()
        }

        actual enum class BindingType {
            VERTEX,
        }

        actual fun size(byteCount: Int): Builder {
            FilaBufferObjectBuilder_size(builderPtr, byteCount.toUInt())
            return this
        }

        actual fun bindingType(bindingType: BindingType): Builder {
            val type = when (bindingType) {
                BindingType.VERTEX -> FILA_BUFFER_OBJECT_BINDING_VERTEX
            }
            FilaBufferObjectBuilder_bindingType(builderPtr, type)
            return this
        }

        actual fun build(engine: Engine): BufferObject {
            val bufferObjectPtr = FilaBufferObjectBuilder_build(builderPtr, engine.nativeObject.toCPointer())
            FilaBufferObjectBuilder_destroy(builderPtr)
            builderPtr = null
            return BufferObject(bufferObjectPtr)
        }
    }

    actual fun getByteCount(): Int {
        return FilaBufferObject_getByteCount(nativePtr).toInt()
    }

    actual fun setBuffer(engine: Engine, buffer: Any) {
        // buffer is expected to be a native pixel buffer descriptor equivalent. Untyped Any is safely ignored for now.
    }

    actual fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int) {
        // Ignored untyped Any
    }

    actual fun setBuffer(
        engine: Engine,
        buffer: Any,
        destOffsetInBytes: Int,
        count: Int,
        handler: Any?,
        callback: (() -> Unit)?,
    ) {
        // Ignored untyped Any and abstract callback
    }

    actual val nativeObject: Long
        get() = nativePtr?.rawValue?.toLong() ?: 0L

    actual internal fun invalidate() {
        nativePtr = null
    }
}
