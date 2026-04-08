package dev.filament.kmp

expect class Texture {
    class Builder() {
        fun width(width: Int): Builder
        fun height(height: Int): Builder
        fun depth(depth: Int): Builder
        fun levels(levels: Int): Builder
        fun samples(samples: Int): Builder
        fun sampler(target: Sampler): Builder
        fun format(format: InternalFormat): Builder
        fun usage(usage: Int): Builder
        fun swizzle(r: Swizzle, g: Swizzle, b: Swizzle, a: Swizzle): Builder
        fun importTexture(id: Long): Builder
        fun external(): Builder
        fun build(engine: Engine): Texture
    }

    enum class Sampler { SAMPLER_2D, SAMPLER_2D_ARRAY, SAMPLER_CUBEMAP, SAMPLER_EXTERNAL, SAMPLER_3D, SAMPLER_CUBEMAP_ARRAY }

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
        RGBA_ASTC_4x4, RGBA_ASTC_5x4, RGBA_ASTC_5x5, RGBA_ASTC_6x5, RGBA_ASTC_6x6,
        RGBA_ASTC_8x5, RGBA_ASTC_8x6, RGBA_ASTC_8x8,
        RGBA_ASTC_10x5, RGBA_ASTC_10x6, RGBA_ASTC_10x8, RGBA_ASTC_10x10,
        RGBA_ASTC_12x10, RGBA_ASTC_12x12,
        SRGB8_ALPHA8_ASTC_4x4, SRGB8_ALPHA8_ASTC_5x4, SRGB8_ALPHA8_ASTC_5x5,
        SRGB8_ALPHA8_ASTC_6x5, SRGB8_ALPHA8_ASTC_6x6,
        SRGB8_ALPHA8_ASTC_8x5, SRGB8_ALPHA8_ASTC_8x6, SRGB8_ALPHA8_ASTC_8x8,
        SRGB8_ALPHA8_ASTC_10x5, SRGB8_ALPHA8_ASTC_10x6, SRGB8_ALPHA8_ASTC_10x8,
        SRGB8_ALPHA8_ASTC_10x10, SRGB8_ALPHA8_ASTC_12x10, SRGB8_ALPHA8_ASTC_12x12,
        RED_RGTC1, SIGNED_RED_RGTC1, RED_GREEN_RGTC2, SIGNED_RED_GREEN_RGTC2,
        RGB_BPTC_SIGNED_FLOAT, RGB_BPTC_UNSIGNED_FLOAT, RGBA_BPTC_UNORM, SRGB_ALPHA_BPTC_UNORM
    }

    enum class CubemapFace { POSITIVE_X, NEGATIVE_X, POSITIVE_Y, NEGATIVE_Y, POSITIVE_Z, NEGATIVE_Z }

    enum class Format { R, R_INTEGER, RG, RG_INTEGER, RGB, RGB_INTEGER, RGBA, RGBA_INTEGER, UNUSED, DEPTH_COMPONENT, DEPTH_STENCIL, STENCIL_INDEX, ALPHA }

    enum class Type { UBYTE, BYTE, USHORT, SHORT, UINT, INT, HALF, FLOAT, COMPRESSED, UINT_10F_11F_11F_REV, USHORT_565 }

    enum class Swizzle { SUBSTITUTE_ZERO, SUBSTITUTE_ONE, CHANNEL_0, CHANNEL_1, CHANNEL_2, CHANNEL_3 }

    class Usage {
        companion object {
            val COLOR_ATTACHMENT: Int
            val DEPTH_ATTACHMENT: Int
            val STENCIL_ATTACHMENT: Int
            val UPLOADABLE: Int
            val SAMPLEABLE: Int
            val SUBPASS_INPUT: Int
            val BLIT_SRC: Int
            val BLIT_DST: Int
            val PROTECTED: Int
            val GEN_MIPMAPPABLE: Int
            val DEFAULT: Int
        }
    }

    class PixelBufferDescriptor(
        storage: ByteArray,
        sizeInBytes: Int,
        format: Format,
        type: Type,
        alignment: Int = 1,
        left: Int = 0,
        top: Int = 0,
        stride: Int = 0,
        handler: Any? = null,
        callback: (() -> Unit)? = null
    ) {
        val storage: ByteArray
        val sizeInBytes: Int
        val format: Format
        val type: Type
        val alignment: Int
        val left: Int
        val top: Int
        val stride: Int
        val handler: Any?
        val callback: (() -> Unit)?
    }

    fun getWidth(level: Int = 0): Int
    fun getHeight(level: Int = 0): Int
    fun getDepth(level: Int = 0): Int
    fun getLevels(): Int
    fun getTarget(): Sampler
    fun getFormat(): InternalFormat

    fun setImage(engine: Engine, level: Int, descriptor: PixelBufferDescriptor)
    fun setImage(engine: Engine, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int, descriptor: PixelBufferDescriptor)
    fun setImage(engine: Engine, level: Int, xoffset: Int, yoffset: Int, zoffset: Int, width: Int, height: Int, depth: Int, descriptor: PixelBufferDescriptor)
    fun setExternalStream(engine: Engine, stream: Stream)
    fun generateMipmaps(engine: Engine)

    companion object {
        fun isTextureFormatSupported(engine: Engine, format: InternalFormat): Boolean
        fun isTextureFormatMipmappable(engine: Engine, format: InternalFormat): Boolean
        fun isTextureSwizzleSupported(engine: Engine): Boolean
        fun validatePixelFormatAndType(internalFormat: InternalFormat, pixelDataFormat: Format, pixelDataType: Type): Boolean
        fun getMaxTextureSize(engine: Engine, type: Sampler): Int
        fun getMaxArrayTextureLayers(engine: Engine): Int
    }
}
