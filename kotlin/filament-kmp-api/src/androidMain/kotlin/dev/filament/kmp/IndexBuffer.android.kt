package dev.filament.kmp

import com.google.android.filament.IndexBuffer as AndroidIndexBuffer
import java.nio.Buffer

actual class IndexBuffer internal constructor(
    internal var androidIndexBuffer: AndroidIndexBuffer?,
) {
    actual fun getIndexCount(): Int {
        val indexBuffer = requireNotNull(androidIndexBuffer) { "Calling method on destroyed IndexBuffer" }
        return indexBuffer.indexCount
    }

    actual fun setBuffer(engine: Engine, buffer: Any) {
        setBuffer(engine, buffer, 0, 0, null, null)
    }

    actual fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int) {
        setBuffer(engine, buffer, destOffsetInBytes, count, null, null)
    }

    actual fun setBuffer(
        engine: Engine,
        buffer: Any,
        destOffsetInBytes: Int,
        count: Int,
        handler: Any?,
        callback: (() -> Unit)?,
    ) {
        val indexBuffer = requireNotNull(androidIndexBuffer) { "Calling method on destroyed IndexBuffer" }
        val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
        indexBuffer.setBuffer(androidEngine, buffer as Buffer, destOffsetInBytes, count, handler, callback?.let { Runnable { it() } })
    }

    actual fun getNativeObject(): Long {
        val indexBuffer = requireNotNull(androidIndexBuffer) { "Calling method on destroyed IndexBuffer" }
        return indexBuffer.nativeObject
    }

    actual internal fun invalidate() {
        androidIndexBuffer = null
    }

    actual class Builder {
        private val androidBuilder = AndroidIndexBuffer.Builder()

        actual fun indexCount(indexCount: Int): Builder {
            androidBuilder.indexCount(indexCount)
            return this
        }

        actual fun bufferType(indexType: IndexType): Builder {
            androidBuilder.bufferType(indexType.toAndroid())
            return this
        }

        actual fun build(engine: Engine): IndexBuffer {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return IndexBuffer(androidBuilder.build(androidEngine))
        }

        actual enum class IndexType {
            USHORT,
            UINT,
        }
    }
}

private fun IndexBuffer.Builder.IndexType.toAndroid(): AndroidIndexBuffer.Builder.IndexType = when (this) {
    IndexBuffer.Builder.IndexType.USHORT -> AndroidIndexBuffer.Builder.IndexType.USHORT
    IndexBuffer.Builder.IndexType.UINT -> AndroidIndexBuffer.Builder.IndexType.UINT
}

