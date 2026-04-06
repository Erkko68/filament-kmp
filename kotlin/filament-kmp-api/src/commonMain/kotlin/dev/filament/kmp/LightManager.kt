package dev.filament.kmp

/**
 * LightManager allows you to create a light source in the scene, such as a sun or street lights.
 * <p>
 * At least one light must be added to a scene in order to see anything
 * (unless the [Material.Shading.UNLIT] is used).
 * </p>
 *
 * <h1>Creation and destruction</h1>
 * <p>
 * A Light component is created using the [LightManager.Builder] and destroyed by calling
 * [LightManager.destroy].
 * </p>
 * <pre>
 *  val engine = Engine.create()
 *  val sun = EntityManager.get().create()
 *
 *  LightManager.Builder(Type.SUN)
 *              .castShadows(true)
 *              .build(engine, sun)
 *
 *  engine.getLightManager().destroy(sun)
 * </pre>
 */
expect class LightManager {

    /**
     * Denotes the type of the light being created.
     */
    enum class Type {
        /** Directional light that also draws a sun's disk in the sky. */
        SUN,
        /** Directional light, emits light in a given direction. */
        DIRECTIONAL,
        /** Point light, emits light from a position, in all directions. */
        POINT,
        /** Physically correct spot light. */
        FOCUSED_SPOT,
        /** Spot light with coupling of outer cone and illumination disabled. */
        SPOT
    }

    /**
     * Control the quality / performance of the shadow map associated to this light
     */
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
        var polygonOffsetConstant: Float
        var polygonOffsetSlope: Float
        var screenSpaceContactShadows: Boolean
        var stepCount: Int
        var maxShadowDistance: Float
        var elvsm: Boolean
        var blurWidth: Float
        var shadowBulbRadius: Float
        var transform: FloatArray
    }

    /**
     * Utilities for shadow cascades
     */
    class ShadowCascades {
        companion object {
            fun computeUniformSplits(splitPositions: FloatArray, cascades: Int)
            fun computeLogSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float)
            fun computePracticalSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float, lambda: Float)
        }
    }

    /**
     * Use Builder to construct a Light object instance
     */
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

    companion object {
        const val EFFICIENCY_INCANDESCENT: Float
        const val EFFICIENCY_HALOGEN: Float
        const val EFFICIENCY_FLUORESCENT: Float
        const val EFFICIENCY_LED: Float
    }

    fun getComponentCount(): Int
    fun hasComponent(entity: Int): Boolean
    fun getInstance(entity: Int): Int
    fun destroy(entity: Int)

    fun getType(instance: Int): Type
    fun setLightChannel(instance: Int, channel: Int, enable: Boolean)
    fun getLightChannel(instance: Int, channel: Int): Boolean
    fun setPosition(instance: Int, x: Float, y: Float, z: Float)
    fun getPosition(instance: Int, out: FloatArray?): FloatArray
    fun setDirection(instance: Int, x: Float, y: Float, z: Float)
    fun getDirection(instance: Int, out: FloatArray?): FloatArray
    fun setColor(instance: Int, linearR: Float, linearG: Float, linearB: Float)
    fun getColor(instance: Int, out: FloatArray?): FloatArray
    fun setIntensity(instance: Int, intensity: Float)
    fun setIntensityCandela(instance: Int, intensity: Float)
    fun setIntensity(instance: Int, watts: Float, efficiency: Float)
    fun getIntensity(instance: Int): Float
    fun setFalloff(instance: Int, falloff: Float)
    fun getFalloff(instance: Int): Float
    fun setSpotLightCone(instance: Int, inner: Float, outer: Float)
    fun setSunAngularRadius(instance: Int, angularRadius: Float)
    fun getSunAngularRadius(instance: Int): Float
    fun setSunHaloSize(instance: Int, haloSize: Float)
    fun getSunHaloSize(instance: Int): Float
    fun setSunHaloFalloff(instance: Int, haloFalloff: Float)
    fun getSunHaloFalloff(instance: Int): Float
    fun setShadowCaster(instance: Int, shadowCaster: Boolean)
    fun isShadowCaster(instance: Int): Boolean
    fun getOuterConeAngle(instance: Int): Float
    fun getInnerConeAngle(instance: Int): Float
}
