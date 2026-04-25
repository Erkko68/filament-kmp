package io.github.erkko68.filament

import io.github.erkko68.filament.js.ColorGrading_ToneMapping

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual open class ToneMapper(internal val jsToneMapping: ColorGrading_ToneMapping) {
    actual class Linear : ToneMapper(ColorGrading_ToneMapping.LINEAR)
    actual class ACES : ToneMapper(ColorGrading_ToneMapping.ACES)
    actual class ACESLegacy : ToneMapper(ColorGrading_ToneMapping.ACES_LEGACY)
    actual class Filmic : ToneMapper(ColorGrading_ToneMapping.FILMIC)
    // PBR_NEUTRAL, GT7, AGX not in JS bindings — fall back to ACES
    actual class PBRNeutralToneMapper : ToneMapper(ColorGrading_ToneMapping.ACES)
    actual class GT7ToneMapper : ToneMapper(ColorGrading_ToneMapping.ACES)
    actual class Agx actual constructor(look: AgxLook) : ToneMapper(ColorGrading_ToneMapping.ACES) {
        actual enum class AgxLook { NONE, PUNCHY, GOLDEN }
    }

    actual class Generic actual constructor(
        contrast: Float,
        midGrayIn: Float,
        midGrayOut: Float,
        hdrMax: Float
    ) : ToneMapper(ColorGrading_ToneMapping.ACES) {
        actual var contrast: Float = contrast
        actual var midGrayIn: Float = midGrayIn
        actual var midGrayOut: Float = midGrayOut
        actual var hdrMax: Float = hdrMax
    }

    actual class DisplayRange : ToneMapper(ColorGrading_ToneMapping.DISPLAY_RANGE)
}
