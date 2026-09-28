package io.github.erkko68.filament

import io.github.erkko68.filament.jni.*

actual class IndexBuffer @InternalFilamentApi constructor(internal var nativeHandle: Long) {
    actual class Builder actual constructor() {
        private val nativeBuilder = FilaIndexBufferBuilder_create()
        actual enum class IndexType { USHORT, UINT }
        actual fun indexCount(indexCount: Int): Builder = apply { FilaIndexBufferBuilder_indexCount(nativeBuilder, indexCount) }
        actual fun bufferType(indexType: IndexType): Builder = apply { 
            FilaIndexBufferBuilder_bufferType(nativeBuilder, indexType.ordinal) 
        }
        actual fun build(engine: Engine): IndexBuffer = IndexBuffer(FilaIndexBufferBuilder_build(nativeBuilder, engine.nativeHandle))
    }

    actual val indexCount: Int get() = FilaIndexBuffer_getIndexCount(nativeHandle).toInt()
    
    actual fun setBuffer(engine: Engine, data: ByteArray) {
        setBuffer(engine, data, 0, 0, null)
    }

    actual fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int) {
        setBuffer(engine, data, destOffsetInBytes, count, null)
    }

    actual fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int, callback: (() -> Unit)?) {
        val upload = upload(data, if (count > 0) count else data.size, callback)
        FilaIndexBuffer_setBuffer(nativeHandle, engine.nativeHandle, upload.ptr, upload.size.toLong(), destOffsetInBytes, 0, upload.callback, upload.userData)
    }
}
