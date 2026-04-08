package dev.filament.kmp

/**
 * LightManager allows you to create a light source in the scene, such as a sun or street lights.
 */
expect class LightManager {
    enum class Type { SUN, DIRECTIONAL, POINT, FOCUSED_SPOT, SPOT }

    class ShadowOptions() {
        var mapSize: Int
        var shadowCascades: Int
        var cascadeSplitPositions: FloatArray
        var constantBias: Float
        var normalBias: Float
        var shadowFar: Float
        var shadowNearHint: Float
        var shadowFarHint: Float
        var stable: Boolean
        var lispsm: Boolean
        var screenSpaceContactShadows: Boolean
        var stepCount: Int
        var maxShadowDistance: Float
        var elvsm: Boolean
        var blurWidth: Float
        var shadowBulbRadius: Float
        var transform: FloatArray
    }

    object ShadowCascades {
        fun computeUniformSplits(splitPositions: FloatArray, cascades: Int)
        fun computeLogSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float)
        fun computePracticalSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float, lambda: Float)
    }

    class Builder(type: Type) {
        fun lightChannel(channel: Int, enable: Boolean): Builder
        fun castShadows(enable: Boolean): Builder
        fun shadowOptions(options: ShadowOptions): Builder
        fun castLight(enabled: Boolean): Builder
        fun position(x: Float, y: Float, z: Float): Builder
        fun direction(x: Float, y: Float, z: Float): Builder
        fun color(linearR: Float, linearG: Float, linearB: Float): Builder
        fun intensity(intensity: Float): Builder
        fun intensity(watts: Float, efficiency: Float): Builder
        fun intensityCandela(intensity: Float): Builder
        fun falloff(radius: Float): Builder
        fun spotLightCone(inner: Float, outer: Float): Builder
        fun sunAngularRadius(angularRadius: Float): Builder
        fun sunHaloSize(haloSize: Float): Builder
        fun sunHaloFalloff(haloFalloff: Float): Builder
        fun build(engine: Engine, entity: Int)
    }

    fun getComponentCount(): Int
    fun hasComponent(entity: Int): Boolean
    fun getInstance(entity: Int): Int
    fun destroy(entity: Int)

    fun getType(instance: Int): Type
    fun setDirection(instance: Int, x: Float, y: Float, z: Float)
    fun getDirection(instance: Int, out: FloatArray): FloatArray
    fun setPosition(instance: Int, x: Float, y: Float, z: Float)
    fun getPosition(instance: Int, out: FloatArray): FloatArray
    fun setColor(instance: Int, r: Float, g: Float, b: Float)
    fun getColor(instance: Int, out: FloatArray): FloatArray
    fun setIntensity(instance: Int, intensity: Float)
    fun setIntensity(instance: Int, watts: Float, efficiency: Float)
    fun setIntensityCandela(instance: Int, intensity: Float)
    fun getIntensity(instance: Int): Float
    fun setFalloff(instance: Int, radius: Float)
    fun getFalloff(instance: Int): Float
    fun setSpotLightCone(instance: Int, inner: Float, outer: Float)
    fun getInnerConeAngle(instance: Int): Float
    fun getOuterConeAngle(instance: Int): Float
    fun setSunAngularRadius(instance: Int, angularRadius: Float)
    fun getSunAngularRadius(instance: Int): Float
    fun setSunHaloSize(instance: Int, haloSize: Float)
    fun getSunHaloSize(instance: Int): Float
    fun setSunHaloFalloff(instance: Int, haloFalloff: Float)
    fun getSunHaloFalloff(instance: Int): Float
    fun setShadowCaster(instance: Int, shadowCaster: Boolean)
    fun isShadowCaster(instance: Int): Boolean
    fun setLightChannel(instance: Int, channel: Int, enable: Boolean)
    fun getLightChannel(instance: Int, channel: Int): Boolean
}
