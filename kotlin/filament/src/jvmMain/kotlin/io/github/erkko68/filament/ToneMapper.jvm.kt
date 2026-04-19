package io.github.erkko68.filament

import io.github.erkko68.filament.jni.ToneMapper as JniToneMapper

actual open class ToneMapper(val nativeToneMapper: JniToneMapper) {
    actual class Linear : ToneMapper(JniToneMapper.Linear())
    actual class ACES : ToneMapper(JniToneMapper.ACES())
    actual class ACESLegacy : ToneMapper(JniToneMapper.ACESLegacy())
    actual class Filmic : ToneMapper(JniToneMapper.Filmic())
    actual class PBRNeutralToneMapper : ToneMapper(JniToneMapper.PBRNeutral())
    actual class GT7ToneMapper : ToneMapper(JniToneMapper.GT7())

    actual class Agx actual constructor(look: AgxLook) : ToneMapper(JniToneMapper.Agx(look.toJni())) {
        actual enum class AgxLook {
            NONE, PUNCHY, GOLDEN;
            internal fun toJni() = JniToneMapper.Agx.Look.values()[ordinal]
        }
    }

    actual class Generic actual constructor(
        contrast: Float,
        midGrayIn: Float,
        midGrayOut: Float,
        hdrMax: Float
    ) : ToneMapper(JniToneMapper.Generic(contrast, midGrayIn, midGrayOut, hdrMax)) {
        actual var contrast: Float
            get() = (nativeToneMapper as JniToneMapper.Generic).contrast
            set(value) { (nativeToneMapper as JniToneMapper.Generic).contrast = value }
        actual var midGrayIn: Float
            get() = (nativeToneMapper as JniToneMapper.Generic).midGrayIn
            set(value) { (nativeToneMapper as JniToneMapper.Generic).midGrayIn = value }
        actual var midGrayOut: Float
            get() = (nativeToneMapper as JniToneMapper.Generic).midGrayOut
            set(value) { (nativeToneMapper as JniToneMapper.Generic).midGrayOut = value }
        actual var hdrMax: Float
            get() = (nativeToneMapper as JniToneMapper.Generic).hdrMax
            set(value) { (nativeToneMapper as JniToneMapper.Generic).hdrMax = value }
    }

    actual class DisplayRange : ToneMapper(JniToneMapper.DisplayRange())
}
