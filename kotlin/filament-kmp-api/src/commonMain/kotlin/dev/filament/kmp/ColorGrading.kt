package dev.filament.kmp

expect class ColorGrading {
    class Builder() {
        fun quality(qualityLevel: QualityLevel): Builder
        fun luminanceScaling(luminanceScaling: Boolean): Builder
        fun gamutMapping(gamutMapping: Boolean): Builder
        fun exposure(exposure: Float): Builder
        fun nightAdaptation(adaptation: Float): Builder
        fun whiteBalance(temperature: Float, tint: Float): Builder
        fun contrast(contrast: Float): Builder
        fun vibrance(vibrance: Float): Builder
        fun saturation(saturation: Float): Builder
        fun build(engine: Engine): ColorGrading
    }

    enum class QualityLevel { LOW, MEDIUM, HIGH, ULTRA }
}
