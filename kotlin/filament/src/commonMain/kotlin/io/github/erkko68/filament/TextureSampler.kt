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

    var minFilter: MinFilter
    var magFilter: MagFilter
    var wrapModeS: WrapMode
    var wrapModeT: WrapMode
    var wrapModeR: WrapMode
    var anisotropy: Float
    var compareMode: CompareMode
    var compareFunction: CompareFunction
}
