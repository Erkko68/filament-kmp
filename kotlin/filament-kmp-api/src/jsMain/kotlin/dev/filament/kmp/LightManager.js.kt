package dev.filament.kmp

actual class LightManager {
    actual fun getComponentCount(): Int = TODO("Not yet implemented")

    actual fun hasComponent(entity: Int): Boolean = TODO("Not yet implemented")

    actual fun getInstance(entity: Int): Int = TODO("Not yet implemented")

    actual fun destroy(entity: Int) {
    }

    actual fun getType(i: Int): Type = TODO("Not yet implemented")

    actual fun setLightChannel(i: Int, channel: Int, enable: Boolean) {
    }

    actual fun getLightChannel(i: Int, channel: Int): Boolean = TODO("Not yet implemented")

    actual fun setPosition(i: Int, x: Float, y: Float, z: Float) {
    }

    actual fun getPosition(i: Int, out: FloatArray?): FloatArray = TODO("Not yet implemented")

    actual fun setDirection(i: Int, x: Float, y: Float, z: Float) {
    }

    actual fun getDirection(i: Int, out: FloatArray?): FloatArray = TODO("Not yet implemented")

    actual fun setColor(i: Int, linearR: Float, linearG: Float, linearB: Float) {
    }

    actual fun getColor(i: Int, out: FloatArray?): FloatArray = TODO("Not yet implemented")

    actual fun setIntensity(i: Int, intensity: Float) {
    }

    actual fun setIntensityCandela(i: Int, intensity: Float) {
    }

    actual fun setIntensity(i: Int, watts: Float, efficiency: Float) {
    }

    actual fun getIntensity(i: Int): Float = TODO("Not yet implemented")

    actual fun setFalloff(i: Int, falloff: Float) {
    }

    actual fun getFalloff(i: Int): Float = TODO("Not yet implemented")

    actual fun setSpotLightCone(i: Int, inner: Float, outer: Float) {
    }

    actual fun setSunAngularRadius(i: Int, angularRadius: Float) {
    }

    actual fun getSunAngularRadius(i: Int): Float = TODO("Not yet implemented")

    actual fun setSunHaloSize(i: Int, haloSize: Float) {
    }

    actual fun getSunHaloSize(i: Int): Float = TODO("Not yet implemented")

    actual fun setSunHaloFalloff(i: Int, haloFalloff: Float) {
    }

    actual fun getSunHaloFalloff(i: Int): Float = TODO("Not yet implemented")

    actual fun setShadowCaster(i: Int, shadowCaster: Boolean) {
    }

    actual fun isShadowCaster(i: Int): Boolean = TODO("Not yet implemented")

    actual fun getOuterConeAngle(i: Int): Float = TODO("Not yet implemented")

    actual fun getInnerConeAngle(i: Int): Float = TODO("Not yet implemented")

    actual fun getNativeObject(): Long = TODO("Not yet implemented")

    actual class Builder actual constructor(type: Type) {
        actual fun lightChannel(channel: Int, enable: Boolean): Builder = TODO("Not yet implemented")

        actual fun castShadows(enable: Boolean): Builder = TODO("Not yet implemented")

        actual fun shadowOptions(options: ShadowOptions): Builder = TODO("Not yet implemented")

        actual fun castLight(enabled: Boolean): Builder = TODO("Not yet implemented")

        actual fun position(x: Float, y: Float, z: Float): Builder = TODO("Not yet implemented")

        actual fun direction(x: Float, y: Float, z: Float): Builder = TODO("Not yet implemented")

        actual fun color(linearR: Float, linearG: Float, linearB: Float): Builder = TODO("Not yet implemented")

        actual fun intensity(intensity: Float): Builder = TODO("Not yet implemented")

        actual fun intensity(watts: Float, efficiency: Float): Builder = TODO("Not yet implemented")

        actual fun intensityCandela(intensity: Float): Builder = TODO("Not yet implemented")

        actual fun falloff(radius: Float): Builder = TODO("Not yet implemented")

        actual fun spotLightCone(inner: Float, outer: Float): Builder = TODO("Not yet implemented")

        actual fun sunAngularRadius(angularRadius: Float): Builder = TODO("Not yet implemented")

        actual fun sunHaloSize(haloSize: Float): Builder = TODO("Not yet implemented")

        actual fun sunHaloFalloff(haloFalloff: Float): Builder = TODO("Not yet implemented")

        actual fun build(engine: Engine, entity: Int) {
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
            TODO("Not yet implemented")
        }

        actual fun computeLogSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float) {
            TODO("Not yet implemented")
        }

        actual fun computePracticalSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float, lambda: Float) {
            TODO("Not yet implemented")
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
        actual val EFFICIENCY_INCANDESCENT: Float = 0.0220f
        actual val EFFICIENCY_HALOGEN: Float = 0.0707f
        actual val EFFICIENCY_FLUORESCENT: Float = 0.0878f
        actual val EFFICIENCY_LED: Float = 0.1171f
    }
}

