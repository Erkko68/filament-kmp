@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68

import kotlinx.cinterop.*
import io.github.erkko68.cinterop.*
import cnames.structs.FilaLightManager
import cnames.structs.FilaLightManagerBuilder

actual class LightManager internal constructor(val nativeLightManager: CPointer<FilaLightManager>) {
    actual enum class Type { SUN, DIRECTIONAL, POINT, FOCUSED_SPOT, SPOT }

    actual class ShadowOptions actual constructor() {
        internal val nativeOptions = nativeHeap.alloc<FilaLightManagerShadowOptions>()
        
        init {
            nativeOptions.mapSize = 1024u
            nativeOptions.shadowCascades = 1u
            nativeOptions.constantBias = 0.001f
            nativeOptions.normalBias = 0.4f
            nativeOptions.shadowFar = 0.0f
            nativeOptions.shadowNearHint = 0.0f
            nativeOptions.shadowFarHint = 100.0f
            nativeOptions.stable = false
            nativeOptions.lispsm = true
            nativeOptions.screenSpaceContactShadows = false
            nativeOptions.stepCount = 8u.toUByte()
            nativeOptions.maxShadowDistance = 0.0f
            nativeOptions.vsm.elvsm = false
            nativeOptions.vsm.blurWidth = 0.0f
            nativeOptions.shadowBulbRadius = 0.0f
        }

        actual var mapSize: Int
            get() = nativeOptions.mapSize.toInt()
            set(value) { nativeOptions.mapSize = value.toUInt() }
            
        actual var shadowCascades: Int
            get() = nativeOptions.shadowCascades.toInt()
            set(value) { nativeOptions.shadowCascades = value.toUInt() }
            
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
            set(value) { nativeOptions.stepCount = value.toUByte() }
            
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
    }

    actual object ShadowCascades {
        @OptIn(ExperimentalForeignApi::class)
        actual fun computeUniformSplits(splitPositions: FloatArray, cascades: Int) {
            splitPositions.usePinned { pinned ->
                FilaLightManager_computeUniformSplits(pinned.addressOf(0), cascades.toUByte())
            }
        }
        @OptIn(ExperimentalForeignApi::class)
        actual fun computeLogSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float) {
            splitPositions.usePinned { pinned ->
                FilaLightManager_computeLogSplits(pinned.addressOf(0), cascades.toUByte(), near, far)
            }
        }
        @OptIn(ExperimentalForeignApi::class)
        actual fun computePracticalSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float, lambda: Float) {
            splitPositions.usePinned { pinned ->
                FilaLightManager_computePracticalSplits(pinned.addressOf(0), cascades.toUByte(), near, far, lambda)
            }
        }
    }

    actual class Builder actual constructor(type: Type) {
        private val nativeBuilder = FilaLightManagerBuilder_create(type.ordinal.toUInt())!!
        
        actual fun lightChannel(channel: Int, enable: Boolean): Builder = apply { FilaLightManagerBuilder_lightChannel(nativeBuilder, channel.toUInt(), enable) }
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
        actual fun build(engine: Engine, entity: Int) {
            FilaLightManagerBuilder_build(nativeBuilder, engine.nativeHandle!!, entity.toUInt())
            FilaLightManagerBuilder_destroy(nativeBuilder)
        }
    }

    actual fun getComponentCount(): Int = FilaLightManager_getComponentCount(nativeLightManager).toInt()
    actual fun hasComponent(entity: Int): Boolean = FilaLightManager_hasComponent(nativeLightManager, entity.toUInt())
    actual fun getInstance(entity: Int): Int = FilaLightManager_getInstance(nativeLightManager, entity.toUInt()).toInt()
    actual fun destroy(entity: Int) { FilaLightManager_destroy(nativeLightManager, entity.toUInt()) }

    actual fun getType(instance: Int): Type = Type.values()[FilaLightManager_getType(nativeLightManager, instance.toUInt()).toInt()]
    actual fun setDirection(instance: Int, x: Float, y: Float, z: Float) { FilaLightManager_setDirection(nativeLightManager, instance.toUInt(), x, y, z) }
    actual fun getDirection(instance: Int, out: FloatArray): FloatArray {
        out.usePinned { pinned ->
            FilaLightManager_getDirection(nativeLightManager, instance.toUInt(), pinned.addressOf(0))
        }
        return out
    }
    actual fun setPosition(instance: Int, x: Float, y: Float, z: Float) { FilaLightManager_setPosition(nativeLightManager, instance.toUInt(), x, y, z) }
    actual fun getPosition(instance: Int, out: FloatArray): FloatArray {
        out.usePinned { pinned ->
            FilaLightManager_getPosition(nativeLightManager, instance.toUInt(), pinned.addressOf(0))
        }
        return out
    }
    actual fun setColor(instance: Int, r: Float, g: Float, b: Float) { FilaLightManager_setColor(nativeLightManager, instance.toUInt(), r, g, b) }
    actual fun getColor(instance: Int, out: FloatArray): FloatArray {
        out.usePinned { pinned ->
            FilaLightManager_getColor(nativeLightManager, instance.toUInt(), pinned.addressOf(0))
        }
        return out
    }
    actual fun setIntensity(instance: Int, intensity: Float) { FilaLightManager_setIntensity(nativeLightManager, instance.toUInt(), intensity) }
    actual fun setIntensity(instance: Int, watts: Float, efficiency: Float) { FilaLightManager_setIntensityEfficiency(nativeLightManager, instance.toUInt(), watts, efficiency) }
    actual fun setIntensityCandela(instance: Int, intensity: Float) { FilaLightManager_setIntensityCandela(nativeLightManager, instance.toUInt(), intensity) }
    actual fun getIntensity(instance: Int): Float = FilaLightManager_getIntensity(nativeLightManager, instance.toUInt())
    actual fun setFalloff(instance: Int, radius: Float) { FilaLightManager_setFalloff(nativeLightManager, instance.toUInt(), radius) }
    actual fun getFalloff(instance: Int): Float = FilaLightManager_getFalloff(nativeLightManager, instance.toUInt())
    actual fun setSpotLightCone(instance: Int, inner: Float, outer: Float) { FilaLightManager_setSpotLightCone(nativeLightManager, instance.toUInt(), inner, outer) }
    actual fun getInnerConeAngle(instance: Int): Float = FilaLightManager_getSpotLightInnerCone(nativeLightManager, instance.toUInt())
    actual fun getOuterConeAngle(instance: Int): Float = FilaLightManager_getSpotLightOuterCone(nativeLightManager, instance.toUInt())
    actual fun setSunAngularRadius(instance: Int, angularRadius: Float) { FilaLightManager_setSunAngularRadius(nativeLightManager, instance.toUInt(), angularRadius) }
    actual fun getSunAngularRadius(instance: Int): Float = FilaLightManager_getSunAngularRadius(nativeLightManager, instance.toUInt())
    actual fun setSunHaloSize(instance: Int, haloSize: Float) { FilaLightManager_setSunHaloSize(nativeLightManager, instance.toUInt(), haloSize) }
    actual fun getSunHaloSize(instance: Int): Float = FilaLightManager_getSunHaloSize(nativeLightManager, instance.toUInt())
    actual fun setSunHaloFalloff(instance: Int, haloFalloff: Float) { FilaLightManager_setSunHaloFalloff(nativeLightManager, instance.toUInt(), haloFalloff) }
    actual fun getSunHaloFalloff(instance: Int): Float = FilaLightManager_getSunHaloFalloff(nativeLightManager, instance.toUInt())
    actual fun setShadowCaster(instance: Int, shadowCaster: Boolean) { FilaLightManager_setShadowCaster(nativeLightManager, instance.toUInt(), shadowCaster) }
    actual fun isShadowCaster(instance: Int): Boolean = FilaLightManager_isShadowCaster(nativeLightManager, instance.toUInt())
    actual fun setLightChannel(instance: Int, channel: Int, enable: Boolean) { FilaLightManager_setLightChannel(nativeLightManager, instance.toUInt(), channel.toUInt(), enable) }
    actual fun getLightChannel(instance: Int, channel: Int): Boolean = FilaLightManager_getLightChannel(nativeLightManager, instance.toUInt(), channel.toUInt())
}
