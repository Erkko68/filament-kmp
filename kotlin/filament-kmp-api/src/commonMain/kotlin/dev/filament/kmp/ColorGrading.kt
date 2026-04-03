package dev.filament.kmp

/**
 * <code>ColorGrading</code> is used to transform (either to modify or correct) the colors of the
 * HDR buffer rendered by Filament. Color grading transforms are applied after lighting, and after any
 * lens effects (bloom for instance), and include tone mapping.
 *
 * <h1>Creation, usage and destruction</h1>
 *
 * A ColorGrading object is created using the ColorGrading::Builder and destroyed by calling
 * Engine::destroy(const ColorGrading*). A ColorGrading object is meant to be set on a View.
 *
 * <pre>
 *  Engine engine = Engine.create();
 *
 *  ColorGrading colorGrading = ColorGrading.Builder()
 *              .toneMapping(ColorGrading.ToneMapping.ACES)
 *              .build(engine);
 *
 *  myView.setColorGrading(colorGrading);
 *
 *  engine.destroy(colorGrading);
 * </pre>
 *
 * <h1>Performance</h1>
 *
 * Creating a new ColorGrading object may be more expensive than other Filament objects as a LUT may
 * need to be generated. The generation of this LUT, if necessary, may happen on the CPU.
 *
 * <h1>Ordering</h1>
 *
 * The various transforms held by ColorGrading are applied in the following order:
 * <ul>
 * <li>Exposure</li>
 * <li>Night adaptation</li>
 * <li>White balance</li>
 * <li>Channel mixer</li>
 * <li>Shadows/mid-tones/highlights</li>
 * <li>Slope/offset/power (CDL)</li>
 * <li>Contrast</li>
 * <li>Vibrance</li>
 * <li>Saturation</li>
 * <li>Curves</li>
 * <li>Tone mapping</li>
 * <li>Luminance scaling</li>
 * <li>Gamut mapping</li>
 * </ul>
 *
 * <h1>Defaults</h1>
 *
 * Here are the default color grading options:
 * <ul>
 * <li>Exposure: 0.0</li>
 * <li>Night adaptation: 0.0</li>
 * <li>White balance: temperature <code>0.0</code>, and tint <code>0.0</code></li>
 * <li>Channel mixer: red <code>{1,0,0}</code>, green <code>{0,1,0}</code>, blue <code>{0,0,1}</code></li>
 * <li>Shadows/mid-tones/highlights: shadows <code>{1,1,1,0}</code>, mid-tones <code>{1,1,1,0}</code>,
 * highlights <code>{1,1,1,0}</code>, ranges <code>{0,0.333,0.550,1}</code></li>
 * <li>Slope/offset/power: slope <code>1.0</code>, offset <code>0.0</code>, and power <code>1.0</code></li>
 * <li>Contrast: <code>1.0</code></li>
 * <li>Vibrance: <code>1.0</code></li>
 * <li>Saturation: <code>1.0</code></li>
 * <li>Curves: gamma <code>{1,1,1}</code>, midPoint <code>{1,1,1}</code>, and scale <code>{1,1,1}</code></li>
 * <li>Tone mapping: {@link ToneMapper.ACESLegacy}</li>
 * <li>Luminance scaling: false</li>
 * <li>Gamut mapping: false</li>
 * </ul>
 *
 * @see View
 * @see ToneMapper
 */
expect class ColorGrading {
    /**
     * Color grading quality level.
     */
    enum class QualityLevel {
        LOW,
        MEDIUM,
        HIGH,
        ULTRA,
    }

    /**
     * Color grading LUT format.
     */
    enum class LutFormat {
        INTEGER,
        FLOAT,
    }

    /**
     * List of available tone-mapping operators.
     */
    @Deprecated("Use ColorGrading.Builder.toneMapper(toneMapper)")
    enum class ToneMapping {
        LINEAR,
        ACES_LEGACY,
        ACES,
        FILMIC,
        DISPLAY_RANGE,
    }

    /**
     * Use <code>Builder</code> to construct a <code>ColorGrading</code> object instance.
     */
    class Builder {
        /**
         * Sets the quality level of the color grading.
         *
         * This setting has no effect if generating a 1D LUT.
         *
         * The default quality is {@link QualityLevel#MEDIUM}.
         *
         * @param qualityLevel The desired quality of the color grading process
         *
         * @return This Builder, for chaining calls
         */
        fun quality(qualityLevel: QualityLevel): Builder

        /**
         * When color grading is implemented using a 3D LUT, this sets the texture format of
         * of the LUT.
         *
         * This setting has no effect if generating a 1D LUT.
         *
         * The default is INTEGER
         *
         * @param format The desired format of the 3D LUT.
         *
         * @return This Builder, for chaining calls
         */
        fun format(format: LutFormat): Builder

        /**
         * When color grading is implemented using a 3D LUT, this sets the dimension of the LUT.
         *
         * This setting has no effect if generating a 1D LUT.
         *
         * The default is 32
         *
         * @param dim The desired dimension of the LUT. Between 16 and 64.
         *
         * @return This Builder, for chaining calls
         */
        fun dimensions(dim: Int): Builder

        /**
         * Selects the tone mapping operator to apply to the HDR color buffer.
         *
         * The default tone mapping operator is {@link ToneMapper.ACESLegacy}.
         *
         * @param toneMapper The tone mapping operator to apply to the HDR color buffer
         *
         * @return This Builder, for chaining calls
         */
        fun toneMapper(toneMapper: ToneMapper): Builder

        /**
         * Selects the tone mapping operator to apply to the HDR color buffer.
         *
         * @deprecated Use {@link #toneMapper(ToneMapper)}
         */
        @Deprecated("Use toneMapper(toneMapper)")
        fun toneMapping(toneMapping: ToneMapping): Builder

        /**
         * Enables or disables the luminance scaling component (LICH) from the exposure value
         * invariant luminance system (EVILS).
         *
         * @param luminanceScaling Enables or disables EVILS post-tone mapping
         *
         * @return This Builder, for chaining calls
         */
        fun luminanceScaling(luminanceScaling: Boolean): Builder

        /**
         * Enables or disables gamut mapping to the destination color space's gamut.
         *
         * @param gamutMapping Enables or disables gamut mapping
         *
         * @return This Builder, for chaining calls
         */
        fun gamutMapping(gamutMapping: Boolean): Builder

        /**
         * Adjusts the exposure of this image.
         *
         * @param exposure Value in EV stops. Can be negative, 0, or positive.
         *
         * @return This Builder, for chaining calls
         */
        fun exposure(exposure: Float): Builder

        /**
         * Controls the amount of night adaptation.
         *
         * @param adaptation Amount of adaptation, between 0 (no adaptation) and 1 (full adaptation).
         *
         * @return This Builder, for chaining calls
         */
        fun nightAdaptation(adaptation: Float): Builder

        /**
         * Adjusts the while balance of the image.
         *
         * @param temperature Modification on the blue/yellow axis, as a value between -1.0 and +1.0.
         * @param tint Modification on the green/magenta axis, as a value between -1.0 and +1.0.
         *
         * @return This Builder, for chaining calls
         */
        fun whiteBalance(temperature: Float, tint: Float): Builder

        /**
         * The channel mixer adjustment modifies each output color channel using the specified
         * mix of the source color channels.
         *
         * @param outRed The mix of source RGB for the output red channel, between -2.0 and +2.0
         * @param outGreen The mix of source RGB for the output green channel, between -2.0 and +2.0
         * @param outBlue The mix of source RGB for the output blue channel, between -2.0 and +2.0
         *
         * @return This Builder, for chaining calls
         */
        fun channelMixer(outRed: FloatArray, outGreen: FloatArray, outBlue: FloatArray): Builder

        /**
         * Adjusts the colors separately in 3 distinct tonal ranges or zones: shadows, mid-tones,
         * and highlights.
         *
         * @param shadows Linear RGB color (.rgb) and weight (.w) to apply to the shadows
         * @param midtones Linear RGB color (.rgb) and weight (.w) to apply to the mid-tones
         * @param highlights Linear RGB color (.rgb) and weight (.w) to apply to the highlights
         * @param ranges Range of the shadows (x and y), and range of the highlights (z and w)
         *
         * @return This Builder, for chaining calls
         */
        fun shadowsMidtonesHighlights(shadows: FloatArray, midtones: FloatArray, highlights: FloatArray, ranges: FloatArray): Builder

        /**
         * Applies a slope, offset, and power, as defined by the ASC CDL.
         *
         * @param slope Multiplier of the input color, must be a strictly positive number
         * @param offset Added to the input color, can be a negative or positive number, including 0
         * @param power Power exponent of the input color, must be a strictly positive number
         *
         * @return This Builder, for chaining calls
         */
        fun slopeOffsetPower(slope: FloatArray, offset: FloatArray, power: FloatArray): Builder

        /**
         * Adjusts the contrast of the image.
         *
         * @param contrast Contrast expansion, between 0.0 and 2.0. 1.0 leaves contrast unaffected
         *
         * @return This Builder, for chaining calls
         */
        fun contrast(contrast: Float): Builder

        /**
         * Adjusts the saturation of the image based on the input color's saturation level.
         *
         * @param vibrance Vibrance, between 0.0 and 2.0. 1.0 leaves vibrance unaffected
         *
         * @return This Builder, for chaining calls
         */
        fun vibrance(vibrance: Float): Builder

        /**
         * Adjusts the saturation of the image.
         *
         * @param saturation Saturation, between 0.0 and 2.0. 1.0 leaves saturation unaffected
         *
         * @return This Builder, for chaining calls
         */
        fun saturation(saturation: Float): Builder

        /**
         * Applies a curve to each RGB channel of the image.
         *
         * @param shadowGamma Power value to apply to the shadows, must be strictly positive
         * @param midPoint Mid-point defining where shadows stop and highlights start, must be strictly positive
         * @param highlightScale Scale factor for the highlights, can be any negative or positive value
         *
         * @return This Builder, for chaining calls
         */
        fun curves(shadowGamma: FloatArray, midPoint: FloatArray, highlightScale: FloatArray): Builder

        /**
         * Creates the IndirectLight object and returns a pointer to it.
         *
         * @param engine The {@link Engine} to associate this <code>IndirectLight</code> with.
         *
         * @return A newly created <code>IndirectLight</code>
         *
         * @exception IllegalStateException if a parameter to a builder function was invalid.
         */
        fun build(engine: Engine): ColorGrading
    }

    fun getNativeObject(): Long

    internal fun invalidate()
}

