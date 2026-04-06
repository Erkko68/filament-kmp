package dev.filament.kmp

expect class VertexBuffer {
    class Builder() {
        fun vertexCount(vertexCount: Int): Builder
        fun bufferCount(bufferCount: Int): Builder
        fun attribute(attribute: VertexAttribute, bufferIndex: Int, attributeType: AttributeType, byteOffset: Int = 0, byteStride: Int = 0): Builder
        fun normalized(attribute: VertexAttribute, enabled: Boolean = true): Builder
        fun build(engine: Engine): VertexBuffer
    }

    enum class VertexAttribute {
        POSITION, TANGENTS, COLOR, UV0, UV1, BONE_INDICES, BONE_WEIGHTS, UNUSED,
        CUSTOM0, CUSTOM1, CUSTOM2, CUSTOM3, CUSTOM4, CUSTOM5, CUSTOM6, CUSTOM7
    }

    enum class AttributeType {
        BYTE, BYTE2, BYTE3, BYTE4,
        UBYTE, UBYTE2, UBYTE3, UBYTE4,
        SHORT, SHORT2, SHORT3, SHORT4,
        USHORT, USHORT2, USHORT3, USHORT4,
        INT, UINT,
        FLOAT, FLOAT2, FLOAT3, FLOAT4,
        HALF, HALF2, HALF3, HALF4
    }

    fun getVertexCount(): Int
    fun setBufferAt(engine: Engine, bufferIndex: Int, buffer: Any)
    fun setBufferAt(engine: Engine, bufferIndex: Int, buffer: Any, destOffsetInBytes: Int, count: Int)
}
