package dev.filament.kmp

/**
 * Texture
 * <p>The `Texture` class supports:</p>
 * <ul>
 *  <li>2D textures</li>
 *  <li>3D textures</li>
 *  <li>Cube maps</li>
 *  <li>mip mapping</li>
 * </ul>
 *
 *
 * <h1>Usage example</h1>
 *
 * A `Texture` object is created using the [Texture.Builder] and destroyed by
 * calling [Engine.destroyTexture]. They're bound using [MaterialInstance.setParameter].
 *
 * <pre>
 *  val engine = Engine.create()
 *
 *  val material = Material.Builder()
 *              .payload( ... )
 *              .build(engine)
 *
 *  val mi = material.getDefaultInstance()
 *
 *  val texture = Texture.Builder()
 *              .width(64)
 *              .height(64)
 *              .build(engine)
 *
 *
 *  texture.setImage(engine, 0,
 *          Texture.PixelBufferDescriptor( ... ))
 *
 *  mi.setParameter("parameterName", texture, TextureSampler())
 * </pre>
 *
 * @see setImage
 * @see PixelBufferDescriptor
 * @see MaterialInstance.setParameter
 */
expect class Texture {

    /**
     * Type of sampler
     */
    enum class Sampler {
        SAMPLER_2D,
        SAMPLER_2D_ARRAY,
        SAMPLER_CUBEMAP,
        SAMPLER_EXTERNAL,
        SAMPLER_3D
    }

    /**
     * Internal texel formats
     */
    enum class InternalFormat {
        R8, R8_SNORM, R8UI, R8I, STENCIL8,
        R16F, R16UI, R16I,
        RG8, RG8_SNORM, RG8UI, RG8I,
        RGB565, RGB9_E5, RGB5_A1,
        RGBA4,
        DEPTH16,
        RGB8, SRGB8, RGB8_SNORM, RGB8UI, RGB8I,
        DEPTH24,
        R32F, R32UI, R32I,
        RG16F, RG16UI, RG16I,
        R11F_G11F_B10F,
        RGBA8, SRGB8_A8, RGBA8_SNORM,
        UNUSED,
        RGB10_A2, RGBA8UI, RGBA8I,
        DEPTH32F, DEPTH24_STENCIL8, DEPTH32F_STENCIL8,
        RGB16F, RGB16UI, RGB16I,
        RG32F, RG32UI, RG32I,
        RGBA16F, RGBA16UI, RGBA16I,
        RGB32F, RGB32UI, RGB32I,
        RGBA32F, RGBA32UI, RGBA32I,
        EAC_R11, EAC_R11_SIGNED, EAC_RG11, EAC_RG11_SIGNED,
        ETC2_RGB8, ETC2_SRGB8,
        ETC2_RGB8_A1, ETC2_SRGB8_A1,
        ETC2_EAC_RGBA8, ETC2_EAC_SRGBA8,
        DXT1_RGB, DXT1_RGBA, DXT3_RGBA, DXT5_RGBA,
        DXT1_SRGB, DXT1_SRGBA, DXT3_SRGBA, DXT5_SRGBA,
        RGBA_ASTC_4x4,
        RGBA_ASTC_5x4,
        RGBA_ASTC_5x5,
        RGBA_ASTC_6x5,
        RGBA_ASTC_6x6,
        RGBA_ASTC_8x5,
        RGBA_ASTC_8x6,
        RGBA_ASTC_8x8,
        RGBA_ASTC_10x5,
        RGBA_ASTC_10x6,
        RGBA_ASTC_10x8,
        RGBA_ASTC_10x10,
        RGBA_ASTC_12x10,
        RGBA_ASTC_12x12,
        SRGB8_ALPHA8_ASTC_4x4,
        SRGB8_ALPHA8_ASTC_5x4,
        SRGB8_ALPHA8_ASTC_5x5,
        SRGB8_ALPHA8_ASTC_6x5,
        SRGB8_ALPHA8_ASTC_6x6,
        SRGB8_ALPHA8_ASTC_8x5,
        SRGB8_ALPHA8_ASTC_8x6,
        SRGB8_ALPHA8_ASTC_8x8,
        SRGB8_ALPHA8_ASTC_10x5,
        SRGB8_ALPHA8_ASTC_10x6,
        SRGB8_ALPHA8_ASTC_10x8,
        SRGB8_ALPHA8_ASTC_10x10,
        SRGB8_ALPHA8_ASTC_12x10,
        SRGB8_ALPHA8_ASTC_12x12,
        RED_RGTC1,
        SIGNED_RED_RGTC1,
        RED_GREEN_RGTC2,
        SIGNED_RED_GREEN_RGTC2,
        RGB_BPTC_SIGNED_FLOAT,
        RGB_BPTC_UNSIGNED_FLOAT,
        RGBA_BPTC_UNORM,
        SRGB_ALPHA_BPTC_UNORM
    }

    /**
     * Compressed data types for use with [PixelBufferDescriptor]
     */
    enum class CompressedFormat {
        EAC_R11, EAC_R11_SIGNED, EAC_RG11, EAC_RG11_SIGNED,
        ETC2_RGB8, ETC2_SRGB8,
        ETC2_RGB8_A1, ETC2_SRGB8_A1,
        ETC2_EAC_RGBA8, ETC2_EAC_SRGBA8,
        DXT1_RGB, DXT1_RGBA, DXT3_RGBA, DXT5_RGBA,
        DXT1_SRGB, DXT1_SRGBA, DXT3_SRGBA, DXT5_SRGBA,
        RGBA_ASTC_4x4,
        RGBA_ASTC_5x4,
        RGBA_ASTC_5x5,
        RGBA_ASTC_6x5,
        RGBA_ASTC_6x6,
        RGBA_ASTC_8x5,
        RGBA_ASTC_8x6,
        RGBA_ASTC_8x8,
        RGBA_ASTC_10x5,
        RGBA_ASTC_10x6,
        RGBA_ASTC_10x8,
        RGBA_ASTC_10x10,
        RGBA_ASTC_12x10,
        RGBA_ASTC_12x12,
        SRGB8_ALPHA8_ASTC_4x4,
        SRGB8_ALPHA8_ASTC_5x4,
        SRGB8_ALPHA8_ASTC_5x5,
        SRGB8_ALPHA8_ASTC_6x5,
        SRGB8_ALPHA8_ASTC_6x6,
        SRGB8_ALPHA8_ASTC_8x5,
        SRGB8_ALPHA8_ASTC_8x6,
        SRGB8_ALPHA8_ASTC_8x8,
        SRGB8_ALPHA8_ASTC_10x5,
        SRGB8_ALPHA8_ASTC_10x6,
        SRGB8_ALPHA8_ASTC_10x8,
        SRGB8_ALPHA8_ASTC_10x10,
        SRGB8_ALPHA8_ASTC_12x10,
        SRGB8_ALPHA8_ASTC_12x12,
        RED_RGTC1,
        SIGNED_RED_RGTC1,
        RED_GREEN_RGTC2,
        SIGNED_RED_GREEN_RGTC2,
        RGB_BPTC_SIGNED_FLOAT,
        RGB_BPTC_UNSIGNED_FLOAT,
        RGBA_BPTC_UNORM,
        SRGB_ALPHA_BPTC_UNORM
    }

    /**
     * Cubemap faces
     */
    enum class CubemapFace {
        POSITIVE_X,
        NEGATIVE_X,
        POSITIVE_Y,
        NEGATIVE_Y,
        POSITIVE_Z,
        NEGATIVE_Z
    }

    /**
     * Pixel color format
     */
    enum class Format {
        R,
        R_INTEGER,
        RG,
        RG_INTEGER,
        RGB,
        RGB_INTEGER,
        RGBA,
        RGBA_INTEGER,
        UNUSED,
        DEPTH_COMPONENT,
        DEPTH_STENCIL,
        STENCIL_INDEX,
        ALPHA
    }

    /**
     * Pixel data type
     */
    enum class Type {
        UBYTE,
        BYTE,
        USHORT,
        SHORT,
        UINT,
        INT,
        HALF,
        FLOAT,
        COMPRESSED,
        UINT_10F_11F_11F_REV,
        USHORT_565,
    }

    /**
     * Texture swizzling channels
     */
    enum class Swizzle {
        SUBSTITUTE_ZERO,
        SUBSTITUTE_ONE,
        CHANNEL_0,
        CHANNEL_1,
        CHANNEL_2,
        CHANNEL_3
    }

    /**
     * A descriptor to an image in main memory, typically used to transfer image data from the CPU
     * to the GPU.
     */
    class PixelBufferDescriptor {
        var storage: Buffer
        var type: Type
        var alignment: Int
        var left: Int
        var top: Int
        var stride: Int
        var format: Format
        var compressedSizeInBytes: Int
        var compressedFormat: CompressedFormat
        var handler: Any?
        var callback: Runnable?

        constructor(storage: Buffer, format: Format, type: Type, alignment: Int, left: Int, top: Int, stride: Int, handler: Any?, callback: Runnable?)
        constructor(storage: Buffer, format: Format, type: Type)
        constructor(storage: Buffer, format: Format, type: Type, alignment: Int)
        constructor(storage: Buffer, format: Format, type: Type, alignment: Int, left: Int, top: Int)
        constructor(storage: Buffer, format: CompressedFormat, compressedSizeInBytes: Int)

        fun setCallback(handler: Any?, callback: Runnable?)

        companion object {
            fun computeDataSize(format: Format, type: Type, stride: Int, height: Int, alignment: Int): Int
        }
    }

    /**
     * Options of [generatePrefilterMipmap]
     */
    class PrefilterOptions {
        var sampleCount: Int
        var mirror: Boolean
    }

    /**
     * A bitmask to specify how the texture will be used.
     */
    class Usage {
        companion object {
            const val COLOR_ATTACHMENT: Int
            const val DEPTH_ATTACHMENT: Int
            const val STENCIL_ATTACHMENT: Int
            const val UPLOADABLE: Int
            const val SAMPLEABLE: Int
            const val SUBPASS_INPUT: Int
            const val BLIT_SRC: Int
            const val BLIT_DST: Int
            const val PROTECTED: Int
            const val GEN_MIPMAPPABLE: Int
            val DEFAULT: Int
        }
    }

    class Builder() {
        fun width(width: Int): Builder
        fun height(height: Int): Builder
        fun depth(depth: Int): Builder
        fun levels(levels: Int): Builder
        fun sampler(target: Sampler): Builder
        fun samples(samples: Int): Builder
        fun format(format: InternalFormat): Builder
        fun usage(flags: Int): Builder
        fun swizzle(r: Swizzle, g: Swizzle, b: Swizzle, a: Swizzle): Builder
        fun importTexture(id: Long): Builder
        fun external(): Builder
        fun build(engine: Engine): Texture
    }

    companion object {
        const val BASE_LEVEL: Int

        fun isTextureFormatSupported(engine: Engine, format: InternalFormat): Boolean
        fun isTextureFormatMipmappable(engine: Engine, format: InternalFormat): Boolean
        fun isTextureSwizzleSupported(engine: Engine): Boolean
        fun validatePixelFormatAndType(internalFormat: InternalFormat, pixelDataFormat: Format, pixelDataType: Type): Boolean
        fun getMaxTextureSize(engine: Engine, type: Sampler): Int
        fun getMaxArrayTextureLayers(engine: Engine): Int
    }

    fun getWidth(level: Int): Int
    fun getHeight(level: Int): Int
    fun getDepth(level: Int): Int
    fun getLevels(): Int
    fun getTarget(): Sampler
    fun getFormat(): InternalFormat

    fun setImage(engine: Engine, level: Int, buffer: PixelBufferDescriptor)
    fun setImage(engine: Engine, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: PixelBufferDescriptor)
    fun setImage(engine: Engine, level: Int, xoffset: Int, yoffset: Int, zoffset: Int, width: Int, height: Int, depth: Int, buffer: PixelBufferDescriptor)
    fun setImage(engine: Engine, level: Int, buffer: PixelBufferDescriptor, faceOffsetsInBytes: IntArray)

    fun setExternalImage(engine: Engine, eglImage: Long)
    fun setExternalImage(engine: Engine, externalImageRef: Any)
    fun setExternalStream(engine: Engine, stream: Stream)

    fun generateMipmaps(engine: Engine)
    fun generatePrefilterMipmap(engine: Engine, buffer: PixelBufferDescriptor, faceOffsetsInBytes: IntArray, options: PrefilterOptions?)
}
