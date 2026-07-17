package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

@JsName("VertexBuffer\$Builder")
external class VertexBuffer_Builder : JsAny {
fun vertexCount(count: Double): VertexBuffer_Builder
fun bufferCount(count: Double): VertexBuffer_Builder
fun attribute(attrib: VertexAttribute, bufindex: Double, atype: VertexBuffer_AttributeType, offset: Double, stride: Double): VertexBuffer_Builder
fun enableBufferObjects(enabled: Boolean): VertexBuffer_Builder
fun normalized(attrib: VertexAttribute): VertexBuffer_Builder
fun normalizedIf(attrib: VertexAttribute, normalized: Boolean): VertexBuffer_Builder
fun build(engine: Engine): VertexBuffer
}
