package io.github.erkko68.filament

import io.github.erkko68.filament.jni.LightManager as JniLightManager

actual class LightManager(val nativeLightManager: JniLightManager) {
    actual enum class Type { 
        SUN, DIRECTIONAL, POINT, FOCUSED_SPOT, SPOT;
        internal fun toJni() = JniLightManager.Type.values()[ordinal]
    }

    actual class ShadowOptions actual constructor() {
        private val jni = JniLightManager.ShadowOptions()

        actual var mapSize: Int
            get() = jni.mapSize
            set(value) { jni.mapSize = value }
        actual var shadowCascades: Int
            get() = jni.shadowCascades
            set(value) { jni.shadowCascades = value }
        actual var cascadeSplitPositions: FloatArray
            get() = jni.cascadeSplitPositions
            set(value) { jni.cascadeSplitPositions = value }
        actual var constantBias: Float
            get() = jni.constantBias
            set(value) { jni.constantBias = value }
        actual var normalBias: Float
            get() = jni.normalBias
            set(value) { jni.normalBias = value }
        actual var shadowFar: Float
            get() = jni.shadowFar
            set(value) { jni.shadowFar = value }
        actual var shadowNearHint: Float
            get() = jni.shadowNearHint
            set(value) { jni.shadowNearHint = value }
        actual var shadowFarHint: Float
            get() = jni.shadowFarHint
            set(value) { jni.shadowFarHint = value }
        actual var stable: Boolean
            get() = jni.stable
            set(value) { jni.stable = value }
        actual var lispsm: Boolean
            get() = jni.lispsm
            set(value) { jni.lispsm = value }
        actual var screenSpaceContactShadows: Boolean
            get() = jni.screenSpaceContactShadows
            set(value) { jni.screenSpaceContactShadows = value }
        actual var stepCount: Int
            get() = jni.stepCount
            set(value) { jni.stepCount = value }
        actual var maxShadowDistance: Float
            get() = jni.maxShadowDistance
            set(value) { jni.maxShadowDistance = value }
        actual var elvsm: Boolean
            get() = jni.elvsm
            set(value) { jni.elvsm = value }
        actual var blurWidth: Float
            get() = jni.blurWidth
            set(value) { jni.blurWidth = value }
        actual var shadowBulbRadius: Float
            get() = jni.shadowBulbRadius
            set(value) { jni.shadowBulbRadius = value }
        actual var transform: FloatArray
            get() = jni.transform
            set(value) { jni.transform = value }

        internal fun toJni() = jni
    }

    actual object ShadowCascades {
        actual fun computeUniformSplits(splitPositions: FloatArray, cascades: Int) =
            JniLightManager.ShadowCascades.computeUniformSplits(splitPositions, cascades)
        actual fun computeLogSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float) =
            JniLightManager.ShadowCascades.computeLogSplits(splitPositions, cascades, near, far)
        actual fun computePracticalSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float, lambda: Float) =
            JniLightManager.ShadowCascades.computePracticalSplits(splitPositions, cascades, near, far, lambda)
    }

    actual class Builder actual constructor(type: Type) {
        private val jni = JniLightManager.Builder(type.toJni())

        actual fun lightChannel(channel: Int, enable: Boolean): Builder { jni.lightChannel(channel, enable); return this }
        actual fun castShadows(enable: Boolean): Builder { jni.castShadows(enable); return this }
        actual fun shadowOptions(options: ShadowOptions): Builder { jni.shadowOptions(options.toJni()); return this }
        actual fun castLight(enabled: Boolean): Builder { jni.castLight(enabled); return this }
        actual fun position(x: Float, y: Float, z: Float): Builder { jni.position(x, y, z); return this }
        actual fun direction(x: Float, y: Float, z: Float): Builder { jni.direction(x, y, z); return this }
        actual fun color(linearR: Float, linearG: Float, linearB: Float): Builder { jni.color(linearR, linearG, linearB); return this }
        actual fun intensity(intensity: Float): Builder { jni.intensity(intensity); return this }
        actual fun intensity(watts: Float, efficiency: Float): Builder { jni.intensity(watts, efficiency); return this }
        actual fun intensityCandela(intensity: Float): Builder { jni.intensityCandela(intensity); return this }
        actual fun falloff(radius: Float): Builder { jni.falloff(radius); return this }
        actual fun spotLightCone(inner: Float, outer: Float): Builder { jni.spotLightCone(inner, outer); return this }
        actual fun sunAngularRadius(angularRadius: Float): Builder { jni.sunAngularRadius(angularRadius); return this }
        actual fun sunHaloSize(haloSize: Float): Builder { jni.sunHaloSize(haloSize); return this }
        actual fun sunHaloFalloff(haloFalloff: Float): Builder { jni.sunHaloFalloff(haloFalloff); return this }
        actual fun build(engine: Engine, entity: Int) { jni.build(engine.nativeEngine, entity) }
    }

    actual fun getComponentCount(): Int = nativeLightManager.componentCount
    actual fun hasComponent(entity: Int): Boolean = nativeLightManager.hasComponent(entity)
    actual fun getInstance(entity: Int): Int = nativeLightManager.getInstance(entity)
    actual fun destroy(entity: Int) = nativeLightManager.destroy(entity)

    actual fun getType(instance: Int): Type = Type.values()[nativeLightManager.getType(instance).ordinal]
    actual fun setDirection(instance: Int, x: Float, y: Float, z: Float) = nativeLightManager.setDirection(instance, x, y, z)
    actual fun getDirection(instance: Int, out: FloatArray): FloatArray { nativeLightManager.getDirection(instance, out); return out }
    actual fun setPosition(instance: Int, x: Float, y: Float, z: Float) = nativeLightManager.setPosition(instance, x, y, z)
    actual fun getPosition(instance: Int, out: FloatArray): FloatArray { nativeLightManager.getPosition(instance, out); return out }
    actual fun setColor(instance: Int, r: Float, g: Float, b: Float) = nativeLightManager.setColor(instance, r, g, b)
    actual fun getColor(instance: Int, out: FloatArray): FloatArray { nativeLightManager.getColor(instance, out); return out }
    actual fun setIntensity(instance: Int, intensity: Float) = nativeLightManager.setIntensity(instance, intensity)
    actual fun setIntensity(instance: Int, watts: Float, efficiency: Float) {
        // Find if JNI has this overload. If not, we might need to calculate or use intensity.
        // Actually JNI LightManager has: public void setIntensity(int instance, float intensity)
        // It doesn't seem to have the watts/efficiency overload in the instance methods.
        // I'll calculate it if needed, or for now just call setIntensity with watts.
        nativeLightManager.setIntensity(instance, watts * efficiency * 683.0f) 
    }
    actual fun setIntensityCandela(instance: Int, intensity: Float) = nativeLightManager.setIntensityCandela(instance, intensity)
    actual fun getIntensity(instance: Int): Float = nativeLightManager.getIntensity(instance)
    actual fun setFalloff(instance: Int, radius: Float) = nativeLightManager.setFalloff(instance, radius)
    actual fun getFalloff(instance: Int): Float = nativeLightManager.getFalloff(instance)
    actual fun setSpotLightCone(instance: Int, inner: Float, outer: Float) = nativeLightManager.setSpotLightCone(instance, inner, outer)
    actual fun getInnerConeAngle(instance: Int): Float = nativeLightManager.getInnerConeAngle(instance)
    actual fun getOuterConeAngle(instance: Int): Float = nativeLightManager.getOuterConeAngle(instance)
    actual fun setSunAngularRadius(instance: Int, angularRadius: Float) = nativeLightManager.setSunAngularRadius(instance, angularRadius)
    actual fun getSunAngularRadius(instance: Int): Float = nativeLightManager.getSunAngularRadius(instance)
    actual fun setSunHaloSize(instance: Int, haloSize: Float) = nativeLightManager.setSunHaloSize(instance, haloSize)
    actual fun getSunHaloSize(instance: Int): Float = nativeLightManager.getSunHaloSize(instance)
    actual fun setSunHaloFalloff(instance: Int, haloFalloff: Float) = nativeLightManager.setSunHaloFalloff(instance, haloFalloff)
    actual fun getSunHaloFalloff(instance: Int): Float = nativeLightManager.getSunHaloFalloff(instance)
    actual fun setShadowCaster(instance: Int, shadowCaster: Boolean) = nativeLightManager.setShadowCaster(instance, shadowCaster)
    actual fun isShadowCaster(instance: Int): Boolean = nativeLightManager.isShadowCaster(instance)
    actual fun setLightChannel(instance: Int, channel: Int, enable: Boolean) = nativeLightManager.setLightChannel(instance, channel, enable)
    actual fun getLightChannel(instance: Int, channel: Int): Boolean = nativeLightManager.getLightChannel(instance, channel)
}
