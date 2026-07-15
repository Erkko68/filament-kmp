// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

/**
 * Options to control the vignetting effect.
 */
external interface View_VignetteOptions : JsAny {
/** high values restrict the vignette closer to the corners, between 0 and 1 */
var midPoint: Double?
/** controls the shape of the vignette, from a rounded rectangle (0.0), to an oval (0.5), to a circle (1.0) */
var roundness: Double?
/** softening amount of the vignette effect, between 0 and 1 */
var feather: Double?
/** color of the vignette effect, alpha is currently ignored */
var color: float4
/** enables or disables the vignette effect */
var enabled: Boolean?
}
