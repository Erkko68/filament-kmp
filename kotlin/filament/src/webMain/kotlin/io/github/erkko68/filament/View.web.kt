package io.github.erkko68.filament

import io.github.erkko68.filament.web.interop.toFloatArray

import io.github.erkko68.filament.web.interop.emptyJsObject

import io.github.erkko68.filament.web.interop.jsNumbers
import io.github.erkko68.filament.web.interop.toJsNumbers

import io.github.erkko68.filament.web.View as JSView

// Value-object fields not emitted into the generated option externals.
private external interface BloomOptionsExt : JsAny  {
    var highlight: Double
    var dirt: io.github.erkko68.filament.web.Texture?
    var dirtStrength: Double
}
private external interface AoOptionsExt : JsAny  {
    var ssct: io.github.erkko68.filament.web.View_AmbientOcclusionOptions_Ssct
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual class View(internal val jsView: JSView) {
    private var _scene: Scene? = null
    private var _camera: Camera? = null
    private var _viewport: Viewport = Viewport(0, 0, 0, 0)
    private var _visibleLayersValues: Int = 0
    private var _dithering: Dithering = Dithering.TEMPORAL
    private var _antiAliasing: AntiAliasing = AntiAliasing.NONE
    private var _dynamicResolutionOptions = DynamicResolutionOptions()
    private var _renderQuality = RenderQuality()
    private var _bloomOptions = BloomOptions()
    private var _fogOptions = FogOptions()
    private var _depthOfFieldOptions = DepthOfFieldOptions()
    private var _vignetteOptions = VignetteOptions()
    private var _ambientOcclusionOptions = AmbientOcclusionOptions()
    private var _temporalAntiAliasingOptions = TemporalAntiAliasingOptions()
    private var _screenSpaceReflectionsOptions = ScreenSpaceReflectionsOptions()
    private var _vsmShadowOptions = VsmShadowOptions()
    private var _softShadowOptions = SoftShadowOptions()
    private var _guardBandOptions = GuardBandOptions()
    private var _stereoscopicOptions = StereoscopicOptions()
    private var _multiSampleAntiAliasingOptions = MultiSampleAntiAliasingOptions()
    private var _shadowType = ShadowType.PCF
    private var _renderTarget: RenderTarget? = null
    private var _postProcessingEnabled = true

    // View::getName/setName aren't bound in upstream jsbindings.cpp (v1.71.4) —
    // track locally so the common getter/setter round-trip works.
    private var _name: String? = null
    actual var name: String?
        get() = _name
        set(value) { _name = value }

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

    actual val hasCamera: Boolean get() = _camera != null

    actual var viewport: Viewport
        get() = _viewport
        set(value) {
            _viewport = value
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
        _visibleLayersValues = values
        jsView.setVisibleLayers(select.toDouble(), values.toDouble())
    }

    actual fun setLayerEnabled(layer: Int, enabled: Boolean) {
        val select = 1 shl layer
        val values = if (enabled) select else 0
        setVisibleLayers(select, values)
    }

    actual fun getVisibleLayers(): Int = _visibleLayersValues

    // getView().isPostProcessingEnabled isn't bound upstream; the setter is, so mirror
    // the value locally for a correct getter/setter round-trip (default: enabled).
    actual var isPostProcessingEnabled: Boolean
        get() = _postProcessingEnabled
        set(value) {
            _postProcessingEnabled = value
            jsView.setPostProcessingEnabled(value)
        }

    actual var antiAliasing: AntiAliasing
        get() = _antiAliasing
        set(value) {
            _antiAliasing = value
            jsView.setAntiAliasing(when (value) {
                AntiAliasing.NONE -> io.github.erkko68.filament.web.View_AntiAliasing.NONE
                AntiAliasing.FXAA -> io.github.erkko68.filament.web.View_AntiAliasing.FXAA
            })
        }

    actual var dithering: Dithering
        get() = _dithering
        set(value) {
            _dithering = value
            val jsDith = when(value) {
                Dithering.NONE -> io.github.erkko68.filament.web.View_Dithering.NONE
                Dithering.TEMPORAL -> io.github.erkko68.filament.web.View_Dithering.TEMPORAL
            }
            jsView.setDithering(jsDith)
        }

    actual var dynamicResolutionOptions: DynamicResolutionOptions
        get() = _dynamicResolutionOptions
        set(value) {
            _dynamicResolutionOptions = value
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_DynamicResolutionOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.homogeneousScaling = value.homogeneousScaling
            jsOptions.minScale = jsNumbers(value.minScale, value.minScale)
            jsOptions.maxScale = jsNumbers(value.maxScale, value.maxScale)
            jsOptions.sharpness = value.sharpness.toDouble()
            jsOptions.quality = when(value.quality) {
                View.Quality.LOW -> io.github.erkko68.filament.web.View_QualityLevel.LOW
                View.Quality.MEDIUM -> io.github.erkko68.filament.web.View_QualityLevel.MEDIUM
                View.Quality.HIGH -> io.github.erkko68.filament.web.View_QualityLevel.HIGH
                View.Quality.ULTRA -> io.github.erkko68.filament.web.View_QualityLevel.ULTRA
            }
            jsView.setDynamicResolutionOptions(jsOptions)
        }

    actual var renderQuality: RenderQuality
        get() = _renderQuality
        set(value) {
            _renderQuality = value
            val jsQuality = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_RenderQuality`>()
            jsQuality.hdrColorBuffer = when(value.hdrColorBuffer) {
                View.Quality.LOW -> io.github.erkko68.filament.web.View_QualityLevel.LOW
                View.Quality.MEDIUM -> io.github.erkko68.filament.web.View_QualityLevel.MEDIUM
                View.Quality.HIGH -> io.github.erkko68.filament.web.View_QualityLevel.HIGH
                View.Quality.ULTRA -> io.github.erkko68.filament.web.View_QualityLevel.ULTRA
            }
            // Push to the JS view — without this the setter was a silent no-op and the
            // hdrColorBuffer quality stayed at whatever Filament.js defaults to.
            jsView.setRenderQuality(jsQuality)
        }

    actual var bloomOptions: BloomOptions
        get() = _bloomOptions
        set(value) {
            _bloomOptions = value
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_BloomOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.levels = value.levels.toDouble()
            jsOptions.resolution = value.resolution.toDouble()
            jsOptions.strength = value.strength.toDouble()
            jsOptions.threshold = value.threshold
            jsOptions.blendMode = when (value.blendMode) {
                View.BloomOptions.BlendMode.ADD -> io.github.erkko68.filament.web.View_BloomOptions_BlendMode.ADD
                View.BloomOptions.BlendMode.INTERPOLATE -> io.github.erkko68.filament.web.View_BloomOptions_BlendMode.INTERPOLATE
            }
            jsOptions.unsafeCast<BloomOptionsExt>().let {
                it.highlight = value.highlight.toDouble()
                it.dirt = value.dirt?.jsTexture
                it.dirtStrength = value.dirtStrength.toDouble()
            }
            jsView.setBloomOptions(jsOptions)
        }

    actual var fogOptions: FogOptions
        get() = _fogOptions
        set(value) {
            _fogOptions = value
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
        get() = _depthOfFieldOptions
        set(value) {
            _depthOfFieldOptions = value
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_DepthOfFieldOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.cocScale = value.cocScale.toDouble()
            jsOptions.maxApertureDiameter = value.maxApertureDiameter.toDouble()
            jsOptions.nativeResolution = value.nativeResolution
            jsOptions.filter = when (value.filter) {
                View.DepthOfFieldOptions.Filter.NONE   -> io.github.erkko68.filament.web.View_DepthOfFieldOptions_Filter.NONE
                View.DepthOfFieldOptions.Filter.UNUSED -> io.github.erkko68.filament.web.View_DepthOfFieldOptions_Filter.UNUSED
                View.DepthOfFieldOptions.Filter.MEDIAN -> io.github.erkko68.filament.web.View_DepthOfFieldOptions_Filter.MEDIAN
            }
            jsView.setDepthOfFieldOptions(jsOptions)
        }

    actual var vignetteOptions: VignetteOptions
        get() = _vignetteOptions
        set(value) {
            _vignetteOptions = value
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_VignetteOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.midPoint = value.midPoint.toDouble()
            jsOptions.roundness = value.roundness.toDouble()
            jsOptions.feather = value.feather.toDouble()
            jsOptions.color = jsNumbers(value.color[0], value.color[1], value.color[2], value.color[3])
            jsView.setVignetteOptions(jsOptions)
        }

    actual var ambientOcclusionOptions: AmbientOcclusionOptions
        get() = _ambientOcclusionOptions
        set(value) {
            _ambientOcclusionOptions = value
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
            
            val jsSsct = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_AmbientOcclusionOptions_Ssct`>()
            jsSsct.enabled = value.ssct.enabled
            jsSsct.lightConeRad = value.ssct.lightConeRad.toDouble()
            jsSsct.shadowDistance = value.ssct.shadowDistance.toDouble()
            jsSsct.contactDistanceMax = value.ssct.contactDistanceMax.toDouble()
            jsOptions.unsafeCast<AoOptionsExt>().ssct = jsSsct
            
            jsView.setAmbientOcclusionOptions(jsOptions)
        }

    actual var temporalAntiAliasingOptions: TemporalAntiAliasingOptions
        get() = _temporalAntiAliasingOptions
        set(value) {
            _temporalAntiAliasingOptions = value
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
        get() = _screenSpaceReflectionsOptions
        set(value) {
            _screenSpaceReflectionsOptions = value
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
        get() = _shadowType
        set(value) {
            _shadowType = value
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
        get() = _vsmShadowOptions
        set(value) {
            _vsmShadowOptions = value
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_VsmShadowOptions`>()
            jsOptions.anisotropy = value.anisotropy.toDouble()
            jsOptions.mipmapping = value.mipmapping
            jsOptions.msaaSamples = value.msaaSamples.toDouble()
            jsOptions.highPrecision = value.highPrecision
            jsOptions.lightBleedReduction = value.lightBleedReduction.toDouble()
            jsView.setVsmShadowOptions(jsOptions)
        }

    actual var softShadowOptions: SoftShadowOptions
        get() = _softShadowOptions
        set(value) {
            _softShadowOptions = value
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_SoftShadowOptions`>()
            jsOptions.penumbraScale = value.penumbraScale.toDouble()
            jsOptions.penumbraRatioScale = value.penumbraRatioScale.toDouble()
            jsView.setSoftShadowOptions(jsOptions)
        }

    actual var guardBandOptions: GuardBandOptions
        get() = _guardBandOptions
        set(value) {
            _guardBandOptions = value
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_GuardBandOptions`>()
            jsOptions.enabled = value.enabled
            jsView.setGuardBandOptions(jsOptions)
        }

    actual var stereoscopicOptions: StereoscopicOptions
        get() = _stereoscopicOptions
        set(value) {
            _stereoscopicOptions = value
            val jsOptions = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`View_StereoscopicOptions`>()
            jsOptions.enabled = value.enabled
            jsView.setStereoscopicOptions(jsOptions)
        }

    actual var multiSampleAntiAliasingOptions: MultiSampleAntiAliasingOptions
        get() = _multiSampleAntiAliasingOptions
        set(value) {
            _multiSampleAntiAliasingOptions = value
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
        get() = true
        set(value) {} // setScreenSpaceRefractionEnabled isn't bound in jsbindings.cpp

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

    actual var colorGrading: ColorGrading?
        get() {
            val jsColorGrading = jsView.getColorGrading()
            return if (jsColorGrading != null) ColorGrading(jsColorGrading) else null
        }
        set(value) {
            if (value != null) jsView.setColorGrading(value.jsColorGrading)
        }

    actual fun getLastDynamicResolutionScale(): FloatArray {
        return floatArrayOf(1.0f, 1.0f)
    }

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