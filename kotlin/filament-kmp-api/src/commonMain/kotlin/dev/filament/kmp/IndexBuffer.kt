package dev.filament.kmp

/**
 * A buffer containing vertex indices into a VertexBuffer. Indices can be 16 or 32 bit.
 * The buffer itself is a GPU resource, therefore mutating the data can be relatively slow.
 * Typically these buffers are constant.
 *
 * It is possible, and even encouraged, to use a single index buffer for several
 * Renderables.
 *
 * @see VertexBuffer
 * @see RenderableManager
 */
expect class IndexBuffer {

    class Builder() {
        /**
         * Type of the index buffer.
         */
        enum class IndexType {
            /** 16-bit indices */
            USHORT,

            /** 32-bit indices */
            UINT,
        }

        fun indexCount(indexCount: Int): Builder
        fun bufferType(indexType: IndexType): Builder
        fun build(engine: Engine): IndexBuffer
    }

    fun getIndexCount(): Int

    /**
     * Asynchronously copy-initializes this IndexBuffer from the data provided.
     */
    fun setBuffer(engine: Engine, buffer: Buffer)

    /**
     * Asynchronously copy-initializes a region of this IndexBuffer from the data provided.
     */
    fun setBuffer(engine: Engine, buffer: Buffer, destOffsetInBytes: Int, count: Int)

    /**
     * Asynchronously copy-initializes a region of this IndexBuffer from the data provided.
     * The callback is executed when the buffer is no longer needed.
     */
    fun setBuffer(engine: Engine, buffer: Buffer, destOffsetInBytes: Int, count: Int, handler: Any?, callback: Runnable?)
}
