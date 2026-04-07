package dev.filament.kmp

import com.google.android.filament.ColorGrading as AndroidColorGrading

actual class ColorGrading internal constructor(val nativeColorGrading: AndroidColorGrading) {
    actual class Builder actual constructor() {
        private val nativeBuilder = AndroidColorGrading.Builder()

        actual fun quality(qualityLevel: QualityLevel): Builder {
            nativeBuilder.quality(AndroidColorGrading.QualityLevel.values()[qualityLevel.ordinal])
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
        actual fun build(engine: Engine): ColorGrading = ColorGrading(nativeBuilder.build(engine.nativeEngine))
    }
    
    actual enum class QualityLevel { LOW, MEDIUM, HIGH, ULTRA }
}
