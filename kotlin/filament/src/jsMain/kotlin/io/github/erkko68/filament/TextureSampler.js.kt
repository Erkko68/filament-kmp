package io.github.erkko68.filament

import io.github.erkko68.filament.js.TextureSampler as JSTextureSampler
import io.github.erkko68.filament.js.MinFilter as JSMinFilter
import io.github.erkko68.filament.js.MagFilter as JSMagFilter
import io.github.erkko68.filament.js.WrapMode as JSWrapMode
import io.github.erkko68.filament.js.CompareMode as JSCompareMode
import io.github.erkko68.filament.js.CompareFunc as JSCompareFunc

actual class TextureSampler {
    private var _minFilter: MinFilter = MinFilter.LINEAR
    private var _magFilter: MagFilter = MagFilter.LINEAR
    private var _wrapModeS: WrapMode = WrapMode.CLAMP_TO_EDGE
    private var _wrapModeT: WrapMode = WrapMode.CLAMP_TO_EDGE
    private var _wrapModeR: WrapMode = WrapMode.CLAMP_TO_EDGE
    private var _anisotropy: Float = 0f
    private var _compareMode: CompareMode = CompareMode.NONE
    private var _compareFunction: CompareFunction = CompareFunction.LESS_EQUAL

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
        // Use MinFilter mapped to JSMinFilter via dynamic casting workaround
        val minJs = mapMinFilter(_minFilter)
        val magJs = mapMagFilter(_magFilter)
        val wrapJs = mapWrapMode(_wrapModeS)
        val ts = JSTextureSampler(minJs.unsafeCast<JSMinFilter>(), magJs, wrapJs)
        ts.setAnisotropy(_anisotropy)
        if (_compareMode != CompareMode.NONE) {
            ts.setCompareMode(mapCompareMode(_compareMode), mapCompareFunc(_compareFunction))
        }
        return ts
    }

    actual constructor() {
        _minFilter = MinFilter.LINEAR
        _magFilter = MagFilter.LINEAR
        _wrapModeS = WrapMode.CLAMP_TO_EDGE
    }

    actual constructor(minMag: MagFilter) {
        _magFilter = minMag
        _minFilter = when (minMag) {
            MagFilter.NEAREST -> MinFilter.NEAREST
            MagFilter.LINEAR -> MinFilter.LINEAR
        }
    }

    actual constructor(minMag: MagFilter, wrap: WrapMode) {
        _magFilter = minMag
        _minFilter = when (minMag) {
            MagFilter.NEAREST -> MinFilter.NEAREST
            MagFilter.LINEAR -> MinFilter.LINEAR
        }
        _wrapModeS = wrap
        _wrapModeT = wrap
        _wrapModeR = wrap
    }

    actual constructor(min: MinFilter, mag: MagFilter, wrap: WrapMode) {
        _minFilter = min
        _magFilter = mag
        _wrapModeS = wrap
        _wrapModeT = wrap
        _wrapModeR = wrap
    }

    actual constructor(min: MinFilter, mag: MagFilter, s: WrapMode, t: WrapMode, r: WrapMode) {
        _minFilter = min
        _magFilter = mag
        _wrapModeS = s
        _wrapModeT = t
        _wrapModeR = r
    }

    actual constructor(mode: CompareMode) {
        _compareMode = mode
    }

    actual constructor(mode: CompareMode, function: CompareFunction) {
        _compareMode = mode
        _compareFunction = function
    }

    actual fun getMinFilter(): MinFilter = _minFilter
    actual fun setMinFilter(filter: MinFilter) { _minFilter = filter }
    actual fun getMagFilter(): MagFilter = _magFilter
    actual fun setMagFilter(filter: MagFilter) { _magFilter = filter }
    actual fun getWrapModeS(): WrapMode = _wrapModeS
    actual fun setWrapModeS(mode: WrapMode) { _wrapModeS = mode }
    actual fun getWrapModeT(): WrapMode = _wrapModeT
    actual fun setWrapModeT(mode: WrapMode) { _wrapModeT = mode }
    actual fun getWrapModeR(): WrapMode = _wrapModeR
    actual fun setWrapModeR(mode: WrapMode) { _wrapModeR = mode }
    actual fun getAnisotropy(): Float = _anisotropy
    actual fun setAnisotropy(anisotropy: Float) { _anisotropy = anisotropy }
    actual fun getCompareMode(): CompareMode = _compareMode
    actual fun setCompareMode(mode: CompareMode) { _compareMode = mode }
    actual fun getCompareFunction(): CompareFunction = _compareFunction
    actual fun setCompareFunction(function: CompareFunction) { _compareFunction = function }

    actual enum class WrapMode { CLAMP_TO_EDGE, REPEAT, MIRRORED_REPEAT }
    actual enum class MinFilter { NEAREST, LINEAR, NEAREST_MIPMAP_NEAREST, LINEAR_MIPMAP_NEAREST, NEAREST_MIPMAP_LINEAR, LINEAR_MIPMAP_LINEAR }
    actual enum class MagFilter { NEAREST, LINEAR }
    actual enum class CompareMode { NONE, COMPARE_TO_TEXTURE }
    actual enum class CompareFunction { LESS_EQUAL, GREATER_EQUAL, LESS, GREATER, EQUAL, NOT_EQUAL, ALWAYS, NEVER }
}
