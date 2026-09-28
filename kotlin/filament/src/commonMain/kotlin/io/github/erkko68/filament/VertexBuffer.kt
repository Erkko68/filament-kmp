package io.github.erkko68.filament

import io.github.erkko68.filament.interop.*

/**
 * Holds a set of buffers that define the geometry of a Renderable.
 *
 * The geometry of the Renderable itself is defined by a set of vertex attributes such as
 * position, color, normals, tangents, etc. There is no need to have a 1-to-1 mapping between
 * attributes and buffer. A buffer can hold the data of several attributes—attributes are then
 * referred to as being "interleaved".
 *
 * The buffers themselves are GPU resources, therefore mutating their data can be relatively slow.
 * For this reason, it is best to separate the constant data from the dynamic data into multiple
 * buffers. It is possible, and even encouraged, to use a single vertex buffer for several
 * Renderables.
 *
 * @see IndexBuffer, RenderableManager
 */
class VertexBuffer @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * Vertex attribute types that can be defined in a VertexBuffer.
     *
     * - POSITION: Vertex position in local coordinates
     * - TANGENTS: Surface tangent vectors (should be specified as quaternions for normals)
     * - COLOR: Vertex color (RGBA)
     * - UV0: First set of texture coordinates
     * - UV1: Second set of texture coordinates
     * - BONE_INDICES: Indices for skeletal animation
     * - BONE_WEIGHTS: Weights for skeletal animation
     * - UNUSED: Unused attribute slot
     * - CUSTOM0-CUSTOM7: Custom attributes for specialized effects
     */
    enum class VertexAttribute {
        POSITION, TANGENTS, COLOR, UV0, UV1, BONE_INDICES, BONE_WEIGHTS, UNUSED,
        CUSTOM0, CUSTOM1, CUSTOM2, CUSTOM3, CUSTOM4, CUSTOM5, CUSTOM6, CUSTOM7
    }

    /**
     * Data types for vertex attributes.
     *
     * Signed types: BYTE, BYTE2, BYTE3, BYTE4
     * Unsigned types: UBYTE, UBYTE2, UBYTE3, UBYTE4
     * Signed short: SHORT, SHORT2, SHORT3, SHORT4
     * Unsigned short: USHORT, USHORT2, USHORT3, USHORT4
     * Integer: INT, UINT
     * Floating point: FLOAT, FLOAT2, FLOAT3, FLOAT4
     * Half-precision: HALF, HALF2, HALF3, HALF4
     */
    enum class AttributeType {
        BYTE, BYTE2, BYTE3, BYTE4,
        UBYTE, UBYTE2, UBYTE3, UBYTE4,
        SHORT, SHORT2, SHORT3, SHORT4,
        USHORT, USHORT2, USHORT3, USHORT4,
        INT, UINT,
        FLOAT, FLOAT2, FLOAT3, FLOAT4,
        HALF, HALF2, HALF3, HALF4
    }

    /**
     * Builder for creating VertexBuffer instances.
     *
     * Configure the vertex count, buffers, and attributes, then call build() to create
     * the VertexBuffer.
     */
    class Builder() {
        private val nativeBuilder = FilaVertexBufferBuilder_create()

        /**
         * Defines how many buffers will be created in this vertex buffer set.
         *
         * These buffers are later referenced by index from 0 to bufferCount - 1.
         * The maximum value is 8. This call is mandatory.
         *
         * @param bufferCount Number of buffers in this vertex buffer set
         * @return This Builder, for chaining calls
         */
        fun bufferCount(bufferCount: Int): Builder = apply { FilaVertexBufferBuilder_bufferCount(nativeBuilder, bufferCount) }

        /**
         * Sets the size of each buffer in the set in vertices.
         *
         * @param vertexCount Number of vertices in each buffer in this set
         * @return This Builder, for chaining calls
         */
        fun vertexCount(vertexCount: Int): Builder = apply { FilaVertexBufferBuilder_vertexCount(nativeBuilder, vertexCount) }

        /**
         * Allows buffers to be swapped out and shared using BufferObject.
         *
         * If buffer objects mode is enabled, clients must call setBufferObjectAt() rather than
         * setBufferAt(). This allows sharing of data between VertexBuffer objects, but it may
         * slightly increase the memory footprint of Filament's internal bookkeeping.
         *
         * @param enabled If true, enables buffer object mode (default: false)
         * @return This Builder, for chaining calls
         */
        fun enableBufferObjects(enabled: Boolean): Builder = apply { FilaVertexBufferBuilder_enableBufferObjects(nativeBuilder, enabled) }

        /**
         * Sets up an attribute for this vertex buffer set.
         *
         * Using byteOffset and byteStride, attributes can be interleaved in the same buffer.
         * TANGENTS must be specified as a quaternion and is how normals are specified.
         *
         * Not all backends support 3-component attributes that are not floats.
         *
         * @param attribute The attribute to set up
         * @param bufferIndex The index of the buffer containing the data (0 to bufferCount - 1)
         * @param attributeType The type of the attribute data
         * @param byteOffset Offset in bytes into the buffer (default: 0)
         * @param byteStride Stride in bytes to the next element. When 0, uses the attribute size
         * @return This Builder, for chaining calls
         */
        fun attribute(attribute: VertexAttribute, bufferIndex: Int, attributeType: AttributeType, byteOffset: Int = 0, byteStride: Int = 0): Builder = apply {
            FilaVertexBufferBuilder_attribute(nativeBuilder, attribute.ordinal, bufferIndex, attributeType.ordinal, byteOffset, byteStride)
        }

        /**
         * Sets whether a given attribute should be normalized.
         *
         * A normalized attribute is mapped between 0 and 1 in the shader. This applies only to
         * integer types. By default attributes are not normalized.
         *
         * @param attribute The attribute to set the normalization flag for
         * @param enabled If true, automatically normalize the attribute (default: true)
         * @return This Builder, for chaining calls
         */
        fun normalized(attribute: VertexAttribute, enabled: Boolean = true): Builder = apply { FilaVertexBufferBuilder_normalized(nativeBuilder, attribute.ordinal, enabled) }

        /**
         * Creates the VertexBuffer object.
         *
         * @param engine Engine to associate this VertexBuffer with
         * @return The newly created VertexBuffer
         */
        fun build(engine: Engine): VertexBuffer {
            val handle = FilaVertexBufferBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaVertexBufferBuilder_destroy(nativeBuilder)
            return VertexBuffer(handle)
        }
    }

    /**
     * Gets the number of vertices in each buffer of this set.
     *
     * @return The vertex count
     */
    val vertexCount: Int get() = FilaVertexBuffer_getVertexCount(nativeHandle)

    /**
     * Sets the data for a given buffer in this vertex buffer set.
     *
     * @param engine The engine
     * @param bufferIndex The index of the buffer to set (0 to bufferCount - 1)
     * @param data The vertex data as a ByteArray
     */
    fun setBufferAt(engine: Engine, bufferIndex: Int, data: ByteArray) = setBufferAt(engine, bufferIndex, data, 0, 0, null)

    /**
     * Sets the data for a given buffer with offset and count.
     *
     * @param engine The engine
     * @param bufferIndex The index of the buffer to set (0 to bufferCount - 1)
     * @param data The vertex data as a ByteArray
     * @param destOffsetInBytes Destination offset in bytes
     * @param count Number of bytes to copy
     */
    fun setBufferAt(engine: Engine, bufferIndex: Int, data: ByteArray, destOffsetInBytes: Int, count: Int) = setBufferAt(engine, bufferIndex, data, destOffsetInBytes, count, null)

    /**
     * Sets the data for a given buffer with offset, count, and optional completion callback.
     *
     * @param engine The engine
     * @param bufferIndex The index of the buffer to set (0 to bufferCount - 1)
     * @param data The vertex data as a ByteArray
     * @param destOffsetInBytes Destination offset in bytes
     * @param count Number of bytes to copy
     * @param callback Optional callback that executes when the data upload is complete
     */
    fun setBufferAt(engine: Engine, bufferIndex: Int, data: ByteArray, destOffsetInBytes: Int, count: Int, callback: (() -> Unit)? = null) {
        val upload = upload(data, if (count > 0) count else data.size, callback)
        FilaVertexBuffer_setBufferAt(nativeHandle, engine.nativeHandle, bufferIndex, upload.ptr, upload.size, destOffsetInBytes, NullPointer, upload.callback, upload.userData)
    }

    /**
     * Associates a BufferObject with a buffer in this vertex buffer set.
     *
     * This is only available when buffer objects mode is enabled via enableBufferObjects().
     * Allows sharing of data between VertexBuffer objects.
     *
     * @param engine The engine
     * @param bufferIndex The index of the buffer to set (0 to bufferCount - 1)
     * @param bufferObject The BufferObject to associate
     */
    fun setBufferObjectAt(engine: Engine, bufferIndex: Int, bufferObject: BufferObject) {
        FilaVertexBuffer_setBufferObjectAt(nativeHandle, engine.nativeHandle, bufferIndex, bufferObject.nativeHandle)
    }
}

@ExternalSymbolName("FilaVertexBufferBuilder_create")
private external fun FilaVertexBufferBuilder_create(): NativePointer

@ExternalSymbolName("FilaVertexBufferBuilder_destroy")
private external fun FilaVertexBufferBuilder_destroy(builder: NativePointer)

@ExternalSymbolName("FilaVertexBufferBuilder_build")
private external fun FilaVertexBufferBuilder_build(builder: NativePointer, engine: NativePointer): NativePointer

@ExternalSymbolName("FilaVertexBufferBuilder_bufferCount")
private external fun FilaVertexBufferBuilder_bufferCount(builder: NativePointer, bufferCount: Int)

@ExternalSymbolName("FilaVertexBufferBuilder_vertexCount")
private external fun FilaVertexBufferBuilder_vertexCount(builder: NativePointer, vertexCount: Int)

@ExternalSymbolName("FilaVertexBufferBuilder_enableBufferObjects")
private external fun FilaVertexBufferBuilder_enableBufferObjects(builder: NativePointer, enabled: Boolean)

@ExternalSymbolName("FilaVertexBufferBuilder_attribute")
private external fun FilaVertexBufferBuilder_attribute(builder: NativePointer, attribute: Int, bufferIndex: Int, attributeType: Int, byteOffset: Int, byteStride: Int)

@ExternalSymbolName("FilaVertexBufferBuilder_normalized")
private external fun FilaVertexBufferBuilder_normalized(builder: NativePointer, attribute: Int, normalized: Boolean)

@ExternalSymbolName("FilaVertexBuffer_getVertexCount")
private external fun FilaVertexBuffer_getVertexCount(vertexBuffer: NativePointer): Int

@ExternalSymbolName("FilaVertexBuffer_setBufferAt")
private external fun FilaVertexBuffer_setBufferAt(vertexBuffer: NativePointer, engine: NativePointer, bufferIndex: Int, buffer: NativePointer, sizeInBytes: Int, destOffsetInBytes: Int, handler: NativePointer, callback: NativePointer, userData: NativePointer)

@ExternalSymbolName("FilaVertexBuffer_setBufferObjectAt")
private external fun FilaVertexBuffer_setBufferObjectAt(vertexBuffer: NativePointer, engine: NativePointer, bufferIndex: Int, bufferObject: NativePointer)
