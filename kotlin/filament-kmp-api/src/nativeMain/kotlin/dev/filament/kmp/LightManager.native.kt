@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package dev.filament.kmp

import dev.filament.kmp.cinterop.*
import kotlinx.cinterop.*
import cnames.structs.FilaLightManager
import cnames.structs.FilaLightManagerBuilder

actual class LightManager internal constructor(internal var nativeHandle: CPointer<FilaLightManager>?) {
    actual val componentCount: Int
        get() = FilaLightManager_getComponentCount(nativeHandle).toInt()

    actual fun hasComponent(@Entity entity: Int): Boolean = FilaLightManager_hasComponent(nativeHandle, entity.toUInt())

    @EntityInstance
    actual fun getInstance(@Entity entity: Int): Int = FilaLightManager_getInstance(nativeHandle, entity.toUInt()).toInt()

    actual fun destroy(@Entity entity: Int) {
        FilaLightManager_destroy(nativeHandle, entity.toUInt())
    }

    actual fun getType(@EntityInstance i: Int): Type =
        Type.entries[FilaLightManager_getType(nativeHandle, i.toUInt()).toInt()]

    actual fun setLightChannel(@EntityInstance i: Int, channel: Int, enable: Boolean) {
        FilaLightManager_setLightChannel(nativeHandle, i.toUInt(), channel.toUInt(), enable)
    }

    actual fun getLightChannel(@EntityInstance i: Int, channel: Int): Boolean =
        FilaLightManager_getLightChannel(nativeHandle, i.toUInt(), channel.toUInt())

    actual fun setPosition(@EntityInstance i: Int, x: Float, y: Float, z: Float) {
        FilaLightManager_setPosition(nativeHandle, i.toUInt(), x, y, z)
    }

    actual fun getPosition(@EntityInstance i: Int, out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        result.usePinned { pinned ->
            FilaLightManager_getPosition(nativeHandle, i.toUInt(), pinned.addressOf(0))
        }
        return result
    }

    actual fun setDirection(@EntityInstance i: Int, x: Float, y: Float, z: Float) {
        FilaLightManager_setDirection(nativeHandle, i.toUInt(), x, y, z)
    }

    actual fun getDirection(@EntityInstance i: Int, out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        result.usePinned { pinned ->
            FilaLightManager_getDirection(nativeHandle, i.toUInt(), pinned.addressOf(0))
        }
        return result
    }

    actual fun setColor(@EntityInstance i: Int, linearR: Float, linearG: Float, linearB: Float) {
        FilaLightManager_setColor(nativeHandle, i.toUInt(), linearR, linearG, linearB)
    }

    actual fun getColor(@EntityInstance i: Int, out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(3)
        result.usePinned { pinned ->
            FilaLightManager_getColor(nativeHandle, i.toUInt(), pinned.addressOf(0))
        }
        return result
    }

    actual fun setIntensity(@EntityInstance i: Int, intensity: Float) {
        FilaLightManager_setIntensity(nativeHandle, i.toUInt(), intensity)
    }

    actual fun setIntensityCandela(@EntityInstance i: Int, intensity: Float) {
        FilaLightManager_setIntensityCandela(nativeHandle, i.toUInt(), intensity)
    }

    actual fun setIntensity(@EntityInstance i: Int, watts: Float, efficiency: Float) {
        FilaLightManager_setIntensityEfficiency(nativeHandle, i.toUInt(), watts, efficiency)
    }

    actual fun getIntensity(@EntityInstance i: Int): Float = FilaLightManager_getIntensity(nativeHandle, i.toUInt())

    actual fun setFalloff(@EntityInstance i: Int, falloff: Float) {
        FilaLightManager_setFalloff(nativeHandle, i.toUInt(), falloff)
    }

    actual fun getFalloff(@EntityInstance i: Int): Float = FilaLightManager_getFalloff(nativeHandle, i.toUInt())

    actual fun setSpotLightCone(@EntityInstance i: Int, inner: Float, outer: Float) {
        FilaLightManager_setSpotLightCone(nativeHandle, i.toUInt(), inner, outer)
    }

    actual fun setSunAngularRadius(@EntityInstance i: Int, angularRadius: Float) {
        FilaLightManager_setSunAngularRadius(nativeHandle, i.toUInt(), angularRadius)
    }

    actual fun getSunAngularRadius(@EntityInstance i: Int): Float = FilaLightManager_getSunAngularRadius(nativeHandle, i.toUInt())

    actual fun setSunHaloSize(@EntityInstance i: Int, haloSize: Float) {
        FilaLightManager_setSunHaloSize(nativeHandle, i.toUInt(), haloSize)
    }

    actual fun getSunHaloSize(@EntityInstance i: Int): Float = FilaLightManager_getSunHaloSize(nativeHandle, i.toUInt())

    actual fun setSunHaloFalloff(@EntityInstance i: Int, haloFalloff: Float) {
        FilaLightManager_setSunHaloFalloff(nativeHandle, i.toUInt(), haloFalloff)
    }

    actual fun getSunHaloFalloff(@EntityInstance i: Int): Float = FilaLightManager_getSunHaloFalloff(nativeHandle, i.toUInt())

    actual fun setShadowCaster(@EntityInstance i: Int, shadowCaster: Boolean) {
        FilaLightManager_setShadowCaster(nativeHandle, i.toUInt(), shadowCaster)
    }

    actual fun isShadowCaster(@EntityInstance i: Int): Boolean = FilaLightManager_isShadowCaster(nativeHandle, i.toUInt())

    actual fun getOuterConeAngle(@EntityInstance i: Int): Float = FilaLightManager_getSpotLightOuterCone(nativeHandle, i.toUInt())

    actual fun getInnerConeAngle(@EntityInstance i: Int): Float = FilaLightManager_getSpotLightInnerCone(nativeHandle, i.toUInt())

    actual val nativeObject: Long
        get() = nativeHandle?.rawValue?.toLong() ?: 0L

    actual class Builder actual constructor(type: Type) {
        private val nativeBuilder = FilaLightManagerBuilder_create(type.toFila())

        actual fun lightChannel(channel: Int, enable: Boolean): Builder {
            FilaLightManagerBuilder_lightChannel(nativeBuilder, channel.toUInt(), enable)
            return this
        }

        actual fun castShadows(enable: Boolean): Builder {
            FilaLightManagerBuilder_castShadows(nativeBuilder, enable)
            return this
        }

        actual fun shadowOptions(options: ShadowOptions): Builder {
            memScoped {
                val nativeOptions = alloc<FilaLightManagerShadowOptions>()
                nativeOptions.mapSize = options.mapSize.toUInt()
                nativeOptions.shadowCascades = options.shadowCascades.toUInt()
                for (i in 0 until 3) {
                    nativeOptions.cascadeSplitPositions[i] = options.cascadeSplitPositions[i]
                }
                nativeOptions.constantBias = options.constantBias
                nativeOptions.normalBias = options.normalBias
                nativeOptions.shadowFar = options.shadowFar
                nativeOptions.shadowNearHint = options.shadowNearHint
                nativeOptions.shadowFarHint = options.shadowFarHint
                nativeOptions.stable = options.stable
                nativeOptions.lispsm = options.lispsm
                nativeOptions.screenSpaceContactShadows = options.screenSpaceContactShadows
                nativeOptions.stepCount = options.stepCount.toUByte()
                nativeOptions.maxShadowDistance = options.maxShadowDistance
                nativeOptions.vsm.elvsm = options.elvsm
                nativeOptions.vsm.blurWidth = options.blurWidth
                nativeOptions.shadowBulbRadius = options.shadowBulbRadius
                for (i in 0 until 4) {
                    nativeOptions.transform[i] = options.transform[i]
                }
                FilaLightManagerBuilder_shadowOptions(nativeBuilder, nativeOptions.ptr)
            }
            return this
        }

        actual fun castLight(enabled: Boolean): Builder {
            FilaLightManagerBuilder_castLight(nativeBuilder, enabled)
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

        actual fun build(engine: Engine, @Entity entity: Int) {
            FilaLightManagerBuilder_build(nativeBuilder, engine.nativeHandle, entity.toUInt())
            FilaLightManagerBuilder_destroy(nativeBuilder)
        }
    }

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
        actual var lispsm: Boolean = false
        actual var screenSpaceContactShadows: Boolean = false
        actual var stepCount: Int = 8
        actual var maxShadowDistance: Float = 0.3f
        actual var elvsm: Boolean = false
        actual var blurWidth: Float = 0.0f
        actual var shadowBulbRadius: Float = 0.02f
        actual var transform: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
    }

    actual object ShadowCascades {
        actual fun computeUniformSplits(splitPositions: FloatArray, cascades: Int) {
            splitPositions.usePinned { pinned ->
                FilaLightManager_computeUniformSplits(pinned.addressOf(0), cascades.toUByte())
            }
        }

        actual fun computeLogSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float) {
            splitPositions.usePinned { pinned ->
                FilaLightManager_computeLogSplits(pinned.addressOf(0), cascades.toUByte(), near, far)
            }
        }

        actual fun computePracticalSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float, lambda: Float) {
            splitPositions.usePinned { pinned ->
                FilaLightManager_computePracticalSplits(pinned.addressOf(0), cascades.toUByte(), near, far, lambda)
            }
        }
    }

    actual enum class Type {
        SUN,
        DIRECTIONAL,
        POINT,
        FOCUSED_SPOT,
        SPOT,
    }

    internal fun Type.toFila() = when (this) {
        Type.SUN -> FILA_LIGHT_MANAGER_TYPE_DIRECTIONAL // SUN is often directional with specific properties
        Type.DIRECTIONAL -> FILA_LIGHT_MANAGER_TYPE_DIRECTIONAL
        Type.POINT -> FILA_LIGHT_MANAGER_TYPE_POINT
        Type.FOCUSED_SPOT -> FILA_LIGHT_MANAGER_TYPE_FOCUSED_SPOT
        Type.SPOT -> FILA_LIGHT_MANAGER_TYPE_SPOT
    }

    actual companion object {
        actual val EFFICIENCY_INCANDESCENT: Float = 0.0220f
        actual val EFFICIENCY_HALOGEN: Float = 0.0707f
        actual val EFFICIENCY_FLUORESCENT: Float = 0.0878f
        actual val EFFICIENCY_LED: Float = 0.1171f
    }
}
