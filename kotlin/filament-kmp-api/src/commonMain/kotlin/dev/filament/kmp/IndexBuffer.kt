package dev.filament.kmp

/**
 * A buffer containing vertex indices into a VertexBuffer. Indices can be 16 or 32 bit.
 */
expect class IndexBuffer {
    class Builder {
        /**
         * Type of the index buffer.
         */
        enum class IndexType {
            /** 16-bit indices */
            USHORT,

            /** 32-bit indices */
            UINT,
        }

        /**
         * Size of the index buffer in elements.
         */
        fun indexCount(indexCount: Int): Builder

        /**
         * Type of the index buffer, 16-bit or 32-bit.
         */
        fun bufferType(indexType: IndexType): Builder

        /**
         * Creates and returns the IndexBuffer object.
         */
        fun build(engine: Engine): IndexBuffer
    }

    /**
     * Returns the size of this IndexBuffer in elements.
     */
    fun getIndexCount(): Int

    /**
     * Asynchronously copy-initializes this IndexBuffer from the data provided.
     */
    fun setBuffer(engine: Engine, buffer: Any)

    /**
     * Asynchronously copy-initializes a region of this IndexBuffer from the data provided.
     */
    fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int)

    /**
     * Asynchronously copy-initializes a region of this IndexBuffer from the data provided.
     */
    fun setBuffer(
        engine: Engine,
        buffer: Any,
        destOffsetInBytes: Int,
        count: Int,
        handler: Any?,
        callback: (() -> Unit)?,
    )

    fun getNativeObject(): Long

    internal fun invalidate()
}

