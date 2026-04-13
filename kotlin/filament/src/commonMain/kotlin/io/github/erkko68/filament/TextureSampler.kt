package io.github.erkko68.filament

expect class TextureSampler {
    enum class WrapMode {
        CLAMP_TO_EDGE,
        REPEAT,
        MIRRORED_REPEAT
    }

    enum class MinFilter {
        NEAREST,
        LINEAR,
        NEAREST_MIPMAP_NEAREST,
        LINEAR_MIPMAP_NEAREST,
        NEAREST_MIPMAP_LINEAR,
        LINEAR_MIPMAP_LINEAR,
    }

    enum class MagFilter {
        NEAREST,
        LINEAR
    }

    enum class CompareMode {
        NONE,
        COMPARE_TO_TEXTURE
    }

    enum class CompareFunction {
        LESS_EQUAL,
        GREATER_EQUAL,
        LESS,
        GREATER,
        EQUAL,
        NOT_EQUAL,
        ALWAYS,
        NEVER
    }

    constructor()
    constructor(minMag: MagFilter)
    constructor(minMag: MagFilter, wrap: WrapMode)
    constructor(min: MinFilter, mag: MagFilter, wrap: WrapMode)
    constructor(min: MinFilter, mag: MagFilter, s: WrapMode, t: WrapMode, r: WrapMode)
    constructor(mode: CompareMode)
    constructor(mode: CompareMode, function: CompareFunction)

    fun getMinFilter(): MinFilter
    fun setMinFilter(filter: MinFilter)
    fun getMagFilter(): MagFilter
    fun setMagFilter(filter: MagFilter)
    fun getWrapModeS(): WrapMode
    fun setWrapModeS(mode: WrapMode)
    fun getWrapModeT(): WrapMode
    fun setWrapModeT(mode: WrapMode)
    fun getWrapModeR(): WrapMode
    fun setWrapModeR(mode: WrapMode)
    fun getAnisotropy(): Float
    fun setAnisotropy(anisotropy: Float)
    fun getCompareMode(): CompareMode
    fun setCompareMode(mode: CompareMode)
    fun getCompareFunction(): CompareFunction
    fun setCompareFunction(function: CompareFunction)
}
