@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaToneMapper

actual open class ToneMapper(internal val nativeHandle: CPointer<FilaToneMapper>?) {
    actual class Linear actual constructor() : ToneMapper(FilaToneMapper_Linear())
    actual class ACES actual constructor() : ToneMapper(FilaToneMapper_ACES())
    actual class ACESLegacy actual constructor() : ToneMapper(FilaToneMapper_ACESLegacy())
    actual class Filmic actual constructor() : ToneMapper(FilaToneMapper_Filmic())
    actual class PBRNeutralToneMapper actual constructor() : ToneMapper(FilaToneMapper_PBRNeutral())
    actual class GT7ToneMapper actual constructor() : ToneMapper(FilaToneMapper_GT7())
    
    actual class Agx actual constructor(look: AgxLook) : ToneMapper(
        FilaToneMapper_Agx(look.ordinal.toUInt())
    ) {
        actual enum class AgxLook { NONE, PUNCHY, GOLDEN }
    }
    
    actual class Generic actual constructor(
        contrast: Float,
        midGrayIn: Float,
        midGrayOut: Float,
        hdrMax: Float
    ) : ToneMapper(FilaToneMapper_Generic(contrast, midGrayIn, midGrayOut, hdrMax)) {
        actual var contrast: Float = contrast
        actual var midGrayIn: Float = midGrayIn
        actual var midGrayOut: Float = midGrayOut
        actual var hdrMax: Float = hdrMax
        // Note: C wrapper doesn't have getters/setters for GenericToneMapper yet.
        // We could add them if needed, but for now we initialize in constructor.
    }
    
    actual class DisplayRange actual constructor() : ToneMapper(FilaToneMapper_Linear()) // Placeholder to match parity with Android if we kept it
}
