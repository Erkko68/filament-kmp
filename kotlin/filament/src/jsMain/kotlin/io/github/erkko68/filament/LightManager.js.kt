package io.github.erkko68.filament

actual class LightManager {
    actual fun getComponentCount(): Int {
        TODO("Not yet implemented")
    }

    actual fun hasComponent(entity: Int): Boolean {
        TODO("Not yet implemented")
    }

    actual fun getInstance(entity: Int): Int {
        TODO("Not yet implemented")
    }

    actual fun destroy(entity: Int) {
    }

    actual fun getType(instance: Int): Type {
        TODO("Not yet implemented")
    }

    actual fun setDirection(instance: Int, x: Float, y: Float, z: Float) {
    }

    actual fun getDirection(instance: Int, out: FloatArray): FloatArray {
        TODO("Not yet implemented")
    }

    actual fun setPosition(instance: Int, x: Float, y: Float, z: Float) {
    }

    actual fun getPosition(instance: Int, out: FloatArray): FloatArray {
        TODO("Not yet implemented")
    }

    actual fun setColor(instance: Int, r: Float, g: Float, b: Float) {
    }

    actual fun getColor(instance: Int, out: FloatArray): FloatArray {
        TODO("Not yet implemented")
    }

    actual fun setIntensity(instance: Int, intensity: Float) {
    }

    actual fun setIntensity(instance: Int, watts: Float, efficiency: Float) {
    }

    actual fun setIntensityCandela(instance: Int, intensity: Float) {
    }

    actual fun getIntensity(instance: Int): Float {
        TODO("Not yet implemented")
    }

    actual fun setFalloff(instance: Int, radius: Float) {
    }

    actual fun getFalloff(instance: Int): Float {
        TODO("Not yet implemented")
    }

    actual fun setSpotLightCone(instance: Int, inner: Float, outer: Float) {
    }

    actual fun getInnerConeAngle(instance: Int): Float {
        TODO("Not yet implemented")
    }

    actual fun getOuterConeAngle(instance: Int): Float {
        TODO("Not yet implemented")
    }

    actual fun setSunAngularRadius(instance: Int, angularRadius: Float) {
    }

    actual fun getSunAngularRadius(instance: Int): Float {
        TODO("Not yet implemented")
    }

    actual fun setSunHaloSize(instance: Int, haloSize: Float) {
    }

    actual fun getSunHaloSize(instance: Int): Float {
        TODO("Not yet implemented")
    }

    actual fun setSunHaloFalloff(instance: Int, haloFalloff: Float) {
    }

    actual fun getSunHaloFalloff(instance: Int): Float {
        TODO("Not yet implemented")
    }

    actual fun setShadowCaster(instance: Int, shadowCaster: Boolean) {
    }

    actual fun isShadowCaster(instance: Int): Boolean {
        TODO("Not yet implemented")
    }

    actual fun setLightChannel(instance: Int, channel: Int, enable: Boolean) {
    }

    actual fun getLightChannel(instance: Int, channel: Int): Boolean {
        TODO("Not yet implemented")
    }

    actual enum class Type { SUN, DIRECTIONAL, POINT, FOCUSED_SPOT, SPOT }
    actual class ShadowOptions {
        actual var mapSize: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var shadowCascades: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var cascadeSplitPositions: FloatArray
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var constantBias: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var normalBias: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var shadowFar: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var shadowNearHint: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var shadowFarHint: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var stable: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var lispsm: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var screenSpaceContactShadows: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var stepCount: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var maxShadowDistance: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var elvsm: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var blurWidth: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var shadowBulbRadius: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var transform: FloatArray
            get() = TODO("Not yet implemented")
            set(value) {}
    }

    actual object ShadowCascades {
        actual fun computeUniformSplits(splitPositions: FloatArray, cascades: Int) {
        }

        actual fun computeLogSplits(
            splitPositions: FloatArray,
            cascades: Int,
            near: Float,
            far: Float
        ) {
        }

        actual fun computePracticalSplits(
            splitPositions: FloatArray,
            cascades: Int,
            near: Float,
            far: Float,
            lambda: Float
        ) {
        }
    }

    actual class Builder actual constructor(type: Type) {
        actual fun lightChannel(
            channel: Int,
            enable: Boolean
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun castShadows(enable: Boolean): Builder {
            TODO("Not yet implemented")
        }

        actual fun shadowOptions(options: ShadowOptions): Builder {
            TODO("Not yet implemented")
        }

        actual fun castLight(enabled: Boolean): Builder {
            TODO("Not yet implemented")
        }

        actual fun position(
            x: Float,
            y: Float,
            z: Float
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun direction(
            x: Float,
            y: Float,
            z: Float
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun color(
            linearR: Float,
            linearG: Float,
            linearB: Float
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun intensity(intensity: Float): Builder {
            TODO("Not yet implemented")
        }

        actual fun intensity(
            watts: Float,
            efficiency: Float
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun intensityCandela(intensity: Float): Builder {
            TODO("Not yet implemented")
        }

        actual fun falloff(radius: Float): Builder {
            TODO("Not yet implemented")
        }

        actual fun spotLightCone(
            inner: Float,
            outer: Float
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun sunAngularRadius(angularRadius: Float): Builder {
            TODO("Not yet implemented")
        }

        actual fun sunHaloSize(haloSize: Float): Builder {
            TODO("Not yet implemented")
        }

        actual fun sunHaloFalloff(haloFalloff: Float): Builder {
            TODO("Not yet implemented")
        }

        actual fun build(engine: Engine, entity: Int) {
        }
    }
}