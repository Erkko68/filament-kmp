package io.github.erkko68.filament.web

external class GenericToneMapper(
    contrast: Double,
    midGrayIn: Double,
    midGrayOut: Double,
    hdrMax: Double,
) : ToneMapper {
fun getContrast(): Double
fun setContrast(contrast: Double): Unit
fun getMidGrayIn(): Double
fun setMidGrayIn(midGrayIn: Double): Unit
fun getMidGrayOut(): Double
fun setMidGrayOut(midGrayOut: Double): Unit
fun getHdrMax(): Double
fun setHdrMax(hdrMax: Double): Unit
}
