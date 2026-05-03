package io.github.erkko68.filament

import io.github.erkko68.filament.jni.VertexBuffer as JniVertexBuffer
import java.nio.Buffer

actual class VertexBuffer(val nativeVertexBuffer: JniVertexBuffer) {
    actual enum class VertexAttribute {
        POSITION, TANGENTS, COLOR, UV0, UV1, BONE_INDICES, BONE_WEIGHTS, UNUSED, CUSTOM0, CUSTOM1, CUSTOM2, CUSTOM3, CUSTOM4, CUSTOM5, CUSTOM6, CUSTOM7;
        internal fun toJni() = JniVertexBuffer.VertexAttribute.values()[ordinal]
    }

    actual enum class AttributeType {
        BYTE, BYTE2, BYTE3, BYTE4, UBYTE, UBYTE2, UBYTE3, UBYTE4, SHORT, SHORT2, SHORT3, SHORT4, USHORT, USHORT2, USHORT3, USHORT4, INT, UINT, FLOAT, FLOAT2, FLOAT3, FLOAT4, HALF, HALF2, HALF3, HALF4;
        internal fun toJni() = JniVertexBuffer.AttributeType.values()[ordinal]
    }

    actual class Builder actual constructor() {
        private val jni = JniVertexBuffer.Builder()

        actual fun vertexCount(vertexCount: Int): Builder {
            jni.vertexCount(vertexCount)
            return this
        }

        actual fun bufferCount(bufferCount: Int): Builder {
            jni.bufferCount(bufferCount)
            return this
        }

        actual fun attribute(attribute: VertexAttribute, bufferIndex: Int, attributeType: AttributeType, byteOffset: Int, byteStride: Int): Builder {
            jni.attribute(attribute.toJni(), bufferIndex, attributeType.toJni(), byteOffset, byteStride)
            return this
        }

        actual fun normalized(attribute: VertexAttribute, enabled: Boolean): Builder {
            jni.normalized(attribute.toJni(), enabled)
            return this
        }

        actual fun enableBufferObjects(enabled: Boolean): Builder {
            jni.enableBufferObjects(enabled)
            return this
        }

        actual fun build(engine: Engine): VertexBuffer =
            VertexBuffer(jni.build(engine.nativeEngine))
    }

    actual val vertexCount: Int get() = nativeVertexBuffer.vertexCount

    actual fun setBufferAt(engine: Engine, bufferIndex: Int, data: ByteArray) {
        nativeVertexBuffer.setBufferAt(engine.nativeEngine, bufferIndex, java.nio.ByteBuffer.wrap(data))
    }

    actual fun setBufferAt(engine: Engine, bufferIndex: Int, data: ByteArray, destOffsetInBytes: Int, count: Int) {
        nativeVertexBuffer.setBufferAt(engine.nativeEngine, bufferIndex, java.nio.ByteBuffer.wrap(data), destOffsetInBytes, count)
    }

    actual fun setBufferAt(engine: Engine, bufferIndex: Int, data: ByteArray, destOffsetInBytes: Int, count: Int, callback: (() -> Unit)?) {
        nativeVertexBuffer.setBufferAt(engine.nativeEngine, bufferIndex, java.nio.ByteBuffer.wrap(data), destOffsetInBytes, count, null, callback?.let { Runnable { it() } })
    }

    actual fun setBufferObjectAt(engine: Engine, bufferIndex: Int, bufferObject: BufferObject) {
        nativeVertexBuffer.setBufferObjectAt(engine.nativeEngine, bufferIndex, bufferObject.nativeBufferObject)
    }
}
