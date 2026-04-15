package io.github.erkko68.filament

import io.github.erkko68.filament.jni.ColorGrading as JniColorGrading

actual class ColorGrading(val nativeColorGrading: JniColorGrading) {
    actual enum class QualityLevel { LOW, MEDIUM, HIGH, ULTRA }

    actual class Builder actual constructor() {
        private val jni = JniColorGrading.Builder()

        actual fun quality(qualityLevel: QualityLevel): Builder {
            jni.quality(JniColorGrading.QualityLevel.values()[qualityLevel.ordinal])
            return this
        }

        actual fun toneMapper(toneMapper: ToneMapper): Builder {
            jni.toneMapper(toneMapper.nativeToneMapper)
            return this
        }

        actual fun luminanceScaling(luminanceScaling: Boolean): Builder {
            jni.luminanceScaling(luminanceScaling)
            return this
        }

        actual fun gamutMapping(gamutMapping: Boolean): Builder {
            jni.gamutMapping(gamutMapping)
            return this
        }

        actual fun exposure(exposure: Float): Builder {
            jni.exposure(exposure)
            return this
        }

        actual fun nightAdaptation(adaptation: Float): Builder {
            jni.nightAdaptation(adaptation)
            return this
        }

        actual fun whiteBalance(temperature: Float, tint: Float): Builder {
            jni.whiteBalance(temperature, tint)
            return this
        }

        actual fun channelMixer(outRed: FloatArray, outGreen: FloatArray, outBlue: FloatArray): Builder {
            jni.channelMixer(outRed, outGreen, outBlue)
            return this
        }

        actual fun shadowsMidtonesHighlights(shadows: FloatArray, midtones: FloatArray, highlights: FloatArray, ranges: FloatArray): Builder {
            jni.shadowsMidtonesHighlights(shadows, midtones, highlights, ranges)
            return this
        }

        actual fun slopeOffsetPower(slope: FloatArray, offset: FloatArray, power: FloatArray): Builder {
            jni.slopeOffsetPower(slope, offset, power)
            return this
        }

        actual fun contrast(contrast: Float): Builder {
            jni.contrast(contrast)
            return this
        }

        actual fun vibrance(vibrance: Float): Builder {
            jni.vibrance(vibrance)
            return this
        }

        actual fun saturation(saturation: Float): Builder {
            jni.saturation(saturation)
            return this
        }

        actual fun curves(shadowGamma: FloatArray, midPoint: FloatArray, highlightScale: FloatArray): Builder {
            jni.curves(shadowGamma, midPoint, highlightScale)
            return this
        }

        actual fun build(engine: Engine): ColorGrading = ColorGrading(jni.build(engine.nativeEngine))
    }
}
