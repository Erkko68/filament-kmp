@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaIndexBuffer

actual class IndexBuffer internal constructor(internal var nativeHandle: CPointer<FilaIndexBuffer>?) {
    actual class Builder actual constructor() {
        private val nativeBuilder = FilaIndexBufferBuilder_create()
        actual enum class IndexType { USHORT, UINT }
        actual fun indexCount(indexCount: Int): Builder = apply { FilaIndexBufferBuilder_indexCount(nativeBuilder, indexCount.toUInt()) }
        actual fun bufferType(indexType: IndexType): Builder = apply { 
            FilaIndexBufferBuilder_bufferType(nativeBuilder, indexType.ordinal.toUInt()) 
        }
        actual fun build(engine: Engine): IndexBuffer = IndexBuffer(FilaIndexBufferBuilder_build(nativeBuilder, engine.nativeHandle))
    }

    actual fun getIndexCount(): Int = FilaIndexBuffer_getIndexCount(nativeHandle).toInt()
    
    actual fun setBuffer(engine: Engine, buffer: Any) {
        setBuffer(engine, buffer, 0, 0, null, null)
    }

    actual fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int) {
        setBuffer(engine, buffer, destOffsetInBytes, count, null, null)
    }

    actual fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int, handler: Any?, callback: (() -> Unit)?) {
        val ptr = buffer as? CPointer<*>
        // Callback bridging not yet implemented for Native
        FilaIndexBuffer_setBuffer(nativeHandle, engine.nativeHandle, ptr, 0.toULong(), destOffsetInBytes.toUInt(), null, null, null)
    }
}
