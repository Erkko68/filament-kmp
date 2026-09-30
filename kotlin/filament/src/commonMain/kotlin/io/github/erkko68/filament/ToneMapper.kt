package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
import io.github.erkko68.filament.interop.*

/**
 * Interface for tone mapping operators.
 *
 * A tone mapping operator, or tone mapper, is responsible for compressing the
 * dynamic range of the rendered scene to a dynamic range suitable for display.
 *
 * In Filament, tone mapping is a color grading step. ToneMapper instances are
 * created and passed to the ColorGrading.Builder to produce a 3D LUT that will
 * be used during post-processing to prepare the final color buffer for display.
 *
 * Filament provides several default tone mapping operators that fall into three
 * categories:
 *
 * - Configurable tone mapping operators
 *   - Generic
 *   - Agx
 * - Fixed-aesthetic tone mapping operators
 *   - ACES
 *   - ACESLegacy
 *   - Filmic
 *   - PBRNeutralToneMapper
 *   - GT7ToneMapper
 * - Debug/validation tone mapping operators
 *   - Linear
 *   - DisplayRange
 *
 * Custom tone mapping operators can be created by subclassing ToneMapper.
 */
open class ToneMapper(internal val nativeHandle: NativePointer) : AutoCloseable {
    /**
     * Maps an open domain (or "scene referred") color value to a display domain (or "display
     * referred") color value. Both are in the Rec.2020 color space, with no transfer function
     * applied ("linear Rec.2020").
     *
     * @param color Input color to tone map, as 3 floats.
     * @param out Optional FloatArray for the result; created if null.
     * @return The tone mapped color, as 3 floats.
     */
    operator fun invoke(color: FloatArray, out: FloatArray? = null): FloatArray {
        val result = out ?: FloatArray(3)
        color.usePinned { c -> result.usePinned { r -> FilaToneMapper_invoke(nativeHandle, c, r) } }
        return result
    }

    /**
     * If true, then this function holds that f(x) = vec3(f(x.r), f(x.g), f(x.b)), which lets
     * Filament build its LUT faster.
     */
    val isOneDimensional: Boolean get() = FilaToneMapper_isOneDimensional(nativeHandle)

    /** True if this tone mapper produces LDR output, i.e. never outputs values above 1.0. */
    val isLDR: Boolean get() = FilaToneMapper_isLDR(nativeHandle)

    /** Destroys the native tone mapper. ColorGrading.Builder only uses it during build(), so it can be closed afterwards. */
    override fun close() = FilaToneMapper_destroy(nativeHandle)

    /**
     * Linear tone mapping operator that returns the input color clamped to
     * the 0..1 range. This operator is mostly useful for debugging.
     *
     * Maps scene-referred (open domain) values directly to display-referred values
     * with clamping.
     */
    class Linear() : ToneMapper(FilaLinearToneMapper_asToneMapper(FilaLinearToneMapper_create()))
    /**
     * ACES tone mapping operator.
     *
     * This operator is an implementation of the ACES Reference Rendering Transform (RRT)
     * combined with the Output Device Transform (ODT) for sRGB monitors (dim surround,
     * 100 nits).
     */
    class ACES() : ToneMapper(FilaACESToneMapper_asToneMapper(FilaACESToneMapper_create()))
    /**
     * ACES tone mapping operator, modified for legacy compatibility.
     *
     * This operator is the same as ACES but applies a brightness multiplier of ~1.6
     * to the input color value to target brighter viewing environments. Exists for
     * backward compatibility purposes.
     */
    class ACESLegacy() : ToneMapper(FilaACESLegacyToneMapper_asToneMapper(FilaACESLegacyToneMapper_create()))
    /**
     * "Filmic" tone mapping operator.
     *
     * This tone mapper was designed to approximate the aesthetics of the ACES RRT + ODT
     * for Rec.709 and historically Filament's default tone mapping operator. It exists
     * only for backward compatibility purposes and is not otherwise recommended.
     */
    class Filmic() : ToneMapper(FilaFilmicToneMapper_asToneMapper(FilaFilmicToneMapper_create()))
    /**
     * Khronos PBR Neutral tone mapping operator.
     *
     * This tone mapper was designed to preserve the appearance of materials across
     * lighting conditions while avoiding artifacts in the highlights in high dynamic
     * range conditions.
     */
    class PBRNeutralToneMapper() : ToneMapper(FilaPBRNeutralToneMapper_asToneMapper(FilaPBRNeutralToneMapper_create()))
    /**
     * Gran Turismo 7 tone mapping operator.
     *
     * This tone mapper was designed to preserve the appearance of materials across
     * lighting conditions while avoiding artifacts in the highlights in high dynamic
     * range conditions. This tone mapper targets an SDR paper white value of 250 nits,
     * with a reference luminance of 100 cd/m² (a value of 1.0 in the HDR framebuffer).
     */
    class GT7ToneMapper() : ToneMapper(FilaGT7ToneMapper_asToneMapper(FilaGT7ToneMapper_create()))
    
    /**
     * AgX tone mapping operator.
     *
     * @param look An optional creative adjustment to contrast and saturation.
     */
    class Agx private constructor(private val agx: NativePointer) : ToneMapper(FilaAgxToneMapper_asToneMapper(agx)) {
        constructor(look: AgxLook = AgxLook.NONE) : this(FilaAgxToneMapper_create(look.ordinal))

        /** An optional creative adjustment to contrast and saturation. */
        var look: AgxLook
            get() = AgxLook.entries[FilaAgxToneMapper_getLook(agx)]
            set(value) { FilaAgxToneMapper_setLook(agx, value.ordinal) }

        /**
         * Creative adjustments for the AgX tone mapping operator.
         */
        enum class AgxLook {
            /** Base contrast with no look applied */
            NONE,
            /** A punchy and more chroma laden look for sRGB displays */
            PUNCHY,
            /** A golden tinted, slightly washed look for BT.1886 displays */
            GOLDEN
        }
    }
    
    /**
     * Generic tone mapping operator that gives control over the tone mapping curve.
     *
     * This operator can be used to control the aesthetics of the final image. It also
     * allows control over the dynamic range of the scene-referred values.
     *
     * The tone mapping curve is defined by 5 parameters:
     * - contrast: controls the contrast of the curve
     * - midGrayIn: sets the input middle gray
     * - midGrayOut: sets the output middle gray
     * - hdrMax: defines the maximum input value that will be mapped to output white
     *
     * The default values approximate an ACES tone mapping curve with a maximum input
     * value of 10.0.
     *
     * @param contrast Controls the contrast of the curve. Must be > 0.0, values in the
     *                 range 0.5..2.0 are recommended. Default is 1.55.
     * @param midGrayIn Sets the input middle gray, between 0.0 and 1.0. Default is 0.18.
     * @param midGrayOut Sets the output middle gray, between 0.0 and 1.0. Default is 0.215.
     * @param hdrMax Defines the maximum input value that will be mapped to output white.
     *               Must be >= 1.0. Default is 10.0.
     */
    class Generic private constructor(private val generic: NativePointer) : ToneMapper(FilaGenericToneMapper_asToneMapper(generic)) {
        constructor(
            contrast: Float = 1.55f,
            midGrayIn: Float = 0.18f,
            midGrayOut: Float = 0.215f,
            hdrMax: Float = 10.0f
        ) : this(FilaGenericToneMapper_create(contrast, midGrayIn, midGrayOut, hdrMax))

        /** Controls the contrast of the curve. Must be > 0.0, values in 0.5..2.0 recommended. */
        var contrast: Float
            get() = FilaGenericToneMapper_getContrast(generic)
            set(value) { FilaGenericToneMapper_setContrast(generic, value) }
        /** Sets the input middle gray, between 0.0 and 1.0. */
        var midGrayIn: Float
            get() = FilaGenericToneMapper_getMidGrayIn(generic)
            set(value) { FilaGenericToneMapper_setMidGrayIn(generic, value) }
        /** Sets the output middle gray, between 0.0 and 1.0. */
        var midGrayOut: Float
            get() = FilaGenericToneMapper_getMidGrayOut(generic)
            set(value) { FilaGenericToneMapper_setMidGrayOut(generic, value) }
        /** Defines the maximum input value that maps to output white. Must be >= 1.0. */
        var hdrMax: Float
            get() = FilaGenericToneMapper_getHdrMax(generic)
            set(value) { FilaGenericToneMapper_setHdrMax(generic, value) }
    }
    
    /**
     * A tone mapper that converts the input HDR RGB color into one of 16 debug colors
     * that represent the pixel's exposure.
     *
     * When the output is cyan, the input color represents middle gray (18% exposure).
     * Every exposure stop above or below middle gray causes a color shift.
     *
     * The relationship between exposures and colors is:
     * - -5EV  black
     * - -4EV  darkest blue
     * - -3EV  darker blue
     * - -2EV  dark blue
     * - -1EV  blue
     * -  0EV  cyan
     * - +1EV  dark green
     * - +2EV  green
     * - +3EV  yellow
     * - +4EV  yellow-orange
     * - +5EV  orange
     * - +6EV  bright red
     * - +7EV  red
     * - +8EV  magenta
     * - +9EV  purple
     * - +10EV white
     *
     * This tone mapper is useful to validate and tweak scene lighting.
     */
    class DisplayRange() : ToneMapper(FilaDisplayRangeToneMapper_asToneMapper(FilaDisplayRangeToneMapper_create()))
}
