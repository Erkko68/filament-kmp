package dev.filament.kmp

import com.google.android.filament.TextureSampler as FilaTextureSampler

actual class TextureSampler {
    internal val mSampler: FilaTextureSampler

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
        mSampler = FilaTextureSampler()
    }

    actual constructor(minMag: MagFilter) {
        mSampler = FilaTextureSampler(minMag.toFila())
    }

    actual constructor(minMag: MagFilter, wrap: WrapMode) {
        mSampler = FilaTextureSampler(minMag.toFila(), wrap.toFila())
    }

    actual constructor(min: MinFilter, mag: MagFilter, wrap: WrapMode) {
        mSampler = FilaTextureSampler(min.toFila(), mag.toFila(), wrap.toFila())
    }

    actual constructor(min: MinFilter, mag: MagFilter, s: WrapMode, t: WrapMode, r: WrapMode) {
        mSampler = FilaTextureSampler(min.toFila(), mag.toFila(), s.toFila(), t.toFila(), r.toFila())
    }

    actual constructor(mode: CompareMode) {
        mSampler = FilaTextureSampler(mode.toFila())
    }

    actual constructor(mode: CompareMode, function: CompareFunction) {
        mSampler = FilaTextureSampler(mode.toFila(), function.toFila())
    }

    internal constructor(sampler: FilaTextureSampler) {
        mSampler = sampler
    }

    actual fun getMinFilter(): MinFilter = MinFilter.values()[mSampler.minFilter.ordinal]
    actual fun setMinFilter(filter: MinFilter) { mSampler.minFilter = filter.toFila() }

    actual fun getMagFilter(): MagFilter = MagFilter.values()[mSampler.magFilter.ordinal]
    actual fun setMagFilter(filter: MagFilter) { mSampler.magFilter = filter.toFila() }

    actual fun getWrapModeS(): WrapMode = WrapMode.values()[mSampler.wrapModeS.ordinal]
    actual fun setWrapModeS(mode: WrapMode) { mSampler.wrapModeS = mode.toFila() }

    actual fun getWrapModeT(): WrapMode = WrapMode.values()[mSampler.wrapModeT.ordinal]
    actual fun setWrapModeT(mode: WrapMode) { mSampler.wrapModeT = mode.toFila() }

    actual fun getWrapModeR(): WrapMode = WrapMode.values()[mSampler.wrapModeR.ordinal]
    actual fun setWrapModeR(mode: WrapMode) { mSampler.wrapModeR = mode.toFila() }

    actual fun getAnisotropy(): Float = mSampler.anisotropy
    actual fun setAnisotropy(anisotropy: Float) { mSampler.anisotropy = anisotropy }

    actual fun getCompareMode(): CompareMode = CompareMode.values()[mSampler.compareMode.ordinal]
    actual fun setCompareMode(mode: CompareMode) { mSampler.compareMode = mode.toFila() }

    actual fun getCompareFunction(): CompareFunction = CompareFunction.values()[mSampler.compareFunction.ordinal]
    actual fun setCompareFunction(function: CompareFunction) { mSampler.compareFunction = function.toFila() }

    private fun WrapMode.toFila() = FilaTextureSampler.WrapMode.values()[this.ordinal]
    private fun MinFilter.toFila() = FilaTextureSampler.MinFilter.values()[this.ordinal]
    private fun MagFilter.toFila() = FilaTextureSampler.MagFilter.values()[this.ordinal]
    private fun CompareMode.toFila() = FilaTextureSampler.CompareMode.values()[this.ordinal]
    private fun CompareFunction.toFila() = FilaTextureSampler.CompareFunction.values()[this.ordinal]
}
