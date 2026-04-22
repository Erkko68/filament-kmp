package io.github.erkko68.filament

/**
 * A buffer containing vertex indices into a VertexBuffer. Indices can be 16 or 32 bit.
 */
expect class IndexBuffer {
    class Builder() {
        enum class IndexType {
            USHORT,
            UINT,
        }
        fun indexCount(indexCount: Int): Builder
        fun bufferType(indexType: IndexType): Builder
        fun build(engine: Engine): IndexBuffer
    }

    fun getIndexCount(): Int
    fun setBuffer(engine: Engine, data: ByteArray)
    fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int)
    fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int, callback: (() -> Unit)? = null)
}
