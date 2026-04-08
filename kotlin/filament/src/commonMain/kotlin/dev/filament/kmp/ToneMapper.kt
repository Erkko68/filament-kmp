package dev.filament.kmp

/**
 * Interface for tone mapping operators. A tone mapping operator, or tone mapper,
 * is responsible for compressing the dynamic range of the rendered scene to a
 * dynamic range suitable for display.
 */
expect open class ToneMapper {
    class Linear() : ToneMapper
    class ACES() : ToneMapper
    class ACESLegacy() : ToneMapper
    class Filmic() : ToneMapper
    class PBRNeutralToneMapper() : ToneMapper
    class GT7ToneMapper() : ToneMapper
    
    class Agx(look: AgxLook = AgxLook.NONE) : ToneMapper {
        enum class AgxLook { NONE, PUNCHY, GOLDEN }
    }
    
    class Generic(
        contrast: Float = 1.55f,
        midGrayIn: Float = 0.18f,
        midGrayOut: Float = 0.215f,
        hdrMax: Float = 10.0f
    ) : ToneMapper {
        var contrast: Float
        var midGrayIn: Float
        var midGrayOut: Float
        var hdrMax: Float
    }
    
    class DisplayRange() : ToneMapper
}
