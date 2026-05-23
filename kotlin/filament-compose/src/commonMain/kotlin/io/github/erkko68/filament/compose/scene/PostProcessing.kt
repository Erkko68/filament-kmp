package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import io.github.erkko68.filament.ColorGrading
import io.github.erkko68.filament.ToneMapper
import io.github.erkko68.filament.View
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.LocalFilamentView

/**
 * Configures bloom post-processing for the view.
 *
 * Bloom produces "glow" around bright areas of the scene.
 *
 * @param enabled    Whether bloom is enabled.
 * @param strength   Overall bloom intensity (0.0 to 1.0).
 * @param threshold  If true, only colors brighter than 1.0 will bloom.
 * @param quality    Bloom filter quality.
 * @param resolution Width in pixels of the largest mip in the bloom downsample chain. `0`
 *   lets Filament pick a default (~360 px), which often looks pixelated on high-DPI displays.
 *   Bump this to roughly half your render width for a smoother halo on retina/iOS screens.
 * @param levels     Number of mip levels in the bloom chain. Higher values give a wider, softer
 *   halo at slightly more GPU cost. Range 3..12.
 */
@Composable
fun Bloom(
    enabled: Boolean = true,
    strength: Float = 0.10f,
    threshold: Boolean = true,
    quality: View.Quality = View.Quality.LOW,
    resolution: Int = 0,
    levels: Int = 6,
) {
    val view = LocalFilamentView.current
    DisposableEffect(enabled, strength, threshold, quality, resolution, levels) {
        view.bloomOptions = view.bloomOptions.apply {
            this.enabled = enabled
            this.strength = strength
            this.threshold = threshold
            this.quality = quality
            this.resolution = resolution
            this.levels = levels
        }
        onDispose {
            view.bloomOptions = view.bloomOptions.apply { this.enabled = false }
        }
    }
}

/**
 * Configures vignette post-processing for the view.
 *
 * Vignetting darkens the corners of the viewport.
 */
@Composable
fun Vignette(
    enabled: Boolean = true,
    midPoint: Float = 0.5f,
    roundness: Float = 0.5f,
    feather: Float = 0.5f,
    color: Color = Color(0f, 0f, 0f),
) {
    val view = LocalFilamentView.current
    DisposableEffect(enabled, midPoint, roundness, feather, color) {
        view.vignetteOptions = view.vignetteOptions.apply {
            this.enabled = enabled
            this.midPoint = midPoint
            this.roundness = roundness
            this.feather = feather
            this.color = floatArrayOf(color.r, color.g, color.b, 1.0f)
        }
        onDispose {
            view.vignetteOptions = view.vignetteOptions.apply { this.enabled = false }
        }
    }
}

/**
 * Configures height-based and volumetric fog for the view.
 */
@Composable
fun Fog(
    enabled: Boolean = true,
    distance: Float = 0.0f,
    density: Float = 0.1f,
    height: Float = 0.0f,
    heightFalloff: Float = 1.0f,
    color: Color = Color(0f, 0f, 0f),
) {
    val view = LocalFilamentView.current
    DisposableEffect(enabled, distance, density, height, heightFalloff, color) {
        view.fogOptions = view.fogOptions.apply {
            this.enabled = enabled
            this.distance = distance
            this.density = density
            this.height = height
            this.heightFalloff = heightFalloff
            this.color = floatArrayOf(color.r, color.g, color.b)
        }
        onDispose {
            view.fogOptions = view.fogOptions.apply { this.enabled = false }
        }
    }
}

/**
 * Configures screen-space ambient occlusion (SSAO) for the view.
 */
@Composable
fun AmbientOcclusion(
    enabled: Boolean = true,
    radius: Float = 0.3f,
    bias: Float = 0.01f,
    intensity: Float = 1.0f,
    quality: View.Quality = View.Quality.LOW,
) {
    val view = LocalFilamentView.current
    DisposableEffect(enabled, radius, bias, intensity, quality) {
        view.ambientOcclusionOptions = view.ambientOcclusionOptions.apply {
            this.enabled = enabled
            this.radius = radius
            this.bias = bias
            this.intensity = intensity
            this.quality = quality
        }
        onDispose {
            view.ambientOcclusionOptions = view.ambientOcclusionOptions.apply { this.enabled = false }
        }
    }
}

/**
 * Configures anti-aliasing techniques for the view.
 *
 * This includes MSAA (hardware), FXAA (post-process), and TAA (temporal).
 */
@Composable
fun AntiAliasing(
    msaaEnabled: Boolean = false,
    msaaSampleCount: Int = 4,
    fxaaEnabled: Boolean = true,
    taaEnabled: Boolean = false,
) {
    val view = LocalFilamentView.current
    DisposableEffect(msaaEnabled, msaaSampleCount, fxaaEnabled, taaEnabled) {
        view.multiSampleAntiAliasingOptions = view.multiSampleAntiAliasingOptions.apply {
            this.enabled = msaaEnabled
            this.sampleCount = msaaSampleCount
        }
        view.antiAliasing = if (fxaaEnabled) View.AntiAliasing.FXAA else View.AntiAliasing.NONE
        view.temporalAntiAliasingOptions = view.temporalAntiAliasingOptions.apply {
            this.enabled = taaEnabled
        }
        onDispose {
            view.multiSampleAntiAliasingOptions = view.multiSampleAntiAliasingOptions.apply { this.enabled = false }
            view.antiAliasing = View.AntiAliasing.NONE
            view.temporalAntiAliasingOptions = view.temporalAntiAliasingOptions.apply { this.enabled = false }
        }
    }
}

/**
 * Configures screen-space reflections (SSR) for the view.
 */
@Composable
fun ScreenSpaceReflections(
    enabled: Boolean = true,
    thickness: Float = 0.1f,
    bias: Float = 0.01f,
    maxDistance: Float = 3.0f,
) {
    val view = LocalFilamentView.current
    DisposableEffect(enabled, thickness, bias, maxDistance) {
        view.screenSpaceReflectionsOptions = view.screenSpaceReflectionsOptions.apply {
            this.enabled = enabled
            this.thickness = thickness
            this.bias = bias
            this.maxDistance = maxDistance
        }
        onDispose {
            view.screenSpaceReflectionsOptions = view.screenSpaceReflectionsOptions.apply { this.enabled = false }
        }
    }
}

/**
 * Configures color grading for the view.
 *
 * Color grading allows for adjustments to exposure, white balance, contrast, etc.
 */
@Composable
fun ColorGrade(
    exposure: Float = 0.0f,
    contrast: Float = 1.0f,
    vibrance: Float = 1.0f,
    saturation: Float = 1.0f,
    whiteBalanceTemperature: Float = 0.0f,
    whiteBalanceTint: Float = 0.0f,
    toneMapper: ToneMapper = ToneMapper.ACES(),
) {
    val engine = LocalFilamentEngine.current
    val view = LocalFilamentView.current
    DisposableEffect(exposure, contrast, vibrance, saturation, whiteBalanceTemperature, whiteBalanceTint, toneMapper) {
        val colorGrading = ColorGrading.Builder()
            .exposure(exposure)
            .contrast(contrast)
            .vibrance(vibrance)
            .saturation(saturation)
            .whiteBalance(whiteBalanceTemperature, whiteBalanceTint)
            .toneMapper(toneMapper)
            .build(engine)
        view.colorGrading = colorGrading
        onDispose {
            view.colorGrading = null
            engine.destroyColorGrading(colorGrading)
        }
    }
}

/**
 * A shortcut to set the tone mapper for the view.
 *
 * This internally uses [ColorGrade].
 */
@Composable
fun ToneMapping(
    toneMapper: ToneMapper = ToneMapper.ACES(),
) {
    ColorGrade(toneMapper = toneMapper)
}

/**
 * Configures depth-of-field (bokeh) post-processing for the view.
 *
 * The camera's focus distance and aperture control the plane of focus and blur amount.
 *
 * @param enabled Whether depth-of-field is active.
 * @param cocScale Scales the circle-of-confusion radius. Larger = more blur.
 * @param maxApertureDiameter Maximum physical aperture diameter in metres.
 * @param filter Bokeh filter shape.
 * @param nativeResolution Run DoF at native resolution (higher quality, more expensive).
 */
@Composable
fun DepthOfField(
    enabled: Boolean = true,
    cocScale: Float = 1.0f,
    maxApertureDiameter: Float = 0.01f,
    filter: View.DepthOfFieldOptions.Filter = View.DepthOfFieldOptions.Filter.MEDIAN,
    nativeResolution: Boolean = false,
) {
    val view = LocalFilamentView.current
    DisposableEffect(enabled, cocScale, maxApertureDiameter, filter, nativeResolution) {
        view.depthOfFieldOptions = view.depthOfFieldOptions.apply {
            this.enabled = enabled
            this.cocScale = cocScale
            this.maxApertureDiameter = maxApertureDiameter
            this.filter = filter
            this.nativeResolution = nativeResolution
        }
        onDispose {
            view.depthOfFieldOptions = view.depthOfFieldOptions.apply { this.enabled = false }
        }
    }
}

/**
 * Configures shadow rendering for the view.
 *
 * [type] selects the shadow algorithm. VSM and soft-shadow params are only meaningful for
 * their respective types — Filament ignores irrelevant options.
 *
 * @param type          Shadow algorithm: PCF, VSM, DPCF, PCSS, or PCFd.
 * @param vsmAnisotropy VSM: anisotropy level (power of 2, e.g. 1, 2, 4).
 * @param vsmMipmapping VSM: enable mip-map filtering.
 * @param vsmMsaaSamples VSM: MSAA sample count for variance computation.
 * @param vsmHighPrecision VSM: use 32-bit float instead of 16-bit.
 * @param vsmLightBleedReduction VSM: light-bleed reduction factor (0–1).
 * @param penumbraScale DPCF/PCSS: scales penumbra size.
 * @param penumbraRatioScale DPCF/PCSS: penumbra ratio scale.
 */
@Composable
fun Shadows(
    type: View.ShadowType = View.ShadowType.PCF,
    vsmAnisotropy: Int = 1,
    vsmMipmapping: Boolean = false,
    vsmMsaaSamples: Int = 1,
    vsmHighPrecision: Boolean = false,
    vsmLightBleedReduction: Float = 0.0f,
    penumbraScale: Float = 1.0f,
    penumbraRatioScale: Float = 1.0f,
) {
    val view = LocalFilamentView.current
    DisposableEffect(type, vsmAnisotropy, vsmMipmapping, vsmMsaaSamples, vsmHighPrecision, vsmLightBleedReduction, penumbraScale, penumbraRatioScale) {
        view.shadowType = type
        view.vsmShadowOptions = view.vsmShadowOptions.apply {
            this.anisotropy = vsmAnisotropy
            this.mipmapping = vsmMipmapping
            this.msaaSamples = vsmMsaaSamples
            this.highPrecision = vsmHighPrecision
            this.lightBleedReduction = vsmLightBleedReduction
        }
        view.softShadowOptions = view.softShadowOptions.apply {
            this.penumbraScale = penumbraScale
            this.penumbraRatioScale = penumbraRatioScale
        }
        onDispose {
            view.shadowType = View.ShadowType.PCF
        }
    }
}

/**
 * Enables dynamic resolution scaling, allowing the renderer to lower internal resolution
 * under GPU load and upscale to the target size.
 *
 * @param enabled Whether dynamic resolution is active.
 * @param minScale Minimum scale factor (e.g. 0.5 = half resolution).
 * @param maxScale Maximum scale factor (1.0 = native).
 * @param sharpness Upscale sharpness (0–1).
 * @param quality Quality of the upscaling filter.
 * @param homogeneousScaling Lock X and Y scaling together.
 */
@Composable
fun DynamicResolution(
    enabled: Boolean = true,
    minScale: Float = 0.5f,
    maxScale: Float = 1.0f,
    sharpness: Float = 0.9f,
    quality: View.Quality = View.Quality.LOW,
    homogeneousScaling: Boolean = false,
) {
    val view = LocalFilamentView.current
    DisposableEffect(enabled, minScale, maxScale, sharpness, quality, homogeneousScaling) {
        view.dynamicResolutionOptions = view.dynamicResolutionOptions.apply {
            this.enabled = enabled
            this.minScale = minScale
            this.maxScale = maxScale
            this.sharpness = sharpness
            this.quality = quality
            this.homogeneousScaling = homogeneousScaling
        }
        onDispose {
            view.dynamicResolutionOptions = view.dynamicResolutionOptions.apply { this.enabled = false }
        }
    }
}

/**
 * Selects the dithering applied at tonemap time. [View.Dithering.TEMPORAL] (Filament's
 * native default) hides 8-bit banding in dark gradients and bloom halos by jittering the
 * quantised output between frames. Disable only if you measure a banding pattern that
 * outweighs the noise.
 */
@Composable
fun Dithering(mode: View.Dithering = View.Dithering.TEMPORAL) {
    val view = LocalFilamentView.current
    DisposableEffect(mode) {
        val previous = view.dithering
        view.dithering = mode
        onDispose { view.dithering = previous }
    }
}

/**
 * Selects the precision of the View's HDR color buffer. [View.Quality.HIGH] (the native
 * default) is RGBA16F where the backend supports it — necessary for emissive values
 * above 1.0 to survive without clipping into bloom. Drop to [View.Quality.MEDIUM] for
 * a smaller intermediate buffer when memory pressure outweighs banding.
 */
@Composable
fun RenderQuality(hdrColorBuffer: View.Quality = View.Quality.HIGH) {
    val view = LocalFilamentView.current
    DisposableEffect(hdrColorBuffer) {
        view.renderQuality = view.renderQuality.apply { this.hdrColorBuffer = hdrColorBuffer }
        onDispose { }
    }
}
