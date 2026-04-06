package dev.filament.kmp

import kotlinx.cinterop.*
import filament.*

actual class ColorGrading internal constructor(
    internal val nativeObject: CPointer<FilaColorGrading>
) {
    actual enum class QualityLevel {
        LOW, MEDIUM, HIGH, ULTRA;
        internal fun toNative() = when (this) {
            LOW -> FILA_COLOR_GRADING_QUALITY_LOW
            MEDIUM -> FILA_COLOR_GRADING_QUALITY_MEDIUM
            HIGH -> FILA_COLOR_GRADING_QUALITY_HIGH
            ULTRA -> FILA_COLOR_GRADING_QUALITY_ULTRA
        }
    }

    actual enum class LutFormat {
        INTEGER, FLOAT;
        internal fun toNative() = when (this) {
            INTEGER -> FILA_COLOR_GRADING_LUT_FORMAT_INTEGER
            FLOAT -> FILA_COLOR_GRADING_LUT_FORMAT_FLOAT
        }
    }

    actual enum class ToneMapping {
        LINEAR, ACES_LEGACY, ACES, FILMIC, DISPLAY_RANGE;
        internal fun toNative() = when (this) {
            LINEAR -> FILA_COLOR_GRADING_TONE_MAPPING_LINEAR
            ACES_LEGACY -> FILA_COLOR_GRADING_TONE_MAPPING_ACES_LEGACY
            ACES -> FILA_COLOR_GRADING_TONE_MAPPING_ACES
            FILMIC -> FILA_COLOR_GRADING_TONE_MAPPING_FILMIC
            DISPLAY_RANGE -> FILA_COLOR_GRADING_TONE_MAPPING_DISPLAY_RANGE
        }
    }

    actual class Builder actual constructor() {
        private val nativeBuilder = FilaColorGradingBuilder_create()!!

        actual fun quality(quality: QualityLevel): Builder {
            FilaColorGradingBuilder_quality(nativeBuilder, quality.toNative())
            return this
        }

        actual fun format(format: LutFormat): Builder {
            FilaColorGradingBuilder_format(nativeBuilder, format.toNative())
            return this
        }

        actual fun dimensions(dim: Int): Builder {
            FilaColorGradingBuilder_dimensions(nativeBuilder, dim.toUByte())
            return this
        }

        actual fun toneMapper(toneMapper: ToneMapper): Builder {
            FilaColorGradingBuilder_toneMapper(nativeBuilder, toneMapper.nativeObject)
            return this
        }

        actual fun toneMapping(toneMapping: ToneMapping): Builder {
            FilaColorGradingBuilder_toneMapping(nativeBuilder, toneMapping.toNative())
            return this
        }

        actual fun luminanceScaling(luminanceScaling: Boolean): Builder {
            FilaColorGradingBuilder_luminanceScaling(nativeBuilder, luminanceScaling)
            return this
        }

        actual fun gamutMapping(gamutMapping: Boolean): Builder {
            FilaColorGradingBuilder_gamutMapping(nativeBuilder, gamutMapping)
            return this
        }

        actual fun exposure(exposure: Float): Builder {
            FilaColorGradingBuilder_exposure(nativeBuilder, exposure)
            return this
        }

        actual fun nightAdaptation(adaptation: Float): Builder {
            FilaColorGradingBuilder_nightAdaptation(nativeBuilder, adaptation)
            return this
        }

        actual fun whiteBalance(temperature: Float, tint: Float): Builder {
            FilaColorGradingBuilder_whiteBalance(nativeBuilder, temperature, tint)
            return this
        }

        actual fun channelMixer(outRed: FloatArray, outGreen: FloatArray, outBlue: FloatArray): Builder {
            memScoped {
                FilaColorGradingBuilder_channelMixer(nativeBuilder, outRed.toCValues().ptr, outGreen.toCValues().ptr, outBlue.toCValues().ptr)
            }
            return this
        }

        actual fun shadowsMidtonesHighlights(shadows: FloatArray, midtones: FloatArray, highlights: FloatArray, ranges: FloatArray): Builder {
            memScoped {
                FilaColorGradingBuilder_shadowsMidtonesHighlights(nativeBuilder, shadows.toCValues().ptr, midtones.toCValues().ptr, highlights.toCValues().ptr, ranges.toCValues().ptr)
            }
            return this
        }

        actual fun slopeOffsetPower(slope: FloatArray, offset: FloatArray, power: FloatArray): Builder {
            memScoped {
                FilaColorGradingBuilder_slopeOffsetPower(nativeBuilder, slope.toCValues().ptr, offset.toCValues().ptr, power.toCValues().ptr)
            }
            return this
        }

        actual fun contrast(contrast: Float): Builder {
            FilaColorGradingBuilder_contrast(nativeBuilder, contrast)
            return this
        }

        actual fun vibrance(vibrance: Float): Builder {
            FilaColorGradingBuilder_vibrance(nativeBuilder, vibrance)
            return this
        }

        actual fun saturation(saturation: Float): Builder {
            FilaColorGradingBuilder_saturation(nativeBuilder, saturation)
            return this
        }

        actual fun curves(shadowGamma: FloatArray, midPoint: FloatArray, highlightScale: FloatArray): Builder {
            memScoped {
                FilaColorGradingBuilder_curves(nativeBuilder, shadowGamma.toCValues().ptr, midPoint.toCValues().ptr, highlightScale.toCValues().ptr)
            }
            return this
        }

        actual fun build(engine: Engine): ColorGrading {
            val nativeColorGrading = FilaColorGradingBuilder_build(nativeBuilder, engine.nativeObject)
                ?: throw IllegalStateException("Couldn't create ColorGrading")
            FilaColorGradingBuilder_destroy(nativeBuilder)
            return ColorGrading(nativeColorGrading)
        }
    }

    actual val nativeObject: Long get() = nativeObject.toLong()
}
