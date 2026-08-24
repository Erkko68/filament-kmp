package io.github.erkko68.filament

import io.github.erkko68.filament.web.interop.readNumbersInto

import io.github.erkko68.filament.web.LightManager_Instance as JSLightManagerInstance

import io.github.erkko68.filament.web.interop.emptyJsObject
import io.github.erkko68.filament.web.interop.jsNumbers
import io.github.erkko68.filament.web.interop.toJsNumbers

import io.github.erkko68.filament.web.LightManager as JSLightManager
import io.github.erkko68.filament.web.`LightManager_Builder` as JSLightManagerBuilder
import io.github.erkko68.filament.web.LightManager_Type
import io.github.erkko68.filament.web.LightManager_ShadowCascades.Companion as JSShadowCascades
import io.github.erkko68.filament.web.Entity as JSEntity

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual class LightManager(internal val jsLightManager: JSLightManager) {
    actual fun getComponentCount(): Int = jsLightManager.getComponentCount().toInt()

    actual fun hasComponent(entity: Entity): Boolean =
        jsLightManager.hasComponent(EntityManager.jsEntityOf(entity))

    actual fun getInstance(entity: Entity): EntityInstance {
        return InstanceRegistry.register(jsLightManager.getInstance(EntityManager.jsEntityOf(entity)))
    }

    actual fun destroy(entity: Entity) {
        jsLightManager.destroy(EntityManager.jsEntityOf(entity))
    }

    actual fun getType(instance: EntityInstance): Type {
        return when (jsLightManager.getType(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>())) {
            LightManager_Type.SUN -> Type.SUN
            LightManager_Type.DIRECTIONAL -> Type.DIRECTIONAL
            LightManager_Type.POINT -> Type.POINT
            LightManager_Type.FOCUSED_SPOT -> Type.FOCUSED_SPOT
            LightManager_Type.SPOT -> Type.SPOT
            else -> error("unreachable")
        }
    }

    actual fun setDirection(instance: EntityInstance, x: Float, y: Float, z: Float) {
        jsLightManager.setDirection(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>(), jsNumbers(x, y, z))
    }

    actual fun getDirection(instance: EntityInstance, out: FloatArray): FloatArray {
        val result = out
        (jsLightManager.getDirection(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>()))?.readNumbersInto(result)
        return result
    }

    actual fun setPosition(instance: EntityInstance, x: Float, y: Float, z: Float) {
        jsLightManager.setPosition(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>(), jsNumbers(x, y, z))
    }

    actual fun getPosition(instance: EntityInstance, out: FloatArray): FloatArray {
        val result = out
        (jsLightManager.getPosition(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>()))?.readNumbersInto(result)
        return result
    }

    actual fun setColor(instance: EntityInstance, r: Float, g: Float, b: Float) {
        jsLightManager.setColor(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>(), jsNumbers(r, g, b))
    }

    actual fun getColor(instance: EntityInstance, out: FloatArray): FloatArray {
        val result = out
        (jsLightManager.getColor(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>()))?.readNumbersInto(result)
        return result
    }

    actual fun setIntensity(instance: EntityInstance, intensity: Float) {
        jsLightManager.setIntensity(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>(), intensity.toDouble())
    }

    actual fun setIntensity(instance: EntityInstance, watts: Float, efficiency: Float) {
        jsLightManager.setIntensityEnergy(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>(), watts.toDouble(), efficiency.toDouble())
    }

    actual fun setIntensityCandela(instance: EntityInstance, intensity: Float) {
        jsLightManager.setIntensityCandela(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>(), intensity.toDouble())
    }

    actual fun getIntensity(instance: EntityInstance): Float {
        return jsLightManager.getIntensity(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>()).toFloat()
    }

    actual fun setFalloff(instance: EntityInstance, radius: Float) {
        jsLightManager.setFalloff(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>(), radius.toDouble())
    }

    actual fun getFalloff(instance: EntityInstance): Float {
        return jsLightManager.getFalloff(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>()).toFloat()
    }

    actual fun setSpotLightCone(instance: EntityInstance, inner: Float, outer: Float) {
        jsLightManager.setSpotLightCone(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>(), inner.toDouble(), outer.toDouble())
    }

    actual fun getInnerConeAngle(instance: EntityInstance): Float {
        return jsLightManager.getSpotLightInnerCone(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>()).toFloat()
    }

    actual fun getOuterConeAngle(instance: EntityInstance): Float {
        return jsLightManager.getSpotLightOuterCone(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>()).toFloat()
    }

    actual fun setSunAngularRadius(instance: EntityInstance, angularRadius: Float) {
        jsLightManager.setSunAngularRadius(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>(), angularRadius.toDouble())
    }

    actual fun getSunAngularRadius(instance: EntityInstance): Float {
        return jsLightManager.getSunAngularRadius(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>()).toFloat()
    }

    actual fun setSunHaloSize(instance: EntityInstance, haloSize: Float) {
        jsLightManager.setSunHaloSize(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>(), haloSize.toDouble())
    }

    actual fun getSunHaloSize(instance: EntityInstance): Float {
        return jsLightManager.getSunHaloSize(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>()).toFloat()
    }

    actual fun setSunHaloFalloff(instance: EntityInstance, haloFalloff: Float) {
        jsLightManager.setSunHaloFalloff(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>(), haloFalloff.toDouble())
    }

    actual fun getSunHaloFalloff(instance: EntityInstance): Float {
        return jsLightManager.getSunHaloFalloff(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>()).toFloat()
    }

    actual fun setShadowCaster(instance: EntityInstance, shadowCaster: Boolean) {
        jsLightManager.setShadowCaster(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>(), shadowCaster)
    }

    actual fun isShadowCaster(instance: EntityInstance): Boolean {
        return jsLightManager.isShadowCaster(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>())
    }

    actual fun setLightChannel(instance: EntityInstance, channel: Int, enable: Boolean) {
        jsLightManager.setLightChannel(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>(), channel.toDouble(), enable)
    }

    actual fun getLightChannel(instance: EntityInstance, channel: Int): Boolean {
        return jsLightManager.getLightChannel(InstanceRegistry.get(instance).unsafeCast<JSLightManagerInstance>(), channel.toDouble())
    }

    actual enum class Type { SUN, DIRECTIONAL, POINT, FOCUSED_SPOT, SPOT }
    
    actual class ShadowOptions {
        actual var mapSize: Int = 1024
        actual var shadowCascades: Int = 1
        actual var cascadeSplitPositions: FloatArray = floatArrayOf(0.125f, 0.25f, 0.50f)
        actual var constantBias: Float = 0.001f
        actual var normalBias: Float = 1.0f
        actual var shadowFar: Float = 0.0f
        actual var shadowNearHint: Float = 1.0f
        actual var shadowFarHint: Float = 100.0f
        actual var stable: Boolean = false
        actual var lispsm: Boolean = false  // match Android binding + cleaner PCSS (Filament C++ defaults true)
        actual var screenSpaceContactShadows: Boolean = false
        actual var stepCount: Int = 8
        actual var maxShadowDistance: Float = 0.3f
        actual var elvsm: Boolean = false
        actual var blurWidth: Float = 0.0f
        actual var shadowBulbRadius: Float = -1.0f
        // Identity quaternion (x,y,z,w); 4 floats like jvm/native, not a 16-float mat4.
        actual var transform: FloatArray = floatArrayOf(0f, 0f, 0f, 1f)
        actual var polygonOffsetConstant: Float = 0.5f
        actual var polygonOffsetSlope: Float = 2.0f
        actual var maxPenumbraRatio: Float = 0.0f
        actual var maxSearchRadius: Float = 0.0f
    }

    // The JS forms return the (cascades - 1) splits instead of filling an out-parameter.
    actual object ShadowCascades {
        actual fun computeUniformSplits(splitPositions: FloatArray, cascades: Int) {
            JSShadowCascades.computeUniformSplits(cascades.toDouble()).readNumbersInto(splitPositions)
        }
        actual fun computeLogSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float) {
            JSShadowCascades.computeLogSplits(cascades.toDouble(), near.toDouble(), far.toDouble())
                .readNumbersInto(splitPositions)
        }
        actual fun computePracticalSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float, lambda: Float) {
            JSShadowCascades.computePracticalSplits(
                cascades.toDouble(), near.toDouble(), far.toDouble(), lambda.toDouble()
            ).readNumbersInto(splitPositions)
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
            jsBuilder.lightChannel(channel.toDouble(), enable)
            return this
        }

        actual fun castShadows(enable: Boolean): Builder {
            jsBuilder.castShadows(enable)
            return this
        }

        actual fun shadowOptions(options: ShadowOptions): Builder {
            val js = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.LightManager_ShadowOptions>()
            js.mapSize = options.mapSize.toDouble()
            js.shadowCascades = options.shadowCascades.toDouble()
            js.cascadeSplitPositions = options.cascadeSplitPositions.toJsNumbers()
            val vsm = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.LightManager_ShadowOptions_Vsm>()
            vsm.elvsm = options.elvsm
            vsm.blurWidth = options.blurWidth.toDouble()
            js.vsm = vsm
            js.constantBias = options.constantBias.toDouble()
            js.normalBias = options.normalBias.toDouble()
            js.shadowFar = options.shadowFar.toDouble()
            js.shadowNearHint = options.shadowNearHint.toDouble()
            js.shadowFarHint = options.shadowFarHint.toDouble()
            js.stable = options.stable
            js.screenSpaceContactShadows = options.screenSpaceContactShadows
            js.stepCount = options.stepCount.toDouble()
            js.maxShadowDistance = options.maxShadowDistance.toDouble()
            js.lispsm = options.lispsm
            js.shadowBulbRadius = options.shadowBulbRadius.toDouble()
            js.transform = options.transform.toJsNumbers()
            js.polygonOffsetConstant = options.polygonOffsetConstant.toDouble()
            js.polygonOffsetSlope = options.polygonOffsetSlope.toDouble()
            js.maxPenumbraRatio = options.maxPenumbraRatio.toDouble()
            js.maxSearchRadius = options.maxSearchRadius.toDouble()
            jsBuilder.shadowOptions(js)
            return this
        }

        actual fun castLight(enabled: Boolean): Builder {
            jsBuilder.castLight(enabled)
            return this
        }

        actual fun position(x: Float, y: Float, z: Float): Builder {
            jsBuilder.position(jsNumbers(x, y, z))
            return this
        }

        actual fun direction(x: Float, y: Float, z: Float): Builder {
            jsBuilder.direction(jsNumbers(x, y, z))
            return this
        }

        actual fun color(linearR: Float, linearG: Float, linearB: Float): Builder {
            jsBuilder.color(jsNumbers(linearR, linearG, linearB))
            return this
        }

        actual fun intensity(intensity: Float): Builder {
            jsBuilder.intensity(intensity.toDouble())
            return this
        }

        actual fun intensity(watts: Float, efficiency: Float): Builder {
            jsBuilder.intensityEnergy(watts.toDouble(), efficiency.toDouble())
            return this
        }

        actual fun intensityCandela(intensity: Float): Builder {
            jsBuilder.intensityCandela(intensity.toDouble())
            return this
        }

        actual fun falloff(radius: Float): Builder {
            jsBuilder.falloff(radius.toDouble())
            return this
        }

        actual fun spotLightCone(inner: Float, outer: Float): Builder {
            jsBuilder.spotLightCone(inner.toDouble(), outer.toDouble())
            return this
        }

        actual fun sunAngularRadius(angularRadius: Float): Builder {
            jsBuilder.sunAngularRadius(angularRadius.toDouble())
            return this
        }

        actual fun sunHaloSize(haloSize: Float): Builder {
            jsBuilder.sunHaloSize(haloSize.toDouble())
            return this
        }

        actual fun sunHaloFalloff(haloFalloff: Float): Builder {
            jsBuilder.sunHaloFalloff(haloFalloff.toDouble())
            return this
        }

        actual fun build(engine: Engine, entity: Entity) {
            jsBuilder.build(engine.jsEngine, EntityManager.jsEntityOf(entity))
        }
    }
}