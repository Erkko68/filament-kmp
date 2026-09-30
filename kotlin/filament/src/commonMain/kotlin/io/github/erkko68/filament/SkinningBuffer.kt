package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
import io.github.erkko68.filament.interop.*

/**
 * SkinningBuffer is used to hold skinning data (bones) for skeletal animation.
 *
 * SkinningBuffer is a simple wrapper around a structured Uniform Buffer Object (UBO) that stores
 * bone transformations. It is used by RenderableManager to deform vertices according to bone
 * transformations and weights during skeletal animation (rigged animations).
 *
 * **Important constraint:** Due to GLSL limitations, the SkinningBuffer size must always be a
 * multiple of 256 bones. The builder automatically adjusts the requested bone count to meet this
 * requirement, which may cause some memory overhead. This overhead can be mitigated by using the
 * same SkinningBuffer to store bone information for multiple RenderPrimitives.
 *
 * @see RenderableManager.setSkinningBuffer
 */
class SkinningBuffer @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * Builder for creating SkinningBuffer instances.
     *
     * Configure the number of bones and optional pre-initialization, then call build() to create
     * the SkinningBuffer.
     */
    class Builder() {
        private val nativeBuilder = FilaSkinningBufferBuilder_create()

        /**
         * Sets the number of bones in this buffer.
         *
         * The buffer size will be automatically adjusted to the nearest multiple of 256 bones
         * due to GLSL constraints.
         *
         * @param boneCount Number of bones to allocate
         * @return This Builder, for chaining calls
         */
        fun boneCount(boneCount: Int): Builder {
            FilaSkinningBufferBuilder_boneCount(nativeBuilder, boneCount)
            return this
        }

        /**
         * Sets whether to initialize the buffer with identity bones.
         *
         * When true, the new buffer is created with all bones set to identity transforms.
         * When false (default), the buffer is created uninitialized.
         *
         * @param initialize true to initialize with identity bones, false to leave uninitialized
         * @return This Builder, for chaining calls
         */
        fun initialize(initialize: Boolean): Builder {
            FilaSkinningBufferBuilder_initialize(nativeBuilder, initialize)
            return this
        }

        /**
         * Associates an optional name with this SkinningBuffer for debugging purposes.
         *
         * The name shows up in error messages and should be kept short.
         */
        fun name(name: String): Builder = apply { interopScope { FilaSkinningBufferBuilder_name(nativeBuilder, toInterop(name)) } }

        /**
         * Creates the SkinningBuffer object.
         *
         * @param engine Engine to associate this SkinningBuffer with
         * @return The newly created SkinningBuffer
         */
        fun build(engine: Engine): SkinningBuffer {
            val handle = FilaSkinningBufferBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaSkinningBufferBuilder_destroy(nativeBuilder)
            return SkinningBuffer(handle)
        }
    }

    /**
     * Gets the number of bones in this buffer.
     *
     * @return The bone count (adjusted to nearest multiple of 256)
     */
    val boneCount: Int get() = FilaSkinningBuffer_getBoneCount(nativeHandle)

    /**
     * Updates bone transforms in the range [offset, offset + count).
     *
     * @param engine The engine
     * @param transforms The bone transforms
     * @param count Number of bones to set
     * @param offset Index of the first bone to set in the SkinningBuffer
     */
    fun setBones(engine: Engine, transforms: Array<RenderableManager.Bone>, count: Int = transforms.size, offset: Int = 0) {
        transforms.toFloats().usePinned { FilaSkinningBuffer_setBones_Bone_size_t_size_t(nativeHandle, engine.nativeHandle, it, count, offset) }
    }

    /**
     * Updates bone transforms in the range [offset, offset + count).
     *
     * @param engine The engine
     * @param transforms 4x4 bone transforms, 16 floats per bone
     * @param count Number of bones to set
     * @param offset Index of the first bone to set in the SkinningBuffer
     */
    fun setBones(engine: Engine, transforms: FloatArray, count: Int = transforms.size / 16, offset: Int = 0) {
        transforms.usePinned { FilaSkinningBuffer_setBones_mat4f_size_t_size_t(nativeHandle, engine.nativeHandle, it, count, offset) }
    }
}
