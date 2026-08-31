package io.github.erkko68.filament.web

/**
 * Bone transforms that can be shared between renderables.
 * @see Engine.destroySkinningBuffer
 */
external class SkinningBuffer : JsAny {
fun getBoneCount(): Double
fun setBones(engine: Engine, transforms: js.array.ReadonlyArray<RenderableManager_Bone>, offset: Double): Unit
fun setBonesFromMatrices(engine: Engine, transforms: js.array.ReadonlyArray<mat4>, offset: Double): Unit
companion object {
fun Builder(): SkinningBuffer_Builder
}
}
