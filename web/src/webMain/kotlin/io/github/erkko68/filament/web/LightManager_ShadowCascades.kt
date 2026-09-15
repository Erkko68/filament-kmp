package io.github.erkko68.filament.web

@JsName("LightManager\$ShadowCascades")
external class LightManager_ShadowCascades : JsAny {
companion object {
fun computeUniformSplits(cascades: Double): js.array.ReadonlyArray<JsNumber>
fun computeLogSplits(cascades: Double, near: Double, far: Double): js.array.ReadonlyArray<JsNumber>
fun computePracticalSplits(cascades: Double, near: Double, far: Double, lambda: Double): js.array.ReadonlyArray<JsNumber>
}
}
