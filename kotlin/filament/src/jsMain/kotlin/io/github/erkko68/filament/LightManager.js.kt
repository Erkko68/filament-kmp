package io.github.erkko68.filament

import io.github.erkko68.filament.js.LightManager as JSLightManager
import io.github.erkko68.filament.js.`LightManager_Builder` as JSLightManagerBuilder
import io.github.erkko68.filament.js.LightManager_Type
import io.github.erkko68.filament.js.Entity as JSEntity
import io.github.erkko68.filament.js.LightManager_Instance as JSLightManagerInstance

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual class LightManager(internal val jsLightManager: JSLightManager) {
    actual fun getComponentCount(): Int {
        // Not exposed in JS bindings
        return 0
    }

    actual fun hasComponent(entity: Int): Boolean {
        return jsLightManager.hasComponent(entity.unsafeCast<JSEntity>())
    }

    actual fun getInstance(entity: Int): Int {
        return jsLightManager.getInstance(entity.unsafeCast<JSEntity>()).unsafeCast<Int>()
    }

    actual fun destroy(entity: Int) {
        // Destroyed via engine.destroyEntity or component manager specific destroy if exposed
    }

    actual fun getType(instance: Int): Type {
        return when (jsLightManager.getType(instance.unsafeCast<JSLightManagerInstance>())) {
            LightManager_Type.SUN -> Type.SUN
            LightManager_Type.DIRECTIONAL -> Type.DIRECTIONAL
            LightManager_Type.POINT -> Type.POINT
            LightManager_Type.FOCUSED_SPOT -> Type.FOCUSED_SPOT
            LightManager_Type.SPOT -> Type.SPOT
        }
    }

    actual fun setDirection(instance: Int, x: Float, y: Float, z: Float) {
        jsLightManager.setDirection(instance.unsafeCast<JSLightManagerInstance>(), arrayOf(x, y, z) as Array<Number>)
    }

    actual fun getDirection(instance: Int, out: FloatArray): FloatArray {
        val result = out
        val jsVec = jsLightManager.getDirection(instance.unsafeCast<JSLightManagerInstance>()) as Array<Double>
        for (i in 0 until 3.coerceAtMost(jsVec.size)) result[i] = jsVec[i].toFloat()
        return result
    }

    actual fun setPosition(instance: Int, x: Float, y: Float, z: Float) {
        jsLightManager.setPosition(instance.unsafeCast<JSLightManagerInstance>(), arrayOf(x, y, z) as Array<Number>)
    }

    actual fun getPosition(instance: Int, out: FloatArray): FloatArray {
        val result = out
        val jsVec = jsLightManager.getPosition(instance.unsafeCast<JSLightManagerInstance>()) as Array<Double>
        for (i in 0 until 3.coerceAtMost(jsVec.size)) result[i] = jsVec[i].toFloat()
        return result
    }

    actual fun setColor(instance: Int, r: Float, g: Float, b: Float) {
        jsLightManager.setColor(instance.unsafeCast<JSLightManagerInstance>(), arrayOf(r, g, b) as Array<Number>)
    }

    actual fun getColor(instance: Int, out: FloatArray): FloatArray {
        val result = out
        val jsVec = jsLightManager.getColor(instance.unsafeCast<JSLightManagerInstance>()) as Array<Double>
        for (i in 0 until 3.coerceAtMost(jsVec.size)) result[i] = jsVec[i].toFloat()
        return result
    }

    actual fun setIntensity(instance: Int, intensity: Float) {
        jsLightManager.setIntensity(instance.unsafeCast<JSLightManagerInstance>(), intensity)
    }

    actual fun setIntensity(instance: Int, watts: Float, efficiency: Float) {
        jsLightManager.setIntensityEnergy(instance.unsafeCast<JSLightManagerInstance>(), watts, efficiency)
    }

    actual fun setIntensityCandela(instance: Int, intensity: Float) {
        jsLightManager.setIntensity(instance.unsafeCast<JSLightManagerInstance>(), intensity)
    }

    actual fun getIntensity(instance: Int): Float {
        return jsLightManager.getIntensity(instance.unsafeCast<JSLightManagerInstance>()).toFloat()
    }

    actual fun setFalloff(instance: Int, radius: Float) {
        jsLightManager.setFalloff(instance.unsafeCast<JSLightManagerInstance>(), radius)
    }

    actual fun getFalloff(instance: Int): Float {
        return jsLightManager.getFalloff(instance.unsafeCast<JSLightManagerInstance>()).toFloat()
    }

    actual fun setSpotLightCone(instance: Int, inner: Float, outer: Float) {
        jsLightManager.setSpotLightCone(instance.unsafeCast<JSLightManagerInstance>(), inner, outer)
    }

    actual fun getInnerConeAngle(instance: Int): Float {
        return 0.0f
    }

    actual fun getOuterConeAngle(instance: Int): Float {
        return 0.0f
    }

    actual fun setSunAngularRadius(instance: Int, angularRadius: Float) {
        jsLightManager.setSunAngularRadius(instance.unsafeCast<JSLightManagerInstance>(), angularRadius)
    }

    actual fun getSunAngularRadius(instance: Int): Float {
        return jsLightManager.getSunAngularRadius(instance.unsafeCast<JSLightManagerInstance>()).toFloat()
    }

    actual fun setSunHaloSize(instance: Int, haloSize: Float) {
        jsLightManager.setSunHaloSize(instance.unsafeCast<JSLightManagerInstance>(), haloSize)
    }

    actual fun getSunHaloSize(instance: Int): Float {
        return jsLightManager.getSunHaloSize(instance.unsafeCast<JSLightManagerInstance>()).toFloat()
    }

    actual fun setSunHaloFalloff(instance: Int, haloFalloff: Float) {
        jsLightManager.setSunHaloFalloff(instance.unsafeCast<JSLightManagerInstance>(), haloFalloff)
    }

    actual fun getSunHaloFalloff(instance: Int): Float {
        return jsLightManager.getSunHaloFalloff(instance.unsafeCast<JSLightManagerInstance>()).toFloat()
    }

    actual fun setShadowCaster(instance: Int, shadowCaster: Boolean) {
        jsLightManager.setShadowCaster(instance.unsafeCast<JSLightManagerInstance>(), shadowCaster)
    }

    actual fun isShadowCaster(instance: Int): Boolean {
        return jsLightManager.isShadowCaster(instance.unsafeCast<JSLightManagerInstance>())
    }

    actual fun setLightChannel(instance: Int, channel: Int, enable: Boolean) {
    }

    actual fun getLightChannel(instance: Int, channel: Int): Boolean {
        return true
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
            // Not exposed in JS LightManager.Builder bindings
            return this
        }

        actual fun castShadows(enable: Boolean): Builder {
            jsBuilder.castShadows(enable)
            return this
        }

        actual fun shadowOptions(options: ShadowOptions): Builder {
            val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.LightManager_ShadowOptions>()
            jsOptions.mapSize = options.mapSize
            jsOptions.shadowCascades = options.shadowCascades
            jsOptions.constantBias = options.constantBias
            jsOptions.normalBias = options.normalBias
            jsOptions.shadowFar = options.shadowFar
            jsOptions.shadowNearHint = options.shadowNearHint
            jsOptions.shadowFarHint = options.shadowFarHint
            jsOptions.stable = options.stable
            jsOptions.screenSpaceContactShadows = options.screenSpaceContactShadows
            jsOptions.stepCount = options.stepCount
            jsOptions.maxShadowDistance = options.maxShadowDistance
            jsBuilder.shadowOptions(jsOptions)
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

        actual fun build(engine: Engine, entity: Int) {
            jsBuilder.build(engine.jsEngine, entity.unsafeCast<JSEntity>())
        }
    }
}