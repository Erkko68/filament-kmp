package io.github.erkko68.filament

actual class ColorGrading {
    actual class Builder {
        actual fun quality(qualityLevel: QualityLevel): Builder {
            TODO("Not yet implemented")
        }

        actual fun toneMapper(toneMapper: ToneMapper): Builder {
            TODO("Not yet implemented")
        }

        actual fun luminanceScaling(luminanceScaling: Boolean): Builder {
            TODO("Not yet implemented")
        }

        actual fun gamutMapping(gamutMapping: Boolean): Builder {
            TODO("Not yet implemented")
        }

        actual fun exposure(exposure: Float): Builder {
            TODO("Not yet implemented")
        }

        actual fun nightAdaptation(adaptation: Float): Builder {
            TODO("Not yet implemented")
        }

        actual fun whiteBalance(
            temperature: Float,
            tint: Float
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun channelMixer(
            outRed: FloatArray,
            outGreen: FloatArray,
            outBlue: FloatArray
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun shadowsMidtonesHighlights(
            shadows: FloatArray,
            midtones: FloatArray,
            highlights: FloatArray,
            ranges: FloatArray
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun slopeOffsetPower(
            slope: FloatArray,
            offset: FloatArray,
            power: FloatArray
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun contrast(contrast: Float): Builder {
            TODO("Not yet implemented")
        }

        actual fun vibrance(vibrance: Float): Builder {
            TODO("Not yet implemented")
        }

        actual fun saturation(saturation: Float): Builder {
            TODO("Not yet implemented")
        }

        actual fun curves(
            shadowGamma: FloatArray,
            midPoint: FloatArray,
            highlightScale: FloatArray
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun build(engine: Engine): ColorGrading {
            TODO("Not yet implemented")
        }
    }

    actual enum class QualityLevel { LOW, MEDIUM, HIGH, ULTRA }
}