package dev.filament.kmp

import com.google.android.filament.IndexBuffer as AndroidIndexBuffer
import java.nio.Buffer

actual class IndexBuffer internal constructor(val nativeIndexBuffer: AndroidIndexBuffer) {
    actual class Builder actual constructor() {
        private val nativeBuilder = AndroidIndexBuffer.Builder()

        actual enum class IndexType { USHORT, UINT }

        actual fun indexCount(indexCount: Int): Builder {
            nativeBuilder.indexCount(indexCount)
            return this
        }
        actual fun bufferType(indexType: IndexType): Builder {
            nativeBuilder.bufferType(AndroidIndexBuffer.Builder.IndexType.values()[indexType.ordinal])
            return this
        }
        actual fun build(engine: Engine): IndexBuffer = IndexBuffer(nativeBuilder.build(engine.nativeEngine))
    }

    actual fun getIndexCount(): Int = nativeIndexBuffer.indexCount
    actual fun setBuffer(engine: Engine, buffer: Any, sizeInBytes: Int) {
        nativeIndexBuffer.setBuffer(engine.nativeEngine, buffer as Buffer, 0, sizeInBytes)
    }
    actual fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, sizeInBytes: Int) {
        nativeIndexBuffer.setBuffer(engine.nativeEngine, buffer as Buffer, destOffsetInBytes, sizeInBytes)
    }
}
