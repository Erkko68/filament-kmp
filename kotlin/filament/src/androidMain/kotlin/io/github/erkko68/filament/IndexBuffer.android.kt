package io.github.erkko68.filament

import com.google.android.filament.IndexBuffer as AndroidIndexBuffer
import java.nio.Buffer

actual class IndexBuffer internal constructor(val nativeIndexBuffer: AndroidIndexBuffer) {
    actual class Builder actual constructor() {
        private val nativeBuilder = AndroidIndexBuffer.Builder()
        actual enum class IndexType { USHORT, UINT }
        actual fun indexCount(indexCount: Int): Builder = apply { nativeBuilder.indexCount(indexCount) }
        actual fun bufferType(indexType: IndexType): Builder = apply { 
            nativeBuilder.bufferType(AndroidIndexBuffer.Builder.IndexType.entries[indexType.ordinal])
        }
        actual fun build(engine: Engine): IndexBuffer = IndexBuffer(nativeBuilder.build(engine.nativeEngine))
    }

    actual val indexCount: Int get() = nativeIndexBuffer.indexCount
    actual fun setBuffer(engine: Engine, data: ByteArray) {
        val byteBuffer = java.nio.ByteBuffer.allocateDirect(data.size).apply {
            order(java.nio.ByteOrder.nativeOrder())
            put(data)
            flip()
        }
        nativeIndexBuffer.setBuffer(engine.nativeEngine, byteBuffer)
    }
    actual fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int) {
        val byteBuffer = java.nio.ByteBuffer.allocateDirect(data.size).apply {
            order(java.nio.ByteOrder.nativeOrder())
            put(data)
            flip()
        }
        nativeIndexBuffer.setBuffer(engine.nativeEngine, byteBuffer, destOffsetInBytes, count)
    }
    actual fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int, callback: (() -> Unit)?) {
        val runnable = if (callback != null) Runnable { callback() } else null
        val byteBuffer = java.nio.ByteBuffer.allocateDirect(data.size).apply {
            order(java.nio.ByteOrder.nativeOrder())
            put(data)
            flip()
        }
        nativeIndexBuffer.setBuffer(engine.nativeEngine, byteBuffer, destOffsetInBytes, count, Runnable::run, runnable)
    }
}
