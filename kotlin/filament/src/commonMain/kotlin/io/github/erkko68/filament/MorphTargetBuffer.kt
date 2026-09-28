package io.github.erkko68.filament

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
         * Creates the MorphTargetBuffer and associates it with the given Engine.
         *
         * @param engine Engine to associate this MorphTargetBuffer with.
         * @return The newly created MorphTargetBuffer.
         * @throws UnsupportedOperationException on JS — MorphTargetBuffer is unbound in the web wrapper.
         */
        fun build(engine: Engine): MorphTargetBuffer {
            val handle = FilaMorphTargetBufferBuilder_build(nativeBuilder, engine.pointer)
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
     * Upload position data for a specific morph target.
     *
     * The positions array must contain `count` vertices, with 3 floats per vertex (x, y, z).
     *
     * @param engine The Engine instance.
     * @param targetIndex Zero-based index of the morph target to update (< count).
     * @param positions Array of position offsets (3 floats per vertex).
     * @param count Number of vertices being updated.
     */
    fun setPositionsAt(engine: Engine, targetIndex: Int, positions: FloatArray, count: Int) {
        positions.usePinned { pinned ->
            FilaMorphTargetBuffer_setPositionsAt(
                nativeHandle,
                engine.pointer,
                targetIndex,
                pinned,
                count
            )
        }
    }

    /**
     * Upload tangent data for a specific morph target.
     *
     * The tangents array must contain `count` vertices, with 2 shorts per vertex (quaternion encoded).
     *
     * @param engine The Engine instance.
     * @param targetIndex Zero-based index of the morph target to update (< count).
     * @param tangents Array of tangent offsets (encoded as quaternions).
     * @param count Number of vertices being updated.
     */
    fun setTangentsAt(engine: Engine, targetIndex: Int, tangents: ShortArray, count: Int) {
        tangents.usePinned { pinned ->
            FilaMorphTargetBuffer_setTangentsAt(
                nativeHandle,
                engine.pointer,
                targetIndex,
                pinned,
                count
            )
        }
    }
}

@ExternalSymbolName("FilaMorphTargetBufferBuilder_build")
private external fun FilaMorphTargetBufferBuilder_build(builder: NativePointer, engine: NativePointer): NativePointer

@ExternalSymbolName("FilaMorphTargetBufferBuilder_count")
private external fun FilaMorphTargetBufferBuilder_count(builder: NativePointer, count: Int)

@ExternalSymbolName("FilaMorphTargetBufferBuilder_create")
private external fun FilaMorphTargetBufferBuilder_create(): NativePointer

@ExternalSymbolName("FilaMorphTargetBufferBuilder_destroy")
private external fun FilaMorphTargetBufferBuilder_destroy(builder: NativePointer)

@ExternalSymbolName("FilaMorphTargetBufferBuilder_enableCustomMorphing")
private external fun FilaMorphTargetBufferBuilder_enableCustomMorphing(builder: NativePointer, enabled: Boolean)

@ExternalSymbolName("FilaMorphTargetBufferBuilder_vertexCount")
private external fun FilaMorphTargetBufferBuilder_vertexCount(builder: NativePointer, vertexCount: Int)

@ExternalSymbolName("FilaMorphTargetBufferBuilder_withPositions")
private external fun FilaMorphTargetBufferBuilder_withPositions(builder: NativePointer, enabled: Boolean)

@ExternalSymbolName("FilaMorphTargetBufferBuilder_withTangents")
private external fun FilaMorphTargetBufferBuilder_withTangents(builder: NativePointer, enabled: Boolean)

@ExternalSymbolName("FilaMorphTargetBuffer_getCount")
private external fun FilaMorphTargetBuffer_getCount(buffer: NativePointer): Int

@ExternalSymbolName("FilaMorphTargetBuffer_getVertexCount")
private external fun FilaMorphTargetBuffer_getVertexCount(buffer: NativePointer): Int

@ExternalSymbolName("FilaMorphTargetBuffer_hasPositions")
private external fun FilaMorphTargetBuffer_hasPositions(buffer: NativePointer): Boolean

@ExternalSymbolName("FilaMorphTargetBuffer_hasTangents")
private external fun FilaMorphTargetBuffer_hasTangents(buffer: NativePointer): Boolean

@ExternalSymbolName("FilaMorphTargetBuffer_isCustomMorphingEnabled")
private external fun FilaMorphTargetBuffer_isCustomMorphingEnabled(buffer: NativePointer): Boolean

@ExternalSymbolName("FilaMorphTargetBuffer_setPositionsAt")
private external fun FilaMorphTargetBuffer_setPositionsAt(buffer: NativePointer, engine: NativePointer, targetIndex: Int, positions: NativePointer, count: Int)

@ExternalSymbolName("FilaMorphTargetBuffer_setTangentsAt")
private external fun FilaMorphTargetBuffer_setTangentsAt(buffer: NativePointer, engine: NativePointer, targetIndex: Int, tangents: NativePointer, count: Int)
