package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

/**
 * Options to control the bloom effect
 *
 * <ul>
 * <li>enabled:     Enable or disable the bloom post-processing effect. Disabled by default.</li>
 *
 * <li>levels:      Number of successive blurs to achieve the blur effect, the minimum is 3 and the
 *              maximum is 12. This value together with resolution influences the spread of the
 *              blur effect. This value can be silently reduced to accommodate the original
 *              image size.</li>
 *
 * <li>resolution:  Resolution of bloom's minor axis. The minimum value is 2^levels and the
 *              the maximum is lower of the original resolution and 4096. This parameter is
 *              silently clamped to the minimum and maximum.
 *              It is highly recommended that this value be smaller than the target resolution
 *              after dynamic resolution is applied (horizontally and vertically).</li>
 *
 * <li>strength:    how much of the bloom is added to the original image. Between 0 and 1.</li>
 *
 * <li>blendMode:   Whether the bloom effect is purely additive (false) or mixed with the original
 *              image (true).</li>
 *
 * <li>threshold:   When enabled, a threshold at 1.0 is applied on the source image, this is
 *              useful for artistic reasons and is usually needed when a dirt texture is used.</li>
 *
 * <li>dirt:        A dirt/scratch/smudges texture (that can be RGB), which gets added to the
 *              bloom effect. Smudges are visible where bloom occurs. Threshold must be
 *              enabled for the dirt effect to work properly.</li>
 *
 * <li>dirtStrength: Strength of the dirt texture.</li>
 * </ul>
 */
external interface View_BloomOptions : JsAny {
// JavaScript binding for dirt is not yet supported, must use default value.
// JavaScript binding for dirtStrength is not yet supported, must use default value.
/** bloom's strength between 0.0 and 1.0 */
var strength: Double?
/** resolution of vertical axis (2^levels to 2048) */
var resolution: Double?
/** number of blur levels (1 to 11) */
var levels: Double?
/** how the bloom effect is applied */
var blendMode: View_BloomOptions_BlendMode?
/** whether to threshold the source */
var threshold: Boolean?
/** enable or disable bloom */
var enabled: Boolean?
/** limit highlights to this value before bloom [10, +inf] */
var highlight: Double?
/**
     * Bloom quality level.
     * <ul>
     * <li>LOW (default): use a more optimized down-sampling filter, however there can be artifacts
     *      with dynamic resolution, this can be alleviated by using the homogenous mode.</li>
     * <li>MEDIUM: Good balance between quality and performance.</li>
     * <li>HIGH: In this mode the bloom resolution is automatically increased to avoid artifacts.
     *      This mode can be significantly slower on mobile, especially at high resolution.
     *      This mode greatly improves the anamorphic bloom.</li>
     * </ul>
     */
var quality: View_QualityLevel?
/** enable screen-space lens flare */
var lensFlare: Boolean?
/** enable starburst effect on lens flare */
var starburst: Boolean?
/** amount of chromatic aberration */
var chromaticAberration: Double?
/** number of flare "ghosts" */
var ghostCount: Double?
/** spacing of the ghost in screen units [0, 1[ */
var ghostSpacing: Double?
/** hdr threshold for the ghosts */
var ghostThreshold: Double?
/** thickness of halo in vertical screen units, 0 to disable */
var haloThickness: Double?
/** radius of halo in vertical screen units [0, 0.5] */
var haloRadius: Double?
/** hdr threshold for the halo */
var haloThreshold: Double?
}
