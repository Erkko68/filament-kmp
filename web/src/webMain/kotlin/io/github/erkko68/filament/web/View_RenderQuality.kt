// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

/**
 * Structure used to set the precision of the color buffer and related quality settings.
 *
 * @see #setRenderQuality
 * @see #getRenderQuality
 */
external interface View_RenderQuality : JsAny {
/**
     * Sets the quality of the HDR color buffer.
     *
     * <p>A quality of HIGH or ULTRA means using an RGB16F or RGBA16F color buffer. This means
     * colors in the LDR range (0..1) have a 10 bit precision. A quality of LOW or MEDIUM means
     * using an R11G11B10F opaque color buffer or an RGBA16F transparent color buffer. With
     * R11G11B10F colors in the LDR range have a precision of either 6 bits (red and green
     * channels) or 5 bits (blue channel).</p>
     */
var hdrColorBuffer: View_QualityLevel?
}
