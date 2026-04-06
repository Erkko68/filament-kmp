@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaLightManager
import cnames.structs.FilaLightManagerBuilder

actual class LightManager internal constructor(internal val nativeHandle: CPointer<FilaLightManager>?) {
    actual enum class Type { 
        SUN, DIRECTIONAL, POINT, FOCUSED_SPOT, SPOT;
        internal fun toNative(): FilaLightManagerType = ordinal.toUInt()
    }

    actual class ShadowOptions actual constructor() {
        actual var mapSize: Int = 1024
        actual var shadowCascades: Int = 1
        actual var cascadeSplitPositions: FloatArray = floatArrayOf(0.125f, 0.25f, 0.50f)
        actual var constantBias: Float = 0.001f
        actual var normalBias: Float = 1.0f
        actual var shadowFar: Float = 0.0f
        actual var shadowNearHint: Float = 1.0f
        actual var shadowFarHint: Float = 100.0f
        actual var stable: Boolean = false
        actual var lispsm: Boolean = false
        actual var screenSpaceContactShadows: Boolean = false
        actual var stepCount: Int = 8
        actual var maxShadowDistance: Float = 0.3f
        actual var elvsm: Boolean = false
        actual var blurWidth: Float = 0.0f
        actual var shadowBulbRadius: Float = 0.02f
        actual var transform: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
        
        internal fun toNative(native: FilaLightManagerShadowOptions) {
            native.mapSize = mapSize.toUInt()
            native.shadowCascades = shadowCascades.toUInt()
            for (i in 0 until 3) native.cascadeSplitPositions[i] = cascadeSplitPositions[i]
            native.constantBias = constantBias
            native.normalBias = normalBias
            native.shadowFar = shadowFar
            native.shadowNearHint = shadowNearHint
            native.shadowFarHint = shadowFarHint
            native.stable = stable
            native.lispsm = lispsm
            native.screenSpaceContactShadows = screenSpaceContactShadows
            native.stepCount = stepCount.toUByte()
            native.maxShadowDistance = maxShadowDistance
            native.vsm.elvsm = elvsm
            native.vsm.blurWidth = blurWidth
            native.shadowBulbRadius = shadowBulbRadius
            for (i in 0 until 4) native.transform[i] = transform[i]
        }
    }

    actual class Builder actual constructor(type: Type) {
        private val nativeBuilder = FilaLightManagerBuilder_create(type.toNative())!!

        actual fun castShadows(enable: Boolean): Builder {
            FilaLightManagerBuilder_castShadows(nativeBuilder, enable)
            return this
        }
        actual fun shadowOptions(options: ShadowOptions): Builder {
            memScoped {
                val nativeOptions = alloc<FilaLightManagerShadowOptions>()
                options.toNative(nativeOptions)
                FilaLightManagerBuilder_shadowOptions(nativeBuilder, nativeOptions.ptr)
            }
            return this
        }
        actual fun castLight(enable: Boolean): Builder {
            FilaLightManagerBuilder_castLight(nativeBuilder, enable)
            return this
        }
        actual fun position(x: Float, y: Float, z: Float): Builder {
            FilaLightManagerBuilder_position(nativeBuilder, x, y, z)
            return this
        }
        actual fun direction(x: Float, y: Float, z: Float): Builder {
            FilaLightManagerBuilder_direction(nativeBuilder, x, y, z)
            return this
        }
        actual fun color(linearR: Float, linearG: Float, linearB: Float): Builder {
            FilaLightManagerBuilder_color(nativeBuilder, linearR, linearG, linearB)
            return this
        }
        actual fun intensity(intensity: Float): Builder {
            FilaLightManagerBuilder_intensity(nativeBuilder, intensity)
            return this
        }
        actual fun intensity(watts: Float, efficiency: Float): Builder {
            FilaLightManagerBuilder_intensityEfficiency(nativeBuilder, watts, efficiency)
            return this
        }
        actual fun intensityCandela(intensity: Float): Builder {
            FilaLightManagerBuilder_intensityCandela(nativeBuilder, intensity)
            return this
        }
        actual fun falloff(radius: Float): Builder {
            FilaLightManagerBuilder_falloff(nativeBuilder, radius)
            return this
        }
        actual fun spotLightCone(inner: Float, outer: Float): Builder {
            FilaLightManagerBuilder_spotLightCone(nativeBuilder, inner, outer)
            return this
        }
        actual fun sunAngularRadius(angularRadius: Float): Builder {
            FilaLightManagerBuilder_sunAngularRadius(nativeBuilder, angularRadius)
            return this
        }
        actual fun sunHaloSize(haloSize: Float): Builder {
            FilaLightManagerBuilder_sunHaloSize(nativeBuilder, haloSize)
            return this
        }
        actual fun sunHaloFalloff(haloFalloff: Float): Builder {
            FilaLightManagerBuilder_sunHaloFalloff(nativeBuilder, haloFalloff)
            return this
        }
        actual fun lightChannel(channel: Int, enable: Boolean): Builder {
            FilaLightManagerBuilder_lightChannel(nativeBuilder, channel.toUInt(), enable)
            return this
        }
        actual fun build(engine: Engine, entity: Entity) {
            FilaLightManagerBuilder_build(nativeBuilder, engine.nativeHandle, entity.toUInt())
            FilaLightManagerBuilder_destroy(nativeBuilder)
        }
    }

    actual fun hasComponent(entity: Entity): Boolean = FilaLightManager_hasComponent(nativeHandle, entity.toUInt())
    actual fun getInstance(entity: Entity): EntityInstance = FilaLightManager_getInstance(nativeHandle, entity.toUInt()).toInt()
    actual fun destroy(entity: Entity) = FilaLightManager_destroy(nativeHandle, entity.toUInt())
    
    actual fun setPosition(instance: EntityInstance, x: Float, y: Float, z: Float) = FilaLightManager_setPosition(nativeHandle, instance.toUInt(), x, y, z)
    actual fun getPosition(instance: EntityInstance, outPosition: FloatArray?): FloatArray {
        val result = outPosition ?: FloatArray(3)
        result.usePinned { 
            FilaLightManager_getPosition(nativeHandle, instance.toUInt(), it.addressOf(0))
        }
        return result
    }
    actual fun setDirection(instance: EntityInstance, x: Float, y: Float, z: Float) = FilaLightManager_setDirection(nativeHandle, instance.toUInt(), x, y, z)
    actual fun getDirection(instance: EntityInstance, outDirection: FloatArray?): FloatArray {
        val result = outDirection ?: FloatArray(3)
        result.usePinned { 
            FilaLightManager_getDirection(nativeHandle, instance.toUInt(), it.addressOf(0))
        }
        return result
    }
    actual fun setColor(instance: EntityInstance, r: Float, g: Float, b: Float) = FilaLightManager_setColor(nativeHandle, instance.toUInt(), r, g, b)
    actual fun getColor(instance: EntityInstance, outColor: FloatArray?): FloatArray {
        val result = outColor ?: FloatArray(3)
        result.usePinned { 
            FilaLightManager_getColor(nativeHandle, instance.toUInt(), it.addressOf(0))
        }
        return result
    }
    
    actual fun setIntensity(instance: EntityInstance, intensity: Float) = FilaLightManager_setIntensity(nativeHandle, instance.toUInt(), intensity)
    actual fun setIntensity(instance: EntityInstance, watts: Float, efficiency: Float) = FilaLightManager_setIntensityEfficiency(nativeHandle, instance.toUInt(), watts, efficiency)
    actual fun setIntensityCandela(instance: EntityInstance, intensity: Float) = FilaLightManager_setIntensityCandela(nativeHandle, instance.toUInt(), intensity)
    actual fun getIntensity(instance: EntityInstance): Float = FilaLightManager_getIntensity(nativeHandle, instance.toUInt())
    
    actual fun setFalloff(instance: EntityInstance, radius: Float) = FilaLightManager_setFalloff(nativeHandle, instance.toUInt(), radius)
    actual fun getFalloff(instance: EntityInstance): Float = FilaLightManager_getFalloff(nativeHandle, instance.toUInt())
    
    actual fun setSpotLightCone(instance: EntityInstance, inner: Float, outer: Float) = FilaLightManager_setSpotLightCone(nativeHandle, instance.toUInt(), inner, outer)
    actual fun getSpotLightInnerCone(instance: EntityInstance): Float = FilaLightManager_getSpotLightInnerCone(nativeHandle, instance.toUInt())
    actual fun getSpotLightOuterCone(instance: EntityInstance): Float = FilaLightManager_getSpotLightOuterCone(nativeHandle, instance.toUInt())
    
    actual fun setSunAngularRadius(instance: EntityInstance, angularRadius: Float) = FilaLightManager_setSunAngularRadius(nativeHandle, instance.toUInt(), angularRadius)
    actual fun getSunAngularRadius(instance: EntityInstance): Float = FilaLightManager_getSunAngularRadius(nativeHandle, instance.toUInt())
    actual fun setSunHaloSize(instance: EntityInstance, haloSize: Float) = FilaLightManager_setSunHaloSize(nativeHandle, instance.toUInt(), haloSize)
    actual fun getSunHaloSize(instance: EntityInstance): Float = FilaLightManager_getSunHaloSize(nativeHandle, instance.toUInt())
    actual fun setSunHaloFalloff(instance: EntityInstance, haloFalloff: Float) = FilaLightManager_setSunHaloFalloff(nativeHandle, instance.toUInt(), haloFalloff)
    actual fun getSunHaloFalloff(instance: EntityInstance): Float = FilaLightManager_getSunHaloFalloff(nativeHandle, instance.toUInt())
    
    actual fun setShadowCaster(instance: EntityInstance, shadowCaster: Boolean) = FilaLightManager_setShadowCaster(nativeHandle, instance.toUInt(), shadowCaster)
    actual fun isShadowCaster(instance: EntityInstance): Boolean = FilaLightManager_isShadowCaster(nativeHandle, instance.toUInt())
    
    actual fun setLightChannel(instance: EntityInstance, channel: Int, enable: Boolean) = FilaLightManager_setLightChannel(nativeHandle, instance.toUInt(), channel.toUInt(), enable)
    actual fun getLightChannel(instance: EntityInstance, channel: Int): Boolean = FilaLightManager_getLightChannel(nativeHandle, instance.toUInt(), channel.toUInt())
}
