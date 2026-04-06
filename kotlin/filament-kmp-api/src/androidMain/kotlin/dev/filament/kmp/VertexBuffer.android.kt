package dev.filament.kmp

import com.google.android.filament.VertexBuffer as AndroidVertexBuffer
import java.nio.Buffer

actual class VertexBuffer internal constructor(val nativeVertexBuffer: AndroidVertexBuffer) {
    actual class Builder actual constructor() {
        private val nativeBuilder = AndroidVertexBuffer.Builder()

        actual fun vertexCount(vertexCount: Int): Builder {
            nativeBuilder.vertexCount(vertexCount)
            return this
        }
        actual fun bufferCount(bufferCount: Int): Builder {
            nativeBuilder.bufferCount(bufferCount)
            return this
        }
        actual fun attribute(attribute: VertexAttribute, bufferIndex: Int, attributeType: AttributeType, byteOffset: Int, byteStride: Int): Builder {
            nativeBuilder.attribute(
                AndroidVertexBuffer.VertexAttribute.values()[attribute.ordinal],
                bufferIndex,
                AndroidVertexBuffer.AttributeType.values()[attributeType.ordinal],
                byteOffset, byteStride
            )
            return this
        }
        actual fun normalized(attribute: VertexAttribute, enabled: Boolean): Builder {
            nativeBuilder.normalized(AndroidVertexBuffer.VertexAttribute.values()[attribute.ordinal], enabled)
            return this
        }
        actual fun build(engine: Engine): VertexBuffer = VertexBuffer(nativeBuilder.build(engine.nativeEngine))
    }

    actual enum class VertexAttribute {
        POSITION, TANGENTS, COLOR, UV0, UV1, BONE_INDICES, BONE_WEIGHTS, UNUSED,
        CUSTOM0, CUSTOM1, CUSTOM2, CUSTOM3, CUSTOM4, CUSTOM5, CUSTOM6, CUSTOM7
    }

    actual enum class AttributeType {
        BYTE, BYTE2, BYTE3, BYTE4,
        UBYTE, UBYTE2, UBYTE3, UBYTE4,
        SHORT, SHORT2, SHORT3, SHORT4,
        USHORT, USHORT2, USHORT3, USHORT4,
        INT, UINT,
        FLOAT, FLOAT2, FLOAT3, FLOAT4,
        HALF, HALF2, HALF3, HALF4
    }

    actual fun getVertexCount(): Int = nativeVertexBuffer.vertexCount
    actual fun setBufferAt(engine: Engine, bufferIndex: Int, buffer: Any) {
        nativeVertexBuffer.setBufferAt(engine.nativeEngine, bufferIndex, buffer as Buffer)
    }
    actual fun setBufferAt(engine: Engine, bufferIndex: Int, buffer: Any, destOffsetInBytes: Int, count: Int) {
        nativeVertexBuffer.setBufferAt(engine.nativeEngine, bufferIndex, buffer as Buffer, destOffsetInBytes, count)
    }
}
