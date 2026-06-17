package io.github.erkko68.filament

/**
 * Skybox renders a background environment cube around the camera.
 *
 * The Skybox is rendered as though the camera is inside an infinitely large cube with
 * a cubemap texture mapped to its exterior. This allows rendering a background environment
 * that follows the camera's orientation.
 *
 * **Usage:**
 * Create a Skybox with the Builder, set it on the Scene with [Scene.setSkybox], and
 * destroy with [Engine.destroy]. The Skybox can render an optional sun disk if a SUN
 * light exists in the scene.
 *
 * **Note:** The Skybox and [IndirectLight] both render backgrounds. The Skybox typically
 * uses lower-resolution cubemaps for visual appearance, while IndirectLight provides the
 * high-quality irradiance for lighting calculations.
 *
 * @see Scene.setSkybox
 * @see IndirectLight
 */
expect class Skybox {
    /**
     * Builder for creating Skybox instances.
     *
     * The Skybox can be configured with a cubemap environment, color, intensity, and
     * priority, along with optional sun rendering.
     */
    class Builder() {
        /**
         * Sets the environment cubemap (skybox content).
         *
         * The cubemap is mapped to the exterior of an infinitely large cube centered on
         * the camera, so the cubemap appears mirrored (following OpenGL conventions).
         * The **cmgen** tool generates reflection maps ideal for use as skyboxes.
         *
         * @param cubemap A cube map Texture
         * @return This Builder, for chaining calls
         *
         * @see Texture
         */
        fun environment(cubemap: Texture): Builder

        /**
         * Enables/disables sun disk rendering.
         *
         * The sun can only be rendered if at least one SUN light exists in the scene.
         * Default: false.
         *
         * @param show true to render the sun disk, false to disable
         * @return This Builder, for chaining calls
         */
        fun showSun(show: Boolean): Builder

        /**
         * Skybox intensity when no [IndirectLight] is set on the Scene.
         *
         * This call is ignored when an IndirectLight is set on the Scene, and the intensity
         * of the IndirectLight is used instead.
         *
         * @param envIntensity Scale factor applied to the skybox texel values such that
         *                     the result is in lux, or lumen/m² (default = 30000)
         * @return This Builder, for chaining calls.
         *
         * @see IndirectLight.Builder.intensity
         */
        fun intensity(envIntensity: Float): Builder

        /**
         * Sets a constant background color.
         *
         * Ignored if an environment cubemap is set. Default: opaque black.
         *
         * @param r Red channel [0, 1]
         * @param g Green channel [0, 1]
         * @param b Blue channel [0, 1]
         * @param a Alpha channel [0, 1]
         * @return This Builder, for chaining calls
         */
        fun color(r: Float, g: Float, b: Float, a: Float): Builder

        /**
         * Set the rendering priority of the Skybox.
         *
         * By default, it is set to the lowest priority (7) such that the Skybox is always rendered
         * after the opaque objects, to reduce overdraw when depth culling is enabled.
         *
         * @param priority Clamped to the range [0..7], defaults to 7; 7 is lowest priority (rendered last).
         * @return This Builder, for chaining calls.
         *
         * @see RenderableManager.Builder.priority
         */
        fun priority(priority: Int): Builder

        /**
         * Creates the Skybox object.
         *
         * @param engine Engine to associate this Skybox with
         * @return The newly created Skybox
         */
        fun build(engine: Engine): Skybox
    }

    /**
     * Dynamically updates the skybox constant color.
     *
     * @param r Red channel [0, 1]
     * @param g Green channel [0, 1]
     * @param b Blue channel [0, 1]
     * @param a Alpha channel [0, 1]
     */
    fun setColor(r: Float, g: Float, b: Float, a: Float)

    /**
     * Gets the skybox intensity.
     * @return Intensity multiplier
     */
    val intensity: Float

    /**
     * Gets the layer mask controlling which layers this skybox affects.
     *
     * The layer mask determines which renderables see this skybox (for scenarios
     * with multiple layers).
     *
     * @return Bitmask of visible layers
     */
    val layerMask: Int

    /**
     * Gets the environment cubemap texture.
     * @return The cubemap Texture, or null if using constant color
     */
    val texture: Texture?

    /**
     * Sets the layer mask controlling which layers see this skybox.
     *
     * @param select Which layers to affect (bitmask)
     * @param value Layer mask to set
     */
    fun setLayerMask(select: Int, value: Int)
}
