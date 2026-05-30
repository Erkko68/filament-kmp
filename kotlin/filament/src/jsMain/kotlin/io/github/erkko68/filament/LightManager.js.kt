package io.github.erkko68.filament

import io.github.erkko68.filament.js.LightManager as JSLightManager
import io.github.erkko68.filament.js.`LightManager_Builder` as JSLightManagerBuilder
import io.github.erkko68.filament.js.LightManager_Type
import io.github.erkko68.filament.js.Entity as JSEntity
import io.github.erkko68.filament.js.LightManager_Instance as JSLightManagerInstance

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual class LightManager(internal val jsLightManager: JSLightManager) {
    actual fun getComponentCount(): Int {
        // TODO(js): getComponentCount not exposed in jsbindings.cpp
        return 0
    }

    // Upstream LightManager binding doesn't expose `destroy(Entity)` —
    // components are usually torn down via `engine.destroyEntity`, but we
    // don't have an Engine reference here. Track local removals so the
    // common API's destroy / hasComponent round-trip behaves as expected.
    private val destroyed = mutableSetOf<Entity>()

    actual fun hasComponent(entity: Entity): Boolean {
        if (entity in destroyed) return false
        return jsLightManager.hasComponent(EntityManager.jsEntityOf(entity))
    }

    actual fun getInstance(entity: Entity): EntityInstance {
        return jsLightManager.getInstance(EntityManager.jsEntityOf(entity)).unsafeCast<Int>()
    }

    actual fun destroy(entity: Entity) {
        destroyed += entity
    }

    actual fun getType(instance: EntityInstance): Type {
        return when (jsLightManager.getType(instance.unsafeCast<JSLightManagerInstance>())) {
            LightManager_Type.SUN -> Type.SUN
            LightManager_Type.DIRECTIONAL -> Type.DIRECTIONAL
            LightManager_Type.POINT -> Type.POINT
            LightManager_Type.FOCUSED_SPOT -> Type.FOCUSED_SPOT
            LightManager_Type.SPOT -> Type.SPOT
        }
    }

    actual fun setDirection(instance: EntityInstance, x: Float, y: Float, z: Float) {
        jsLightManager.setDirection(instance.unsafeCast<JSLightManagerInstance>(), arrayOf(x, y, z) as Array<Number>)
    }

    actual fun getDirection(instance: EntityInstance, out: FloatArray): FloatArray {
        val result = out
        val jsVec = jsLightManager.getDirection(instance.unsafeCast<JSLightManagerInstance>()) as Array<Double>
        for (i in 0 until 3.coerceAtMost(jsVec.size)) result[i] = jsVec[i].toFloat()
        return result
    }

    actual fun setPosition(instance: EntityInstance, x: Float, y: Float, z: Float) {
        jsLightManager.setPosition(instance.unsafeCast<JSLightManagerInstance>(), arrayOf(x, y, z) as Array<Number>)
    }

    actual fun getPosition(instance: EntityInstance, out: FloatArray): FloatArray {
        val result = out
        val jsVec = jsLightManager.getPosition(instance.unsafeCast<JSLightManagerInstance>()) as Array<Double>
        for (i in 0 until 3.coerceAtMost(jsVec.size)) result[i] = jsVec[i].toFloat()
        return result
    }

    actual fun setColor(instance: EntityInstance, r: Float, g: Float, b: Float) {
        jsLightManager.setColor(instance.unsafeCast<JSLightManagerInstance>(), arrayOf(r, g, b) as Array<Number>)
    }

    actual fun getColor(instance: EntityInstance, out: FloatArray): FloatArray {
        val result = out
        val jsVec = jsLightManager.getColor(instance.unsafeCast<JSLightManagerInstance>()) as Array<Double>
        for (i in 0 until 3.coerceAtMost(jsVec.size)) result[i] = jsVec[i].toFloat()
        return result
    }

    actual fun setIntensity(instance: EntityInstance, intensity: Float) {
        jsLightManager.setIntensity(instance.unsafeCast<JSLightManagerInstance>(), intensity)
    }

    actual fun setIntensity(instance: EntityInstance, watts: Float, efficiency: Float) {
        jsLightManager.setIntensityEnergy(instance.unsafeCast<JSLightManagerInstance>(), watts, efficiency)
    }

    actual fun setIntensityCandela(instance: EntityInstance, intensity: Float) {
        jsLightManager.setIntensity(instance.unsafeCast<JSLightManagerInstance>(), intensity)
    }

    actual fun getIntensity(instance: EntityInstance): Float {
        return jsLightManager.getIntensity(instance.unsafeCast<JSLightManagerInstance>()).toFloat()
    }

    actual fun setFalloff(instance: EntityInstance, radius: Float) {
        jsLightManager.setFalloff(instance.unsafeCast<JSLightManagerInstance>(), radius)
    }

    actual fun getFalloff(instance: EntityInstance): Float {
        return jsLightManager.getFalloff(instance.unsafeCast<JSLightManagerInstance>()).toFloat()
    }

    // get{Inner,Outer}ConeAngle aren't bound in upstream jsbindings.cpp (v1.71.4) —
    // mirror the last setSpotLightCone value per-instance so the common getters
    // return what was set.
    private val coneAngles = mutableMapOf<Int, Pair<Float, Float>>()

    actual fun setSpotLightCone(instance: EntityInstance, inner: Float, outer: Float) {
        coneAngles[instance] = inner to outer
        jsLightManager.setSpotLightCone(instance.unsafeCast<JSLightManagerInstance>(), inner, outer)
    }

    actual fun getInnerConeAngle(instance: EntityInstance): Float = coneAngles[instance]?.first ?: 0f

    actual fun getOuterConeAngle(instance: EntityInstance): Float = coneAngles[instance]?.second ?: 0f

    actual fun setSunAngularRadius(instance: EntityInstance, angularRadius: Float) {
        jsLightManager.setSunAngularRadius(instance.unsafeCast<JSLightManagerInstance>(), angularRadius)
    }

    actual fun getSunAngularRadius(instance: EntityInstance): Float {
        return jsLightManager.getSunAngularRadius(instance.unsafeCast<JSLightManagerInstance>()).toFloat()
    }

    actual fun setSunHaloSize(instance: EntityInstance, haloSize: Float) {
        jsLightManager.setSunHaloSize(instance.unsafeCast<JSLightManagerInstance>(), haloSize)
    }

    actual fun getSunHaloSize(instance: EntityInstance): Float {
        return jsLightManager.getSunHaloSize(instance.unsafeCast<JSLightManagerInstance>()).toFloat()
    }

    actual fun setSunHaloFalloff(instance: EntityInstance, haloFalloff: Float) {
        jsLightManager.setSunHaloFalloff(instance.unsafeCast<JSLightManagerInstance>(), haloFalloff)
    }

    actual fun getSunHaloFalloff(instance: EntityInstance): Float {
        return jsLightManager.getSunHaloFalloff(instance.unsafeCast<JSLightManagerInstance>()).toFloat()
    }

    actual fun setShadowCaster(instance: EntityInstance, shadowCaster: Boolean) {
        jsLightManager.setShadowCaster(instance.unsafeCast<JSLightManagerInstance>(), shadowCaster)
    }

    actual fun isShadowCaster(instance: EntityInstance): Boolean {
        return jsLightManager.isShadowCaster(instance.unsafeCast<JSLightManagerInstance>())
    }

    actual fun setLightChannel(instance: EntityInstance, channel: Int, enable: Boolean) {
        jsLightManager.setLightChannel(instance.unsafeCast<JSLightManagerInstance>(), channel, enable)
    }

    actual fun getLightChannel(instance: EntityInstance, channel: Int): Boolean {
        return jsLightManager.getLightChannel(instance.unsafeCast<JSLightManagerInstance>(), channel)
    }

    actual enum class Type { SUN, DIRECTIONAL, POINT, FOCUSED_SPOT, SPOT }
    actual class ShadowOptions {
        actual var mapSize: Int = 512
        actual var shadowCascades: Int = 1
        actual var cascadeSplitPositions: FloatArray = floatArrayOf(0.1f, 0.5f, 0.9f)
        actual var constantBias: Float = 0.001f
        actual var normalBias: Float = 0.4f
        actual var shadowFar: Float = 0.0f
        actual var shadowNearHint: Float = 0.0f
        actual var shadowFarHint: Float = 100.0f
        actual var stable: Boolean = false
        actual var lispsm: Boolean = false
        actual var screenSpaceContactShadows: Boolean = false
        actual var stepCount: Int = 1
        actual var maxShadowDistance: Float = 0.0f
        actual var elvsm: Boolean = false
        actual var blurWidth: Float = 0.0f
        actual var shadowBulbRadius: Float = 0.0f
        actual var transform: FloatArray = FloatArray(16)
    }

    actual object ShadowCascades {
        actual fun computeUniformSplits(splitPositions: FloatArray, cascades: Int) {
        }
        actual fun computeLogSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float) {
        }
        actual fun computePracticalSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float, lambda: Float) {
        }
    }

    actual class Builder actual constructor(type: Type) {
        private val jsBuilder: JSLightManagerBuilder = JSLightManager.Builder(
            when (type) {
                Type.SUN -> LightManager_Type.SUN
                Type.DIRECTIONAL -> LightManager_Type.DIRECTIONAL
                Type.POINT -> LightManager_Type.POINT
                Type.FOCUSED_SPOT -> LightManager_Type.FOCUSED_SPOT
                Type.SPOT -> LightManager_Type.SPOT
            }
        )

        actual fun lightChannel(channel: Int, enable: Boolean): Builder {
            jsBuilder.lightChannel(channel, enable)
            return this
        }

        actual fun castShadows(enable: Boolean): Builder {
            jsBuilder.castShadows(enable)
            return this
        }

        actual fun shadowOptions(options: ShadowOptions): Builder {
            // No-op on JS — see UPSTREAM_INCONSISTENCIES.md "LightManager
            // shadowOptions". jsbindings.cpp registers the ShadowOptions
            // value_object with a `transform` field typed as mat4f, but the
            // only mat4 type in the embind registry is `flatmat4` — a wrapper
            // struct registered under a different RTTI. embind looks up mat4f,
            // finds nothing, and throws "unbound types" for any input. The
            // upstream `Filament.shadowOptions(overrides)` wrapper itself
            // omits `transform`/`lispsm`/`shadowBulbRadius` from its defaults
            // because no JS caller can populate them. Until upstream registers
            // mat4f (or changes the field to flatmat4), this is unreachable.
            return this
        }

        actual fun castLight(enabled: Boolean): Builder {
            jsBuilder.castLight(enabled)
            return this
        }

        actual fun position(x: Float, y: Float, z: Float): Builder {
            jsBuilder.position(arrayOf(x, y, z) as Array<Number>)
            return this
        }

        actual fun direction(x: Float, y: Float, z: Float): Builder {
            jsBuilder.direction(arrayOf(x, y, z) as Array<Number>)
            return this
        }

        actual fun color(linearR: Float, linearG: Float, linearB: Float): Builder {
            jsBuilder.color(arrayOf(linearR, linearG, linearB) as Array<Number>)
            return this
        }

        actual fun intensity(intensity: Float): Builder {
            jsBuilder.intensity(intensity)
            return this
        }

        actual fun intensity(watts: Float, efficiency: Float): Builder {
            // intensityEnergy not in JS builder bindings; use intensity instead
            jsBuilder.intensity(watts)
            return this
        }

        actual fun intensityCandela(intensity: Float): Builder {
            jsBuilder.intensity(intensity)
            return this
        }

        actual fun falloff(radius: Float): Builder {
            jsBuilder.falloff(radius)
            return this
        }

        actual fun spotLightCone(inner: Float, outer: Float): Builder {
            jsBuilder.spotLightCone(inner, outer)
            return this
        }

        actual fun sunAngularRadius(angularRadius: Float): Builder {
            jsBuilder.sunAngularRadius(angularRadius)
            return this
        }

        actual fun sunHaloSize(haloSize: Float): Builder {
            jsBuilder.sunHaloSize(haloSize)
            return this
        }

        actual fun sunHaloFalloff(haloFalloff: Float): Builder {
            jsBuilder.sunHaloFalloff(haloFalloff)
            return this
        }

        actual fun build(engine: Engine, entity: Entity) {
            jsBuilder.build(engine.jsEngine, EntityManager.jsEntityOf(entity))
        }
    }
}