// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

/**
 * Options for Multi-Sample Anti-aliasing (MSAA)
 * @see #setMultiSampleAntiAliasingOptions
 */
external interface View_MultiSampleAntiAliasingOptions : JsAny {
/** enables or disables msaa */
var enabled: Boolean?
/**
     * sampleCount number of samples to use for multi-sampled anti-aliasing.<br>
     *              0: treated as 1
     *              1: no anti-aliasing
     *              n: sample count. Effective sample could be different depending on the
     *                 GPU capabilities.
     */
var sampleCount: Double?
/**
     * custom resolve improves quality for HDR scenes, but may impact performance.
     */
var customResolve: Boolean?
}
