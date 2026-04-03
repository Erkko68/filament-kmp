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

    actual fun setImage(engine: Engine, level: Int, buffer: Any) {
        val texture = requireNotNull(androidTexture) { "Calling method on destroyed Texture" }
        val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
        texture.setImage(androidEngine, level, buffer as AndroidTexture.PixelBufferDescriptor)
    }

    actual fun setImage(engine: Engine, level: Int, buffer: Any, faceOffsetsInBytes: IntArray) {
        val texture = requireNotNull(androidTexture) { "Calling method on destroyed Texture" }
        val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
        texture.setImage(androidEngine, level, buffer as AndroidTexture.PixelBufferDescriptor, faceOffsetsInBytes)
    }

    actual fun setImage(
        engine: Engine,
        level: Int,
        xoffset: Int,
        yoffset: Int,
        width: Int,
        height: Int,
        buffer: Any,
    ) {
        val texture = requireNotNull(androidTexture) { "Calling method on destroyed Texture" }
        val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
        texture.setImage(androidEngine, level, xoffset, yoffset, width, height, buffer as AndroidTexture.PixelBufferDescriptor)
    }

    actual fun setImage(
        engine: Engine,
        level: Int,
        xoffset: Int,
        yoffset: Int,
        zoffset: Int,
        width: Int,
        height: Int,
        depth: Int,
        buffer: Any,
    ) {
        val texture = requireNotNull(androidTexture) { "Calling method on destroyed Texture" }
        val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
        texture.setImage(
            androidEngine,
            level,
            xoffset,
            yoffset,
            zoffset,
            width,
            height,
            depth,
            buffer as AndroidTexture.PixelBufferDescriptor,
        )
    }

    actual fun setExternalImage(engine: Engine, eglImage: Long) {
        val texture = requireNotNull(androidTexture) { "Calling method on destroyed Texture" }
        val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
        texture.setExternalImage(androidEngine, eglImage)
    }

    actual fun setExternalImage(engine: Engine, externalImageRef: Any) {
        val texture = requireNotNull(androidTexture) { "Calling method on destroyed Texture" }
        val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
        texture.setExternalImage(androidEngine, externalImageRef)
    }

    actual fun setExternalStream(engine: Engine, stream: Stream) {
        val texture = requireNotNull(androidTexture) { "Calling method on destroyed Texture" }
        val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
        val androidStream = requireNotNull(stream.androidStream) { "Calling method on destroyed Stream" }
        texture.setExternalStream(androidEngine, androidStream)
    }

    actual fun generateMipmaps(engine: Engine) {
        val texture = requireNotNull(androidTexture) { "Calling method on destroyed Texture" }
        val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
        texture.generateMipmaps(androidEngine)
    }

    actual fun generatePrefilterMipmap(engine: Engine, buffer: Any, faceOffsetsInBytes: IntArray, options: PrefilterOptions?) {
        val texture = requireNotNull(androidTexture) { "Calling method on destroyed Texture" }
        val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
        texture.generatePrefilterMipmap(
            androidEngine,
            buffer as AndroidTexture.PixelBufferDescriptor,
            faceOffsetsInBytes,
            options?.toAndroid(),
        )
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

        actual fun swizzle(r: Swizzle, g: Swizzle, b: Swizzle, a: Swizzle): Builder {
            androidBuilder.swizzle(r.toAndroid(), g.toAndroid(), b.toAndroid(), a.toAndroid())
            return this
        }

        actual fun importTexture(id: Long): Builder {
            androidBuilder.importTexture(id)
            return this
        }

        actual fun external(): Builder {
            androidBuilder.external()
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

    actual enum class Format {
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

    actual enum class Type {
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

    actual enum class Swizzle {
        SUBSTITUTE_ZERO,
        SUBSTITUTE_ONE,
        CHANNEL_0,
        CHANNEL_1,
        CHANNEL_2,
        CHANNEL_3,
    }

    actual class PrefilterOptions {
        actual var sampleCount: Int = 8
        actual var mirror: Boolean = true
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

        actual fun validatePixelFormatAndType(
            internalFormat: InternalFormat,
            pixelDataFormat: Format,
            pixelDataType: Type,
        ): Boolean {
            return AndroidTexture.validatePixelFormatAndType(
                internalFormat.toAndroid(),
                pixelDataFormat.toAndroid(),
                pixelDataType.toAndroid(),
            )
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

private fun Texture.Sampler.toAndroid(): AndroidTexture.Sampler = AndroidTexture.Sampler.valueOf(name)

private fun AndroidTexture.Sampler.toKmp(): Texture.Sampler = Texture.Sampler.valueOf(name)

private fun Texture.InternalFormat.toAndroid(): AndroidTexture.InternalFormat = AndroidTexture.InternalFormat.valueOf(name)

private fun AndroidTexture.InternalFormat.toKmp(): Texture.InternalFormat = when (this) {
    AndroidTexture.InternalFormat.RGBA8 -> Texture.InternalFormat.RGBA8
    AndroidTexture.InternalFormat.SRGB8_A8 -> Texture.InternalFormat.SRGB8_A8
    AndroidTexture.InternalFormat.DEPTH24 -> Texture.InternalFormat.DEPTH24
    AndroidTexture.InternalFormat.DEPTH24_STENCIL8 -> Texture.InternalFormat.DEPTH24_STENCIL8
    AndroidTexture.InternalFormat.DEPTH32F -> Texture.InternalFormat.DEPTH32F
    AndroidTexture.InternalFormat.DEPTH32F_STENCIL8 -> Texture.InternalFormat.DEPTH32F_STENCIL8
    else -> Texture.InternalFormat.RGBA8
}

private fun Texture.Format.toAndroid(): AndroidTexture.Format = AndroidTexture.Format.valueOf(name)

private fun Texture.Type.toAndroid(): AndroidTexture.Type = AndroidTexture.Type.valueOf(name)

private fun Texture.Swizzle.toAndroid(): AndroidTexture.Swizzle = AndroidTexture.Swizzle.valueOf(name)

private fun Texture.PrefilterOptions.toAndroid(): AndroidTexture.PrefilterOptions =
    AndroidTexture.PrefilterOptions().also {
        it.sampleCount = sampleCount
        it.mirror = mirror
    }

internal fun Texture.CubemapFace.toAndroid(): AndroidTexture.CubemapFace = AndroidTexture.CubemapFace.valueOf(name)

internal fun AndroidTexture.CubemapFace.toKmp(): Texture.CubemapFace = Texture.CubemapFace.valueOf(name)
