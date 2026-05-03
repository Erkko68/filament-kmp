@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*
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
        actual var contrast: Float
            get() = FilaToneMapper_Generic_getContrast(nativeHandle)
            set(value) { FilaToneMapper_Generic_setContrast(nativeHandle, value) }
        actual var midGrayIn: Float
            get() = FilaToneMapper_Generic_getMidGrayIn(nativeHandle)
            set(value) { FilaToneMapper_Generic_setMidGrayIn(nativeHandle, value) }
        actual var midGrayOut: Float
            get() = FilaToneMapper_Generic_getMidGrayOut(nativeHandle)
            set(value) { FilaToneMapper_Generic_setMidGrayOut(nativeHandle, value) }
        actual var hdrMax: Float
            get() = FilaToneMapper_Generic_getHdrMax(nativeHandle)
            set(value) { FilaToneMapper_Generic_setHdrMax(nativeHandle, value) }
    }
    
    actual class DisplayRange actual constructor() : ToneMapper(FilaToneMapper_Linear()) // Placeholder to match parity with Android if we kept it
}
