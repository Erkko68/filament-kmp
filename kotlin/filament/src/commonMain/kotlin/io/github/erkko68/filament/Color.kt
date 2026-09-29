package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
import io.github.erkko68.filament.interop.*

/** Types of RGB colors. */
enum class RgbType {
    /** The color is defined in Rec.709-sRGB-D65 (sRGB) space. */
    sRGB,
    /** The color is defined in Rec.709-Linear-D65 ("linear sRGB") space. */
    LINEAR,
}

/** Types of RGBA colors. */
enum class RgbaType {
    /** Rec.709-sRGB-D65 (sRGB) space, RGB not pre-multiplied by alpha (50% transparent red is <1,0,0,0.5>). */
    sRGB,
    /** Rec.709-Linear-D65 ("linear sRGB") space, RGB not pre-multiplied by alpha. */
    LINEAR,
    /** Rec.709-sRGB-D65 (sRGB) space, RGB pre-multiplied by alpha (50% transparent red is <0.5,0,0,0.5>). */
    PREMULTIPLIED_sRGB,
    /** Rec.709-Linear-D65 ("linear sRGB") space, RGB pre-multiplied by alpha. */
    PREMULTIPLIED_LINEAR,
}

/**
 * Utilities to manipulate and convert colors. RGB colors are 3 floats, RGBA colors 4; results are new arrays.
 * sRGB conversions are the accurate ones (C++'s default `ColorConversion::ACCURATE`).
 */
object Color {
    init { Filament.init() } // CPU-only, usable before any Engine exists

    /** Converts an RGB color to linear space; the conversion depends on [type]. */
    fun toLinear(type: RgbType, color: FloatArray): FloatArray =
        call(3) { out -> interopScope { FilaColor_toLinear_RgbType_float3(type.ordinal, toInterop(color), out) } }

    /** Converts an RGBA color to linear space; the conversion depends on [type]. */
    fun toLinear(type: RgbaType, color: FloatArray): FloatArray =
        call(4) { out -> interopScope { FilaColor_toLinear_RgbaType_float4(type.ordinal, toInterop(color), out) } }

    /** Converts an sRGB color, RGB or RGBA (alpha left unmodified), to linear space. */
    fun toLinear(color: FloatArray): FloatArray = call(color.size) { out ->
        interopScope {
            if (color.size == 3) FilaColor_toLinear_sRGBColor(toInterop(color), out)
            else FilaColor_toLinear_sRGBColorA(toInterop(color), out)
        }
    }

    /** Converts a linear color, RGB or RGBA (alpha left unmodified), to sRGB space. */
    fun toSRGB(color: FloatArray): FloatArray = call(color.size) { out ->
        interopScope {
            if (color.size == 3) FilaColor_toSRGB_LinearColor(toInterop(color), out)
            else FilaColor_toSRGB_LinearColorA(toInterop(color), out)
        }
    }

    /** Converts a correlated color temperature, in kelvin within 1,000K..15,000K, to a linear sRGB color. */
    fun cct(K: Float): FloatArray = call(3) { FilaColor_cct(K, it) }

    /** Converts a CIE standard illuminant series D, in kelvin within 4,000K..25,000K, to a linear sRGB color. */
    fun illuminantD(K: Float): FloatArray = call(3) { FilaColor_illuminantD(K, it) }

    /**
     * Computes the Beer-Lambert absorption coefficients that turn white light into [color] at [distance],
     * for the absorption parameter of materials that use refraction.
     */
    fun absorptionAtDistance(color: FloatArray, distance: Float): FloatArray =
        call(3) { out -> interopScope { FilaColor_absorptionAtDistance(toInterop(color), distance, out) } }

    private inline fun call(size: Int, block: (NativePointer) -> Unit): FloatArray {
        require(size == 3 || size == 4) { "a color has 3 or 4 components, not $size" }
        return FloatArray(size).also { out -> out.usePinned(block) }
    }
}
