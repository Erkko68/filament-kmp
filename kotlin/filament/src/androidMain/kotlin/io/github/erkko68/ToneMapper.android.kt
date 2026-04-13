package io.github.erkko68

import com.google.android.filament.ToneMapper as FilamentToneMapper

actual open class ToneMapper(internal val nativeToneMapper: FilamentToneMapper) {
    actual class Linear actual constructor() : ToneMapper(FilamentToneMapper.Linear())
    actual class ACES actual constructor() : ToneMapper(FilamentToneMapper.ACES())
    actual class ACESLegacy actual constructor() : ToneMapper(FilamentToneMapper.ACESLegacy())
    actual class Filmic actual constructor() : ToneMapper(FilamentToneMapper.Filmic())
    actual class PBRNeutralToneMapper actual constructor() : ToneMapper(FilamentToneMapper.PBRNeutralToneMapper())
    actual class GT7ToneMapper actual constructor() : ToneMapper(FilamentToneMapper.GT7ToneMapper())
    
    actual class Agx actual constructor(look: AgxLook) : ToneMapper(
        FilamentToneMapper.Agx(FilamentToneMapper.Agx.AgxLook.entries[look.ordinal])
    ) {
        actual enum class AgxLook { NONE, PUNCHY, GOLDEN }
    }
    
    actual class Generic actual constructor(
        contrast: Float,
        midGrayIn: Float,
        midGrayOut: Float,
        hdrMax: Float
    ) : ToneMapper(FilamentToneMapper.Generic(contrast, midGrayIn, midGrayOut, hdrMax)) {
        private val genericNative get() = nativeToneMapper as FilamentToneMapper.Generic
        actual var contrast: Float get() = genericNative.contrast; set(v) { genericNative.contrast = v }
        actual var midGrayIn: Float get() = genericNative.midGrayIn; set(v) { genericNative.midGrayIn = v }
        actual var midGrayOut: Float get() = genericNative.midGrayOut; set(v) { genericNative.midGrayOut = v }
        actual var hdrMax: Float get() = genericNative.hdrMax; set(v) { genericNative.hdrMax = v }
    }
    
    actual class DisplayRange actual constructor() : ToneMapper(FilamentToneMapper.Linear())
}
