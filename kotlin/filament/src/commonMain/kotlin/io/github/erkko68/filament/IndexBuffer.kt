package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
import io.github.erkko68.filament.interop.*

/**
 * A buffer containing vertex indices into a VertexBuffer.
 *
 * IndexBuffer holds a sequence of indices that reference vertices in an associated VertexBuffer.
 * Indices can be 16-bit (USHORT) or 32-bit (UINT). Typically used in conjunction with
 * RenderableManager to define the geometry of primitives.
 *
 * @see VertexBuffer, RenderableManager
 */
class IndexBuffer @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /** Type of the index buffer. */
    enum class IndexType(internal val value: Int) {
        /** 16-bit indices */
        USHORT(12),
        /** 32-bit indices */
        UINT(17),
    }

    /**
     * Builder for creating IndexBuffer instances.
     *
     * Configure the index count and type (16-bit or 32-bit), then call build() to create
     * the IndexBuffer.
     */
    class Builder() {
        private val nativeBuilder = FilaIndexBufferBuilder_create()

        /**
         * Sets the number of indices in this buffer.
         *
         * @param indexCount Number of indices
         * @return This Builder, for chaining calls
         */
        fun indexCount(indexCount: Int): Builder = apply { FilaIndexBufferBuilder_indexCount(nativeBuilder, indexCount) }

        /**
         * Sets the index data type (USHORT for 16-bit or UINT for 32-bit).
         *
         * @param indexType The index data type (default: USHORT)
         * @return This Builder, for chaining calls
         */
        fun bufferType(indexType: IndexType): Builder = apply { FilaIndexBufferBuilder_bufferType(nativeBuilder, indexType.value) }

        /**
         * Associates an optional name with this IndexBuffer for debugging purposes.
         *
         * The name shows up in error messages and should be kept short.
         */
        fun name(name: String): Builder = apply { interopScope { FilaIndexBufferBuilder_name(nativeBuilder, toInterop(name)) } }

        /**
         * Creates the IndexBuffer object.
         *
         * @param engine Engine to associate this IndexBuffer with
         * @return The newly created IndexBuffer
         */
        fun build(engine: Engine): IndexBuffer {
            val handle = FilaIndexBufferBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaIndexBufferBuilder_destroy(nativeBuilder)
            return IndexBuffer(handle)
        }
    }

    /**
     * Gets the number of indices in this buffer.
     *
     * @return The index count
     */
    val indexCount: Int get() = FilaIndexBuffer_getIndexCount(nativeHandle)

    /**
     * Sets the index data for this buffer.
     *
     * @param engine The engine
     * @param data The index data as a ByteArray
     */
    fun setBuffer(engine: Engine, data: ByteArray) = setBuffer(engine, data, 0, 0, null)

    /**
     * Sets the index data with offset and count.
     *
     * @param engine The engine
     * @param data The index data as a ByteArray
     * @param destOffsetInBytes Destination offset in bytes
     * @param count Number of bytes to copy
     */
    fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int) = setBuffer(engine, data, destOffsetInBytes, count, null)

    /**
     * Sets the index data with offset, count, and optional completion callback.
     *
     * @param engine The engine
     * @param data The index data as a ByteArray
     * @param destOffsetInBytes Destination offset in bytes
     * @param count Number of bytes to copy
     * @param callback Optional callback that executes when the data upload is complete
     */
    fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int, callback: (() -> Unit)? = null) {
        val upload = upload(data, if (count > 0) count else data.size, callback)
        FilaIndexBuffer_setBuffer(nativeHandle, engine.nativeHandle, upload.ptr, upload.size, upload.callback, upload.userData, destOffsetInBytes)
    }

    /**
     * Returns whether the asynchronous creation of this IndexBuffer has completed; always true
     * when it wasn't built with `Builder.async()`.
     */
    val isCreationComplete: Boolean get() = FilaIndexBuffer_isCreationComplete(nativeHandle)
}
