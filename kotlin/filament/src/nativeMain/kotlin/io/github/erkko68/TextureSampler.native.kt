@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68

import kotlinx.cinterop.*
import io.github.erkko68.cinterop.*

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
            FILA_TEXTURE_SAMPLER_MIN_FILTER_LINEAR_MIPMAP_LINEAR.toUInt(),
            FILA_TEXTURE_SAMPLER_MAG_FILTER_LINEAR.toUInt(),
            FILA_TEXTURE_SAMPLER_WRAP_MODE_REPEAT.toUInt(),
            FILA_TEXTURE_SAMPLER_WRAP_MODE_REPEAT.toUInt(),
            FILA_TEXTURE_SAMPLER_WRAP_MODE_REPEAT.toUInt()
        )
    }

    actual constructor(minMag: MagFilter) {
        val min = if (minMag == MagFilter.NEAREST) FILA_TEXTURE_SAMPLER_MIN_FILTER_NEAREST else FILA_TEXTURE_SAMPLER_MIN_FILTER_LINEAR
        nativeHandle = FilaTextureSampler_create(
            min.toUInt(),
            minMag.toFila(),
            FILA_TEXTURE_SAMPLER_WRAP_MODE_CLAMP_TO_EDGE.toUInt(),
            FILA_TEXTURE_SAMPLER_WRAP_MODE_CLAMP_TO_EDGE.toUInt(),
            FILA_TEXTURE_SAMPLER_WRAP_MODE_CLAMP_TO_EDGE.toUInt()
        )
    }

    actual constructor(minMag: MagFilter, wrap: WrapMode) {
        val min = if (minMag == MagFilter.NEAREST) FILA_TEXTURE_SAMPLER_MIN_FILTER_NEAREST else FILA_TEXTURE_SAMPLER_MIN_FILTER_LINEAR
        nativeHandle = FilaTextureSampler_create(
            min.toUInt(),
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
        nativeHandle = FilaTextureSampler_createCompare(mode.toFila(), FILA_TEXTURE_SAMPLER_COMPARE_FUNC_LE.toUInt())
    }

    actual constructor(mode: CompareMode, function: CompareFunction) {
        nativeHandle = FilaTextureSampler_createCompare(mode.toFila(), function.toFila())
    }

    internal constructor(sampler: FilaTextureSampler) {
        nativeHandle = sampler
    }

    actual fun getMinFilter(): MinFilter {
        val f = FilaTextureSampler_getMinFilter(nativeHandle)
        return MinFilter.entries[f.toInt()]
    }
    actual fun setMinFilter(filter: MinFilter) {
        nativeHandle = FilaTextureSampler_setMinFilter(nativeHandle, filter.toFila())
    }

    actual fun getMagFilter(): MagFilter {
        val f = FilaTextureSampler_getMagFilter(nativeHandle)
        return MagFilter.entries[f.toInt()]
    }
    actual fun setMagFilter(filter: MagFilter) {
        nativeHandle = FilaTextureSampler_setMagFilter(nativeHandle, filter.toFila())
    }

    actual fun getWrapModeS(): WrapMode {
        val f = FilaTextureSampler_getWrapModeS(nativeHandle)
        return WrapMode.entries[f.toInt()]
    }
    actual fun setWrapModeS(mode: WrapMode) {
        nativeHandle = FilaTextureSampler_setWrapModeS(nativeHandle, mode.toFila())
    }

    actual fun getWrapModeT(): WrapMode {
        val f = FilaTextureSampler_getWrapModeT(nativeHandle)
        return WrapMode.entries[f.toInt()]
    }
    actual fun setWrapModeT(mode: WrapMode) {
        nativeHandle = FilaTextureSampler_setWrapModeT(nativeHandle, mode.toFila())
    }

    actual fun getWrapModeR(): WrapMode {
        val f = FilaTextureSampler_getWrapModeR(nativeHandle)
        return WrapMode.entries[f.toInt()]
    }
    actual fun setWrapModeR(mode: WrapMode) {
        nativeHandle = FilaTextureSampler_setWrapModeR(nativeHandle, mode.toFila())
    }

    actual fun getAnisotropy(): Float = FilaTextureSampler_getAnisotropy(nativeHandle)
    actual fun setAnisotropy(anisotropy: Float) {
        nativeHandle = FilaTextureSampler_setAnisotropy(nativeHandle, anisotropy)
    }

    actual fun getCompareMode(): CompareMode {
        val f = FilaTextureSampler_getCompareMode(nativeHandle)
        return CompareMode.entries[f.toInt()]
    }
    actual fun setCompareMode(mode: CompareMode) {
        nativeHandle = FilaTextureSampler_setCompareMode(nativeHandle, mode.toFila())
    }

    actual fun getCompareFunction(): CompareFunction {
        val f = FilaTextureSampler_getCompareFunction(nativeHandle)
        return if (f.toInt() == 0) CompareFunction.LESS_EQUAL else mappingFilaToCompareFunction(f)
    }
    actual fun setCompareFunction(function: CompareFunction) {
        nativeHandle = FilaTextureSampler_setCompareFunction(nativeHandle, function.toFila())
    }

    private fun WrapMode.toFila(): FilaTextureSamplerWrapMode = this.ordinal.toUInt()
    private fun MinFilter.toFila(): FilaTextureSamplerMinFilter = this.ordinal.toUInt()
    private fun MagFilter.toFila(): FilaTextureSamplerMagFilter = this.ordinal.toUInt()
    private fun CompareMode.toFila(): FilaTextureSamplerCompareMode = this.ordinal.toUInt()
    private fun CompareFunction.toFila(): FilaTextureSamplerCompareFunc = this.ordinal.toUInt()

    private fun mappingFilaToCompareFunction(f: FilaTextureSamplerCompareFunc): CompareFunction {
        return CompareFunction.entries[f.toInt()]
    }
}
