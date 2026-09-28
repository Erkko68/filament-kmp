package io.github.erkko68.filament

import io.github.erkko68.filament.interop.*

/**
 * ColorGrading is used to transform (modify or correct) the colors of the HDR buffer.
 *
 * Color grading transforms are applied after lighting and lens effects (like bloom), and include
 * tone mapping. ColorGrading allows adjusting image properties like exposure, contrast, saturation,
 * temperature, tint, shadows, midtones, and highlights. These adjustments are applied through a
 * lookup table (LUT) for efficient processing.
 *
 * **Creation and usage:** A ColorGrading object is created using the Builder and destroyed with
 * [Engine.destroy]. A ColorGrading object is meant to be set on a [View].
 *
 * **Order of operations:** The various transforms are applied in the following order:
 * 1. Exposure
 * 2. Night adaptation
 * 3. White balance
 * 4. Channel mixer
 * 5. Shadows/mid-tones/highlights
 * 6. Slope/offset/power (CDL)
 * 7. Contrast
 * 8. Vibrance
 * 9. Saturation
 * 10. Curves
 * 11. Tone mapping
 * 12. Luminance scaling
 * 13. Gamut mapping
 *
 * **Performance note:** Creating a ColorGrading object may be more expensive than other Filament
 * objects as a LUT may need to be generated, which may happen on the CPU.
 *
 * @see View
 */
class ColorGrading @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * Builder for creating ColorGrading instances.
     *
     * Configure color grading settings such as exposure, contrast, saturation, and tone mapping,
     * then call build() to create the ColorGrading object.
     */
    class Builder() {
        internal val nativeHandle: NativePointer = FilaColorGradingBuilder_create()

        /**
         * Sets the quality level of the color grading LUT.
         *
         * @param qualityLevel The quality level (default: MEDIUM)
         * @return This Builder, for chaining calls
         */
        fun quality(qualityLevel: QualityLevel): Builder {
            FilaColorGradingBuilder_quality(nativeHandle, qualityLevel.ordinal)
            return this
        }

        /**
         * Sets the format of the color grading LUT.
         *
         * @param format The LUT format (default: FLOAT)
         * @return This Builder, for chaining calls
         */
        fun format(format: LutFormat): Builder {
            FilaColorGradingBuilder_format(nativeHandle, format.ordinal)
            return this
        }

        /**
         * Sets the dimensions of the color grading LUT (3D cube side length).
         *
         * @param dim The side length of the LUT cube (default: 32)
         * @return This Builder, for chaining calls
         */
        fun dimensions(dim: Int): Builder {
            FilaColorGradingBuilder_dimensions(nativeHandle, dim)
            return this
        }

        /**
         * Sets the tone mapper to use for tone mapping.
         *
         * @param toneMapper The tone mapper object
         * @return This Builder, for chaining calls
         */
        fun toneMapper(toneMapper: ToneMapper): Builder {
            FilaColorGradingBuilder_toneMapper(nativeHandle, toneMapper.nativeHandle)
            return this
        }

        /**
         * Enables or disables luminance scaling.
         *
         * Luminance scaling adjusts the brightness based on perceived luminance.
         *
         * @param luminanceScaling true to enable, false to disable (default: false)
         * @return This Builder, for chaining calls
         */
        fun luminanceScaling(luminanceScaling: Boolean): Builder {
            FilaColorGradingBuilder_luminanceScaling(nativeHandle, luminanceScaling)
            return this
        }

        /**
         * Enables or disables gamut mapping.
         *
         * Gamut mapping ensures colors stay within valid display gamut after grading.
         *
         * @param gamutMapping true to enable, false to disable (default: false)
         * @return This Builder, for chaining calls
         */
        fun gamutMapping(gamutMapping: Boolean): Builder {
            FilaColorGradingBuilder_gamutMapping(nativeHandle, gamutMapping)
            return this
        }

        /**
         * Adjusts the exposure of the image in exposure value (EV) stops.
         *
         * Each stop brightens (positive values) or darkens (negative values) the image by a factor
         * of 2. For example:
         * - exposure = +3 brightens the image 8 times (2^3 = 8)
         * - exposure = -2 darkens the image 4 times (2^-2 = 0.25)
         * - exposure = 0 has no effect
         *
         * Note: This exposure is applied after all post-processing (bloom, etc.), unlike camera exposure.
         * Default: 0.0 (no adjustment).
         *
         * @param exposure Value in EV stops (can be negative, zero, or positive)
         * @return This Builder, for chaining calls
         */
        fun exposure(exposure: Float): Builder {
            FilaColorGradingBuilder_exposure(nativeHandle, exposure)
            return this
        }

        /**
         * Controls the amount of night adaptation to replicate low-light vision.
         *
         * In low-light conditions, the peak luminance sensitivity of the eye shifts toward the
         * blue end of the color spectrum: darker tones appear brighter (reduced contrast), and
         * colors are blue-shifted (darker areas shift more intensely toward blue).
         *
         * Default: 0.0 (no adaptation).
         *
         * @param adaptation Amount of adaptation, between 0 (no adaptation) and 1 (full adaptation)
         * @return This Builder, for chaining calls
         */
        fun nightAdaptation(adaptation: Float): Builder {
            FilaColorGradingBuilder_nightAdaptation(nativeHandle, adaptation)
            return this
        }

        /**
         * Adjusts the white balance of the image to remove color casts or for artistic purposes.
         *
         * The white balance adjustment is defined with two values:
         * - **Temperature** [-1.0, +1.0]: Modifies colors on a blue/yellow axis. -1.0 is equivalent
         *   to 50,000K (cool/blue), +1.0 is equivalent to 2,000K (warm/yellow). Lower values appear
         *   cooler, higher values appear warmer.
         * - **Tint** [-1.0, +1.0]: Modifies colors on a green/magenta axis. -1.0 applies a strong
         *   green cast, +1.0 applies a strong magenta cast.
         *
         * Both values are clipped to the range [-1.0, +1.0] if outside this range.
         * Default: temperature=0, tint=0 (no adjustment).
         *
         * @param temperature Modification on the blue/yellow axis [-1.0, +1.0]
         * @param tint Modification on the green/magenta axis [-1.0, +1.0]
         * @return This Builder, for chaining calls
         */
        fun whiteBalance(temperature: Float, tint: Float): Builder {
            FilaColorGradingBuilder_whiteBalance(nativeHandle, temperature, tint)
            return this
        }

        /**
         * Sets the channel mixer to adjust individual color channels.
         *
         * Each channel is specified as a 3-element array representing the influence of R, G, B.
         *
         * @param outRed Output red channel mix [R, G, B]
         * @param outGreen Output green channel mix [R, G, B]
         * @param outBlue Output blue channel mix [R, G, B]
         * @return This Builder, for chaining calls
         */
        fun channelMixer(outRed: FloatArray, outGreen: FloatArray, outBlue: FloatArray): Builder {
            interopScope {
                FilaColorGradingBuilder_channelMixer(nativeHandle, toInterop(outRed), toInterop(outGreen), toInterop(outBlue))
            }
            return this
        }

        /**
         * Sets shadows, midtones, and highlights adjustments.
         *
         * Each adjustment is a 3-element array for [lift, gamma, gain].
         *
         * @param shadows Adjustment for shadows [lift, gamma, gain]
         * @param midtones Adjustment for midtones [lift, gamma, gain]
         * @param highlights Adjustment for highlights [lift, gamma, gain]
         * @param ranges Ranges for tone separation (optional)
         * @return This Builder, for chaining calls
         */
        fun shadowsMidtonesHighlights(shadows: FloatArray, midtones: FloatArray, highlights: FloatArray, ranges: FloatArray): Builder {
            interopScope {
                FilaColorGradingBuilder_shadowsMidtonesHighlights(nativeHandle, toInterop(shadows), toInterop(midtones), toInterop(highlights), toInterop(ranges))
            }
            return this
        }

        /**
         * Sets slope, offset, and power adjustments (ASC CDL).
         *
         * Each adjustment is a 3-element array for [R, G, B].
         *
         * @param slope Slope values [R, G, B]
         * @param offset Offset values [R, G, B]
         * @param power Power values [R, G, B]
         * @return This Builder, for chaining calls
         */
        fun slopeOffsetPower(slope: FloatArray, offset: FloatArray, power: FloatArray): Builder {
            interopScope {
                FilaColorGradingBuilder_slopeOffsetPower(nativeHandle, toInterop(slope), toInterop(offset), toInterop(power))
            }
            return this
        }

        /**
         * Sets the contrast adjustment.
         *
         * @param contrast Contrast value (default: 1.0, <1.0 reduces, >1.0 increases)
         * @return This Builder, for chaining calls
         */
        fun contrast(contrast: Float): Builder {
            FilaColorGradingBuilder_contrast(nativeHandle, contrast)
            return this
        }

        /**
         * Sets the vibrance adjustment (selective saturation).
         *
         * @param vibrance Vibrance value (default: 0.0, positive increases vibrant colors)
         * @return This Builder, for chaining calls
         */
        fun vibrance(vibrance: Float): Builder {
            FilaColorGradingBuilder_vibrance(nativeHandle, vibrance)
            return this
        }

        /**
         * Sets the saturation adjustment.
         *
         * @param saturation Saturation value (default: 1.0, <1.0 desaturates, >1.0 saturates)
         * @return This Builder, for chaining calls
         */
        fun saturation(saturation: Float): Builder {
            FilaColorGradingBuilder_saturation(nativeHandle, saturation)
            return this
        }

        /**
         * Sets curve adjustments for shadows, midtones, and highlights.
         *
         * @param shadowGamma Gamma curve for shadows
         * @param midPoint Midpoint curve adjustment
         * @param highlightScale Highlight curve adjustment
         * @return This Builder, for chaining calls
         */
        fun curves(shadowGamma: FloatArray, midPoint: FloatArray, highlightScale: FloatArray): Builder {
            interopScope {
                FilaColorGradingBuilder_curves(nativeHandle, toInterop(shadowGamma), toInterop(midPoint), toInterop(highlightScale))
            }
            return this
        }

        /**
         * Specifies a custom 3D color grading LUT to map the final sRGB color, applied after
         * post-processing in LDR (sRGB space).
         *
         * @param data LUT data as a flat array of RGB triplets; size must be
         *   `dimension * dimension * dimension * 3` floats
         * @param dimension Dimension of the custom LUT (e.g. 16, 32, 64)
         * @return This Builder, for chaining calls
         */
        fun customLut(data: FloatArray, dimension: Int): Builder {
            data.usePinned { pinned ->
                FilaColorGradingBuilder_customLut(nativeHandle, pinned, dimension)
            }
            return this
        }

        /**
         * Enables or disables fast math approximations.
         *
         * Fast math may sacrifice some precision for better performance.
         *
         * @param fastMath true to enable fast math (default: true)
         * @return This Builder, for chaining calls
         */
        fun fastMath(fastMath: Boolean): Builder {
            FilaColorGradingBuilder_fastMath(nativeHandle, fastMath)
            return this
        }

        /**
         * Creates the ColorGrading object.
         *
         * @param engine Engine to associate this ColorGrading with
         * @return The newly created ColorGrading
         */
        fun build(engine: Engine): ColorGrading {
            return ColorGrading(FilaColorGradingBuilder_build(nativeHandle, engine.nativeHandle))
        }
    }

    /**
     * Quality level for color grading affects 3D LUT resolution and bit depth.
     *
     * - LOW: 16x16x16 10-bit LUT
     * - MEDIUM: 32x32x32 10-bit LUT (default)
     * - HIGH: 32x32x32 16-bit LUT
     * - ULTRA: 64x64x64 16-bit LUT
     *
     * This setting has no effect if generating a 1D LUT.
     */
    enum class QualityLevel { LOW, MEDIUM, HIGH, ULTRA }
    /**
     * Format for the color grading 3D LUT (lookup table).
     *
     * - INTEGER: 10 bits per component (default)
     * - FLOAT: 16 bits per component (10 bits mantissa precision)
     *
     * This setting has no effect if generating a 1D LUT.
     */
    enum class LutFormat { INTEGER, FLOAT }
}

@ExternalSymbolName("FilaColorGradingBuilder_build")
private external fun FilaColorGradingBuilder_build(builder: NativePointer, engine: NativePointer): NativePointer

@ExternalSymbolName("FilaColorGradingBuilder_channelMixer")
private external fun FilaColorGradingBuilder_channelMixer(builder: NativePointer, outRed: NativePointer, outGreen: NativePointer, outBlue: NativePointer)

@ExternalSymbolName("FilaColorGradingBuilder_contrast")
private external fun FilaColorGradingBuilder_contrast(builder: NativePointer, contrast: Float)

@ExternalSymbolName("FilaColorGradingBuilder_create")
private external fun FilaColorGradingBuilder_create(): NativePointer

@ExternalSymbolName("FilaColorGradingBuilder_curves")
private external fun FilaColorGradingBuilder_curves(builder: NativePointer, shadowGamma: NativePointer, midPoint: NativePointer, highlightScale: NativePointer)

@ExternalSymbolName("FilaColorGradingBuilder_customLut")
private external fun FilaColorGradingBuilder_customLut(builder: NativePointer, data: NativePointer, dimension: Int)

@ExternalSymbolName("FilaColorGradingBuilder_dimensions")
private external fun FilaColorGradingBuilder_dimensions(builder: NativePointer, dim: Int)

@ExternalSymbolName("FilaColorGradingBuilder_exposure")
private external fun FilaColorGradingBuilder_exposure(builder: NativePointer, exposure: Float)

@ExternalSymbolName("FilaColorGradingBuilder_fastMath")
private external fun FilaColorGradingBuilder_fastMath(builder: NativePointer, fastMath: Boolean)

@ExternalSymbolName("FilaColorGradingBuilder_format")
private external fun FilaColorGradingBuilder_format(builder: NativePointer, format: Int)

@ExternalSymbolName("FilaColorGradingBuilder_gamutMapping")
private external fun FilaColorGradingBuilder_gamutMapping(builder: NativePointer, gamutMapping: Boolean)

@ExternalSymbolName("FilaColorGradingBuilder_luminanceScaling")
private external fun FilaColorGradingBuilder_luminanceScaling(builder: NativePointer, luminanceScaling: Boolean)

@ExternalSymbolName("FilaColorGradingBuilder_nightAdaptation")
private external fun FilaColorGradingBuilder_nightAdaptation(builder: NativePointer, adaptation: Float)

@ExternalSymbolName("FilaColorGradingBuilder_quality")
private external fun FilaColorGradingBuilder_quality(builder: NativePointer, quality: Int)

@ExternalSymbolName("FilaColorGradingBuilder_saturation")
private external fun FilaColorGradingBuilder_saturation(builder: NativePointer, saturation: Float)

@ExternalSymbolName("FilaColorGradingBuilder_shadowsMidtonesHighlights")
private external fun FilaColorGradingBuilder_shadowsMidtonesHighlights(builder: NativePointer, shadows: NativePointer, midtones: NativePointer, highlights: NativePointer, ranges: NativePointer)

@ExternalSymbolName("FilaColorGradingBuilder_slopeOffsetPower")
private external fun FilaColorGradingBuilder_slopeOffsetPower(builder: NativePointer, slope: NativePointer, offset: NativePointer, power: NativePointer)

@ExternalSymbolName("FilaColorGradingBuilder_toneMapper")
private external fun FilaColorGradingBuilder_toneMapper(builder: NativePointer, toneMapper: NativePointer)

@ExternalSymbolName("FilaColorGradingBuilder_vibrance")
private external fun FilaColorGradingBuilder_vibrance(builder: NativePointer, vibrance: Float)

@ExternalSymbolName("FilaColorGradingBuilder_whiteBalance")
private external fun FilaColorGradingBuilder_whiteBalance(builder: NativePointer, temperature: Float, tint: Float)
