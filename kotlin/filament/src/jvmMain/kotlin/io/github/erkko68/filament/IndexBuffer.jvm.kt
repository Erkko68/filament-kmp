package io.github.erkko68.filament

import java.nio.ByteBuffer
import java.nio.ByteOrder

actual class IndexBuffer(val nativeIndexBuffer: io.github.erkko68.filament.jni.IndexBuffer) {
    actual class Builder actual constructor() {
        private val jni = io.github.erkko68.filament.jni.IndexBuffer.Builder()

        actual enum class IndexType {
            USHORT,
            UINT;
            internal fun toJni() = io.github.erkko68.filament.jni.IndexBuffer.Builder.IndexType.values()[ordinal]
        }
        
        actual fun indexCount(indexCount: Int): Builder {
            jni.indexCount(indexCount)
            return this
        }

        actual fun bufferType(indexType: IndexType): Builder {
            val jniValues = io.github.erkko68.filament.jni.IndexBuffer.Builder.IndexType.values()
            jni.bufferType(jniValues[indexType.ordinal])
            return this
        }

        actual fun build(engine: Engine): IndexBuffer =
            IndexBuffer(jni.build(engine.nativeEngine))
    }

    actual val indexCount: Int get() = nativeIndexBuffer.indexCount

    actual fun setBuffer(engine: Engine, data: ByteArray) {
        val buffer = ByteBuffer.allocateDirect(data.size).order(ByteOrder.nativeOrder())
        buffer.put(data)
        buffer.rewind()
        nativeIndexBuffer.setBuffer(engine.nativeEngine, buffer)
    }

    actual fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int) {
        val buffer = ByteBuffer.allocateDirect(data.size).order(ByteOrder.nativeOrder())
        buffer.put(data)
        buffer.rewind()
        nativeIndexBuffer.setBuffer(engine.nativeEngine, buffer, destOffsetInBytes, count)
    }

    actual fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int, callback: (() -> Unit)?) {
        val buffer = ByteBuffer.allocateDirect(data.size).order(ByteOrder.nativeOrder())
        buffer.put(data)
        buffer.rewind()
        val runnable: java.lang.Runnable? = if (callback != null) Runnable { callback() } else null
        nativeIndexBuffer.setBuffer(engine.nativeEngine, buffer, destOffsetInBytes, count, null, runnable)
    }
}
