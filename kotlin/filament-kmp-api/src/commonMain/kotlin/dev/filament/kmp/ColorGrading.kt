package dev.filament.kmp

/**
 * ColorGrading is used to transform (either to modify or correct) the colors of the
 * HDR buffer rendered by Filament.
 */
expect class ColorGrading {

    /**
     * Color grading quality level.
     */
    enum class QualityLevel {
        LOW,
        MEDIUM,
        HIGH,
        ULTRA
    }

    /**
     * Color grading LUT format.
     */
    enum class LutFormat {
        INTEGER,
        FLOAT
    }

    /**
     * List of available tone-mapping operators.
     * @deprecated Use ColorGrading.Builder.toneMapper(ToneMapper)
     */
    @Deprecated("Use toneMapper instead")
    enum class ToneMapping {
        LINEAR,
        ACES_LEGACY,
        ACES,
        FILMIC,
        DISPLAY_RANGE,
    }

    class Builder() {
        /**
         * Sets the quality level of the color grading.
         */
        fun quality(qualityLevel: QualityLevel): Builder

        /**
         * When color grading is implemented using a 3D LUT, this sets the texture format.
         */
        fun format(format: LutFormat): Builder

        /**
         * When color grading is implemented using a 3D LUT, this sets the dimension of the LUT.
         */
        fun dimensions(dim: Int): Builder

        /**
         * Selects the tone mapping operator to apply.
         */
        fun toneMapper(toneMapper: ToneMapper): Builder

        /**
         * Selects the tone mapping operator to apply.
         * @deprecated Use toneMapper(ToneMapper)
         */
        @Deprecated("Use toneMapper instead")
        fun toneMapping(toneMapping: ToneMapping): Builder

        /**
         * Enables or disables the luminance scaling component (LICH).
         */
        fun luminanceScaling(luminanceScaling: Boolean): Builder

        /**
         * Enables or disables gamut mapping to the destination color space's gamut.
         */
        fun gamutMapping(gamutMapping: Boolean): Builder

        /**
         * Adjusts the exposure of this image.
         */
        fun exposure(exposure: Float): Builder

        /**
         * Controls the amount of night adaptation.
         */
        fun nightAdaptation(adaptation: Float): Builder

        /**
         * Adjusts the while balance of the image.
         */
        fun whiteBalance(temperature: Float, tint: Float): Builder

        /**
         * The channel mixer adjustment modifies each output color channel.
         */
        fun channelMixer(outRed: FloatArray, outGreen: FloatArray, outBlue: FloatArray): Builder

        /**
         * Adjusts the colors separately in 3 distinct tonal ranges: shadows, mid-tones, and highlights.
         */
        fun shadowsMidtonesHighlights(shadows: FloatArray, midtones: FloatArray, highlights: FloatArray, ranges: FloatArray): Builder

        /**
         * Applies a slope, offset, and power (ASC CDL).
         */
        fun slopeOffsetPower(slope: FloatArray, offset: FloatArray, power: FloatArray): Builder

        /**
         * Adjusts the contrast of the image.
         */
        fun contrast(contrast: Float): Builder

        /**
         * Adjusts the saturation of the image based on the input color's saturation level.
         */
        fun vibrance(vibrance: Float): Builder

        /**
         * Adjusts the saturation of the image.
         */
        fun saturation(saturation: Float): Builder

        /**
         * Applies a curve to each RGB channel of the image.
         */
        fun curves(shadowGamma: FloatArray, midPoint: FloatArray, highlightScale: FloatArray): Builder

        /**
         * Creates the ColorGrading object.
         */
        fun build(engine: Engine): ColorGrading
    }
}
