package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

/**
 * Options for Temporal Anti-aliasing (TAA)
 * Most TAA parameters are extremely costly to change, as they will trigger the TAA post-process
 * shaders to be recompiled. These options should be changed or set during initialization.
 * `filterWidth`, `feedback` and `jitterPattern`, however, can be changed at any time.
 *
 * <p><code>feedback</code> of 0.1 effectively accumulates a maximum of 19 samples in steady state.
 * see "A Survey of Temporal Antialiasing Techniques" by Lei Yang and all for more information.</p>
 *
 * @see #setTemporalAntiAliasingOptions
 */
external interface View_TemporalAntiAliasingOptions : JsAny {
/** @deprecated has no effect. */
var filterWidth: Double?
/** history feedback, between 0 (maximum temporal AA) and 1 (no temporal AA). */
var feedback: Double?
/** texturing lod bias (typically -1 or -2) */
var lodBias: Double?
/** post-TAA sharpen, especially useful when upscaling is true. */
var sharpness: Double?
/** enables or disables temporal anti-aliasing */
var enabled: Boolean?
/** Upscaling factor. Disables Dynamic Resolution. [BETA] */
var upscaling: Double?
/** whether to filter the history buffer */
var filterHistory: Boolean?
/** whether to apply the reconstruction filter to the input */
var filterInput: Boolean?
/** whether to use the YcoCg color-space for history rejection */
var useYCoCg: Boolean?
/** set to true for HDR content */
var hdr: Boolean?
/** type of color gamut box */
var boxType: View_TemporalAntiAliasingOptions_BoxType?
/** clipping algorithm */
var boxClipping: View_TemporalAntiAliasingOptions_BoxClipping?
/** Jitter Pattern */
var jitterPattern: View_TemporalAntiAliasingOptions_JitterPattern?
/** High values increases ghosting artefact, lower values increases jittering, range [0.75, 1.25] */
var varianceGamma: Double?
/** adjust the feedback dynamically to reduce flickering */
var preventFlickering: Boolean?
/** whether to apply history reprojection (debug option) */
var historyReprojection: Boolean?
}
