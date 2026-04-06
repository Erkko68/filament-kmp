package dev.filament.kmp

/**
 * Interface for tone mapping operators.
 */
expect open class ToneMapper {

    /**
     * Linear tone mapping operator.
     */
    class Linear() : ToneMapper

    /**
     * ACES tone mapping operator.
     */
    class ACES() : ToneMapper

    /**
     * ACES tone mapping operator, modified to match the perceived brightness of FilmicToneMapper.
     */
    class ACESLegacy() : ToneMapper

    /**
     * "Filmic" tone mapping operator.
     */
    class Filmic() : ToneMapper

    /**
     * Khronos PBR Neutral tone mapping operator.
     */
    class PBRNeutralToneMapper() : ToneMapper

    /**
     * Gran Turismo 7 tone mapping operator.
     */
    class GT7ToneMapper() : ToneMapper

    /**
     * AgX tone mapping operator.
     */
    class Agx : ToneMapper {
        enum class AgxLook {
            NONE,
            PUNCHY,
            GOLDEN
        }

        constructor()
        constructor(look: AgxLook)
    }

    /**
     * Generic tone mapping operator.
     */
    class Generic : ToneMapper {
        constructor()
        constructor(contrast: Float, midGrayIn: Float, midGrayOut: Float, hdrMax: Float)

        fun getContrast(): Float
        fun setContrast(contrast: Float)
        fun getMidGrayIn(): Float
        fun setMidGrayIn(midGrayIn: Float)
        fun getMidGrayOut(): Float
        fun setMidGrayOut(midGrayOut: Float)
        fun getHdrMax(): Float
        fun setHdrMax(hdrMax: Float)
    }
}
