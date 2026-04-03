package dev.filament.kmp

actual class IndirectLight {
    actual fun setIntensity(intensity: Float) {
    }

    actual fun getIntensity(): Float = TODO("Not yet implemented")

    actual fun setRotation(rotation: FloatArray) {
    }

    actual fun getRotation(rotation: FloatArray?): FloatArray = TODO("Not yet implemented")

    @Deprecated("Use IndirectLight.getDirectionEstimate(sh, direction)")
    actual fun getDirectionEstimate(direction: FloatArray?): FloatArray = TODO("Not yet implemented")

    @Deprecated("Use IndirectLight.getColorEstimate(colorIntensity, sh, x, y, z)")
    actual fun getColorEstimate(colorIntensity: FloatArray?, x: Float, y: Float, z: Float): FloatArray = TODO("Not yet implemented")

    actual fun getReflectionsTexture(): Texture? = TODO("Not yet implemented")

    actual fun getIrradianceTexture(): Texture? = TODO("Not yet implemented")

    actual fun getNativeObject(): Long = TODO("Not yet implemented")

    actual internal fun invalidate() {
    }

    actual class Builder {
        actual fun reflections(cubemap: Texture): Builder = TODO("Not yet implemented")

        actual fun irradiance(bands: Int, sh: FloatArray): Builder = TODO("Not yet implemented")

        actual fun radiance(bands: Int, sh: FloatArray): Builder = TODO("Not yet implemented")

        actual fun irradiance(cubemap: Texture): Builder = TODO("Not yet implemented")

        actual fun intensity(envIntensity: Float): Builder = TODO("Not yet implemented")

        actual fun rotation(rotation: FloatArray): Builder = TODO("Not yet implemented")

        actual fun build(engine: Engine): IndirectLight = TODO("Not yet implemented")
    }

    actual companion object {
        actual fun getDirectionEstimate(sh: FloatArray, direction: FloatArray?): FloatArray = TODO("Not yet implemented")

        actual fun getColorEstimate(colorIntensity: FloatArray?, sh: FloatArray, x: Float, y: Float, z: Float): FloatArray = TODO("Not yet implemented")
    }
}

