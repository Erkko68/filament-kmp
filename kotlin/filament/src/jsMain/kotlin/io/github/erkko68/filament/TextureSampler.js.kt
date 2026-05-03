package io.github.erkko68.filament

import io.github.erkko68.filament.js.TextureSampler as JSTextureSampler
import io.github.erkko68.filament.js.MinFilter as JSMinFilter
import io.github.erkko68.filament.js.MagFilter as JSMagFilter
import io.github.erkko68.filament.js.WrapMode as JSWrapMode
import io.github.erkko68.filament.js.CompareMode as JSCompareMode
import io.github.erkko68.filament.js.CompareFunc as JSCompareFunc

actual class TextureSampler {
    actual var minFilter: MinFilter = MinFilter.LINEAR
    actual var magFilter: MagFilter = MagFilter.LINEAR
    actual var wrapModeS: WrapMode = WrapMode.CLAMP_TO_EDGE
    actual var wrapModeT: WrapMode = WrapMode.CLAMP_TO_EDGE
    actual var wrapModeR: WrapMode = WrapMode.CLAMP_TO_EDGE
    actual var anisotropy: Float = 0f
    actual var compareMode: CompareMode = CompareMode.NONE
    actual var compareFunction: CompareFunction = CompareFunction.LESS_EQUAL

    internal val jsTextureSampler: JSTextureSampler
        get() = buildJsSampler()

    private fun mapMinFilter(f: MinFilter) = when (f) {
        MinFilter.NEAREST -> JSMinFilter.NEAREST
        MinFilter.LINEAR -> JSMinFilter.LINEAR
        MinFilter.NEAREST_MIPMAP_NEAREST -> JSMinFilter.NEAREST_MIPMAP_NEAREST
        MinFilter.LINEAR_MIPMAP_NEAREST -> JSMinFilter.LINEAR_MIPMAP_NEAREST
        MinFilter.NEAREST_MIPMAP_LINEAR -> JSMinFilter.NEAREST_MIPMAP_LINEAR
        MinFilter.LINEAR_MIPMAP_LINEAR -> JSMinFilter.LINEAR_MIPMAP_LINEAR
    }

    private fun mapMagFilter(f: MagFilter) = when (f) {
        MagFilter.NEAREST -> JSMagFilter.NEAREST
        MagFilter.LINEAR -> JSMagFilter.LINEAR
    }

    private fun mapWrapMode(m: WrapMode) = when (m) {
        WrapMode.CLAMP_TO_EDGE -> JSWrapMode.CLAMP_TO_EDGE
        WrapMode.REPEAT -> JSWrapMode.REPEAT
        WrapMode.MIRRORED_REPEAT -> JSWrapMode.MIRRORED_REPEAT
    }

    private fun mapCompareFunc(f: CompareFunction) = when (f) {
        CompareFunction.LESS_EQUAL -> JSCompareFunc.LESS_EQUAL
        CompareFunction.GREATER_EQUAL -> JSCompareFunc.GREATER_EQUAL
        CompareFunction.LESS -> JSCompareFunc.LESS
        CompareFunction.GREATER -> JSCompareFunc.GREATER
        CompareFunction.EQUAL -> JSCompareFunc.EQUAL
        CompareFunction.NOT_EQUAL -> JSCompareFunc.NOT_EQUAL
        CompareFunction.ALWAYS -> JSCompareFunc.ALWAYS
        CompareFunction.NEVER -> JSCompareFunc.NEVER
    }

    private fun mapCompareMode(m: CompareMode) = when (m) {
        CompareMode.NONE -> JSCompareMode.NONE
        CompareMode.COMPARE_TO_TEXTURE -> JSCompareMode.COMPARE_TO_TEXTURE
    }

    // JS TextureSampler requires all 3 args; we always build from current state
    private fun buildJsSampler(): JSTextureSampler {
        val minJs = mapMinFilter(minFilter)
        val magJs = mapMagFilter(magFilter)
        val wrapJs = mapWrapMode(wrapModeS)
        val ts = JSTextureSampler(minJs.unsafeCast<JSMinFilter>(), magJs, wrapJs)
        ts.setAnisotropy(anisotropy)
        if (compareMode != CompareMode.NONE) {
            ts.setCompareMode(mapCompareMode(compareMode), mapCompareFunc(compareFunction))
        }
        return ts
    }

    actual constructor()

    actual constructor(minMag: MagFilter) {
        magFilter = minMag
        minFilter = when (minMag) {
            MagFilter.NEAREST -> MinFilter.NEAREST
            MagFilter.LINEAR -> MinFilter.LINEAR
        }
    }

    actual constructor(minMag: MagFilter, wrap: WrapMode) {
        magFilter = minMag
        minFilter = when (minMag) {
            MagFilter.NEAREST -> MinFilter.NEAREST
            MagFilter.LINEAR -> MinFilter.LINEAR
        }
        wrapModeS = wrap
        wrapModeT = wrap
        wrapModeR = wrap
    }

    actual constructor(min: MinFilter, mag: MagFilter, wrap: WrapMode) {
        minFilter = min
        magFilter = mag
        wrapModeS = wrap
        wrapModeT = wrap
        wrapModeR = wrap
    }

    actual constructor(min: MinFilter, mag: MagFilter, s: WrapMode, t: WrapMode, r: WrapMode) {
        minFilter = min
        magFilter = mag
        wrapModeS = s
        wrapModeT = t
        wrapModeR = r
    }

    actual constructor(mode: CompareMode) {
        compareMode = mode
    }

    actual constructor(mode: CompareMode, function: CompareFunction) {
        compareMode = mode
        compareFunction = function
    }

    actual enum class WrapMode { CLAMP_TO_EDGE, REPEAT, MIRRORED_REPEAT }
    actual enum class MinFilter { NEAREST, LINEAR, NEAREST_MIPMAP_NEAREST, LINEAR_MIPMAP_NEAREST, NEAREST_MIPMAP_LINEAR, LINEAR_MIPMAP_LINEAR }
    actual enum class MagFilter { NEAREST, LINEAR }
    actual enum class CompareMode { NONE, COMPARE_TO_TEXTURE }
    actual enum class CompareFunction { LESS_EQUAL, GREATER_EQUAL, LESS, GREATER, EQUAL, NOT_EQUAL, ALWAYS, NEVER }
}
