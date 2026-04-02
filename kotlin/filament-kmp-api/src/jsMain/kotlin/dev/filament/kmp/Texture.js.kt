package dev.filament.kmp

actual class Texture {
    actual fun getWidth(level: Int): Int = TODO("Not yet implemented")

    actual fun getHeight(level: Int): Int = TODO("Not yet implemented")

    actual fun getDepth(level: Int): Int = TODO("Not yet implemented")

    actual fun getLevels(): Int = TODO("Not yet implemented")

    actual fun getTarget(): Sampler = TODO("Not yet implemented")

    actual fun getFormat(): InternalFormat = TODO("Not yet implemented")

    actual fun getNativeObject(): Long = TODO("Not yet implemented")

    actual internal fun invalidate() {
    }

    actual class Builder {
        actual fun width(width: Int): Builder = TODO("Not yet implemented")

        actual fun height(height: Int): Builder = TODO("Not yet implemented")

        actual fun depth(depth: Int): Builder = TODO("Not yet implemented")

        actual fun levels(levels: Int): Builder = TODO("Not yet implemented")

        actual fun sampler(target: Sampler): Builder = TODO("Not yet implemented")

        actual fun samples(samples: Int): Builder = TODO("Not yet implemented")

        actual fun format(format: InternalFormat): Builder = TODO("Not yet implemented")

        actual fun usage(flags: Int): Builder = TODO("Not yet implemented")

        actual fun build(engine: Engine): Texture = TODO("Not yet implemented")
    }

    actual enum class Sampler {
        SAMPLER_2D,
        SAMPLER_2D_ARRAY,
        SAMPLER_CUBEMAP,
        SAMPLER_EXTERNAL,
        SAMPLER_3D,
    }

    actual enum class InternalFormat {
        RGBA8,
        SRGB8_A8,
        DEPTH24,
        DEPTH24_STENCIL8,
        DEPTH32F,
        DEPTH32F_STENCIL8,
    }

    actual enum class CubemapFace {
        POSITIVE_X,
        NEGATIVE_X,
        POSITIVE_Y,
        NEGATIVE_Y,
        POSITIVE_Z,
        NEGATIVE_Z,
    }

    actual object Usage {
        actual val COLOR_ATTACHMENT: Int = 0x1
        actual val DEPTH_ATTACHMENT: Int = 0x2
        actual val STENCIL_ATTACHMENT: Int = 0x4
        actual val UPLOADABLE: Int = 0x8
        actual val SAMPLEABLE: Int = 0x10
        actual val SUBPASS_INPUT: Int = 0x20
        actual val BLIT_SRC: Int = 0x40
        actual val BLIT_DST: Int = 0x80
        actual val PROTECTED: Int = 0x0100
        actual val GEN_MIPMAPPABLE: Int = 0x0200
        actual val DEFAULT: Int = UPLOADABLE or SAMPLEABLE
    }

    actual companion object {
        actual val BASE_LEVEL: Int = 0

        actual fun isTextureFormatSupported(engine: Engine, format: InternalFormat): Boolean {
            TODO("Not yet implemented")
        }

        actual fun isTextureFormatMipmappable(engine: Engine, format: InternalFormat): Boolean {
            TODO("Not yet implemented")
        }

        actual fun isTextureSwizzleSupported(engine: Engine): Boolean {
            TODO("Not yet implemented")
        }

        actual fun getMaxTextureSize(engine: Engine, type: Sampler): Int {
            TODO("Not yet implemented")
        }

        actual fun getMaxArrayTextureLayers(engine: Engine): Int {
            TODO("Not yet implemented")
        }
    }
}

