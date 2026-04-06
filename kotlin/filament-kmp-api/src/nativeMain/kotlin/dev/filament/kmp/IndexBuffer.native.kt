package dev.filament.kmp

import kotlinx.cinterop.*
import filament.*

actual class IndexBuffer internal constructor(
    internal val nativeObject: CPointer<FilaIndexBuffer>
) {
    actual enum class IndexType {
        USHORT, UINT;

        internal fun toNative(): FilaIndexBufferType = when (this) {
            USHORT -> FILA_INDEX_BUFFER_TYPE_USHORT
            UINT -> FILA_INDEX_BUFFER_TYPE_UINT
        }
    }

    actual class Builder actual constructor() {
        private val nativeBuilder = FilaIndexBufferBuilder_create()!!

        actual fun indexCount(indexCount: Int): Builder {
            FilaIndexBufferBuilder_indexCount(nativeBuilder, indexCount.toUInt())
            return this
        }

        actual fun bufferType(indexType: IndexType): Builder {
            FilaIndexBufferBuilder_bufferType(nativeBuilder, indexType.toNative())
            return this
        }

        actual fun build(engine: Engine): IndexBuffer {
            val nativeIndexBuffer = FilaIndexBufferBuilder_build(nativeBuilder, engine.nativeObject)
                ?: throw IllegalStateException("Couldn't create IndexBuffer")
            FilaIndexBufferBuilder_destroy(nativeBuilder)
            return IndexBuffer(nativeIndexBuffer)
        }
    }

    actual fun getIndexCount(): Int = FilaIndexBuffer_getIndexCount(nativeObject).toInt()

    actual fun setBuffer(engine: Engine, buffer: Buffer) {
        setBuffer(engine, buffer, 0, 0, null, null)
    }

    actual fun setBuffer(engine: Engine, buffer: Buffer, destOffsetInBytes: Int, count: Int) {
        setBuffer(engine, buffer, destOffsetInBytes, count, null, null)
    }

    actual fun setBuffer(engine: Engine, buffer: Buffer, destOffsetInBytes: Int, count: Int, handler: Any?, callback: Runnable?) {
        val bufferPtr = buffer.nativePointer() ?: return
        val sizeInBytes = if (count > 0) count * buffer.elementSize() else (buffer.limit() - buffer.position()) * buffer.elementSize()

        FilaIndexBuffer_setBuffer(nativeObject, engine.nativeObject, bufferPtr, sizeInBytes.toULong(), destOffsetInBytes.toUInt(), null, null, null)
    }
}
