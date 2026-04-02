package dev.filament.kmp

/**
 * A generic GPU buffer containing data.
 *
 * Usage of this BufferObject is optional. For simple use cases it is not necessary. It is useful
 * only when you need to share data between multiple VertexBuffer instances. It also allows you to
 * efficiently swap-out the buffers in VertexBuffer.
 */
expect class BufferObject {
    class Builder {
        enum class BindingType {
            VERTEX,
        }

        /**
         * Size of the buffer in bytes.
         *
         * @param byteCount Maximum number of bytes the BufferObject can hold.
         * @return A reference to this Builder for chaining calls.
         */
        fun size(byteCount: Int): Builder

        /**
         * The binding type for this buffer object. (defaults to VERTEX)
         */
        fun bindingType(bindingType: BindingType): Builder

        /**
         * Creates and returns the BufferObject object.
         */
        fun build(engine: Engine): BufferObject
    }

    /**
     * Returns the size of this BufferObject in elements.
     */
    fun getByteCount(): Int

    /**
     * Asynchronously copy-initializes this BufferObject from the data provided.
     */
    fun setBuffer(engine: Engine, buffer: Any)

    /**
     * Asynchronously copy-initializes a region of this BufferObject from the data provided.
     */
    fun setBuffer(engine: Engine, buffer: Any, destOffsetInBytes: Int, count: Int)

    /**
     * Asynchronously copy-initializes a region of this BufferObject from the data provided.
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

