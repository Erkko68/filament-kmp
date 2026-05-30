package io.github.erkko68.filament.compose.scene

import io.github.erkko68.filament.ColorGrading
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.ToneMapper
import io.github.erkko68.filament.View

/**
 * Per-view visual configuration: post-processing effects plus render-quality options. Pass to
 * [io.github.erkko68.filament.compose.FilamentView] via `postProcessing = ...`.
 *
 * Each effect is a singleton value class — there is exactly one of each per Filament view, so
 * a `null` field means "leave Filament's native default / effect off", and a non-null field
 * enables and configures that effect. Animate an effect by passing a new value each frame:
 * `PostProcessing(bloom = Bloom(strength = animated))`.
 *
 * ```kotlin
 * FilamentView(
 *     scene = scene,
 *     cameraState = cam,
 *     postProcessing = PostProcessing(
 *         bloom        = Bloom(strength = 0.2f),
 *         antiAliasing = AntiAliasing(fxaaEnabled = true),
 *         shadows      = Shadows(type = View.ShadowType.PCF),
 *     ),
 * )
 * ```
 *
 * @param enabled Master switch for all post-processing (maps to `View.isPostProcessingEnabled`).
 */
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
    val shadows: Shadows? = null,
    val dynamicResolution: DynamicResolution? = null,
    val dithering: Dithering? = null,
    val renderQuality: RenderQuality? = null,
)

/**
 * Bloom — "glow" around bright areas.
 *
 * @param resolution Width in px of the largest mip in the downsample chain. `0` lets Filament
 *   pick a default (~360 px), which can look pixelated on high-DPI displays. Bump to roughly
 *   half the render width for a smoother halo on retina/iOS screens.
 * @param levels Mip levels in the bloom chain (3..12). Higher = wider, softer halo.
 */
data class Bloom(
    val strength: Float = 0.10f,
    val threshold: Boolean = true,
    val quality: View.Quality = View.Quality.LOW,
    val resolution: Int = 0,
    val levels: Int = 6,
)

/** Vignette — darkens the corners of the viewport. */
data class Vignette(
    val midPoint: Float = 0.5f,
    val roundness: Float = 0.5f,
    val feather: Float = 0.5f,
    val color: Color = Color(0f, 0f, 0f),
)

/** Height-based and volumetric fog. */
data class Fog(
    val distance: Float = 0.0f,
    val density: Float = 0.1f,
    val height: Float = 0.0f,
    val heightFalloff: Float = 1.0f,
    val color: Color = Color(0f, 0f, 0f),
)

/** Screen-space ambient occlusion (SSAO). */
data class AmbientOcclusion(
    val radius: Float = 0.3f,
    val bias: Float = 0.01f,
    val intensity: Float = 1.0f,
    val quality: View.Quality = View.Quality.LOW,
)

/** Anti-aliasing: MSAA (hardware), FXAA (post-process), and TAA (temporal). */
data class AntiAliasing(
    val msaaEnabled: Boolean = false,
    val msaaSampleCount: Int = 4,
    val fxaaEnabled: Boolean = true,
    val taaEnabled: Boolean = false,
)

/** Screen-space reflections (SSR). */
data class ScreenSpaceReflections(
    val thickness: Float = 0.1f,
    val bias: Float = 0.01f,
    val maxDistance: Float = 3.0f,
)

/** Color grading — exposure, white balance, contrast, tone mapping, etc. */
data class ColorGrade(
    val exposure: Float = 0.0f,
    val contrast: Float = 1.0f,
    val vibrance: Float = 1.0f,
    val saturation: Float = 1.0f,
    val whiteBalanceTemperature: Float = 0.0f,
    val whiteBalanceTint: Float = 0.0f,
    val toneMapper: ToneMapper = ToneMapper.ACES(),
)

/**
 * Depth-of-field (bokeh). The camera's focus distance and aperture control the focal plane.
 *
 * @param cocScale Scales the circle-of-confusion radius. Larger = more blur.
 * @param nativeResolution Run DoF at native resolution (higher quality, more expensive).
 */
data class DepthOfField(
    val cocScale: Float = 1.0f,
    val maxApertureDiameter: Float = 0.01f,
    val filter: View.DepthOfFieldOptions.Filter = View.DepthOfFieldOptions.Filter.MEDIAN,
    val nativeResolution: Boolean = false,
)

/**
 * Shadow rendering. [type] selects the algorithm; VSM/soft-shadow params apply only to their
 * respective types (Filament ignores irrelevant options).
 */
data class Shadows(
    val type: View.ShadowType = View.ShadowType.PCF,
    val vsmAnisotropy: Int = 1,
    val vsmMipmapping: Boolean = false,
    val vsmMsaaSamples: Int = 1,
    val vsmHighPrecision: Boolean = false,
    val vsmLightBleedReduction: Float = 0.0f,
    val penumbraScale: Float = 1.0f,
    val penumbraRatioScale: Float = 1.0f,
)

/**
 * Dynamic resolution scaling — lowers internal resolution under GPU load and upscales.
 *
 * @param minScale Minimum scale factor (e.g. 0.5 = half resolution).
 * @param maxScale Maximum scale factor (1.0 = native).
 */
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
data class Dithering(val mode: View.Dithering = View.Dithering.TEMPORAL)

/**
 * Precision of the view's HDR color buffer. [View.Quality.HIGH] (the native default) is
 * RGBA16F where supported — needed for emissive values above 1.0 to survive into bloom.
 */
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
            this.threshold = it.threshold
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
    view.antiAliasing = if (antiAliasing?.fxaaEnabled == true) View.AntiAliasing.FXAA else View.AntiAliasing.NONE
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

    // Shadows: null restores the PCF native default.
    view.shadowType = shadows?.type ?: View.ShadowType.PCF
    shadows?.let {
        view.vsmShadowOptions = view.vsmShadowOptions.apply {
            this.anisotropy = it.vsmAnisotropy
            this.mipmapping = it.vsmMipmapping
            this.msaaSamples = it.vsmMsaaSamples
            this.highPrecision = it.vsmHighPrecision
            this.lightBleedReduction = it.vsmLightBleedReduction
        }
        view.softShadowOptions = view.softShadowOptions.apply {
            this.penumbraScale = it.penumbraScale
            this.penumbraRatioScale = it.penumbraRatioScale
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
    renderQuality?.let {
        view.renderQuality = view.renderQuality.apply { this.hdrColorBuffer = it.hdrColorBuffer }
    }

    return colorGrade?.let { c ->
        ColorGrading.Builder()
            .exposure(c.exposure)
            .contrast(c.contrast)
            .vibrance(c.vibrance)
            .saturation(c.saturation)
            .whiteBalance(c.whiteBalanceTemperature, c.whiteBalanceTint)
            .toneMapper(c.toneMapper)
            .build(engine)
            .also { view.colorGrading = it }
    } ?: run {
        view.colorGrading = null
        null
    }
}
