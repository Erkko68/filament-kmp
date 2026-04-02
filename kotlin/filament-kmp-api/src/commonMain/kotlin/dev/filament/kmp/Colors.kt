package dev.filament.kmp

/**
 * Utilities to manipulate and convert colors.
 */
@Target(
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.FIELD,
)
annotation class LinearColor

expect object Colors {
    /**
     * Types of RGB colors.
     */
    enum class RgbType {
        /** The color is defined in sRGB space. */
        SRGB,

        /** The color is defined in linear space. */
        LINEAR,
    }

    /**
     * Types of RGBA colors.
     */
    enum class RgbaType {
        /**
         * The color is defined in sRGB space and the RGB values have not been premultiplied by the
         * alpha (for instance, a 50% transparent red is <1,0,0,0.5>).
         */
        SRGB,

        /**
         * The color is defined in linear space and the RGB values have not been premultiplied by
         * the alpha (for instance, a 50% transparent red is <1,0,0,0.5>).
         */
        LINEAR,

        /**
         * The color is defined in sRGB space and the RGB values have been premultiplied by the
         * alpha (for instance, a 50% transparent red is <0.5,0,0,0.5>).
         */
        PREMULTIPLIED_SRGB,

        /**
         * The color is defined in linear space and the RGB values have been premultiplied by the
         * alpha (for instance, a 50% transparent red is <0.5,0,0,0.5>).
         */
        PREMULTIPLIED_LINEAR,
    }

    /**
     * Type of color conversion to use when converting to/from sRGB and linear spaces.
     */
    enum class Conversion {
        /** Accurate conversion using the sRGB standard. */
        ACCURATE,

        /** Fast conversion using a simple gamma 2.2 curve. */
        FAST,
    }

    /**
     * Converts an RGB color to linear space, the conversion depends on the specified type.
     *
     * @param type the color space of the RGB color values provided
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     *
     * @return an RGB float array of size 3 with the result of the conversion
     */
    @LinearColor
    fun toLinear(type: RgbType, r: Float, g: Float, b: Float): FloatArray

    /**
     * Converts an RGB color to linear space, the conversion depends on the specified type.
     *
     * @param type the color space of the RGB color values provided
     * @param rgb an RGB float array of size 3, will be modified
     *
     * @return the passed-in <code>rgb</code> array, after applying the conversion
     */
    @LinearColor
    fun toLinear(type: RgbType, rgb: FloatArray): FloatArray

    /**
     * Converts an RGBA color to linear space, with pre-multiplied alpha.
     *
     * @param type the color space and type of RGBA color values provided
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @param a the alpha component
     *
     * @return an RGBA float array of size 4 with the result of the conversion
     */
    @LinearColor
    fun toLinear(type: RgbaType, r: Float, g: Float, b: Float, a: Float): FloatArray

    /**
     * Converts an RGBA color to linear space, with pre-multiplied alpha.
     *
     * @param type the color space of the RGBA color values provided
     * @param rgba an RGBA float array of size 4, will be modified
     *
     * @return the passed-in <code>rgba</code> array, after applying the conversion
     */
    @LinearColor
    fun toLinear(type: RgbaType, rgba: FloatArray): FloatArray

    /**
     * Converts an RGB color in sRGB space to an RGB color in linear space.
     *
     * @param conversion the conversion algorithm to use
     * @param rgb an RGB float array of at least size 3, will be modified
     *
     * @return the passed-in <code>rgb</code> array, after applying the conversion. The alpha
     * channel, if present, is left unmodified.
     */
    @LinearColor
    fun toLinear(conversion: Conversion, rgb: FloatArray): FloatArray

    /**
     * Converts a correlated color temperature to a linear RGB color in sRGB space. The temperature
     * must be expressed in Kelvin and must be in the range 1,000K to 15,000K.
     *
     * @param temperature the temperature, in Kelvin
     *
     * @return an RGB float array of size 3 with the result of the conversion
     */
    @LinearColor
    fun cct(temperature: Float): FloatArray

    /**
     * Converts a CIE standard illuminant series D to a linear RGB color in sRGB space. The
     * temperature must be expressed in Kelvin and must be in the range 4,000K to 25,000K.
     *
     * @param temperature the temperature, in Kelvin
     *
     * @return an RGB float array of size 3 with the result of the conversion
     */
    @LinearColor
    fun illuminantD(temperature: Float): FloatArray
}

