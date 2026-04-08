package dev.filament.kmp

import com.google.android.filament.Texture as AndroidTexture
import java.nio.Buffer

actual class Texture public constructor(val nativeTexture: AndroidTexture) {
    actual class Builder actual constructor() {
        private val nativeBuilder = AndroidTexture.Builder()

        actual fun width(width: Int): Builder {
            nativeBuilder.width(width)
            return this
        }
        actual fun height(height: Int): Builder {
            nativeBuilder.height(height)
            return this
        }
        actual fun depth(depth: Int): Builder {
            nativeBuilder.depth(depth)
            return this
        }
        actual fun levels(levels: Int): Builder {
            nativeBuilder.levels(levels)
            return this
        }
        actual fun samples(samples: Int): Builder {
            nativeBuilder.samples(samples)
            return this
        }
        actual fun sampler(target: Sampler): Builder {
            nativeBuilder.sampler(AndroidTexture.Sampler.values()[target.ordinal])
            return this
        }
        actual fun format(format: InternalFormat): Builder {
            nativeBuilder.format(AndroidTexture.InternalFormat.values()[format.ordinal])
            return this
        }
        actual fun usage(usage: Int): Builder {
            nativeBuilder.usage(usage)
            return this
        }
        actual fun swizzle(r: Swizzle, g: Swizzle, b: Swizzle, a: Swizzle): Builder {
            nativeBuilder.swizzle(
                AndroidTexture.Swizzle.values()[r.ordinal],
                AndroidTexture.Swizzle.values()[g.ordinal],
                AndroidTexture.Swizzle.values()[b.ordinal],
                AndroidTexture.Swizzle.values()[a.ordinal]
            )
            return this
        }
        actual fun importTexture(id: Long): Builder {
            nativeBuilder.importTexture(id)
            return this
        }
        actual fun external(): Builder {
            nativeBuilder.external()
            return this
        }
        actual fun build(engine: Engine): Texture = Texture(nativeBuilder.build(engine.nativeEngine))
    }

    actual enum class Sampler { SAMPLER_2D, SAMPLER_2D_ARRAY, SAMPLER_CUBEMAP, SAMPLER_EXTERNAL, SAMPLER_3D, SAMPLER_CUBEMAP_ARRAY }

    actual enum class InternalFormat {
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

    actual enum class CubemapFace { POSITIVE_X, NEGATIVE_X, POSITIVE_Y, NEGATIVE_Y, POSITIVE_Z, NEGATIVE_Z }

    actual enum class Format { R, R_INTEGER, RG, RG_INTEGER, RGB, RGB_INTEGER, RGBA, RGBA_INTEGER, UNUSED, DEPTH_COMPONENT, DEPTH_STENCIL, STENCIL_INDEX, ALPHA }

    actual enum class Type { UBYTE, BYTE, USHORT, SHORT, UINT, INT, HALF, FLOAT, COMPRESSED, UINT_10F_11F_11F_REV, USHORT_565 }

    actual enum class Swizzle { SUBSTITUTE_ZERO, SUBSTITUTE_ONE, CHANNEL_0, CHANNEL_1, CHANNEL_2, CHANNEL_3 }

    actual class Usage {
        actual companion object {
            actual val COLOR_ATTACHMENT = AndroidTexture.Usage.COLOR_ATTACHMENT
            actual val DEPTH_ATTACHMENT = AndroidTexture.Usage.DEPTH_ATTACHMENT
            actual val STENCIL_ATTACHMENT = AndroidTexture.Usage.STENCIL_ATTACHMENT
            actual val UPLOADABLE = AndroidTexture.Usage.UPLOADABLE
            actual val SAMPLEABLE = AndroidTexture.Usage.SAMPLEABLE
            actual val SUBPASS_INPUT = AndroidTexture.Usage.SUBPASS_INPUT
            actual val BLIT_SRC = AndroidTexture.Usage.BLIT_SRC
            actual val BLIT_DST = AndroidTexture.Usage.BLIT_DST
            actual val PROTECTED = AndroidTexture.Usage.PROTECTED
            actual val GEN_MIPMAPPABLE = AndroidTexture.Usage.GEN_MIPMAPPABLE
            actual val DEFAULT = AndroidTexture.Usage.DEFAULT
        }
    }

    actual class PixelBufferDescriptor actual constructor(
        actual val storage: ByteArray,
        actual val sizeInBytes: Int,
        actual val format: Format,
        actual val type: Type,
        actual val alignment: Int,
        actual val left: Int,
        actual val top: Int,
        actual val stride: Int,
        actual val handler: Any?,
        actual val callback: (() -> Unit)?
    ) {
        internal fun toNative(): AndroidTexture.PixelBufferDescriptor {
            val byteBuffer = java.nio.ByteBuffer.allocateDirect(storage.size).apply {
                order(java.nio.ByteOrder.nativeOrder())
                put(storage)
                flip()
            }
            val runnable = if (callback != null) Runnable { callback.invoke() } else null
            return AndroidTexture.PixelBufferDescriptor(
                byteBuffer,
                AndroidTexture.Format.values()[format.ordinal],
                AndroidTexture.Type.values()[type.ordinal],
                alignment, left, top, stride, handler, runnable
            )
        }
    }

    actual fun getWidth(level: Int): Int = nativeTexture.getWidth(level)
    actual fun getHeight(level: Int): Int = nativeTexture.getHeight(level)
    actual fun getDepth(level: Int): Int = nativeTexture.getDepth(level)
    actual fun getLevels(): Int = nativeTexture.levels
    actual fun getTarget(): Sampler = Sampler.values()[nativeTexture.target.ordinal]
    actual fun getFormat(): InternalFormat = InternalFormat.values()[nativeTexture.format.ordinal]

    actual fun setImage(engine: Engine, level: Int, descriptor: PixelBufferDescriptor) {
        nativeTexture.setImage(engine.nativeEngine, level, descriptor.toNative())
    }

    actual fun setImage(engine: Engine, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int, descriptor: PixelBufferDescriptor) {
        nativeTexture.setImage(engine.nativeEngine, level, xoffset, yoffset, width, height, descriptor.toNative())
    }

    actual fun setImage(engine: Engine, level: Int, xoffset: Int, yoffset: Int, zoffset: Int, width: Int, height: Int, depth: Int, descriptor: PixelBufferDescriptor) {
        nativeTexture.setImage(engine.nativeEngine, level, xoffset, yoffset, zoffset, width, height, depth, descriptor.toNative())
    }

    actual fun generateMipmaps(engine: Engine) {
        nativeTexture.generateMipmaps(engine.nativeEngine)
    }

    actual fun setExternalStream(engine: Engine, stream: Stream) {
        nativeTexture.setExternalStream(engine.nativeEngine, stream.nativeStream)
    }

    actual companion object {
        actual fun isTextureFormatSupported(engine: Engine, format: InternalFormat): Boolean =
            AndroidTexture.isTextureFormatSupported(engine.nativeEngine, AndroidTexture.InternalFormat.values()[format.ordinal])
        actual fun isTextureFormatMipmappable(engine: Engine, format: InternalFormat): Boolean =
            AndroidTexture.isTextureFormatMipmappable(engine.nativeEngine, AndroidTexture.InternalFormat.values()[format.ordinal])
        actual fun isTextureSwizzleSupported(engine: Engine): Boolean =
            AndroidTexture.isTextureSwizzleSupported(engine.nativeEngine)
        actual fun validatePixelFormatAndType(internalFormat: InternalFormat, pixelDataFormat: Format, pixelDataType: Type): Boolean =
            AndroidTexture.validatePixelFormatAndType(
                AndroidTexture.InternalFormat.values()[internalFormat.ordinal],
                AndroidTexture.Format.values()[pixelDataFormat.ordinal],
                AndroidTexture.Type.values()[pixelDataType.ordinal]
            )
        actual fun getMaxTextureSize(engine: Engine, type: Sampler): Int =
            AndroidTexture.getMaxTextureSize(engine.nativeEngine, AndroidTexture.Sampler.values()[type.ordinal])
        actual fun getMaxArrayTextureLayers(engine: Engine): Int =
            AndroidTexture.getMaxArrayTextureLayers(engine.nativeEngine)
    }
}
