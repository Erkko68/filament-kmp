package dev.filament.kmp

import com.google.android.filament.VertexBuffer as AndroidVertexBuffer
import java.nio.Buffer

actual class VertexBuffer internal constructor(
    internal var androidVertexBuffer: AndroidVertexBuffer?,
) {
    actual fun getVertexCount(): Int {
        val vertexBuffer = requireNotNull(androidVertexBuffer) { "Calling method on destroyed VertexBuffer" }
        return vertexBuffer.vertexCount
    }

    actual fun setBufferAt(engine: Engine, bufferIndex: Int, buffer: Any) {
        setBufferAt(engine, bufferIndex, buffer, 0, 0, null, null)
    }

    actual fun setBufferAt(engine: Engine, bufferIndex: Int, buffer: Any, destOffsetInBytes: Int, count: Int) {
        setBufferAt(engine, bufferIndex, buffer, destOffsetInBytes, count, null, null)
    }

    actual fun setBufferAt(
        engine: Engine,
        bufferIndex: Int,
        buffer: Any,
        destOffsetInBytes: Int,
        count: Int,
        handler: Any?,
        callback: (() -> Unit)?,
    ) {
        val vertexBuffer = requireNotNull(androidVertexBuffer) { "Calling method on destroyed VertexBuffer" }
        val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
        vertexBuffer.setBufferAt(
            androidEngine,
            bufferIndex,
            buffer as Buffer,
            destOffsetInBytes,
            count,
            handler,
            callback?.let { Runnable { it() } },
        )
    }

    actual fun setBufferObjectAt(engine: Engine, bufferIndex: Int, bufferObject: BufferObject) {
        val vertexBuffer = requireNotNull(androidVertexBuffer) { "Calling method on destroyed VertexBuffer" }
        val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
        val androidBufferObject = requireNotNull(bufferObject.androidBufferObject) { "Calling method on destroyed BufferObject" }
        vertexBuffer.setBufferObjectAt(androidEngine, bufferIndex, androidBufferObject)
    }

    actual fun getNativeObject(): Long {
        val vertexBuffer = requireNotNull(androidVertexBuffer) { "Calling method on destroyed VertexBuffer" }
        return vertexBuffer.nativeObject
    }

    actual internal fun invalidate() {
        androidVertexBuffer = null
    }

    actual class Builder {
        private val androidBuilder = AndroidVertexBuffer.Builder()

        actual fun vertexCount(vertexCount: Int): Builder {
            androidBuilder.vertexCount(vertexCount)
            return this
        }

        actual fun enableBufferObjects(enabled: Boolean): Builder {
            androidBuilder.enableBufferObjects(enabled)
            return this
        }

        actual fun bufferCount(bufferCount: Int): Builder {
            androidBuilder.bufferCount(bufferCount)
            return this
        }

        actual fun attribute(
            attribute: VertexAttribute,
            bufferIndex: Int,
            attributeType: AttributeType,
            byteOffset: Int,
            byteStride: Int,
        ): Builder {
            androidBuilder.attribute(attribute.toAndroid(), bufferIndex, attributeType.toAndroid(), byteOffset, byteStride)
            return this
        }

        actual fun attribute(attribute: VertexAttribute, bufferIndex: Int, attributeType: AttributeType): Builder {
            androidBuilder.attribute(attribute.toAndroid(), bufferIndex, attributeType.toAndroid())
            return this
        }

        actual fun normalized(attribute: VertexAttribute): Builder {
            androidBuilder.normalized(attribute.toAndroid())
            return this
        }

        actual fun normalized(attribute: VertexAttribute, enabled: Boolean): Builder {
            androidBuilder.normalized(attribute.toAndroid(), enabled)
            return this
        }

        actual fun build(engine: Engine): VertexBuffer {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return VertexBuffer(androidBuilder.build(androidEngine))
        }
    }

    actual enum class VertexAttribute {
        POSITION,
        TANGENTS,
        COLOR,
        UV0,
        UV1,
        BONE_INDICES,
        BONE_WEIGHTS,
        UNUSED,
        CUSTOM0,
        CUSTOM1,
        CUSTOM2,
        CUSTOM3,
        CUSTOM4,
        CUSTOM5,
        CUSTOM6,
        CUSTOM7,
    }

    actual enum class AttributeType {
        BYTE,
        BYTE2,
        BYTE3,
        BYTE4,
        UBYTE,
        UBYTE2,
        UBYTE3,
        UBYTE4,
        SHORT,
        SHORT2,
        SHORT3,
        SHORT4,
        USHORT,
        USHORT2,
        USHORT3,
        USHORT4,
        INT,
        UINT,
        FLOAT,
        FLOAT2,
        FLOAT3,
        FLOAT4,
        HALF,
        HALF2,
        HALF3,
        HALF4,
    }
}

private fun VertexBuffer.VertexAttribute.toAndroid(): AndroidVertexBuffer.VertexAttribute =
    AndroidVertexBuffer.VertexAttribute.valueOf(name)

private fun VertexBuffer.AttributeType.toAndroid(): AndroidVertexBuffer.AttributeType =
    AndroidVertexBuffer.AttributeType.valueOf(name)

