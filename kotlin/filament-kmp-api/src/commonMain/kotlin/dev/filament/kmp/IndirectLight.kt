package dev.filament.kmp

expect class IndirectLight {
    class Builder() {
        fun reflections(cubemap: Texture): Builder
        fun irradiance(bands: Int, sh: FloatArray): Builder
        fun irradiance(cubemap: Texture): Builder
        fun intensity(envIntensity: Float): Builder
        fun rotation(rotation: FloatArray): Builder
        fun build(engine: Engine): IndirectLight
    }

    fun setIntensity(intensity: Float)
    fun getIntensity(): Float
    fun setRotation(rotation: FloatArray)
    fun getRotation(outRotation: FloatArray? = null): FloatArray
}
