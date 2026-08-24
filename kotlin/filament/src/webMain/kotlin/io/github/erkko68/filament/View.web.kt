package io.github.erkko68.filament

import io.github.erkko68.filament.web.interop.toFloatArray

import io.github.erkko68.filament.web.interop.emptyJsObject

import io.github.erkko68.filament.web.interop.jsNumbers
import io.github.erkko68.filament.web.interop.toJsNumbers

import io.github.erkko68.filament.web.View as JSView

// The option externals type every field nullable because the .d.ts marks them optional,
// but a value_object coming back from the engine always carries all of them — the
// fallbacks below are unreachable and only exist to satisfy the type system.
private fun Double?.f(): Float = this?.toFloat() ?: 0f
private fun Double?.i(): Int = this?.toInt() ?: 0
private fun Boolean?.b(): Boolean = this == true

private fun io.github.erkko68.filament.web.View_QualityLevel?.toQuality(): View.Quality = when (this) {
    io.github.erkko68.filament.web.View_QualityLevel.LOW -> View.Quality.LOW
    io.github.erkko68.filament.web.View_QualityLevel.MEDIUM -> View.Quality.MEDIUM
    io.github.erkko68.filament.web.View_QualityLevel.ULTRA -> View.Quality.ULTRA
    else -> View.Quality.HIGH
}

private fun View.Quality.toJs(): io.github.erkko68.filament.web.View_QualityLevel = when (this) {
    View.Quality.LOW -> io.github.erkko68.filament.web.View_QualityLevel.LOW
    View.Quality.MEDIUM -> io.github.erkko68.filament.web.View_QualityLevel.MEDIUM
    View.Quality.HIGH -> io.github.erkko68.filament.web.View_QualityLevel.HIGH
    View.Quality.ULTRA -> io.github.erkko68.filament.web.View_QualityLevel.ULTRA
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual class View(internal val jsView: JSView) {
    // Wrapper identity, as on the other platforms: the engine hands back a raw JS/native
    // object, so the Kotlin wrapper the caller set is what the getter must return.
    private var _scene: Scene? = null
    private var _camera: Camera? = null
    private var _renderTarget: RenderTarget? = null
    private var _colorGrading: ColorGrading? = null

    actual var name: String?
        get() = jsView.getName().takeIf { it.isNotEmpty() }
        set(value) { jsView.setName(value ?: "") }

    actual var scene: Scene?
        get() = _scene
        set(value) {
            _scene = value
            if (value != null) jsView.setScene(value.jsScene)
        }

    actual var camera: Camera?
        get() = _camera
        set(value) {
            _camera = value
            if (value != null) jsView.setCamera(value.jsCamera)
        }

    actual val hasCamera: Boolean get() = jsView.hasCamera()

    actual var viewport: Viewport
        get() {
            val v = jsView.getViewport()?.toFloatArray(4) ?: return Viewport(0, 0, 0, 0)
            return Viewport(v[0].toInt(), v[1].toInt(), v[2].toInt(), v[3].toInt())
        }
        set(value) {
            jsView.setViewport(jsNumbers(value.left, value.bottom, value.width, value.height))
        }

    actual var blendMode: BlendMode
        get() = when (jsView.getBlendMode()) {
            io.github.erkko68.filament.web.View_BlendMode.OPAQUE -> View.BlendMode.OPAQUE
            io.github.erkko68.filament.web.View_BlendMode.TRANSLUCENT -> View.BlendMode.TRANSLUCENT
            else -> error("unreachable")
        }
        set(value) {
            jsView.setBlendMode(when (value) {
                View.BlendMode.OPAQUE -> io.github.erkko68.filament.web.View_BlendMode.OPAQUE
                View.BlendMode.TRANSLUCENT -> io.github.erkko68.filament.web.View_BlendMode.TRANSLUCENT
            })
        }

    actual fun setVisibleLayers(select: Int, values: Int) {
        jsView.setVisibleLayers(select.toDouble(), values.toDouble())
    }

    actual fun setLayerEnabled(layer: Int, enabled: Boolean) {
        jsView.setLayerEnabled(layer.toDouble(), enabled)
    }

    actual fun getVisibleLayers(): Int = jsView.getVisibleLayers().toInt()

    actual var isPostProcessingEnabled: Boolean
        get() = jsView.isPostProcessingEnabled()
        set(value) { jsView.setPostProcessingEnabled(value) }

    actual var antiAliasing: AntiAliasing
        get() = when (jsView.getAntiAliasing()) {
            io.github.erkko68.filament.web.View_AntiAliasing.FXAA -> AntiAliasing.FXAA
            else -> AntiAliasing.NONE
        }
        set(value) {
            jsView.setAntiAliasing(when (value) {
                AntiAliasing.NONE -> io.github.erkko68.filament.web.View_AntiAliasing.NONE
                AntiAliasing.FXAA -> io.github.erkko68.filament.web.View_AntiAliasing.FXAA
            })
        }

    actual var dithering: Dithering
        get() = when (jsView.getDithering()) {
            io.github.erkko68.filament.web.View_Dithering.NONE -> Dithering.NONE
            else -> Dithering.TEMPORAL
        }
        set(value) {
            val jsDith = when(value) {
                Dithering.NONE -> io.github.erkko68.filament.web.View_Dithering.NONE
                Dithering.TEMPORAL -> io.github.erkko68.filament.web.View_Dithering.TEMPORAL
            }
            jsView.setDithering(jsDith)
        }

    actual var dynamicResolutionOptions: DynamicResolutionOptions
        get() = jsView.getDynamicResolutionOptions().let { o ->
            DynamicResolutionOptions().apply {
                enabled = o.enabled.b()
                homogeneousScaling = o.homogeneousScaling.b()
                minScale = (o.minScale?.toFloatArray(2) ?: FloatArray(2))[0]
                maxScale = (o.maxScale?.toFloatArray(2) ?: FloatArray(2))[0]
                sharpness = o.sharpness.f()
                quality = o.quality.toQuality()
            }
        }
        set(value) {
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_DynamicResolutionOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.homogeneousScaling = value.homogeneousScaling
            jsOptions.minScale = jsNumbers(value.minScale, value.minScale)
            jsOptions.maxScale = jsNumbers(value.maxScale, value.maxScale)
            jsOptions.sharpness = value.sharpness.toDouble()
            jsOptions.quality = value.quality.toJs()
            jsView.setDynamicResolutionOptions(jsOptions)
        }

    actual var renderQuality: RenderQuality
        get() = RenderQuality().apply {
            hdrColorBuffer = jsView.getRenderQuality().hdrColorBuffer.toQuality()
        }
        set(value) {
            val jsQuality = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_RenderQuality`>()
            jsQuality.hdrColorBuffer = value.hdrColorBuffer.toJs()
            jsView.setRenderQuality(jsQuality)
        }

    // `dirt`/`dirtStrength` are not registered on the View$BloomOptions value_object
    // ("JavaScript binding for dirt is not yet supported"), so they cannot round-trip.
    actual var bloomOptions: BloomOptions
        get() = jsView.getBloomOptions().let { o ->
            BloomOptions().apply {
                enabled = o.enabled.b()
                levels = o.levels.i()
                resolution = o.resolution.i()
                strength = o.strength.f()
                threshold = o.threshold.b()
                quality = o.quality.toQuality()
                lensFlare = o.lensFlare.b()
                starburst = o.starburst.b()
                chromaticAberration = o.chromaticAberration.f()
                ghostCount = o.ghostCount.i()
                ghostSpacing = o.ghostSpacing.f()
                ghostThreshold = o.ghostThreshold.f()
                haloRadius = o.haloRadius.f()
                haloThickness = o.haloThickness.f()
                haloThreshold = o.haloThreshold.f()
                highlight = o.highlight.f()
                blendMode = when (o.blendMode) {
                    io.github.erkko68.filament.web.View_BloomOptions_BlendMode.INTERPOLATE ->
                        View.BloomOptions.BlendMode.INTERPOLATE
                    else -> View.BloomOptions.BlendMode.ADD
                }
            }
        }
        set(value) {
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_BloomOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.levels = value.levels.toDouble()
            jsOptions.resolution = value.resolution.toDouble()
            jsOptions.strength = value.strength.toDouble()
            jsOptions.threshold = value.threshold
            jsOptions.quality = value.quality.toJs()
            jsOptions.lensFlare = value.lensFlare
            jsOptions.starburst = value.starburst
            jsOptions.chromaticAberration = value.chromaticAberration.toDouble()
            jsOptions.ghostCount = value.ghostCount.toDouble()
            jsOptions.ghostSpacing = value.ghostSpacing.toDouble()
            jsOptions.ghostThreshold = value.ghostThreshold.toDouble()
            jsOptions.haloRadius = value.haloRadius.toDouble()
            jsOptions.haloThickness = value.haloThickness.toDouble()
            jsOptions.haloThreshold = value.haloThreshold.toDouble()
            jsOptions.highlight = value.highlight.toDouble()
            jsOptions.blendMode = when (value.blendMode) {
                View.BloomOptions.BlendMode.ADD -> io.github.erkko68.filament.web.View_BloomOptions_BlendMode.ADD
                View.BloomOptions.BlendMode.INTERPOLATE -> io.github.erkko68.filament.web.View_BloomOptions_BlendMode.INTERPOLATE
            }
            jsView.setBloomOptions(jsOptions)
        }

    // `skyColor` is not registered on the View$FogOptions value_object, so it cannot round-trip.
    actual var fogOptions: FogOptions
        get() = jsView.getFogOptions().let { o ->
            FogOptions().apply {
                enabled = o.enabled.b()
                distance = o.distance.f()
                density = o.density.f()
                height = o.height.f()
                heightFalloff = o.heightFalloff.f()
                color = o.color?.toFloatArray(3) ?: floatArrayOf(1f, 1f, 1f)
                cutOffDistance = o.cutOffDistance.f()
                maximumOpacity = o.maximumOpacity.f()
                inScatteringStart = o.inScatteringStart.f()
                inScatteringSize = o.inScatteringSize.f()
                fogColorFromIbl = o.fogColorFromIbl.b()
            }
        }
        set(value) {
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_FogOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.distance = value.distance.toDouble()
            jsOptions.density = value.density.toDouble()
            jsOptions.height = value.height.toDouble()
            jsOptions.heightFalloff = value.heightFalloff.toDouble()
            jsOptions.color = jsNumbers(value.color[0], value.color[1], value.color[2])
            jsOptions.cutOffDistance = value.cutOffDistance.toDouble()
            jsOptions.maximumOpacity = value.maximumOpacity.toDouble()
            jsOptions.inScatteringStart = value.inScatteringStart.toDouble()
            jsOptions.inScatteringSize = value.inScatteringSize.toDouble()
            jsOptions.fogColorFromIbl = value.fogColorFromIbl
            jsView.setFogOptions(jsOptions)
        }

    actual var depthOfFieldOptions: DepthOfFieldOptions
        get() = jsView.getDepthOfFieldOptions().let { o ->
            DepthOfFieldOptions().apply {
                enabled = o.enabled.b()
                cocScale = o.cocScale.f()
                maxApertureDiameter = o.maxApertureDiameter.f()
                nativeResolution = o.nativeResolution.b()
                foregroundRingCount = o.foregroundRingCount.i()
                backgroundRingCount = o.backgroundRingCount.i()
                fastGatherRingCount = o.fastGatherRingCount.i()
                maxForegroundCOC = o.maxForegroundCOC.i()
                maxBackgroundCOC = o.maxBackgroundCOC.i()
                filter = when (o.filter) {
                    io.github.erkko68.filament.web.View_DepthOfFieldOptions_Filter.NONE ->
                        View.DepthOfFieldOptions.Filter.NONE
                    io.github.erkko68.filament.web.View_DepthOfFieldOptions_Filter.UNUSED ->
                        View.DepthOfFieldOptions.Filter.UNUSED
                    else -> View.DepthOfFieldOptions.Filter.MEDIAN
                }
            }
        }
        set(value) {
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_DepthOfFieldOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.cocScale = value.cocScale.toDouble()
            jsOptions.cocAspectRatio = value.cocAspectRatio.toDouble()
            jsOptions.maxApertureDiameter = value.maxApertureDiameter.toDouble()
            jsOptions.nativeResolution = value.nativeResolution
            jsOptions.foregroundRingCount = value.foregroundRingCount.toDouble()
            jsOptions.backgroundRingCount = value.backgroundRingCount.toDouble()
            jsOptions.fastGatherRingCount = value.fastGatherRingCount.toDouble()
            jsOptions.maxForegroundCOC = value.maxForegroundCOC.toDouble()
            jsOptions.maxBackgroundCOC = value.maxBackgroundCOC.toDouble()
            jsOptions.filter = when (value.filter) {
                View.DepthOfFieldOptions.Filter.NONE   -> io.github.erkko68.filament.web.View_DepthOfFieldOptions_Filter.NONE
                View.DepthOfFieldOptions.Filter.UNUSED -> io.github.erkko68.filament.web.View_DepthOfFieldOptions_Filter.UNUSED
                View.DepthOfFieldOptions.Filter.MEDIAN -> io.github.erkko68.filament.web.View_DepthOfFieldOptions_Filter.MEDIAN
            }
            jsView.setDepthOfFieldOptions(jsOptions)
        }

    actual var vignetteOptions: VignetteOptions
        get() = jsView.getVignetteOptions().let { o ->
            VignetteOptions().apply {
                enabled = o.enabled.b()
                midPoint = o.midPoint.f()
                roundness = o.roundness.f()
                feather = o.feather.f()
                color = o.color?.toFloatArray(4) ?: floatArrayOf(0f, 0f, 0f, 1f)
            }
        }
        set(value) {
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_VignetteOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.midPoint = value.midPoint.toDouble()
            jsOptions.roundness = value.roundness.toDouble()
            jsOptions.feather = value.feather.toDouble()
            jsOptions.color = jsNumbers(value.color[0], value.color[1], value.color[2], value.color[3])
            jsView.setVignetteOptions(jsOptions)
        }

    // `ssct` (and `gtao`) are not registered on the View$AmbientOcclusionOptions
    // value_object, so those sub-structs cannot be pushed to or read back from the engine.
    actual var ambientOcclusionOptions: AmbientOcclusionOptions
        get() = jsView.getAmbientOcclusionOptions().let { o ->
            AmbientOcclusionOptions().apply {
                enabled = o.enabled.b()
                aoType = when (o.aoType) {
                    io.github.erkko68.filament.web.View_AmbientOcclusionOptions_AmbientOcclusionType.GTAO ->
                        AmbientOcclusionOptions.AmbientOcclusionType.GTAO
                    else -> AmbientOcclusionOptions.AmbientOcclusionType.SAO
                }
                radius = o.radius.f()
                bias = o.bias.f()
                intensity = o.intensity.f()
                power = o.power.f()
                resolution = o.resolution.f()
                bilateralThreshold = o.bilateralThreshold.f()
                minHorizonAngleRad = o.minHorizonAngleRad.f()
                bentNormals = o.bentNormals.b()
                quality = o.quality.toQuality()
                lowPassFilter = o.lowPassFilter.toQuality()
                upsampling = o.upsampling.toQuality()
            }
        }
        set(value) {
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_AmbientOcclusionOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.aoType = when (value.aoType) {
                AmbientOcclusionOptions.AmbientOcclusionType.SAO -> io.github.erkko68.filament.web.View_AmbientOcclusionOptions_AmbientOcclusionType.SAO
                AmbientOcclusionOptions.AmbientOcclusionType.GTAO -> io.github.erkko68.filament.web.View_AmbientOcclusionOptions_AmbientOcclusionType.GTAO
            }
            jsOptions.radius = value.radius.toDouble()
            jsOptions.bias = value.bias.toDouble()
            jsOptions.intensity = value.intensity.toDouble()
            jsOptions.power = value.power.toDouble()
            jsOptions.resolution = value.resolution.toDouble()
            jsOptions.bilateralThreshold = value.bilateralThreshold.toDouble()
            jsOptions.minHorizonAngleRad = value.minHorizonAngleRad.toDouble()
            jsOptions.bentNormals = value.bentNormals
            jsOptions.quality = value.quality.toJs()
            jsOptions.lowPassFilter = value.lowPassFilter.toJs()
            jsOptions.upsampling = value.upsampling.toJs()
            jsView.setAmbientOcclusionOptions(jsOptions)
        }

    actual var temporalAntiAliasingOptions: TemporalAntiAliasingOptions
        get() = jsView.getTemporalAntiAliasingOptions().let { o ->
            TemporalAntiAliasingOptions().apply {
                enabled = o.enabled.b()
                feedback = o.feedback.f()
                lodBias = o.lodBias.f()
                sharpness = o.sharpness.f()
                upscaling = o.upscaling.f()
                filterHistory = o.filterHistory.b()
                filterInput = o.filterInput.b()
                useYCoCg = o.useYCoCg.b()
                hdr = o.hdr.b()
                varianceGamma = o.varianceGamma.f()
                preventFlickering = o.preventFlickering.b()
                historyReprojection = o.historyReprojection.b()
                boxType = when (o.boxType) {
                    io.github.erkko68.filament.web.View_TemporalAntiAliasingOptions_BoxType.AABB_VARIANCE ->
                        TemporalAntiAliasingOptions.BoxType.AABB_VARIANCE
                    else -> TemporalAntiAliasingOptions.BoxType.AABB
                }
                boxClipping = when (o.boxClipping) {
                    io.github.erkko68.filament.web.View_TemporalAntiAliasingOptions_BoxClipping.CLAMP ->
                        TemporalAntiAliasingOptions.BoxClipping.CLAMP
                    io.github.erkko68.filament.web.View_TemporalAntiAliasingOptions_BoxClipping.NONE ->
                        TemporalAntiAliasingOptions.BoxClipping.NONE
                    else -> TemporalAntiAliasingOptions.BoxClipping.ACCURATE
                }
                jitterPattern = when (o.jitterPattern) {
                    io.github.erkko68.filament.web.View_TemporalAntiAliasingOptions_JitterPattern.RGSS_X4 ->
                        TemporalAntiAliasingOptions.JitterPattern.RGSS_X4
                    io.github.erkko68.filament.web.View_TemporalAntiAliasingOptions_JitterPattern.UNIFORM_HELIX_X4 ->
                        TemporalAntiAliasingOptions.JitterPattern.UNIFORM_HELIX_X4
                    io.github.erkko68.filament.web.View_TemporalAntiAliasingOptions_JitterPattern.HALTON_23_X8 ->
                        TemporalAntiAliasingOptions.JitterPattern.HALTON_23_X8
                    io.github.erkko68.filament.web.View_TemporalAntiAliasingOptions_JitterPattern.HALTON_23_X32 ->
                        TemporalAntiAliasingOptions.JitterPattern.HALTON_23_X32
                    else -> TemporalAntiAliasingOptions.JitterPattern.HALTON_23_X16
                }
            }
        }
        set(value) {
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_TemporalAntiAliasingOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.feedback = value.feedback.toDouble()
            jsOptions.lodBias = value.lodBias.toDouble()
            jsOptions.sharpness = value.sharpness.toDouble()
            jsOptions.upscaling = value.upscaling.toDouble()
            jsOptions.filterHistory = value.filterHistory
            jsOptions.filterInput = value.filterInput
            jsOptions.useYCoCg = value.useYCoCg
            jsOptions.hdr = value.hdr
            jsOptions.boxType = when (value.boxType) {
                TemporalAntiAliasingOptions.BoxType.AABB -> io.github.erkko68.filament.web.View_TemporalAntiAliasingOptions_BoxType.AABB
                TemporalAntiAliasingOptions.BoxType.AABB_VARIANCE -> io.github.erkko68.filament.web.View_TemporalAntiAliasingOptions_BoxType.AABB_VARIANCE
            }
            jsOptions.boxClipping = when (value.boxClipping) {
                TemporalAntiAliasingOptions.BoxClipping.ACCURATE -> io.github.erkko68.filament.web.View_TemporalAntiAliasingOptions_BoxClipping.ACCURATE
                TemporalAntiAliasingOptions.BoxClipping.CLAMP -> io.github.erkko68.filament.web.View_TemporalAntiAliasingOptions_BoxClipping.CLAMP
                TemporalAntiAliasingOptions.BoxClipping.NONE -> io.github.erkko68.filament.web.View_TemporalAntiAliasingOptions_BoxClipping.NONE
            }
            jsOptions.jitterPattern = when (value.jitterPattern) {
                TemporalAntiAliasingOptions.JitterPattern.RGSS_X4 -> io.github.erkko68.filament.web.View_TemporalAntiAliasingOptions_JitterPattern.RGSS_X4
                TemporalAntiAliasingOptions.JitterPattern.UNIFORM_HELIX_X4 -> io.github.erkko68.filament.web.View_TemporalAntiAliasingOptions_JitterPattern.UNIFORM_HELIX_X4
                TemporalAntiAliasingOptions.JitterPattern.HALTON_23_X8 -> io.github.erkko68.filament.web.View_TemporalAntiAliasingOptions_JitterPattern.HALTON_23_X8
                TemporalAntiAliasingOptions.JitterPattern.HALTON_23_X16 -> io.github.erkko68.filament.web.View_TemporalAntiAliasingOptions_JitterPattern.HALTON_23_X16
                TemporalAntiAliasingOptions.JitterPattern.HALTON_23_X32 -> io.github.erkko68.filament.web.View_TemporalAntiAliasingOptions_JitterPattern.HALTON_23_X32
            }
            jsOptions.varianceGamma = value.varianceGamma.toDouble()
            jsOptions.preventFlickering = value.preventFlickering
            jsOptions.historyReprojection = value.historyReprojection
            jsView.setTemporalAntiAliasingOptions(jsOptions)
        }

    actual var screenSpaceReflectionsOptions: ScreenSpaceReflectionsOptions
        get() = jsView.getScreenSpaceReflectionsOptions().let { o ->
            ScreenSpaceReflectionsOptions().apply {
                enabled = o.enabled.b()
                thickness = o.thickness.f()
                bias = o.bias.f()
                maxDistance = o.maxDistance.f()
                stride = o.stride.f()
            }
        }
        set(value) {
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_ScreenSpaceReflectionsOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.thickness = value.thickness.toDouble()
            jsOptions.bias = value.bias.toDouble()
            jsOptions.maxDistance = value.maxDistance.toDouble()
            jsOptions.stride = value.stride.toDouble()
            jsView.setScreenSpaceReflectionsOptions(jsOptions)
        }

    actual var gridSize: Double
        get() = jsView.getGridSize()
        set(value) { jsView.setGridSize(value) }

    actual val effectiveGridSize: Double
        get() = jsView.getEffectiveGridSize()

    actual var renderTarget: RenderTarget?
        get() = _renderTarget
        set(value) {
            _renderTarget = value
            if (value != null) jsView.setRenderTarget(value.jsRenderTarget)
        }

    actual var shadowType: ShadowType
        get() = when (jsView.getShadowType()) {
            io.github.erkko68.filament.web.View_ShadowType.VSM -> ShadowType.VSM
            io.github.erkko68.filament.web.View_ShadowType.DPCF -> ShadowType.DPCF
            io.github.erkko68.filament.web.View_ShadowType.PCSS -> ShadowType.PCSS
            io.github.erkko68.filament.web.View_ShadowType.PCFd -> ShadowType.PCFd
            else -> ShadowType.PCF
        }
        set(value) {
            val jsType = when(value) {
                ShadowType.PCF -> io.github.erkko68.filament.web.View_ShadowType.PCF
                ShadowType.VSM -> io.github.erkko68.filament.web.View_ShadowType.VSM
                ShadowType.DPCF -> io.github.erkko68.filament.web.View_ShadowType.DPCF
                ShadowType.PCSS -> io.github.erkko68.filament.web.View_ShadowType.PCSS
                ShadowType.PCFd -> io.github.erkko68.filament.web.View_ShadowType.PCFd
            }
            jsView.setShadowType(jsType)
        }

    actual var vsmShadowOptions: VsmShadowOptions
        get() = jsView.getVsmShadowOptions().let { o ->
            VsmShadowOptions().apply {
                anisotropy = o.anisotropy.i()
                mipmapping = o.mipmapping.b()
                msaaSamples = o.msaaSamples.i()
                highPrecision = o.highPrecision.b()
                lightBleedReduction = o.lightBleedReduction.f()
            }
        }
        set(value) {
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_VsmShadowOptions`>()
            jsOptions.anisotropy = value.anisotropy.toDouble()
            jsOptions.mipmapping = value.mipmapping
            jsOptions.msaaSamples = value.msaaSamples.toDouble()
            jsOptions.highPrecision = value.highPrecision
            jsOptions.lightBleedReduction = value.lightBleedReduction.toDouble()
            jsView.setVsmShadowOptions(jsOptions)
        }

    actual var softShadowOptions: SoftShadowOptions
        get() = jsView.getSoftShadowOptions().let { o ->
            SoftShadowOptions().apply {
                penumbraScale = o.penumbraScale.f()
                penumbraRatioScale = o.penumbraRatioScale.f()
                maxPenumbraRatio = o.maxPenumbraRatio.f()
                maxSearchRadius = o.maxSearchRadius.f()
            }
        }
        set(value) {
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_SoftShadowOptions`>()
            jsOptions.penumbraScale = value.penumbraScale.toDouble()
            jsOptions.penumbraRatioScale = value.penumbraRatioScale.toDouble()
            jsOptions.maxPenumbraRatio = value.maxPenumbraRatio.toDouble()
            jsOptions.maxSearchRadius = value.maxSearchRadius.toDouble()
            jsView.setSoftShadowOptions(jsOptions)
        }

    actual var guardBandOptions: GuardBandOptions
        get() = GuardBandOptions().apply { enabled = jsView.getGuardBandOptions().enabled.b() }
        set(value) {
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_GuardBandOptions`>()
            jsOptions.enabled = value.enabled
            jsView.setGuardBandOptions(jsOptions)
        }

    actual var stereoscopicOptions: StereoscopicOptions
        get() = StereoscopicOptions().apply { enabled = jsView.getStereoscopicOptions().enabled.b() }
        set(value) {
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_StereoscopicOptions`>()
            jsOptions.enabled = value.enabled
            jsView.setStereoscopicOptions(jsOptions)
        }

    actual var multiSampleAntiAliasingOptions: MultiSampleAntiAliasingOptions
        get() = jsView.getMultiSampleAntiAliasingOptions().let { o ->
            MultiSampleAntiAliasingOptions().apply {
                enabled = o.enabled.b()
                sampleCount = o.sampleCount.i()
                customResolve = o.customResolve.b()
            }
        }
        set(value) {
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_MultiSampleAntiAliasingOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.sampleCount = value.sampleCount.toDouble()
            jsOptions.customResolve = value.customResolve
            jsView.setMultiSampleAntiAliasingOptions(jsOptions)
        }

    actual var isFrustumCullingEnabled: Boolean
        get() = jsView.isFrustumCullingEnabled()
        set(value) { jsView.setFrustumCullingEnabled(value) }
    actual var isShadowingEnabled: Boolean
        get() = jsView.isShadowingEnabled()
        set(value) { jsView.setShadowingEnabled(value) }
    actual var isScreenSpaceRefractionEnabled: Boolean
        get() = jsView.isScreenSpaceRefractionEnabled()
        set(value) { jsView.setScreenSpaceRefractionEnabled(value) }

    actual var isStencilBufferEnabled: Boolean
        get() = jsView.isStencilBufferEnabled()
        set(value) { jsView.setStencilBufferEnabled(value) }

    actual var isFrontFaceWindingInverted: Boolean
        get() = jsView.isFrontFaceWindingInverted()
        set(value) { jsView.setFrontFaceWindingInverted(value) }

    actual var isTransparentPickingEnabled: Boolean
        get() = jsView.isTransparentPickingEnabled()
        set(value) { jsView.setTransparentPickingEnabled(value) }

    actual fun setMaterialGlobal(index: Int, value: FloatArray) {
        require(value.size == 4) { "setMaterialGlobal expects a float4; got size ${value.size}" }
        @Suppress("UNCHECKED_CAST")
        jsView.setMaterialGlobal(index.toDouble(), value.toJsNumbers())
    }

    actual fun getMaterialGlobal(index: Int): FloatArray {
        return jsView.getMaterialGlobal(index.toDouble())?.toFloatArray(4) ?: FloatArray(4)
    }

    actual val fogEntity: Int
        get() = jsView.getFogEntity().getId().toInt()

    actual fun getVisibleRenderableCount(): Int = jsView.getVisibleRenderableCount().toInt()

    actual fun clearFrameHistory(engine: Engine) {
        jsView.clearFrameHistory(engine.jsEngine)
    }

    actual fun setDynamicLightingOptions(zNear: Float, zFar: Float) {
        jsView.setDynamicLightingOptions(zNear.toDouble(), zFar.toDouble())
    }

    // Cached for wrapper identity like the other platforms; the engine-side getter still
    // backs the case where the view was given a ColorGrading outside this wrapper.
    actual var colorGrading: ColorGrading?
        get() = _colorGrading ?: jsView.getColorGrading()?.let { ColorGrading(it) }
        set(value) {
            _colorGrading = value
            if (value != null) jsView.setColorGrading(value.jsColorGrading)
        }

    actual fun getLastDynamicResolutionScale(): FloatArray =
        jsView.getLastDynamicResolutionScale()?.toFloatArray(2) ?: floatArrayOf(1.0f, 1.0f)

    actual fun pick(x: Int, y: Int, callback: (PickingQueryResult) -> Unit) {
        jsView.pick(x.toDouble(), y.toDouble()) { result ->
            callback(PickingQueryResult(
                result.renderable.getId().toInt(),
                result.depth.toFloat(),
                (result.fragCoords?.toFloatArray(3) ?: FloatArray(3))
            ))
        }
    }

    actual class PickingQueryResult actual constructor(
        actual val renderable: Int,
        actual val depth: Float,
        actual val fragCoords: FloatArray
    )

    actual enum class Dithering { NONE, TEMPORAL }
    actual enum class AntiAliasing { NONE, FXAA }
    actual enum class BlendMode { OPAQUE, TRANSLUCENT }
    actual enum class Quality { LOW, MEDIUM, HIGH, ULTRA }
    actual enum class ShadowType { PCF, VSM, DPCF, PCSS, PCFd }
    actual class DynamicResolutionOptions {
        actual var enabled: Boolean = false
        actual var homogeneousScaling: Boolean = false
        actual var minScale: Float = 0.5f
        actual var maxScale: Float = 1.0f
        actual var sharpness: Float = 0.9f
        actual var quality: Quality = Quality.LOW
    }
    actual class RenderQuality {
        actual var hdrColorBuffer: Quality = Quality.HIGH
    }
    actual class BloomOptions {
        actual var enabled: Boolean = false
        actual var levels: Int = 6
        actual var resolution: Int = 384
        actual var strength: Float = 0.10f
        actual var threshold: Boolean = true
        actual var dirt: Texture? = null
        actual var dirtStrength: Float = 0.20f
        actual var quality: Quality = Quality.LOW
        actual var lensFlare: Boolean = false
        actual var starburst: Boolean = true
        actual var chromaticAberration: Float = 0.005f
        actual var ghostCount: Int = 4
        actual var ghostSpacing: Float = 0.6f
        actual var ghostThreshold: Float = 10.0f
        actual var haloRadius: Float = 0.4f
        actual var haloThickness: Float = 0.1f
        actual var haloThreshold: Float = 10.0f
        actual var highlight: Float = 1000.0f
        actual var blendMode: BlendMode = BlendMode.ADD
        actual enum class BlendMode { ADD, INTERPOLATE }
    }
    actual class FogOptions {
        actual var enabled: Boolean = false
        actual var distance: Float = 0.0f
        actual var density: Float = 0.1f
        actual var height: Float = 0.0f
        actual var heightFalloff: Float = 1.0f
        actual var color: FloatArray = floatArrayOf(1.0f, 1.0f, 1.0f)
        actual var cutOffDistance: Float = Float.POSITIVE_INFINITY
        actual var maximumOpacity: Float = 1.0f
        actual var inScatteringStart: Float = 0.0f
        actual var inScatteringSize: Float = -1.0f
        actual var fogColorFromIbl: Boolean = false
        actual var skyColor: Texture? = null
    }
    actual class DepthOfFieldOptions {
        actual var enabled: Boolean = false
        actual var cocScale: Float = 1.0f
        @PlatformGap(platforms = [FilamentPlatform.ANDROID], behavior = "tracked locally only — upstream's nSetDepthOfFieldOptions does not marshal cocAspectRatio, so the engine keeps 1.0; the getter still reports what you set.")
        actual var cocAspectRatio: Float = 1.0f
        actual var maxApertureDiameter: Float = 0.01f
        actual var filter: Filter = Filter.MEDIAN
        actual var nativeResolution: Boolean = false
        actual var foregroundRingCount: Int = 0
        actual var backgroundRingCount: Int = 0
        actual var fastGatherRingCount: Int = 0
        actual var maxForegroundCOC: Int = 0
        actual var maxBackgroundCOC: Int = 0
        actual enum class Filter { NONE, UNUSED, MEDIAN }
    }
    actual class VignetteOptions {
        actual var enabled: Boolean = false
        actual var midPoint: Float = 0.5f
        actual var roundness: Float = 0.5f
        actual var feather: Float = 0.5f
        actual var color: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
    }
    actual class AmbientOcclusionOptions {
        actual enum class AmbientOcclusionType { SAO, GTAO }
        actual var aoType: AmbientOcclusionType = AmbientOcclusionType.SAO
        actual var radius: Float = 0.3f
        actual var bias: Float = 0.0005f
        actual var intensity: Float = 1.0f
        actual var power: Float = 1.0f
        actual var minHorizonAngleRad: Float = 0.0f
        actual var quality: Quality = Quality.LOW
        actual var lowPassFilter: Quality = Quality.MEDIUM
        actual var upsampling: Quality = Quality.LOW
        actual var enabled: Boolean = false
        actual var bentNormals: Boolean = false
        actual var bilateralThreshold: Float = 0.05f
        actual var resolution: Float = 0.5f
        actual var ssct: Ssct = Ssct()
        @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "tracked locally only — Options.h marks the gtao struct %codegen_skip_javascript%, so filament.js has no binding and the engine keeps its GTAO defaults.")
        actual var gtao: Gtao = Gtao()
        actual class Ssct {
            actual var enabled: Boolean = false
            actual var lightConeRad: Float = 1.0f
            actual var shadowDistance: Float = 0.3f
            actual var contactDistanceMax: Float = 1.0f
            actual var intensity: Float = 0.8f
            actual var lightDirection: FloatArray = floatArrayOf(0.0f, -1.0f, 0.0f)
            actual var depthBias: Float = 0.01f
            actual var depthSlopeBias: Float = 0.01f
            actual var sampleCount: Int = 4
            actual var rayCount: Int = 1
        }
        // TODO(web-api-parity): filament.js says outright "JavaScript binding for gtao is not yet
        // supported, must use default value" — the nested struct is %codegen_skip_javascript%, so
        // these are tracked locally and the engine keeps its GTAO defaults.
        actual class Gtao {
            actual var sampleSliceCount: Int = 4
            actual var sampleStepsPerSlice: Int = 3
            actual var thicknessHeuristic: Float = 0.004f
            actual var useVisibilityBitmasks: Boolean = false
            actual var constThickness: Float = 0.5f
            actual var linearThickness: Boolean = false
        }
    }
    actual class TemporalAntiAliasingOptions {
        actual enum class BoxType { AABB, AABB_VARIANCE }
        actual enum class BoxClipping { ACCURATE, CLAMP, NONE }
        actual enum class JitterPattern { RGSS_X4, UNIFORM_HELIX_X4, HALTON_23_X8, HALTON_23_X16, HALTON_23_X32 }
        actual var feedback: Float = 0.12f
        actual var lodBias: Float = -1.0f
        actual var sharpness: Float = 0.0f
        actual var enabled: Boolean = false
        actual var upscaling: Float = 1.0f
        actual var filterHistory: Boolean = true
        actual var filterInput: Boolean = true
        actual var useYCoCg: Boolean = false
        actual var hdr: Boolean = true
        actual var boxType: BoxType = BoxType.AABB
        actual var boxClipping: BoxClipping = BoxClipping.ACCURATE
        actual var jitterPattern: JitterPattern = JitterPattern.HALTON_23_X16
        actual var varianceGamma: Float = 1.0f
        actual var preventFlickering: Boolean = false
        actual var historyReprojection: Boolean = true
    }
    actual class ScreenSpaceReflectionsOptions {
        actual var enabled: Boolean = false
        actual var thickness: Float = 0.1f
        actual var bias: Float = 0.01f
        actual var maxDistance: Float = 3.0f
        actual var stride: Float = 2.0f
    }
    actual class VsmShadowOptions {
        actual var anisotropy: Int = 0
        actual var mipmapping: Boolean = false
        actual var msaaSamples: Int = 1
        actual var highPrecision: Boolean = false
        actual var lightBleedReduction: Float = 0.15f
    }
    actual class SoftShadowOptions {
        actual var penumbraScale: Float = 1.0f
        actual var penumbraRatioScale: Float = 1.0f
        actual var maxPenumbraRatio: Float = 10.0f
        actual var maxSearchRadius: Float = 1.0f
    }
    actual class GuardBandOptions {
        actual var enabled: Boolean = false
    }
    actual class StereoscopicOptions {
        actual var enabled: Boolean = false
    }
    actual class MultiSampleAntiAliasingOptions {
        actual var enabled: Boolean = false
        actual var sampleCount: Int = 4
        actual var customResolve: Boolean = false
    }
}