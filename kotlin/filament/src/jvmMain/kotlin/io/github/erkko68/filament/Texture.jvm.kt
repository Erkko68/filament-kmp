package io.github.erkko68.filament

import io.github.erkko68.filament.jni.Texture as JniTexture
import java.nio.ByteBuffer
import java.nio.ByteOrder

actual class Texture(val nativeTexture: JniTexture) {
    actual class Builder actual constructor() {
        private val jni = JniTexture.Builder()

        actual fun width(width: Int): Builder { jni.width(width); return this }
        actual fun height(height: Int): Builder { jni.height(height); return this }
        actual fun depth(depth: Int): Builder { jni.depth(depth); return this }
        actual fun levels(levels: Int): Builder { jni.levels(levels); return this }
        actual fun samples(samples: Int): Builder { jni.samples(samples); return this }
        actual fun sampler(target: Sampler): Builder { jni.sampler(target.toJni()); return this }
        actual fun format(format: InternalFormat): Builder { jni.format(format.toJni()); return this }
        actual fun usage(usage: Int): Builder { jni.usage(usage); return this }
        actual fun swizzle(r: Swizzle, g: Swizzle, b: Swizzle, a: Swizzle): Builder {
            jni.swizzle(r.toJni(), g.toJni(), b.toJni(), a.toJni())
            return this
        }
        actual fun importTexture(id: Long): Builder { jni.importTexture(id); return this }
        actual fun external(): Builder { jni.external(); return this }
        actual fun build(engine: Engine): Texture = Texture(jni.build(engine.nativeEngine))
    }

    actual enum class Sampler {
        SAMPLER_2D, SAMPLER_2D_ARRAY, SAMPLER_CUBEMAP, SAMPLER_EXTERNAL, SAMPLER_3D, SAMPLER_CUBEMAP_ARRAY;
        internal fun toJni() = JniTexture.Sampler.valueOf(name)
    }

    actual enum class InternalFormat {
        R8, R8_SNORM, R8UI, R8I, STENCIL8, R16F, R16UI, R16I, RG8, RG8_SNORM, RG8UI, RG8I, RGB565, RGB9_E5, RGB5_A1, RGBA4, DEPTH16, RGB8, SRGB8, RGB8_SNORM, RGB8UI, RGB8I, DEPTH24, R32F, R32UI, R32I, RG16F, RG16UI, RG16I, R11F_G11F_B10F, RGBA8, SRGB8_A8, RGBA8_SNORM, UNUSED, RGB10_A2, RGBA8UI, RGBA8I, DEPTH32F, DEPTH24_STENCIL8, DEPTH32F_STENCIL8, RGB16F, RGB16UI, RGB16I, RG32F, RG32UI, RG32I, RGBA16F, RGBA16UI, RGBA16I, RGB32F, RGB32UI, RGB32I, RGBA32F, RGBA32UI, RGBA32I, EAC_R11, EAC_R11_SIGNED, EAC_RG11, EAC_RG11_SIGNED, ETC2_RGB8, ETC2_SRGB8, ETC2_RGB8_A1, ETC2_SRGB8_A1, ETC2_EAC_RGBA8, ETC2_EAC_SRGBA8, DXT1_RGB, DXT1_RGBA, DXT3_RGBA, DXT5_RGBA, DXT1_SRGB, DXT1_SRGBA, DXT3_SRGBA, DXT5_SRGBA, RGBA_ASTC_4x4, RGBA_ASTC_5x4, RGBA_ASTC_5x5, RGBA_ASTC_6x5, RGBA_ASTC_6x6, RGBA_ASTC_8x5, RGBA_ASTC_8x6, RGBA_ASTC_8x8, RGBA_ASTC_10x5, RGBA_ASTC_10x6, RGBA_ASTC_10x8, RGBA_ASTC_10x10, RGBA_ASTC_12x10, RGBA_ASTC_12x12, SRGB8_ALPHA8_ASTC_4x4, SRGB8_ALPHA8_ASTC_5x4, SRGB8_ALPHA8_ASTC_5x5, SRGB8_ALPHA8_ASTC_6x5, SRGB8_ALPHA8_ASTC_6x6, SRGB8_ALPHA8_ASTC_8x5, SRGB8_ALPHA8_ASTC_8x6, SRGB8_ALPHA8_ASTC_8x8, SRGB8_ALPHA8_ASTC_10x5, SRGB8_ALPHA8_ASTC_10x6, SRGB8_ALPHA8_ASTC_10x8, SRGB8_ALPHA8_ASTC_10x10, SRGB8_ALPHA8_ASTC_12x10, SRGB8_ALPHA8_ASTC_12x12, RED_RGTC1, SIGNED_RED_RGTC1, RED_GREEN_RGTC2, SIGNED_RED_GREEN_RGTC2, RGB_BPTC_SIGNED_FLOAT, RGB_BPTC_UNSIGNED_FLOAT, RGBA_BPTC_UNORM, SRGB_ALPHA_BPTC_UNORM;
        
        internal fun toJni(): JniTexture.InternalFormat {
            return try {
                JniTexture.InternalFormat.valueOf(this.name)
            } catch (e: Exception) {
                // Handle naming differences between KMP and JNI (e.g. DXT vs DXP)
                val jniName = this.name
                    .replace("DXT", "DXP")
                    .replace("SRGBA8", "SRGB8_A8")
                JniTexture.InternalFormat.valueOf(jniName)
            }
        }
    }

    actual enum class CubemapFace {
        POSITIVE_X, NEGATIVE_X, POSITIVE_Y, NEGATIVE_Y, POSITIVE_Z, NEGATIVE_Z;
        internal fun toJni() = JniTexture.CubemapFace.values()[ordinal]
    }

    actual enum class Format {
        R, R_INTEGER, RG, RG_INTEGER, RGB, RGB_INTEGER, RGBA, RGBA_INTEGER, UNUSED, DEPTH_COMPONENT, DEPTH_STENCIL, STENCIL_INDEX, ALPHA;
        internal fun toJni() = JniTexture.Format.values()[ordinal]
    }

    actual enum class Type {
        UBYTE, BYTE, USHORT, SHORT, UINT, INT, HALF, FLOAT, COMPRESSED, UINT_10F_11F_11F_REV, USHORT_565;
        internal fun toJni() = JniTexture.Type.values()[ordinal]
    }

    actual enum class Swizzle {
        SUBSTITUTE_ZERO, SUBSTITUTE_ONE, CHANNEL_0, CHANNEL_1, CHANNEL_2, CHANNEL_3;
        internal fun toJni() = JniTexture.Swizzle.values()[ordinal]
    }

    actual class Usage {
        actual companion object {
            actual val COLOR_ATTACHMENT: Int = 0x1
            actual val DEPTH_ATTACHMENT: Int = 0x2
            actual val STENCIL_ATTACHMENT: Int = 0x4
            actual val UPLOADABLE: Int = 0x8
            actual val SAMPLEABLE: Int = 0x10
            actual val SUBPASS_INPUT: Int = 0x20
            actual val BLIT_SRC: Int = 0x40
            actual val BLIT_DST: Int = 0x80
            actual val PROTECTED: Int = 0x100
            actual val GEN_MIPMAPPABLE: Int = 0x200
            actual val DEFAULT: Int = UPLOADABLE or SAMPLEABLE
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
    ) {
        internal val jni: JniTexture.PixelBufferDescriptor
        init {
            val buffer = ByteBuffer.allocateDirect(sizeInBytes).order(ByteOrder.nativeOrder())
            buffer.put(storage, 0, minOf(storage.size, sizeInBytes))
            buffer.flip()
            jni = JniTexture.PixelBufferDescriptor(buffer, format.toJni(), type.toJni(), alignment, left, top, stride, null, callback?.let { Runnable { it() } })
        }
    }

    actual fun getWidth(level: Int): Int = nativeTexture.getWidth(level)
    actual fun getHeight(level: Int): Int = nativeTexture.getHeight(level)
    actual fun getDepth(level: Int): Int = nativeTexture.getDepth(level)
    actual fun getLevels(): Int = nativeTexture.levels
    actual fun getTarget(): Sampler = Sampler.valueOf(nativeTexture.target.name)
    actual fun getFormat(): InternalFormat = InternalFormat.valueOf(nativeTexture.format.name)

    actual fun setImage(engine: Engine, level: Int, descriptor: PixelBufferDescriptor) {
        nativeTexture.setImage(engine.nativeEngine, level, descriptor.jni)
    }

    actual fun setImage(engine: Engine, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int, descriptor: PixelBufferDescriptor) {
        nativeTexture.setImage(engine.nativeEngine, level, xoffset, yoffset, width, height, descriptor.jni)
    }

    actual fun setImage(engine: Engine, level: Int, xoffset: Int, yoffset: Int, zoffset: Int, width: Int, height: Int, depth: Int, descriptor: PixelBufferDescriptor) {
        nativeTexture.setImage(engine.nativeEngine, level, xoffset, yoffset, zoffset, width, height, depth, descriptor.jni)
    }

    actual fun setExternalStream(engine: Engine, stream: Stream) {
        nativeTexture.setExternalStream(engine.nativeEngine, stream.nativeStream)
    }

    actual fun generateMipmaps(engine: Engine) {
        nativeTexture.generateMipmaps(engine.nativeEngine)
    }

    actual companion object {
        actual fun isTextureFormatSupported(engine: Engine, format: InternalFormat): Boolean = JniTexture.isTextureFormatSupported(engine.nativeEngine, format.toJni())
        actual fun isTextureFormatMipmappable(engine: Engine, format: InternalFormat): Boolean = JniTexture.isTextureFormatMipmappable(engine.nativeEngine, format.toJni())
        actual fun isTextureSwizzleSupported(engine: Engine): Boolean = JniTexture.isTextureSwizzleSupported(engine.nativeEngine)
        actual fun validatePixelFormatAndType(internalFormat: InternalFormat, pixelDataFormat: Format, pixelDataType: Type): Boolean =
            JniTexture.validatePixelFormatAndType(internalFormat.toJni(), pixelDataFormat.toJni(), pixelDataType.toJni())
        actual fun getMaxTextureSize(engine: Engine, type: Sampler): Int = JniTexture.getMaxTextureSize(engine.nativeEngine, type.toJni())
        actual fun getMaxArrayTextureLayers(engine: Engine): Int = JniTexture.getMaxArrayTextureLayers(engine.nativeEngine)
        actual fun computeDataSize(format: Format, type: Type, stride: Int, height: Int, alignment: Int): Int =
            JniTexture.PixelBufferDescriptor.computeDataSize(format.toJni(), type.toJni(), stride, height, alignment)
    }
}
