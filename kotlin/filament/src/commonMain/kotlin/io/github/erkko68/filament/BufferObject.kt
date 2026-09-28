package io.github.erkko68.filament

import io.github.erkko68.filament.interop.*

/**
 * A generic GPU buffer for storing data.
 *
 * BufferObject usage is optional and mainly useful when you need to share data between multiple
 * VertexBuffer instances. It also allows you to efficiently swap-out buffers in VertexBuffer.
 *
 * For simple use cases where you don't need to share data, it is not necessary to use BufferObject.
 * The buffer is created uninitialized; use setBuffer() to initialize it.
 *
 * @see VertexBuffer, IndexBuffer
 */
class BufferObject @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * Binding type options for BufferObject.
     *
     * Distinguishes between different GPU buffer binding points:
     * - VERTEX: Vertex buffer (used with VertexBuffer)
     * - UNIFORM: Uniform/constant buffer
     * - SHADER_STORAGE: Shader storage buffer (for compute)
     */
    enum class BindingType {
        VERTEX,
        UNIFORM,
        SHADER_STORAGE,
    }

    /**
     * Builder for creating BufferObject instances.
     *
     * Configure the buffer size and binding type, then call build() to create the BufferObject.
     */
    class Builder() {
        private val nativeBuilder = FilaBufferObjectBuilder_create()

        /**
         * Sets the size of this buffer in bytes.
         *
         * @param byteCount Maximum number of bytes the BufferObject can hold
         * @return This Builder, for chaining calls
         */
        fun size(byteCount: Int): Builder = apply { FilaBufferObjectBuilder_size(nativeBuilder, byteCount) }

        /**
         * Sets the binding type for this buffer object.
         *
         * Distinguishes between different buffer types (VERTEX, UNIFORM, SHADER_STORAGE).
         * Default: VERTEX.
         *
         * @param bindingType The binding type for this buffer
         * @return This Builder, for chaining calls
         */
        fun bindingType(bindingType: BindingType): Builder = apply { FilaBufferObjectBuilder_bindingType(nativeBuilder, bindingType.ordinal) }

        /**
         * Creates the BufferObject.
         *
         * After creation, the buffer object is uninitialized. Use setBuffer() to initialize it.
         *
         * @param engine Engine to associate this BufferObject with
         * @return The newly created BufferObject
         */
        fun build(engine: Engine): BufferObject {
            val handle = FilaBufferObjectBuilder_build(nativeBuilder, engine.pointer)
            FilaBufferObjectBuilder_destroy(nativeBuilder)
            return BufferObject(handle)
        }
    }

    /**
     * Gets the size of this BufferObject in bytes.
     *
     * @return The maximum capacity of the BufferObject in bytes
     */
    val byteCount: Int get() = FilaBufferObject_getByteCount(nativeHandle)

    /**
     * Asynchronously copies data to initialize a region of this BufferObject.
     *
     * @param engine The engine
     * @param data The data to copy
     */
    fun setBuffer(engine: Engine, data: ByteArray) = setBuffer(engine, data, 0, 0, null)

    /**
     * Asynchronously copies data to a region of this BufferObject with offset and count.
     *
     * @param engine The engine
     * @param data The data to copy
     * @param destOffsetInBytes Destination offset in bytes (must be a multiple of 4)
     * @param count Number of bytes to copy
     */
    fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int) = setBuffer(engine, data, destOffsetInBytes, count, null)

    /**
     * Asynchronously copies data to a region of this BufferObject with callback.
     *
     * @param engine The engine
     * @param data The data to copy
     * @param destOffsetInBytes Destination offset in bytes (must be a multiple of 4)
     * @param count Number of bytes to copy
     * @param callback Optional callback that executes when the data upload is complete
     */
    fun setBuffer(engine: Engine, data: ByteArray, destOffsetInBytes: Int, count: Int, callback: (() -> Unit)? = null) {
        val upload = upload(data, if (count > 0) count else data.size, callback)
        FilaBufferObject_setBuffer(nativeHandle, engine.pointer, upload.ptr, upload.size, destOffsetInBytes, NullPointer, upload.callback, upload.userData)
    }
}

@ExternalSymbolName("FilaBufferObjectBuilder_create")
private external fun FilaBufferObjectBuilder_create(): NativePointer

@ExternalSymbolName("FilaBufferObjectBuilder_destroy")
private external fun FilaBufferObjectBuilder_destroy(builder: NativePointer)

@ExternalSymbolName("FilaBufferObjectBuilder_build")
private external fun FilaBufferObjectBuilder_build(builder: NativePointer, engine: NativePointer): NativePointer

@ExternalSymbolName("FilaBufferObjectBuilder_size")
private external fun FilaBufferObjectBuilder_size(builder: NativePointer, byteCount: Int)

@ExternalSymbolName("FilaBufferObjectBuilder_bindingType")
private external fun FilaBufferObjectBuilder_bindingType(builder: NativePointer, bindingType: Int)

@ExternalSymbolName("FilaBufferObject_getByteCount")
private external fun FilaBufferObject_getByteCount(bufferObject: NativePointer): Int

@ExternalSymbolName("FilaBufferObject_setBuffer")
private external fun FilaBufferObject_setBuffer(bufferObject: NativePointer, engine: NativePointer, buffer: NativePointer, sizeInBytes: Int, destOffsetInBytes: Int, handler: NativePointer, callback: NativePointer, userData: NativePointer)
