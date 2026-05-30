package io.github.erkko68.filament

import io.github.erkko68.filament.js.Texture as JSTexture
import io.github.erkko68.filament.js.Texture_InternalFormat as JSTextureInternalFormat
import io.github.erkko68.filament.js.`driver_PixelBufferDescriptor` as JSPixelBufferDescriptor
import io.github.erkko68.filament.js.PixelDataFormat
import io.github.erkko68.filament.js.PixelDataType
import io.github.erkko68.filament.js.Texture_Builder as JSTextureBuilder

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual class Texture(val jsTexture: JSTexture) {
    // Filament JS exposes dimensions only via `_getWidth(engine, level)` etc.
    // (see jsbindings.cpp), so when an engine is known we delegate; otherwise
    // we fall back to the dimensions captured when the Texture was built.
    internal var engine: Engine? = null
    private var _width = 0
    private var _height = 0
    private var _depth = 0
    private var _levels = 1
    private var _format = InternalFormat.RGBA8
    private var _target = Sampler.SAMPLER_2D

    internal constructor(jsTexture: JSTexture, engine: Engine, width: Int, height: Int, depth: Int, levels: Int, format: InternalFormat, target: Sampler) : this(jsTexture) {
        this.engine = engine
        _width = width
        _height = height
        _depth = depth
        _levels = levels
        _format = format
        _target = target
    }

    actual class Builder actual constructor() {
        private val jsBuilder: JSTextureBuilder = JSTexture.Builder()
        private var _width = 1
        private var _height = 1
        private var _depth = 1
        private var _levels = 1
        private var _format = InternalFormat.RGBA8
        private var _target = Sampler.SAMPLER_2D

        actual fun width(width: Int): Builder {
            _width = width
            jsBuilder.width(width)
            return this
        }

        actual fun height(height: Int): Builder {
            _height = height
            jsBuilder.height(height)
            return this
        }

        actual fun depth(depth: Int): Builder {
            _depth = depth
            jsBuilder.depth(depth)
            return this
        }

        actual fun levels(levels: Int): Builder {
            _levels = levels
            jsBuilder.levels(levels)
            return this
        }

        actual fun samples(samples: Int): Builder {
            // Not in JS?
            return this
        }

        actual fun sampler(target: Sampler): Builder {
            _target = target
            // Upstream binds only SAMPLER_2D / SAMPLER_CUBEMAP / SAMPLER_EXTERNAL.
            // Map common-API samplers to the closest JS-bound option so the
            // native side knows e.g. that a texture is a cubemap (needed by
            // Skybox/IndirectLight which assert isCubemap()).
            val jsSampler = when (target) {
                Sampler.SAMPLER_CUBEMAP, Sampler.SAMPLER_CUBEMAP_ARRAY ->
                    io.github.erkko68.filament.js.Texture_Sampler.SAMPLER_CUBEMAP
                Sampler.SAMPLER_EXTERNAL ->
                    io.github.erkko68.filament.js.Texture_Sampler.SAMPLER_EXTERNAL
                else -> io.github.erkko68.filament.js.Texture_Sampler.SAMPLER_2D
            }
            jsBuilder.sampler(jsSampler)
            return this
        }

        actual fun format(format: InternalFormat): Builder {
            _format = format
            jsBuilder.format(mapInternalFormat(format))
            return this
        }

        actual fun usage(usage: Int): Builder {
            jsBuilder.usage(usage)
            return this
        }

        actual fun swizzle(
            r: Swizzle,
            g: Swizzle,
            b: Swizzle,
            a: Swizzle
        ): Builder {
            return this
        }

        actual fun importTexture(id: Long): Builder {
            return this
        }

        actual fun external(): Builder {
            return this
        }

        actual fun build(engine: Engine): Texture {
            return Texture(jsBuilder.build(engine.jsEngine), engine, _width, _height, _depth, _levels, _format, _target)
        }
    }

    actual enum class Sampler { SAMPLER_2D, SAMPLER_2D_ARRAY, SAMPLER_CUBEMAP, SAMPLER_EXTERNAL, SAMPLER_3D, SAMPLER_CUBEMAP_ARRAY }

    actual enum class InternalFormat {
        R8, R8_SNORM, R8UI, R8I, STENCIL8, R16F, R16UI, R16I, RG8, RG8_SNORM, RG8UI, RG8I, RGB565, RGB9_E5, RGB5_A1, RGBA4, DEPTH16, RGB8, SRGB8, RGB8_SNORM, RGB8UI, RGB8I, DEPTH24, R32F, R32UI, R32I, RG16F, RG16UI, RG16I, R11F_G11F_B10F, RGBA8, SRGB8_A8, RGBA8_SNORM, UNUSED, RGB10_A2, RGBA8UI, RGBA8I, DEPTH32F, DEPTH24_STENCIL8, DEPTH32F_STENCIL8, RGB16F, RGB16UI, RGB16I, RG32F, RG32UI, RG32I, RGBA16F, RGBA16UI, RGBA16I, RGB32F, RGB32UI, RGB32I, RGBA32F, RGBA32UI, RGBA32I, EAC_R11, EAC_R11_SIGNED, EAC_RG11, EAC_RG11_SIGNED, ETC2_RGB8, ETC2_SRGB8, ETC2_RGB8_A1, ETC2_SRGB8_A1, ETC2_EAC_RGBA8, ETC2_EAC_SRGBA8, DXT1_RGB, DXT1_RGBA, DXT3_RGBA, DXT5_RGBA, DXT1_SRGB, DXT1_SRGBA, DXT3_SRGBA, DXT5_SRGBA, RGBA_ASTC_4x4, RGBA_ASTC_5x4, RGBA_ASTC_5x5, RGBA_ASTC_6x5, RGBA_ASTC_6x6, RGBA_ASTC_8x5, RGBA_ASTC_8x6, RGBA_ASTC_8x8, RGBA_ASTC_10x5, RGBA_ASTC_10x6, RGBA_ASTC_10x8, RGBA_ASTC_10x10, RGBA_ASTC_12x10, RGBA_ASTC_12x12, SRGB8_ALPHA8_ASTC_4x4, SRGB8_ALPHA8_ASTC_5x4, SRGB8_ALPHA8_ASTC_5x5, SRGB8_ALPHA8_ASTC_6x5, SRGB8_ALPHA8_ASTC_6x6, SRGB8_ALPHA8_ASTC_8x5, SRGB8_ALPHA8_ASTC_8x6, SRGB8_ALPHA8_ASTC_8x8, SRGB8_ALPHA8_ASTC_10x5, SRGB8_ALPHA8_ASTC_10x6, SRGB8_ALPHA8_ASTC_10x8, SRGB8_ALPHA8_ASTC_10x10, SRGB8_ALPHA8_ASTC_12x10, SRGB8_ALPHA8_ASTC_12x12, RED_RGTC1, SIGNED_RED_RGTC1, RED_GREEN_RGTC2, SIGNED_RED_GREEN_RGTC2, RGB_BPTC_SIGNED_FLOAT, RGB_BPTC_UNSIGNED_FLOAT, RGBA_BPTC_UNORM, SRGB_ALPHA_BPTC_UNORM
    }

    actual enum class CubemapFace { POSITIVE_X, NEGATIVE_X, POSITIVE_Y, NEGATIVE_Y, POSITIVE_Z, NEGATIVE_Z }

    actual enum class Format { R, R_INTEGER, RG, RG_INTEGER, RGB, RGB_INTEGER, RGBA, RGBA_INTEGER, UNUSED, DEPTH_COMPONENT, DEPTH_STENCIL, STENCIL_INDEX, ALPHA }

    actual enum class Type { UBYTE, BYTE, USHORT, SHORT, UINT, INT, HALF, FLOAT, COMPRESSED, UINT_10F_11F_11F_REV, USHORT_565 }

    actual enum class Swizzle { SUBSTITUTE_ZERO, SUBSTITUTE_ONE, CHANNEL_0, CHANNEL_1, CHANNEL_2, CHANNEL_3 }

    actual class Usage {
        actual companion object {
            actual val COLOR_ATTACHMENT: Int = 1
            actual val DEPTH_ATTACHMENT: Int = 2
            actual val STENCIL_ATTACHMENT: Int = 4
            actual val UPLOADABLE: Int = 8
            actual val SAMPLEABLE: Int = 16
            actual val SUBPASS_INPUT: Int = 32
            actual val BLIT_SRC: Int = 64
            actual val BLIT_DST: Int = 128
            actual val PROTECTED: Int = 256
            actual val GEN_MIPMAPPABLE: Int = 512
            actual val DEFAULT: Int = 24
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
        internal val jsPbd: JSPixelBufferDescriptor = run {
            // Upstream exposes `Filament.PixelBuffer(typedarray, format, datatype)` (a
            // function, not a constructor) which copies the typed array into the WASM
            // heap and returns a driver$PixelBufferDescriptor. The class itself is
            // `Filament.driver$PixelBufferDescriptor`; there's no `Filament.PixelBufferDescriptor`.
            val u8 = org.khronos.webgl.Int8Array(storage.size).also { arr ->
                storage.forEachIndexed { i, b -> arr.asDynamic()[i] = b }
            }
            val typed = org.khronos.webgl.Uint8Array(u8.buffer)
            (js("Filament").PixelBuffer)(typed, mapFormat(format), mapType(type)).unsafeCast<JSPixelBufferDescriptor>()
        }
    }

    actual fun getWidth(level: Int): Int {
        engine?.let { return jsTexture.getWidth(it.jsEngine, level).toInt() }
        return if (level == 0) _width else (_width shr level).coerceAtLeast(1)
    }

    actual fun getHeight(level: Int): Int {
        engine?.let { return jsTexture.getHeight(it.jsEngine, level).toInt() }
        return if (level == 0) _height else (_height shr level).coerceAtLeast(1)
    }

    actual fun getDepth(level: Int): Int {
        engine?.let { return jsTexture.getDepth(it.jsEngine, level).toInt() }
        return if (level == 0) _depth else (_depth shr level).coerceAtLeast(1)
    }

    actual fun getLevels(): Int {
        engine?.let { return jsTexture.getLevels(it.jsEngine).toInt() }
        return _levels
    }

    actual fun getTarget(): Sampler {
        return _target
    }

    actual fun getFormat(): InternalFormat {
        return _format
    }

    actual fun setImage(
        engine: Engine,
        level: Int,
        descriptor: PixelBufferDescriptor
    ) {
        if (_target == Sampler.SAMPLER_CUBEMAP) {
            jsTexture.setImageCube(engine.jsEngine, level, descriptor.jsPbd)
        } else {
            jsTexture.setImage(engine.jsEngine, level, descriptor.jsPbd)
        }
    }

    actual fun setImage(
        engine: Engine,
        level: Int,
        xoffset: Int,
        yoffset: Int,
        width: Int,
        height: Int,
        descriptor: PixelBufferDescriptor
    ) {
        // JS bindings only support setImage(engine, level, pbd); sub-region upload not available
        jsTexture.setImage(engine.jsEngine, level, descriptor.jsPbd)
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
        descriptor: PixelBufferDescriptor
    ) {
        // Deep setImage is for 3D textures or arrays
        jsTexture.asDynamic().setImage(engine.jsEngine, level, xoffset, yoffset, zoffset, width, height, depth, descriptor.jsPbd)
    }

    actual fun setExternalStream(
        engine: Engine,
        stream: Stream
    ) {
    }

    actual fun generateMipmaps(engine: Engine) {
        jsTexture.generateMipmaps(engine.jsEngine)
    }

    actual companion object {
        actual fun isTextureFormatSupported(
            engine: Engine,
            format: InternalFormat
        ): Boolean {
            return true
        }

        actual fun isTextureFormatMipmappable(
            engine: Engine,
            format: InternalFormat
        ): Boolean {
            return JSTexture.isTextureFormatMipmappable(engine.jsEngine, mapInternalFormat(format))
        }

        actual fun isTextureSwizzleSupported(engine: Engine): Boolean {
            // TODO(js): isTextureSwizzleSupported not bound in jsbindings.cpp — see docs/patches/UPSTREAM_INCONSISTENCIES.md.
            return false
        }

        actual fun validatePixelFormatAndType(
            internalFormat: InternalFormat,
            pixelDataFormat: Format,
            pixelDataType: Type
        ): Boolean {
            return JSTexture.validatePixelFormatAndType(
                mapInternalFormat(internalFormat),
                mapFormat(pixelDataFormat),
                mapType(pixelDataType),
            )
        }

        actual fun getMaxTextureSize(
            engine: Engine,
            type: Sampler
        ): Int {
            return 8192
        }

        actual fun getMaxArrayTextureLayers(engine: Engine): Int {
            return 256
        }

        actual fun computeDataSize(
            format: Format,
            type: Type,
            stride: Int,
            height: Int,
            alignment: Int
        ): Int {
            val bytesPerPixel = when (type) {
                Type.UBYTE, Type.BYTE -> 1
                Type.USHORT, Type.SHORT, Type.HALF -> 2
                Type.UINT, Type.INT, Type.FLOAT -> 4
                else -> 1
            }
            val rowSize = stride * bytesPerPixel
            val alignedRowSize = if (alignment > 1) ((rowSize + alignment - 1) / alignment) * alignment else rowSize
            return alignedRowSize * height
        }
    }
}

private fun mapInternalFormat(format: Texture.InternalFormat): JSTextureInternalFormat {
    return when(format) {
        Texture.InternalFormat.R8 -> JSTextureInternalFormat.R8
        Texture.InternalFormat.RGBA8 -> JSTextureInternalFormat.RGBA8
        Texture.InternalFormat.SRGB8_A8 -> JSTextureInternalFormat.SRGB8_A8
        Texture.InternalFormat.RGB8 -> JSTextureInternalFormat.RGB8
        Texture.InternalFormat.SRGB8 -> JSTextureInternalFormat.SRGB8
        Texture.InternalFormat.DEPTH16 -> JSTextureInternalFormat.DEPTH16
        Texture.InternalFormat.DEPTH24 -> JSTextureInternalFormat.DEPTH24
        Texture.InternalFormat.DEPTH32F -> JSTextureInternalFormat.DEPTH32F
        Texture.InternalFormat.RGBA16F -> JSTextureInternalFormat.RGBA16F
        Texture.InternalFormat.RGBA32F -> JSTextureInternalFormat.RGBA32F
        else -> JSTextureInternalFormat.RGBA8 // Fallback
    }
}

private fun mapFormat(format: Texture.Format): PixelDataFormat {
    return when(format) {
        Texture.Format.R -> PixelDataFormat.R
        Texture.Format.RG -> PixelDataFormat.RG
        Texture.Format.RGB -> PixelDataFormat.RGB
        Texture.Format.RGBA -> PixelDataFormat.RGBA
        Texture.Format.DEPTH_COMPONENT -> PixelDataFormat.DEPTH_COMPONENT
        Texture.Format.DEPTH_STENCIL -> PixelDataFormat.DEPTH_STENCIL
        Texture.Format.ALPHA -> PixelDataFormat.ALPHA
        else -> PixelDataFormat.RGBA
    }
}

private fun mapType(type: Texture.Type): PixelDataType {
    return when(type) {
        Texture.Type.UBYTE -> PixelDataType.UBYTE
        Texture.Type.BYTE -> PixelDataType.BYTE
        Texture.Type.USHORT -> PixelDataType.USHORT
        Texture.Type.SHORT -> PixelDataType.SHORT
        Texture.Type.UINT -> PixelDataType.UINT
        Texture.Type.INT -> PixelDataType.INT
        Texture.Type.HALF -> PixelDataType.HALF
        Texture.Type.FLOAT -> PixelDataType.FLOAT
        else -> PixelDataType.UBYTE
    }
}