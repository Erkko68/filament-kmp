// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

/**
 * Ground Truth-base Ambient Occlusion (GTAO) options
 */
external interface View_AmbientOcclusionOptions_Gtao : JsAny {
/** # of slices. Higher value makes less noise. */
var sampleSliceCount: Double?
/** # of steps the radius is divided into for integration. Higher value makes less bias. */
var sampleStepsPerSlice: Double?
/** thickness heuristic, should be closed to 0. No effect when useVisibilityBitmasks sets to true. */
var thicknessHeuristic: Double?
/**
     * Enables or disables visibility bitmasks mode. Notes that bent normal doesn't work under this mode.
     * Caution: Changing this option at runtime is very expensive as it may trigger a shader re-compilation.
     */
var useVisibilityBitmasks: Boolean?
/** constant thickness value of objects on the screen in world space. Only take effect when useVisibilityBitmasks is set to true. */
var constThickness: Double?
/**
     * Increase thickness with distance to maintain detail on distant surfaces.
     * Caution: Changing this option at runtime is very expensive as it may trigger a shader re-compilation.
     */
var linearThickness: Boolean?
}
