@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaTexture

actual class Texture internal constructor(internal var nativeHandle: CPointer<FilaTexture>?) {
    actual class Builder actual constructor() {
        private val nativeBuilder = FilaTextureBuilder_create()

        actual fun width(width: Int): Builder {
            FilaTextureBuilder_width(nativeBuilder, width.toUInt())
            return this
        }
        actual fun height(height: Int): Builder {
            FilaTextureBuilder_height(nativeBuilder, height.toUInt())
            return this
        }
        actual fun depth(depth: Int): Builder {
            FilaTextureBuilder_depth(nativeBuilder, depth.toUInt())
            return this
        }
        actual fun levels(levels: Int): Builder {
            FilaTextureBuilder_levels(nativeBuilder, levels.toUByte())
            return this
        }
        actual fun samples(samples: Int): Builder {
            FilaTextureBuilder_samples(nativeBuilder, samples.toUByte())
            return this
        }
        actual fun sampler(target: Sampler): Builder {
            FilaTextureBuilder_sampler(nativeBuilder, target.ordinal.toUInt())
            return this
        }
        actual fun format(format: InternalFormat): Builder {
            FilaTextureBuilder_format(nativeBuilder, format.ordinal.toUInt())
            return this
        }
        actual fun usage(usage: Int): Builder {
            FilaTextureBuilder_usage(nativeBuilder, usage.toUInt())
            return this
        }
        actual fun swizzle(r: Swizzle, g: Swizzle, b: Swizzle, a: Swizzle): Builder {
            FilaTextureBuilder_swizzle(nativeBuilder, r.ordinal.toUInt(), g.ordinal.toUInt(), b.ordinal.toUInt(), a.ordinal.toUInt())
            return this
        }
        actual fun build(engine: Engine): Texture {
            val handle = FilaTextureBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaTextureBuilder_destroy(nativeBuilder)
            return Texture(handle)
        }
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
            actual val COLOR_ATTACHMENT = 0x1
            actual val SAMPLEABLE = 0x10
            actual val DEPTH_ATTACHMENT = 0x2
            actual val STENCIL_ATTACHMENT = 0x4
            actual val UPLOADABLE = 0x8
            actual val BLIT_SRC = 0x40
            actual val BLIT_DST = 0x80
        }
    }

    actual class PixelBufferDescriptor actual constructor(
        actual val storage: Any,
        actual val sizeInBytes: Int,
        actual val format: Format,
        actual val type: Type,
        actual val alignment: Int,
        actual val left: Int,
        actual val top: Int,
        actual val stride: Int
    )

    actual fun getWidth(level: Int): Int = FilaTexture_getWidth(nativeHandle, level.toULong()).toInt()
    actual fun getHeight(level: Int): Int = FilaTexture_getHeight(nativeHandle, level.toULong()).toInt()
    actual fun getDepth(level: Int): Int = FilaTexture_getDepth(nativeHandle, level.toULong()).toInt()
    actual fun getLevels(): Int = FilaTexture_getLevels(nativeHandle).toInt()
    actual fun getTarget(): Sampler = Sampler.values()[FilaTexture_getTarget(nativeHandle).toInt()]
    actual fun getFormat(): InternalFormat = InternalFormat.values()[FilaTexture_getFormat(nativeHandle).toInt()]

    actual fun setImage(engine: Engine, level: Int, descriptor: PixelBufferDescriptor) {
        val width = getWidth(level)
        val height = getHeight(level)
        val depth = getDepth(level)
        setImage(engine, level, 0, 0, 0, width, height, depth, descriptor)
    }

    actual fun setImage(engine: Engine, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int, descriptor: PixelBufferDescriptor) {
        setImage(engine, level, xoffset, yoffset, 0, width, height, 1, descriptor)
    }

    actual fun setImage(engine: Engine, level: Int, xoffset: Int, yoffset: Int, zoffset: Int, width: Int, height: Int, depth: Int, descriptor: PixelBufferDescriptor) {
        val ptr = descriptor.storage as? CPointer<*>
        FilaTexture_setImage(
            nativeHandle, engine.nativeHandle, level.toULong(),
            xoffset.toUInt(), yoffset.toUInt(), zoffset.toUInt(),
            width.toUInt(), height.toUInt(), depth.toUInt(),
            ptr, descriptor.sizeInBytes.toULong(),
            descriptor.format.ordinal.toUInt(), descriptor.type.ordinal.toUInt(),
            descriptor.alignment.toUByte(),
            descriptor.left.toUInt(), descriptor.top.toUInt(), descriptor.stride.toUInt(),
            null, null, null
        )
    }

    actual fun generateMipmaps(engine: Engine) {
        FilaTexture_generateMipmaps(nativeHandle, engine.nativeHandle)
    }
}
