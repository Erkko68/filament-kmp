package io.github.erkko68.filament

import io.github.erkko68.filament.js.VertexBuffer as JSVertexBuffer
import io.github.erkko68.filament.js.`VertexBuffer_Builder` as JSVertexBufferBuilder
import io.github.erkko68.filament.js.VertexAttribute as JSVertexAttribute
import io.github.erkko68.filament.js.VertexBuffer_AttributeType as JSVertexBufferAttributeType

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual class VertexBuffer(internal val jsVertexBuffer: JSVertexBuffer) {
    actual fun getVertexCount(): Int {
        return jsVertexBuffer.getVertexCount().toInt()
    }

    actual fun setBufferAt(engine: Engine, bufferIndex: Int, data: ByteArray) {
        jsVertexBuffer.setBufferAt(engine.jsEngine, bufferIndex, data.asDynamic())
    }

    actual fun setBufferAt(
        engine: Engine,
        bufferIndex: Int,
        data: ByteArray,
        destOffsetInBytes: Int,
        count: Int
    ) {
        val clippedData = if (count < data.size) data.sliceArray(0 until count) else data
        jsVertexBuffer.setBufferAt(engine.jsEngine, bufferIndex, clippedData.asDynamic(), destOffsetInBytes)
    }

    actual fun setBufferAt(
        engine: Engine,
        bufferIndex: Int,
        data: ByteArray,
        destOffsetInBytes: Int,
        count: Int,
        handler: Any?,
        callback: (() -> Unit)?
    ) {
        val clippedData = if (count < data.size) data.sliceArray(0 until count) else data
        jsVertexBuffer.setBufferAt(engine.jsEngine, bufferIndex, clippedData.asDynamic(), destOffsetInBytes)
        callback?.invoke()
    }

    actual fun setBufferObjectAt(
        engine: Engine,
        bufferIndex: Int,
        bufferObject: BufferObject
    ) {
    }

    actual enum class VertexAttribute { POSITION, TANGENTS, COLOR, UV0, UV1, BONE_INDICES, BONE_WEIGHTS, UNUSED, CUSTOM0, CUSTOM1, CUSTOM2, CUSTOM3, CUSTOM4, CUSTOM5, CUSTOM6, CUSTOM7 }
    actual enum class AttributeType { BYTE, BYTE2, BYTE3, BYTE4, UBYTE, UBYTE2, UBYTE3, UBYTE4, SHORT, SHORT2, SHORT3, SHORT4, USHORT, USHORT2, USHORT3, USHORT4, INT, UINT, FLOAT, FLOAT2, FLOAT3, FLOAT4, HALF, HALF2, HALF3, HALF4 }
    
    actual class Builder {
        private val jsBuilder: JSVertexBufferBuilder = JSVertexBufferBuilder()

        actual fun vertexCount(vertexCount: Int): Builder {
            jsBuilder.vertexCount(vertexCount)
            return this
        }

        actual fun enableBufferObjects(enabled: Boolean): Builder {
            return this
        }

        actual fun bufferCount(bufferCount: Int): Builder {
            jsBuilder.bufferCount(bufferCount)
            return this
        }

        actual fun attribute(
            attribute: VertexAttribute,
            bufferIndex: Int,
            attributeType: AttributeType,
            byteOffset: Int,
            byteStride: Int
        ): Builder {
            jsBuilder.attribute(mapAttribute(attribute), bufferIndex, mapAttributeType(attributeType), byteOffset, byteStride)
            return this
        }

        actual fun normalized(
            attribute: VertexAttribute,
            enabled: Boolean
        ): Builder {
            jsBuilder.normalized(mapAttribute(attribute), enabled)
            return this
        }

        actual fun build(engine: Engine): VertexBuffer {
            return VertexBuffer(jsBuilder.build(engine.jsEngine))
        }
    }
}

private fun mapAttribute(attr: VertexBuffer.VertexAttribute): JSVertexAttribute {
    return when (attr) {
        VertexBuffer.VertexAttribute.POSITION -> JSVertexAttribute.POSITION
        VertexBuffer.VertexAttribute.TANGENTS -> JSVertexAttribute.TANGENTS
        VertexBuffer.VertexAttribute.COLOR -> JSVertexAttribute.COLOR
        VertexBuffer.VertexAttribute.UV0 -> JSVertexAttribute.UV0
        VertexBuffer.VertexAttribute.UV1 -> JSVertexAttribute.UV1
        VertexBuffer.VertexAttribute.BONE_INDICES -> JSVertexAttribute.BONE_INDICES
        VertexBuffer.VertexAttribute.BONE_WEIGHTS -> JSVertexAttribute.BONE_WEIGHTS
        VertexBuffer.VertexAttribute.CUSTOM0 -> JSVertexAttribute.CUSTOM0
        VertexBuffer.VertexAttribute.CUSTOM1 -> JSVertexAttribute.CUSTOM1
        VertexBuffer.VertexAttribute.CUSTOM2 -> JSVertexAttribute.CUSTOM2
        VertexBuffer.VertexAttribute.CUSTOM3 -> JSVertexAttribute.CUSTOM3
        VertexBuffer.VertexAttribute.CUSTOM4 -> JSVertexAttribute.CUSTOM4
        VertexBuffer.VertexAttribute.CUSTOM5 -> JSVertexAttribute.CUSTOM5
        VertexBuffer.VertexAttribute.CUSTOM6 -> JSVertexAttribute.CUSTOM6
        VertexBuffer.VertexAttribute.CUSTOM7 -> JSVertexAttribute.CUSTOM7
        else -> JSVertexAttribute.POSITION
    }
}

private fun mapAttributeType(type: VertexBuffer.AttributeType): JSVertexBufferAttributeType {
    return when (type) {
        VertexBuffer.AttributeType.BYTE -> JSVertexBufferAttributeType.BYTE
        VertexBuffer.AttributeType.BYTE2 -> JSVertexBufferAttributeType.BYTE2
        VertexBuffer.AttributeType.BYTE3 -> JSVertexBufferAttributeType.BYTE3
        VertexBuffer.AttributeType.BYTE4 -> JSVertexBufferAttributeType.BYTE4
        VertexBuffer.AttributeType.UBYTE -> JSVertexBufferAttributeType.UBYTE
        VertexBuffer.AttributeType.UBYTE2 -> JSVertexBufferAttributeType.UBYTE2
        VertexBuffer.AttributeType.UBYTE3 -> JSVertexBufferAttributeType.UBYTE3
        VertexBuffer.AttributeType.UBYTE4 -> JSVertexBufferAttributeType.UBYTE4
        VertexBuffer.AttributeType.SHORT -> JSVertexBufferAttributeType.SHORT
        VertexBuffer.AttributeType.SHORT2 -> JSVertexBufferAttributeType.SHORT2
        VertexBuffer.AttributeType.SHORT3 -> JSVertexBufferAttributeType.SHORT3
        VertexBuffer.AttributeType.SHORT4 -> JSVertexBufferAttributeType.SHORT4
        VertexBuffer.AttributeType.USHORT -> JSVertexBufferAttributeType.USHORT
        VertexBuffer.AttributeType.USHORT2 -> JSVertexBufferAttributeType.USHORT2
        VertexBuffer.AttributeType.USHORT3 -> JSVertexBufferAttributeType.USHORT3
        VertexBuffer.AttributeType.USHORT4 -> JSVertexBufferAttributeType.USHORT4
        VertexBuffer.AttributeType.INT -> JSVertexBufferAttributeType.INT
        VertexBuffer.AttributeType.UINT -> JSVertexBufferAttributeType.UINT
        VertexBuffer.AttributeType.FLOAT -> JSVertexBufferAttributeType.FLOAT
        VertexBuffer.AttributeType.FLOAT2 -> JSVertexBufferAttributeType.FLOAT2
        VertexBuffer.AttributeType.FLOAT3 -> JSVertexBufferAttributeType.FLOAT3
        VertexBuffer.AttributeType.FLOAT4 -> JSVertexBufferAttributeType.FLOAT4
        VertexBuffer.AttributeType.HALF -> JSVertexBufferAttributeType.HALF
        VertexBuffer.AttributeType.HALF2 -> JSVertexBufferAttributeType.HALF2
        VertexBuffer.AttributeType.HALF3 -> JSVertexBufferAttributeType.HALF3
        VertexBuffer.AttributeType.HALF4 -> JSVertexBufferAttributeType.HALF4
    }
}