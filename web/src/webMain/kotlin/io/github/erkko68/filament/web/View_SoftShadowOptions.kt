package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

/**
 * View-level options for DPCF and PCSS Shadowing.
 * @see #setSoftShadowOptions
 * <b>Warning:</b> This API is still experimental and subject to change.
 */
external interface View_SoftShadowOptions : JsAny {
/**
     * Globally scales the penumbra of all DPCF and PCSS shadows
     * Acceptable values are greater than 0
     */
var penumbraScale: Double?
/**
     * Globally scales the computed penumbra ratio of all DPCF and PCSS shadows.
     * This effectively controls the strength of contact hardening effect and is useful for
     * artistic purposes. Higher values make the shadows become softer faster.
     * Acceptable values are equal to or greater than 1.
     */
var penumbraRatioScale: Double?
/**
     * Global default maximum geometric ratio applied to PCSS, as a smooth asymptotic squash.
     * Individual lights can override it via LightManager$ShadowOptions.maxPenumbraRatio.
     */
var maxPenumbraRatio: Double?
/**
     * Global default maximum world-space radius for the PCSS blocker search.
     * Individual lights can override it via LightManager$ShadowOptions.maxSearchRadius.
     */
var maxSearchRadius: Double?
}
