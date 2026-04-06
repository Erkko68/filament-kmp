package dev.filament.kmp

import kotlinx.cinterop.*
import filament.*

actual class Texture internal constructor(
    internal val nativeObject: CPointer<FilaTexture>
) {
    actual enum class Sampler {
        SAMPLER_2D, SAMPLER_2D_ARRAY, SAMPLER_CUBEMAP, SAMPLER_EXTERNAL, SAMPLER_3D;

        internal fun toNative(): FilaTextureSampler = when (this) {
            SAMPLER_2D -> FILA_TEXTURE_SAMPLER_2D
            SAMPLER_2D_ARRAY -> FILA_TEXTURE_SAMPLER_2D_ARRAY
            SAMPLER_CUBEMAP -> FILA_TEXTURE_SAMPLER_CUBEMAP
            SAMPLER_EXTERNAL -> FILA_TEXTURE_SAMPLER_EXTERNAL
            SAMPLER_3D -> FILA_TEXTURE_SAMPLER_3D
        }

        internal companion object {
            fun fromNative(v: FilaTextureSampler): Sampler = when (v) {
                FILA_TEXTURE_SAMPLER_2D -> SAMPLER_2D
                FILA_TEXTURE_SAMPLER_2D_ARRAY -> SAMPLER_2D_ARRAY
                FILA_TEXTURE_SAMPLER_CUBEMAP -> SAMPLER_CUBEMAP
                FILA_TEXTURE_SAMPLER_EXTERNAL -> SAMPLER_EXTERNAL
                FILA_TEXTURE_SAMPLER_3D -> SAMPLER_3D
                else -> SAMPLER_2D
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
        RGBA_ASTC_4x4, RGBA_ASTC_5x4, RGBA_ASTC_5x5, RGBA_ASTC_6x5, RGBA_ASTC_6x6, RGBA_ASTC_8x5, RGBA_ASTC_8x6, RGBA_ASTC_8x8, RGBA_ASTC_10x5, RGBA_ASTC_10x6, RGBA_ASTC_10x8, RGBA_ASTC_10x10, RGBA_ASTC_12x10, RGBA_ASTC_12x12,
        SRGB8_ALPHA8_ASTC_4x4, SRGB8_ALPHA8_ASTC_5x4, SRGB8_ALPHA8_ASTC_5x5, SRGB8_ALPHA8_ASTC_6x5, SRGB8_ALPHA8_ASTC_6x6, SRGB8_ALPHA8_ASTC_8x5, SRGB8_ALPHA8_ASTC_8x6, SRGB8_ALPHA8_ASTC_8x8, SRGB8_ALPHA8_ASTC_10x5, SRGB8_ALPHA8_ASTC_10x6, SRGB8_ALPHA8_ASTC_10x8, SRGB8_ALPHA8_ASTC_10x10, SRGB8_ALPHA8_ASTC_12x10, SRGB8_ALPHA8_ASTC_12x12,
        RED_RGTC1, SIGNED_RED_RGTC1, RED_GREEN_RGTC2, SIGNED_RED_GREEN_RGTC2,
        RGB_BPTC_SIGNED_FLOAT, RGB_BPTC_UNSIGNED_FLOAT, RGBA_BPTC_UNORM, SRGB_ALPHA_BPTC_UNORM;

        internal fun toNative(): FilaTextureInternalFormat = when (this) {
            R8 -> FILA_TEXTURE_INTERNAL_FORMAT_R8
            R8_SNORM -> FILA_TEXTURE_INTERNAL_FORMAT_R8_SNORM
            R8UI -> FILA_TEXTURE_INTERNAL_FORMAT_R8UI
            R8I -> FILA_TEXTURE_INTERNAL_FORMAT_R8I
            STENCIL8 -> FILA_TEXTURE_INTERNAL_FORMAT_STENCIL8
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
            else -> FILA_TEXTURE_INTERNAL_FORMAT_RGBA8 // Fallback
        }

        internal companion object {
            fun fromNative(v: FilaTextureInternalFormat): InternalFormat = when (v) {
                FILA_TEXTURE_INTERNAL_FORMAT_R8 -> R8
                FILA_TEXTURE_INTERNAL_FORMAT_RGBA8 -> RGBA8
                FILA_TEXTURE_INTERNAL_FORMAT_SRGB8_A8 -> SRGB8_A8
                // ... we might need to complete this if we actually use the getter in apps
                else -> RGBA8
            }
        }
    }

    actual enum class CompressedFormat {
        EAC_R11, EAC_R11_SIGNED, EAC_RG11, EAC_RG11_SIGNED,
        // ... handled via mapping to backend types if needed
        ETC2_RGB8, ETC2_SRGB8, ETC2_RGB8_A1, ETC2_SRGB8_A1, ETC2_EAC_RGBA8, ETC2_EAC_SRGBA8,
        DXT1_RGB, DXT1_RGBA, DXT3_RGBA, DXT5_RGBA, DXT1_SRGB, DXT1_SRGBA, DXT3_SRGBA, DXT5_SRGBA,
        RGBA_ASTC_4x4, RGBA_ASTC_5x4, RGBA_ASTC_5x5, RGBA_ASTC_6x5, RGBA_ASTC_6x6, RGBA_ASTC_8x5, RGBA_ASTC_8x6, RGBA_ASTC_8x8, RGBA_ASTC_10x5, RGBA_ASTC_10x6, RGBA_ASTC_10x8, RGBA_ASTC_10x10, RGBA_ASTC_12x10, RGBA_ASTC_12x12,
        SRGB8_ALPHA8_ASTC_4x4, SRGB8_ALPHA8_ASTC_5x4, SRGB8_ALPHA8_ASTC_5x5, SRGB8_ALPHA8_ASTC_6x5, SRGB8_ALPHA8_ASTC_6x6, SRGB8_ALPHA8_ASTC_8x5, SRGB8_ALPHA8_ASTC_8x6, SRGB8_ALPHA8_ASTC_8x8, SRGB8_ALPHA8_ASTC_10x5, SRGB8_ALPHA8_ASTC_10x6, SRGB8_ALPHA8_ASTC_10x8, SRGB8_ALPHA8_ASTC_10x10, SRGB8_ALPHA8_ASTC_12x10, SRGB8_ALPHA8_ASTC_12x12,
        RED_RGTC1, SIGNED_RED_RGTC1, RED_GREEN_RGTC2, SIGNED_RED_GREEN_RGTC2,
        RGB_BPTC_SIGNED_FLOAT, RGB_BPTC_UNSIGNED_FLOAT, RGBA_BPTC_UNORM, SRGB_ALPHA_BPTC_UNORM;
    }

    actual enum class CubemapFace {
        POSITIVE_X, NEGATIVE_X, POSITIVE_Y, NEGATIVE_Y, POSITIVE_Z, NEGATIVE_Z;
    }

    actual enum class Format {
        R, R_INTEGER, RG, RG_INTEGER, RGB, RGB_INTEGER, RGBA, RGBA_INTEGER, UNUSED, DEPTH_COMPONENT, DEPTH_STENCIL, STENCIL_INDEX, ALPHA;

        internal fun toNative(): FilaPixelDataFormat = when (this) {
            R -> FILA_PIXEL_DATA_FORMAT_R
            R_INTEGER -> FILA_PIXEL_DATA_FORMAT_R_INTEGER
            RG -> FILA_PIXEL_DATA_FORMAT_RG
            RG_INTEGER -> FILA_PIXEL_DATA_FORMAT_RG_INTEGER
            RGB -> FILA_PIXEL_DATA_FORMAT_RGB
            RGB_INTEGER -> FILA_PIXEL_DATA_FORMAT_RGB_INTEGER
            RGBA -> FILA_PIXEL_DATA_FORMAT_RGBA
            RGBA_INTEGER -> FILA_PIXEL_DATA_FORMAT_RGBA_INTEGER
            DEPTH_COMPONENT -> FILA_PIXEL_DATA_FORMAT_DEPTH_COMPONENT
            DEPTH_STENCIL -> FILA_PIXEL_DATA_FORMAT_DEPTH_STENCIL
            ALPHA -> FILA_PIXEL_DATA_FORMAT_ALPHA
            else -> FILA_PIXEL_DATA_FORMAT_RGBA
        }
    }

    actual enum class Type {
        UBYTE, BYTE, USHORT, SHORT, UINT, INT, HALF, FLOAT, COMPRESSED, UINT_10F_11F_11F_REV, USHORT_565;

        internal fun toNative(): FilaPixelDataType = when (this) {
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

    actual enum class Swizzle {
        SUBSTITUTE_ZERO, SUBSTITUTE_ONE, CHANNEL_0, CHANNEL_1, CHANNEL_2, CHANNEL_3;

        internal fun toNative(): FilaTextureSwizzle = when (this) {
            SUBSTITUTE_ZERO -> FILA_TEXTURE_SWIZZLE_SUBSTITUTE_ZERO
            SUBSTITUTE_ONE -> FILA_TEXTURE_SWIZZLE_SUBSTITUTE_ONE
            CHANNEL_0 -> FILA_TEXTURE_SWIZZLE_CHANNEL_0
            CHANNEL_1 -> FILA_TEXTURE_SWIZZLE_CHANNEL_1
            CHANNEL_2 -> FILA_TEXTURE_SWIZZLE_CHANNEL_2
            CHANNEL_3 -> FILA_TEXTURE_SWIZZLE_CHANNEL_3
        }
    }

    actual class PixelBufferDescriptor {
        actual var storage: Buffer
        actual var type: Type
        actual var alignment: Int
        actual var left: Int
        actual var top: Int
        actual var stride: Int
        actual var format: Format
        actual var compressedSizeInBytes: Int
        actual var compressedFormat: CompressedFormat
        actual var handler: Any?
        actual var callback: Runnable?

        actual constructor(storage: Buffer, format: Format, type: Type, alignment: Int, left: Int, top: Int, stride: Int, handler: Any?, callback: Runnable?) {
            this.storage = storage
            this.format = format
            this.type = type
            this.alignment = alignment
            this.left = left
            this.top = top
            this.stride = stride
            this.handler = handler
            this.callback = callback
            this.compressedSizeInBytes = 0
            this.compressedFormat = CompressedFormat.DXT1_RGB
        }

        actual constructor(storage: Buffer, format: Format, type: Type) : this(storage, format, type, 1, 0, 0, 0, null, null)
        actual constructor(storage: Buffer, format: Format, type: Type, alignment: Int) : this(storage, format, type, alignment, 0, 0, 0, null, null)
        actual constructor(storage: Buffer, format: Format, type: Type, alignment: Int, left: Int, top: Int) : this(storage, format, type, alignment, left, top, 0, null, null)
        
        actual constructor(storage: Buffer, format: CompressedFormat, compressedSizeInBytes: Int) {
            this.storage = storage
            this.format = Format.RGBA
            this.type = Type.COMPRESSED
            this.alignment = 1
            this.left = 0
            this.top = 0
            this.stride = 0
            this.handler = null
            this.callback = null
            this.compressedSizeInBytes = compressedSizeInBytes
            this.compressedFormat = format
        }

        actual fun setCallback(handler: Any?, callback: Runnable?) {
            this.handler = handler
            this.callback = callback
        }

        actual companion object {
            actual fun computeDataSize(format: Format, type: Type, stride: Int, height: Int, alignment: Int): Int = 0 // Mock
        }
    }

    actual class PrefilterOptions actual constructor() {
        actual var sampleCount: Int = 8
        actual var mirror: Boolean = false
    }

    actual class Usage {
        actual companion object {
            actual const val COLOR_ATTACHMENT: Int = 0x1
            actual const val DEPTH_ATTACHMENT: Int = 0x2
            actual const val STENCIL_ATTACHMENT: Int = 0x4
            actual const val UPLOADABLE: Int = 0x8
            actual const val SAMPLEABLE: Int = 0x10
            actual const val SUBPASS_INPUT: Int = 0x20
            actual const val BLIT_SRC: Int = 0x40
            actual const val BLIT_DST: Int = 0x80
            actual const val PROTECTED: Int = 0x100
            actual const val GEN_MIPMAPPABLE: Int = 0x200
            actual val DEFAULT: Int = UPLOADABLE or SAMPLEABLE
        }
    }

    actual class Builder actual constructor() {
        private val nativeBuilder = FilaTextureBuilder_create()!!

        actual fun width(width: Int): Builder { FilaTextureBuilder_width(nativeBuilder, width.toUInt()); return this }
        actual fun height(height: Int): Builder { FilaTextureBuilder_height(nativeBuilder, height.toUInt()); return this }
        actual fun depth(depth: Int): Builder { FilaTextureBuilder_depth(nativeBuilder, depth.toUInt()); return this }
        actual fun levels(levels: Int): Builder { FilaTextureBuilder_levels(nativeBuilder, levels.toUByte()); return this }
        actual fun sampler(target: Sampler): Builder { FilaTextureBuilder_sampler(nativeBuilder, target.toNative()); return this }
        actual fun samples(samples: Int): Builder { FilaTextureBuilder_samples(nativeBuilder, samples.toUByte()); return this }
        actual fun format(format: InternalFormat): Builder { FilaTextureBuilder_format(nativeBuilder, format.toNative()); return this }
        actual fun usage(flags: Int): Builder { FilaTextureBuilder_usage(nativeBuilder, flags.toUInt()); return this }
        actual fun swizzle(r: Swizzle, g: Swizzle, b: Swizzle, a: Swizzle): Builder {
            FilaTextureBuilder_swizzle(nativeBuilder, r.toNative(), g.toNative(), b.toNative(), a.toNative())
            return this
        }
        actual fun importTexture(id: Long): Builder { FilaTextureBuilder_importTexture(nativeBuilder, id); return this }
        actual fun external(): Builder { FilaTextureBuilder_external(nativeBuilder); return this }
        actual fun build(engine: Engine): Texture {
            val nativeTexture = FilaTextureBuilder_build(nativeBuilder, engine.nativeObject)!!
            FilaTextureBuilder_destroy(nativeBuilder)
            return Texture(nativeTexture)
        }
    }

    actual companion object {
        actual const val BASE_LEVEL: Int = 0

        actual fun isTextureFormatSupported(engine: Engine, format: InternalFormat): Boolean = FilaTexture_isTextureFormatSupported(engine.nativeObject, format.toNative())
        actual fun isTextureFormatMipmappable(engine: Engine, format: InternalFormat): Boolean = FilaTexture_isTextureFormatMipmappable(engine.nativeObject, format.toNative())
        actual fun isTextureSwizzleSupported(engine: Engine): Boolean = FilaTexture_isTextureSwizzleSupported(engine.nativeObject)
        actual fun validatePixelFormatAndType(internalFormat: InternalFormat, pixelDataFormat: Format, pixelDataType: Type): Boolean = 
            FilaTexture_validatePixelFormatAndType(internalFormat.toNative(), pixelDataFormat.toNative(), pixelDataType.toNative())
        actual fun getMaxTextureSize(engine: Engine, type: Sampler): Int = FilaTexture_getMaxTextureSize(engine.nativeObject, type.toNative()).toInt()
        actual fun getMaxArrayTextureLayers(engine: Engine): Int = FilaTexture_getMaxArrayTextureLayers(engine.nativeObject).toInt()
    }

    actual fun getWidth(level: Int): Int = FilaTexture_getWidth(nativeObject, level.toULong()).toInt()
    actual fun getHeight(level: Int): Int = FilaTexture_getHeight(nativeObject, level.toULong()).toInt()
    actual fun getDepth(level: Int): Int = FilaTexture_getDepth(nativeObject, level.toULong()).toInt()
    actual fun getLevels(): Int = FilaTexture_getLevels(nativeObject).toInt()
    actual fun getTarget(): Sampler = Sampler.fromNative(FilaTexture_getTarget(nativeObject))
    actual fun getFormat(): InternalFormat = InternalFormat.fromNative(FilaTexture_getFormat(nativeObject))

    actual fun setImage(engine: Engine, level: Int, buffer: PixelBufferDescriptor) {
        setImage(engine, level, 0, 0, buffer.storage.limit(), buffer.storage.limit(), buffer)
    }

    actual fun setImage(engine: Engine, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: PixelBufferDescriptor) {
        setImage(engine, level, xoffset, yoffset, 0, width, height, 1, buffer)
    }

    actual fun setImage(engine: Engine, level: Int, xoffset: Int, yoffset: Int, zoffset: Int, width: Int, height: Int, depth: Int, buffer: PixelBufferDescriptor) {
        val storagePtr = buffer.storage.nativePointer() ?: return
        val sizeInBytes = if (buffer.type == Type.COMPRESSED) buffer.compressedSizeInBytes.toULong() else (buffer.storage.limit() * buffer.storage.elementSize()).toULong()
        FilaTexture_setImage(nativeObject, engine.nativeObject, level.toULong(), xoffset.toUInt(), yoffset.toUInt(), zoffset.toUInt(), width.toUInt(), height.toUInt(), depth.toUInt(), storagePtr, sizeInBytes, buffer.format.toNative(), buffer.type.toNative(), buffer.alignment.toUByte(), buffer.left.toUInt(), buffer.top.toUInt(), buffer.stride.toUInt(), null, null, null)
    }

    actual fun setImage(engine: Engine, level: Int, buffer: PixelBufferDescriptor, faceOffsetsInBytes: IntArray) {
        // ... handled via set_image_cubemap in wrapper if needed
    }

    actual fun setExternalImage(engine: Engine, eglImage: Long) { FilaTexture_setExternalImage(nativeObject, engine.nativeObject, eglImage.toCPointer()) }
    actual fun setExternalImage(engine: Engine, externalImageRef: Any) {}
    actual fun setExternalStream(engine: Engine, stream: Stream) { FilaTexture_setExternalStream(nativeObject, engine.nativeObject, stream.nativeObject) }

    actual fun generateMipmaps(engine: Engine) { FilaTexture_generateMipmaps(nativeObject, engine.nativeObject) }
    actual fun generatePrefilterMipmap(engine: Engine, buffer: PixelBufferDescriptor, faceOffsetsInBytes: IntArray, options: PrefilterOptions?) {}
}
