@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*

actual class TextureSampler {
    internal var mSampler: FilaTextureSampler = 0uL

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
        mSampler = FilaTextureSampler_create(
            FILA_TEXTURE_SAMPLER_MIN_FILTER_LINEAR_MIPMAP_LINEAR,
            FILA_TEXTURE_SAMPLER_MAG_FILTER_LINEAR,
            FILA_TEXTURE_SAMPLER_WRAP_MODE_REPEAT,
            FILA_TEXTURE_SAMPLER_WRAP_MODE_REPEAT,
            FILA_TEXTURE_SAMPLER_WRAP_MODE_REPEAT
        )
    }

    actual constructor(minMag: MagFilter) {
        val min = if (minMag == MagFilter.NEAREST) FILA_TEXTURE_SAMPLER_MIN_FILTER_NEAREST else FILA_TEXTURE_SAMPLER_MIN_FILTER_LINEAR
        mSampler = FilaTextureSampler_create(
            min,
            minMag.toFila(),
            FILA_TEXTURE_SAMPLER_WRAP_MODE_CLAMP_TO_EDGE,
            FILA_TEXTURE_SAMPLER_WRAP_MODE_CLAMP_TO_EDGE,
            FILA_TEXTURE_SAMPLER_WRAP_MODE_CLAMP_TO_EDGE
        )
    }

    actual constructor(minMag: MagFilter, wrap: WrapMode) {
        val min = if (minMag == MagFilter.NEAREST) FILA_TEXTURE_SAMPLER_MIN_FILTER_NEAREST else FILA_TEXTURE_SAMPLER_MIN_FILTER_LINEAR
        mSampler = FilaTextureSampler_create(
            min,
            minMag.toFila(),
            wrap.toFila(),
            wrap.toFila(),
            wrap.toFila()
        )
    }

    actual constructor(min: MinFilter, mag: MagFilter, wrap: WrapMode) {
        mSampler = FilaTextureSampler_create(
            min.toFila(),
            mag.toFila(),
            wrap.toFila(),
            wrap.toFila(),
            wrap.toFila()
        )
    }

    actual constructor(min: MinFilter, mag: MagFilter, s: WrapMode, t: WrapMode, r: WrapMode) {
        mSampler = FilaTextureSampler_create(
            min.toFila(),
            mag.toFila(),
            s.toFila(),
            t.toFila(),
            r.toFila()
        )
    }

    actual constructor(mode: CompareMode) {
        mSampler = FilaTextureSampler_createCompare(mode.toFila(), FILA_TEXTURE_SAMPLER_COMPARE_FUNC_LE)
    }

    actual constructor(mode: CompareMode, function: CompareFunction) {
        mSampler = FilaTextureSampler_createCompare(mode.toFila(), function.toFila())
    }

    internal constructor(sampler: FilaTextureSampler) {
        mSampler = sampler
    }

    actual fun getMinFilter(): MinFilter {
        val f = FilaTextureSampler_getMinFilter(mSampler)
        return MinFilter.values()[f.toInt()]
    }
    actual fun setMinFilter(filter: MinFilter) {
        mSampler = FilaTextureSampler_setMinFilter(mSampler, filter.toFila())
    }

    actual fun getMagFilter(): MagFilter {
        val f = FilaTextureSampler_getMagFilter(mSampler)
        return MagFilter.values()[f.toInt()]
    }
    actual fun setMagFilter(filter: MagFilter) {
        mSampler = FilaTextureSampler_setMagFilter(mSampler, filter.toFila())
    }

    actual fun getWrapModeS(): WrapMode {
        val f = FilaTextureSampler_getWrapModeS(mSampler)
        return WrapMode.values()[f.toInt()]
    }
    actual fun setWrapModeS(mode: WrapMode) {
        mSampler = FilaTextureSampler_setWrapModeS(mSampler, mode.toFila())
    }

    actual fun getWrapModeT(): WrapMode {
        val f = FilaTextureSampler_getWrapModeT(mSampler)
        return WrapMode.values()[f.toInt()]
    }
    actual fun setWrapModeT(mode: WrapMode) {
        mSampler = FilaTextureSampler_setWrapModeT(mSampler, mode.toFila())
    }

    actual fun getWrapModeR(): WrapMode {
        val f = FilaTextureSampler_getWrapModeR(mSampler)
        return WrapMode.values()[f.toInt()]
    }
    actual fun setWrapModeR(mode: WrapMode) {
        mSampler = FilaTextureSampler_setWrapModeR(mSampler, mode.toFila())
    }

    actual fun getAnisotropy(): Float = FilaTextureSampler_getAnisotropy(mSampler)
    actual fun setAnisotropy(anisotropy: Float) {
        mSampler = FilaTextureSampler_setAnisotropy(mSampler, anisotropy)
    }

    actual fun getCompareMode(): CompareMode {
        val f = FilaTextureSampler_getCompareMode(mSampler)
        return CompareMode.values()[f.toInt()]
    }
    actual fun setCompareMode(mode: CompareMode) {
        mSampler = FilaTextureSampler_setCompareMode(mSampler, mode.toFila())
    }

    actual fun getCompareFunction(): CompareFunction {
        val f = FilaTextureSampler_getCompareFunction(mSampler)
        return if (f.toInt() == 0) CompareFunction.LESS_EQUAL else mappingFilaToCompareFunction(f)
    }
    actual fun setCompareFunction(function: CompareFunction) {
        mSampler = FilaTextureSampler_setCompareFunction(mSampler, function.toFila())
    }

    private fun WrapMode.toFila(): FilaTextureSamplerWrapMode = this.ordinal.toUInt()
    private fun MinFilter.toFila(): FilaTextureSamplerMinFilter = this.ordinal.toUInt()
    private fun MagFilter.toFila(): FilaTextureSamplerMagFilter = this.ordinal.toUInt()
    private fun CompareMode.toFila(): FilaTextureSamplerCompareMode = this.ordinal.toUInt()
    private fun CompareFunction.toFila(): FilaTextureSamplerCompareFunc = this.ordinal.toUInt()

    private fun mappingFilaToCompareFunction(f: FilaTextureSamplerCompareFunc): CompareFunction {
        return CompareFunction.values()[f.toInt()]
    }
}
