package dev.filament.kmp

import com.google.android.filament.TextureSampler as AndroidTextureSampler

actual class TextureSampler {
    internal val androidSampler: AndroidTextureSampler

    actual constructor() {
        androidSampler = AndroidTextureSampler()
    }

    actual constructor(minMag: MagFilter) {
        androidSampler = AndroidTextureSampler(minMag.toAndroid())
    }

    actual constructor(minMag: MagFilter, wrap: WrapMode) {
        androidSampler = AndroidTextureSampler(minMag.toAndroid(), wrap.toAndroid())
    }

    actual constructor(min: MinFilter, mag: MagFilter, wrap: WrapMode) {
        androidSampler = AndroidTextureSampler(min.toAndroid(), mag.toAndroid(), wrap.toAndroid())
    }

    actual constructor(min: MinFilter, mag: MagFilter, s: WrapMode, t: WrapMode, r: WrapMode) {
        androidSampler = AndroidTextureSampler(
            min.toAndroid(),
            mag.toAndroid(),
            s.toAndroid(),
            t.toAndroid(),
            r.toAndroid(),
        )
    }

    actual constructor(mode: CompareMode) {
        androidSampler = AndroidTextureSampler(mode.toAndroid())
    }

    actual constructor(mode: CompareMode, function: CompareFunction) {
        androidSampler = AndroidTextureSampler(mode.toAndroid(), function.toAndroid())
    }

    actual val minFilter: MinFilter
        get() = androidSampler.minFilter.toKmp()

    actual fun setMinFilter(filter: MinFilter) {
        androidSampler.setMinFilter(filter.toAndroid())
    }

    actual val magFilter: MagFilter
        get() = androidSampler.magFilter.toKmp()

    actual fun setMagFilter(filter: MagFilter) {
        androidSampler.setMagFilter(filter.toAndroid())
    }

    actual val wrapModeS: WrapMode
        get() = androidSampler.wrapModeS.toKmp()

    actual fun setWrapModeS(mode: WrapMode) {
        androidSampler.setWrapModeS(mode.toAndroid())
    }

    actual val wrapModeT: WrapMode
        get() = androidSampler.wrapModeT.toKmp()

    actual fun setWrapModeT(mode: WrapMode) {
        androidSampler.setWrapModeT(mode.toAndroid())
    }

    actual val wrapModeR: WrapMode
        get() = androidSampler.wrapModeR.toKmp()

    actual fun setWrapModeR(mode: WrapMode) {
        androidSampler.setWrapModeR(mode.toAndroid())
    }

    actual val anisotropy: Float
        get() = androidSampler.anisotropy

    actual fun setAnisotropy(anisotropy: Float) {
        androidSampler.setAnisotropy(anisotropy)
    }

    actual val compareMode: CompareMode
        get() = androidSampler.compareMode.toKmp()

    actual fun setCompareMode(mode: CompareMode) {
        androidSampler.setCompareMode(mode.toAndroid())
    }

    actual val compareFunction: CompareFunction
        get() = androidSampler.compareFunction.toKmp()

    actual fun setCompareFunction(function: CompareFunction) {
        androidSampler.setCompareFunction(function.toAndroid())
    }

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
        LESS_EQUAL,
        GREATER_EQUAL,
        LESS,
        GREATER,
        EQUAL,
        NOT_EQUAL,
        ALWAYS,
        NEVER,
    }
}

private fun TextureSampler.WrapMode.toAndroid(): AndroidTextureSampler.WrapMode = when (this) {
    TextureSampler.WrapMode.CLAMP_TO_EDGE -> AndroidTextureSampler.WrapMode.CLAMP_TO_EDGE
    TextureSampler.WrapMode.REPEAT -> AndroidTextureSampler.WrapMode.REPEAT
    TextureSampler.WrapMode.MIRRORED_REPEAT -> AndroidTextureSampler.WrapMode.MIRRORED_REPEAT
}

private fun AndroidTextureSampler.WrapMode.toKmp(): TextureSampler.WrapMode = when (this) {
    AndroidTextureSampler.WrapMode.CLAMP_TO_EDGE -> TextureSampler.WrapMode.CLAMP_TO_EDGE
    AndroidTextureSampler.WrapMode.REPEAT -> TextureSampler.WrapMode.REPEAT
    AndroidTextureSampler.WrapMode.MIRRORED_REPEAT -> TextureSampler.WrapMode.MIRRORED_REPEAT
}

private fun TextureSampler.MinFilter.toAndroid(): AndroidTextureSampler.MinFilter = when (this) {
    TextureSampler.MinFilter.NEAREST -> AndroidTextureSampler.MinFilter.NEAREST
    TextureSampler.MinFilter.LINEAR -> AndroidTextureSampler.MinFilter.LINEAR
    TextureSampler.MinFilter.NEAREST_MIPMAP_NEAREST -> AndroidTextureSampler.MinFilter.NEAREST_MIPMAP_NEAREST
    TextureSampler.MinFilter.LINEAR_MIPMAP_NEAREST -> AndroidTextureSampler.MinFilter.LINEAR_MIPMAP_NEAREST
    TextureSampler.MinFilter.NEAREST_MIPMAP_LINEAR -> AndroidTextureSampler.MinFilter.NEAREST_MIPMAP_LINEAR
    TextureSampler.MinFilter.LINEAR_MIPMAP_LINEAR -> AndroidTextureSampler.MinFilter.LINEAR_MIPMAP_LINEAR
}

private fun AndroidTextureSampler.MinFilter.toKmp(): TextureSampler.MinFilter = when (this) {
    AndroidTextureSampler.MinFilter.NEAREST -> TextureSampler.MinFilter.NEAREST
    AndroidTextureSampler.MinFilter.LINEAR -> TextureSampler.MinFilter.LINEAR
    AndroidTextureSampler.MinFilter.NEAREST_MIPMAP_NEAREST -> TextureSampler.MinFilter.NEAREST_MIPMAP_NEAREST
    AndroidTextureSampler.MinFilter.LINEAR_MIPMAP_NEAREST -> TextureSampler.MinFilter.LINEAR_MIPMAP_NEAREST
    AndroidTextureSampler.MinFilter.NEAREST_MIPMAP_LINEAR -> TextureSampler.MinFilter.NEAREST_MIPMAP_LINEAR
    AndroidTextureSampler.MinFilter.LINEAR_MIPMAP_LINEAR -> TextureSampler.MinFilter.LINEAR_MIPMAP_LINEAR
}

private fun TextureSampler.MagFilter.toAndroid(): AndroidTextureSampler.MagFilter = when (this) {
    TextureSampler.MagFilter.NEAREST -> AndroidTextureSampler.MagFilter.NEAREST
    TextureSampler.MagFilter.LINEAR -> AndroidTextureSampler.MagFilter.LINEAR
}

private fun AndroidTextureSampler.MagFilter.toKmp(): TextureSampler.MagFilter = when (this) {
    AndroidTextureSampler.MagFilter.NEAREST -> TextureSampler.MagFilter.NEAREST
    AndroidTextureSampler.MagFilter.LINEAR -> TextureSampler.MagFilter.LINEAR
}

private fun TextureSampler.CompareMode.toAndroid(): AndroidTextureSampler.CompareMode = when (this) {
    TextureSampler.CompareMode.NONE -> AndroidTextureSampler.CompareMode.NONE
    TextureSampler.CompareMode.COMPARE_TO_TEXTURE -> AndroidTextureSampler.CompareMode.COMPARE_TO_TEXTURE
}

private fun AndroidTextureSampler.CompareMode.toKmp(): TextureSampler.CompareMode = when (this) {
    AndroidTextureSampler.CompareMode.NONE -> TextureSampler.CompareMode.NONE
    AndroidTextureSampler.CompareMode.COMPARE_TO_TEXTURE -> TextureSampler.CompareMode.COMPARE_TO_TEXTURE
}

private fun TextureSampler.CompareFunction.toAndroid(): AndroidTextureSampler.CompareFunction = when (this) {
    TextureSampler.CompareFunction.LESS_EQUAL -> AndroidTextureSampler.CompareFunction.LESS_EQUAL
    TextureSampler.CompareFunction.GREATER_EQUAL -> AndroidTextureSampler.CompareFunction.GREATER_EQUAL
    TextureSampler.CompareFunction.LESS -> AndroidTextureSampler.CompareFunction.LESS
    TextureSampler.CompareFunction.GREATER -> AndroidTextureSampler.CompareFunction.GREATER
    TextureSampler.CompareFunction.EQUAL -> AndroidTextureSampler.CompareFunction.EQUAL
    TextureSampler.CompareFunction.NOT_EQUAL -> AndroidTextureSampler.CompareFunction.NOT_EQUAL
    TextureSampler.CompareFunction.ALWAYS -> AndroidTextureSampler.CompareFunction.ALWAYS
    TextureSampler.CompareFunction.NEVER -> AndroidTextureSampler.CompareFunction.NEVER
}

private fun AndroidTextureSampler.CompareFunction.toKmp(): TextureSampler.CompareFunction = when (this) {
    AndroidTextureSampler.CompareFunction.LESS_EQUAL -> TextureSampler.CompareFunction.LESS_EQUAL
    AndroidTextureSampler.CompareFunction.GREATER_EQUAL -> TextureSampler.CompareFunction.GREATER_EQUAL
    AndroidTextureSampler.CompareFunction.LESS -> TextureSampler.CompareFunction.LESS
    AndroidTextureSampler.CompareFunction.GREATER -> TextureSampler.CompareFunction.GREATER
    AndroidTextureSampler.CompareFunction.EQUAL -> TextureSampler.CompareFunction.EQUAL
    AndroidTextureSampler.CompareFunction.NOT_EQUAL -> TextureSampler.CompareFunction.NOT_EQUAL
    AndroidTextureSampler.CompareFunction.ALWAYS -> TextureSampler.CompareFunction.ALWAYS
    AndroidTextureSampler.CompareFunction.NEVER -> TextureSampler.CompareFunction.NEVER
}

