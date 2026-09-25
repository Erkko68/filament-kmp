package io.github.erkko68.filament

import io.github.erkko68.filament.wasm.*

actual class LightManager @InternalFilamentApi constructor(internal val nativeLightManager: Int) {
    actual enum class Type { SUN, DIRECTIONAL, POINT, FOCUSED_SPOT, SPOT }

    actual class ShadowOptions actual constructor() {
        internal val nativeOptions = FilaLightManagerShadowOptions(fila.allocZeroed(FilaLightManagerShadowOptions.SIZE))
        
        init {
            nativeOptions.mapSize = 1024
            nativeOptions.shadowCascades = 1
            nativeOptions.cascadeSplitPositions[0] = 0.125f
            nativeOptions.cascadeSplitPositions[1] = 0.25f
            nativeOptions.cascadeSplitPositions[2] = 0.50f
            nativeOptions.constantBias = 0.001f
            nativeOptions.normalBias = 1.0f
            nativeOptions.shadowFar = 0.0f
            nativeOptions.shadowNearHint = 1.0f
            nativeOptions.shadowFarHint = 100.0f
            nativeOptions.stable = false
            nativeOptions.lispsm = false  // match Android binding + cleaner PCSS (Filament C++ defaults true)
            nativeOptions.polygonOffsetConstant = 0.5f
            nativeOptions.polygonOffsetSlope = 2.0f
            nativeOptions.screenSpaceContactShadows = false
            nativeOptions.stepCount = 8
            nativeOptions.maxShadowDistance = 0.3f
            nativeOptions.vsm.elvsm = false
            nativeOptions.vsm.blurWidth = 0.0f
            nativeOptions.shadowBulbRadius = -1.0f
            // Identity quaternion (x,y,z,w) = (0,0,0,1). A zero/garbage transform collapses the
            // directional shadow frustum, so shadows silently fail to render.
            nativeOptions.transform[0] = 0.0f
            nativeOptions.transform[1] = 0.0f
            nativeOptions.transform[2] = 0.0f
            nativeOptions.transform[3] = 1.0f
            // 0 means "defer to the View-wide SoftShadowOptions" — matches Filament's defaults.
            nativeOptions.penumbraScale = 1.0f
            nativeOptions.penumbraRatioScale = 1.0f
            // 0 means "defer to the View-wide View.SoftShadowOptions" — matches Filament's defaults.
            nativeOptions.maxPenumbraRatio = 0.0f
            nativeOptions.maxSearchRadius = 0.0f
        }

        actual var mapSize: Int
            get() = nativeOptions.mapSize.toInt()
            set(value) { nativeOptions.mapSize = value }
            
        actual var shadowCascades: Int
            get() = nativeOptions.shadowCascades.toInt()
            set(value) { nativeOptions.shadowCascades = value }
            
        actual var cascadeSplitPositions: FloatArray
            get() = FloatArray(3) { nativeOptions.cascadeSplitPositions[it] }
            set(value) { for (i in 0 until 3.coerceAtMost(value.size)) nativeOptions.cascadeSplitPositions[i] = value[i] }
            
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
            get() = nativeOptions.stepCount.toInt()
            set(value) { nativeOptions.stepCount = value }
            
        actual var maxShadowDistance: Float
            get() = nativeOptions.maxShadowDistance
            set(value) { nativeOptions.maxShadowDistance = value }
            
        actual var elvsm: Boolean
            get() = nativeOptions.vsm.elvsm
            set(value) { nativeOptions.vsm.elvsm = value }
            
        actual var blurWidth: Float
            get() = nativeOptions.vsm.blurWidth
            set(value) { nativeOptions.vsm.blurWidth = value }
            
        actual var shadowBulbRadius: Float
            get() = nativeOptions.shadowBulbRadius
            set(value) { nativeOptions.shadowBulbRadius = value }
            
        actual var transform: FloatArray
            get() = FloatArray(4) { nativeOptions.transform[it] }
            set(value) { for (i in 0 until 4.coerceAtMost(value.size)) nativeOptions.transform[i] = value[i] }

        actual var polygonOffsetConstant: Float
            get() = nativeOptions.polygonOffsetConstant
            set(value) { nativeOptions.polygonOffsetConstant = value }

        actual var polygonOffsetSlope: Float
            get() = nativeOptions.polygonOffsetSlope
            set(value) { nativeOptions.polygonOffsetSlope = value }

        actual var penumbraScale: Float
            get() = nativeOptions.penumbraScale
            set(value) { nativeOptions.penumbraScale = value }

        actual var penumbraRatioScale: Float
            get() = nativeOptions.penumbraRatioScale
            set(value) { nativeOptions.penumbraRatioScale = value }

        actual var maxPenumbraRatio: Float
            get() = nativeOptions.maxPenumbraRatio
            set(value) { nativeOptions.maxPenumbraRatio = value }

        actual var maxSearchRadius: Float
            get() = nativeOptions.maxSearchRadius
            set(value) { nativeOptions.maxSearchRadius = value }
    }

    actual object ShadowCascades {
        actual fun computeUniformSplits(splitPositions: FloatArray, cascades: Int) {
            splitPositions.usePinned { pinned ->
                FilaLightManager_computeUniformSplits(pinned, cascades)
            }
        }
        actual fun computeLogSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float) {
            splitPositions.usePinned { pinned ->
                FilaLightManager_computeLogSplits(pinned, cascades, near, far)
            }
        }
        actual fun computePracticalSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float, lambda: Float) {
            splitPositions.usePinned { pinned ->
                FilaLightManager_computePracticalSplits(pinned, cascades, near, far, lambda)
            }
        }
    }

    actual class Builder actual constructor(type: Type) {
        private val nativeBuilder = FilaLightManagerBuilder_create(type.ordinal)
        
        actual fun lightChannel(channel: Int, enable: Boolean): Builder = apply { FilaLightManagerBuilder_lightChannel(nativeBuilder, channel, enable) }
        actual fun castShadows(enable: Boolean): Builder = apply { FilaLightManagerBuilder_castShadows(nativeBuilder, enable) }
        actual fun shadowOptions(options: ShadowOptions): Builder = apply { FilaLightManagerBuilder_shadowOptions(nativeBuilder, options.nativeOptions.ptr) }
        actual fun castLight(enabled: Boolean): Builder = apply { FilaLightManagerBuilder_castLight(nativeBuilder, enabled) }
        actual fun position(x: Float, y: Float, z: Float): Builder = apply { FilaLightManagerBuilder_position(nativeBuilder, x, y, z) }
        actual fun direction(x: Float, y: Float, z: Float): Builder = apply { FilaLightManagerBuilder_direction(nativeBuilder, x, y, z) }
        actual fun color(linearR: Float, linearG: Float, linearB: Float): Builder = apply { FilaLightManagerBuilder_color(nativeBuilder, linearR, linearG, linearB) }
        actual fun intensity(intensity: Float): Builder = apply { FilaLightManagerBuilder_intensity(nativeBuilder, intensity) }
        actual fun intensity(watts: Float, efficiency: Float): Builder = apply { FilaLightManagerBuilder_intensityEfficiency(nativeBuilder, watts, efficiency) }
        actual fun intensityCandela(intensity: Float): Builder = apply { FilaLightManagerBuilder_intensityCandela(nativeBuilder, intensity) }
        actual fun falloff(radius: Float): Builder = apply { FilaLightManagerBuilder_falloff(nativeBuilder, radius) }
        actual fun spotLightCone(inner: Float, outer: Float): Builder = apply { FilaLightManagerBuilder_spotLightCone(nativeBuilder, inner, outer) }
        actual fun sunAngularRadius(angularRadius: Float): Builder = apply { FilaLightManagerBuilder_sunAngularRadius(nativeBuilder, angularRadius) }
        actual fun sunHaloSize(haloSize: Float): Builder = apply { FilaLightManagerBuilder_sunHaloSize(nativeBuilder, haloSize) }
        actual fun sunHaloFalloff(haloFalloff: Float): Builder = apply { FilaLightManagerBuilder_sunHaloFalloff(nativeBuilder, haloFalloff) }
        actual fun build(engine: Engine, entity: Entity) {
            FilaLightManagerBuilder_build(nativeBuilder, engine.nativeHandle, entity)
            FilaLightManagerBuilder_destroy(nativeBuilder)
        }
    }

    actual val componentCount: Int get() = FilaLightManager_getComponentCount(nativeLightManager).toInt()
    actual fun hasComponent(entity: Entity): Boolean = FilaLightManager_hasComponent(nativeLightManager, entity)
    actual fun getInstance(entity: Entity): EntityInstance = FilaLightManager_getInstance(nativeLightManager, entity).toInt()
    actual fun destroy(entity: Entity) { FilaLightManager_destroy(nativeLightManager, entity) }

    actual fun getType(instance: EntityInstance): Type = Type.entries[FilaLightManager_getType(nativeLightManager, instance).toInt()]
    actual fun setDirection(instance: EntityInstance, x: Float, y: Float, z: Float) { FilaLightManager_setDirection(nativeLightManager, instance, x, y, z) }
    actual fun getDirection(instance: EntityInstance, out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        result.usePinned { pinned ->
            FilaLightManager_getDirection(nativeLightManager, instance, pinned)
        }
        return result
    }
    actual fun setPosition(instance: EntityInstance, x: Float, y: Float, z: Float) { FilaLightManager_setPosition(nativeLightManager, instance, x, y, z) }
    actual fun getPosition(instance: EntityInstance, out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        result.usePinned { pinned ->
            FilaLightManager_getPosition(nativeLightManager, instance, pinned)
        }
        return result
    }
    actual fun setColor(instance: EntityInstance, r: Float, g: Float, b: Float) { FilaLightManager_setColor(nativeLightManager, instance, r, g, b) }
    actual fun getColor(instance: EntityInstance, out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        result.usePinned { pinned ->
            FilaLightManager_getColor(nativeLightManager, instance, pinned)
        }
        return result
    }
    actual fun setIntensity(instance: EntityInstance, intensity: Float) { FilaLightManager_setIntensity(nativeLightManager, instance, intensity) }
    actual fun setIntensity(instance: EntityInstance, watts: Float, efficiency: Float) { FilaLightManager_setIntensityEfficiency(nativeLightManager, instance, watts, efficiency) }
    actual fun setIntensityCandela(instance: EntityInstance, intensity: Float) { FilaLightManager_setIntensityCandela(nativeLightManager, instance, intensity) }
    actual fun getIntensity(instance: EntityInstance): Float = FilaLightManager_getIntensity(nativeLightManager, instance)
    actual fun setFalloff(instance: EntityInstance, radius: Float) { FilaLightManager_setFalloff(nativeLightManager, instance, radius) }
    actual fun getFalloff(instance: EntityInstance): Float = FilaLightManager_getFalloff(nativeLightManager, instance)
    actual fun setSpotLightCone(instance: EntityInstance, inner: Float, outer: Float) { FilaLightManager_setSpotLightCone(nativeLightManager, instance, inner, outer) }
    actual fun getInnerConeAngle(instance: EntityInstance): Float = FilaLightManager_getSpotLightInnerCone(nativeLightManager, instance)
    actual fun getOuterConeAngle(instance: EntityInstance): Float = FilaLightManager_getSpotLightOuterCone(nativeLightManager, instance)
    actual fun setSunAngularRadius(instance: EntityInstance, angularRadius: Float) { FilaLightManager_setSunAngularRadius(nativeLightManager, instance, angularRadius) }
    actual fun getSunAngularRadius(instance: EntityInstance): Float = FilaLightManager_getSunAngularRadius(nativeLightManager, instance)
    actual fun setSunHaloSize(instance: EntityInstance, haloSize: Float) { FilaLightManager_setSunHaloSize(nativeLightManager, instance, haloSize) }
    actual fun getSunHaloSize(instance: EntityInstance): Float = FilaLightManager_getSunHaloSize(nativeLightManager, instance)
    actual fun setSunHaloFalloff(instance: EntityInstance, haloFalloff: Float) { FilaLightManager_setSunHaloFalloff(nativeLightManager, instance, haloFalloff) }
    actual fun getSunHaloFalloff(instance: EntityInstance): Float = FilaLightManager_getSunHaloFalloff(nativeLightManager, instance)
    actual fun setShadowCaster(instance: EntityInstance, shadowCaster: Boolean) { FilaLightManager_setShadowCaster(nativeLightManager, instance, shadowCaster) }
    actual fun isShadowCaster(instance: EntityInstance): Boolean = FilaLightManager_isShadowCaster(nativeLightManager, instance)
    actual fun setLightChannel(instance: EntityInstance, channel: Int, enable: Boolean) { FilaLightManager_setLightChannel(nativeLightManager, instance, channel, enable) }
    actual fun getLightChannel(instance: EntityInstance, channel: Int): Boolean = FilaLightManager_getLightChannel(nativeLightManager, instance, channel)
}
