@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*
import cnames.structs.FilaTexture

actual class Texture public constructor(public var nativeHandle: CPointer<FilaTexture>?) {

    actual enum class Sampler {
        SAMPLER_2D, SAMPLER_2D_ARRAY, SAMPLER_CUBEMAP, SAMPLER_EXTERNAL, SAMPLER_3D, SAMPLER_CUBEMAP_ARRAY;
        internal fun toNative(): FilaTextureSamplerType {
            return when (this) {
                SAMPLER_2D -> FILA_TEXTURE_SAMPLER_2D
                SAMPLER_2D_ARRAY -> FILA_TEXTURE_SAMPLER_2D_ARRAY
                SAMPLER_CUBEMAP -> FILA_TEXTURE_SAMPLER_CUBEMAP
                SAMPLER_EXTERNAL -> FILA_TEXTURE_SAMPLER_EXTERNAL
                SAMPLER_3D -> FILA_TEXTURE_SAMPLER_3D
                SAMPLER_CUBEMAP_ARRAY -> FILA_TEXTURE_SAMPLER_CUBEMAP_ARRAY
            }
        }
        companion object {
            internal fun fromNative(native: FilaTextureSamplerType): Sampler {
                return when (native) {
                    FILA_TEXTURE_SAMPLER_2D -> SAMPLER_2D
                    FILA_TEXTURE_SAMPLER_2D_ARRAY -> SAMPLER_2D_ARRAY
                    FILA_TEXTURE_SAMPLER_CUBEMAP -> SAMPLER_CUBEMAP
                    FILA_TEXTURE_SAMPLER_EXTERNAL -> SAMPLER_EXTERNAL
                    FILA_TEXTURE_SAMPLER_3D -> SAMPLER_3D
                    FILA_TEXTURE_SAMPLER_CUBEMAP_ARRAY -> SAMPLER_CUBEMAP_ARRAY
                    else -> SAMPLER_2D
                }
            }
        }
    }

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
        RGB_BPTC_SIGNED_FLOAT, RGB_BPTC_UNSIGNED_FLOAT, RGBA_BPTC_UNORM, SRGB_ALPHA_BPTC_UNORM;

        internal fun toNative(): FilaTextureInternalFormat {
            return when (this) {
                R16F -> FILA_TEXTURE_INTERNAL_FORMAT_R16F
                R16UI -> FILA_TEXTURE_INTERNAL_FORMAT_R16UI
                R16I -> FILA_TEXTURE_INTERNAL_FORMAT_R16I
                RG8 -> FILA_TEXTURE_INTERNAL_FORMAT_RG8
                RG8_SNORM -> FILA_TEXTURE_INTERNAL_FORMAT_RG8_SNORM
                RG8UI -> FILA_TEXTURE_INTERNAL_FORMAT_RG8UI
                RG8I -> FILA_TEXTURE_INTERNAL_FORMAT_RG8I
                RGB565 -> FILA_TEXTURE_INTERNAL_FORMAT_RGB565
                RGB9_E5 -> FILA_TEXTURE_INTERNAL_FORMAT_RGB9_E5
                RGB5_A1 -> FILA_TEXTURE_INTERNAL_FORMAT_RGB5_A1
                RGBA4 -> FILA_TEXTURE_INTERNAL_FORMAT_RGBA4
                DEPTH16 -> FILA_TEXTURE_INTERNAL_FORMAT_DEPTH16
                RGB8 -> FILA_TEXTURE_INTERNAL_FORMAT_RGB8
                SRGB8 -> FILA_TEXTURE_INTERNAL_FORMAT_SRGB8
                RGB8_SNORM -> FILA_TEXTURE_INTERNAL_FORMAT_RGB8_SNORM
                RGB8UI -> FILA_TEXTURE_INTERNAL_FORMAT_RGB8UI
                RGB8I -> FILA_TEXTURE_INTERNAL_FORMAT_RGB8I
                DEPTH24 -> FILA_TEXTURE_INTERNAL_FORMAT_DEPTH24
                R32F -> FILA_TEXTURE_INTERNAL_FORMAT_R32F
                R32UI -> FILA_TEXTURE_INTERNAL_FORMAT_R32UI
                R32I -> FILA_TEXTURE_INTERNAL_FORMAT_R32I
                RG16F -> FILA_TEXTURE_INTERNAL_FORMAT_RG16F
                RG16UI -> FILA_TEXTURE_INTERNAL_FORMAT_RG16UI
                RG16I -> FILA_TEXTURE_INTERNAL_FORMAT_RG16I
                R11F_G11F_B10F -> FILA_TEXTURE_INTERNAL_FORMAT_R11F_G11F_B10F
                RGBA8 -> FILA_TEXTURE_INTERNAL_FORMAT_RGBA8
                SRGB8_A8 -> FILA_TEXTURE_INTERNAL_FORMAT_SRGB8_A8
                RGBA8_SNORM -> FILA_TEXTURE_INTERNAL_FORMAT_RGBA8_SNORM
                UNUSED -> FILA_TEXTURE_INTERNAL_FORMAT_RGBA8
                RGB10_A2 -> FILA_TEXTURE_INTERNAL_FORMAT_RGB10_A2
                RGBA8UI -> FILA_TEXTURE_INTERNAL_FORMAT_RGBA8UI
                RGBA8I -> FILA_TEXTURE_INTERNAL_FORMAT_RGBA8I
                DEPTH32F -> FILA_TEXTURE_INTERNAL_FORMAT_DEPTH32F
                DEPTH24_STENCIL8 -> FILA_TEXTURE_INTERNAL_FORMAT_DEPTH24_STENCIL8
                DEPTH32F_STENCIL8 -> FILA_TEXTURE_INTERNAL_FORMAT_DEPTH32F_STENCIL8
                RGB16F -> FILA_TEXTURE_INTERNAL_FORMAT_RGB16F
                RGB16UI -> FILA_TEXTURE_INTERNAL_FORMAT_RGB16UI
                RGB16I -> FILA_TEXTURE_INTERNAL_FORMAT_RGB16I
                RG32F -> FILA_TEXTURE_INTERNAL_FORMAT_RG32F
                RG32UI -> FILA_TEXTURE_INTERNAL_FORMAT_RG32UI
                RG32I -> FILA_TEXTURE_INTERNAL_FORMAT_RG32I
                RGBA16F -> FILA_TEXTURE_INTERNAL_FORMAT_RGBA16F
                RGBA16UI -> FILA_TEXTURE_INTERNAL_FORMAT_RGBA16UI
                RGBA16I -> FILA_TEXTURE_INTERNAL_FORMAT_RGBA16I
                RGB32F -> FILA_TEXTURE_INTERNAL_FORMAT_RGB32F
                RGB32UI -> FILA_TEXTURE_INTERNAL_FORMAT_RGB32UI
                RGB32I -> FILA_TEXTURE_INTERNAL_FORMAT_RGB32I
                RGBA32F -> FILA_TEXTURE_INTERNAL_FORMAT_RGBA32F
                RGBA32UI -> FILA_TEXTURE_INTERNAL_FORMAT_RGBA32UI
                RGBA32I -> FILA_TEXTURE_INTERNAL_FORMAT_RGBA32I
                else -> FILA_TEXTURE_INTERNAL_FORMAT_RGBA8
            }
        }

        companion object {
            internal fun fromNative(native: FilaTextureInternalFormat): InternalFormat {
                return InternalFormat.entries.find { it.toNative() == native } ?: RGBA8
            }
        }
    }

    actual enum class CubemapFace {
        POSITIVE_X, NEGATIVE_X, POSITIVE_Y, NEGATIVE_Y, POSITIVE_Z, NEGATIVE_Z;
        internal fun toNative(): UInt = this.ordinal.toUInt()
    }

    actual enum class Format {
        R, R_INTEGER, RG, RG_INTEGER, RGB, RGB_INTEGER, RGBA, RGBA_INTEGER, UNUSED, DEPTH_COMPONENT, DEPTH_STENCIL, STENCIL_INDEX, ALPHA;
        internal fun toNative(): FilaPixelDataFormat {
            return when (this) {
                R -> FILA_PIXEL_DATA_FORMAT_R
                R_INTEGER -> FILA_PIXEL_DATA_FORMAT_R_INTEGER
                RG -> FILA_PIXEL_DATA_FORMAT_RG
                RG_INTEGER -> FILA_PIXEL_DATA_FORMAT_RG_INTEGER
                RGB -> FILA_PIXEL_DATA_FORMAT_RGB
                RGB_INTEGER -> FILA_PIXEL_DATA_FORMAT_RGB_INTEGER
                RGBA -> FILA_PIXEL_DATA_FORMAT_RGBA
                RGBA_INTEGER -> FILA_PIXEL_DATA_FORMAT_RGBA_INTEGER
                UNUSED -> FILA_PIXEL_DATA_FORMAT_UNUSED
                DEPTH_COMPONENT -> FILA_PIXEL_DATA_FORMAT_DEPTH_COMPONENT
                DEPTH_STENCIL -> FILA_PIXEL_DATA_FORMAT_DEPTH_STENCIL
                STENCIL_INDEX -> FILA_PIXEL_DATA_FORMAT_STENCIL_INDEX
                ALPHA -> FILA_PIXEL_DATA_FORMAT_ALPHA
            }
        }
    }

    actual enum class Type {
        UBYTE, BYTE, USHORT, SHORT, UINT, INT, HALF, FLOAT, COMPRESSED, UINT_10F_11F_11F_REV, USHORT_565;
        internal fun toNative(): FilaPixelDataType {
            return when (this) {
                UBYTE -> FILA_PIXEL_DATA_TYPE_UBYTE
                BYTE -> FILA_PIXEL_DATA_TYPE_BYTE
                USHORT -> FILA_PIXEL_DATA_TYPE_USHORT
                SHORT -> FILA_PIXEL_DATA_TYPE_SHORT
                UINT -> FILA_PIXEL_DATA_TYPE_UINT
                INT -> FILA_PIXEL_DATA_TYPE_INT
                HALF -> FILA_PIXEL_DATA_TYPE_HALF
                FLOAT -> FILA_PIXEL_DATA_TYPE_FLOAT
                COMPRESSED -> FILA_PIXEL_DATA_TYPE_COMPRESSED
                UINT_10F_11F_11F_REV -> FILA_PIXEL_DATA_TYPE_UINT_10F_11F_11F_REV
                USHORT_565 -> FILA_PIXEL_DATA_TYPE_USHORT_565
            }
        }
    }

    actual enum class Swizzle {
        SUBSTITUTE_ZERO, SUBSTITUTE_ONE, CHANNEL_0, CHANNEL_1, CHANNEL_2, CHANNEL_3;
        internal fun toNative(): FilaTextureSwizzle {
            return when (this) {
                SUBSTITUTE_ZERO -> FILA_TEXTURE_SWIZZLE_SUBSTITUTE_ZERO
                SUBSTITUTE_ONE -> FILA_TEXTURE_SWIZZLE_SUBSTITUTE_ONE
                CHANNEL_0 -> FILA_TEXTURE_SWIZZLE_CHANNEL_0
                CHANNEL_1 -> FILA_TEXTURE_SWIZZLE_CHANNEL_1
                CHANNEL_2 -> FILA_TEXTURE_SWIZZLE_CHANNEL_2
                CHANNEL_3 -> FILA_TEXTURE_SWIZZLE_CHANNEL_3
            }
        }
    }

    actual class Usage {
        actual companion object {
            actual val COLOR_ATTACHMENT: Int = FILA_TEXTURE_USAGE_COLOR_ATTACHMENT.toInt()
            actual val DEPTH_ATTACHMENT: Int = FILA_TEXTURE_USAGE_DEPTH_ATTACHMENT.toInt()
            actual val STENCIL_ATTACHMENT: Int = FILA_TEXTURE_USAGE_STENCIL_ATTACHMENT.toInt()
            actual val UPLOADABLE: Int = FILA_TEXTURE_USAGE_UPLOADABLE.toInt()
            actual val SAMPLEABLE: Int = FILA_TEXTURE_USAGE_SAMPLEABLE.toInt()
            actual val SUBPASS_INPUT: Int = FILA_TEXTURE_USAGE_SUBPASS_INPUT.toInt()
            actual val BLIT_SRC: Int = FILA_TEXTURE_USAGE_BLIT_SRC.toInt()
            actual val BLIT_DST: Int = FILA_TEXTURE_USAGE_BLIT_DST.toInt()
            actual val PROTECTED: Int = FILA_TEXTURE_USAGE_PROTECTED.toInt()
            actual val GEN_MIPMAPPABLE: Int = FILA_TEXTURE_USAGE_GEN_MIPMAPPABLE.toInt()
            actual val DEFAULT: Int = FILA_TEXTURE_USAGE_DEFAULT.toInt()
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
        actual val callback: (() -> Unit)?
    )

    actual class Builder actual constructor() {
        private val nativeBuilder = FilaTextureBuilder_create()

        actual fun width(width: Int): Builder = apply { FilaTextureBuilder_width(nativeBuilder, width.toUInt()) }
        actual fun height(height: Int): Builder = apply { FilaTextureBuilder_height(nativeBuilder, height.toUInt()) }
        actual fun depth(depth: Int): Builder = apply { FilaTextureBuilder_depth(nativeBuilder, depth.toUInt()) }
        actual fun levels(levels: Int): Builder = apply { FilaTextureBuilder_levels(nativeBuilder, levels.toUByte()) }
        actual fun samples(samples: Int): Builder = apply { FilaTextureBuilder_samples(nativeBuilder, samples.toUByte()) }
        actual fun sampler(target: Sampler): Builder = apply { FilaTextureBuilder_sampler(nativeBuilder, target.toNative()) }
        actual fun format(format: InternalFormat): Builder = apply { FilaTextureBuilder_format(nativeBuilder, format.toNative()) }
        actual fun usage(usage: Int): Builder = apply { FilaTextureBuilder_usage(nativeBuilder, usage.toUInt()) }
        actual fun swizzle(r: Swizzle, g: Swizzle, b: Swizzle, a: Swizzle): Builder = apply {
            FilaTextureBuilder_swizzle(nativeBuilder, r.toNative(), g.toNative(), b.toNative(), a.toNative())
        }
        actual fun importTexture(id: Long): Builder = apply { FilaTextureBuilder_importTexture(nativeBuilder, id) }
        actual fun external(): Builder = apply { FilaTextureBuilder_external(nativeBuilder) }
        actual fun build(engine: Engine): Texture {
            val handle = FilaTextureBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaTextureBuilder_destroy(nativeBuilder)
            return Texture(handle)
        }
    }

    actual fun getWidth(level: Int): Int = FilaTexture_getWidth(nativeHandle, level.toULong()).toInt()
    actual fun getHeight(level: Int): Int = FilaTexture_getHeight(nativeHandle, level.toULong()).toInt()
    actual fun getDepth(level: Int): Int = FilaTexture_getDepth(nativeHandle, level.toULong()).toInt()
    actual fun getLevels(): Int = FilaTexture_getLevels(nativeHandle).toInt()
    actual fun getTarget(): Sampler = Sampler.fromNative(FilaTexture_getTarget(nativeHandle))
    actual fun getFormat(): InternalFormat = InternalFormat.fromNative(FilaTexture_getFormat(nativeHandle))

    private class PixelBufferPinWrapper(val pinned: Pinned<*>, val callback: (() -> Unit)?)

    actual fun setImage(engine: Engine, level: Int, descriptor: PixelBufferDescriptor) {
        setImage(engine, level, 0, 0, 0, getWidth(level), getHeight(level), getDepth(level), descriptor)
    }

    actual fun setImage(engine: Engine, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int, descriptor: PixelBufferDescriptor) {
        setImage(engine, level, xoffset, yoffset, 0, width, height, 1, descriptor)
    }

    actual fun setImage(engine: Engine, level: Int, xoffset: Int, yoffset: Int, zoffset: Int, width: Int, height: Int, depth: Int, descriptor: PixelBufferDescriptor) {
        val pinned = descriptor.storage.pin()
        val ptr = pinned.addressOf(0).reinterpret<ByteVar>()

        val wrapper = PixelBufferPinWrapper(pinned, descriptor.callback)
        val stableRef = StableRef.create(wrapper)
        val callbackWrapper = staticCFunction { _: COpaquePointer?, _: ULong, user: COpaquePointer? ->
            val ref = user!!.asStableRef<PixelBufferPinWrapper>()
            val wrap = ref.get()
            wrap.callback?.invoke()
            wrap.pinned.unpin()
            ref.dispose()
        }
        FilaTexture_setImage(
            nativeHandle, engine.nativeHandle, level.toULong(),
            xoffset.toUInt(), yoffset.toUInt(), zoffset.toUInt(),
            width.toUInt(), height.toUInt(), depth.toUInt(),
            ptr, descriptor.sizeInBytes.toULong(),
            descriptor.format.toNative(), descriptor.type.toNative(),
            descriptor.alignment.toUByte(), descriptor.left.toUInt(), descriptor.top.toUInt(), descriptor.stride.toUInt(),
            null, callbackWrapper, stableRef.asCPointer()
        )
    }

    actual fun generateMipmaps(engine: Engine) {
        FilaTexture_generateMipmaps(nativeHandle, engine.nativeHandle)
    }

    actual fun setExternalStream(engine: Engine, stream: Stream) {
        FilaTexture_setExternalStream(nativeHandle, engine.nativeHandle, stream.nativeHandle)
    }

    actual companion object {
        actual fun isTextureFormatSupported(engine: Engine, format: InternalFormat): Boolean =
            FilaTexture_isTextureFormatSupported(engine.nativeHandle, format.toNative())

        actual fun isTextureFormatMipmappable(engine: Engine, format: InternalFormat): Boolean =
            FilaTexture_isTextureFormatMipmappable(engine.nativeHandle, format.toNative())

        actual fun isTextureSwizzleSupported(engine: Engine): Boolean =
            FilaTexture_isTextureSwizzleSupported(engine.nativeHandle)

        actual fun validatePixelFormatAndType(internalFormat: InternalFormat, pixelDataFormat: Format, pixelDataType: Type): Boolean =
            FilaTexture_validatePixelFormatAndType(internalFormat.toNative(), pixelDataFormat.toNative(), pixelDataType.toNative())

        actual fun getMaxTextureSize(engine: Engine, type: Sampler): Int =
            FilaTexture_getMaxTextureSize(engine.nativeHandle, type.toNative()).toInt()

        actual fun getMaxArrayTextureLayers(engine: Engine): Int =
            FilaTexture_getMaxArrayTextureLayers(engine.nativeHandle).toInt()

        actual fun computeDataSize(format: Format, type: Type, stride: Int, height: Int, alignment: Int): Int =
            FilaTexture_computeDataSize(format.toNative(), type.toNative(), stride.toULong(), height.toULong(), alignment.toULong()).toInt()
    }
}
