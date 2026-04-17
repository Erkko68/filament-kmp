package io.github.erkko68.filament

actual open class ToneMapper {
    actual class Linear : ToneMapper()
    actual class ACES : ToneMapper()
    actual class ACESLegacy : ToneMapper()
    actual class Filmic : ToneMapper()
    actual class PBRNeutralToneMapper : ToneMapper()
    actual class GT7ToneMapper : ToneMapper()
    actual class Agx actual constructor(look: AgxLook) :
        ToneMapper() {
        actual enum class AgxLook { NONE, PUNCHY, GOLDEN }
    }

    actual class Generic actual constructor(
        contrast: Float,
        midGrayIn: Float,
        midGrayOut: Float,
        hdrMax: Float
    ) : ToneMapper() {
        actual var contrast: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var midGrayIn: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var midGrayOut: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var hdrMax: Float
            get() = TODO("Not yet implemented")
            set(value) {}
    }

    actual class DisplayRange : ToneMapper()
}