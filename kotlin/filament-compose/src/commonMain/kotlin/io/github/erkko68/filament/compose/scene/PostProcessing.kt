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
 * @param enabled   Whether bloom is enabled.
 * @param strength  Overall bloom intensity (0.0 to 1.0).
 * @param threshold If true, only colors brighter than 1.0 will bloom.
 * @param quality   Bloom filter quality.
 */
@Composable
fun Bloom(
    enabled: Boolean = true,
    strength: Float = 0.10f,
    threshold: Boolean = true,
    quality: View.Quality = View.Quality.LOW,
) {
    val view = LocalFilamentView.current
    DisposableEffect(enabled, strength, threshold, quality) {
        val options = view.getBloomOptions().apply {
            this.enabled = enabled
            this.strength = strength
            this.threshold = threshold
            this.quality = quality
        }
        view.setBloomOptions(options)
        onDispose {
            options.enabled = false
            view.setBloomOptions(options)
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
        val options = view.getVignetteOptions().apply {
            this.enabled = enabled
            this.midPoint = midPoint
            this.roundness = roundness
            this.feather = feather
            this.color = floatArrayOf(color.r, color.g, color.b, 1.0f)
        }
        view.setVignetteOptions(options)
        onDispose {
            options.enabled = false
            view.setVignetteOptions(options)
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
        val options = view.getFogOptions().apply {
            this.enabled = enabled
            this.distance = distance
            this.density = density
            this.height = height
            this.heightFalloff = heightFalloff
            this.color = floatArrayOf(color.r, color.g, color.b)
        }
        view.setFogOptions(options)
        onDispose {
            options.enabled = false
            view.setFogOptions(options)
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
        val options = view.getAmbientOcclusionOptions().apply {
            this.enabled = enabled
            this.radius = radius
            this.bias = bias
            this.intensity = intensity
            this.quality = quality
        }
        view.setAmbientOcclusionOptions(options)
        onDispose {
            options.enabled = false
            view.setAmbientOcclusionOptions(options)
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
        val msaaOptions = view.getMultiSampleAntiAliasingOptions().apply {
            this.enabled = msaaEnabled
            this.sampleCount = msaaSampleCount
        }
        view.setMultiSampleAntiAliasingOptions(msaaOptions)

        view.setAntiAliasing(if (fxaaEnabled) View.AntiAliasing.FXAA else View.AntiAliasing.NONE)

        val taaOptions = view.getTemporalAntiAliasingOptions().apply {
            this.enabled = taaEnabled
        }
        view.setTemporalAntiAliasingOptions(taaOptions)

        onDispose {
            msaaOptions.enabled = false
            view.setMultiSampleAntiAliasingOptions(msaaOptions)
            view.setAntiAliasing(View.AntiAliasing.NONE)
            taaOptions.enabled = false
            view.setTemporalAntiAliasingOptions(taaOptions)
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
        val options = view.getScreenSpaceReflectionsOptions().apply {
            this.enabled = enabled
            this.thickness = thickness
            this.bias = bias
            this.maxDistance = maxDistance
        }
        view.setScreenSpaceReflectionsOptions(options)
        onDispose {
            options.enabled = false
            view.setScreenSpaceReflectionsOptions(options)
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
        view.setColorGrading(colorGrading)
        onDispose {
            view.setColorGrading(null)
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
        val options = view.getDepthOfFieldOptions().apply {
            this.enabled = enabled
            this.cocScale = cocScale
            this.maxApertureDiameter = maxApertureDiameter
            this.filter = filter
            this.nativeResolution = nativeResolution
        }
        view.setDepthOfFieldOptions(options)
        onDispose {
            options.enabled = false
            view.setDepthOfFieldOptions(options)
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
        view.setShadowType(type)
        val vsmOptions = view.getVsmShadowOptions().apply {
            this.anisotropy = vsmAnisotropy
            this.mipmapping = vsmMipmapping
            this.msaaSamples = vsmMsaaSamples
            this.highPrecision = vsmHighPrecision
            this.lightBleedReduction = vsmLightBleedReduction
        }
        view.setVsmShadowOptions(vsmOptions)
        val softOptions = view.getSoftShadowOptions().apply {
            this.penumbraScale = penumbraScale
            this.penumbraRatioScale = penumbraRatioScale
        }
        view.setSoftShadowOptions(softOptions)
        onDispose {
            view.setShadowType(View.ShadowType.PCF)
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
        val options = view.getDynamicResolutionOptions().apply {
            this.enabled = enabled
            this.minScale = minScale
            this.maxScale = maxScale
            this.sharpness = sharpness
            this.quality = quality
            this.homogeneousScaling = homogeneousScaling
        }
        view.setDynamicResolutionOptions(options)
        onDispose {
            options.enabled = false
            view.setDynamicResolutionOptions(options)
        }
    }
}
