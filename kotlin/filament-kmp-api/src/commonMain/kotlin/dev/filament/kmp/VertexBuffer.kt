package dev.filament.kmp

/**
 * Holds a set of buffers that define the geometry of a Renderable.
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
        CUSTOM7,
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

    class Builder {
        /**
         * Size of each buffer in this set, expressed in in number of vertices.
         */
        fun vertexCount(vertexCount: Int): Builder

        /**
         * Allows buffers to be swapped out and shared using BufferObject.
         */
        fun enableBufferObjects(enabled: Boolean): Builder

        /**
         * Defines how many buffers will be created in this vertex buffer set.
         */
        fun bufferCount(bufferCount: Int): Builder

        /**
         * Sets up an attribute for this vertex buffer set.
         */
        fun attribute(
            attribute: VertexAttribute,
            bufferIndex: Int,
            attributeType: AttributeType,
            byteOffset: Int,
            byteStride: Int,
        ): Builder

        /**
         * Sets up an attribute for this vertex buffer set.
         */
        fun attribute(attribute: VertexAttribute, bufferIndex: Int, attributeType: AttributeType): Builder

        /**
         * Sets whether a given attribute should be normalized.
         */
        fun normalized(attribute: VertexAttribute): Builder

        /**
         * Sets whether a given attribute should be normalized.
         */
        fun normalized(attribute: VertexAttribute, enabled: Boolean): Builder

        /**
         * Creates the VertexBuffer object.
         */
        fun build(engine: Engine): VertexBuffer
    }

    /**
     * Returns the vertex count.
     */
    fun getVertexCount(): Int

    /**
     * Asynchronously copy-initializes the specified buffer from the given buffer data.
     */
    fun setBufferAt(engine: Engine, bufferIndex: Int, buffer: Any)

    /**
     * Asynchronously copy-initializes a region of the specified buffer from the given buffer data.
     */
    fun setBufferAt(engine: Engine, bufferIndex: Int, buffer: Any, destOffsetInBytes: Int, count: Int)

    /**
     * Asynchronously copy-initializes a region of the specified buffer from the given buffer data.
     */
    fun setBufferAt(
        engine: Engine,
        bufferIndex: Int,
        buffer: Any,
        destOffsetInBytes: Int,
        count: Int,
        handler: Any?,
        callback: (() -> Unit)?,
    )

    /**
     * Swaps in the given buffer object.
     */
    fun setBufferObjectAt(engine: Engine, bufferIndex: Int, bufferObject: BufferObject)

    fun getNativeObject(): Long

    internal fun invalidate()
}

