@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaColorGrading

actual class ColorGrading internal constructor(internal var nativeHandle: CPointer<FilaColorGrading>?) {
    actual class Builder actual constructor() {
        private val nativeBuilder = FilaColorGradingBuilder_create()

        actual fun quality(qualityLevel: QualityLevel): Builder {
            FilaColorGradingBuilder_quality(nativeBuilder, qualityLevel.ordinal.toUInt())
            return this
        }
        
        actual fun toneMapping(toneMapping: ToneMapping): Builder {
            FilaColorGradingBuilder_toneMapping(nativeBuilder, toneMapping.ordinal.toUInt())
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
        
        actual fun build(engine: Engine): ColorGrading {
            val handle = FilaColorGradingBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaColorGradingBuilder_destroy(nativeBuilder)
            return ColorGrading(handle)
        }
    }

    actual enum class QualityLevel { LOW, MEDIUM, HIGH, ULTRA }
    actual enum class ToneMapping { LINEAR, ACES_LEGACY, ACES, FILMIC, DISPLAY_RANGE }
}
