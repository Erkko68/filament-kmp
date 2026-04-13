package io.github.erkko68.filament

import com.google.android.filament.ColorGrading as FilamentColorGrading

actual class ColorGrading internal constructor(internal val nativeColorGrading: FilamentColorGrading) {
    actual class Builder actual constructor() {
        private val nativeBuilder = FilamentColorGrading.Builder()

        actual fun quality(qualityLevel: QualityLevel): Builder {
            nativeBuilder.quality(FilamentColorGrading.QualityLevel.entries[qualityLevel.ordinal])
            return this
        }

        actual fun toneMapper(toneMapper: ToneMapper): Builder {
            nativeBuilder.toneMapper(toneMapper.nativeToneMapper)
            return this
        }

        actual fun luminanceScaling(luminanceScaling: Boolean): Builder {
            nativeBuilder.luminanceScaling(luminanceScaling)
            return this
        }

        actual fun gamutMapping(gamutMapping: Boolean): Builder {
            nativeBuilder.gamutMapping(gamutMapping)
            return this
        }

        actual fun exposure(exposure: Float): Builder {
            nativeBuilder.exposure(exposure)
            return this
        }

        actual fun nightAdaptation(adaptation: Float): Builder {
            nativeBuilder.nightAdaptation(adaptation)
            return this
        }

        actual fun whiteBalance(temperature: Float, tint: Float): Builder {
            nativeBuilder.whiteBalance(temperature, tint)
            return this
        }

        actual fun channelMixer(outRed: FloatArray, outGreen: FloatArray, outBlue: FloatArray): Builder {
            nativeBuilder.channelMixer(outRed, outGreen, outBlue)
            return this
        }

        actual fun shadowsMidtonesHighlights(shadows: FloatArray, midtones: FloatArray, highlights: FloatArray, ranges: FloatArray): Builder {
            nativeBuilder.shadowsMidtonesHighlights(shadows, midtones, highlights, ranges)
            return this
        }

        actual fun slopeOffsetPower(slope: FloatArray, offset: FloatArray, power: FloatArray): Builder {
            nativeBuilder.slopeOffsetPower(slope, offset, power)
            return this
        }

        actual fun contrast(contrast: Float): Builder {
            nativeBuilder.contrast(contrast)
            return this
        }

        actual fun vibrance(vibrance: Float): Builder {
            nativeBuilder.vibrance(vibrance)
            return this
        }

        actual fun saturation(saturation: Float): Builder {
            nativeBuilder.saturation(saturation)
            return this
        }

        actual fun curves(shadowGamma: FloatArray, midPoint: FloatArray, highlightScale: FloatArray): Builder {
            nativeBuilder.curves(shadowGamma, midPoint, highlightScale)
            return this
        }

        actual fun build(engine: Engine): ColorGrading {
            return ColorGrading(nativeBuilder.build(engine.nativeEngine))
        }
    }

    actual enum class QualityLevel { LOW, MEDIUM, HIGH, ULTRA }
}
