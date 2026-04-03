package dev.filament.kmp

actual open class ToneMapper {
    actual fun getNativeObject(): Long = TODO("Not yet implemented")

    actual class Linear actual constructor() : ToneMapper()

    actual class ACES actual constructor() : ToneMapper()

    actual class ACESLegacy actual constructor() : ToneMapper()

    actual class Filmic actual constructor() : ToneMapper()

    actual class PBRNeutralToneMapper actual constructor() : ToneMapper()

    actual class GT7ToneMapper actual constructor() : ToneMapper()

    actual class Agx actual constructor(look: AgxLook) : ToneMapper() {
        actual enum class AgxLook {
            NONE,
            PUNCHY,
            GOLDEN,
        }
    }

    actual class Generic actual constructor(
        contrast: Float,
        midGrayIn: Float,
        midGrayOut: Float,
        hdrMax: Float,
    ) : ToneMapper() {
        actual fun getContrast(): Float = TODO("Not yet implemented")

        actual fun setContrast(contrast: Float) {
            TODO("Not yet implemented")
        }

        actual fun getMidGrayIn(): Float = TODO("Not yet implemented")

        actual fun setMidGrayIn(midGrayIn: Float) {
            TODO("Not yet implemented")
        }

        actual fun getMidGrayOut(): Float = TODO("Not yet implemented")

        actual fun setMidGrayOut(midGrayOut: Float) {
            TODO("Not yet implemented")
        }

        actual fun getHdrMax(): Float = TODO("Not yet implemented")

        actual fun setHdrMax(hdrMax: Float) {
            TODO("Not yet implemented")
        }
    }
}

