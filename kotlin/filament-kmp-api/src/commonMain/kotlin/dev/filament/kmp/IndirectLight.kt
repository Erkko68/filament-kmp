package dev.filament.kmp

/**
 * IndirectLight is used to simulate environment lighting, a form of global illumination.
 */
expect class IndirectLight {

    class Builder() {
        /**
         * Set the reflections cubemap mipmap chain.
         */
        fun reflections(cubemap: Texture): Builder

        /**
         * Sets the irradiance as Spherical Harmonics.
         */
        fun irradiance(bands: Int, sh: FloatArray): Builder

        /**
         * Sets the irradiance from the radiance expressed as Spherical Harmonics.
         */
        fun radiance(bands: Int, sh: FloatArray): Builder

        /**
         * Sets the irradiance as a cubemap.
         */
        fun irradiance(cubemap: Texture): Builder

        /**
         * Environment intensity (optional).
         */
        fun intensity(envIntensity: Float): Builder

        /**
         * Specifies the rigid-body transformation to apply to the IBL.
         */
        fun rotation(rotation: FloatArray): Builder

        /**
         * Creates the IndirectLight object.
         */
        fun build(engine: Engine): IndirectLight
    }

    /**
     * Sets the environment's intensity.
     */
    fun setIntensity(intensity: Float)

    /**
     * Returns the environment's intensity in lux, or lumen/m^2.
     */
    fun getIntensity(): Float

    /**
     * Sets the rigid-body transformation to apply to the IBL.
     */
    fun setRotation(rotation: FloatArray)

    /**
     * Returns the rigid-body transformation applied to the IBL.
     */
    fun getRotation(rotation: FloatArray?): FloatArray

    /** @deprecated */
    @Deprecated("Use static getDirectionEstimate instead")
    fun getDirectionEstimate(direction: FloatArray?): FloatArray

    /** @deprecated */
    @Deprecated("Use static getColorEstimate instead")
    fun getColorEstimate(colorIntensity: FloatArray?, x: Float, y: Float, z: Float): FloatArray

    fun getReflectionsTexture(): Texture?

    fun getIrradianceTexture(): Texture?

    companion object {
        /**
         * Helper to estimate the direction of the dominant light in the environment.
         */
        fun getDirectionEstimate(sh: FloatArray, direction: FloatArray?): FloatArray

        /**
         * Helper to estimate the color and relative intensity of the environment in a given direction.
         */
        fun getColorEstimate(colorIntensity: FloatArray?, sh: FloatArray, x: Float, y: Float, z: Float): FloatArray
    }
}
