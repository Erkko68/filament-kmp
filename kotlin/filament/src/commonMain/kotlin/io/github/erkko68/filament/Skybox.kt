package io.github.erkko68.filament

expect class Skybox {
    class Builder() {
        fun environment(cubemap: Texture): Builder
        fun showSun(show: Boolean): Builder
        fun intensity(envIntensity: Float): Builder
        fun color(r: Float, g: Float, b: Float, a: Float): Builder
        fun build(engine: Engine): Skybox
    }

    fun setColor(r: Float, g: Float, b: Float, a: Float)
    fun getIntensity(): Float
}
