package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

/**
 * Dynamic resolution can be used to either reach a desired target frame rate
 * by lowering the resolution of a View, or to increase the quality when the
 * rendering is faster than the target frame rate.
 *
 * <p>This structure can be used to specify the minimum scale factor used when
 * lowering the resolution of a View, and the maximum scale factor used when
 * increasing the resolution for higher quality rendering. The scale factors
 * can be controlled on each X and Y axis independently. By default, all scale
 * factors are set to 1.0.</p>
 *
 * <ul>
 * <li>enabled:   enable or disables dynamic resolution on a View</li>
 *
 * <li>homogeneousScaling: by default the system scales the major axis first. Set this to true
 *                     to force homogeneous scaling.</li>
 *
 * <li>minScale:  the minimum scale in X and Y this View should use</li>
 *
 * <li>maxScale:  the maximum scale in X and Y this View should use</li>
 *
 * <li>quality:   upscaling quality.
 *            LOW: 1 bilinear tap, Medium: 4 bilinear taps, High: 9 bilinear taps (tent)</li>
 * </ul>
 *
 * <p>Note:
 * Dynamic resolution is only supported on platforms where the time to render
 * a frame can be measured accurately. On platforms where this is not supported,
 * Dynamic Resolution can't be enabled unless <code>minScale == maxScale</code>.</p>
 *
 * @see Renderer.FrameRateOptions
 *
 */
external interface View_DynamicResolutionOptions : JsAny {
/** minimum scale factors in x and y */
var minScale: float2
/** maximum scale factors in x and y */
var maxScale: float2
/** sharpness when QualityLevel::MEDIUM or higher is used [0 (disabled), 1 (sharpest)] */
var sharpness: Double?
/** enable or disable dynamic resolution */
var enabled: Boolean?
/** set to true to force homogeneous scaling */
var homogeneousScaling: Boolean?
/**
     * Upscaling quality
     * <ul>
     * <li>LOW:    bilinear filtered blit. Fastest, poor quality</li>
     * <li>MEDIUM: Qualcomm Snapdragon Game Super Resolution (SGSR) 1.0</li>
     * <li>HIGH:   AMD FidelityFX FSR1 w/ mobile optimizations</li>
     * <li>ULTRA:  AMD FidelityFX FSR1</li>
     * </ul>
     *      FSR1 and SGSR require a well anti-aliased (MSAA or TAA), noise free scene.
     *      Avoid FXAA and dithering.
     *
     * <p>The default upscaling quality is set to LOW.</p>
     *
     * <p>caveat: currently, <code>quality</code> is always set to LOW if the View is TRANSLUCENT.</p>
     */
var quality: View_QualityLevel?
}
