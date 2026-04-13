package io.github.erkko68

expect class ColorGrading {
    class Builder() {
        fun quality(qualityLevel: QualityLevel): Builder
        fun toneMapper(toneMapper: ToneMapper): Builder
        fun luminanceScaling(luminanceScaling: Boolean): Builder
        fun gamutMapping(gamutMapping: Boolean): Builder
        fun exposure(exposure: Float): Builder
        fun nightAdaptation(adaptation: Float): Builder
        fun whiteBalance(temperature: Float, tint: Float): Builder
        fun channelMixer(outRed: FloatArray, outGreen: FloatArray, outBlue: FloatArray): Builder
        fun shadowsMidtonesHighlights(shadows: FloatArray, midtones: FloatArray, highlights: FloatArray, ranges: FloatArray): Builder
        fun slopeOffsetPower(slope: FloatArray, offset: FloatArray, power: FloatArray): Builder
        fun contrast(contrast: Float): Builder
        fun vibrance(vibrance: Float): Builder
        fun saturation(saturation: Float): Builder
        fun curves(shadowGamma: FloatArray, midPoint: FloatArray, highlightScale: FloatArray): Builder
        fun build(engine: Engine): ColorGrading
    }

    enum class QualityLevel { LOW, MEDIUM, HIGH, ULTRA }
}
