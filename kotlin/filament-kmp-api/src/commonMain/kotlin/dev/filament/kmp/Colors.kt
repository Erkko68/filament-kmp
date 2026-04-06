package dev.filament.kmp

/**
 * Utilities to manipulate and convert colors.
 */
expect class Colors {

    /**
     * Types of RGB colors.
     */
    enum class RgbType {
        SRGB,
        LINEAR
    }

    /**
     * Types of RGBA colors.
     */
    enum class RgbaType {
        SRGB,
        LINEAR,
        PREMULTIPLIED_SRGB,
        PREMULTIPLIED_LINEAR
    }

    /**
     * Type of color conversion to use when converting to/from sRGB and linear spaces.
     */
    enum class Conversion {
        ACCURATE,
        FAST
    }

    companion object {
        /**
         * Converts an RGB color to linear space.
         */
        fun toLinear(type: RgbType, r: Float, g: Float, b: Float): FloatArray

        /**
         * Converts an RGB color to linear space.
         */
        fun toLinear(type: RgbType, rgb: FloatArray): FloatArray

        /**
         * Converts an RGBA color to linear space.
         */
        fun toLinear(type: RgbaType, r: Float, g: Float, b: Float, a: Float): FloatArray

        /**
         * Converts an RGBA color to linear space.
         */
        fun toLinear(type: RgbaType, rgba: FloatArray): FloatArray

        /**
         * Converts an RGB color in sRGB space to an RGB color in linear space.
         */
        fun toLinear(conversion: Conversion, rgb: FloatArray): FloatArray

        /**
         * Converts a correlated color temperature to a linear RGB color in sRGB space.
         */
        fun cct(temperature: Float): FloatArray

        /**
         * Converts a CIE standard illuminant series D to a linear RGB color in sRGB space.
         */
        fun illuminantD(temperature: Float): FloatArray
    }
}
