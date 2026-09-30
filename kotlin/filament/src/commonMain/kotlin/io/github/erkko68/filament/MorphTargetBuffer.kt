package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
import io.github.erkko68.filament.interop.*

/**
 * A container for vertex morphing data that supports both automatic and manual morphing.
 *
 * MorphTargetBuffer operates in a hybrid model depending on the attribute being morphed:
 *
 * **1. Automatic for Built-ins (positions/tangents):**
 * Enable via `withPositions(true)` or `withTangents(true)`. The MorphTargetBuffer will
 * allocate internal storage and hold the data for these attributes, which you upload
 * via `setPositionsAt()` or `setTangentsAt()`. The framework automatically applies
 * the morphing logic in the vertex shader.
 *
 * **2. Manual for Custom Data (e.g., UVs, colors):**
 * The MorphTargetBuffer does NOT hold data for custom targets. The user is responsible
 * for the full data pipeline:
 * - Create and manage a separate Texture to hold the morph target data (offsets).
 * - In the material, declare a `sampler2d_array` parameter.
 * - Bind the Texture to the material instance.
 * - In the vertex shader, manually call `morphData2`, `morphData3`, or `morphData4`
 *   with the custom sampler to apply the morphing.
 *
 * A MorphTargetBuffer object must be associated with a Renderable via
 * RenderableManager.Builder.morphing() to enable the morphing pipeline.
 *
 * @see RenderableManager
 */
class MorphTargetBuffer @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * Builder for creating MorphTargetBuffer instances.
     */
    class Builder() {
        private val nativeBuilder = FilaMorphTargetBufferBuilder_create()

        /**
         * Sets the size of the morph targets in vertex counts.
         *
         * @param vertexCount Number of vertices the morph targets can hold.
         * @return This Builder, for chaining calls.
         */
        fun vertexCount(vertexCount: Int): Builder {
            FilaMorphTargetBufferBuilder_vertexCount(nativeBuilder, vertexCount)
            return this
        }

        /**
         * Sets the number of morph targets to allocate.
         *
         * @param count Number of morph targets (e.g., blend shapes) to allocate.
         * @return This Builder, for chaining calls.
         */
        fun count(count: Int): Builder {
            FilaMorphTargetBufferBuilder_count(nativeBuilder, count)
            return this
        }

        /**
         * Enable automatic morphing for positions.
         *
         * When enabled, the MorphTargetBuffer allocates internal storage for position
         * morph targets. Upload data via setPositionsAt().
         *
         * @param enabled true to enable position morphing, false to disable.
         * @return This Builder, for chaining calls.
         */
        fun withPositions(enabled: Boolean = true): Builder {
            FilaMorphTargetBufferBuilder_withPositions(nativeBuilder, enabled)
            return this
        }

        /**
         * Enable automatic morphing for tangents.
         *
         * When enabled, the MorphTargetBuffer allocates internal storage for tangent
         * morph targets. Upload data via setTangentsAt().
         *
         * @param enabled true to enable tangent morphing, false to disable.
         * @return This Builder, for chaining calls.
         */
        fun withTangents(enabled: Boolean = true): Builder {
            FilaMorphTargetBufferBuilder_withTangents(nativeBuilder, enabled)
            return this
        }

        /**
         * Enable the custom morphing pipeline for user-defined attributes.
         *
         * When enabled, you can supply custom morph target data via a Texture and
         * apply it manually in the vertex shader.
         *
         * @param enabled true to enable custom morphing, false to disable.
         * @return This Builder, for chaining calls.
         */
        fun enableCustomMorphing(enabled: Boolean = true): Builder {
            FilaMorphTargetBufferBuilder_enableCustomMorphing(nativeBuilder, enabled)
            return this
        }

        /**
         * Associates an optional name with this MorphTargetBuffer for debugging purposes.
         *
         * The name shows up in error messages and should be kept short.
         */
        fun name(name: String): Builder = apply { interopScope { FilaMorphTargetBufferBuilder_name(nativeBuilder, toInterop(name)) } }

        /**
         * Creates the MorphTargetBuffer and associates it with the given Engine.
         *
         * @param engine Engine to associate this MorphTargetBuffer with.
         * @return The newly created MorphTargetBuffer.
         */
        fun build(engine: Engine): MorphTargetBuffer {
            val handle = FilaMorphTargetBufferBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaMorphTargetBufferBuilder_destroy(nativeBuilder)
            return MorphTargetBuffer(handle)
        }
    }

    /**
     * Gets the number of vertices this MorphTargetBuffer can hold.
     *
     * @return Vertex count capacity.
     */
    val vertexCount: Int get() = FilaMorphTargetBuffer_getVertexCount(nativeHandle)
    /**
     * Gets the number of morph targets (blend shapes) allocated.
     *
     * @return Number of morph targets.
     */
    val count: Int get() = FilaMorphTargetBuffer_getCount(nativeHandle)
    /**
     * Indicates whether this buffer supports automatic position morphing.
     *
     * @return true if position morphing is enabled, false otherwise.
     */
    val hasPositions: Boolean get() = FilaMorphTargetBuffer_hasPositions(nativeHandle)
    /**
     * Indicates whether this buffer supports automatic tangent morphing.
     *
     * @return true if tangent morphing is enabled, false otherwise.
     */
    val hasTangents: Boolean get() = FilaMorphTargetBuffer_hasTangents(nativeHandle)
    /**
     * Indicates whether custom morphing is enabled for user-defined attributes.
     *
     * @return true if custom morphing is enabled, false otherwise.
     */
    val isCustomMorphingEnabled: Boolean get() = FilaMorphTargetBuffer_isCustomMorphingEnabled(nativeHandle)

    /**
     * Updates positions for the given morph target. Requires a buffer built with
     * `withPositions(true)`; the 4th component is 1.0.
     *
     * @param engine The Engine instance.
     * @param targetIndex Zero-based index of the morph target to update (< count).
     * @param positions At least [count] positions, 3 floats per vertex (x, y, z).
     * @param count Number of vertices being updated.
     * @param offset Offset into the target buffer, in vertices.
     */
    fun setPositionsAt(engine: Engine, targetIndex: Int, positions: FloatArray, count: Int, offset: Int = 0) {
        positions.usePinned { FilaMorphTargetBuffer_setPositionsAt_float3_size_t_size_t(nativeHandle, engine.nativeHandle, targetIndex, it, count, offset) }
    }

    /**
     * Updates tangents for the given morph target. Requires a buffer built with
     * `withTangents(true)`.
     *
     * @param engine The Engine instance.
     * @param targetIndex Zero-based index of the morph target to update (< count).
     * @param tangents At least [count] quaternions, 4 shorts each: components in [-1, 1] times 32767.
     * @param count Number of vertices being updated.
     * @param offset Offset into the target buffer, in vertices.
     */
    fun setTangentsAt(engine: Engine, targetIndex: Int, tangents: ShortArray, count: Int, offset: Int = 0) {
        tangents.usePinned { FilaMorphTargetBuffer_setTangentsAt(nativeHandle, engine.nativeHandle, targetIndex, it, count, offset) }
    }
}
