package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

/**
 * Screen Space Cone Tracing (SSCT) options
 * Ambient shadows from dominant light
 */
external interface View_AmbientOcclusionOptions_Ssct : JsAny {
/** full cone angle in radian, between 0 and pi/2 */
var lightConeRad: Double?
/** how far shadows can be cast */
var shadowDistance: Double?
/** max distance for contact */
var contactDistanceMax: Double?
/** intensity */
var intensity: Double?
/** light direction */
var lightDirection: float3
/** depth bias in world units (mitigate self shadowing) */
var depthBias: Double?
/** depth slope bias (mitigate self shadowing) */
var depthSlopeBias: Double?
/** tracing sample count, between 1 and 255 */
var sampleCount: Double?
/** # of rays to trace, between 1 and 255 */
var rayCount: Double?
/** enables or disables SSCT */
var enabled: Boolean?
}
