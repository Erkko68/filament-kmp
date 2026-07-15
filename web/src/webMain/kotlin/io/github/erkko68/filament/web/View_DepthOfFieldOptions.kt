// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

/**
 * Options to control Depth of Field (DoF) effect in the scene.
 *
 * <p>cocScale can be used to set the depth of field blur independently of the camera
 * aperture, e.g. for artistic reasons. This can be achieved by setting:
 *      cocScale = cameraAperture / desiredDoFAperture</p>
 *
 * @see Camera
 */
external interface View_DepthOfFieldOptions : JsAny {
/** circle of confusion scale factor (amount of blur) */
var cocScale: Double?
/** width/height aspect ratio of the circle of confusion (simulate anamorphic lenses) */
var cocAspectRatio: Double?
/** maximum aperture diameter in meters (zero to disable rotation) */
var maxApertureDiameter: Double?
/** enable or disable depth of field effect */
var enabled: Boolean?
/** filter to use for filling gaps in the kernel */
var filter: View_DepthOfFieldOptions_Filter?
/** perform DoF processing at native resolution */
var nativeResolution: Boolean?
/** number of kernel rings for foreground tiles */
var foregroundRingCount: Double?
/** number of kernel rings for background tiles */
var backgroundRingCount: Double?
/** number of kernel rings for fast tiles */
var fastGatherRingCount: Double?
/**
     * maximum circle-of-confusion in pixels for the foreground, must be in [0, 32] range.
     * A value of 0 means default, which is 32 on desktop and 24 on mobile.
     */
var maxForegroundCOC: Double?
/**
     * maximum circle-of-confusion in pixels for the background, must be in [0, 32] range.
     * A value of 0 means default, which is 32 on desktop and 24 on mobile.
     */
var maxBackgroundCOC: Double?
}
