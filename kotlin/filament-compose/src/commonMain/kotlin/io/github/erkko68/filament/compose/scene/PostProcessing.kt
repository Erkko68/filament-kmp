package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Immutable
import io.github.erkko68.filament.ColorGrading
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.ToneMapper
import io.github.erkko68.filament.View

/**
 * Per-view visual configuration: post-processing effects plus render-quality options. Pass to
 * [io.github.erkko68.filament.compose.FilamentView] via `postProcessing = ...`.
 *
 * Each effect is a singleton value class — there is exactly one of each per Filament view, so
 * a `null` field means "leave Filament's native default" (for effects that are natively off —
 * bloom, fog, DoF, … — that means off; for FXAA and dithering, which are natively on, null
 * keeps them on), and a non-null field configures that effect explicitly. Animate an effect by
 * passing a new value each frame: `PostProcessing(bloom = Bloom(strength = animated))`.
 *
 * ```kotlin
 * FilamentView(
 *     scene = scene,
 *     cameraState = cam,
 *     postProcessing = PostProcessing(
 *         bloom        = Bloom(strength = 0.2f),
 *         antiAliasing = AntiAliasing(fxaaEnabled = true),
 *     ),
 * )
 * ```
 *
 * @param enabled Master switch for all post-processing (maps to `View.isPostProcessingEnabled`).
 */
@Immutable
data class PostProcessing(
    val enabled: Boolean = true,
    val bloom: Bloom? = null,
    val vignette: Vignette? = null,
    val fog: Fog? = null,
    val ambientOcclusion: AmbientOcclusion? = null,
    val antiAliasing: AntiAliasing? = null,
    val screenSpaceReflections: ScreenSpaceReflections? = null,
    val colorGrade: ColorGrade? = null,
    val depthOfField: DepthOfField? = null,
    val dynamicResolution: DynamicResolution? = null,
    val dithering: Dithering? = null,
    val renderQuality: RenderQuality? = null,
)

/**
 * Bloom — "glow" around bright areas.
 *
 * @param strength How much bloom is blended into the image, `0..1`.
 * @param thresholdEnabled Whether to clip the source at a fixed luminance of `1.0` before
 *   blooming. On by default; off blooms the whole image, which is usually only wanted with a
 *   dirt texture.
 * @param quality Quality of the bloom's up/downsample chain.
 * @param resolution Width in px of the largest mip in the downsample chain. `0` lets Filament
 *   pick a default (~360 px), which can look pixelated on high-DPI displays. Bump to roughly
 *   half the render width for a smoother halo on retina/iOS screens.
 * @param levels Mip levels in the bloom chain (3..12). Higher = wider, softer halo.
 */
@Immutable
data class Bloom(
    val strength: Float = 0.10f,
    val thresholdEnabled: Boolean = true,
    val quality: View.Quality = View.Quality.LOW,
    val resolution: Int = 0,
    val levels: Int = 6,
)

/** Vignette — darkens the corners of the viewport. */
@Immutable
data class Vignette(
    val midPoint: Float = 0.5f,
    val roundness: Float = 0.5f,
    val feather: Float = 0.5f,
    val color: LinearColor = LinearColor(0f, 0f, 0f),
)

/** Height-based and volumetric fog. */
@Immutable
data class Fog(
    val distance: Float = 0.0f,
    val density: Float = 0.1f,
    val height: Float = 0.0f,
    val heightFalloff: Float = 1.0f,
    val color: LinearColor = LinearColor(0f, 0f, 0f),
)

/** Screen-space ambient occlusion (SSAO). */
@Immutable
data class AmbientOcclusion(
    val radius: Float = 0.3f,
    val bias: Float = 0.01f,
    val intensity: Float = 1.0f,
    val quality: View.Quality = View.Quality.LOW,
)

/** Anti-aliasing: MSAA (hardware), FXAA (post-process), and TAA (temporal). */
@Immutable
data class AntiAliasing(
    val msaaEnabled: Boolean = false,
    val msaaSampleCount: Int = 4,
    val fxaaEnabled: Boolean = true,
    val taaEnabled: Boolean = false,
)

/** Screen-space reflections (SSR). */
@Immutable
data class ScreenSpaceReflections(
    val thickness: Float = 0.1f,
    val bias: Float = 0.01f,
    val maxDistance: Float = 3.0f,
)

/**
 * Tone mapping operator selection for [ColorGrade], as a *value*: every variant compares
 * structurally, so two [ColorGrade]s built with the same settings are equal and don't force a
 * ColorGrading rebuild (the native [ToneMapper] instances have identity equality, which would
 * re-bake the grading LUT on every recomposition). The native operator is constructed only when
 * the configuration is actually applied.
 */
@Immutable
sealed interface ToneMapping {
    /** ACES RRT + sRGB ODT — Filament's recommended default. */
    data object ACES : ToneMapping
    /** ACES with a ~1.6 brightness multiplier, for backward compatibility. */
    data object ACESLegacy : ToneMapping
    /** Approximation of ACES aesthetics; exists for backward compatibility. */
    data object Filmic : ToneMapping
    /** Khronos PBR Neutral — preserves material appearance across lighting conditions. */
    data object PBRNeutral : ToneMapping
    /** Gran Turismo 7 operator (250-nit paper white target). */
    data object GT7 : ToneMapping
    /** Clamped linear pass-through; mostly for debugging. */
    data object Linear : ToneMapping
    /** Maps exposure stops to debug colors for validating scene lighting. */
    data object DisplayRange : ToneMapping

    /** AgX operator with an optional creative [look]. */
    data class Agx(val look: ToneMapper.Agx.AgxLook = ToneMapper.Agx.AgxLook.NONE) : ToneMapping

    /** Configurable curve — see [ToneMapper.Generic] for parameter semantics. */
    data class Generic(
        val contrast: Float = 1.55f,
        val midGrayIn: Float = 0.18f,
        val midGrayOut: Float = 0.215f,
        val hdrMax: Float = 10.0f,
    ) : ToneMapping
}

internal fun ToneMapping.toToneMapper(): ToneMapper = when (this) {
    ToneMapping.ACES         -> ToneMapper.ACES()
    ToneMapping.ACESLegacy   -> ToneMapper.ACESLegacy()
    ToneMapping.Filmic       -> ToneMapper.Filmic()
    ToneMapping.PBRNeutral   -> ToneMapper.PBRNeutralToneMapper()
    ToneMapping.GT7          -> ToneMapper.GT7ToneMapper()
    ToneMapping.Linear       -> ToneMapper.Linear()
    ToneMapping.DisplayRange -> ToneMapper.DisplayRange()
    is ToneMapping.Agx       -> ToneMapper.Agx(look)
    is ToneMapping.Generic   -> ToneMapper.Generic(contrast, midGrayIn, midGrayOut, hdrMax)
}

/** Color grading — exposure, white balance, contrast, tone mapping, etc. */
@Immutable
data class ColorGrade(
    val exposure: Float = 0.0f,
    val contrast: Float = 1.0f,
    val vibrance: Float = 1.0f,
    val saturation: Float = 1.0f,
    val whiteBalanceTemperature: Float = 0.0f,
    val whiteBalanceTint: Float = 0.0f,
    val toneMapping: ToneMapping = ToneMapping.ACES,
)

/**
 * Depth-of-field (bokeh). The camera's focus distance and aperture control the focal plane.
 *
 * @param cocScale Scales the circle-of-confusion radius. Larger = more blur.
 * @param nativeResolution Run DoF at native resolution (higher quality, more expensive).
 */
@Immutable
data class DepthOfField(
    val cocScale: Float = 1.0f,
    val maxApertureDiameter: Float = 0.01f,
    val filter: View.DepthOfFieldOptions.Filter = View.DepthOfFieldOptions.Filter.MEDIAN,
    val nativeResolution: Boolean = false,
)

// Shadows are not post-processing — the per-view `Shadows` technique is a `FilamentView` parameter,
// and per-light `ShadowConfig` lives on each light. Both are defined in Shadows.kt.

/**
 * Dynamic resolution scaling — lowers internal resolution under GPU load and upscales.
 *
 * @param minScale Minimum scale factor (e.g. 0.5 = half resolution).
 * @param maxScale Maximum scale factor (1.0 = native).
 */
@Immutable
data class DynamicResolution(
    val minScale: Float = 0.5f,
    val maxScale: Float = 1.0f,
    val sharpness: Float = 0.9f,
    val quality: View.Quality = View.Quality.LOW,
    val homogeneousScaling: Boolean = false,
)

/**
 * Dithering applied at tonemap time. [View.Dithering.TEMPORAL] (Filament's native default)
 * hides 8-bit banding in dark gradients and bloom halos.
 */
@Immutable
data class Dithering(val mode: View.Dithering = View.Dithering.TEMPORAL)

/**
 * Precision of the view's HDR color buffer. [View.Quality.HIGH] (the native default) is
 * RGBA16F where supported — needed for emissive values above 1.0 to survive into bloom.
 */
@Immutable
data class RenderQuality(val hdrColorBuffer: View.Quality = View.Quality.HIGH)

/**
 * Applies this configuration to [view], allocating a [ColorGrading] if [colorGrade] is set.
 * Returns the allocated grading (or null) so the caller can destroy it. Sets every option so
 * that re-applying a changed config also clears effects that became null.
 */
internal fun PostProcessing.applyTo(view: View, engine: Engine): ColorGrading? {
    view.isPostProcessingEnabled = enabled

    view.bloomOptions = view.bloomOptions.apply {
        this.enabled = bloom != null
        bloom?.let {
            this.strength = it.strength
            this.threshold = it.thresholdEnabled
            this.quality = it.quality
            this.resolution = it.resolution
            this.levels = it.levels
        }
    }

    view.vignetteOptions = view.vignetteOptions.apply {
        this.enabled = vignette != null
        vignette?.let {
            this.midPoint = it.midPoint
            this.roundness = it.roundness
            this.feather = it.feather
            this.color = floatArrayOf(it.color.r, it.color.g, it.color.b, 1.0f)
        }
    }

    view.fogOptions = view.fogOptions.apply {
        this.enabled = fog != null
        fog?.let {
            this.distance = it.distance
            this.density = it.density
            this.height = it.height
            this.heightFalloff = it.heightFalloff
            this.color = floatArrayOf(it.color.r, it.color.g, it.color.b)
        }
    }

    view.ambientOcclusionOptions = view.ambientOcclusionOptions.apply {
        this.enabled = ambientOcclusion != null
        ambientOcclusion?.let {
            this.radius = it.radius
            this.bias = it.bias
            this.intensity = it.intensity
            this.quality = it.quality
        }
    }

    view.multiSampleAntiAliasingOptions = view.multiSampleAntiAliasingOptions.apply {
        this.enabled = antiAliasing?.msaaEnabled == true
        antiAliasing?.let { this.sampleCount = it.msaaSampleCount }
    }
    // null keeps Filament's native default (FXAA on) — turning AA off requires an explicit
    // AntiAliasing(fxaaEnabled = false), consistent with "null = native default" elsewhere.
    view.antiAliasing = when {
        antiAliasing == null || antiAliasing.fxaaEnabled -> View.AntiAliasing.FXAA
        else -> View.AntiAliasing.NONE
    }
    view.temporalAntiAliasingOptions = view.temporalAntiAliasingOptions.apply {
        this.enabled = antiAliasing?.taaEnabled == true
    }

    view.screenSpaceReflectionsOptions = view.screenSpaceReflectionsOptions.apply {
        this.enabled = screenSpaceReflections != null
        screenSpaceReflections?.let {
            this.thickness = it.thickness
            this.bias = it.bias
            this.maxDistance = it.maxDistance
        }
    }

    view.depthOfFieldOptions = view.depthOfFieldOptions.apply {
        this.enabled = depthOfField != null
        depthOfField?.let {
            this.cocScale = it.cocScale
            this.maxApertureDiameter = it.maxApertureDiameter
            this.filter = it.filter
            this.nativeResolution = it.nativeResolution
        }
    }

    view.dynamicResolutionOptions = view.dynamicResolutionOptions.apply {
        this.enabled = dynamicResolution != null
        dynamicResolution?.let {
            this.minScale = it.minScale
            this.maxScale = it.maxScale
            this.sharpness = it.sharpness
            this.quality = it.quality
            this.homogeneousScaling = it.homogeneousScaling
        }
    }

    view.dithering = dithering?.mode ?: View.Dithering.TEMPORAL
    // null restores Filament's native default (HIGH), like every other nullable effect here.
    view.renderQuality = view.renderQuality.apply {
        this.hdrColorBuffer = renderQuality?.hdrColorBuffer ?: View.Quality.HIGH
    }

    return colorGrade?.let { c ->
        ColorGrading.Builder()
            .exposure(c.exposure)
            .contrast(c.contrast)
            .vibrance(c.vibrance)
            .saturation(c.saturation)
            .whiteBalance(c.whiteBalanceTemperature, c.whiteBalanceTint)
            .toneMapper(c.toneMapping.toToneMapper())
            .build(engine)
            .also { view.colorGrading = it }
    } ?: run {
        view.colorGrading = null
        null
    }
}
