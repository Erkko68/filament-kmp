package io.github.erkko68.filament

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
open class ToneMapper(internal val nativeHandle: NativePointer) {
    /**
     * Linear tone mapping operator that returns the input color clamped to
     * the 0..1 range. This operator is mostly useful for debugging.
     *
     * Maps scene-referred (open domain) values directly to display-referred values
     * with clamping.
     */
    class Linear() : ToneMapper(FilaToneMapper_Linear())
    /**
     * ACES tone mapping operator.
     *
     * This operator is an implementation of the ACES Reference Rendering Transform (RRT)
     * combined with the Output Device Transform (ODT) for sRGB monitors (dim surround,
     * 100 nits).
     */
    class ACES() : ToneMapper(FilaToneMapper_ACES())
    /**
     * ACES tone mapping operator, modified for legacy compatibility.
     *
     * This operator is the same as ACES but applies a brightness multiplier of ~1.6
     * to the input color value to target brighter viewing environments. Exists for
     * backward compatibility purposes.
     */
    class ACESLegacy() : ToneMapper(FilaToneMapper_ACESLegacy())
    /**
     * "Filmic" tone mapping operator.
     *
     * This tone mapper was designed to approximate the aesthetics of the ACES RRT + ODT
     * for Rec.709 and historically Filament's default tone mapping operator. It exists
     * only for backward compatibility purposes and is not otherwise recommended.
     */
    class Filmic() : ToneMapper(FilaToneMapper_Filmic())
    /**
     * Khronos PBR Neutral tone mapping operator.
     *
     * This tone mapper was designed to preserve the appearance of materials across
     * lighting conditions while avoiding artifacts in the highlights in high dynamic
     * range conditions.
     */
    class PBRNeutralToneMapper() : ToneMapper(FilaToneMapper_PBRNeutral())
    /**
     * Gran Turismo 7 tone mapping operator.
     *
     * This tone mapper was designed to preserve the appearance of materials across
     * lighting conditions while avoiding artifacts in the highlights in high dynamic
     * range conditions. This tone mapper targets an SDR paper white value of 250 nits,
     * with a reference luminance of 100 cd/m² (a value of 1.0 in the HDR framebuffer).
     */
    class GT7ToneMapper() : ToneMapper(FilaToneMapper_GT7())
    
    /**
     * AgX tone mapping operator.
     *
     * @param look An optional creative adjustment to contrast and saturation.
     */
    class Agx(look: AgxLook) : ToneMapper(
        FilaToneMapper_Agx(look.ordinal)
    ) {
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
    class Generic(
        contrast: Float,
        midGrayIn: Float,
        midGrayOut: Float,
        hdrMax: Float
    ) : ToneMapper(FilaToneMapper_Generic(contrast, midGrayIn, midGrayOut, hdrMax)) {
        /** Controls the contrast of the curve. Must be > 0.0, values in 0.5..2.0 recommended. */
        var contrast: Float
            get() = FilaToneMapper_Generic_getContrast(nativeHandle)
            set(value) { FilaToneMapper_Generic_setContrast(nativeHandle, value) }
        /** Sets the input middle gray, between 0.0 and 1.0. */
        var midGrayIn: Float
            get() = FilaToneMapper_Generic_getMidGrayIn(nativeHandle)
            set(value) { FilaToneMapper_Generic_setMidGrayIn(nativeHandle, value) }
        /** Sets the output middle gray, between 0.0 and 1.0. */
        var midGrayOut: Float
            get() = FilaToneMapper_Generic_getMidGrayOut(nativeHandle)
            set(value) { FilaToneMapper_Generic_setMidGrayOut(nativeHandle, value) }
        /** Defines the maximum input value that maps to output white. Must be >= 1.0. */
        var hdrMax: Float
            get() = FilaToneMapper_Generic_getHdrMax(nativeHandle)
            set(value) { FilaToneMapper_Generic_setHdrMax(nativeHandle, value) }
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
    class DisplayRange() : ToneMapper(FilaToneMapper_DisplayRange())
}

@ExternalSymbolName("FilaToneMapper_ACES")
private external fun FilaToneMapper_ACES(): NativePointer

@ExternalSymbolName("FilaToneMapper_ACESLegacy")
private external fun FilaToneMapper_ACESLegacy(): NativePointer

@ExternalSymbolName("FilaToneMapper_Agx")
private external fun FilaToneMapper_Agx(look: Int): NativePointer

@ExternalSymbolName("FilaToneMapper_DisplayRange")
private external fun FilaToneMapper_DisplayRange(): NativePointer

@ExternalSymbolName("FilaToneMapper_Filmic")
private external fun FilaToneMapper_Filmic(): NativePointer

@ExternalSymbolName("FilaToneMapper_GT7")
private external fun FilaToneMapper_GT7(): NativePointer

@ExternalSymbolName("FilaToneMapper_Generic")
private external fun FilaToneMapper_Generic(contrast: Float, midGrayIn: Float, midGrayOut: Float, hdrMax: Float): NativePointer

@ExternalSymbolName("FilaToneMapper_Generic_getContrast")
private external fun FilaToneMapper_Generic_getContrast(toneMapper: NativePointer): Float

@ExternalSymbolName("FilaToneMapper_Generic_getHdrMax")
private external fun FilaToneMapper_Generic_getHdrMax(toneMapper: NativePointer): Float

@ExternalSymbolName("FilaToneMapper_Generic_getMidGrayIn")
private external fun FilaToneMapper_Generic_getMidGrayIn(toneMapper: NativePointer): Float

@ExternalSymbolName("FilaToneMapper_Generic_getMidGrayOut")
private external fun FilaToneMapper_Generic_getMidGrayOut(toneMapper: NativePointer): Float

@ExternalSymbolName("FilaToneMapper_Generic_setContrast")
private external fun FilaToneMapper_Generic_setContrast(toneMapper: NativePointer, contrast: Float)

@ExternalSymbolName("FilaToneMapper_Generic_setHdrMax")
private external fun FilaToneMapper_Generic_setHdrMax(toneMapper: NativePointer, hdrMax: Float)

@ExternalSymbolName("FilaToneMapper_Generic_setMidGrayIn")
private external fun FilaToneMapper_Generic_setMidGrayIn(toneMapper: NativePointer, midGrayIn: Float)

@ExternalSymbolName("FilaToneMapper_Generic_setMidGrayOut")
private external fun FilaToneMapper_Generic_setMidGrayOut(toneMapper: NativePointer, midGrayOut: Float)

@ExternalSymbolName("FilaToneMapper_Linear")
private external fun FilaToneMapper_Linear(): NativePointer

@ExternalSymbolName("FilaToneMapper_PBRNeutral")
private external fun FilaToneMapper_PBRNeutral(): NativePointer
