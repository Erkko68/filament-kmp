package dev.filament.kmp

import com.google.android.filament.IndexBuffer as AndroidIndexBuffer
import java.nio.Buffer

actual class IndexBuffer internal constructor(val nativeIndexBuffer: AndroidIndexBuffer) {
    actual class Builder actual constructor() {
        private val nativeBuilder = AndroidIndexBuffer.Builder()
        actual enum class IndexType { USHORT, UINT }
        actual fun indexCount(indexCount: Int): Builder = apply { nativeBuilder.indexCount(indexCount) }
        actual fun bufferType(indexType: IndexType): Builder = apply { 
            nativeBuilder.bufferType(AndroidIndexBuffer.Builder.IndexType.values()[indexType.ordinal]) 
        }
        actual fun build(engine: Engine): IndexBuffer = IndexBuffer(nativeBuilder.build(engine.nativeEngine))
    }

    actual fun getIndexCount(): Int = nativeIndexBuffer.indexCount
    actual fun setBuffer(engine: Engine, buffer: Any) {
        nativeIndexBuffer.setBuffer(engine.nativeEngine, buffer as Buffer)
    }
    actual fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int) {
        nativeIndexBuffer.setBuffer(engine.nativeEngine, buffer as Buffer, destOffsetInBytes, count)
    }
    actual fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int, handler: Any?, callback: (() -> Unit)?) {
        val executor = handler as? java.util.concurrent.Executor ?: Runnable::run
        val runnable = if (callback != null) Runnable { callback() } else null
        nativeIndexBuffer.setBuffer(engine.nativeEngine, buffer as Buffer, destOffsetInBytes, count, executor, runnable)
    }
}
