package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class VertexBuffer : JsAny {
fun setBufferAt(engine: Engine, bufindex: Double, f32array: BufferReference, byteOffset: Double = definedExternally): Unit
fun setBufferObjectAt(engine: Engine, bufindex: Double, bo: BufferObject): Unit
fun getVertexCount(): Double
companion object {
fun Builder(): VertexBuffer_Builder
}
}
