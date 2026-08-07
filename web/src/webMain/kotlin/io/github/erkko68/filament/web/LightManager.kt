package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class LightManager : JsAny {
fun hasComponent(entity: Entity): Boolean
fun getComponentCount(): Double
fun destroy(entity: Entity): Unit
fun getInstance(entity: Entity): LightManager_Instance
fun getType(instance: LightManager_Instance): LightManager_Type
fun isDirectional(instance: LightManager_Instance): Boolean
fun isPointLight(instance: LightManager_Instance): Boolean
fun isSpotLight(instance: LightManager_Instance): Boolean
fun setPosition(instance: LightManager_Instance, value: float3): Unit
fun getPosition(instance: LightManager_Instance): float3
fun setDirection(instance: LightManager_Instance, value: float3): Unit
fun getDirection(instance: LightManager_Instance): float3
fun setColor(instance: LightManager_Instance, value: float3): Unit
fun getColor(instance: LightManager_Instance): float3
fun setIntensity(instance: LightManager_Instance, intensity: Double): Unit
fun setIntensityEnergy(instance: LightManager_Instance, watts: Double, efficiency: Double): Unit
fun getIntensity(instance: LightManager_Instance): Double
fun setFalloff(instance: LightManager_Instance, radius: Double): Unit
fun getFalloff(instance: LightManager_Instance): Double
fun setShadowOptions(instance: LightManager_Instance, options: LightManager_ShadowOptions): Unit
fun setSpotLightCone(instance: LightManager_Instance, inner: Double, outer: Double): Unit
fun setSunAngularRadius(instance: LightManager_Instance, angularRadius: Double): Unit
fun getSunAngularRadius(instance: LightManager_Instance): Double
fun setSunHaloSize(instance: LightManager_Instance, haloSize: Double): Unit
fun getSunHaloSize(instance: LightManager_Instance): Double
fun setSunHaloFalloff(instance: LightManager_Instance, haloFalloff: Double): Unit
fun getSunHaloFalloff(instance: LightManager_Instance): Double
fun setShadowCaster(instance: LightManager_Instance, shadowCaster: Boolean): Double
fun isShadowCaster(instance: LightManager_Instance): Boolean
fun getLightChannel(instance: LightManager_Instance, channel: Double): Boolean
fun setLightChannel(instance: LightManager_Instance, channel: Double, enable: Boolean): Unit
companion object {
fun Builder(ltype: LightManager_Type): LightManager_Builder
}
}

// ── LightManager ──────────────────────────────────────────────────────────────
