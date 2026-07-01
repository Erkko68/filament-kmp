package io.github.erkko68.filament

import io.github.erkko68.filament.web.interop.jsNumbers
import io.github.erkko68.filament.web.interop.toJsNumbers

import io.github.erkko68.filament.web.ColorGrading as JSColorGrading
import io.github.erkko68.filament.web.`ColorGrading_Builder` as JSColorGradingBuilder
import io.github.erkko68.filament.web.ColorGrading_QualityLevel

actual class ColorGrading(internal val jsColorGrading: JSColorGrading) {
    actual class Builder {
        private val jsBuilder = JSColorGrading.Builder()

        actual fun quality(qualityLevel: QualityLevel): Builder {
            jsBuilder.quality(when (qualityLevel) {
                QualityLevel.LOW -> ColorGrading_QualityLevel.LOW
                QualityLevel.MEDIUM -> ColorGrading_QualityLevel.MEDIUM
                QualityLevel.HIGH -> ColorGrading_QualityLevel.HIGH
                QualityLevel.ULTRA -> ColorGrading_QualityLevel.ULTRA
            })
            return this
        }

        actual fun format(format: LutFormat): Builder {
            val jsFormat = when (format) {
                LutFormat.INTEGER -> io.github.erkko68.filament.web.ColorGrading_LutFormat.INTEGER
                LutFormat.FLOAT -> io.github.erkko68.filament.web.ColorGrading_LutFormat.FLOAT
            }
            jsBuilder.format(jsFormat)
            return this
        }

        actual fun dimensions(dim: Int): Builder {
            jsBuilder.dimensions(dim.toDouble())
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
            jsBuilder.exposure(exposure.toDouble())
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
            jsBuilder.whiteBalance(temperature.toDouble(), tint.toDouble())
            return this
        }

        actual fun channelMixer(
            outRed: FloatArray,
            outGreen: FloatArray,
            outBlue: FloatArray
        ): Builder {
            jsBuilder.channelMixer(
                outRed.toJsNumbers(),
                outGreen.toJsNumbers(),
                outBlue.toJsNumbers()
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
                shadows.toJsNumbers(),
                midtones.toJsNumbers(),
                highlights.toJsNumbers(),
                ranges.toJsNumbers()
            )
            return this
        }

        actual fun slopeOffsetPower(
            slope: FloatArray,
            offset: FloatArray,
            power: FloatArray
        ): Builder {
            jsBuilder.slopeOffsetPower(
                slope.toJsNumbers(),
                offset.toJsNumbers(),
                power.toJsNumbers()
            )
            return this
        }

        actual fun contrast(contrast: Float): Builder {
            jsBuilder.contrast(contrast.toDouble())
            return this
        }

        actual fun vibrance(vibrance: Float): Builder {
            jsBuilder.vibrance(vibrance.toDouble())
            return this
        }

        actual fun saturation(saturation: Float): Builder {
            jsBuilder.saturation(saturation.toDouble())
            return this
        }

        actual fun curves(
            shadowGamma: FloatArray,
            midPoint: FloatArray,
            highlightScale: FloatArray
        ): Builder {
            jsBuilder.curves(
                shadowGamma.toJsNumbers(),
                midPoint.toJsNumbers(),
                highlightScale.toJsNumbers()
            )
            return this
        }

        actual fun fastMath(fastMath: Boolean): Builder {
            jsBuilder.fastMath(fastMath)
            return this
        }

        actual fun build(engine: Engine): ColorGrading {
            return ColorGrading(jsBuilder.build(engine.jsEngine))
        }
    }

    actual enum class QualityLevel { LOW, MEDIUM, HIGH, ULTRA }
    actual enum class LutFormat { INTEGER, FLOAT }
}