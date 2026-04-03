package dev.filament.kmp

import com.google.android.filament.ToneMapper as AndroidToneMapper

actual open class ToneMapper internal constructor(
    internal val androidToneMapper: AndroidToneMapper,
) {
    actual fun getNativeObject(): Long = androidToneMapper.nativeObject

    actual class Linear actual constructor() : ToneMapper(AndroidToneMapper.Linear())

    actual class ACES actual constructor() : ToneMapper(AndroidToneMapper.ACES())

    actual class ACESLegacy actual constructor() : ToneMapper(AndroidToneMapper.ACESLegacy())

    actual class Filmic actual constructor() : ToneMapper(AndroidToneMapper.Filmic())

    actual class PBRNeutralToneMapper actual constructor() : ToneMapper(AndroidToneMapper.PBRNeutralToneMapper())

    actual class GT7ToneMapper actual constructor() : ToneMapper(AndroidToneMapper.GT7ToneMapper())

    actual class Agx actual constructor(look: AgxLook) : ToneMapper(AndroidToneMapper.Agx(look.toAndroid())) {
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
    ) : ToneMapper(AndroidToneMapper.Generic(contrast, midGrayIn, midGrayOut, hdrMax)) {
        private val genericToneMapper: AndroidToneMapper.Generic
            get() = androidToneMapper as AndroidToneMapper.Generic

        actual fun getContrast(): Float = genericToneMapper.contrast

        actual fun setContrast(contrast: Float) {
            genericToneMapper.contrast = contrast
        }

        actual fun getMidGrayIn(): Float = genericToneMapper.midGrayIn

        actual fun setMidGrayIn(midGrayIn: Float) {
            genericToneMapper.midGrayIn = midGrayIn
        }

        actual fun getMidGrayOut(): Float = genericToneMapper.midGrayOut

        actual fun setMidGrayOut(midGrayOut: Float) {
            genericToneMapper.midGrayOut = midGrayOut
        }

        actual fun getHdrMax(): Float = genericToneMapper.hdrMax

        actual fun setHdrMax(hdrMax: Float) {
            genericToneMapper.hdrMax = hdrMax
        }
    }
}

private fun ToneMapper.Agx.AgxLook.toAndroid(): AndroidToneMapper.Agx.AgxLook =
    AndroidToneMapper.Agx.AgxLook.valueOf(name)

