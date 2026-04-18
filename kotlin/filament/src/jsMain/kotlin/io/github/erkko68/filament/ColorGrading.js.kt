package io.github.erkko68.filament

import io.github.erkko68.filament.js.ColorGrading as JSColorGrading
import io.github.erkko68.filament.js.`ColorGrading_Builder` as JSColorGradingBuilder
import io.github.erkko68.filament.js.ColorGrading_QualityLevel

actual class ColorGrading(internal val jsColorGrading: JSColorGrading) {
    actual class Builder {
        private val jsBuilder = JSColorGradingBuilder()

        actual fun quality(qualityLevel: QualityLevel): Builder {
            jsBuilder.quality(when (qualityLevel) {
                QualityLevel.LOW -> ColorGrading_QualityLevel.LOW
                QualityLevel.MEDIUM -> ColorGrading_QualityLevel.MEDIUM
                QualityLevel.HIGH -> ColorGrading_QualityLevel.HIGH
                QualityLevel.ULTRA -> ColorGrading_QualityLevel.ULTRA
            })
            return this
        }

        actual fun toneMapper(toneMapper: ToneMapper): Builder {
            jsBuilder.toneMapping(toneMapper.jsToneMapping)
            return this
        }

        actual fun luminanceScaling(luminanceScaling: Boolean): Builder {
            jsBuilder.luminanceScaling(luminanceScaling)
            return this
        }

        actual fun gamutMapping(gamutMapping: Boolean): Builder {
            jsBuilder.gamutMapping(gamutMapping)
            return this
        }

        actual fun exposure(exposure: Float): Builder {
            jsBuilder.exposure(exposure)
            return this
        }

        actual fun nightAdaptation(adaptation: Float): Builder {
            jsBuilder.nightAdaptation(adaptation > 0.5f) // JS might take bool
            return this
        }

        actual fun whiteBalance(
            temperature: Float,
            tint: Float
        ): Builder {
            jsBuilder.whiteBalance(temperature, tint)
            return this
        }

        actual fun channelMixer(
            outRed: FloatArray,
            outGreen: FloatArray,
            outBlue: FloatArray
        ): Builder {
            jsBuilder.channelMixer(
                outRed.toTypedArray() as Array<Number>,
                outGreen.toTypedArray() as Array<Number>,
                outBlue.toTypedArray() as Array<Number>
            )
            return this
        }

        actual fun shadowsMidtonesHighlights(
            shadows: FloatArray,
            midtones: FloatArray,
            highlights: FloatArray,
            ranges: FloatArray
        ): Builder {
            jsBuilder.shadowsMidtonesHighlights(
                shadows.toTypedArray() as Array<Number>,
                midtones.toTypedArray() as Array<Number>,
                highlights.toTypedArray() as Array<Number>,
                ranges.toTypedArray() as Array<Number>
            )
            return this
        }

        actual fun slopeOffsetPower(
            slope: FloatArray,
            offset: FloatArray,
            power: FloatArray
        ): Builder {
            jsBuilder.slopeOffsetPower(
                slope.toTypedArray() as Array<Number>,
                offset.toTypedArray() as Array<Number>,
                power.toTypedArray() as Array<Number>
            )
            return this
        }

        actual fun contrast(contrast: Float): Builder {
            jsBuilder.contrast(contrast)
            return this
        }

        actual fun vibrance(vibrance: Float): Builder {
            jsBuilder.vibrance(vibrance)
            return this
        }

        actual fun saturation(saturation: Float): Builder {
            jsBuilder.saturation(saturation)
            return this
        }

        actual fun curves(
            shadowGamma: FloatArray,
            midPoint: FloatArray,
            highlightScale: FloatArray
        ): Builder {
            jsBuilder.curves(
                shadowGamma.toTypedArray() as Array<Number>,
                midPoint.toTypedArray() as Array<Number>,
                highlightScale.toTypedArray() as Array<Number>
            )
            return this
        }

        actual fun build(engine: Engine): ColorGrading {
            return ColorGrading(jsBuilder.build(engine.jsEngine))
        }
    }

    actual enum class QualityLevel { LOW, MEDIUM, HIGH, ULTRA }
}