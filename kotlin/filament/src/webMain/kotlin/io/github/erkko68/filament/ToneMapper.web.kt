package io.github.erkko68.filament

import io.github.erkko68.filament.web.ACESLegacyToneMapper
import io.github.erkko68.filament.web.ACESToneMapper
import io.github.erkko68.filament.web.AgxToneMapper
import io.github.erkko68.filament.web.AgxToneMapper_AgxLook
import io.github.erkko68.filament.web.DisplayRangeToneMapper
import io.github.erkko68.filament.web.FilmicToneMapper
import io.github.erkko68.filament.web.GT7ToneMapper as JsGT7ToneMapper
import io.github.erkko68.filament.web.GenericToneMapper
import io.github.erkko68.filament.web.LinearToneMapper
import io.github.erkko68.filament.web.PBRNeutralToneMapper as JsPBRNeutralToneMapper
import io.github.erkko68.filament.web.ToneMapper as JsToneMapper

actual open class ToneMapper(internal val jsToneMapper: JsToneMapper) {
    actual class Linear : ToneMapper(LinearToneMapper())
    actual class ACES : ToneMapper(ACESToneMapper())
    actual class ACESLegacy : ToneMapper(ACESLegacyToneMapper())
    actual class Filmic : ToneMapper(FilmicToneMapper())
    actual class PBRNeutralToneMapper : ToneMapper(JsPBRNeutralToneMapper())
    actual class GT7ToneMapper : ToneMapper(JsGT7ToneMapper())
    actual class DisplayRange : ToneMapper(DisplayRangeToneMapper())

    actual class Agx actual constructor(look: AgxLook) : ToneMapper(
        AgxToneMapper(
            when (look) {
                AgxLook.PUNCHY -> AgxToneMapper_AgxLook.PUNCHY
                AgxLook.GOLDEN -> AgxToneMapper_AgxLook.GOLDEN
                AgxLook.NONE -> AgxToneMapper_AgxLook.NONE
            }
        )
    ) {
        actual enum class AgxLook { NONE, PUNCHY, GOLDEN }
    }

    actual class Generic actual constructor(
        contrast: Float,
        midGrayIn: Float,
        midGrayOut: Float,
        hdrMax: Float
    ) : ToneMapper(
        GenericToneMapper(
            contrast.toDouble(),
            midGrayIn.toDouble(),
            midGrayOut.toDouble(),
            hdrMax.toDouble()
        )
    ) {
        private val generic: GenericToneMapper get() = jsToneMapper.unsafeCast<GenericToneMapper>()

        actual var contrast: Float
            get() = generic.getContrast().toFloat()
            set(value) { generic.setContrast(value.toDouble()) }
        actual var midGrayIn: Float
            get() = generic.getMidGrayIn().toFloat()
            set(value) { generic.setMidGrayIn(value.toDouble()) }
        actual var midGrayOut: Float
            get() = generic.getMidGrayOut().toFloat()
            set(value) { generic.setMidGrayOut(value.toDouble()) }
        actual var hdrMax: Float
            get() = generic.getHdrMax().toFloat()
            set(value) { generic.setHdrMax(value.toDouble()) }
    }
}
