package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

/**
 * Options for Screen-space Reflections.
 * @see #setScreenSpaceReflectionsOptions
 */
external interface View_ScreenSpaceReflectionsOptions : JsAny {
/** ray thickness, in world units */
var thickness: Double?
/** bias, in world units, to prevent self-intersections */
var bias: Double?
/** maximum distance, in world units, to raycast */
var maxDistance: Double?
/** stride, in texels, for samples along the ray. */
var stride: Double?
var enabled: Boolean?
}
