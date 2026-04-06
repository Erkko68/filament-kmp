package dev.filament.kmp

/**
 * Holds a set of buffers that define the geometry of a Renderable.
 *
 * <p>
 * The geometry of the Renderable itself is defined by a set of vertex attributes such as
 * position, color, normals, tangents, etc...
 * </p>
 *
 * <p>
 * There is no need to have a 1-to-1 mapping between attributes and buffer. A buffer can hold the
 * data of several attributes -- attributes are then referred as being "interleaved".
 * </p>
 *
 * <p>
 * The buffers themselves are GPU resources, therefore mutating their data can be relatively slow.
 * For this reason, it is best to separate the constant data from the dynamic data into multiple
 * buffers.
 * </p>
 *
 * <p>
 * It is possible, and even encouraged, to use a single vertex buffer for several
 * Renderables.
 * </p>
 *
 * @see IndexBuffer
 * @see RenderableManager
 */
expect class VertexBuffer {

    enum class VertexAttribute {
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
        CUSTOM7
    }

    enum class AttributeType {
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

    class Builder() {
        fun vertexCount(vertexCount: Int): Builder
        fun enableBufferObjects(enabled: Boolean): Builder
        fun bufferCount(bufferCount: Int): Builder
        fun attribute(attribute: VertexAttribute, bufferIndex: Int, attributeType: AttributeType, byteOffset: Int, byteStride: Int): Builder
        fun attribute(attribute: VertexAttribute, bufferIndex: Int, attributeType: AttributeType): Builder
        fun normalized(attribute: VertexAttribute): Builder
        fun normalized(attribute: VertexAttribute, enabled: Boolean): Builder
        fun build(engine: Engine): VertexBuffer
    }

    fun getVertexCount(): Int

    /**
     * Asynchronously copy-initializes the specified buffer from the given buffer data.
     */
    fun setBufferAt(engine: Engine, bufferIndex: Int, buffer: Buffer)

    /**
     * Asynchronously copy-initializes a region of the specified buffer from the given buffer data.
     */
    fun setBufferAt(engine: Engine, bufferIndex: Int, buffer: Buffer, destOffsetInBytes: Int, count: Int)

    /**
     * Asynchronously copy-initializes a region of the specified buffer from the given buffer data.
     * The callback is executed when the buffer is no longer needed.
     */
    fun setBufferAt(engine: Engine, bufferIndex: Int, buffer: Buffer, destOffsetInBytes: Int, count: Int, handler: Any?, callback: Runnable?)

    /**
     * Swaps in the given buffer object.
     */
    fun setBufferObjectAt(engine: Engine, bufferIndex: Int, bufferObject: BufferObject)
}
