package dev.filament.kmp

import kotlinx.cinterop.*
import filament.*

actual class ToneMapper internal constructor(
    internal val nativeObject: CPointer<FilaToneMapper>
) {
    actual enum class AgxLook {
        NONE, PUNCHY, GOLDEN;
        internal fun toNative(): FilaAgxLook = when (this) {
            NONE -> FILA_AGX_LOOK_NONE
            PUNCHY -> FILA_AGX_LOOK_PUNCHY
            GOLDEN -> FILA_AGX_LOOK_GOLDEN
        }
    }

    actual companion object {
        actual fun Linear(): ToneMapper = ToneMapper(FilaToneMapper_Linear()!!)
        actual fun ACES(): ToneMapper = ToneMapper(FilaToneMapper_ACES()!!)
        actual fun ACESLegacy(): ToneMapper = ToneMapper(FilaToneMapper_ACESLegacy()!!)
        actual fun Filmic(): ToneMapper = ToneMapper(FilaToneMapper_Filmic()!!)
        actual fun PBRNeutral(): ToneMapper = ToneMapper(FilaToneMapper_PBRNeutral()!!)
        actual fun GT7(): ToneMapper = ToneMapper(FilaToneMapper_GT7()!!)
        actual fun Agx(look: AgxLook): ToneMapper = ToneMapper(FilaToneMapper_Agx(look.toNative())!!)
        actual fun Generic(contrast: Float, midGrayIn: Float, midGrayOut: Float, hdrMax: Float): ToneMapper = 
            ToneMapper(FilaToneMapper_Generic(contrast, midGrayIn, midGrayOut, hdrMax)!!)
        actual fun DisplayRange(): ToneMapper = ToneMapper(FilaToneMapper_DisplayRange()!!)
    }

    internal fun destroy() {
        FilaToneMapper_destroy(nativeObject)
    }
}
