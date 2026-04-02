package dev.filament.kmp

/**
 * <code>TextureSampler</code> defines how a texture is accessed.
 */
expect class TextureSampler {
    enum class WrapMode {
        /**
         * The edge of the texture extends to infinity.
         */
        CLAMP_TO_EDGE,

        /**
         * The texture infinitely repeats in the wrap direction.
         */
        REPEAT,

        /**
         * The texture infinitely repeats and mirrors in the wrap direction.
         */
        MIRRORED_REPEAT,
    }

    enum class MinFilter {
        /**
         * No filtering. Nearest neighbor is used.
         */
        NEAREST,

        /**
         * Box filtering. Weighted average of 4 neighbors is used.
         */
        LINEAR,

        /**
         * Mip-mapping is activated. But no filtering occurs.
         */
        NEAREST_MIPMAP_NEAREST,

        /**
         * Box filtering within a mip-map level.
         */
        LINEAR_MIPMAP_NEAREST,

        /**
         * Mip-map levels are interpolated, but no other filtering occurs.
         */
        NEAREST_MIPMAP_LINEAR,

        /**
         * Both interpolated Mip-mapping and linear filtering are used.
         */
        LINEAR_MIPMAP_LINEAR,
    }

    enum class MagFilter {
        /**
         * No filtering. Nearest neighbor is used.
         */
        NEAREST,

        /**
         * Box filtering. Weighted average of 4 neighbors is used.
         */
        LINEAR,
    }

    enum class CompareMode {
        NONE,
        COMPARE_TO_TEXTURE,
    }

    /**
     * Comparison functions for the depth sampler.
     */
    enum class CompareFunction {
        /**
         * Less or equal
         */
        LESS_EQUAL,

        /**
         * Greater or equal
         */
        GREATER_EQUAL,

        /**
         * Strictly less than
         */
        LESS,

        /**
         * Strictly greater than
         */
        GREATER,

        /**
         * Equal
         */
        EQUAL,

        /**
         * Not equal
         */
        NOT_EQUAL,

        /**
         * Always. Depth testing is deactivated.
         */
        ALWAYS,

        /**
         * Never. The depth test always fails.
         */
        NEVER,
    }

    /**
     * Initializes the <code>TextureSampler</code> with default values.
     * <br>Minification filter: {@link MinFilter#LINEAR_MIPMAP_LINEAR}
     * <br>Magnification filter: {@link MagFilter#LINEAR}
     * <br>Wrap modes: {@link WrapMode#REPEAT}
     */
    constructor()

    /**
     * Initializes the <code>TextureSampler</code> with default values, but specifying the
     * minification and magnification filters.
     */
    constructor(minMag: MagFilter)

    /**
     * Initializes the <code>TextureSampler</code> with user specified values.
     */
    constructor(minMag: MagFilter, wrap: WrapMode)

    /**
     * Initializes the <code>TextureSampler</code> with user specified values.
     */
    constructor(min: MinFilter, mag: MagFilter, wrap: WrapMode)

    /**
     * Initializes the <code>TextureSampler</code> with user specified values.
     */
    constructor(min: MinFilter, mag: MagFilter, s: WrapMode, t: WrapMode, r: WrapMode)

    /**
     * Initializes the <code>TextureSampler</code> with user specified comparison mode. The
     * comparison function is set to {@link CompareFunction#LESS_EQUAL}.
     */
    constructor(mode: CompareMode)

    /**
     * Initializes the <code>TextureSampler</code> with user specified comparison mode and function.
     */
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

