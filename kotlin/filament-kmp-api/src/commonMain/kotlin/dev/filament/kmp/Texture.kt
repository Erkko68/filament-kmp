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

        fun build(engine: Engine): Texture
    }

    fun getWidth(level: Int): Int

    fun getHeight(level: Int): Int

    fun getDepth(level: Int): Int

    fun getLevels(): Int

    fun getTarget(): Sampler

    fun getFormat(): InternalFormat

    fun getNativeObject(): Long

    internal fun invalidate()

    companion object {
        val BASE_LEVEL: Int

        fun isTextureFormatSupported(engine: Engine, format: InternalFormat): Boolean

        fun isTextureFormatMipmappable(engine: Engine, format: InternalFormat): Boolean

        fun isTextureSwizzleSupported(engine: Engine): Boolean

        fun getMaxTextureSize(engine: Engine, type: Sampler): Int

        fun getMaxArrayTextureLayers(engine: Engine): Int
    }
}

