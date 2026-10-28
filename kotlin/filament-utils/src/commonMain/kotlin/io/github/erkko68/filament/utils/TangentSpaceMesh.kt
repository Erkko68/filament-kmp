package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.InternalFilamentApi
import io.github.erkko68.filament.interop.toInterop
import io.github.erkko68.filament.interop.strided
import io.github.erkko68.filament.interop.MeshInputs
import io.github.erkko68.filament.interop.*
import io.github.erkko68.filament.utils.capi.*

/**
 * Builds Filament-style TANGENTS buffers (tangent-space quaternions) from an input mesh, with a
 * choice of algorithms. Supersedes SurfaceOrientation.
 *
 * If the chosen algorithm didn't remesh the input ([remeshed]), use the data you provided instead of
 * querying it back. Getting positions or UVs that weren't provided as input aborts.
 */
class TangentSpaceMesh @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) : AutoCloseable {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /** The tangent-space algorithm. */
    enum class Algorithm {
        /**
         * Picks the best algorithm for the input: FRISVAD for normals only, MIKKTSPACE for
         * normals + uvs + positions + indices.
         */
        DEFAULT,
        /** Mikkelsen 2008. Requires normals + uvs + positions + indices. Remeshes. */
        MIKKTSPACE,
        /** Lengyel 2019. Requires normals + uvs + positions + indices. */
        LENGYEL,
        /** Hughes-Moller 1999, Optix variant. Requires normals. */
        HUGHES_MOLLER,
        /** Frisvad 2012. Requires normals. */
        FRISVAD,
    }

    /**
     * Auxiliary vertex attributes: they don't affect the tangent space, but are mapped when the
     * mesh is remeshed.
     */
    enum class AuxAttribute { UV1, COLORS, JOINTS, WEIGHTS }

    /** Collects the input mesh. Strides are in bytes; 0 means tightly packed. */
    class Builder() {
        init { Filament.init() } // CPU-only, usable before any Engine exists
        private val nativeBuilder = FilaGeometryTangentSpaceMeshBuilder_create()
        // The C++ builder keeps the array pointers until build(), so the native copies live until then.
        private val heap = InteropScope()
        private val inputs = MeshInputs()

        /** The number of input vertices. */
        fun vertexCount(vertexCount: Int): Builder = apply { inputs.vertices = vertexCount; FilaGeometryTangentSpaceMeshBuilder_vertexCount(nativeBuilder, vertexCount) }

        /** The input normals, 3 floats each. */
        fun normals(normals: FloatArray, stride: Int = 0): Builder = apply {
            inputs.vertexArray("normals", normals.size, 3, stride)
            FilaGeometryTangentSpaceMeshBuilder_normals(nativeBuilder, heap.toInterop(normals, normals.size), stride)
        }

        /** The input tangents, 4 floats each; w is the handedness (-1 or +1) for the bitangent. */
        fun tangents(tangents: FloatArray, stride: Int = 0): Builder = apply {
            inputs.vertexArray("tangents", tangents.size, 4, stride)
            FilaGeometryTangentSpaceMeshBuilder_tangents(nativeBuilder, heap.toInterop(tangents, tangents.size), stride)
        }

        /** The input UVs, 2 floats each. */
        fun uvs(uvs: FloatArray, stride: Int = 0): Builder = apply {
            inputs.vertexArray("uvs", uvs.size, 2, stride)
            FilaGeometryTangentSpaceMeshBuilder_uvs(nativeBuilder, heap.toInterop(uvs, uvs.size), stride)
        }

        /** The input positions, 3 floats each. */
        fun positions(positions: FloatArray, stride: Int = 0): Builder = apply {
            inputs.vertexArray("positions", positions.size, 3, stride)
            FilaGeometryTangentSpaceMeshBuilder_positions(nativeBuilder, heap.toInterop(positions, positions.size), stride)
        }

        /** The number of input triangles. */
        fun triangleCount(triangleCount: Int): Builder = apply { inputs.triangles = triangleCount; FilaGeometryTangentSpaceMeshBuilder_triangleCount(nativeBuilder, triangleCount) }

        /** The triangles in 32-bit indices, 3 per triangle. */
        fun triangles(triangles: IntArray): Builder = apply {
            inputs.triangleArray(triangles.size)
            FilaGeometryTangentSpaceMeshBuilder_triangles_uint3(nativeBuilder, heap.toInterop(triangles, triangles.size))
        }

        /** The triangles in 16-bit indices, 3 per triangle. */
        fun triangles(triangles: ShortArray): Builder = apply {
            inputs.triangleArray(triangles.size)
            FilaGeometryTangentSpaceMeshBuilder_triangles_ushort3(nativeBuilder, heap.toInterop(triangles, triangles.size))
        }

        /**
         * Auxiliary input, mapped along when the mesh is remeshed: float2, float3 or float4 per vertex
         * ([components] 2–4). Read it back with [TangentSpaceMesh.getAux] and the same [components].
         */
        fun aux(attribute: AuxAttribute, data: FloatArray, components: Int, stride: Int = 0): Builder = apply {
            inputs.vertexArray(attribute.name, data.size, components, stride)
            val p = heap.toInterop(data, data.size)
            when (components) {
                2 -> FilaGeometryTangentSpaceMeshBuilder_aux_float2(nativeBuilder, attribute.ordinal, p, stride)
                3 -> FilaGeometryTangentSpaceMeshBuilder_aux_float3(nativeBuilder, attribute.ordinal, p, stride)
                4 -> FilaGeometryTangentSpaceMeshBuilder_aux_float4(nativeBuilder, attribute.ordinal, p, stride)
                else -> throw IllegalArgumentException("float aux takes 2, 3 or 4 components, not $components")
            }
        }

        /** Auxiliary input as unsigned shorts (e.g. joint indices): ushort3 or ushort4 per vertex ([components] 3–4). */
        fun aux(attribute: AuxAttribute, data: ShortArray, components: Int, stride: Int = 0): Builder = apply {
            inputs.vertexArray(attribute.name, data.size, components, stride, elementBytes = 2)
            val p = heap.toInterop(data, data.size)
            when (components) {
                3 -> FilaGeometryTangentSpaceMeshBuilder_aux_ushort3(nativeBuilder, attribute.ordinal, p, stride)
                4 -> FilaGeometryTangentSpaceMeshBuilder_aux_ushort4(nativeBuilder, attribute.ordinal, p, stride)
                else -> throw IllegalArgumentException("ushort aux takes 3 or 4 components, not $components")
            }
        }

        /** The algorithm to use; the one built with may differ if the input doesn't suit it. */
        fun algorithm(algorithm: Algorithm): Builder = apply { FilaGeometryTangentSpaceMeshBuilder_algorithm(nativeBuilder, algorithm.ordinal) }

        /**
         * Computes the tangent space. The builder can't be used afterwards.
         *
         * @throws IllegalArgumentException if an array is shorter than [vertexCount] or [triangleCount] needs.
         */
        fun build(): TangentSpaceMesh {
            inputs.shortfall()?.let { error ->
                FilaGeometryTangentSpaceMeshBuilder_destroy(nativeBuilder)
                heap.release()
                throw IllegalArgumentException(error)
            }
            val handle = FilaGeometryTangentSpaceMeshBuilder_build(nativeBuilder)
            FilaGeometryTangentSpaceMeshBuilder_destroy(nativeBuilder)
            heap.release()
            return TangentSpaceMesh(handle)
        }
    }

    /** Frees the mesh. */
    fun destroy() {
        if (nativeHandle != NullPointer) FilaGeometryTangentSpaceMesh_destroy(nativeHandle)
        nativeHandle = NullPointer
    }

    /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
    override fun close() = destroy()

    /** The number of output vertices, which differs from the input's if the mesh was [remeshed]. */
    val vertexCount: Int get() = FilaGeometryTangentSpaceMesh_getVertexCount(nativeHandle)

    /** Copies out the output positions, 3 floats per vertex. */
    fun getPositions(out: FloatArray, stride: Int = 0) = out.usePinned(strided(vertexCount, 3, stride, 4)) { FilaGeometryTangentSpaceMesh_getPositions(nativeHandle, it, stride) }

    /** Copies out the output UVs, 2 floats per vertex. */
    fun getUVs(out: FloatArray, stride: Int = 0) = out.usePinned(strided(vertexCount, 2, stride, 4)) { FilaGeometryTangentSpaceMesh_getUVs(nativeHandle, it, stride) }

    /** Copies out the tangent-space quaternions, 4 floats per vertex. */
    fun getQuats(out: FloatArray, stride: Int = 0) = out.usePinned(strided(vertexCount, 4, stride, 4)) { FilaGeometryTangentSpaceMesh_getQuats_quatf_size_t(nativeHandle, it, stride) }

    /** Copies out the tangent-space quaternions as 4 normalized shorts per vertex (short4). */
    fun getQuats(out: ShortArray, stride: Int = 0) = out.usePinned(strided(vertexCount, 4, stride, 2)) { FilaGeometryTangentSpaceMesh_getQuats_short4_size_t(nativeHandle, it, stride) }

    /** Copies out the tangent-space quaternions as 4 half floats per vertex (quath). */
    fun getHalfQuats(out: ShortArray, stride: Int = 0) = out.usePinned(strided(vertexCount, 4, stride, 2)) { FilaGeometryTangentSpaceMesh_getQuats_quath_size_t(nativeHandle, it, stride) }

    /** Copies out an auxiliary attribute given to [Builder.aux] as floats, [components] (2–4) per vertex. */
    fun getAux(attribute: AuxAttribute, out: FloatArray, components: Int, stride: Int = 0) = out.usePinned(strided(vertexCount, components, stride, 4)) {
        when (components) {
            2 -> FilaGeometryTangentSpaceMesh_getAux_float2_size_t(nativeHandle, attribute.ordinal, it, stride)
            3 -> FilaGeometryTangentSpaceMesh_getAux_float3_size_t(nativeHandle, attribute.ordinal, it, stride)
            4 -> FilaGeometryTangentSpaceMesh_getAux_float4_size_t(nativeHandle, attribute.ordinal, it, stride)
            else -> throw IllegalArgumentException("float aux takes 2, 3 or 4 components, not $components")
        }
    }

    /** Copies out an auxiliary attribute given to [Builder.aux] as unsigned shorts, [components] (3–4) per vertex. */
    fun getAux(attribute: AuxAttribute, out: ShortArray, components: Int, stride: Int = 0) = out.usePinned(strided(vertexCount, components, stride, 2)) {
        when (components) {
            3 -> FilaGeometryTangentSpaceMesh_getAux_ushort3_size_t(nativeHandle, attribute.ordinal, it, stride)
            4 -> FilaGeometryTangentSpaceMesh_getAux_ushort4_size_t(nativeHandle, attribute.ordinal, it, stride)
            else -> throw IllegalArgumentException("ushort aux takes 3 or 4 components, not $components")
        }
    }

    /** The number of output triangles. */
    val triangleCount: Int get() = FilaGeometryTangentSpaceMesh_getTriangleCount(nativeHandle)

    /** Copies out the output triangles in 32-bit indices, 3 per triangle. */
    fun getTriangles(out: IntArray) = out.usePinned(triangleCount * 3) { FilaGeometryTangentSpaceMesh_getTriangles_uint3(nativeHandle, it) }

    /** Copies out the output triangles in 16-bit indices, 3 per triangle. */
    fun getTriangles(out: ShortArray) = out.usePinned(triangleCount * 3) { FilaGeometryTangentSpaceMesh_getTriangles_ushort3(nativeHandle, it) }

    /** Whether the algorithm remeshed the input: then read positions, UVs and triangles back. */
    val remeshed: Boolean get() = FilaGeometryTangentSpaceMesh_remeshed(nativeHandle)
}
