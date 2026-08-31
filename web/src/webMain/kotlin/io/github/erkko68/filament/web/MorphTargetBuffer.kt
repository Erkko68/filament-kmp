package io.github.erkko68.filament.web

/**
 * Per-target morph positions and tangents.
 * @see Engine.destroyMorphTargetBuffer
 */
external class MorphTargetBuffer : JsAny {
fun getVertexCount(): Double
fun getCount(): Double
fun hasPositions(): Boolean
fun hasTangents(): Boolean
fun isCustomMorphingEnabled(): Boolean
fun setPositionsAt(engine: Engine, targetIndex: Double, positions: org.khronos.webgl.Float32Array, count: Double, offset: Double): Unit
fun setTangentsAt(engine: Engine, targetIndex: Double, tangents: org.khronos.webgl.Int16Array, count: Double, offset: Double): Unit
companion object {
fun Builder(): MorphTargetBuffer_Builder
}
}
