package io.github.erkko68.filament

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
         * Creates the SkinningBuffer object.
         *
         * @param engine Engine to associate this SkinningBuffer with
         * @return The newly created SkinningBuffer
         * @throws UnsupportedOperationException on JS — SkinningBuffer is unbound in the web wrapper.
         */
        fun build(engine: Engine): SkinningBuffer {
            val handle = FilaSkinningBufferBuilder_build(nativeBuilder, engine.pointer)
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
     * Updates bone transforms in the range [offset, offset + boneCount).
     *
     * Each bone is specified as a 4x4 transformation matrix in row-major order.
     *
     * @param engine The engine
     * @param matrices Array of 4x4 matrices (16 floats per matrix)
     * @param boneCount Number of bones to set
     * @param offset Offset in elements (not bytes) in the SkinningBuffer (default: 0)
     */
    fun setBonesAsMatrices(engine: Engine, matrices: FloatArray, boneCount: Int, offset: Int) {
        matrices.usePinned { pinned ->
            FilaSkinningBuffer_setBonesMat4f(
                nativeHandle, engine.pointer,
                pinned,
                boneCount, offset
            )
        }
    }

    /**
     * Updates bone transforms in the range [offset, offset + boneCount) using quaternion+translation format.
     *
     * Each bone is specified as a quaternion (4 floats: x, y, z, w) followed by a translation (3 floats: x, y, z).
     * This format is more compact than 4x4 matrices (7 floats per bone vs 16).
     *
     * @param engine The engine
     * @param bones Array of quaternions and translations (7 floats per bone: qx, qy, qz, qw, tx, ty, tz)
     * @param boneCount Number of bones to set
     * @param offset Offset in elements (not bytes) in the SkinningBuffer (default: 0)
     */
    fun setBonesAsQuaternions(engine: Engine, bones: FloatArray, boneCount: Int, offset: Int) {
        // Each bone is 8 floats: [qx,qy,qz,qw, tx,ty,tz,1] — matches FilaBone memory layout.
        bones.usePinned { pinned ->
            FilaSkinningBuffer_setBonesQuaternions(
                nativeHandle, engine.pointer,
                pinned,
                boneCount, offset
            )
        }
    }
}

@ExternalSymbolName("FilaSkinningBufferBuilder_boneCount")
private external fun FilaSkinningBufferBuilder_boneCount(builder: NativePointer, boneCount: Int)

@ExternalSymbolName("FilaSkinningBufferBuilder_build")
private external fun FilaSkinningBufferBuilder_build(builder: NativePointer, engine: NativePointer): NativePointer

@ExternalSymbolName("FilaSkinningBufferBuilder_create")
private external fun FilaSkinningBufferBuilder_create(): NativePointer

@ExternalSymbolName("FilaSkinningBufferBuilder_destroy")
private external fun FilaSkinningBufferBuilder_destroy(builder: NativePointer)

@ExternalSymbolName("FilaSkinningBufferBuilder_initialize")
private external fun FilaSkinningBufferBuilder_initialize(builder: NativePointer, initialize: Boolean)

@ExternalSymbolName("FilaSkinningBuffer_getBoneCount")
private external fun FilaSkinningBuffer_getBoneCount(buffer: NativePointer): Int

@ExternalSymbolName("FilaSkinningBuffer_setBonesMat4f")
private external fun FilaSkinningBuffer_setBonesMat4f(buffer: NativePointer, engine: NativePointer, matrices: NativePointer, boneCount: Int, offset: Int)

@ExternalSymbolName("FilaSkinningBuffer_setBonesQuaternions")
private external fun FilaSkinningBuffer_setBonesQuaternions(buffer: NativePointer, engine: NativePointer, bones: NativePointer, boneCount: Int, offset: Int)
