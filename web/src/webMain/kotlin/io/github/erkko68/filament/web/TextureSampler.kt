package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class TextureSampler : JsAny {
constructor (minfilter: MinFilter, magfilter: MagFilter, wrapmode: WrapMode)
fun setMinFilter(filter: MinFilter): Unit
fun getMinFilter(): MinFilter
fun setMagFilter(filter: MagFilter): Unit
fun getMagFilter(): MagFilter
fun setWrapModeS(mode: WrapMode): Unit
fun getWrapModeS(): WrapMode
fun setWrapModeT(mode: WrapMode): Unit
fun getWrapModeT(): WrapMode
fun setWrapModeR(mode: WrapMode): Unit
fun getWrapModeR(): WrapMode
fun setAnisotropy(value: Double): Unit
fun getAnisotropy(): Double
fun setCompareMode(mode: CompareMode, func: CompareFunc): Unit
fun getCompareMode(): CompareMode
fun getCompareFunc(): CompareFunc
}
