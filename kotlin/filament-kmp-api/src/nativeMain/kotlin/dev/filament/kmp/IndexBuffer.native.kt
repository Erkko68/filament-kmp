@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaIndexBuffer

actual class IndexBuffer internal constructor(internal var nativeHandle: CPointer<FilaIndexBuffer>?) {
    actual class Builder actual constructor() {
        private val nativeBuilder = FilaIndexBufferBuilder_create()

        actual enum class IndexType { USHORT, UINT }

        actual fun indexCount(indexCount: Int): Builder {
            FilaIndexBufferBuilder_indexCount(nativeBuilder, indexCount.toUInt())
            return this
        }
        actual fun bufferType(indexType: IndexType): Builder {
            FilaIndexBufferBuilder_bufferType(nativeBuilder, indexType.ordinal.toUInt())
            return this
        }
        actual fun build(engine: Engine): IndexBuffer {
            val handle = FilaIndexBufferBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaIndexBufferBuilder_destroy(nativeBuilder)
            return IndexBuffer(handle)
        }
    }

    actual fun getIndexCount(): Int = FilaIndexBuffer_getIndexCount(nativeHandle).toInt()
    
    actual fun setBuffer(engine: Engine, buffer: Any) {
        // FIXME: sizeInBytes should be passed or calculated
        FilaIndexBuffer_setBuffer(nativeHandle, engine.nativeHandle, buffer as? CPointer<*>, 0u.toULong(), 0u, null, null, null)
    }

    actual fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int) {
        FilaIndexBuffer_setBuffer(nativeHandle, engine.nativeHandle, buffer as? CPointer<*>, 0u.toULong(), destOffsetInBytes.toUInt(), null, null, null)
    }
}
