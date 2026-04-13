package io.github.erkko68

import com.google.android.filament.LightManager as AndroidLightManager
import com.google.android.filament.Engine as AndroidEngine

actual class LightManager internal constructor(val nativeLightManager: AndroidLightManager) {
    actual enum class Type { SUN, DIRECTIONAL, POINT, FOCUSED_SPOT, SPOT }

    actual class ShadowOptions actual constructor() {
        private val nativeOptions = AndroidLightManager.ShadowOptions()
        
        actual var mapSize: Int
            get() = nativeOptions.mapSize
            set(value) { nativeOptions.mapSize = value }
            
        actual var shadowCascades: Int
            get() = nativeOptions.shadowCascades
            set(value) { nativeOptions.shadowCascades = value }
            
        actual var cascadeSplitPositions: FloatArray
            get() = nativeOptions.cascadeSplitPositions
            set(value) { nativeOptions.cascadeSplitPositions = value }
            
        actual var constantBias: Float
            get() = nativeOptions.constantBias
            set(value) { nativeOptions.constantBias = value }
            
        actual var normalBias: Float
            get() = nativeOptions.normalBias
            set(value) { nativeOptions.normalBias = value }
            
        actual var shadowFar: Float
            get() = nativeOptions.shadowFar
            set(value) { nativeOptions.shadowFar = value }
            
        actual var shadowNearHint: Float
            get() = nativeOptions.shadowNearHint
            set(value) { nativeOptions.shadowNearHint = value }
            
        actual var shadowFarHint: Float
            get() = nativeOptions.shadowFarHint
            set(value) { nativeOptions.shadowFarHint = value }
            
        actual var stable: Boolean
            get() = nativeOptions.stable
            set(value) { nativeOptions.stable = value }
            
        actual var lispsm: Boolean
            get() = nativeOptions.lispsm
            set(value) { nativeOptions.lispsm = value }
            
        actual var screenSpaceContactShadows: Boolean
            get() = nativeOptions.screenSpaceContactShadows
            set(value) { nativeOptions.screenSpaceContactShadows = value }
            
        actual var stepCount: Int
            get() = nativeOptions.stepCount
            set(value) { nativeOptions.stepCount = value }
            
        actual var maxShadowDistance: Float
            get() = nativeOptions.maxShadowDistance
            set(value) { nativeOptions.maxShadowDistance = value }
            
        actual var elvsm: Boolean
            get() = nativeOptions.elvsm
            set(value) { nativeOptions.elvsm = value }
            
        actual var blurWidth: Float
            get() = nativeOptions.blurWidth
            set(value) { nativeOptions.blurWidth = value }
            
        actual var shadowBulbRadius: Float
            get() = nativeOptions.shadowBulbRadius
            set(value) { nativeOptions.shadowBulbRadius = value }
            
        actual var transform: FloatArray
            get() = nativeOptions.transform
            set(value) { nativeOptions.transform = value }
            
        internal fun toNative(): AndroidLightManager.ShadowOptions = nativeOptions
    }

    actual object ShadowCascades {
        actual fun computeUniformSplits(splitPositions: FloatArray, cascades: Int) {
            AndroidLightManager.ShadowCascades.computeUniformSplits(splitPositions, cascades)
        }
        actual fun computeLogSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float) {
            AndroidLightManager.ShadowCascades.computeLogSplits(splitPositions, cascades, near, far)
        }
        actual fun computePracticalSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float, lambda: Float) {
            AndroidLightManager.ShadowCascades.computePracticalSplits(splitPositions, cascades, near, far, lambda)
        }
    }

    actual class Builder actual constructor(type: Type) {
        private val nativeBuilder = AndroidLightManager.Builder(AndroidLightManager.Type.values()[type.ordinal])
        
        actual fun lightChannel(channel: Int, enable: Boolean): Builder = apply { nativeBuilder.lightChannel(channel, enable) }
        actual fun castShadows(enable: Boolean): Builder = apply { nativeBuilder.castShadows(enable) }
        actual fun shadowOptions(options: ShadowOptions): Builder = apply { nativeBuilder.shadowOptions(options.toNative()) }
        actual fun castLight(enabled: Boolean): Builder = apply { nativeBuilder.castLight(enabled) }
        actual fun position(x: Float, y: Float, z: Float): Builder = apply { nativeBuilder.position(x, y, z) }
        actual fun direction(x: Float, y: Float, z: Float): Builder = apply { nativeBuilder.direction(x, y, z) }
        actual fun color(linearR: Float, linearG: Float, linearB: Float): Builder = apply { nativeBuilder.color(linearR, linearG, linearB) }
        actual fun intensity(intensity: Float): Builder = apply { nativeBuilder.intensity(intensity) }
        actual fun intensity(watts: Float, efficiency: Float): Builder = apply { nativeBuilder.intensity(watts, efficiency) }
        actual fun intensityCandela(intensity: Float): Builder = apply { nativeBuilder.intensityCandela(intensity) }
        actual fun falloff(radius: Float): Builder = apply { nativeBuilder.falloff(radius) }
        actual fun spotLightCone(inner: Float, outer: Float): Builder = apply { nativeBuilder.spotLightCone(inner, outer) }
        actual fun sunAngularRadius(angularRadius: Float): Builder = apply { nativeBuilder.sunAngularRadius(angularRadius) }
        actual fun sunHaloSize(haloSize: Float): Builder = apply { nativeBuilder.sunHaloSize(haloSize) }
        actual fun sunHaloFalloff(haloFalloff: Float): Builder = apply { nativeBuilder.sunHaloFalloff(haloFalloff) }
        actual fun build(engine: Engine, entity: Int) { nativeBuilder.build(engine.nativeEngine, entity) }
    }

    actual fun getComponentCount(): Int = nativeLightManager.componentCount
    actual fun hasComponent(entity: Int): Boolean = nativeLightManager.hasComponent(entity)
    actual fun getInstance(entity: Int): Int = nativeLightManager.getInstance(entity)
    actual fun destroy(entity: Int) { nativeLightManager.destroy(entity) }

    actual fun getType(instance: Int): Type = Type.values()[nativeLightManager.getType(instance).ordinal]
    actual fun setDirection(instance: Int, x: Float, y: Float, z: Float) { nativeLightManager.setDirection(instance, x, y, z) }
    actual fun getDirection(instance: Int, out: FloatArray): FloatArray = nativeLightManager.getDirection(instance, out)
    actual fun setPosition(instance: Int, x: Float, y: Float, z: Float) { nativeLightManager.setPosition(instance, x, y, z) }
    actual fun getPosition(instance: Int, out: FloatArray): FloatArray = nativeLightManager.getPosition(instance, out)
    actual fun setColor(instance: Int, r: Float, g: Float, b: Float) { nativeLightManager.setColor(instance, r, g, b) }
    actual fun getColor(instance: Int, out: FloatArray): FloatArray = nativeLightManager.getColor(instance, out)
    actual fun setIntensity(instance: Int, intensity: Float) { nativeLightManager.setIntensity(instance, intensity) }
    actual fun setIntensity(instance: Int, watts: Float, efficiency: Float) { nativeLightManager.setIntensity(instance, watts, efficiency) }
    actual fun setIntensityCandela(instance: Int, intensity: Float) { nativeLightManager.setIntensityCandela(instance, intensity) }
    actual fun getIntensity(instance: Int): Float = nativeLightManager.getIntensity(instance)
    actual fun setFalloff(instance: Int, radius: Float) { nativeLightManager.setFalloff(instance, radius) }
    actual fun getFalloff(instance: Int): Float = nativeLightManager.getFalloff(instance)
    actual fun setSpotLightCone(instance: Int, inner: Float, outer: Float) { nativeLightManager.setSpotLightCone(instance, inner, outer) }
    actual fun getInnerConeAngle(instance: Int): Float = nativeLightManager.getInnerConeAngle(instance)
    actual fun getOuterConeAngle(instance: Int): Float = nativeLightManager.getOuterConeAngle(instance)
    actual fun setSunAngularRadius(instance: Int, angularRadius: Float) { nativeLightManager.setSunAngularRadius(instance, angularRadius) }
    actual fun getSunAngularRadius(instance: Int): Float = nativeLightManager.getSunAngularRadius(instance)
    actual fun setSunHaloSize(instance: Int, haloSize: Float) { nativeLightManager.setSunHaloSize(instance, haloSize) }
    actual fun getSunHaloSize(instance: Int): Float = nativeLightManager.getSunHaloSize(instance)
    actual fun setSunHaloFalloff(instance: Int, haloFalloff: Float) { nativeLightManager.setSunHaloFalloff(instance, haloFalloff) }
    actual fun getSunHaloFalloff(instance: Int): Float = nativeLightManager.getSunHaloFalloff(instance)
    actual fun setShadowCaster(instance: Int, shadowCaster: Boolean) { nativeLightManager.setShadowCaster(instance, shadowCaster) }
    actual fun isShadowCaster(instance: Int): Boolean = nativeLightManager.isShadowCaster(instance)
    actual fun setLightChannel(instance: Int, channel: Int, enable: Boolean) { nativeLightManager.setLightChannel(instance, channel, enable) }
    actual fun getLightChannel(instance: Int, channel: Int): Boolean = nativeLightManager.getLightChannel(instance, channel)
}
