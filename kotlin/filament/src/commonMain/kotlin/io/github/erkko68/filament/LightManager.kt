package io.github.erkko68.filament

import io.github.erkko68.filament.interop.*

/**
 * LightManager allows you to create light sources in the scene.
 *
 * At least one light must be added to a scene to see anything (unless using [Material.Shading.UNLIT]).
 *
 * **Light types** (directional, point, spot):
 * - Directional lights: parallel rays from infinitely far (e.g., sun); cast shadows
 * - Point lights: emit from a position in all directions; intensity diminishes with inverse square law
 * - Spot lights: emit from a position within a cone; two cone angles control falloff
 *
 * **Performance tips:**
 * - Prefer spot lights over point lights; use smallest outer cone angle possible
 * - Use smallest falloff distance to minimize sphere-of-influence overlap
 * - Non-overlapping lights have negligible overhead (hundreds are fine)
 *
 * **Shadow support:**
 * - Only directional and spot lights can cast shadows
 * - Configure shadows via [Builder.shadowOptions] and [ShadowOptions]
 */
class LightManager @InternalFilamentApi constructor(internal val nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * Light type determines behavior and allowed parameters.
     * - SUN: Directional light with sun disk in sky (illumination + halo)
     * - DIRECTIONAL: Plain directional light (illumination only)
     * - POINT: Omnidirectional light from a position
     * - FOCUSED_SPOT: Physically correct spot light (outer cone angle affects illumination)
     * - SPOT: Spot light with outer cone and illumination decoupled (easier to tweak)
     */
    enum class Type { SUN, DIRECTIONAL, POINT, FOCUSED_SPOT, SPOT }

    /**
     * Shadow map quality and performance parameters.
     * Controls resolution, stability, bias, cascading, and filtering techniques.
     */
    class ShadowOptions() {
        /**
         * Size of the shadow map in texels. Must be a power of two and at least 8.
         * Default: 1024. Larger values improve shadow quality but hurt performance.
         */
        var mapSize: Int = 1024

        /**
         * Number of shadow cascades (1-4). Values > 1 enable Cascaded Shadow Mapping (CSM).
         * Only applicable to directional lights.
         * When using cascades, [cascadeSplitPositions] must also be set.
         * Default: 1.
         */
        var shadowCascades: Int = 1

        /**
         * Split positions for shadow cascades along the camera's Z axis.
         * Camera near plane = 0.0f, far plane = 1.0f.
         * For N cascades, store N-1 split positions (e.g., for 4 cascades: [0.25f, 0.50f, 0.75f]).
         * Use [ShadowCascades] helper methods to compute.
         * Default: [0.125f, 0.25f, 0.50f].
         */
        var cascadeSplitPositions: FloatArray = floatArrayOf(0.125f, 0.25f, 0.50f)

        /**
         * Constant bias in world units (e.g., meters) to move shadows away from the light.
         * Ignored for VSM shadow type. Default: 0.001 (1mm).
         */
        var constantBias: Float = 0.001f

        /**
         * Scale for maximum sampling error to move shadows away from fragment normals.
         * Should typically be 1.0. Ignored for VSM shadow type. Default: 1.0.
         */
        var normalBias: Float = 1.0f

        /**
         * Distance from camera after which shadows are clipped (directional lights only).
         * Use 0.0 to use the camera's far distance. Improves quality and performance by
         * discarding distant shadows that don't contribute. Default: 0.0.
         */
        var shadowFar: Float = 0.0f

        /**
         * Optimize shadow quality from this distance onward. Shadows render in front of this
         * distance but may have degraded quality. Use 0.0 for camera near distance.
         * Default: 1.0 (1 meter). Quality may degrade when this is reduced significantly.
         */
        var shadowNearHint: Float = 1.0f

        /**
         * Optimize shadow quality until this distance. Shadows render behind this distance but
         * may have degraded quality. Use Float.POSITIVE_INFINITY for camera far distance.
         * Default: 100.0.
         */
        var shadowFarHint: Float = 100.0f

        /**
         * When true, prioritizes stability over resolution by disabling resolution-enhancing
         * features. Disables LiSPSM. Produces lower-resolution but stable shadows.
         * Default: false.
         */
        var stable: Boolean = false

        /**
         * Light-space perspective shadow mapping (LiSPSM) improves effective shadow resolution
         * without cascades. Incompatible with large blur widths. Disable if blurring artifacts
         * become problematic (only relevant for VSM with blur or PCSS shadow types).
         * Automatically disabled if [stable] is true. Default: true.
         */
        var lispsm: Boolean = false  // match Android binding + cleaner PCSS (Filament C++ defaults true)

        /**
         * Enable screen-space contact shadows (SSCS) for more detailed shadow transitions.
         * Useful in large scenes; adds overhead. Applies regardless of shadow-caster status.
         * Default: false.
         */
        var screenSpaceContactShadows: Boolean = false

        /**
         * Number of ray-marching steps for SSCS (ignored for non-directional lights).
         * Default: 8.
         */
        var stepCount: Int = 8

        /**
         * Maximum shadow-occluder distance for SSCS in world units.
         * (ignored for non-directional lights). Default: 0.3 (30 cm).
         */
        var maxShadowDistance: Float = 0.3f

        /**
         * For VSM shadow type: use Exponential Layered VSM (ELVSM) for improved light leak
         * reduction. Doubles shadow map memory. Mostly useful with large blur widths.
         * Default: false.
         */
        var elvsm: Boolean = false

        /**
         * For VSM shadow type: blur width (0 to disable). Max: 125.
         * Default: 0.0 (disabled).
         */
        var blurWidth: Float = 0.0f

        /**
         * Light bulb radius for soft shadow effects (DPCF/PCSS only).
         * For spot and point lights, this is the radius of the light bulb in meters.
         * For directional lights, this is tan(angularRadius).
         * Default: -1.0 (automatic calculation based on light type).
         */
        var shadowBulbRadius: Float = -1.0f

        /**
         * Transform the shadow direction via a unit quaternion (artistic use only).
         * Ignored for non-directional lights. Default: identity.
         */
        // Identity quaternion (x,y,z,w): a zero transform collapses the directional shadow frustum.
        var transform: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)

        /**
         * Constant bias in depth-resolution units by which shadows are moved away from
         * the light. The default value of 0.5 is used to round depth values up.
         * Generally this value shouldn't be changed or at least be small and positive.
         * Ignored when the View's ShadowType is set to VSM.
         * Default: 0.5.
         */
        var polygonOffsetConstant: Float = 0.5f

        /**
         * Bias based on the change in depth in depth-resolution units by which shadows
         * are moved away from the light. The default value of 2.0 works well with
         * SHADOW_SAMPLING_PCF_LOW. Generally this value is between 0.5 and the size in
         * texel of the PCF filter. Setting this value correctly is essential for LiSPSM
         * shadow maps. Ignored when the View's ShadowType is set to VSM.
         * Default: 2.0.
         */
        var polygonOffsetSlope: Float = 2.0f

        /**
         * Per-light scale on the final PCSS penumbra size, modulated with the View-wide
         * [View.SoftShadowOptions.penumbraScale] (global * local). Default: 1.0.
         */
        var penumbraScale: Float = 1.0f

        /**
         * Per-light scale on the PCSS geometric ratio — how fast the shadow softens with
         * blocker distance — modulated with [View.SoftShadowOptions.penumbraRatioScale].
         * Default: 1.0.
         */
        var penumbraRatioScale: Float = 1.0f

        /**
         * Per-light cap on the PCSS penumbra ratio, applied as a smooth asymptotic squash.
         * 0 defers to the View-wide [View.SoftShadowOptions.maxPenumbraRatio]. Default: 0.0.
         */
        var maxPenumbraRatio: Float = 0.0f

        /**
         * Per-light limit, in world units, on the PCSS blocker-search footprint. 0 defers to the
         * View-wide [View.SoftShadowOptions.maxSearchRadius]. Default: 0.0.
         */
        var maxSearchRadius: Float = 0.0f
    }

    /**
     * Utility methods for computing cascaded shadow map split positions.
     * Use these to populate [ShadowOptions.cascadeSplitPositions].
     */
    object ShadowCascades {
        /**
         * Compute uniform split positions (equal distance along camera Z axis).
         * Simple but may waste resolution in areas where it's less needed.
         * @param splitPositions Array of size (cascades - 1) to receive split positions
         * @param cascades Number of cascades (1-4)
         */
        fun computeUniformSplits(splitPositions: FloatArray, cascades: Int) {
            splitPositions.usePinned { pinned ->
                FilaLightManager_computeUniformSplits(pinned, cascades)
            }
        }
        /**
         * Compute logarithmic split positions (more resolution near camera).
         * Better distribution of resolution than uniform for typical scenes.
         * @param splitPositions Array of size (cascades - 1) to receive split positions
         * @param cascades Number of cascades (1-4)
         * @param near Camera near plane distance
         * @param far Camera far plane distance
         */
        fun computeLogSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float) {
            splitPositions.usePinned { pinned ->
                FilaLightManager_computeLogSplits(pinned, cascades, near, far)
            }
        }
        /**
         * Compute practical split positions (interpolates between log and uniform schemes).
         * Provides fine-grained control over resolution distribution.
         * See: Zhang et al 2006, "Parallel-split shadow maps for large-scale virtual environments"
         * @param splitPositions Array of size (cascades - 1) to receive split positions
         * @param cascades Number of cascades (1-4)
         * @param near Camera near plane distance
         * @param far Camera far plane distance
         * @param lambda Interpolation factor [0, 1]: 0 = logarithmic, 1 = uniform. Start with 0.5
         */
        fun computePracticalSplits(splitPositions: FloatArray, cascades: Int, near: Float, far: Float, lambda: Float) {
            splitPositions.usePinned { pinned ->
                FilaLightManager_computePracticalSplits(pinned, cascades, near, far, lambda)
            }
        }
    }

    /**
     * Builder for creating and configuring light components.
     * All builder methods return this Builder for method chaining.
     * @param type Type of light to create
     */
    class Builder(type: Type) {
        private val nativeBuilder = FilaLightManagerBuilder_create(type.ordinal)

        /**
         * Enable or disable a light channel (0-7). Channel 0 enabled by default.
         * Use channels to selectively control which lights affect which objects.
         * @param channel Light channel index [0, 7]
         * @param enable Whether to enable (true) or disable (false)
         * @return This Builder
         */
        fun lightChannel(channel: Int, enable: Boolean): Builder = apply { FilaLightManagerBuilder_lightChannel(nativeBuilder, channel, enable) }
        /**
         * Enable shadows for this light (disabled by default).
         * Only directional and spot lights can cast shadows.
         * @param enable Whether shadows are cast
         * @return This Builder
         */
        fun castShadows(enable: Boolean): Builder = apply { FilaLightManagerBuilder_castShadows(nativeBuilder, enable) }
        /**
         * Set shadow map quality and performance parameters.
         * @param options ShadowOptions configuration
         * @return This Builder
         */
        fun shadowOptions(options: ShadowOptions): Builder = apply {
            with(options) {
                FilaLightManagerBuilder_shadowOptions(
                    nativeBuilder, mapSize, shadowCascades,
                    cascadeSplitPositions[0], cascadeSplitPositions[1], cascadeSplitPositions[2],
                    constantBias, normalBias, shadowFar, shadowNearHint, shadowFarHint, stable, lispsm,
                    polygonOffsetConstant, polygonOffsetSlope, screenSpaceContactShadows, stepCount, maxShadowDistance,
                    elvsm, blurWidth, shadowBulbRadius,
                    transform[0], transform[1], transform[2], transform[3],
                    penumbraScale, penumbraRatioScale, maxPenumbraRatio, maxSearchRadius,
                )
            }
        }
        /**
         * Whether this light casts light (enabled by default).
         * Useful for lights that cast shadows without illuminating the scene.
         * @param enabled Whether light is emitted
         * @return This Builder
         */
        fun castLight(enabled: Boolean): Builder = apply { FilaLightManagerBuilder_castLight(nativeBuilder, enabled) }
        /**
         * Set the light's initial position in world space (ignored for directional lights).
         * Default: origin (0, 0, 0).
         * @param x World X position
         * @param y World Y position
         * @param z World Z position
         * @return This Builder
         */
        fun position(x: Float, y: Float, z: Float): Builder = apply { FilaLightManagerBuilder_position(nativeBuilder, x, y, z) }
        /**
         * Set the light's initial direction in world space (should be a unit vector).
         * Ignored for point lights. Default: (0, -1, 0) (downward).
         * @param x World direction X
         * @param y World direction Y
         * @param z World direction Z
         * @return This Builder
         */
        fun direction(x: Float, y: Float, z: Float): Builder = apply { FilaLightManagerBuilder_direction(nativeBuilder, x, y, z) }
        /**
         * Set the light's color in linear sRGB. Default: white (1, 1, 1).
         * @param linearR Linear red channel [0, ∞)
         * @param linearG Linear green channel [0, ∞)
         * @param linearB Linear blue channel [0, ∞)
         * @return This Builder
         */
        fun color(linearR: Float, linearG: Float, linearB: Float): Builder = apply { FilaLightManagerBuilder_color(nativeBuilder, linearR, linearG, linearB) }
        /**
         * Set the light's intensity. Meaning depends on light type:
         * - Directional: illuminance in lux (lumen/m²)
         * - Point/Spot: luminous power in lumen
         *
         * Example: sun's illuminance ≈ 100,000 lux.
         * Overrides prior intensity or intensityCandela calls.
         * @param intensity Intensity value
         * @return This Builder
         */
        fun intensity(intensity: Float): Builder = apply { FilaLightManagerBuilder_intensity(nativeBuilder, intensity) }
        /**
         * Set the light's intensity from electrical watts and efficiency.
         * Commercial lightbulbs often list wattage; efficiency depends on bulb type:
         * - Incandescent: 2.2%, Halogen: 7.0%, LED: 8.7%, Fluorescent: 10.7%
         * Equivalent to `intensity(efficiency * 683 * watts)`.
         * Overrides prior intensity or intensityCandela calls.
         * @param watts Electrical power consumed
         * @param efficiency Efficiency as a fraction (e.g., 0.087 for 8.7%)
         * @return This Builder
         */
        fun intensity(watts: Float, efficiency: Float): Builder = apply { FilaLightManagerBuilder_intensityEfficiency(nativeBuilder, watts, efficiency) }
        /**
         * Set the light's intensity in candela (luminous intensity).
         * For directional lights, equivalent to [intensity].
         * Overrides prior intensity or intensityCandela calls.
         * @param intensity Luminous intensity in candela
         * @return This Builder
         */
        fun intensityCandela(intensity: Float): Builder = apply { FilaLightManagerBuilder_intensityCandela(nativeBuilder, intensity) }
        /**
         * Set the falloff distance for point and spot lights (ignored for directional lights).
         * Beyond this distance, the light has no effect. Defines the light's sphere of influence,
         * impacting performance. Minimize overlapping spheres of influence for best performance.
         * Default: 1 meter.
         * @param radius Falloff distance in world units
         * @return This Builder
         */
        fun falloff(radius: Float): Builder = apply { FilaLightManagerBuilder_falloff(nativeBuilder, radius) }
        /**
         * Define a spot light's angular falloff via inner and outer cones (half-angles in radians).
         * Both angles are clamped to [0.00873, π/2] to avoid precision issues.
         * Ignored for directional and point lights.
         * For [Type.FOCUSED_SPOT] (physically correct), outer cone angle affects total illumination.
         * For [Type.SPOT], outer cone and illumination are decoupled (easier to tweak).
         * @param inner Inner cone angle in radians (0.00873-outer)
         * @param outer Outer cone angle in radians (0.00873-π/2)
         * @return This Builder
         */
        fun spotLightCone(inner: Float, outer: Float): Builder = apply { FilaLightManagerBuilder_spotLightCone(nativeBuilder, inner, outer) }
        /**
         * Set the sun's angular radius in degrees (only for [Type.SUN] lights).
         * Earth's sun appears 0.526°-0.545°; range [0.25°, 20.0°].
         * Default: 0.545°.
         * @param angularRadius Angular radius in degrees
         * @return This Builder
         */
        fun sunAngularRadius(angularRadius: Float): Builder = apply { FilaLightManagerBuilder_sunAngularRadius(nativeBuilder, angularRadius) }
        /**
         * Set the sun's halo radius as a multiplier of [sunAngularRadius].
         * Must be at least 1.0. Default: 10.0.
         * @param haloSize Radius multiplier
         * @return This Builder
         */
        fun sunHaloSize(haloSize: Float): Builder = apply { FilaLightManagerBuilder_sunHaloSize(nativeBuilder, haloSize) }
        /**
         * Set the sun's halo falloff exponent. Must be at least 1.0. Default: 80.0.
         * Controls how quickly the halo dims away from the sun's edge.
         * @param haloFalloff Falloff exponent
         * @return This Builder
         */
        fun sunHaloFalloff(haloFalloff: Float): Builder = apply { FilaLightManagerBuilder_sunHaloFalloff(nativeBuilder, haloFalloff) }
        /**
         * Build and attach the light component to an entity.
         * If the entity already has a light, it is destroyed first.
         * Supports up to 2048 lights per Engine.
         * @param engine Engine to associate this light with
         * @param entity Entity to attach the light component to
         * @return This Builder
         */
        fun build(engine: Engine, entity: Entity) {
            FilaLightManagerBuilder_build(nativeBuilder, engine.nativeHandle, entity)
            FilaLightManagerBuilder_destroy(nativeBuilder)
        }
    }

    /**
     * Returns the number of light components (may include inactive/destroyed lights).
     * Check with EntityManager.isAlive() before use if needed.
     * @return Number of light components
     * @throws UnsupportedOperationException on JS — getComponentCount is unbound in the web wrapper.
     */
    val componentCount: Int get() = FilaLightManager_getComponentCount(nativeHandle)
    /**
     * Returns whether a particular entity has a light component.
     * @param entity Entity to check
     * @return true if entity has a light component
     */
    fun hasComponent(entity: Entity): Boolean = FilaLightManager_hasComponent(nativeHandle, entity)
    /**
     * Get the light component instance for an entity.
     * @param entity Entity with a light component
     * @return EntityInstance representing the light
     */
    fun getInstance(entity: Entity): EntityInstance = FilaLightManager_getInstance(nativeHandle, entity)
    /**
     * Destroy the light component on an entity.
     * @param entity Entity whose light component is destroyed
     */
    fun destroy(entity: Entity) { FilaLightManager_destroy(nativeHandle, entity) }

    /**
     * Get the type of a light.
     * @param instance Light instance obtained from getInstance()
     * @return Light type
     */
    fun getType(instance: EntityInstance): Type = Type.entries[FilaLightManager_getType(nativeHandle, instance)]
    /**
     * Dynamically update the light's direction in world space.
     * Ignored for point lights. Should be a unit vector.
     * @param instance Light instance
     * @param x World direction X
     * @param y World direction Y
     * @param z World direction Z
     */
    fun setDirection(instance: EntityInstance, x: Float, y: Float, z: Float) { FilaLightManager_setDirection(nativeHandle, instance, x, y, z) }
    /**
     * Get the light's direction in world space.
     * @param instance Light instance
     * @param out FloatArray of at least 3 elements [x, y, z]
     * @return The out array
     */
    fun getDirection(instance: EntityInstance, out: FloatArray? = null): FloatArray {
        val result = out ?: FloatArray(3)
        result.usePinned { pinned ->
            FilaLightManager_getDirection(nativeHandle, instance, pinned)
        }
        return result
    }
    /**
     * Dynamically update the light's position in world space.
     * Ignored for directional lights.
     * @param instance Light instance
     * @param x World X position
     * @param y World Y position
     * @param z World Z position
     */
    fun setPosition(instance: EntityInstance, x: Float, y: Float, z: Float) { FilaLightManager_setPosition(nativeHandle, instance, x, y, z) }
    /**
     * Get the light's position in world space.
     * @param instance Light instance
     * @param out FloatArray of at least 3 elements [x, y, z]
     * @return The out array
     */
    fun getPosition(instance: EntityInstance, out: FloatArray? = null): FloatArray {
        val result = out ?: FloatArray(3)
        result.usePinned { pinned ->
            FilaLightManager_getPosition(nativeHandle, instance, pinned)
        }
        return result
    }
    /**
     * Dynamically update the light's color in linear sRGB.
     * @param instance Light instance
     * @param r Linear red channel
     * @param g Linear green channel
     * @param b Linear blue channel
     */
    fun setColor(instance: EntityInstance, r: Float, g: Float, b: Float) { FilaLightManager_setColor(nativeHandle, instance, r, g, b) }
    /**
     * Get the light's color in linear sRGB.
     * @param instance Light instance
     * @param out FloatArray of at least 3 elements [r, g, b]
     * @return The out array
     */
    fun getColor(instance: EntityInstance, out: FloatArray? = null): FloatArray {
        val result = out ?: FloatArray(3)
        result.usePinned { pinned ->
            FilaLightManager_getColor(nativeHandle, instance, pinned)
        }
        return result
    }
    /**
     * Dynamically update the light's intensity.
     * Meaning depends on light type:
     * - Directional: illuminance in lux
     * - Point/Spot: luminous power in lumen
     * Intensity can be negative.
     * @param instance Light instance
     * @param intensity New intensity
     */
    fun setIntensity(instance: EntityInstance, intensity: Float) { FilaLightManager_setIntensity(nativeHandle, instance, intensity) }
    /**
     * Dynamically update the light's intensity from watts and efficiency.
     * Equivalent to `setIntensity(watts * 683.0 * efficiency)`.
     * @param instance Light instance
     * @param watts Electrical power
     * @param efficiency Efficiency as a fraction
     */
    fun setIntensity(instance: EntityInstance, watts: Float, efficiency: Float) { FilaLightManager_setIntensityEfficiency(nativeHandle, instance, watts, efficiency) }
    /**
     * Dynamically update the light's intensity in candela.
     * For directional lights, equivalent to [setIntensity].
     * For [Type.FOCUSED_SPOT], the returned value depends on outer cone angle.
     * @param instance Light instance
     * @param intensity Luminous intensity in candela
     */
    fun setIntensityCandela(instance: EntityInstance, intensity: Float) { FilaLightManager_setIntensityCandela(nativeHandle, instance, intensity) }
    /**
     * Get the light's luminous intensity in candela.
     * @param instance Light instance
     * @return Intensity in candela
     */
    fun getIntensity(instance: EntityInstance): Float = FilaLightManager_getIntensity(nativeHandle, instance)
    /**
     * Dynamically update the light's falloff distance for point/spot lights.
     * Ignored for directional lights.
     * @param instance Light instance
     * @param radius Falloff distance in world units
     */
    fun setFalloff(instance: EntityInstance, radius: Float) { FilaLightManager_setFalloff(nativeHandle, instance, radius) }
    /**
     * Get the light's falloff distance.
     * @param instance Light instance
     * @return Falloff distance in world units
     */
    fun getFalloff(instance: EntityInstance): Float = FilaLightManager_getFalloff(nativeHandle, instance)
    /**
     * Dynamically update a spot light's cone angles (half-angles in radians).
     * Ignored for directional and point lights.
     * @param instance Light instance
     * @param inner Inner cone angle in radians
     * @param outer Outer cone angle in radians
     */
    fun setSpotLightCone(instance: EntityInstance, inner: Float, outer: Float) { FilaLightManager_setSpotLightCone(nativeHandle, instance, inner, outer) }
    /**
     * Get the spot light's inner cone angle in radians.
     * The value may differ slightly from what was set due to recomputation.
     * @param instance Light instance
     * @return Inner cone angle in radians
     */
    fun getInnerConeAngle(instance: EntityInstance): Float = FilaLightManager_getSpotLightInnerCone(nativeHandle, instance)
    /**
     * Get the spot light's outer cone angle in radians.
     * @param instance Light instance
     * @return Outer cone angle in radians
     */
    fun getOuterConeAngle(instance: EntityInstance): Float = FilaLightManager_getSpotLightOuterCone(nativeHandle, instance)
    /**
     * Dynamically update the sun's angular radius in degrees.
     * Only applicable to [Type.SUN] lights.
     * @param instance Light instance
     * @param angularRadius Angular radius in degrees
     */
    fun setSunAngularRadius(instance: EntityInstance, angularRadius: Float) { FilaLightManager_setSunAngularRadius(nativeHandle, instance, angularRadius) }
    /**
     * Get the sun's angular radius in degrees.
     * @param instance Light instance
     * @return Angular radius in degrees
     */
    fun getSunAngularRadius(instance: EntityInstance): Float = FilaLightManager_getSunAngularRadius(nativeHandle, instance)
    /**
     * Dynamically update the sun's halo radius as a multiplier of angular radius.
     * Only applicable to [Type.SUN] lights.
     * @param instance Light instance
     * @param haloSize Radius multiplier
     */
    fun setSunHaloSize(instance: EntityInstance, haloSize: Float) { FilaLightManager_setSunHaloSize(nativeHandle, instance, haloSize) }
    /**
     * Get the sun's halo size multiplier.
     * @param instance Light instance
     * @return Halo size multiplier
     */
    fun getSunHaloSize(instance: EntityInstance): Float = FilaLightManager_getSunHaloSize(nativeHandle, instance)
    /**
     * Dynamically update the sun's halo falloff exponent.
     * Only applicable to [Type.SUN] lights.
     * @param instance Light instance
     * @param haloFalloff Falloff exponent
     */
    fun setSunHaloFalloff(instance: EntityInstance, haloFalloff: Float) { FilaLightManager_setSunHaloFalloff(nativeHandle, instance, haloFalloff) }
    /**
     * Get the sun's halo falloff exponent.
     * @param instance Light instance
     * @return Halo falloff exponent
     */
    fun getSunHaloFalloff(instance: EntityInstance): Float = FilaLightManager_getSunHaloFalloff(nativeHandle, instance)
    /**
     * Dynamically enable or disable shadow casting for this light.
     * Only directional and spot lights can cast shadows.
     * @param instance Light instance
     * @param shadowCaster Whether this light casts shadows
     */
    fun setShadowCaster(instance: EntityInstance, shadowCaster: Boolean) { FilaLightManager_setShadowCaster(nativeHandle, instance, shadowCaster) }
    /**
     * Check whether this light casts shadows.
     * @param instance Light instance
     * @return true if this light casts shadows
     */
    fun isShadowCaster(instance: EntityInstance): Boolean = FilaLightManager_isShadowCaster(nativeHandle, instance)
    /**
     * Enable or disable a light channel (0-7) on this light.
     * Channel 0 is enabled by default.
     * @param instance Light instance
     * @param channel Channel index [0, 7]
     * @param enable Whether to enable the channel
     */
    fun setLightChannel(instance: EntityInstance, channel: Int, enable: Boolean) { FilaLightManager_setLightChannel(nativeHandle, instance, channel, enable) }
    /**
     * Check if a light channel is enabled on this light.
     * @param instance Light instance
     * @param channel Channel index [0, 7]
     * @return true if the channel is enabled
     */
    fun getLightChannel(instance: EntityInstance, channel: Int): Boolean = FilaLightManager_getLightChannel(nativeHandle, instance, channel)
}

@ExternalSymbolName("FilaLightManagerBuilder_build")
private external fun FilaLightManagerBuilder_build(builder: NativePointer, engine: NativePointer, entity: Int): Boolean

@ExternalSymbolName("FilaLightManagerBuilder_castLight")
private external fun FilaLightManagerBuilder_castLight(builder: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaLightManagerBuilder_castShadows")
private external fun FilaLightManagerBuilder_castShadows(builder: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaLightManagerBuilder_color")
private external fun FilaLightManagerBuilder_color(builder: NativePointer, linearR: Float, linearG: Float, linearB: Float)

@ExternalSymbolName("FilaLightManagerBuilder_create")
private external fun FilaLightManagerBuilder_create(type: Int): NativePointer

@ExternalSymbolName("FilaLightManagerBuilder_destroy")
private external fun FilaLightManagerBuilder_destroy(builder: NativePointer)

@ExternalSymbolName("FilaLightManagerBuilder_direction")
private external fun FilaLightManagerBuilder_direction(builder: NativePointer, x: Float, y: Float, z: Float)

@ExternalSymbolName("FilaLightManagerBuilder_falloff")
private external fun FilaLightManagerBuilder_falloff(builder: NativePointer, radius: Float)

@ExternalSymbolName("FilaLightManagerBuilder_intensity")
private external fun FilaLightManagerBuilder_intensity(builder: NativePointer, intensity: Float)

@ExternalSymbolName("FilaLightManagerBuilder_intensityCandela")
private external fun FilaLightManagerBuilder_intensityCandela(builder: NativePointer, intensity: Float)

@ExternalSymbolName("FilaLightManagerBuilder_intensityEfficiency")
private external fun FilaLightManagerBuilder_intensityEfficiency(builder: NativePointer, watts: Float, efficiency: Float)

@ExternalSymbolName("FilaLightManagerBuilder_lightChannel")
private external fun FilaLightManagerBuilder_lightChannel(builder: NativePointer, channel: Int, enable: Boolean)

@ExternalSymbolName("FilaLightManagerBuilder_position")
private external fun FilaLightManagerBuilder_position(builder: NativePointer, x: Float, y: Float, z: Float)

@ExternalSymbolName("FilaLightManagerBuilder_shadowOptions")
private external fun FilaLightManagerBuilder_shadowOptions(builder: NativePointer, mapSize: Int, shadowCascades: Int, cascadeSplitPositions_0: Float, cascadeSplitPositions_1: Float, cascadeSplitPositions_2: Float, constantBias: Float, normalBias: Float, shadowFar: Float, shadowNearHint: Float, shadowFarHint: Float, stable: Boolean, lispsm: Boolean, polygonOffsetConstant: Float, polygonOffsetSlope: Float, screenSpaceContactShadows: Boolean, stepCount: Int, maxShadowDistance: Float, vsm_elvsm: Boolean, vsm_blurWidth: Float, shadowBulbRadius: Float, transform_0: Float, transform_1: Float, transform_2: Float, transform_3: Float, penumbraScale: Float, penumbraRatioScale: Float, maxPenumbraRatio: Float, maxSearchRadius: Float)

@ExternalSymbolName("FilaLightManagerBuilder_spotLightCone")
private external fun FilaLightManagerBuilder_spotLightCone(builder: NativePointer, inner: Float, outer: Float)

@ExternalSymbolName("FilaLightManagerBuilder_sunAngularRadius")
private external fun FilaLightManagerBuilder_sunAngularRadius(builder: NativePointer, angularRadius: Float)

@ExternalSymbolName("FilaLightManagerBuilder_sunHaloFalloff")
private external fun FilaLightManagerBuilder_sunHaloFalloff(builder: NativePointer, haloFalloff: Float)

@ExternalSymbolName("FilaLightManagerBuilder_sunHaloSize")
private external fun FilaLightManagerBuilder_sunHaloSize(builder: NativePointer, haloSize: Float)

@ExternalSymbolName("FilaLightManager_computeLogSplits")
private external fun FilaLightManager_computeLogSplits(splitPositions: NativePointer, cascades: Int, nearPlane: Float, farPlane: Float)

@ExternalSymbolName("FilaLightManager_computePracticalSplits")
private external fun FilaLightManager_computePracticalSplits(splitPositions: NativePointer, cascades: Int, nearPlane: Float, farPlane: Float, lambda: Float)

@ExternalSymbolName("FilaLightManager_computeUniformSplits")
private external fun FilaLightManager_computeUniformSplits(splitPositions: NativePointer, cascades: Int)

@ExternalSymbolName("FilaLightManager_destroy")
private external fun FilaLightManager_destroy(lm: NativePointer, entity: Int)

@ExternalSymbolName("FilaLightManager_getColor")
private external fun FilaLightManager_getColor(lm: NativePointer, instance: Int, out: NativePointer)

@ExternalSymbolName("FilaLightManager_getComponentCount")
private external fun FilaLightManager_getComponentCount(lm: NativePointer): Int

@ExternalSymbolName("FilaLightManager_getDirection")
private external fun FilaLightManager_getDirection(lm: NativePointer, instance: Int, out: NativePointer)

@ExternalSymbolName("FilaLightManager_getFalloff")
private external fun FilaLightManager_getFalloff(lm: NativePointer, instance: Int): Float

@ExternalSymbolName("FilaLightManager_getInstance")
private external fun FilaLightManager_getInstance(lm: NativePointer, entity: Int): Int

@ExternalSymbolName("FilaLightManager_getIntensity")
private external fun FilaLightManager_getIntensity(lm: NativePointer, instance: Int): Float

@ExternalSymbolName("FilaLightManager_getLightChannel")
private external fun FilaLightManager_getLightChannel(lm: NativePointer, instance: Int, channel: Int): Boolean

@ExternalSymbolName("FilaLightManager_getPosition")
private external fun FilaLightManager_getPosition(lm: NativePointer, instance: Int, out: NativePointer)

@ExternalSymbolName("FilaLightManager_getSpotLightInnerCone")
private external fun FilaLightManager_getSpotLightInnerCone(lm: NativePointer, instance: Int): Float

@ExternalSymbolName("FilaLightManager_getSpotLightOuterCone")
private external fun FilaLightManager_getSpotLightOuterCone(lm: NativePointer, instance: Int): Float

@ExternalSymbolName("FilaLightManager_getSunAngularRadius")
private external fun FilaLightManager_getSunAngularRadius(lm: NativePointer, instance: Int): Float

@ExternalSymbolName("FilaLightManager_getSunHaloFalloff")
private external fun FilaLightManager_getSunHaloFalloff(lm: NativePointer, instance: Int): Float

@ExternalSymbolName("FilaLightManager_getSunHaloSize")
private external fun FilaLightManager_getSunHaloSize(lm: NativePointer, instance: Int): Float

@ExternalSymbolName("FilaLightManager_getType")
private external fun FilaLightManager_getType(lm: NativePointer, instance: Int): Int

@ExternalSymbolName("FilaLightManager_hasComponent")
private external fun FilaLightManager_hasComponent(lm: NativePointer, entity: Int): Boolean

@ExternalSymbolName("FilaLightManager_isShadowCaster")
private external fun FilaLightManager_isShadowCaster(lm: NativePointer, instance: Int): Boolean

@ExternalSymbolName("FilaLightManager_setColor")
private external fun FilaLightManager_setColor(lm: NativePointer, instance: Int, linearR: Float, linearG: Float, linearB: Float)

@ExternalSymbolName("FilaLightManager_setDirection")
private external fun FilaLightManager_setDirection(lm: NativePointer, instance: Int, x: Float, y: Float, z: Float)

@ExternalSymbolName("FilaLightManager_setFalloff")
private external fun FilaLightManager_setFalloff(lm: NativePointer, instance: Int, radius: Float)

@ExternalSymbolName("FilaLightManager_setIntensity")
private external fun FilaLightManager_setIntensity(lm: NativePointer, instance: Int, intensity: Float)

@ExternalSymbolName("FilaLightManager_setIntensityCandela")
private external fun FilaLightManager_setIntensityCandela(lm: NativePointer, instance: Int, intensity: Float)

@ExternalSymbolName("FilaLightManager_setIntensityEfficiency")
private external fun FilaLightManager_setIntensityEfficiency(lm: NativePointer, instance: Int, watts: Float, efficiency: Float)

@ExternalSymbolName("FilaLightManager_setLightChannel")
private external fun FilaLightManager_setLightChannel(lm: NativePointer, instance: Int, channel: Int, enable: Boolean)

@ExternalSymbolName("FilaLightManager_setPosition")
private external fun FilaLightManager_setPosition(lm: NativePointer, instance: Int, x: Float, y: Float, z: Float)

@ExternalSymbolName("FilaLightManager_setShadowCaster")
private external fun FilaLightManager_setShadowCaster(lm: NativePointer, instance: Int, shadowCaster: Boolean)

@ExternalSymbolName("FilaLightManager_setSpotLightCone")
private external fun FilaLightManager_setSpotLightCone(lm: NativePointer, instance: Int, inner: Float, outer: Float)

@ExternalSymbolName("FilaLightManager_setSunAngularRadius")
private external fun FilaLightManager_setSunAngularRadius(lm: NativePointer, instance: Int, angularRadius: Float)

@ExternalSymbolName("FilaLightManager_setSunHaloFalloff")
private external fun FilaLightManager_setSunHaloFalloff(lm: NativePointer, instance: Int, haloFalloff: Float)

@ExternalSymbolName("FilaLightManager_setSunHaloSize")
private external fun FilaLightManager_setSunHaloSize(lm: NativePointer, instance: Int, haloSize: Float)
