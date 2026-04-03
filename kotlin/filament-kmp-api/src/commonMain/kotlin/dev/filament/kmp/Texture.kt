package dev.filament.kmp

expect class Texture {
    enum class Sampler {
        SAMPLER_2D,
        SAMPLER_2D_ARRAY,
        SAMPLER_CUBEMAP,
        SAMPLER_EXTERNAL,
        SAMPLER_3D,
    }

    enum class InternalFormat {
        RGBA8,
        SRGB8_A8,
        DEPTH24,
        DEPTH24_STENCIL8,
        DEPTH32F,
        DEPTH32F_STENCIL8,
    }

    enum class CubemapFace {
        POSITIVE_X,
        NEGATIVE_X,
        POSITIVE_Y,
        NEGATIVE_Y,
        POSITIVE_Z,
        NEGATIVE_Z,
    }

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
        ALPHA,
    }

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

    enum class Swizzle {
        SUBSTITUTE_ZERO,
        SUBSTITUTE_ONE,
        CHANNEL_0,
        CHANNEL_1,
        CHANNEL_2,
        CHANNEL_3,
    }

    class PrefilterOptions {
        var sampleCount: Int
        var mirror: Boolean
    }

    object Usage {
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

    class Builder {
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

    fun getWidth(level: Int): Int

    fun getHeight(level: Int): Int

    fun getDepth(level: Int): Int

    fun getLevels(): Int

    fun getTarget(): Sampler

    fun getFormat(): InternalFormat

    fun setImage(engine: Engine, level: Int, buffer: Any)

    fun setImage(engine: Engine, level: Int, buffer: Any, faceOffsetsInBytes: IntArray)

    fun setImage(engine: Engine, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Any)

    fun setImage(
        engine: Engine,
        level: Int,
        xoffset: Int,
        yoffset: Int,
        zoffset: Int,
        width: Int,
        height: Int,
        depth: Int,
        buffer: Any,
    )

    fun setExternalImage(engine: Engine, eglImage: Long)

    fun setExternalImage(engine: Engine, externalImageRef: Any)

    fun setExternalStream(engine: Engine, stream: Stream)

    fun generateMipmaps(engine: Engine)

    fun generatePrefilterMipmap(engine: Engine, buffer: Any, faceOffsetsInBytes: IntArray, options: PrefilterOptions?)

    fun getNativeObject(): Long

    internal fun invalidate()

    companion object {
        val BASE_LEVEL: Int

        fun isTextureFormatSupported(engine: Engine, format: InternalFormat): Boolean

        fun isTextureFormatMipmappable(engine: Engine, format: InternalFormat): Boolean

        fun isTextureSwizzleSupported(engine: Engine): Boolean

        fun validatePixelFormatAndType(internalFormat: InternalFormat, pixelDataFormat: Format, pixelDataType: Type): Boolean

        fun getMaxTextureSize(engine: Engine, type: Sampler): Int

        fun getMaxArrayTextureLayers(engine: Engine): Int
    }
}
