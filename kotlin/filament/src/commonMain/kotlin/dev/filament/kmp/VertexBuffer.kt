package dev.filament.kmp

expect class VertexBuffer {
    enum class VertexAttribute {
        POSITION, // 0
        TANGENTS, // 1
        COLOR,    // 2
        UV0,      // 3
        UV1,      // 4
        BONE_INDICES, // 5
        BONE_WEIGHTS, // 6
        UNUSED,       // 7
        CUSTOM0,      // 8
        CUSTOM1,
        CUSTOM2,
        CUSTOM3,
        CUSTOM4,
        CUSTOM5,
        CUSTOM6,
        CUSTOM7
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

    class Builder() {
        fun vertexCount(vertexCount: Int): Builder
        fun enableBufferObjects(enabled: Boolean): Builder
        fun bufferCount(bufferCount: Int): Builder
        fun attribute(attribute: VertexAttribute, bufferIndex: Int, attributeType: AttributeType, byteOffset: Int = 0, byteStride: Int = 0): Builder
        fun normalized(attribute: VertexAttribute, enabled: Boolean = true): Builder
        fun build(engine: Engine): VertexBuffer
    }

    fun getVertexCount(): Int
    fun setBufferAt(engine: Engine, bufferIndex: Int, data: ByteArray)
    fun setBufferAt(engine: Engine, bufferIndex: Int, data: ByteArray, destOffsetInBytes: Int, count: Int)
    fun setBufferAt(engine: Engine, bufferIndex: Int, data: ByteArray, destOffsetInBytes: Int, count: Int, handler: Any? = null, callback: (() -> Unit)? = null)
    fun setBufferObjectAt(engine: Engine, bufferIndex: Int, bufferObject: BufferObject)
}
