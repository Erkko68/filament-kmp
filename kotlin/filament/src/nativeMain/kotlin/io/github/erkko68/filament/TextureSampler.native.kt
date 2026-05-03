@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*

actual class TextureSampler {
    internal var nativeHandle: FilaTextureSampler = 0uL

    actual enum class WrapMode {
        CLAMP_TO_EDGE,
        REPEAT,
        MIRRORED_REPEAT
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
        LINEAR
    }

    actual enum class CompareMode {
        NONE,
        COMPARE_TO_TEXTURE
    }

    actual enum class CompareFunction {
        LESS_EQUAL,
        GREATER_EQUAL,
        LESS,
        GREATER,
        EQUAL,
        NOT_EQUAL,
        ALWAYS,
        NEVER
    }

    actual constructor() {
        nativeHandle = FilaTextureSampler_create(
            FILA_TEXTURE_SAMPLER_MIN_FILTER_LINEAR_MIPMAP_LINEAR,
            FILA_TEXTURE_SAMPLER_MAG_FILTER_LINEAR,
            FILA_TEXTURE_SAMPLER_WRAP_MODE_REPEAT,
            FILA_TEXTURE_SAMPLER_WRAP_MODE_REPEAT,
            FILA_TEXTURE_SAMPLER_WRAP_MODE_REPEAT
        )
    }

    actual constructor(minMag: MagFilter) {
        val min = if (minMag == MagFilter.NEAREST) FILA_TEXTURE_SAMPLER_MIN_FILTER_NEAREST else FILA_TEXTURE_SAMPLER_MIN_FILTER_LINEAR
        nativeHandle = FilaTextureSampler_create(
            min,
            minMag.toFila(),
            FILA_TEXTURE_SAMPLER_WRAP_MODE_CLAMP_TO_EDGE,
            FILA_TEXTURE_SAMPLER_WRAP_MODE_CLAMP_TO_EDGE,
            FILA_TEXTURE_SAMPLER_WRAP_MODE_CLAMP_TO_EDGE
        )
    }

    actual constructor(minMag: MagFilter, wrap: WrapMode) {
        val min = if (minMag == MagFilter.NEAREST) FILA_TEXTURE_SAMPLER_MIN_FILTER_NEAREST else FILA_TEXTURE_SAMPLER_MIN_FILTER_LINEAR
        nativeHandle = FilaTextureSampler_create(
            min,
            minMag.toFila(),
            wrap.toFila(),
            wrap.toFila(),
            wrap.toFila()
        )
    }

    actual constructor(min: MinFilter, mag: MagFilter, wrap: WrapMode) {
        nativeHandle = FilaTextureSampler_create(
            min.toFila(),
            mag.toFila(),
            wrap.toFila(),
            wrap.toFila(),
            wrap.toFila()
        )
    }

    actual constructor(min: MinFilter, mag: MagFilter, s: WrapMode, t: WrapMode, r: WrapMode) {
        nativeHandle = FilaTextureSampler_create(
            min.toFila(),
            mag.toFila(),
            s.toFila(),
            t.toFila(),
            r.toFila()
        )
    }

    actual constructor(mode: CompareMode) {
        nativeHandle = FilaTextureSampler_createCompare(mode.toFila(), FILA_TEXTURE_SAMPLER_COMPARE_FUNC_LE)
    }

    actual constructor(mode: CompareMode, function: CompareFunction) {
        nativeHandle = FilaTextureSampler_createCompare(mode.toFila(), function.toFila())
    }

    internal constructor(sampler: FilaTextureSampler) {
        nativeHandle = sampler
    }

    actual var minFilter: MinFilter
        get() = MinFilter.entries[FilaTextureSampler_getMinFilter(nativeHandle).toInt()]
        set(value) { nativeHandle = FilaTextureSampler_setMinFilter(nativeHandle, value.toFila()) }

    actual var magFilter: MagFilter
        get() = MagFilter.entries[FilaTextureSampler_getMagFilter(nativeHandle).toInt()]
        set(value) { nativeHandle = FilaTextureSampler_setMagFilter(nativeHandle, value.toFila()) }

    actual var wrapModeS: WrapMode
        get() = WrapMode.entries[FilaTextureSampler_getWrapModeS(nativeHandle).toInt()]
        set(value) { nativeHandle = FilaTextureSampler_setWrapModeS(nativeHandle, value.toFila()) }

    actual var wrapModeT: WrapMode
        get() = WrapMode.entries[FilaTextureSampler_getWrapModeT(nativeHandle).toInt()]
        set(value) { nativeHandle = FilaTextureSampler_setWrapModeT(nativeHandle, value.toFila()) }

    actual var wrapModeR: WrapMode
        get() = WrapMode.entries[FilaTextureSampler_getWrapModeR(nativeHandle).toInt()]
        set(value) { nativeHandle = FilaTextureSampler_setWrapModeR(nativeHandle, value.toFila()) }

    actual var anisotropy: Float
        get() = FilaTextureSampler_getAnisotropy(nativeHandle)
        set(value) { nativeHandle = FilaTextureSampler_setAnisotropy(nativeHandle, value) }

    actual var compareMode: CompareMode
        get() = CompareMode.entries[FilaTextureSampler_getCompareMode(nativeHandle).toInt()]
        set(value) { nativeHandle = FilaTextureSampler_setCompareMode(nativeHandle, value.toFila()) }

    actual var compareFunction: CompareFunction
        get() = CompareFunction.entries[FilaTextureSampler_getCompareFunction(nativeHandle).toInt()]
        set(value) { nativeHandle = FilaTextureSampler_setCompareFunction(nativeHandle, value.toFila()) }

    private fun WrapMode.toFila(): FilaTextureSamplerWrapMode = this.ordinal.toUInt()
    private fun MinFilter.toFila(): FilaTextureSamplerMinFilter = this.ordinal.toUInt()
    private fun MagFilter.toFila(): FilaTextureSamplerMagFilter = this.ordinal.toUInt()
    private fun CompareMode.toFila(): FilaTextureSamplerCompareMode = this.ordinal.toUInt()
    private fun CompareFunction.toFila(): FilaTextureSamplerCompareFunc = this.ordinal.toUInt()

}
