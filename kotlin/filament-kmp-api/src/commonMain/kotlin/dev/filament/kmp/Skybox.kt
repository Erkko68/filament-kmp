package dev.filament.kmp

/**
 * Skybox
 * <p>When added to a {@link Scene}, the <code>Skybox</code> fills all untouched pixels.</p>
 *
 * <h1>Creation and destruction</h1>
 *
 * A <code>Skybox</code> object is created using the {@link Skybox.Builder} and destroyed by calling
 * {@link Engine#destroySkybox}.<br>
 * <pre>
 *  Engine engine = Engine.create();
 *
 *  Scene scene = engine.createScene();
 *
 *  Skybox skybox = new Skybox.Builder()
 *              .environment(cubemap)
 *              .build(engine);
 *
 *  scene.setSkybox(skybox);
 * </pre>
 *
 * Currently only {@link Texture} based sky boxes are supported.
 *
 * @see Scene
 * @see IndirectLight
 */
expect class Skybox {
    /**
     * Use <code>Builder</code> to construct a <code>Skybox</code> object instance.
     */
    class Builder {
        /**
         * Set the environment map (i.e. the skybox content).
         *
         * <p>The <code>Skybox</code> is rendered as though it were an infinitely large cube with the
         * camera inside it. This means that the cubemap which is mapped onto the cube's exterior
         * will appear mirrored. This follows the OpenGL conventions.</p>
         *
         * <p>The <code>cmgen</code> tool generates reflection maps by default which are therefore
         * ideal to use as skyboxes.</p>
         *
         * @param cubemap A cubemap {@link Texture}
         *
         * @return This Builder, for chaining calls.
         *
         * @see Texture
         */
        fun environment(cubemap: Texture): Builder

        /**
         * Indicates whether the sun should be rendered.
         * The sun can only be
         * rendered if there is at least one light of type {@link LightManager.Type#SUN} in
         * the {@link Scene}. The default value is <code>false</code>.
         *
         * @param show <code>true</code> if the sun should be rendered, <code>false</code> otherwise
         *
         * @return This Builder, for chaining calls.
         */
        fun showSun(show: Boolean): Builder

        /**
         * Sets the <code>Skybox</code> intensity when no {@link IndirectLight} is set on the
         * {@link Scene}.
         *
         * <p>This call is ignored when an {@link IndirectLight} is set on the {@link Scene}, and
         * the intensity of the {@link IndirectLight} is used instead.</p>
         *
         * @param envIntensity  Scale factor applied to the skybox texel values such that
         *                      the result is in <i>lux</i>, or <i>lumen/m^2</i> (default = 30000)
         *
         * @return This Builder, for chaining calls.
         *
         * @see IndirectLight.Builder#intensity
         */
        fun intensity(envIntensity: Float): Builder

        /**
         * Sets the <code>Skybox</code> to a constant color. Default is opaque black.
         *
         * Ignored if an environment is set.
         *
         * @return This Builder, for chaining calls.
         */
        fun color(r: Float, g: Float, b: Float, a: Float): Builder

        /**
         * Sets the <code>Skybox</code> to a constant color. Default is opaque black.
         *
         * Ignored if an environment is set.
         *
         * @param color an array of 4 floats
         * @return This Builder, for chaining calls.
         */
        fun color(color: FloatArray): Builder

        /**
         * Set the rendering priority of the Skybox.
         * By default, it is set to the lowest
         * priority (7) such that the Skybox is always rendered after the opaque objects,
         * to reduce overdraw when depth culling is enabled.
         *
         * @param priority clamped to the range [0..7], defaults to 4; 7 is lowest priority
         *                 (rendered last).
         *
         * @return Builder reference for chaining calls.
         *
         * @see RenderableManager.Builder#priority
         */
        fun priority(priority: Int): Builder

        /**
         * Creates a <code>Skybox</code> object.
         *
         * @param engine the {@link Engine} to associate this <code>Skybox</code> with.
         *
         * @return A newly created <code>Skybox</code>object
         *
         * @exception IllegalStateException can be thrown if the  <code>Skybox</code> couldn't be created
         */
        fun build(engine: Engine): Skybox
    }

    /**
     * Mutates the <code>Skybox</code>'s constant color.
     *
     * Ignored if an environment is set.
     */
    fun setColor(r: Float, g: Float, b: Float, a: Float)

    /**
     * Mutates the <code>Skybox</code>'s constant color.
     * Ignored if an environment is set.
     *
     * @param color an array of 4 floats
     */
    fun setColor(color: FloatArray)

    /**
     * Sets bits in a visibility mask. By default, this is <code>0x1</code>.
     * <p>This provides a simple mechanism for hiding or showing this <code>Skybox</code> in a
     * {@link Scene}.</p>
     *
     * <p>For example, to set bit 1 and reset bits 0 and 2 while leaving all other bits unaffected,
     * call: <code>setLayerMask(7, 2)</code>.</p>
     *
     * @param select the set of bits to affect
     * @param values the replacement values for the affected bits
     *
     * @see View#setVisibleLayers
     */
    fun setLayerMask(select: Int, values: Int)

    /**
     * @return the visibility mask bits
     */
    fun getLayerMask(): Int

    /**
     * Returns the <code>Skybox</code>'s intensity in <i>lux</i>, or <i>lumen/m^2</i>.
     */
    fun getIntensity(): Float

    /**
     * @return the associated texture, or null if it does not exist
     */
    fun getTexture(): Texture?

    fun getNativeObject(): Long

    internal fun invalidate()
}

