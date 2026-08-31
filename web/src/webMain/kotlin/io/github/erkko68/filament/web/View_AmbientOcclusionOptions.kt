package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

/**
 * Options for screen space Ambient Occlusion (SSAO) and Screen Space Cone Tracing (SSCT)
 * @see #setAmbientOcclusionOptions
 */
external interface View_AmbientOcclusionOptions : JsAny {
/** Type of ambient occlusion algorithm. */
var aoType: View_AmbientOcclusionOptions_AmbientOcclusionType?
/** Ambient Occlusion radius in meters, between 0 and ~10. */
var radius: Double?
/** Controls ambient occlusion's contrast. Must be positive. */
var power: Double?
/**
     * Self-occlusion bias in meters. Use to avoid self-occlusion.
     * Between 0 and a few mm. No effect when aoType set to GTAO
     */
var bias: Double?
/** How each dimension of the AO buffer is scaled. Must be either 0.5 or 1.0. */
var resolution: Double?
/** Strength of the Ambient Occlusion effect. */
var intensity: Double?
/** depth distance that constitute an edge for filtering */
var bilateralThreshold: Double?
/** affects # of samples used for AO and params for filtering */
var quality: View_QualityLevel?
/** affects AO smoothness. Recommend setting to HIGH when aoType set to GTAO. */
var lowPassFilter: View_QualityLevel?
/** affects AO buffer upsampling quality */
var upsampling: View_QualityLevel?
/** enables or disables screen-space ambient occlusion */
var enabled: Boolean?
/** enables bent normals computation from AO, and specular AO */
var bentNormals: Boolean?
/** min angle in radian to consider. No effect when aoType set to GTAO. */
var minHorizonAngleRad: Double?
}
