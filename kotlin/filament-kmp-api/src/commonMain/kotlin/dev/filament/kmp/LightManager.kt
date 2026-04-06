package dev.filament.kmp

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

    class Builder(type: Type) {
        fun castShadows(enable: Boolean): Builder
        fun shadowOptions(options: ShadowOptions): Builder
        fun castLight(enable: Boolean): Builder
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
        fun lightChannel(channel: Int, enable: Boolean): Builder
        fun build(engine: Engine, entity: Entity)
    }

    fun hasComponent(entity: Entity): Boolean
    fun getInstance(entity: Entity): EntityInstance
    fun destroy(entity: Entity)
    
    fun setPosition(instance: EntityInstance, x: Float, y: Float, z: Float)
    fun getPosition(instance: EntityInstance, outPosition: FloatArray?): FloatArray
    fun setDirection(instance: EntityInstance, x: Float, y: Float, z: Float)
    fun getDirection(instance: EntityInstance, outDirection: FloatArray?): FloatArray
    fun setColor(instance: EntityInstance, r: Float, g: Float, b: Float)
    fun getColor(instance: EntityInstance, outColor: FloatArray?): FloatArray
    
    fun setIntensity(instance: EntityInstance, intensity: Float)
    fun setIntensity(instance: EntityInstance, watts: Float, efficiency: Float)
    fun setIntensityCandela(instance: EntityInstance, intensity: Float)
    fun getIntensity(instance: EntityInstance): Float
    
    fun setFalloff(instance: EntityInstance, radius: Float)
    fun getFalloff(instance: EntityInstance): Float
    
    fun setSpotLightCone(instance: EntityInstance, inner: Float, outer: Float)
    fun getSpotLightInnerCone(instance: EntityInstance): Float
    fun getSpotLightOuterCone(instance: EntityInstance): Float
    
    fun setSunAngularRadius(instance: EntityInstance, angularRadius: Float)
    fun getSunAngularRadius(instance: EntityInstance): Float
    fun setSunHaloSize(instance: EntityInstance, haloSize: Float)
    fun getSunHaloSize(instance: EntityInstance): Float
    fun setSunHaloFalloff(instance: EntityInstance, haloFalloff: Float)
    fun getSunHaloFalloff(instance: EntityInstance): Float
    
    fun setShadowCaster(instance: EntityInstance, shadowCaster: Boolean)
    fun isShadowCaster(instance: EntityInstance): Boolean
    
    fun setLightChannel(instance: EntityInstance, channel: Int, enable: Boolean)
    fun getLightChannel(instance: EntityInstance, channel: Int): Boolean
}
