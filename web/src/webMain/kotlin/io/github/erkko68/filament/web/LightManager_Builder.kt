// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class LightManager_Builder : JsAny {
fun build(engine: Engine, entity: Entity): Unit
fun castLight(enable: Boolean): LightManager_Builder
fun castShadows(enable: Boolean): LightManager_Builder
fun shadowOptions(options: LightManager_ShadowOptions): LightManager_Builder
fun color(rgb: float3): LightManager_Builder
fun direction(value: float3): LightManager_Builder
fun intensity(value: Double): LightManager_Builder
fun falloff(value: Double): LightManager_Builder
fun position(value: float3): LightManager_Builder
fun spotLightCone(inner: Double, outer: Double): LightManager_Builder
fun sunAngularRadius(angularRadius: Double): LightManager_Builder
fun sunHaloFalloff(haloFalloff: Double): LightManager_Builder
fun sunHaloSize(haloSize: Double): LightManager_Builder
fun lightChannel(channel: Double, enable: Boolean): LightManager_Builder
}
