package dev.filament.kmp

import com.google.android.filament.Texture as AndroidTexture

actual class Texture internal constructor(val nativeTexture: AndroidTexture) {
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
        actual fun build(engine: Engine): Texture = Texture(nativeBuilder.build(engine.nativeEngine))
    }

    actual enum class Sampler { SAMPLER_2D, SAMPLER_2D_ARRAY, SAMPLER_CUBEMAP, SAMPLER_EXTERNAL, SAMPLER_3D }

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

    actual class Usage {
        actual companion object {
            actual val COLOR_ATTACHMENT = 0x1
            actual val SAMPLEABLE = 0x2
            actual val DEPTH_ATTACHMENT = 0x4
            actual val STENCIL_ATTACHMENT = 0x8
            actual val UPLOADABLE = 0x10
            actual val BLIT_SRC = 0x20
            actual val BLIT_DST = 0x40
        }
    }

    actual fun getWidth(level: Int): Int = nativeTexture.getWidth(level)
    actual fun getHeight(level: Int): Int = nativeTexture.getHeight(level)
    actual fun getDepth(level: Int): Int = nativeTexture.getDepth(level)
    actual fun getLevels(): Int = nativeTexture.levels
    actual fun getTarget(): Sampler = Sampler.values()[nativeTexture.target.ordinal]
    actual fun getFormat(): InternalFormat = InternalFormat.values()[nativeTexture.format.ordinal]
}
