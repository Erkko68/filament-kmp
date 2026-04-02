package dev.filament.kmp

actual class TextureSampler {
    actual constructor() {
    }

    actual constructor(minMag: MagFilter) {
    }

    actual constructor(minMag: MagFilter, wrap: WrapMode) {
    }

    actual constructor(min: MinFilter, mag: MagFilter, wrap: WrapMode) {
    }

    actual constructor(min: MinFilter, mag: MagFilter, s: WrapMode, t: WrapMode, r: WrapMode) {
    }

    actual constructor(mode: CompareMode) {
    }

    actual constructor(mode: CompareMode, function: CompareFunction) {
    }

    actual fun getMinFilter(): MinFilter = TODO("Not yet implemented")

    actual fun setMinFilter(filter: MinFilter) {
        TODO("Not yet implemented")
    }

    actual fun getMagFilter(): MagFilter = TODO("Not yet implemented")

    actual fun setMagFilter(filter: MagFilter) {
        TODO("Not yet implemented")
    }

    actual fun getWrapModeS(): WrapMode = TODO("Not yet implemented")

    actual fun setWrapModeS(mode: WrapMode) {
        TODO("Not yet implemented")
    }

    actual fun getWrapModeT(): WrapMode = TODO("Not yet implemented")

    actual fun setWrapModeT(mode: WrapMode) {
        TODO("Not yet implemented")
    }

    actual fun getWrapModeR(): WrapMode = TODO("Not yet implemented")

    actual fun setWrapModeR(mode: WrapMode) {
        TODO("Not yet implemented")
    }

    actual fun getAnisotropy(): Float = TODO("Not yet implemented")

    actual fun setAnisotropy(anisotropy: Float) {
        TODO("Not yet implemented")
    }

    actual fun getCompareMode(): CompareMode = TODO("Not yet implemented")

    actual fun setCompareMode(mode: CompareMode) {
        TODO("Not yet implemented")
    }

    actual fun getCompareFunction(): CompareFunction = TODO("Not yet implemented")

    actual fun setCompareFunction(function: CompareFunction) {
        TODO("Not yet implemented")
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

