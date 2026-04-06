@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import dev.filament.kmp.cinterop.*
import kotlinx.cinterop.*

actual class TextureSampler {
    internal var nativeObject: Long = 0L

    actual constructor() {
        nativeObject = FilaTextureSampler_create(
            FILA_TEXTURE_SAMPLER_MIN_FILTER_NEAREST,
            FILA_TEXTURE_SAMPLER_MAG_FILTER_NEAREST,
            FILA_TEXTURE_SAMPLER_WRAP_MODE_CLAMP_TO_EDGE,
            FILA_TEXTURE_SAMPLER_WRAP_MODE_CLAMP_TO_EDGE,
            FILA_TEXTURE_SAMPLER_WRAP_MODE_CLAMP_TO_EDGE
        ).toLong()
    }

    actual constructor(minMag: MagFilter) : this(MinFilter.entries[minMag.ordinal], minMag, WrapMode.CLAMP_TO_EDGE)

    actual constructor(minMag: MagFilter, wrap: WrapMode) : this(MinFilter.entries[minMag.ordinal], minMag, wrap)

    actual constructor(min: MinFilter, mag: MagFilter, wrap: WrapMode) : this(min, mag, wrap, wrap, wrap)

    actual constructor(min: MinFilter, mag: MagFilter, s: WrapMode, t: WrapMode, r: WrapMode) {
        nativeObject = FilaTextureSampler_create(
            min.toFila(),
            mag.toFila(),
            s.toFila(),
            t.toFila(),
            r.toFila()
        ).toLong()
    }

    actual constructor(mode: CompareMode) : this(mode, CompareFunction.LESS_EQUAL)

    actual constructor(mode: CompareMode, function: CompareFunction) {
        nativeObject = FilaTextureSampler_createCompare(
            mode.toFila(),
            function.toFila()
        ).toLong()
    }

    internal constructor(native: Long) {
        this.nativeObject = native
    }

    actual val minFilter: MinFilter
        get() = MinFilter.entries[FilaTextureSampler_getMinFilter(nativeObject.toULong()).toInt()]

    actual val magFilter: MagFilter
        get() = MagFilter.entries[FilaTextureSampler_getMagFilter(nativeObject.toULong()).toInt()]

    actual val wrapModeS: WrapMode
        get() = WrapMode.entries[FilaTextureSampler_getWrapModeS(nativeObject.toULong()).toInt()]

    actual val wrapModeT: WrapMode
        get() = WrapMode.entries[FilaTextureSampler_getWrapModeT(nativeObject.toULong()).toInt()]

    actual val wrapModeR: WrapMode
        get() = WrapMode.entries[FilaTextureSampler_getWrapModeR(nativeObject.toULong()).toInt()]

    actual val anisotropy: Float
        get() = FilaTextureSampler_getAnisotropy(nativeObject.toULong())

    actual val compareMode: CompareMode
        get() = CompareMode.entries[FilaTextureSampler_getCompareMode(nativeObject.toULong()).toInt()]

    actual val compareFunction: CompareFunction
        get() = CompareFunction.entries[FilaTextureSampler_getCompareFunction(nativeObject.toULong()).toInt()]

    actual enum class WrapMode {
        CLAMP_TO_EDGE,
        REPEAT,
        MIRRORED_REPEAT,
    }

    actual enum class MinFilter {
        NEAREST,
        LINEAR,
        NEAREST_MIPMAP_NEAREST,
        LINEAR_MIPMAP_NEAREST,
        NEAREST_MIPMAP_LINEAR,
        LINEAR_MIPMAP_LINEAR,
    }

    actual enum class MagFilter {
        NEAREST,
        LINEAR,
    }

    actual enum class CompareMode {
        NONE,
        COMPARE_TO_TEXTURE,
    }

    actual enum class CompareFunction {
        LESS_EQUAL, // C: LE
        GREATER_EQUAL, // C: GE
        LESS, // C: L
        GREATER, // C: G
        EQUAL, // C: E
        NOT_EQUAL, // C: NE
        ALWAYS, // C: A
        NEVER, // C: N
    }

    private fun WrapMode.toFila() = when (this) {
        WrapMode.CLAMP_TO_EDGE -> FILA_TEXTURE_SAMPLER_WRAP_MODE_CLAMP_TO_EDGE
        WrapMode.REPEAT -> FILA_TEXTURE_SAMPLER_WRAP_MODE_REPEAT
        WrapMode.MIRRORED_REPEAT -> FILA_TEXTURE_SAMPLER_WRAP_MODE_MIRRORED_REPEAT
    }

    private fun MinFilter.toFila() = when (this) {
        MinFilter.NEAREST -> FILA_TEXTURE_SAMPLER_MIN_FILTER_NEAREST
        MinFilter.LINEAR -> FILA_TEXTURE_SAMPLER_MIN_FILTER_LINEAR
        MinFilter.NEAREST_MIPMAP_NEAREST -> FILA_TEXTURE_SAMPLER_MIN_FILTER_NEAREST_MIPMAP_NEAREST
        MinFilter.LINEAR_MIPMAP_NEAREST -> FILA_TEXTURE_SAMPLER_MIN_FILTER_LINEAR_MIPMAP_NEAREST
        MinFilter.NEAREST_MIPMAP_LINEAR -> FILA_TEXTURE_SAMPLER_MIN_FILTER_NEAREST_MIPMAP_LINEAR
        MinFilter.LINEAR_MIPMAP_LINEAR -> FILA_TEXTURE_SAMPLER_MIN_FILTER_LINEAR_MIPMAP_LINEAR
    }

    private fun MagFilter.toFila() = when (this) {
        MagFilter.NEAREST -> FILA_TEXTURE_SAMPLER_MAG_FILTER_NEAREST
        MagFilter.LINEAR -> FILA_TEXTURE_SAMPLER_MAG_FILTER_LINEAR
    }

    private fun CompareMode.toFila() = when (this) {
        CompareMode.NONE -> FILA_TEXTURE_SAMPLER_COMPARE_MODE_NONE
        CompareMode.COMPARE_TO_TEXTURE -> FILA_TEXTURE_SAMPLER_COMPARE_MODE_COMPARE_TO_TEXTURE
    }

    private fun CompareFunction.toFila() = when (this) {
        CompareFunction.LESS_EQUAL -> FILA_TEXTURE_SAMPLER_COMPARE_FUNC_LE
        CompareFunction.GREATER_EQUAL -> FILA_TEXTURE_SAMPLER_COMPARE_FUNC_GE
        CompareFunction.LESS -> FILA_TEXTURE_SAMPLER_COMPARE_FUNC_L
        CompareFunction.GREATER -> FILA_TEXTURE_SAMPLER_COMPARE_FUNC_G
        CompareFunction.EQUAL -> FILA_TEXTURE_SAMPLER_COMPARE_FUNC_E
        CompareFunction.NOT_EQUAL -> FILA_TEXTURE_SAMPLER_COMPARE_FUNC_NE
        CompareFunction.ALWAYS -> FILA_TEXTURE_SAMPLER_COMPARE_FUNC_A
        CompareFunction.NEVER -> FILA_TEXTURE_SAMPLER_COMPARE_FUNC_N
    }
}
