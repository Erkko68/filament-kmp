package dev.filament.kmp

/**
 * LightManager allows you to create a light source in the scene, such as a sun or street lights.
 * <p>
 * At least one light must be added to a scene in order to see anything
 * (unless the {@link Material.Shading#UNLIT} is used).
 * </p>
 *
 * <h1><u>Creation and destruction</u></h1>
 * <p>
 * A Light component is created using the {@link LightManager.Builder} and destroyed by calling
 * {@link LightManager#destroy}.
 * </p>
 * <pre>
 *  Engine engine = Engine.create();
 *  int sun = EntityManager.get().create();
 *
 *  LightManager.Builder(Type.SUN)
 *              .castShadows(true)
 *              .build(engine, sun);
 *
 *  engine.getLightManager().destroy(sun);
 * </pre>
 *
 * <h1><u>Light types</u></h1>
 *
 * Lights come in three flavors:
 * <ul>
 * <li>directional lights</li>
 * <li>point lights</li>
 * <li>spot lights</li>
 * </ul>
 *
 * <h1><u>Performance considerations</u></h1>
 * <p>
 * Generally, adding lights to the scene hurts performance, however filament is designed to be
 * able to handle hundreds of lights in a scene under certain conditions.
 * </p>
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
        SPOT,
    }

    /**
     * Control the quality / performance of the shadow map associated to this light
     */
    class ShadowOptions {
        /** Size of the shadow map in texels. Must be a power-of-two and larger or equal to 8. */
        var mapSize: Int

        /**
         * Number of shadow cascades to use for this light. Must be between 1 and 4 (inclusive).
         * A value greater than 1 turns on cascaded shadow mapping (CSM).
         * Only applicable to Type.SUN or Type.DIRECTIONAL lights.
         *
         * <p>
         * When using shadow cascades, {@link ShadowOptions#cascadeSplitPositions} must also be set.
         * </p>
         *
         * @see ShadowOptions#cascadeSplitPositions
         */
        var shadowCascades: Int

        /**
         * The split positions for shadow cascades.
         */
        var cascadeSplitPositions: FloatArray

        /** Constant bias in world units (e.g. meters) by which shadows are moved away from the
         * light. 1mm by default.
         * This is ignored when the View's ShadowType is set to VSM.
         */
        var constantBias: Float

        /** Amount by which the maximum sampling error is scaled. The resulting value is used
         * to move the shadow away from the fragment normal. Should be 1.0.
         * This is ignored when the View's ShadowType is set to VSM.
         */
        var normalBias: Float

        /** Distance from the camera after which shadows are clipped. This is used to clip
         * shadows that are too far and wouldn't contribute to the scene much, improving
         * performance and quality. This value is always positive.
         * Use 0.0f to use the camera far distance.
         * This only affect directional lights.
         */
        var shadowFar: Float

        /** Optimize the quality of shadows from this distance from the camera. Shadows will
         * be rendered in front of this distance, but the quality may not be optimal.
         * This value is always positive. Use 0.0f to use the camera near distance.
         * The default of 1m works well with many scenes. The quality of shadows may drop
         * rapidly when this value decreases.
         */
        var shadowNearHint: Float

        /** Optimize the quality of shadows in front of this distance from the camera. Shadows
         * will be rendered behind this distance, but the quality may not be optimal.
         * This value is always positive. Use std::numerical_limits<float>::infinity() to
         * use the camera far distance.
         */
        var shadowFarHint: Float

        /**
         * Controls whether the shadow map should be optimized for resolution or stability.
         * When set to true, all resolution enhancing features that can affect stability are
         * disabling, resulting in significantly lower resolution shadows, albeit stable ones.
         *
         * Setting this flag to true always disables LiSPSM (see below).
         */
        var stable: Boolean

        /**
         * LiSPSM, or light-space perspective shadow-mapping is a technique allowing to better
         * optimize the use of the shadow-map texture. When enabled the effective resolution of
         * shadows is greatly improved and yields result similar to using cascades without the
         * extra cost. LiSPSM comes with some drawbacks however, in particular it is incompatible
         * with blurring because it effectively affects the blur kernel size.
         *
         * Blurring is only an issue when using ShadowType.VSM with a large blur or with
         * ShadowType.PCSS however.
         *
         * If these blurring artifacts become problematic, this flag can be used to disable LiSPSM.
         */
        var lispsm: Boolean

        /**
         * Whether screen-space contact shadows are used. This applies regardless of whether a
         * Renderable is a shadow caster.
         * Screen-space contact shadows are typically useful in large scenes.
         * (off by default)
         */
        var screenSpaceContactShadows: Boolean

        /**
         * Number of ray-marching steps for screen-space contact shadows (8 by default).
         *<p>
         * <b>CAUTION:</b> this parameter is ignored for all lights except the directional/sun light,
         *                 all other lights use the same value set for the directional/sun light.
         *</p>
         */
        var stepCount: Int

        /**
         * Maximum shadow-occluder distance for screen-space contact shadows (world units).
         * (30 cm by default)
         *<p>
         * <b>CAUTION:</b> this parameter is ignored for all lights except the directional/sun light,
         *                 all other lights use the same value set for the directional/sun light.
         *</p>
         */
        var maxShadowDistance: Float

        /**
         * When elvsm is set to true, "Exponential Layered VSM without Layers" are used.
         */
        var elvsm: Boolean

        /**
         * Blur width for the VSM blur. Zero do disable.
         * The maximum value is 125.
         */
        var blurWidth: Float

        /**
         * Light bulb radius used for soft shadows. Currently this is only used when DPCF is
         * enabled. (2cm by default).
         */
        var shadowBulbRadius: Float

        /**
         * Transforms the shadow direction. Must be a unit quaternion.
         */
        var transform: FloatArray
    }

    object ShadowCascades {
        /**
         * Utility method to compute {@link ShadowOptions#cascadeSplitPositions} according to a
         * uniform split scheme.
         */
        fun computeUniformSplits(splitPositions: FloatArray, cascades: Int)

        /**
         * Utility method to compute {@link ShadowOptions#cascadeSplitPositions} according to a
         * logarithmic split scheme.
         */
        fun computeLogSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float)

        /**
         * Utility method to compute {@link ShadowOptions#cascadeSplitPositions} according to a
         * practical split scheme.
         */
        fun computePracticalSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float, lambda: Float)
    }

    /** Typical efficiency of an incandescent light bulb (2.2%) */
    companion object {
        val EFFICIENCY_INCANDESCENT: Float

        /** Typical efficiency of an halogen light bulb (7.0%) */
        val EFFICIENCY_HALOGEN: Float

        /** Typical efficiency of a fluorescent light bulb (8.7%) */
        val EFFICIENCY_FLUORESCENT: Float

        /** Typical efficiency of a LED light bulb (11.7%) */
        val EFFICIENCY_LED: Float
    }

    /**
     *  Use Builder to construct a Light object instance
     */
    class Builder(type: Type) {
        /**
         * Enables or disables a light channel. Light channel 0 is enabled by default.
         */
        fun lightChannel(channel: Int, enable: Boolean): Builder

        /**
         * Whether this Light casts shadows (disabled by default)
         */
        fun castShadows(enable: Boolean): Builder

        /**
         * Sets the shadow map options for this light.
         */
        fun shadowOptions(options: ShadowOptions): Builder

        /**
         * Whether this light casts light (enabled by default)
         */
        fun castLight(enabled: Boolean): Builder

        /**
         * Sets the initial position of the light in world space.
         */
        fun position(x: Float, y: Float, z: Float): Builder

        /**
         * Sets the initial direction of a light in world space.
         */
        fun direction(x: Float, y: Float, z: Float): Builder

        /**
         * Sets the initial color of a light.
         */
        fun color(linearR: Float, linearG: Float, linearB: Float): Builder

        /**
         * Sets the initial intensity of a light.
         */
        fun intensity(intensity: Float): Builder

        /**
         * Sets the initial intensity of a light in watts.
         */
        fun intensity(watts: Float, efficiency: Float): Builder

        /**
         * Sets the initial intensity of a spot or point light in candela.
         */
        fun intensityCandela(intensity: Float): Builder

        /**
         * Set the falloff distance for point lights and spot lights.
         */
        fun falloff(radius: Float): Builder

        /**
         * Defines a spot light's angular falloff attenuation.
         */
        fun spotLightCone(inner: Float, outer: Float): Builder

        /**
         * Defines the angular radius of the sun, in degrees, between 0.25° and 20.0°
         */
        fun sunAngularRadius(angularRadius: Float): Builder

        /**
         * Defines the halo radius of the sun.
         */
        fun sunHaloSize(haloSize: Float): Builder

        /**
         * Defines the halo falloff of the sun.
         */
        fun sunHaloFalloff(haloFalloff: Float): Builder

        /**
         * Adds the Light component to an entity.
         */
        fun build(engine: Engine, @Entity entity: Int)
    }

    /**
     * Returns the number of components in the LightManager, note that components are not
     * guaranteed to be active. Use the {@link EntityManager#isAlive} before use if needed.
     *
     * @return number of component in the LightManager
     */
    fun getComponentCount(): Int

    /**
     * Returns whether a particular Entity is associated with a component of this LightManager
     * @param entity An Entity.
     * @return true if this Entity has a component associated with this manager.
     */
    fun hasComponent(@Entity entity: Int): Boolean

    /**
     * Gets an Instance representing the Light component associated with the given Entity.
     * @param entity An Entity.
     * @return An Instance object, which represents the Light component associated with the Entity entity.
     *         The instance is 0 if the component doesn't exist.
     * @see #hasComponent
     */
    @EntityInstance
    fun getInstance(@Entity entity: Int): Int

    /**
     * Destroys this component from the given entity
     * @param entity An Entity.
     */
    fun destroy(@Entity entity: Int)

    /**
     * @param i     Instance of the component obtained from getInstance().
     */
    fun getType(@EntityInstance i: Int): Type

    /**
     * Enables or disables a light channel.
     * Light channel 0 is enabled by default.
     *
     * @param i        Instance of the component obtained from getInstance().
     * @param channel  Light channel to set
     * @param enable   true to enable, false to disable
     *
     * @see Builder#lightChannel
     */
    fun setLightChannel(@EntityInstance i: Int, channel: Int, enable: Boolean)

    /**
     * Returns whether a light channel is enabled on a specified renderable.
     * @param i        Instance of the component obtained from getInstance().
     * @param channel  Light channel to query
     * @return         true if the light channel is enabled, false otherwise
     */
    fun getLightChannel(@EntityInstance i: Int, channel: Int): Boolean

    /**
     * Dynamically updates the light's position.
     *
     * @param i        Instance of the component obtained from getInstance().
     * @param x Light's position x coordinate in world space. The default is 0.
     * @param y Light's position y coordinate in world space. The default is 0.
     * @param z Light's position z coordinate in world space. The default is 0.
     */
    fun setPosition(@EntityInstance i: Int, x: Float, y: Float, z: Float)

    /**
     * returns the light's position in world space
     * @param i     Instance of the component obtained from getInstance().
     * @param out   An array of 3 float to receive the result or null.
     * @return      An array of 3 float containing the light's position coordinates.
     */
    fun getPosition(@EntityInstance i: Int, out: FloatArray? = null): FloatArray

    /**
     * Dynamically updates the light's direction
     *
     * @param i Instance of the component obtained from getInstance().
     * @param x light's direction x coordinate (default is 0)
     * @param y light's direction y coordinate (default is -1)
     * @param z light's direction z coordinate (default is 0)
     */
    fun setDirection(@EntityInstance i: Int, x: Float, y: Float, z: Float)

    /**
     * returns the light's direction in world space
     * @param i     Instance of the component obtained from getInstance().
     * @param out   An array of 3 float to receive the result or null.
     * @return      An array of 3 float containing the light's direction.
     */
    fun getDirection(@EntityInstance i: Int, out: FloatArray? = null): FloatArray

    /**
     * Dynamically updates the light's hue as linear sRGB
     */
    fun setColor(@EntityInstance i: Int, linearR: Float, linearG: Float, linearB: Float)

    /**
     * Returns the light color
     */
    fun getColor(@EntityInstance i: Int, out: FloatArray? = null): FloatArray

    /**
     * Dynamically updates the light's intensity. The intensity can be negative.
     */
    fun setIntensity(@EntityInstance i: Int, intensity: Float)

    /**
     * Dynamically updates the light's intensity in candela. The intensity can be negative.
     */
    fun setIntensityCandela(@EntityInstance i: Int, intensity: Float)

    /**
     * Dynamically updates the light's intensity. The intensity can be negative.
     */
    fun setIntensity(@EntityInstance i: Int, watts: Float, efficiency: Float)

    /**
     * returns the light's luminous intensity in <i>lumens</i>.
     */
    fun getIntensity(@EntityInstance i: Int): Float

    /**
     * Set the falloff distance for point lights and spot lights.
     */
    fun setFalloff(@EntityInstance i: Int, falloff: Float)

    /**
     * returns the falloff distance of this light.
     */
    fun getFalloff(@EntityInstance i: Int): Float

    /**
     * Dynamically updates a spot light's cone as angles
     */
    fun setSpotLightCone(@EntityInstance i: Int, inner: Float, outer: Float)

    /**
     * Dynamically updates the angular radius of a Type.SUN light
     */
    fun setSunAngularRadius(@EntityInstance i: Int, angularRadius: Float)

    /**
     * returns the angular radius if the sun in degrees.
     */
    fun getSunAngularRadius(@EntityInstance i: Int): Float

    /**
     * Dynamically updates the halo radius of a Type.SUN light.
     */
    fun setSunHaloSize(@EntityInstance i: Int, haloSize: Float)

    /**
     * returns the halo size of a Type.SUN light as a multiplier of the
     * sun angular radius.
     */
    fun getSunHaloSize(@EntityInstance i: Int): Float

    /**
     * Dynamically updates the halo falloff of a Type.SUN light.
     */
    fun setSunHaloFalloff(@EntityInstance i: Int, haloFalloff: Float)

    /**
     * returns the halo falloff of a Type.SUN light as a dimensionless value.
     */
    fun getSunHaloFalloff(@EntityInstance i: Int): Float

    /**
     * Whether this Light casts shadows (disabled by default)
     */
    fun setShadowCaster(@EntityInstance i: Int, shadowCaster: Boolean)

    /**
     * returns whether this light casts shadows.
     */
    fun isShadowCaster(@EntityInstance i: Int): Boolean

    fun getOuterConeAngle(@EntityInstance i: Int): Float

    fun getInnerConeAngle(@EntityInstance i: Int): Float

    fun getNativeObject(): Long
}

