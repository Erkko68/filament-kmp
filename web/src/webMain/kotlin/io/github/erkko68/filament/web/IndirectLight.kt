// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class IndirectLight : JsAny {
fun setIntensity(intensity: Double): Unit
fun getIntensity(): Double
fun setRotation(value: mat3): Unit
fun getRotation(): mat3
fun getReflectionsTexture(): Texture?
fun getIrradianceTexture(): Texture?
var shfloats: js.array.ReadonlyArray<JsNumber>
companion object {
fun Builder(): IndirectLight_Builder
fun getDirectionEstimate(f32array: JsAny?): float3
fun getColorEstimate(f32array: JsAny?, direction: float3): float4
}
}
