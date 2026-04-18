package io.github.erkko68.filament

import io.github.erkko68.filament.js.TextureSampler as JSTextureSampler
import io.github.erkko68.filament.js.MinFilter as JSMinFilter
import io.github.erkko68.filament.js.MagFilter as JSMagFilter
import io.github.erkko68.filament.js.WrapMode as JSWrapMode
import io.github.erkko68.filament.js.CompareMode as JSCompareMode
import io.github.erkko68.filament.js.CompareFunction as JSCompareFunction

actual class TextureSampler {
    internal val jsTextureSampler: JSTextureSampler

    actual fun getMinFilter(): MinFilter {
        // Mapping back from JS enum if needed, or store it
        return MinFilter.LINEAR
    }

    actual fun setMinFilter(filter: MinFilter) {
        val jsFilter = when (filter) {
            MinFilter.NEAREST -> JSMinFilter.NEAREST
            MinFilter.LINEAR -> JSMinFilter.LINEAR
            MinFilter.NEAREST_MIPMAP_NEAREST -> JSMinFilter.NEAREST_MIPMAP_NEAREST
            MinFilter.LINEAR_MIPMAP_NEAREST -> JSMinFilter.LINEAR_MIPMAP_NEAREST
            MinFilter.NEAREST_MIPMAP_LINEAR -> JSMinFilter.NEAREST_MIPMAP_LINEAR
            MinFilter.LINEAR_MIPMAP_LINEAR -> JSMinFilter.LINEAR_MIPMAP_LINEAR
        }
        jsTextureSampler.setMinFilter(jsFilter)
    }

    actual fun getMagFilter(): MagFilter {
        return MagFilter.LINEAR
    }

    actual fun setMagFilter(filter: MagFilter) {
        val jsFilter = when (filter) {
            MagFilter.NEAREST -> JSMagFilter.NEAREST
            MagFilter.LINEAR -> JSMagFilter.LINEAR
        }
        jsTextureSampler.setMagFilter(jsFilter)
    }

    actual fun getWrapModeS(): WrapMode {
        return WrapMode.CLAMP_TO_EDGE
    }

    actual fun setWrapModeS(mode: WrapMode) {
        jsTextureSampler.setWrapModeS(when (mode) {
            WrapMode.CLAMP_TO_EDGE -> JSWrapMode.CLAMP_TO_EDGE
            WrapMode.REPEAT -> JSWrapMode.REPEAT
            WrapMode.MIRRORED_REPEAT -> JSWrapMode.MIRRORED_REPEAT
        })
    }

    actual fun getWrapModeT(): WrapMode {
        return WrapMode.CLAMP_TO_EDGE
    }

    actual fun setWrapModeT(mode: WrapMode) {
        jsTextureSampler.setWrapModeT(when (mode) {
            WrapMode.CLAMP_TO_EDGE -> JSWrapMode.CLAMP_TO_EDGE
            WrapMode.REPEAT -> JSWrapMode.REPEAT
            WrapMode.MIRRORED_REPEAT -> JSWrapMode.MIRRORED_REPEAT
        })
    }

    actual fun getWrapModeR(): WrapMode {
        return WrapMode.CLAMP_TO_EDGE
    }

    actual fun setWrapModeR(mode: WrapMode) {
        jsTextureSampler.setWrapModeR(when (mode) {
            WrapMode.CLAMP_TO_EDGE -> JSWrapMode.CLAMP_TO_EDGE
            WrapMode.REPEAT -> JSWrapMode.REPEAT
            WrapMode.MIRRORED_REPEAT -> JSWrapMode.MIRRORED_REPEAT
        })
    }

    actual fun getAnisotropy(): Float {
        return 0f
    }

    actual fun setAnisotropy(anisotropy: Float) {
        jsTextureSampler.setAnisotropy(anisotropy)
    }

    actual fun getCompareMode(): CompareMode {
        return CompareMode.NONE
    }

    actual fun setCompareMode(mode: CompareMode) {
        jsTextureSampler.setCompareMode(when (mode) {
            CompareMode.NONE -> JSCompareMode.NONE
            CompareMode.COMPARE_TO_TEXTURE -> JSCompareMode.COMPARE_TO_TEXTURE
        })
    }

    actual fun getCompareFunction(): CompareFunction {
        return CompareFunction.LESS_EQUAL
    }

    actual fun setCompareFunction(function: CompareFunction) {
        jsTextureSampler.setCompareFunction(when (function) {
            CompareFunction.LESS_EQUAL -> JSCompareFunction.LESS_EQUAL
            CompareFunction.GREATER_EQUAL -> JSCompareFunction.GREATER_EQUAL
            CompareFunction.LESS -> JSCompareFunction.LESS
            CompareFunction.GREATER -> JSCompareFunction.GREATER
            CompareFunction.EQUAL -> JSCompareFunction.EQUAL
            CompareFunction.NOT_EQUAL -> JSCompareFunction.NOT_EQUAL
            CompareFunction.ALWAYS -> JSCompareFunction.ALWAYS
            CompareFunction.NEVER -> JSCompareFunction.NEVER
        })
    }

    actual enum class WrapMode { CLAMP_TO_EDGE, REPEAT, MIRRORED_REPEAT }
    actual enum class MinFilter { NEAREST, LINEAR, NEAREST_MIPMAP_NEAREST, LINEAR_MIPMAP_NEAREST, NEAREST_MIPMAP_LINEAR, LINEAR_MIPMAP_LINEAR }
    actual enum class MagFilter { NEAREST, LINEAR }
    actual enum class CompareMode { NONE, COMPARE_TO_TEXTURE }
    actual enum class CompareFunction { LESS_EQUAL, GREATER_EQUAL, LESS, GREATER, EQUAL, NOT_EQUAL, ALWAYS, NEVER }

    actual constructor() {
        jsTextureSampler = JSTextureSampler()
    }

    actual constructor(minMag: MagFilter) {
        jsTextureSampler = JSTextureSampler(when (minMag) {
            MagFilter.NEAREST -> JSMagFilter.NEAREST
            MagFilter.LINEAR -> JSMagFilter.LINEAR
        })
    }

    actual constructor(
        minMag: MagFilter,
        wrap: WrapMode
    ) {
        jsTextureSampler = JSTextureSampler(
            when (minMag) {
                MagFilter.NEAREST -> JSMagFilter.NEAREST
                MagFilter.LINEAR -> JSMagFilter.LINEAR
            },
            when (wrap) {
                WrapMode.CLAMP_TO_EDGE -> JSWrapMode.CLAMP_TO_EDGE
                WrapMode.REPEAT -> JSWrapMode.REPEAT
                WrapMode.MIRRORED_REPEAT -> JSWrapMode.MIRRORED_REPEAT
            }
        )
    }

    actual constructor(
        min: MinFilter,
        mag: MagFilter,
        wrap: WrapMode
    ) {
        jsTextureSampler = JSTextureSampler(
            when (min) {
                MinFilter.NEAREST -> JSMinFilter.NEAREST
                MinFilter.LINEAR -> JSMinFilter.LINEAR
                MinFilter.NEAREST_MIPMAP_NEAREST -> JSMinFilter.NEAREST_MIPMAP_NEAREST
                MinFilter.LINEAR_MIPMAP_NEAREST -> JSMinFilter.LINEAR_MIPMAP_NEAREST
                MinFilter.NEAREST_MIPMAP_LINEAR -> JSMinFilter.NEAREST_MIPMAP_LINEAR
                MinFilter.LINEAR_MIPMAP_LINEAR -> JSMinFilter.LINEAR_MIPMAP_LINEAR
            },
            when (mag) {
                MagFilter.NEAREST -> JSMagFilter.NEAREST
                MagFilter.LINEAR -> JSMagFilter.LINEAR
            },
            when (wrap) {
                WrapMode.CLAMP_TO_EDGE -> JSWrapMode.CLAMP_TO_EDGE
                WrapMode.REPEAT -> JSWrapMode.REPEAT
                WrapMode.MIRRORED_REPEAT -> JSWrapMode.MIRRORED_REPEAT
            }
        )
    }

    actual constructor(
        min: MinFilter,
        mag: MagFilter,
        s: WrapMode,
        t: WrapMode,
        r: WrapMode
    ) : this(min, mag, s) {
        setWrapModeT(t)
        setWrapModeR(r)
    }

    actual constructor(mode: CompareMode) {
        jsTextureSampler = JSTextureSampler(when (mode) {
            CompareMode.NONE -> JSCompareMode.NONE
            CompareMode.COMPARE_TO_TEXTURE -> JSCompareMode.COMPARE_TO_TEXTURE
        })
    }

    actual constructor(
        mode: CompareMode,
        function: CompareFunction
    ) {
        jsTextureSampler = JSTextureSampler(
            when (mode) {
                CompareMode.NONE -> JSCompareMode.NONE
                CompareMode.COMPARE_TO_TEXTURE -> JSCompareMode.COMPARE_TO_TEXTURE
            },
            when (function) {
                CompareFunction.LESS_EQUAL -> JSCompareFunction.LESS_EQUAL
                CompareFunction.GREATER_EQUAL -> JSCompareFunction.GREATER_EQUAL
                CompareFunction.LESS -> JSCompareFunction.LESS
                CompareFunction.GREATER -> JSCompareFunction.GREATER
                CompareFunction.EQUAL -> JSCompareFunction.EQUAL
                CompareFunction.NOT_EQUAL -> JSCompareFunction.NOT_EQUAL
                CompareFunction.ALWAYS -> JSCompareFunction.ALWAYS
                CompareFunction.NEVER -> JSCompareFunction.NEVER
            }
        )
    }
}