package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
import io.github.erkko68.filament.interop.*
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log2

/**
 * TextureSampler defines how a texture is accessed during rendering.
 *
 * It specifies filtering modes, wrapping modes, and optional comparison modes for texture sampling.
 * MaterialInstance copies it when binding a texture, so later changes don't affect bound textures.
 */
class TextureSampler private constructor(
    minFilter: MinFilter,
    magFilter: MagFilter,
    wrapModeS: WrapMode,
    wrapModeT: WrapMode,
    wrapModeR: WrapMode,
    compareMode: CompareMode,
    compareFunc: CompareFunc,
) {
    /**
     * Texture wrapping mode for texture coordinates outside the [0..1] range.
     */
    enum class WrapMode {
        /** Clamp texture coordinates to [0..1]. Coordinates outside this range sample the edge. */
        CLAMP_TO_EDGE,
        /** Repeat the texture (tile it). */
        REPEAT,
        /** Repeat the texture in a mirrored pattern. */
        MIRRORED_REPEAT
    }

    /**
     * Minification filter specifies how texels are filtered when the texture is sampled
     * at a size smaller than its original resolution.
     */
    enum class MinFilter {
        /** Use the nearest texel. Fastest but lower quality. */
        NEAREST,
        /** Linearly interpolate between four nearest texels. */
        LINEAR,
        /** Use nearest mipmap level, nearest texel. */
        NEAREST_MIPMAP_NEAREST,
        /** Use nearest mipmap level, interpolate between texels. */
        LINEAR_MIPMAP_NEAREST,
        /** Interpolate between mipmap levels, use nearest texel. */
        NEAREST_MIPMAP_LINEAR,
        /** Interpolate between mipmap levels and texels (trilinear filtering). */
        LINEAR_MIPMAP_LINEAR,
    }

    /**
     * Magnification filter specifies how texels are filtered when the texture is sampled
     * at a size larger than its original resolution.
     */
    enum class MagFilter {
        /** Use the nearest texel. */
        NEAREST,
        /** Linearly interpolate between four nearest texels. */
        LINEAR
    }

    /**
     * Compare mode for shadow map sampling and similar depth comparisons.
     */
    enum class CompareMode {
        /** No comparison is performed. */
        NONE,
        /** Perform a depth comparison and return the result. */
        COMPARE_TO_TEXTURE
    }

    /**
     * Comparison function for depth and stencil tests, and for COMPARE_TO_TEXTURE sampling.
     */
    enum class CompareFunc {
        /** Less or equal. */
        LE,
        /** Greater or equal. */
        GE,
        /** Strictly less than. */
        L,
        /** Strictly greater than. */
        G,
        /** Equal. */
        E,
        /** Not equal. */
        NE,
        /** Always. Depth / stencil testing is deactivated. */
        A,
        /** Never. The depth / stencil test always fails. */
        N
    }

    /**
     * Creates a TextureSampler with the default parameters: NEAREST filtering, CLAMP_TO_EDGE on all axes,
     * anisotropy 1, no comparison.
     */
    constructor() : this(MinFilter.NEAREST, MagFilter.NEAREST, WrapMode.CLAMP_TO_EDGE)

    /**
     * Creates a TextureSampler with the same minification and magnification filter and one wrap mode for all axes.
     *
     * @param minMag Filtering for both minification and magnification.
     * @param str Wrapping mode applied to all three axes (S, T, R).
     */
    constructor(minMag: MagFilter, str: WrapMode = WrapMode.CLAMP_TO_EDGE) :
        this(if (minMag == MagFilter.NEAREST) MinFilter.NEAREST else MinFilter.LINEAR, minMag, str)

    /**
     * Creates a TextureSampler with separate minification and magnification filters,
     * and a wrap mode applied to all axes.
     *
     * @param min Minification filter.
     * @param mag Magnification filter.
     * @param str Wrapping mode applied to all three axes (S, T, R).
     */
    constructor(min: MinFilter, mag: MagFilter, str: WrapMode = WrapMode.CLAMP_TO_EDGE) : this(min, mag, str, str, str)

    /**
     * Creates a TextureSampler with separate filters and wrap modes for each axis.
     *
     * @param min Minification filter.
     * @param mag Magnification filter.
     * @param s Wrap mode for the S (horizontal) texture coordinate.
     * @param t Wrap mode for the T (vertical) texture coordinate.
     * @param r Wrap mode for the R (depth) texture coordinate.
     */
    constructor(min: MinFilter, mag: MagFilter, s: WrapMode, t: WrapMode, r: WrapMode) : this(min, mag, s, t, r, CompareMode.NONE, CompareFunc.LE)

    /**
     * Creates a TextureSampler with default filtering and wrapping, and the given comparison mode.
     *
     * @param mode Compare mode to use.
     * @param func Comparison function.
     */
    constructor(mode: CompareMode, func: CompareFunc = CompareFunc.LE) :
        this(MinFilter.NEAREST, MagFilter.NEAREST, WrapMode.CLAMP_TO_EDGE, WrapMode.CLAMP_TO_EDGE, WrapMode.CLAMP_TO_EDGE, mode, func)

    /** Minification filter. */
    var minFilter: MinFilter = minFilter

    /** Magnification filter. */
    var magFilter: MagFilter = magFilter

    /** Wrap mode for S (horizontal) texture coordinate. */
    var wrapModeS: WrapMode = wrapModeS

    /** Wrap mode for T (vertical) texture coordinate. */
    var wrapModeT: WrapMode = wrapModeT

    /** Wrap mode for R (depth) texture coordinate. */
    var wrapModeR: WrapMode = wrapModeR

    /**
     * Anisotropic filtering amount. Should be a power-of-two. Default is 1.0 (disabled).
     * The maximum permissible value is 128.
     */
    var anisotropy: Float
        get() = (1 shl anisotropyLog2).toFloat()
        set(value) {
            // As TextureSampler::setAnisotropy: ilogb(|value|) clamped to 7, stored in a 3-bit field.
            val log2 = if (value == 0f) Int.MIN_VALUE else floor(log2(abs(value))).toInt()
            anisotropyLog2 = (if (log2 < 7) log2 else 7) and 7
        }

    private var anisotropyLog2 = 0

    /** Comparison mode. */
    var compareMode: CompareMode = compareMode
        private set

    /** Comparison function for depth comparisons. */
    var compareFunc: CompareFunc = compareFunc
        private set

    /**
     * Sets the comparison mode and function.
     *
     * @param mode Compare mode to use.
     * @param func Comparison function.
     */
    fun setCompareMode(mode: CompareMode, func: CompareFunc = CompareFunc.LE) {
        compareMode = mode
        compareFunc = func
    }
}

/** A native TextureSampler with this one's settings, for the duration of [block]. */
internal inline fun <T> TextureSampler.useNative(block: (NativePointer) -> T): T {
    val s = FilaTextureSampler_create_MinFilter_MagFilter_WrapMode_WrapMode_WrapMode(
        minFilter.ordinal, magFilter.ordinal, wrapModeS.ordinal, wrapModeT.ordinal, wrapModeR.ordinal,
    )
    try {
        FilaTextureSampler_setAnisotropy(s, anisotropy)
        FilaTextureSampler_setCompareMode(s, compareMode.ordinal, compareFunc.ordinal)
        return block(s)
    } finally {
        FilaTextureSampler_destroy(s)
    }
}
