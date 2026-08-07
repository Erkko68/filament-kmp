package io.github.erkko68.filament

import io.github.erkko68.filament.web.Texture as JSTexture
import io.github.erkko68.filament.web.Texture_InternalFormat as JSTextureInternalFormat
import io.github.erkko68.filament.web.`driver_PixelBufferDescriptor` as JSPixelBufferDescriptor
import io.github.erkko68.filament.web.PixelDataFormat
import io.github.erkko68.filament.web.PixelDataType
import io.github.erkko68.filament.web.Texture_Builder as JSTextureBuilder
import org.khronos.webgl.set

// The generated Texture external only binds setImage(engine, level, pbd); the deep/sub-region
// overload exists in filament.js but isn't emitted, so re-type it here instead of `asDynamic()`.
private external interface JsTextureExt : JsAny  {
    fun setImage(
        engine: io.github.erkko68.filament.web.Engine,
        level: Int, xoffset: Int, yoffset: Int, zoffset: Int,
        width: Int, height: Int, depth: Int,
        pbd: JSPixelBufferDescriptor,
    )
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual class Texture(val jsTexture: JSTexture) {
    // Filament JS exposes dimensions only via `_getWidth(engine, level)` etc.
    // (see jsbindings.cpp), so when an engine is known we delegate; otherwise
    // we fall back to the dimensions captured when the Texture was built.
    // getTarget/getFormat take no engine, so they always go to the engine.
    internal var engine: Engine? = null
    private var _width = 0
    private var _height = 0
    private var _depth = 0
    private var _levels = 1

    internal constructor(jsTexture: JSTexture, engine: Engine, width: Int, height: Int, depth: Int, levels: Int) : this(jsTexture) {
        this.engine = engine
        _width = width
        _height = height
        _depth = depth
        _levels = levels
    }

    actual class Builder actual constructor() {
        private val jsBuilder: JSTextureBuilder = JSTexture.Builder()
        private var _width = 1
        private var _height = 1
        private var _depth = 1
        private var _levels = 1

        actual fun width(width: Int): Builder {
            _width = width
            jsBuilder.width(width.toDouble())
            return this
        }

        actual fun height(height: Int): Builder {
            _height = height
            jsBuilder.height(height.toDouble())
            return this
        }

        actual fun depth(depth: Int): Builder {
            _depth = depth
            jsBuilder.depth(depth.toDouble())
            return this
        }

        actual fun levels(levels: Int): Builder {
            _levels = levels
            jsBuilder.levels(levels.toDouble())
            return this
        }

        actual fun samples(samples: Int): Builder {
            jsBuilder.samples(samples.toDouble())
            return this
        }

        actual fun sampler(target: Sampler): Builder {
            jsBuilder.sampler(when (target) {
                Sampler.SAMPLER_2D -> io.github.erkko68.filament.web.Texture_Sampler.SAMPLER_2D
                Sampler.SAMPLER_2D_ARRAY -> io.github.erkko68.filament.web.Texture_Sampler.SAMPLER_2D_ARRAY
                Sampler.SAMPLER_CUBEMAP -> io.github.erkko68.filament.web.Texture_Sampler.SAMPLER_CUBEMAP
                Sampler.SAMPLER_EXTERNAL -> io.github.erkko68.filament.web.Texture_Sampler.SAMPLER_EXTERNAL
                Sampler.SAMPLER_3D -> io.github.erkko68.filament.web.Texture_Sampler.SAMPLER_3D
                Sampler.SAMPLER_CUBEMAP_ARRAY -> io.github.erkko68.filament.web.Texture_Sampler.SAMPLER_CUBEMAP_ARRAY
            })
            return this
        }

        actual fun format(format: InternalFormat): Builder {
            jsBuilder.format(mapInternalFormat(format))
            return this
        }

        actual fun usage(usage: Int): Builder {
            jsBuilder.usage(usage.toDouble())
            return this
        }

        // WebGL has no texture swizzle, so build() rejects a swizzled texture; the binding
        // exists so callers get that explicit error instead of a silently ignored swizzle.
        // Pair with Texture.isTextureSwizzleSupported, which returns false here.
        actual fun swizzle(
            r: Swizzle,
            g: Swizzle,
            b: Swizzle,
            a: Swizzle
        ): Builder {
            jsBuilder.swizzle(mapSwizzle(r), mapSwizzle(g), mapSwizzle(b), mapSwizzle(a))
            return this
        }

        @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — importTexture takes a backend texture handle, which filament.js does not expose.")
        actual fun importTexture(id: Long): Builder {
            return this
        }

        actual fun external(): Builder {
            jsBuilder.external()
            return this
        }

        actual fun build(engine: Engine): Texture {
            return Texture(jsBuilder.build(engine.jsEngine), engine, _width, _height, _depth, _levels)
        }
    }

    actual enum class Sampler { SAMPLER_2D, SAMPLER_2D_ARRAY, SAMPLER_CUBEMAP, SAMPLER_EXTERNAL, SAMPLER_3D, SAMPLER_CUBEMAP_ARRAY }

    actual enum class InternalFormat {
        R8, R8_SNORM, R8UI, R8I, STENCIL8, R16F, R16UI, R16I, RG8, RG8_SNORM, RG8UI, RG8I, RGB565, RGB9_E5, RGB5_A1, RGBA4, DEPTH16, RGB8, SRGB8, RGB8_SNORM, RGB8UI, RGB8I, DEPTH24, R32F, R32UI, R32I, RG16F, RG16UI, RG16I, R11F_G11F_B10F, RGBA8, SRGB8_A8, RGBA8_SNORM, UNUSED, RGB10_A2, RGBA8UI, RGBA8I, DEPTH32F, DEPTH24_STENCIL8, DEPTH32F_STENCIL8, RGB16F, RGB16UI, RGB16I, RG32F, RG32UI, RG32I, RGBA16F, RGBA16UI, RGBA16I, RGB32F, RGB32UI, RGB32I, RGBA32F, RGBA32UI, RGBA32I, EAC_R11, EAC_R11_SIGNED, EAC_RG11, EAC_RG11_SIGNED, ETC2_RGB8, ETC2_SRGB8, ETC2_RGB8_A1, ETC2_SRGB8_A1, ETC2_EAC_RGBA8, ETC2_EAC_SRGBA8, DXT1_RGB, DXT1_RGBA, DXT3_RGBA, DXT5_RGBA, DXT1_SRGB, DXT1_SRGBA, DXT3_SRGBA, DXT5_SRGBA, RGBA_ASTC_4x4, RGBA_ASTC_5x4, RGBA_ASTC_5x5, RGBA_ASTC_6x5, RGBA_ASTC_6x6, RGBA_ASTC_8x5, RGBA_ASTC_8x6, RGBA_ASTC_8x8, RGBA_ASTC_10x5, RGBA_ASTC_10x6, RGBA_ASTC_10x8, RGBA_ASTC_10x10, RGBA_ASTC_12x10, RGBA_ASTC_12x12, SRGB8_ALPHA8_ASTC_4x4, SRGB8_ALPHA8_ASTC_5x4, SRGB8_ALPHA8_ASTC_5x5, SRGB8_ALPHA8_ASTC_6x5, SRGB8_ALPHA8_ASTC_6x6, SRGB8_ALPHA8_ASTC_8x5, SRGB8_ALPHA8_ASTC_8x6, SRGB8_ALPHA8_ASTC_8x8, SRGB8_ALPHA8_ASTC_10x5, SRGB8_ALPHA8_ASTC_10x6, SRGB8_ALPHA8_ASTC_10x8, SRGB8_ALPHA8_ASTC_10x10, SRGB8_ALPHA8_ASTC_12x10, SRGB8_ALPHA8_ASTC_12x12, RED_RGTC1, SIGNED_RED_RGTC1, RED_GREEN_RGTC2, SIGNED_RED_GREEN_RGTC2, RGB_BPTC_SIGNED_FLOAT, RGB_BPTC_UNSIGNED_FLOAT, RGBA_BPTC_UNORM, SRGB_ALPHA_BPTC_UNORM
    }

    actual enum class CubemapFace { POSITIVE_X, NEGATIVE_X, POSITIVE_Y, NEGATIVE_Y, POSITIVE_Z, NEGATIVE_Z }

    actual enum class Format { R, R_INTEGER, RG, RG_INTEGER, RGB, RGB_INTEGER, RGBA, RGBA_INTEGER, UNUSED, DEPTH_COMPONENT, DEPTH_STENCIL, ALPHA }

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
                storage.forEachIndexed { i, b -> arr[i] = b }
            }
            val typed = org.khronos.webgl.Uint8Array(u8.buffer)
            newPixelBuffer(typed, mapFormat(format), mapType(type))
        }
    }

    actual fun getWidth(level: Int): Int {
        engine?.let { return jsTexture.getWidth(it.jsEngine, level.toDouble()).toInt() }
        return if (level == 0) _width else (_width shr level).coerceAtLeast(1)
    }

    actual fun getHeight(level: Int): Int {
        engine?.let { return jsTexture.getHeight(it.jsEngine, level.toDouble()).toInt() }
        return if (level == 0) _height else (_height shr level).coerceAtLeast(1)
    }

    actual fun getDepth(level: Int): Int {
        engine?.let { return jsTexture.getDepth(it.jsEngine, level.toDouble()).toInt() }
        return if (level == 0) _depth else (_depth shr level).coerceAtLeast(1)
    }

    actual fun getLevels(): Int {
        engine?.let { return jsTexture.getLevels(it.jsEngine).toInt() }
        return _levels
    }

    actual fun getTarget(): Sampler = when (jsTexture.getTarget()) {
        io.github.erkko68.filament.web.Texture_Sampler.SAMPLER_2D_ARRAY -> Sampler.SAMPLER_2D_ARRAY
        io.github.erkko68.filament.web.Texture_Sampler.SAMPLER_CUBEMAP -> Sampler.SAMPLER_CUBEMAP
        io.github.erkko68.filament.web.Texture_Sampler.SAMPLER_EXTERNAL -> Sampler.SAMPLER_EXTERNAL
        io.github.erkko68.filament.web.Texture_Sampler.SAMPLER_3D -> Sampler.SAMPLER_3D
        io.github.erkko68.filament.web.Texture_Sampler.SAMPLER_CUBEMAP_ARRAY -> Sampler.SAMPLER_CUBEMAP_ARRAY
        else -> Sampler.SAMPLER_2D
    }

    actual fun getFormat(): InternalFormat = jsTexture.getFormat().toCommon()

    actual fun setImage(
        engine: Engine,
        level: Int,
        descriptor: PixelBufferDescriptor
    ) {
        // 1.73.0 removed the _setImageCube binding; setImage now uploads a full cubemap level
        // (all six faces, tightly packed) just like the other targets.
        jsTexture.setImage(engine.jsEngine, level.toDouble(), descriptor.jsPbd)
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
        jsTexture.setImage(engine.jsEngine, level.toDouble(), descriptor.jsPbd)
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
        jsTexture.unsafeCast<JsTextureExt>().setImage(
            engine.jsEngine, level, xoffset, yoffset, zoffset, width, height, depth, descriptor.jsPbd
        )
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
            return JSTexture.isTextureSwizzleSupported(engine.jsEngine)
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

// Texture$InternalFormat registers Filament's TextureFormat in declaration order, so the
// first 101 entries line up 1:1 with the common enum and the ordinal is the mapping. The
// trailing RGTC/BPTC formats have no JS counterpart (WebGL cannot sample them anyway).
private const val JS_INTERNAL_FORMAT_COUNT = 101

private fun mapInternalFormat(format: Texture.InternalFormat): JSTextureInternalFormat {
    val ordinal = format.ordinal
    if (ordinal >= JS_INTERNAL_FORMAT_COUNT) return JSTextureInternalFormat.RGBA8
    return JSTextureInternalFormat.values
        .unsafeCast<js.array.JsArray<JSTextureInternalFormat>>()[ordinal]!!
}

private fun JSTextureInternalFormat.toCommon(): Texture.InternalFormat =
    Texture.InternalFormat.entries[value.toInt()]

private fun mapSwizzle(s: Texture.Swizzle): io.github.erkko68.filament.web.Texture_Swizzle = when (s) {
    Texture.Swizzle.SUBSTITUTE_ZERO -> io.github.erkko68.filament.web.Texture_Swizzle.SUBSTITUTE_ZERO
    Texture.Swizzle.SUBSTITUTE_ONE -> io.github.erkko68.filament.web.Texture_Swizzle.SUBSTITUTE_ONE
    Texture.Swizzle.CHANNEL_0 -> io.github.erkko68.filament.web.Texture_Swizzle.CHANNEL_0
    Texture.Swizzle.CHANNEL_1 -> io.github.erkko68.filament.web.Texture_Swizzle.CHANNEL_1
    Texture.Swizzle.CHANNEL_2 -> io.github.erkko68.filament.web.Texture_Swizzle.CHANNEL_2
    Texture.Swizzle.CHANNEL_3 -> io.github.erkko68.filament.web.Texture_Swizzle.CHANNEL_3
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
// Filament.PixelBuffer(typedarray, format, datatype) copies the typed array into the WASM
// heap and returns a driver_PixelBufferDescriptor. Top-level so js() is legal on wasmJs.
private fun newPixelBuffer(data: JsAny?, format: JsAny?, datatype: JsAny?): JSPixelBufferDescriptor =
    js("Filament.PixelBuffer(data, format, datatype)")
