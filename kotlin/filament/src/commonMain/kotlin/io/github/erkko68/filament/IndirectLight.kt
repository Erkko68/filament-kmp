package io.github.erkko68.filament

expect class IndirectLight {
    class Builder() {
        fun reflections(cubemap: Texture): Builder
        fun irradiance(bands: Int, sh: FloatArray): Builder
        fun radiance(bands: Int, sh: FloatArray): Builder
        fun irradiance(cubemap: Texture): Builder
        fun intensity(envIntensity: Float): Builder
        fun rotation(rotation: FloatArray): Builder
        fun build(engine: Engine): IndirectLight
    }

    var intensity: Float
    var rotation: FloatArray

    val reflectionsTexture: Texture?
    val irradianceTexture: Texture?

    companion object {
        fun getDirectionEstimate(sh: FloatArray, out: FloatArray? = null): FloatArray
        fun getColorEstimate(sh: FloatArray, x: Double, y: Double, z: Double, out: FloatArray? = null): FloatArray
    }
}
