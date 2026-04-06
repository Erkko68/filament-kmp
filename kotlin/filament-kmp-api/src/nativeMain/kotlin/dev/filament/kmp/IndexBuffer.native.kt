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
    
    actual fun setBuffer(engine: Engine, buffer: Any, sizeInBytes: Int) {
        val ptr = buffer as? CPointer<*>
        FilaIndexBuffer_setBuffer(nativeHandle, engine.nativeHandle, ptr, sizeInBytes.toULong(), 0u, null, null, null)
    }

    actual fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, sizeInBytes: Int) {
        val ptr = buffer as? CPointer<*>
        FilaIndexBuffer_setBuffer(nativeHandle, engine.nativeHandle, ptr, sizeInBytes.toULong(), destOffsetInBytes.toUInt(), null, null, null)
    }
}
