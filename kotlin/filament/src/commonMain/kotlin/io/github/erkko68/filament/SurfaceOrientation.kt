package io.github.erkko68.filament

import io.github.erkko68.filament.interop.*

/**
 * The surface orientation helper can be used to populate Filament-style TANGENTS buffers.
 */
class SurfaceOrientation @InternalFilamentApi constructor(internal val nativeHandle: NativePointer) : AutoCloseable {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * The Builder is used to construct an immutable surface orientation helper.
     *
     * Clients provide pointers into their own data, which is synchronously consumed during build().
     * At a minimum, clients must supply a vertex count. They can supply data in any of the
     * following combinations:
     *
     * 1. normals only ........................... not recommended, selects arbitrary orientation
     * 2. normals + tangents ..................... sign of W determines bitangent orientation
     * 3. normals + uvs + positions + indices .... selects Lengyel's Method
     * 4. positions + indices .................... generates normals for flat shading only
     *
     * Additionally, the client-side data has the following type constraints:
     *
     * - Normals must be 3-element float arrays
     * - Tangents must be 4-element float arrays
     * - UVs must be 2-element float arrays
     * - Positions must be 3-element float arrays
     * - Triangles must be 3-element index arrays (ushort or uint)
     *
     * Currently, mikktspace is not supported because it requires re-indexing the mesh. Instead
     * we use the method described by Eric Lengyel in "Foundations of Game Engine Development"
     * (Volume 2, Chapter 7).
     */
    class Builder() {
        init { Filament.init() } // CPU-only, usable before any Engine exists
        private val nativeBuilder = FilaSurfaceOrientationBuilder_create()
        // The C++ builder keeps the array pointers until build(), so the native copies live until then.
        private val heap = InteropScope() // the C++ builder keeps the pointers until build()

        /**
         * Specifies the vertex count. This attribute is required.
         *
         * @param vertexCount The number of vertices in the mesh.
         * @return This Builder instance for method chaining.
         */
        fun vertexCount(vertexCount: Int): Builder {
            FilaSurfaceOrientationBuilder_vertexCount(nativeBuilder, vertexCount)
            return this
        }

        /**
         * Specifies the vertex normals. Stride is the byte offset between consecutive normals.
         *
         * @param buffer An array of normal vectors, each with 3 floats.
         * @param stride Byte offset between consecutive normals. 0 means tightly packed.
         * @return This Builder instance for method chaining.
         */
        fun normals(buffer: FloatArray, stride: Int = 0): Builder {
            FilaSurfaceOrientationBuilder_normals(nativeBuilder, heap.toInterop(buffer), stride)
            return this
        }

        /**
         * Specifies the vertex tangents. Stride is the byte offset between consecutive tangents.
         *
         * @param buffer An array of tangent vectors, each with 4 floats (the 4th component is the sign of the bitangent).
         * @param stride Byte offset between consecutive tangents. 0 means tightly packed.
         * @return This Builder instance for method chaining.
         */
        fun tangents(buffer: FloatArray, stride: Int = 0): Builder {
            FilaSurfaceOrientationBuilder_tangents(nativeBuilder, heap.toInterop(buffer), stride)
            return this
        }

        /**
         * Specifies the vertex texture coordinates. Stride is the byte offset between consecutive UVs.
         *
         * @param buffer An array of UV coordinates, each with 2 floats.
         * @param stride Byte offset between consecutive UVs. 0 means tightly packed.
         * @return This Builder instance for method chaining.
         */
        fun uvs(buffer: FloatArray, stride: Int = 0): Builder {
            FilaSurfaceOrientationBuilder_uvs(nativeBuilder, heap.toInterop(buffer), stride)
            return this
        }

        /**
         * Specifies the vertex positions. Stride is the byte offset between consecutive positions.
         *
         * @param buffer An array of position vectors, each with 3 floats.
         * @param stride Byte offset between consecutive positions. 0 means tightly packed.
         * @return This Builder instance for method chaining.
         */
        fun positions(buffer: FloatArray, stride: Int = 0): Builder {
            FilaSurfaceOrientationBuilder_positions(nativeBuilder, heap.toInterop(buffer), stride)
            return this
        }

        /**
         * Specifies the number of triangles in the mesh.
         *
         * @param triangleCount The number of triangles.
         * @return This Builder instance for method chaining.
         */
        fun triangleCount(triangleCount: Int): Builder {
            FilaSurfaceOrientationBuilder_triangleCount(nativeBuilder, triangleCount)
            return this
        }

        /**
         * Specifies 16-bit triangle indices.
         *
         * @param buffer An array of 16-bit unsigned indices, grouped into sets of 3 (one triangle per set).
         * @return This Builder instance for method chaining.
         */
        fun triangles16(buffer: ShortArray): Builder {
            FilaSurfaceOrientationBuilder_triangles16(nativeBuilder, heap.toInterop(buffer))
            return this
        }

        /**
         * Specifies 32-bit triangle indices.
         *
         * @param buffer An array of 32-bit unsigned indices, grouped into sets of 3 (one triangle per set).
         * @return This Builder instance for method chaining.
         */
        fun triangles32(buffer: IntArray): Builder {
            FilaSurfaceOrientationBuilder_triangles32(nativeBuilder, heap.toInterop(buffer))
            return this
        }

        /**
         * Generates quaternions or returns null if the submitted data is an incomplete combination.
         *
         * @return A SurfaceOrientation instance, or null if the data combination is incomplete.
         */
        fun build(): SurfaceOrientation {
            val handle = FilaSurfaceOrientationBuilder_build(nativeBuilder)
            FilaSurfaceOrientationBuilder_destroy(nativeBuilder)
            heap.release()
            return SurfaceOrientation(handle)
        }
    }

    /**
     * Returns the vertex count.
     *
     * @return The number of vertices for which quaternions were generated.
     */
    val vertexCount: Int get() = FilaSurfaceOrientation_getVertexCount(nativeHandle)

    /**
     * Converts quaternions into float format and writes up to the specified count
     * to the given output buffer. Normally the count should be equal to the vertex count.
     *
     * @param buffer Output buffer where quaternions will be written as floats.
     * @param count The number of quaternions to write. Should equal vertex count in most cases.
     */
    fun getQuatsAsFloat(buffer: FloatArray, count: Int) {
        buffer.usePinned { pinned ->
            FilaSurfaceOrientation_getQuatsAsFloat(nativeHandle, pinned, count)
        }
    }

    /**
     * Converts quaternions into half-precision format and writes up to the specified count
     * to the given output buffer.
     *
     * @param buffer Output buffer where quaternions will be written as half-precision floats.
     * @param count The number of quaternions to write.
     */
    fun getQuatsAsHalf(buffer: ShortArray, count: Int) {
        buffer.usePinned { pinned ->
            val ptr: NativePointer = pinned
            FilaSurfaceOrientation_getQuatsAsHalf(nativeHandle, ptr, count)
        }
    }

    /**
     * Converts quaternions into short format and writes up to the specified count
     * to the given output buffer.
     *
     * @param buffer Output buffer where quaternions will be written as shorts.
     * @param count The number of quaternions to write.
     */
    fun getQuatsAsShort(buffer: ShortArray, count: Int) {
        buffer.usePinned { pinned ->
            FilaSurfaceOrientation_getQuatsAsShort(nativeHandle, pinned, count)
        }
    }

    /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
    override fun close() = destroy()


    /**
     * Destroys this SurfaceOrientation instance and releases associated resources.
     */
    fun destroy() {
        FilaSurfaceOrientation_destroy(nativeHandle)
    }
}

@ExternalSymbolName("FilaSurfaceOrientationBuilder_build")
private external fun FilaSurfaceOrientationBuilder_build(builder: NativePointer): NativePointer

@ExternalSymbolName("FilaSurfaceOrientationBuilder_create")
private external fun FilaSurfaceOrientationBuilder_create(): NativePointer

@ExternalSymbolName("FilaSurfaceOrientationBuilder_destroy")
private external fun FilaSurfaceOrientationBuilder_destroy(builder: NativePointer)

@ExternalSymbolName("FilaSurfaceOrientationBuilder_normals")
private external fun FilaSurfaceOrientationBuilder_normals(builder: NativePointer, buffer: NativePointer, stride: Int)

@ExternalSymbolName("FilaSurfaceOrientationBuilder_positions")
private external fun FilaSurfaceOrientationBuilder_positions(builder: NativePointer, buffer: NativePointer, stride: Int)

@ExternalSymbolName("FilaSurfaceOrientationBuilder_tangents")
private external fun FilaSurfaceOrientationBuilder_tangents(builder: NativePointer, buffer: NativePointer, stride: Int)

@ExternalSymbolName("FilaSurfaceOrientationBuilder_triangleCount")
private external fun FilaSurfaceOrientationBuilder_triangleCount(builder: NativePointer, triangleCount: Int)

@ExternalSymbolName("FilaSurfaceOrientationBuilder_triangles16")
private external fun FilaSurfaceOrientationBuilder_triangles16(builder: NativePointer, buffer: NativePointer)

@ExternalSymbolName("FilaSurfaceOrientationBuilder_triangles32")
private external fun FilaSurfaceOrientationBuilder_triangles32(builder: NativePointer, buffer: NativePointer)

@ExternalSymbolName("FilaSurfaceOrientationBuilder_uvs")
private external fun FilaSurfaceOrientationBuilder_uvs(builder: NativePointer, buffer: NativePointer, stride: Int)

@ExternalSymbolName("FilaSurfaceOrientationBuilder_vertexCount")
private external fun FilaSurfaceOrientationBuilder_vertexCount(builder: NativePointer, vertexCount: Int)

@ExternalSymbolName("FilaSurfaceOrientation_destroy")
private external fun FilaSurfaceOrientation_destroy(orientation: NativePointer)

@ExternalSymbolName("FilaSurfaceOrientation_getQuatsAsFloat")
private external fun FilaSurfaceOrientation_getQuatsAsFloat(orientation: NativePointer, buffer: NativePointer, count: Int)

@ExternalSymbolName("FilaSurfaceOrientation_getQuatsAsHalf")
private external fun FilaSurfaceOrientation_getQuatsAsHalf(orientation: NativePointer, buffer: NativePointer, count: Int)

@ExternalSymbolName("FilaSurfaceOrientation_getQuatsAsShort")
private external fun FilaSurfaceOrientation_getQuatsAsShort(orientation: NativePointer, buffer: NativePointer, count: Int)

@ExternalSymbolName("FilaSurfaceOrientation_getVertexCount")
private external fun FilaSurfaceOrientation_getVertexCount(orientation: NativePointer): Int
