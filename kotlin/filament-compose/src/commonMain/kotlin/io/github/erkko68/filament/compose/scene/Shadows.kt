package io.github.erkko68.filament.compose.scene

import io.github.erkko68.filament.LightManager
import io.github.erkko68.filament.View

/**
 * Shadows in Filament are configured at two levels, and both live here:
 *
 * - **Per-view technique** — [Shadows], passed to a view via `FilamentView(shadows = …)`. Selects the
 *   algorithm ([Shadows.Pcf]/[Shadows.Vsm]/[Shadows.Dpcf]/[Shadows.Pcss]) for the *whole* view and its
 *   global tuning; passing `null` disables shadowing entirely. This is genuinely view-wide — a view
 *   renders one shadow path, so all casters share a technique.
 * - **Per-light quality** — [ShadowConfig], passed as a light's `shadow`. Controls that one caster's
 *   shadow map: resolution, bias, cascades, contact shadows, penumbra size.
 *
 * Both levels disable with `null`, symmetrically. A soft shadow needs both halves: a per-light
 * [ShadowConfig.bulbRadius] *and* a view technique of [Shadows.Dpcf] or [Shadows.Pcss] (this split
 * mirrors Filament — penumbra size is per-caster, the soft algorithm is per-view).
 */

/**
 * Per-light shadow-map quality, mapping onto the core [LightManager.ShadowOptions]. Pass a non-null
 * `shadow` to a [DirectionalLight]/[SunLight]/[SpotLight]/[FocusedSpotLight] to make it cast shadows;
 * `null` disables them. (Point lights cannot cast shadows.)
 *
 * Many fields apply only under a specific view [Shadows] technique (noted per property). Defaults
 * mirror Filament's, except [lispsm] (defaults `false` here for cleaner soft shadows). Filament's
 * `polygonOffset*` depth-bias fields are deliberately **not** exposed — they're package-private in
 * the Android binding and so can't be surfaced cross-platform; use [constantBias]/[normalBias] for
 * acne instead.
 *
 * @property mapSize Shadow-map resolution in texels; power-of-two ≥ 8. Higher = sharper but costlier.
 * @property constantBias World-space depth bias pushing shadows off surfaces (reduces acne). Ignored for VSM.
 * @property normalBias Normal-scaled bias; typically 1.0. Ignored for VSM.
 * @property shadowFar Directional-only: distance after which shadows are clipped. 0 = camera far.
 * @property shadowNearHint Concentrate shadow resolution from this camera distance onward (world units).
 * @property shadowFarHint Concentrate shadow resolution up to this camera distance (world units).
 * @property stable Trade resolution for temporal stability (also forces [lispsm] off). Good for slow pans.
 * @property lispsm Light-space perspective shadow maps — boosts effective resolution near the camera, but
 *   can worsen DPCF/PCSS/VSM penumbra artifacts. Default `false`; ignored when [stable] is true.
 * @property cascades Directional-only: number of CSM cascades (1–4). >1 sharpens large scenes.
 * @property cascadeSplits Optional explicit cascade split positions (size `cascades - 1`, camera-Z
 *   0..1). Null auto-computes uniform splits.
 * @property contactShadows Enable screen-space contact shadows (SSCS) for fine, close-up contact detail.
 * @property contactShadowDistance SSCS max occluder distance, world units.
 * @property contactShadowSteps SSCS ray-march step count (directional lights only); higher = better, costlier.
 * @property bulbRadius Soft-shadow penumbra size, world units. Only under [Shadows.Dpcf]/[Shadows.Pcss].
 * @property blurWidth Blur width; only under the [Shadows.Vsm] technique. 0 disables.
 * @property elvsm Exponential Layered VSM — better light-leak reduction at 2× shadow memory. Only under [Shadows.Vsm].
 * @property transform Optional unit quaternion `(x, y, z, w)` rotating the shadow's direction
 *   (directional-only, artistic). Null = identity (no rotation).
 */
data class ShadowConfig(
    val mapSize: Int = 1024,
    val constantBias: Float = 0.001f,
    val normalBias: Float = 1.0f,
    val shadowFar: Float = 0f,
    val shadowNearHint: Float = 1f,
    val shadowFarHint: Float = 100f,
    val stable: Boolean = false,
    val lispsm: Boolean = false,
    val cascades: Int = 1,
    val cascadeSplits: List<Float>? = null,
    val contactShadows: Boolean = false,
    val contactShadowDistance: Float = 0.3f,
    val contactShadowSteps: Int = 8,
    val bulbRadius: Float = 0.02f,
    val blurWidth: Float = 0f,
    val elvsm: Boolean = false,
    val transform: List<Float>? = null,
)

internal fun ShadowConfig.toShadowOptions(): LightManager.ShadowOptions {
    val o = LightManager.ShadowOptions()
    o.mapSize = mapSize
    o.constantBias = constantBias
    o.normalBias = normalBias
    o.shadowFar = shadowFar
    o.shadowNearHint = shadowNearHint
    o.shadowFarHint = shadowFarHint
    o.stable = stable
    o.lispsm = lispsm
    o.shadowCascades = cascades.coerceIn(1, 4)
    if (o.shadowCascades > 1) {
        o.cascadeSplitPositions = cascadeSplits?.toFloatArray()
            ?: FloatArray(o.shadowCascades - 1).also {
                LightManager.ShadowCascades.computeUniformSplits(it, o.shadowCascades)
            }
    }
    o.screenSpaceContactShadows = contactShadows
    o.maxShadowDistance = contactShadowDistance
    o.stepCount = contactShadowSteps
    o.shadowBulbRadius = bulbRadius
    o.blurWidth = blurWidth
    o.elvsm = elvsm
    transform?.let { o.transform = it.toFloatArray() }
    return o
}

/**
 * View-wide shadow technique, passed to a view via `FilamentView(shadows = …)` (or
 * `FilamentSceneView(shadows = …)`). Each subtype is one of Filament's shadow algorithms and carries
 * only the tuning that algorithm actually uses — so there are no silently-ignored fields. Passing
 * `null` for the view's `shadows` disables shadowing entirely. See [ShadowConfig] for the per-light
 * counterpart that sets each caster's map quality.
 *
 * **Web caveat:** Filament's web build doesn't bind `View::setShadowType`, so the technique is locked
 * to [Pcf]; [Vsm]/[Dpcf]/[Pcss] are silently ignored on web (disabling via `null` still works). See
 * the Web "Current limitations" in docs/platform-notes.md.
 */
sealed interface Shadows {
    /** Percentage-Closer Filtering — hard-edged, cheapest, and Filament's default. */
    data object Pcf : Shadows

    /** A higher-quality PCF variant (`PCFd`). */
    data object Pcfd : Shadows

    /**
     * Variance Shadow Maps — pre-filterable soft edges, good with many receivers.
     *
     * @property anisotropy Anisotropic filtering exponent for the shadow map (texture is 2^n wider).
     * @property mipmapping Generate mipmaps for the VSM shadow map.
     * @property msaaSamples MSAA sample count used when rendering the shadow map (1 = off).
     * @property highPrecision Use a 32-bit shadow texture instead of 16-bit (reduces light bleed).
     * @property lightBleedReduction Cuts VSM's characteristic light bleeding; 0..1.
     */
    data class Vsm(
        val anisotropy: Int = 1,
        val mipmapping: Boolean = false,
        val msaaSamples: Int = 1,
        val highPrecision: Boolean = false,
        val lightBleedReduction: Float = 0.0f,
    ) : Shadows

    /**
     * Dynamic Percentage-Closer soft Filtering — soft shadows whose penumbra grows with each light's
     * [ShadowConfig.bulbRadius]. Cheaper than [Pcss].
     *
     * @property penumbraScale Global multiplier on penumbra size.
     * @property penumbraRatioScale Scales how fast the penumbra widens with distance.
     */
    data class Dpcf(
        val penumbraScale: Float = 1.0f,
        val penumbraRatioScale: Float = 1.0f,
    ) : Shadows

    /**
     * Percentage-Closer Soft Shadows — physically-based soft shadows whose penumbra grows with each
     * light's [ShadowConfig.bulbRadius].
     *
     * @property penumbraScale Global multiplier on penumbra size.
     * @property penumbraRatioScale Scales how fast the penumbra widens with distance.
     */
    data class Pcss(
        val penumbraScale: Float = 1.0f,
        val penumbraRatioScale: Float = 1.0f,
    ) : Shadows
}

/**
 * Applies a view's shadow technique. `null` disables shadowing; otherwise enables it, sets the
 * [View.shadowType], and pushes only the options the chosen technique uses.
 */
internal fun Shadows?.applyTo(view: View) {
    view.isShadowingEnabled = this != null
    when (this) {
        null -> Unit
        Shadows.Pcf  -> view.shadowType = View.ShadowType.PCF
        Shadows.Pcfd -> view.shadowType = View.ShadowType.PCFd
        is Shadows.Vsm -> {
            view.shadowType = View.ShadowType.VSM
            view.vsmShadowOptions = view.vsmShadowOptions.apply {
                anisotropy = this@applyTo.anisotropy
                mipmapping = this@applyTo.mipmapping
                msaaSamples = this@applyTo.msaaSamples
                highPrecision = this@applyTo.highPrecision
                lightBleedReduction = this@applyTo.lightBleedReduction
            }
        }
        is Shadows.Dpcf -> {
            view.shadowType = View.ShadowType.DPCF
            view.applySoftShadows(penumbraScale, penumbraRatioScale)
        }
        is Shadows.Pcss -> {
            view.shadowType = View.ShadowType.PCSS
            view.applySoftShadows(penumbraScale, penumbraRatioScale)
        }
    }
}

private fun View.applySoftShadows(penumbraScale: Float, penumbraRatioScale: Float) {
    softShadowOptions = softShadowOptions.apply {
        this.penumbraScale = penumbraScale
        this.penumbraRatioScale = penumbraRatioScale
    }
}
