@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaTexture

actual class Texture internal constructor(internal var nativeHandle: CPointer<FilaTexture>?) {
    actual class Builder actual constructor() {
        private val nativeBuilder = FilaTextureBuilder_create()

        actual fun width(width: Int): Builder = apply { FilaTextureBuilder_width(nativeBuilder, width.toUInt()) }
        actual fun height(height: Int): Builder = apply { FilaTextureBuilder_height(nativeBuilder, height.toUInt()) }
        actual fun depth(depth: Int): Builder = apply { FilaTextureBuilder_depth(nativeBuilder, depth.toUInt()) }
        actual fun levels(levels: Int): Builder = apply { FilaTextureBuilder_levels(nativeBuilder, levels.toUByte()) }
        actual fun samples(samples: Int): Builder = apply { FilaTextureBuilder_samples(nativeBuilder, samples.toUByte()) }
        actual fun sampler(target: Sampler): Builder = apply { FilaTextureBuilder_sampler(nativeBuilder, target.ordinal.toUInt()) }
        actual fun format(format: InternalFormat): Builder = apply { FilaTextureBuilder_format(nativeBuilder, format.ordinal.toUInt()) }
        actual fun usage(usage: Int): Builder = apply { FilaTextureBuilder_usage(nativeBuilder, usage.toUInt()) }
        
        actual fun swizzle(r: Swizzle, g: Swizzle, b: Swizzle, a: Swizzle): Builder = apply {
            FilaTextureBuilder_swizzle(nativeBuilder, r.ordinal.toUInt(), g.ordinal.toUInt(), b.ordinal.toUInt(), a.ordinal.toUInt())
        }

        actual fun importTexture(id: Long): Builder = apply {
            FilaTextureBuilder_importTexture(nativeBuilder, id.toLong())
        }

        actual fun external(): Builder = apply {
            FilaTextureBuilder_external(nativeBuilder)
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
            actual val COLOR_ATTACHMENT = 0x0001
            actual val DEPTH_ATTACHMENT = 0x0002
            actual val STENCIL_ATTACHMENT = 0x0004
            actual val UPLOADABLE = 0x0008
            actual val SAMPLEABLE = 0x0010
            actual val SUBPASS_INPUT = 0x0020
            actual val BLIT_SRC = 0x0040
            actual val BLIT_DST = 0x0080
            actual val PROTECTED = 0x0100
            actual val GEN_MIPMAPPABLE = 0x0200
            actual val DEFAULT = 0x0008 or 0x0010
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
        actual val stride: Int,
        actual val handler: Any?,
        actual val callback: (() -> Unit)?
    )

    actual fun getWidth(level: Int): Int = FilaTexture_getWidth(nativeHandle, level.toULong()).toInt()
    actual fun getHeight(level: Int): Int = FilaTexture_getHeight(nativeHandle, level.toULong()).toInt()
    actual fun getDepth(level: Int): Int = FilaTexture_getDepth(nativeHandle, level.toULong()).toInt()
    actual fun getLevels(): Int = FilaTexture_getLevels(nativeHandle).toInt()
    actual fun getTarget(): Sampler = Sampler.values()[FilaTexture_getTarget(nativeHandle).toInt()]
    actual fun getFormat(): InternalFormat = InternalFormat.values()[FilaTexture_getFormat(nativeHandle).toInt()]

    actual fun setImage(engine: Engine, level: Int, descriptor: PixelBufferDescriptor) {
        setImage(engine, level, 0, 0, 0, getWidth(level), getHeight(level), 1, descriptor)
    }

    actual fun setImage(engine: Engine, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int, descriptor: PixelBufferDescriptor) {
        setImage(engine, level, xoffset, yoffset, 0, width, height, 1, descriptor)
    }

    actual fun setImage(engine: Engine, level: Int, xoffset: Int, yoffset: Int, zoffset: Int, width: Int, height: Int, depth: Int, descriptor: PixelBufferDescriptor) {
        descriptor.storage.usePinned { pinned ->
            FilaTexture_setImage(
                nativeHandle, engine.nativeHandle, level.toULong(),
                xoffset.toUInt(), yoffset.toUInt(), zoffset.toUInt(),
                width.toUInt(), height.toUInt(), depth.toUInt(),
                pinned.addressOf(0), descriptor.sizeInBytes.toULong(),
                descriptor.format.ordinal.toUInt(), descriptor.type.ordinal.toUInt(),
                descriptor.alignment.toUByte(), descriptor.left.toUInt(), descriptor.top.toUInt(), descriptor.stride.toUInt(),
                null, null, null // TODO: handle callback
            )
        }
    }

    actual fun generateMipmaps(engine: Engine) {
        FilaTexture_generateMipmaps(nativeHandle, engine.nativeHandle)
    }

    actual fun setExternalImage(engine: Engine, eglImage: Long) {
        FilaTexture_setExternalImage(nativeHandle, engine.nativeHandle, eglImage.toCPointer())
    }

    actual fun setExternalStream(engine: Engine, stream: Stream) {
        FilaTexture_setExternalStream(nativeHandle, engine.nativeHandle, stream.nativeHandle)
    }

    actual companion object {
        actual fun isTextureFormatSupported(engine: Engine, format: InternalFormat): Boolean =
            FilaTexture_isTextureFormatSupported(engine.nativeHandle, format.ordinal.toUInt())
        actual fun isTextureFormatMipmappable(engine: Engine, format: InternalFormat): Boolean =
            FilaTexture_isTextureFormatMipmappable(engine.nativeHandle, format.ordinal.toUInt())
        actual fun isTextureSwizzleSupported(engine: Engine): Boolean =
            FilaTexture_isTextureSwizzleSupported(engine.nativeHandle)
        actual fun validatePixelFormatAndType(internalFormat: InternalFormat, pixelDataFormat: Format, pixelDataType: Type): Boolean =
            FilaTexture_validatePixelFormatAndType(internalFormat.ordinal.toUInt(), pixelDataFormat.ordinal.toUInt(), pixelDataType.ordinal.toUInt())
        actual fun getMaxTextureSize(engine: Engine, type: Sampler): Int =
            FilaTexture_getMaxTextureSize(engine.nativeHandle, type.ordinal.toUInt()).toInt()
        actual fun getMaxArrayTextureLayers(engine: Engine): Int =
            FilaTexture_getMaxArrayTextureLayers(engine.nativeHandle).toInt()
    }
}

private fun Any.usePinned(block: (Pinned<*>) -> Unit) {
    when (this) {
        is ByteArray -> this.pin().use { block(it) }
        is FloatArray -> this.pin().use { block(it) }
        is ShortArray -> this.pin().use { block(it) }
        is IntArray -> this.pin().use { block(it) }
        else -> throw IllegalArgumentException("Unsupported storage type for pinning")
    }
}
