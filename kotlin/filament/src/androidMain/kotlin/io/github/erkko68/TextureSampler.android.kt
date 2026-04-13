package io.github.erkko68

import com.google.android.filament.TextureSampler as FilaTextureSampler

actual class TextureSampler {
    internal val nativeSampler: FilaTextureSampler

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
        nativeSampler = FilaTextureSampler()
    }

    actual constructor(minMag: MagFilter) {
        nativeSampler = FilaTextureSampler(minMag.toFila())
    }

    actual constructor(minMag: MagFilter, wrap: WrapMode) {
        nativeSampler = FilaTextureSampler(minMag.toFila(), wrap.toFila())
    }

    actual constructor(min: MinFilter, mag: MagFilter, wrap: WrapMode) {
        nativeSampler = FilaTextureSampler(min.toFila(), mag.toFila(), wrap.toFila())
    }

    actual constructor(min: MinFilter, mag: MagFilter, s: WrapMode, t: WrapMode, r: WrapMode) {
        nativeSampler = FilaTextureSampler(min.toFila(), mag.toFila(), s.toFila(), t.toFila(), r.toFila())
    }

    actual constructor(mode: CompareMode) {
        nativeSampler = FilaTextureSampler(mode.toFila())
    }

    actual constructor(mode: CompareMode, function: CompareFunction) {
        nativeSampler = FilaTextureSampler(mode.toFila(), function.toFila())
    }

    internal constructor(sampler: FilaTextureSampler) {
        nativeSampler = sampler
    }

    actual fun getMinFilter(): MinFilter = MinFilter.values()[nativeSampler.minFilter.ordinal]
    actual fun setMinFilter(filter: MinFilter) { nativeSampler.minFilter = filter.toFila() }

    actual fun getMagFilter(): MagFilter = MagFilter.values()[nativeSampler.magFilter.ordinal]
    actual fun setMagFilter(filter: MagFilter) { nativeSampler.magFilter = filter.toFila() }

    actual fun getWrapModeS(): WrapMode = WrapMode.values()[nativeSampler.wrapModeS.ordinal]
    actual fun setWrapModeS(mode: WrapMode) { nativeSampler.wrapModeS = mode.toFila() }

    actual fun getWrapModeT(): WrapMode = WrapMode.values()[nativeSampler.wrapModeT.ordinal]
    actual fun setWrapModeT(mode: WrapMode) { nativeSampler.wrapModeT = mode.toFila() }

    actual fun getWrapModeR(): WrapMode = WrapMode.values()[nativeSampler.wrapModeR.ordinal]
    actual fun setWrapModeR(mode: WrapMode) { nativeSampler.wrapModeR = mode.toFila() }

    actual fun getAnisotropy(): Float = nativeSampler.anisotropy
    actual fun setAnisotropy(anisotropy: Float) { nativeSampler.anisotropy = anisotropy }

    actual fun getCompareMode(): CompareMode = CompareMode.values()[nativeSampler.compareMode.ordinal]
    actual fun setCompareMode(mode: CompareMode) { nativeSampler.compareMode = mode.toFila() }

    actual fun getCompareFunction(): CompareFunction = CompareFunction.values()[nativeSampler.compareFunction.ordinal]
    actual fun setCompareFunction(function: CompareFunction) { nativeSampler.compareFunction = function.toFila() }

    private fun WrapMode.toFila() = FilaTextureSampler.WrapMode.values()[this.ordinal]
    private fun MinFilter.toFila() = FilaTextureSampler.MinFilter.values()[this.ordinal]
    private fun MagFilter.toFila() = FilaTextureSampler.MagFilter.values()[this.ordinal]
    private fun CompareMode.toFila() = FilaTextureSampler.CompareMode.values()[this.ordinal]
    private fun CompareFunction.toFila() = FilaTextureSampler.CompareFunction.values()[this.ordinal]
}
