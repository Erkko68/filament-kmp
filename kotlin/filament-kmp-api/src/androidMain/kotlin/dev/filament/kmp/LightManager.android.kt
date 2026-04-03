package dev.filament.kmp

import com.google.android.filament.LightManager as AndroidLightManager

actual class LightManager internal constructor(
    internal val androidLightManager: AndroidLightManager,
) {
    actual fun getComponentCount(): Int = androidLightManager.componentCount

    actual fun hasComponent(entity: Int): Boolean = androidLightManager.hasComponent(entity)

    actual fun getInstance(entity: Int): Int = androidLightManager.getInstance(entity)

    actual fun destroy(entity: Int) {
        androidLightManager.destroy(entity)
    }

    actual fun getType(i: Int): Type = androidLightManager.getType(i).toKmp()

    actual fun setLightChannel(i: Int, channel: Int, enable: Boolean) {
        androidLightManager.setLightChannel(i, channel, enable)
    }

    actual fun getLightChannel(i: Int, channel: Int): Boolean = androidLightManager.getLightChannel(i, channel)

    actual fun setPosition(i: Int, x: Float, y: Float, z: Float) {
        androidLightManager.setPosition(i, x, y, z)
    }

    actual fun getPosition(i: Int, out: FloatArray?): FloatArray = androidLightManager.getPosition(i, out)

    actual fun setDirection(i: Int, x: Float, y: Float, z: Float) {
        androidLightManager.setDirection(i, x, y, z)
    }

    actual fun getDirection(i: Int, out: FloatArray?): FloatArray = androidLightManager.getDirection(i, out)

    actual fun setColor(i: Int, linearR: Float, linearG: Float, linearB: Float) {
        androidLightManager.setColor(i, linearR, linearG, linearB)
    }

    actual fun getColor(i: Int, out: FloatArray?): FloatArray = androidLightManager.getColor(i, out)

    actual fun setIntensity(i: Int, intensity: Float) {
        androidLightManager.setIntensity(i, intensity)
    }

    actual fun setIntensityCandela(i: Int, intensity: Float) {
        androidLightManager.setIntensityCandela(i, intensity)
    }

    actual fun setIntensity(i: Int, watts: Float, efficiency: Float) {
        androidLightManager.setIntensity(i, watts, efficiency)
    }

    actual fun getIntensity(i: Int): Float = androidLightManager.getIntensity(i)

    actual fun setFalloff(i: Int, falloff: Float) {
        androidLightManager.setFalloff(i, falloff)
    }

    actual fun getFalloff(i: Int): Float = androidLightManager.getFalloff(i)

    actual fun setSpotLightCone(i: Int, inner: Float, outer: Float) {
        androidLightManager.setSpotLightCone(i, inner, outer)
    }

    actual fun setSunAngularRadius(i: Int, angularRadius: Float) {
        androidLightManager.setSunAngularRadius(i, angularRadius)
    }

    actual fun getSunAngularRadius(i: Int): Float = androidLightManager.getSunAngularRadius(i)

    actual fun setSunHaloSize(i: Int, haloSize: Float) {
        androidLightManager.setSunHaloSize(i, haloSize)
    }

    actual fun getSunHaloSize(i: Int): Float = androidLightManager.getSunHaloSize(i)

    actual fun setSunHaloFalloff(i: Int, haloFalloff: Float) {
        androidLightManager.setSunHaloFalloff(i, haloFalloff)
    }

    actual fun getSunHaloFalloff(i: Int): Float = androidLightManager.getSunHaloFalloff(i)

    actual fun setShadowCaster(i: Int, shadowCaster: Boolean) {
        androidLightManager.setShadowCaster(i, shadowCaster)
    }

    actual fun isShadowCaster(i: Int): Boolean = androidLightManager.isShadowCaster(i)

    actual fun getOuterConeAngle(i: Int): Float = androidLightManager.getOuterConeAngle(i)

    actual fun getInnerConeAngle(i: Int): Float = androidLightManager.getInnerConeAngle(i)

    actual fun getNativeObject(): Long = androidLightManager.nativeObject

    actual class Builder actual constructor(type: Type) {
        private val androidBuilder = AndroidLightManager.Builder(type.toAndroid())

        actual fun lightChannel(channel: Int, enable: Boolean): Builder {
            androidBuilder.lightChannel(channel, enable)
            return this
        }

        actual fun castShadows(enable: Boolean): Builder {
            androidBuilder.castShadows(enable)
            return this
        }

        actual fun shadowOptions(options: ShadowOptions): Builder {
            val androidOptions = AndroidLightManager.ShadowOptions().apply {
                mapSize = options.mapSize
                shadowCascades = options.shadowCascades
                cascadeSplitPositions = options.cascadeSplitPositions
                constantBias = options.constantBias
                normalBias = options.normalBias
                shadowFar = options.shadowFar
                shadowNearHint = options.shadowNearHint
                shadowFarHint = options.shadowFarHint
                stable = options.stable
                lispsm = options.lispsm
                screenSpaceContactShadows = options.screenSpaceContactShadows
                stepCount = options.stepCount
                maxShadowDistance = options.maxShadowDistance
                elvsm = options.elvsm
                blurWidth = options.blurWidth
                shadowBulbRadius = options.shadowBulbRadius
                transform = options.transform
            }
            androidBuilder.shadowOptions(androidOptions)
            return this
        }

        actual fun castLight(enabled: Boolean): Builder {
            androidBuilder.castLight(enabled)
            return this
        }

        actual fun position(x: Float, y: Float, z: Float): Builder {
            androidBuilder.position(x, y, z)
            return this
        }

        actual fun direction(x: Float, y: Float, z: Float): Builder {
            androidBuilder.direction(x, y, z)
            return this
        }

        actual fun color(linearR: Float, linearG: Float, linearB: Float): Builder {
            androidBuilder.color(linearR, linearG, linearB)
            return this
        }

        actual fun intensity(intensity: Float): Builder {
            androidBuilder.intensity(intensity)
            return this
        }

        actual fun intensity(watts: Float, efficiency: Float): Builder {
            androidBuilder.intensity(watts, efficiency)
            return this
        }

        actual fun intensityCandela(intensity: Float): Builder {
            androidBuilder.intensityCandela(intensity)
            return this
        }

        actual fun falloff(radius: Float): Builder {
            androidBuilder.falloff(radius)
            return this
        }

        actual fun spotLightCone(inner: Float, outer: Float): Builder {
            androidBuilder.spotLightCone(inner, outer)
            return this
        }

        actual fun sunAngularRadius(angularRadius: Float): Builder {
            androidBuilder.sunAngularRadius(angularRadius)
            return this
        }

        actual fun sunHaloSize(haloSize: Float): Builder {
            androidBuilder.sunHaloSize(haloSize)
            return this
        }

        actual fun sunHaloFalloff(haloFalloff: Float): Builder {
            androidBuilder.sunHaloFalloff(haloFalloff)
            return this
        }

        actual fun build(engine: Engine, entity: Int) {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            androidBuilder.build(androidEngine, entity)
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
            AndroidLightManager.ShadowCascades.computeUniformSplits(splitPositions, cascades)
        }

        actual fun computeLogSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float) {
            AndroidLightManager.ShadowCascades.computeLogSplits(splitPositions, cascades, near, far)
        }

        actual fun computePracticalSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float, lambda: Float) {
            AndroidLightManager.ShadowCascades.computePracticalSplits(splitPositions, cascades, near, far, lambda)
        }
    }

    actual enum class Type {
        SUN,
        DIRECTIONAL,
        POINT,
        FOCUSED_SPOT,
        SPOT,
    }

    actual companion object {
        actual val EFFICIENCY_INCANDESCENT: Float = AndroidLightManager.EFFICIENCY_INCANDESCENT
        actual val EFFICIENCY_HALOGEN: Float = AndroidLightManager.EFFICIENCY_HALOGEN
        actual val EFFICIENCY_FLUORESCENT: Float = AndroidLightManager.EFFICIENCY_FLUORESCENT
        actual val EFFICIENCY_LED: Float = AndroidLightManager.EFFICIENCY_LED
    }
}

private fun LightManager.Type.toAndroid(): AndroidLightManager.Type = when (this) {
    LightManager.Type.SUN -> AndroidLightManager.Type.SUN
    LightManager.Type.DIRECTIONAL -> AndroidLightManager.Type.DIRECTIONAL
    LightManager.Type.POINT -> AndroidLightManager.Type.POINT
    LightManager.Type.FOCUSED_SPOT -> AndroidLightManager.Type.FOCUSED_SPOT
    LightManager.Type.SPOT -> AndroidLightManager.Type.SPOT
}

private fun AndroidLightManager.Type.toKmp(): LightManager.Type = when (this) {
    AndroidLightManager.Type.SUN -> LightManager.Type.SUN
    AndroidLightManager.Type.DIRECTIONAL -> LightManager.Type.DIRECTIONAL
    AndroidLightManager.Type.POINT -> LightManager.Type.POINT
    AndroidLightManager.Type.FOCUSED_SPOT -> LightManager.Type.FOCUSED_SPOT
    AndroidLightManager.Type.SPOT -> LightManager.Type.SPOT
}

