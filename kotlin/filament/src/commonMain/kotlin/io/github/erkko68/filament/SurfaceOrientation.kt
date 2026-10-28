package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
import io.github.erkko68.filament.interop.MeshInputs
import io.github.erkko68.filament.interop.strided
import io.github.erkko68.filament.interop.*

/**
 * The surface orientation helper can be used to populate Filament-style TANGENTS buffers.
 */
class SurfaceOrientation @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) : AutoCloseable {
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
        private val nativeBuilder = FilaGeometrySurfaceOrientationBuilder_create()
        private val heap = InteropScope() // the C++ builder keeps the pointers until build()
        private val inputs = MeshInputs()

        /**
         * Specifies the vertex count. This attribute is required.
         *
         * @param vertexCount The number of vertices in the mesh.
         * @return This Builder instance for method chaining.
         */
        fun vertexCount(vertexCount: Int): Builder {
            inputs.vertices = vertexCount
            FilaGeometrySurfaceOrientationBuilder_vertexCount(nativeBuilder, vertexCount)
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
            inputs.vertexArray("normals", buffer.size, 3, stride)
            FilaGeometrySurfaceOrientationBuilder_normals(nativeBuilder, heap.toInterop(buffer, buffer.size), stride)
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
            inputs.vertexArray("tangents", buffer.size, 4, stride)
            FilaGeometrySurfaceOrientationBuilder_tangents(nativeBuilder, heap.toInterop(buffer, buffer.size), stride)
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
            inputs.vertexArray("uvs", buffer.size, 2, stride)
            FilaGeometrySurfaceOrientationBuilder_uvs(nativeBuilder, heap.toInterop(buffer, buffer.size), stride)
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
            inputs.vertexArray("positions", buffer.size, 3, stride)
            FilaGeometrySurfaceOrientationBuilder_positions(nativeBuilder, heap.toInterop(buffer, buffer.size), stride)
            return this
        }

        /**
         * Specifies the number of triangles in the mesh.
         *
         * @param triangleCount The number of triangles.
         * @return This Builder instance for method chaining.
         */
        fun triangleCount(triangleCount: Int): Builder {
            inputs.triangles = triangleCount
            FilaGeometrySurfaceOrientationBuilder_triangleCount(nativeBuilder, triangleCount)
            return this
        }

        /**
         * Specifies 32-bit triangle indices, 3 per triangle.
         *
         * @return This Builder instance for method chaining.
         */
        fun triangles(buffer: IntArray): Builder {
            inputs.triangleArray(buffer.size)
            FilaGeometrySurfaceOrientationBuilder_triangles_uint3(nativeBuilder, heap.toInterop(buffer, buffer.size))
            return this
        }

        /**
         * Specifies 16-bit triangle indices, 3 per triangle.
         *
         * @return This Builder instance for method chaining.
         */
        fun triangles(buffer: ShortArray): Builder {
            inputs.triangleArray(buffer.size)
            FilaGeometrySurfaceOrientationBuilder_triangles_ushort3(nativeBuilder, heap.toInterop(buffer, buffer.size))
            return this
        }

        /**
         * Generates quaternions or returns null if the submitted data is an incomplete combination.
         *
         * @return A SurfaceOrientation instance, or null if the data combination is incomplete.
         * @throws IllegalArgumentException if an array is shorter than [vertexCount] or [triangleCount] needs.
         */
        fun build(): SurfaceOrientation? {
            inputs.shortfall()?.let { error ->
                FilaGeometrySurfaceOrientationBuilder_destroy(nativeBuilder)
                heap.release()
                throw IllegalArgumentException(error)
            }
            val handle = FilaGeometrySurfaceOrientationBuilder_build(nativeBuilder)
            FilaGeometrySurfaceOrientationBuilder_destroy(nativeBuilder)
            heap.release()
            return handle.takeIf { it != NullPointer }?.let(::SurfaceOrientation)
        }
    }

    /** The number of vertices for which quaternions were generated. */
    val vertexCount: Int get() = FilaGeometrySurfaceOrientation_getVertexCount(nativeHandle)

    /**
     * Writes [quatCount] quaternions (4 floats each) to [out]; normally quatCount is the vertex count.
     *
     * @param stride Byte offset between consecutive quaternions; 0 means tightly packed.
     */
    fun getQuats(out: FloatArray, quatCount: Int, stride: Int = 0) =
        out.usePinned(quatValues(quatCount, stride, 4)) { FilaGeometrySurfaceOrientation_getQuats_quatf_size_t_size_t(nativeHandle, it, quatCount, stride) }

    /** [getQuats] as normalized shorts (short4). */
    fun getQuats(out: ShortArray, quatCount: Int, stride: Int = 0) =
        out.usePinned(quatValues(quatCount, stride, 2)) { FilaGeometrySurfaceOrientation_getQuats_short4_size_t_size_t(nativeHandle, it, quatCount, stride) }

    /** [getQuats] as half floats (quath). */
    fun getHalfQuats(out: ShortArray, quatCount: Int, stride: Int = 0) =
        out.usePinned(quatValues(quatCount, stride, 2)) { FilaGeometrySurfaceOrientation_getQuats_quath_size_t_size_t(nativeHandle, it, quatCount, stride) }

    // Values [quatCount] quaternions take in an out array of [bytes]-sized values; there are only vertexCount of them.
    private fun quatValues(quatCount: Int, stride: Int, bytes: Int): Int {
        require(quatCount in 0..vertexCount) { "$quatCount quaternions requested, but there are $vertexCount" }
        return strided(quatCount, 4, stride, bytes)
    }

    /** Destroys this SurfaceOrientation. */
    fun destroy() {
        if (nativeHandle != NullPointer) FilaGeometrySurfaceOrientation_destroy(nativeHandle)
        nativeHandle = NullPointer
    }

    /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
    override fun close() = destroy()
}
