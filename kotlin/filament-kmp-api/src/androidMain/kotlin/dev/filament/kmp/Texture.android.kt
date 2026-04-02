package dev.filament.kmp

import com.google.android.filament.Texture as AndroidTexture

actual class Texture internal constructor(
    internal var androidTexture: AndroidTexture?,
) {
    actual fun getWidth(level: Int): Int {
        val texture = requireNotNull(androidTexture) { "Calling method on destroyed Texture" }
        return texture.getWidth(level)
    }

    actual fun getHeight(level: Int): Int {
        val texture = requireNotNull(androidTexture) { "Calling method on destroyed Texture" }
        return texture.getHeight(level)
    }

    actual fun getDepth(level: Int): Int {
        val texture = requireNotNull(androidTexture) { "Calling method on destroyed Texture" }
        return texture.getDepth(level)
    }

    actual fun getLevels(): Int {
        val texture = requireNotNull(androidTexture) { "Calling method on destroyed Texture" }
        return texture.levels
    }

    actual fun getTarget(): Sampler {
        val texture = requireNotNull(androidTexture) { "Calling method on destroyed Texture" }
        return texture.target.toKmp()
    }

    actual fun getFormat(): InternalFormat {
        val texture = requireNotNull(androidTexture) { "Calling method on destroyed Texture" }
        return texture.format.toKmp()
    }

    actual fun getNativeObject(): Long {
        val texture = requireNotNull(androidTexture) { "Calling method on destroyed Texture" }
        return texture.nativeObject
    }

    actual internal fun invalidate() {
        androidTexture = null
    }

    actual class Builder {
        private val androidBuilder = AndroidTexture.Builder()

        actual fun width(width: Int): Builder {
            androidBuilder.width(width)
            return this
        }

        actual fun height(height: Int): Builder {
            androidBuilder.height(height)
            return this
        }

        actual fun depth(depth: Int): Builder {
            androidBuilder.depth(depth)
            return this
        }

        actual fun levels(levels: Int): Builder {
            androidBuilder.levels(levels)
            return this
        }

        actual fun sampler(target: Sampler): Builder {
            androidBuilder.sampler(target.toAndroid())
            return this
        }

        actual fun samples(samples: Int): Builder {
            androidBuilder.samples(samples)
            return this
        }

        actual fun format(format: InternalFormat): Builder {
            androidBuilder.format(format.toAndroid())
            return this
        }

        actual fun usage(flags: Int): Builder {
            androidBuilder.usage(flags)
            return this
        }

        actual fun build(engine: Engine): Texture {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return Texture(androidBuilder.build(androidEngine))
        }
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
        actual val COLOR_ATTACHMENT: Int = AndroidTexture.Usage.COLOR_ATTACHMENT
        actual val DEPTH_ATTACHMENT: Int = AndroidTexture.Usage.DEPTH_ATTACHMENT
        actual val STENCIL_ATTACHMENT: Int = AndroidTexture.Usage.STENCIL_ATTACHMENT
        actual val UPLOADABLE: Int = AndroidTexture.Usage.UPLOADABLE
        actual val SAMPLEABLE: Int = AndroidTexture.Usage.SAMPLEABLE
        actual val SUBPASS_INPUT: Int = AndroidTexture.Usage.SUBPASS_INPUT
        actual val BLIT_SRC: Int = AndroidTexture.Usage.BLIT_SRC
        actual val BLIT_DST: Int = AndroidTexture.Usage.BLIT_DST
        actual val PROTECTED: Int = AndroidTexture.Usage.PROTECTED
        actual val GEN_MIPMAPPABLE: Int = AndroidTexture.Usage.GEN_MIPMAPPABLE
        actual val DEFAULT: Int = AndroidTexture.Usage.DEFAULT
    }

    actual companion object {
        actual val BASE_LEVEL: Int = AndroidTexture.BASE_LEVEL

        actual fun isTextureFormatSupported(engine: Engine, format: InternalFormat): Boolean {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return AndroidTexture.isTextureFormatSupported(androidEngine, format.toAndroid())
        }

        actual fun isTextureFormatMipmappable(engine: Engine, format: InternalFormat): Boolean {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return AndroidTexture.isTextureFormatMipmappable(androidEngine, format.toAndroid())
        }

        actual fun isTextureSwizzleSupported(engine: Engine): Boolean {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return AndroidTexture.isTextureSwizzleSupported(androidEngine)
        }

        actual fun getMaxTextureSize(engine: Engine, type: Sampler): Int {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return AndroidTexture.getMaxTextureSize(androidEngine, type.toAndroid())
        }

        actual fun getMaxArrayTextureLayers(engine: Engine): Int {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return AndroidTexture.getMaxArrayTextureLayers(androidEngine)
        }
    }
}

private fun Texture.Sampler.toAndroid(): AndroidTexture.Sampler = when (this) {
    Texture.Sampler.SAMPLER_2D -> AndroidTexture.Sampler.SAMPLER_2D
    Texture.Sampler.SAMPLER_2D_ARRAY -> AndroidTexture.Sampler.SAMPLER_2D_ARRAY
    Texture.Sampler.SAMPLER_CUBEMAP -> AndroidTexture.Sampler.SAMPLER_CUBEMAP
    Texture.Sampler.SAMPLER_EXTERNAL -> AndroidTexture.Sampler.SAMPLER_EXTERNAL
    Texture.Sampler.SAMPLER_3D -> AndroidTexture.Sampler.SAMPLER_3D
}

private fun AndroidTexture.Sampler.toKmp(): Texture.Sampler = when (this) {
    AndroidTexture.Sampler.SAMPLER_2D -> Texture.Sampler.SAMPLER_2D
    AndroidTexture.Sampler.SAMPLER_2D_ARRAY -> Texture.Sampler.SAMPLER_2D_ARRAY
    AndroidTexture.Sampler.SAMPLER_CUBEMAP -> Texture.Sampler.SAMPLER_CUBEMAP
    AndroidTexture.Sampler.SAMPLER_EXTERNAL -> Texture.Sampler.SAMPLER_EXTERNAL
    AndroidTexture.Sampler.SAMPLER_3D -> Texture.Sampler.SAMPLER_3D
}

private fun Texture.InternalFormat.toAndroid(): AndroidTexture.InternalFormat = when (this) {
    Texture.InternalFormat.RGBA8 -> AndroidTexture.InternalFormat.RGBA8
    Texture.InternalFormat.SRGB8_A8 -> AndroidTexture.InternalFormat.SRGB8_A8
    Texture.InternalFormat.DEPTH24 -> AndroidTexture.InternalFormat.DEPTH24
    Texture.InternalFormat.DEPTH24_STENCIL8 -> AndroidTexture.InternalFormat.DEPTH24_STENCIL8
    Texture.InternalFormat.DEPTH32F -> AndroidTexture.InternalFormat.DEPTH32F
    Texture.InternalFormat.DEPTH32F_STENCIL8 -> AndroidTexture.InternalFormat.DEPTH32F_STENCIL8
}

private fun AndroidTexture.InternalFormat.toKmp(): Texture.InternalFormat = when (this) {
    AndroidTexture.InternalFormat.RGBA8 -> Texture.InternalFormat.RGBA8
    AndroidTexture.InternalFormat.SRGB8_A8 -> Texture.InternalFormat.SRGB8_A8
    AndroidTexture.InternalFormat.DEPTH24 -> Texture.InternalFormat.DEPTH24
    AndroidTexture.InternalFormat.DEPTH24_STENCIL8 -> Texture.InternalFormat.DEPTH24_STENCIL8
    AndroidTexture.InternalFormat.DEPTH32F -> Texture.InternalFormat.DEPTH32F
    AndroidTexture.InternalFormat.DEPTH32F_STENCIL8 -> Texture.InternalFormat.DEPTH32F_STENCIL8
    else -> Texture.InternalFormat.RGBA8
}

internal fun Texture.CubemapFace.toAndroid(): AndroidTexture.CubemapFace = when (this) {
    Texture.CubemapFace.POSITIVE_X -> AndroidTexture.CubemapFace.POSITIVE_X
    Texture.CubemapFace.NEGATIVE_X -> AndroidTexture.CubemapFace.NEGATIVE_X
    Texture.CubemapFace.POSITIVE_Y -> AndroidTexture.CubemapFace.POSITIVE_Y
    Texture.CubemapFace.NEGATIVE_Y -> AndroidTexture.CubemapFace.NEGATIVE_Y
    Texture.CubemapFace.POSITIVE_Z -> AndroidTexture.CubemapFace.POSITIVE_Z
    Texture.CubemapFace.NEGATIVE_Z -> AndroidTexture.CubemapFace.NEGATIVE_Z
}

internal fun AndroidTexture.CubemapFace.toKmp(): Texture.CubemapFace = when (this) {
    AndroidTexture.CubemapFace.POSITIVE_X -> Texture.CubemapFace.POSITIVE_X
    AndroidTexture.CubemapFace.NEGATIVE_X -> Texture.CubemapFace.NEGATIVE_X
    AndroidTexture.CubemapFace.POSITIVE_Y -> Texture.CubemapFace.POSITIVE_Y
    AndroidTexture.CubemapFace.NEGATIVE_Y -> Texture.CubemapFace.NEGATIVE_Y
    AndroidTexture.CubemapFace.POSITIVE_Z -> Texture.CubemapFace.POSITIVE_Z
    AndroidTexture.CubemapFace.NEGATIVE_Z -> Texture.CubemapFace.NEGATIVE_Z
}

