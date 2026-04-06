package dev.filament.kmp

/**
 * Skybox
 * <p>When added to a [Scene], the Skybox fills all untouched pixels.</p>
 */
expect class Skybox {

    class Builder() {
        /**
         * Set the environment map (i.e. the skybox content).
         */
        fun environment(cubemap: Texture): Builder

        /**
         * Indicates whether the sun should be rendered.
         */
        fun showSun(show: Boolean): Builder

        /**
         * Sets the Skybox intensity when no IndirectLight is set on the Scene.
         */
        fun intensity(envIntensity: Float): Builder

        /**
         * Sets the Skybox to a constant color. Default is opaque black.
         */
        fun color(r: Float, g: Float, b: Float, a: Float): Builder

        /**
         * Sets the Skybox to a constant color. Default is opaque black.
         */
        fun color(color: FloatArray): Builder

        /**
         * Set the rendering priority of the Skybox.
         */
        fun priority(priority: Int): Builder

        /**
         * Creates a Skybox object
         */
        fun build(engine: Engine): Skybox
    }

    /**
     * Mutates the Skybox's constant color.
     */
    fun setColor(r: Float, g: Float, b: Float, a: Float)

    /**
     * Mutates the Skybox's constant color.
     */
    fun setColor(color: FloatArray)

    /**
     * Sets bits in a visibility mask. By default, this is 0x1.
     */
    fun setLayerMask(select: Int, values: Int)

    /**
     * @return the visibility mask bits
     */
    fun getLayerMask(): Int

    /**
     * Returns the Skybox's intensity in lux, or lumen/m^2.
     */
    fun getIntensity(): Float

    /**
     * @return the associated texture, or null if it does not exist
     */
    fun getTexture(): Texture?
}
