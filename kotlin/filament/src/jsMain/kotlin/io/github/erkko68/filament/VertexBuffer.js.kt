package io.github.erkko68.filament

actual class VertexBuffer {
    actual fun getVertexCount(): Int {
        TODO("Not yet implemented")
    }

    actual fun setBufferAt(engine: Engine, bufferIndex: Int, data: ByteArray) {
    }

    actual fun setBufferAt(
        engine: Engine,
        bufferIndex: Int,
        data: ByteArray,
        destOffsetInBytes: Int,
        count: Int
    ) {
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
        actual fun vertexCount(vertexCount: Int): Builder {
            TODO("Not yet implemented")
        }

        actual fun enableBufferObjects(enabled: Boolean): Builder {
            TODO("Not yet implemented")
        }

        actual fun bufferCount(bufferCount: Int): Builder {
            TODO("Not yet implemented")
        }

        actual fun attribute(
            attribute: VertexAttribute,
            bufferIndex: Int,
            attributeType: AttributeType,
            byteOffset: Int,
            byteStride: Int
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun normalized(
            attribute: VertexAttribute,
            enabled: Boolean
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun build(engine: Engine): VertexBuffer {
            TODO("Not yet implemented")
        }
    }
}