// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

/**
 * View-level options for VSM Shadowing.
 * @see #setVsmShadowOptions
 * <b>Warning:</b> This API is still experimental and subject to change.
 */
external interface View_VsmShadowOptions : JsAny {
/**
     * Sets the number of anisotropic samples to use when sampling a VSM shadow map. If greater
     * than 0, mipmaps will automatically be generated each frame for all lights.
     *
     * <p>The number of anisotropic samples = 2 ^ vsmAnisotropy.</p>
     */
var anisotropy: Double?
/**
     * Whether to generate mipmaps for all VSM shadow maps.
     */
var mipmapping: Boolean?
/**
     * The number of MSAA samples to use when rendering VSM shadow maps.
     * Must be a power-of-two and greater than or equal to 1. A value of 1 effectively turns
     * off MSAA.
     * Higher values may not be available depending on the underlying hardware.
     */
var msaaSamples: Double?
/**
     * Whether to use a 32-bits or 16-bits texture format for VSM shadow maps. 32-bits
     * precision is rarely needed, but it does reduce light leaks as well as "fading"
     * of the shadows in some situations. Setting highPrecision to true for a single
     * shadow map will double the memory usage of all shadow maps.
     * This may not be supported on all mobile devices.
     */
var highPrecision: Boolean?
/**
     * @deprecated has no effect.
     */
var minVarianceScale: Double?
/**
     * VSM light bleeding reduction amount, between 0 and 1.
     */
var lightBleedReduction: Double?
}
