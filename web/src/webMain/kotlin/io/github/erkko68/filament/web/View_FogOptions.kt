// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

/**
 * Options to control large-scale fog in the scene. Materials can enable the <code>linearFog</code> property,
 * which uses a simplified, linear equation for fog calculation; in this mode, the heightFalloff
 * is ignored as well as the mipmap selection in IBL or skyColor mode.
 */
external interface View_FogOptions : JsAny {
/**
     * Distance in world units [m] from the camera to where the fog starts ( >= 0.0 )
     */
var distance: Double?
/**
     * Distance in world units [m] after which the fog calculation is disabled.
     * This can be used to exclude the skybox, which is desirable if it already contains clouds or
     * fog. The default value is +infinity which applies the fog to everything.
     *
     * <p>Note: The SkyBox is typically at a distance of 1e19 in world space (depending on the near
     * plane distance and projection used though).</p>
     */
var cutOffDistance: Double?
/**
     * fog's maximum opacity between 0 and 1. Ignored in <code>linearFog</code> mode.
     */
var maximumOpacity: Double?
/**
     * Fog's floor in world units [m]. This sets the "sea level".
     */
var height: Double?
/**
     * How fast the fog dissipates with the altitude. heightFalloff has a unit of [1/m].
     * It can be expressed as 1/H, where H is the altitude change in world units [m] that causes a
     * factor 2.78 (e) change in fog density.
     *
     * <p>A falloff of 0 means the fog density is constant everywhere and may result is slightly
     * faster computations.</p>
     *
     * <p>In <code>linearFog</code> mode, only use to compute the slope of the linear equation. Completely
     * ignored if set to 0.</p>
     */
var heightFalloff: Double?
/**
     *  Fog's color is used for ambient light in-scattering, a good value is
     *  to use the average of the ambient light, possibly tinted towards blue
     *  for outdoors environments. Color component's values should be between 0 and 1, values
     *  above one are allowed but could create a non energy-conservative fog (this is dependant
     *  on the IBL's intensity as well).
     *
     *  <p>We assume that our fog has no absorption and therefore all the light it scatters out
     *  becomes ambient light in-scattering and has lost all directionality, i.e.: scattering is
     *  isotropic. This somewhat simulates Rayleigh scattering.</p>
     *
     *  <p>This value is used as a tint instead, when fogColorFromIbl is enabled.</p>
     *
     *  @see #fogColorFromIbl
     */
var color: float3
/**
     * Extinction factor in [1/m] at an altitude 'height'. The extinction factor controls how much
     * light is absorbed and out-scattered per unit of distance. Each unit of extinction reduces
     * the incoming light to 37% of its original value.
     *
     * <p>Note: The extinction factor is related to the fog density, it's usually some constant K times
     * the density at sea level (more specifically at fog height). The constant K depends on
     * the composition of the fog/atmosphere.</p>
     *
     * <p>For historical reason this parameter is called <code>density</code>.</p>
     *
     * <p>In <code>linearFog</code> mode this is the slope of the linear equation if heightFalloff is set to 0.
     * Otherwise, heightFalloff affects the slope calculation such that it matches the slope of
     * the standard equation at the camera height.</p>
     */
var density: Double?
/**
     * Distance in world units [m] from the camera where the Sun in-scattering starts.
     * Ignored in <code>linearFog</code> mode.
     */
var inScatteringStart: Double?
/**
     * Very inaccurately simulates the Sun's in-scattering. That is, the light from the sun that
     * is scattered (by the fog) towards the camera.
     * Size of the Sun in-scattering (>0 to activate). Good values are >> 1 (e.g. ~10 - 100).
     * Smaller values result is a larger scattering size.
     * Ignored in <code>linearFog</code> mode.
     */
var inScatteringSize: Double?
/**
     * The fog color will be sampled from the IBL in the view direction and tinted by <code>color</code>.
     * Depending on the scene this can produce very convincing results.
     *
     * <p>This simulates a more anisotropic phase-function.</p>
     *
     * <p><code>fogColorFromIbl</code> is ignored when skyTexture is specified.</p>
     *
     * @see #skyColor
     */
var fogColorFromIbl: Boolean?
// JavaScript binding for skyColor is not yet supported, must use default value.
/**
     * Enable or disable large-scale fog
     */
var enabled: Boolean?
}
