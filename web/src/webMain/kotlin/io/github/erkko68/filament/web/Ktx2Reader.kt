package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class Ktx2Reader : JsAny {
constructor (engine: Engine, quiet: Boolean)
fun requestFormat(format: Texture_InternalFormat): Unit
fun unrequestFormat(format: Texture_InternalFormat): Unit
fun load(urlOrBuffer: BufferReference, transfer: web.mediacapabilities.TransferFunction): Texture?
}
