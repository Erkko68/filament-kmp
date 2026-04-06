package dev.filament.kmp

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
    fun setBuffer(engine: Engine, buffer: Any, sizeInBytes: Int)
    fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, sizeInBytes: Int)
}
