package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class IndexBuffer : JsAny {
fun setBuffer(engine: Engine, u16array: BufferReference, byteOffset: Double = definedExternally): Unit
fun getIndexCount(): Double
companion object {
fun Builder(): IndexBuffer_Builder
}
}
