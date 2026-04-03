package dev.filament.kmp

import com.google.android.filament.ColorGrading as AndroidColorGrading

actual class ColorGrading internal constructor(
    internal var androidColorGrading: AndroidColorGrading?,
) {
    actual fun getNativeObject(): Long {
        val colorGrading = requireNotNull(androidColorGrading) { "Calling method on destroyed ColorGrading" }
        return colorGrading.nativeObject
    }

    actual internal fun invalidate() {
        androidColorGrading = null
    }

    actual class Builder {
        private val androidBuilder = AndroidColorGrading.Builder()

        actual fun quality(qualityLevel: QualityLevel): Builder {
            androidBuilder.quality(AndroidColorGrading.QualityLevel.valueOf(qualityLevel.name))
            return this
        }

        actual fun format(format: LutFormat): Builder {
            androidBuilder.format(AndroidColorGrading.LutFormat.valueOf(format.name))
            return this
        }

        actual fun dimensions(dim: Int): Builder {
            androidBuilder.dimensions(dim)
            return this
        }

        actual fun toneMapper(toneMapper: ToneMapper): Builder {
            androidBuilder.toneMapper(toneMapper.androidToneMapper)
            return this
        }

        actual fun toneMapping(toneMapping: ToneMapping): Builder {
            androidBuilder.toneMapping(AndroidColorGrading.ToneMapping.valueOf(toneMapping.name))
            return this
        }

        actual fun luminanceScaling(luminanceScaling: Boolean): Builder {
            androidBuilder.luminanceScaling(luminanceScaling)
            return this
        }

        actual fun gamutMapping(gamutMapping: Boolean): Builder {
            androidBuilder.gamutMapping(gamutMapping)
            return this
        }

        actual fun exposure(exposure: Float): Builder {
            androidBuilder.exposure(exposure)
            return this
        }

        actual fun nightAdaptation(adaptation: Float): Builder {
            androidBuilder.nightAdaptation(adaptation)
            return this
        }

        actual fun whiteBalance(temperature: Float, tint: Float): Builder {
            androidBuilder.whiteBalance(temperature, tint)
            return this
        }

        actual fun channelMixer(outRed: FloatArray, outGreen: FloatArray, outBlue: FloatArray): Builder {
            androidBuilder.channelMixer(outRed, outGreen, outBlue)
            return this
        }

        actual fun shadowsMidtonesHighlights(shadows: FloatArray, midtones: FloatArray, highlights: FloatArray, ranges: FloatArray): Builder {
            androidBuilder.shadowsMidtonesHighlights(shadows, midtones, highlights, ranges)
            return this
        }

        actual fun slopeOffsetPower(slope: FloatArray, offset: FloatArray, power: FloatArray): Builder {
            androidBuilder.slopeOffsetPower(slope, offset, power)
            return this
        }

        actual fun contrast(contrast: Float): Builder {
            androidBuilder.contrast(contrast)
            return this
        }

        actual fun vibrance(vibrance: Float): Builder {
            androidBuilder.vibrance(vibrance)
            return this
        }

        actual fun saturation(saturation: Float): Builder {
            androidBuilder.saturation(saturation)
            return this
        }

        actual fun curves(shadowGamma: FloatArray, midPoint: FloatArray, highlightScale: FloatArray): Builder {
            androidBuilder.curves(shadowGamma, midPoint, highlightScale)
            return this
        }

        actual fun build(engine: Engine): ColorGrading {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return ColorGrading(androidBuilder.build(androidEngine))
        }
    }

    actual enum class QualityLevel {
        LOW,
        MEDIUM,
        HIGH,
        ULTRA,
    }

    actual enum class LutFormat {
        INTEGER,
        FLOAT,
    }

    @Deprecated("Use ColorGrading.Builder.toneMapper(toneMapper)")
    actual enum class ToneMapping {
        LINEAR,
        ACES_LEGACY,
        ACES,
        FILMIC,
        DISPLAY_RANGE,
    }
}

