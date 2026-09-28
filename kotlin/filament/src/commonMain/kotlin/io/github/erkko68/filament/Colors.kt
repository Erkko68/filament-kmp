package io.github.erkko68.filament

import io.github.erkko68.filament.interop.*

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Utilities for color space conversions and color temperature calculations.
 *
 * Colors provides functions to convert between color spaces (sRGB ↔ linear) and to generate
 * colors from color temperatures (in Kelvin). These utilities are useful for proper color
 * handling in rendering and for creating realistic lighting based on physical color temperatures.
 */
object Colors {
    /**
     * RGB color space types.
     *
     * - SRGB: sRGB color space (standard for display, gamma-corrected)
     * - LINEAR: Linear color space (used internally by renderers)
     */
    enum class RgbType {
        SRGB,
        LINEAR
    }

    /**
     * RGBA color space types (including alpha channel).
     *
     * - SRGB: sRGB with straight alpha
     * - LINEAR: Linear with straight alpha
     * - PREMULTIPLIED_SRGB: sRGB with pre-multiplied alpha
     * - PREMULTIPLIED_LINEAR: Linear with pre-multiplied alpha
     */
    enum class RgbaType {
        SRGB,
        LINEAR,
        PREMULTIPLIED_SRGB,
        PREMULTIPLIED_LINEAR
    }

    /**
     * Conversion quality/accuracy options.
     *
     * - ACCURATE: Exact conversion (slightly slower)
     * - FAST: Fast approximation (good for most cases)
     */
    enum class Conversion {
        ACCURATE,
        FAST
    }

    /**
     * Converts an RGB color from the specified color space to linear RGB.
     *
     * @param type The source color space (sRGB or LINEAR)
     * @param r Red channel value [0, 1]
     * @param g Green channel value [0, 1]
     * @param b Blue channel value [0, 1]
     * @return Linear RGB as a 3-element array [R, G, B]
     */
    fun toLinear(type: RgbType, r: Float, g: Float, b: Float): FloatArray {
        return toLinear(type, floatArrayOf(r, g, b))
    }

    /**
     * Converts an RGB color from the specified color space to linear RGB.
     *
     * @param type The source color space (sRGB or LINEAR)
     * @param rgb RGB color as a 3-element array [R, G, B]
     * @return Linear RGB as a 3-element array [R, G, B]
     */
    fun toLinear(type: RgbType, rgb: FloatArray): FloatArray {
        val out = FloatArray(3)
        rgb.usePinned { i -> out.usePinned { o -> FilaColors_toLinearRgb(type.ordinal, i, o) } }
        return out.copyInto(rgb)
    }

    /**
     * Converts an RGBA color from the specified color space to linear RGB.
     *
     * @param type The source color space (sRGB, LINEAR, PREMULTIPLIED_SRGB, PREMULTIPLIED_LINEAR)
     * @param r Red channel value [0, 1]
     * @param g Green channel value [0, 1]
     * @param b Blue channel value [0, 1]
     * @param a Alpha channel value [0, 1]
     * @return Linear RGB with alpha as a 4-element array [R, G, B, A]
     */
    fun toLinear(type: RgbaType, r: Float, g: Float, b: Float, a: Float): FloatArray {
        return toLinear(type, floatArrayOf(r, g, b, a))
    }

    /**
     * Converts an RGBA color from the specified color space to linear RGB.
     *
     * @param type The source color space (sRGB, LINEAR, PREMULTIPLIED_SRGB, PREMULTIPLIED_LINEAR)
     * @param rgba RGBA color as a 4-element array [R, G, B, A]
     * @return Linear RGB with alpha as a 4-element array [R, G, B, A]
     */
    fun toLinear(type: RgbaType, rgba: FloatArray): FloatArray {
        val out = FloatArray(4)
        rgba.usePinned { i -> out.usePinned { o -> FilaColors_toLinearRgba(type.ordinal, i, o) } }
        return out.copyInto(rgba)
    }

    /**
     * Converts an RGB color from the specified color space to linear RGB using the specified
     * conversion quality.
     *
     * @param conversion The conversion method (ACCURATE or FAST)
     * @param rgb RGB color as a 3-element array [R, G, B]
     * @return Linear RGB as a 3-element array [R, G, B]
     */
    fun toLinear(conversion: Conversion, rgb: FloatArray): FloatArray {
        val out = FloatArray(3)
        rgb.usePinned { i -> out.usePinned { o -> FilaColors_toLinearConvert(conversion.ordinal, i, o) } }
        return out.copyInto(rgb)
    }

    /**
     * Generates an RGB color from a color correlated color temperature (CCT) in Kelvin.
     *
     * This uses the standard CCT to RGB conversion formula to approximate the color of light
     * at a given temperature. Useful for creating realistic lighting based on physical
     * color temperatures (e.g., 6500K for daylight, 3000K for warm indoor light).
     *
     * @param temperature Color temperature in Kelvin (typically 1000K to 10000K)
     * @return Linear RGB color as a 3-element array [R, G, B]
     */
    fun cct(temperature: Float): FloatArray {
        val color = FloatArray(3)
        color.usePinned { FilaColors_cct(temperature, it) }
        return color
    }

    /**
     * Generates an RGB color for a CIE D (daylight) illuminant at the specified temperature.
     *
     * The CIE D illuminant series is the standard for daylight representation in color science.
     * It's used as a reference for standard light sources in graphics and photography.
     *
     * @param temperature Color temperature in Kelvin
     * @return Linear RGB color as a 3-element array [R, G, B]
     */
    fun illuminantD(temperature: Float): FloatArray {
        val color = FloatArray(3)
        color.usePinned { FilaColors_illuminantD(temperature, it) }
        return color
    }
}

@ExternalSymbolName("FilaColors_cct")
private external fun FilaColors_cct(temperature: Float, outColor: NativePointer)

@ExternalSymbolName("FilaColors_illuminantD")
private external fun FilaColors_illuminantD(temperature: Float, outColor: NativePointer)

@ExternalSymbolName("FilaColors_toLinearConvert")
private external fun FilaColors_toLinearConvert(conversion: Int, inRgb: NativePointer, outRgb: NativePointer)

@ExternalSymbolName("FilaColors_toLinearRgb")
private external fun FilaColors_toLinearRgb(type: Int, inRgb: NativePointer, outRgb: NativePointer)

@ExternalSymbolName("FilaColors_toLinearRgba")
private external fun FilaColors_toLinearRgba(type: Int, inRgba: NativePointer, outRgba: NativePointer)
