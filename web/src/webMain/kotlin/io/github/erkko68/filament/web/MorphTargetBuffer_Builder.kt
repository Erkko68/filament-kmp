package io.github.erkko68.filament.web

@JsName("MorphTargetBuffer\$Builder")
external class MorphTargetBuffer_Builder : JsAny {
fun vertexCount(vertexCount: Double): MorphTargetBuffer_Builder
fun count(count: Double): MorphTargetBuffer_Builder
fun withPositions(enable: Boolean): MorphTargetBuffer_Builder
fun withTangents(enable: Boolean): MorphTargetBuffer_Builder
fun enableCustomMorphing(enable: Boolean): MorphTargetBuffer_Builder
fun build(engine: Engine): MorphTargetBuffer
}
