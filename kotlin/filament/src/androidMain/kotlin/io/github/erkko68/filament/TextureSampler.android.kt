package io.github.erkko68.filament

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

    actual var minFilter: MinFilter
        get() = MinFilter.entries[nativeSampler.minFilter.ordinal]
        set(value) { nativeSampler.minFilter = value.toFila() }

    actual var magFilter: MagFilter
        get() = MagFilter.entries[nativeSampler.magFilter.ordinal]
        set(value) { nativeSampler.magFilter = value.toFila() }

    actual var wrapModeS: WrapMode
        get() = WrapMode.entries[nativeSampler.wrapModeS.ordinal]
        set(value) { nativeSampler.wrapModeS = value.toFila() }

    actual var wrapModeT: WrapMode
        get() = WrapMode.entries[nativeSampler.wrapModeT.ordinal]
        set(value) { nativeSampler.wrapModeT = value.toFila() }

    actual var wrapModeR: WrapMode
        get() = WrapMode.entries[nativeSampler.wrapModeR.ordinal]
        set(value) { nativeSampler.wrapModeR = value.toFila() }

    actual var anisotropy: Float
        get() = nativeSampler.anisotropy
        set(value) { nativeSampler.anisotropy = value }

    actual var compareMode: CompareMode
        get() = CompareMode.entries[nativeSampler.compareMode.ordinal]
        set(value) { nativeSampler.compareMode = value.toFila() }

    actual var compareFunction: CompareFunction
        get() = CompareFunction.entries[nativeSampler.compareFunction.ordinal]
        set(value) { nativeSampler.compareFunction = value.toFila() }

    private fun WrapMode.toFila() = FilaTextureSampler.WrapMode.entries[this.ordinal]
    private fun MinFilter.toFila() = FilaTextureSampler.MinFilter.entries[this.ordinal]
    private fun MagFilter.toFila() = FilaTextureSampler.MagFilter.entries[this.ordinal]
    private fun CompareMode.toFila() = FilaTextureSampler.CompareMode.entries[this.ordinal]
    private fun CompareFunction.toFila() = FilaTextureSampler.CompareFunction.entries[this.ordinal]
}
