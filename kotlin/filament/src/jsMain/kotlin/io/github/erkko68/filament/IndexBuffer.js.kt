package io.github.erkko68.filament

import io.github.erkko68.filament.js.IndexBuffer as JSIndexBuffer
import io.github.erkko68.filament.js.`IndexBuffer_Builder` as JSIndexBufferBuilder
import io.github.erkko68.filament.js.IndexBuffer_IndexType

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual class IndexBuffer(internal val jsIndexBuffer: JSIndexBuffer) {
    actual fun getIndexCount(): Int {
        return jsIndexBuffer.getIndexCount().toInt()
    }

    actual fun setBuffer(engine: Engine, data: ByteArray) {
        jsIndexBuffer.setBuffer(engine.jsEngine, data.asDynamic())
    }

    actual fun setBuffer(
        engine: Engine,
        data: ByteArray,
        destOffsetInBytes: Int,
        count: Int
    ) {
        val clippedData = if (count < data.size) data.sliceArray(0 until count) else data
        jsIndexBuffer.setBuffer(engine.jsEngine, clippedData.asDynamic(), destOffsetInBytes)
    }

    actual fun setBuffer(
        engine: Engine,
        data: ByteArray,
        destOffsetInBytes: Int,
        count: Int,
        handler: Any?,
        callback: (() -> Unit)?
    ) {
        val clippedData = if (count < data.size) data.sliceArray(0 until count) else data
        jsIndexBuffer.setBuffer(engine.jsEngine, clippedData.asDynamic(), destOffsetInBytes)
        callback?.invoke()
    }

    actual class Builder {
        private val jsBuilder: JSIndexBufferBuilder = JSIndexBufferBuilder()

        actual fun indexCount(indexCount: Int): Builder {
            jsBuilder.indexCount(indexCount)
            return this
        }

        actual fun bufferType(indexType: IndexType): Builder {
            val jsType = when (indexType) {
                IndexType.USHORT -> IndexBuffer_IndexType.USHORT
                IndexType.UINT -> IndexBuffer_IndexType.UINT
            }
            jsBuilder.bufferType(jsType)
            return this
        }

        actual fun build(engine: Engine): IndexBuffer {
            return IndexBuffer(jsBuilder.build(engine.jsEngine))
        }

        actual enum class IndexType { USHORT, UINT }
    }
}