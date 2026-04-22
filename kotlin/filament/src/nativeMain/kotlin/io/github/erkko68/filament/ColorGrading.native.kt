@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*
import cnames.structs.FilaColorGrading
import cnames.structs.FilaColorGradingBuilder

actual class ColorGrading internal constructor(internal var nativeHandle: CPointer<FilaColorGrading>?) {
    actual class Builder actual constructor() {
        private val nativeHandle: CPointer<FilaColorGradingBuilder>? = FilaColorGradingBuilder_create()

        actual fun quality(qualityLevel: QualityLevel): Builder {
            FilaColorGradingBuilder_quality(nativeHandle, qualityLevel.ordinal.toUInt())
            return this
        }

        actual fun format(format: LutFormat): Builder {
            FilaColorGradingBuilder_format(nativeHandle, format.ordinal.toUInt())
            return this
        }

        actual fun dimensions(dim: Int): Builder {
            FilaColorGradingBuilder_dimensions(nativeHandle, dim.toUByte())
            return this
        }

        actual fun toneMapper(toneMapper: ToneMapper): Builder {
            FilaColorGradingBuilder_toneMapper(nativeHandle, toneMapper.nativeHandle)
            return this
        }

        actual fun luminanceScaling(luminanceScaling: Boolean): Builder {
            FilaColorGradingBuilder_luminanceScaling(nativeHandle, luminanceScaling)
            return this
        }

        actual fun gamutMapping(gamutMapping: Boolean): Builder {
            FilaColorGradingBuilder_gamutMapping(nativeHandle, gamutMapping)
            return this
        }

        actual fun exposure(exposure: Float): Builder {
            FilaColorGradingBuilder_exposure(nativeHandle, exposure)
            return this
        }

        actual fun nightAdaptation(adaptation: Float): Builder {
            FilaColorGradingBuilder_nightAdaptation(nativeHandle, adaptation)
            return this
        }

        actual fun whiteBalance(temperature: Float, tint: Float): Builder {
            FilaColorGradingBuilder_whiteBalance(nativeHandle, temperature, tint)
            return this
        }

        actual fun channelMixer(outRed: FloatArray, outGreen: FloatArray, outBlue: FloatArray): Builder {
            memScoped {
                FilaColorGradingBuilder_channelMixer(nativeHandle, outRed.toCValues().ptr, outGreen.toCValues().ptr, outBlue.toCValues().ptr)
            }
            return this
        }

        actual fun shadowsMidtonesHighlights(shadows: FloatArray, midtones: FloatArray, highlights: FloatArray, ranges: FloatArray): Builder {
            memScoped {
                FilaColorGradingBuilder_shadowsMidtonesHighlights(nativeHandle, shadows.toCValues().ptr, midtones.toCValues().ptr, highlights.toCValues().ptr, ranges.toCValues().ptr)
            }
            return this
        }

        actual fun slopeOffsetPower(slope: FloatArray, offset: FloatArray, power: FloatArray): Builder {
            memScoped {
                FilaColorGradingBuilder_slopeOffsetPower(nativeHandle, slope.toCValues().ptr, offset.toCValues().ptr, power.toCValues().ptr)
            }
            return this
        }

        actual fun contrast(contrast: Float): Builder {
            FilaColorGradingBuilder_contrast(nativeHandle, contrast)
            return this
        }

        actual fun vibrance(vibrance: Float): Builder {
            FilaColorGradingBuilder_vibrance(nativeHandle, vibrance)
            return this
        }

        actual fun saturation(saturation: Float): Builder {
            FilaColorGradingBuilder_saturation(nativeHandle, saturation)
            return this
        }

        actual fun curves(shadowGamma: FloatArray, midPoint: FloatArray, highlightScale: FloatArray): Builder {
            memScoped {
                FilaColorGradingBuilder_curves(nativeHandle, shadowGamma.toCValues().ptr, midPoint.toCValues().ptr, highlightScale.toCValues().ptr)
            }
            return this
        }

        actual fun build(engine: Engine): ColorGrading {
            return ColorGrading(FilaColorGradingBuilder_build(nativeHandle, engine.nativeHandle))
        }
    }

    actual enum class QualityLevel { LOW, MEDIUM, HIGH, ULTRA }
    actual enum class LutFormat { INTEGER, FLOAT }
}
