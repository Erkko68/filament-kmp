package io.github.erkko68.filament

import io.github.erkko68.filament.js.View as JSView

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

    actual var name: String?
        get() = null
        set(value) {}

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
            jsView.setViewport(arrayOf(value.left, value.bottom, value.width, value.height) as Array<Number>)
        }

    actual var blendMode: BlendMode
        get() = when (jsView.getBlendMode()) {
            io.github.erkko68.filament.js.View_BlendMode.OPAQUE -> View.BlendMode.OPAQUE
            io.github.erkko68.filament.js.View_BlendMode.TRANSLUCENT -> View.BlendMode.TRANSLUCENT
        }
        set(value) {
            jsView.setBlendMode(when (value) {
                View.BlendMode.OPAQUE -> io.github.erkko68.filament.js.View_BlendMode.OPAQUE
                View.BlendMode.TRANSLUCENT -> io.github.erkko68.filament.js.View_BlendMode.TRANSLUCENT
            })
        }

    actual fun setVisibleLayers(select: Int, values: Int) {
        _visibleLayersValues = values
        jsView.setVisibleLayers(select, values)
    }

    actual fun setLayerEnabled(layer: Int, enabled: Boolean) {
        val select = 1 shl layer
        val values = if (enabled) select else 0
        setVisibleLayers(select, values)
    }

    actual fun getVisibleLayers(): Int = _visibleLayersValues

    actual var isPostProcessingEnabled: Boolean
        get() = true // Not exposed in JS bindings
        set(value) { jsView.setPostProcessingEnabled(value) }

    actual var antiAliasing: AntiAliasing
        get() = _antiAliasing
        set(value) {
            _antiAliasing = value
            jsView.setAntiAliasing(when (value) {
                AntiAliasing.NONE -> io.github.erkko68.filament.js.View_AntiAliasing.NONE
                AntiAliasing.FXAA -> io.github.erkko68.filament.js.View_AntiAliasing.FXAA
            })
        }

    actual var dithering: Dithering
        get() = _dithering
        set(value) {
            _dithering = value
            val jsDith = when(value) {
                Dithering.NONE -> io.github.erkko68.filament.js.View_Dithering.NONE
                Dithering.TEMPORAL -> io.github.erkko68.filament.js.View_Dithering.TEMPORAL
            }
            val jsViewExt = jsView.asDynamic()
            if (jsViewExt.setDithering != null) {
                jsViewExt.setDithering(jsDith)
            }
        }

    actual var dynamicResolutionOptions: DynamicResolutionOptions
        get() = _dynamicResolutionOptions
        set(value) {
            _dynamicResolutionOptions = value
            val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_DynamicResolutionOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.homogeneousScaling = value.homogeneousScaling
            jsOptions.minScale = arrayOf(value.minScale, value.minScale) as Array<Number>
            jsOptions.maxScale = arrayOf(value.maxScale, value.maxScale) as Array<Number>
            jsOptions.sharpness = value.sharpness
            jsOptions.quality = when(value.quality) {
                View.Quality.LOW -> io.github.erkko68.filament.js.View_QualityLevel.LOW
                View.Quality.MEDIUM -> io.github.erkko68.filament.js.View_QualityLevel.MEDIUM
                View.Quality.HIGH -> io.github.erkko68.filament.js.View_QualityLevel.HIGH
                View.Quality.ULTRA -> io.github.erkko68.filament.js.View_QualityLevel.ULTRA
            }
            val jsViewExt = jsView.asDynamic()
            if (jsViewExt.setDynamicResolutionOptions != null) {
                jsViewExt.setDynamicResolutionOptions(jsOptions)
            }
        }

    actual var renderQuality: RenderQuality
        get() = _renderQuality
        set(value) {
            _renderQuality = value
            val jsQuality = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_RenderQuality`>()
            jsQuality.hdrColorBuffer = when(value.hdrColorBuffer) {
                View.Quality.LOW -> io.github.erkko68.filament.js.View_QualityLevel.LOW
                View.Quality.MEDIUM -> io.github.erkko68.filament.js.View_QualityLevel.MEDIUM
                View.Quality.HIGH -> io.github.erkko68.filament.js.View_QualityLevel.HIGH
                View.Quality.ULTRA -> io.github.erkko68.filament.js.View_QualityLevel.ULTRA
            }
            // Push to the JS view — without this the setter was a silent no-op and the
            // hdrColorBuffer quality stayed at whatever Filament.js defaults to.
            val jsViewExt = jsView.asDynamic()
            if (jsViewExt.setRenderQuality != null) {
                jsViewExt.setRenderQuality(jsQuality)
            }
        }

    actual var bloomOptions: BloomOptions
        get() = _bloomOptions
        set(value) {
            _bloomOptions = value
            val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_BloomOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.levels = value.levels
            jsOptions.resolution = value.resolution
            jsOptions.strength = value.strength
            jsOptions.threshold = value.threshold
            jsOptions.blendMode = when (value.blendMode) {
                View.BloomOptions.BlendMode.ADD -> io.github.erkko68.filament.js.View_BloomOptions_BlendMode.ADD
                View.BloomOptions.BlendMode.INTERPOLATE -> io.github.erkko68.filament.js.View_BloomOptions_BlendMode.INTERPOLATE
            }
            jsOptions.asDynamic().highlight = value.highlight
            jsOptions.asDynamic().dirt = value.dirt?.jsTexture
            jsOptions.asDynamic().dirtStrength = value.dirtStrength
            jsView.setBloomOptions(jsOptions)
        }

    actual var fogOptions: FogOptions
        get() = _fogOptions
        set(value) {
            _fogOptions = value
            val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_FogOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.distance = value.distance
            jsOptions.density = value.density
            jsOptions.height = value.height
            jsOptions.heightFalloff = value.heightFalloff
            jsOptions.color = arrayOf(value.color[0], value.color[1], value.color[2])
            jsOptions.cutOffDistance = value.cutOffDistance
            jsOptions.maximumOpacity = value.maximumOpacity
            jsOptions.inScatteringStart = value.inScatteringStart
            jsOptions.inScatteringSize = value.inScatteringSize
            jsOptions.fogColorFromIbl = value.fogColorFromIbl
            jsOptions.asDynamic().densityMap = value.densityMap?.jsTexture
            jsView.setFogOptions(jsOptions)
        }

    actual var depthOfFieldOptions: DepthOfFieldOptions
        get() = _depthOfFieldOptions
        set(value) {
            _depthOfFieldOptions = value
            val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_DepthOfFieldOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.cocScale = value.cocScale
            jsOptions.maxApertureDiameter = value.maxApertureDiameter
            jsOptions.nativeResolution = value.nativeResolution
            jsOptions.filter = when (value.filter) {
                View.DepthOfFieldOptions.Filter.NONE   -> io.github.erkko68.filament.js.View_DepthOfFieldOptions_Filter.NONE
                View.DepthOfFieldOptions.Filter.UNUSED -> io.github.erkko68.filament.js.View_DepthOfFieldOptions_Filter.UNUSED
                View.DepthOfFieldOptions.Filter.MEDIAN -> io.github.erkko68.filament.js.View_DepthOfFieldOptions_Filter.MEDIAN
            }
            jsView.setDepthOfFieldOptions(jsOptions)
        }

    actual var vignetteOptions: VignetteOptions
        get() = _vignetteOptions
        set(value) {
            _vignetteOptions = value
            val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_VignetteOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.midPoint = value.midPoint
            jsOptions.roundness = value.roundness
            jsOptions.feather = value.feather
            jsOptions.color = arrayOf(value.color[0], value.color[1], value.color[2], value.color[3])
            jsView.setVignetteOptions(jsOptions)
        }

    actual var ambientOcclusionOptions: AmbientOcclusionOptions
        get() = _ambientOcclusionOptions
        set(value) {
            _ambientOcclusionOptions = value
            val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_AmbientOcclusionOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.radius = value.radius
            jsOptions.bias = value.bias
            jsOptions.intensity = value.intensity
            jsOptions.power = value.power
            jsOptions.resolution = value.resolution
            jsOptions.bilateralThreshold = value.bilateralThreshold
            
            val jsSsct = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_AmbientOcclusionOptions_Ssct`>()
            jsSsct.enabled = value.ssct.enabled
            jsSsct.lightConeRad = value.ssct.lightConeRad
            jsSsct.shadowDistance = value.ssct.shadowDistance
            jsSsct.contactDistanceMax = value.ssct.contactDistanceMax
            jsOptions.asDynamic().ssct = jsSsct
            
            jsView.setAmbientOcclusionOptions(jsOptions)
        }

    actual var temporalAntiAliasingOptions: TemporalAntiAliasingOptions
        get() = _temporalAntiAliasingOptions
        set(value) {
            _temporalAntiAliasingOptions = value
            val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_TemporalAntiAliasingOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.feedback = value.feedback
            jsOptions.lodBias = value.lodBias
            jsOptions.sharpness = value.sharpness
            jsOptions.upscaling = value.upscaling
            jsOptions.filterHistory = value.filterHistory
            jsView.setTemporalAntiAliasingOptions(jsOptions)
        }

    actual var screenSpaceReflectionsOptions: ScreenSpaceReflectionsOptions
        get() = _screenSpaceReflectionsOptions
        set(value) {
            _screenSpaceReflectionsOptions = value
            val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_ScreenSpaceReflectionsOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.thickness = value.thickness
            jsOptions.bias = value.bias
            jsOptions.maxDistance = value.maxDistance
            jsOptions.stride = value.stride
            jsView.setScreenSpaceReflectionsOptions(jsOptions)
        }

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
            val jsViewExt = jsView.asDynamic()
            if (jsViewExt.setShadowType != null) {
                val jsType = when(value) {
                    ShadowType.PCF -> io.github.erkko68.filament.js.View_ShadowType.PCF
                    ShadowType.VSM -> io.github.erkko68.filament.js.View_ShadowType.VSM
                    ShadowType.DPCF -> io.github.erkko68.filament.js.View_ShadowType.DPCF
                    ShadowType.PCSS -> io.github.erkko68.filament.js.View_ShadowType.PCSS
                    ShadowType.PCFd -> io.github.erkko68.filament.js.View_ShadowType.PCFd
                }
                jsViewExt.setShadowType(jsType)
            }
        }

    actual var vsmShadowOptions: VsmShadowOptions
        get() = _vsmShadowOptions
        set(value) {
            _vsmShadowOptions = value
            val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_VsmShadowOptions`>()
            jsOptions.anisotropy = value.anisotropy
            jsOptions.mipmapping = value.mipmapping
            jsOptions.msaaSamples = value.msaaSamples
            jsOptions.highPrecision = value.highPrecision
            jsOptions.lightBleedReduction = value.lightBleedReduction
            val jsViewExt = jsView.asDynamic()
            if (jsViewExt.setVsmShadowOptions != null) {
                jsViewExt.setVsmShadowOptions(jsOptions)
            }
        }

    actual var softShadowOptions: SoftShadowOptions
        get() = _softShadowOptions
        set(value) {
            _softShadowOptions = value
            val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_SoftShadowOptions`>()
            jsOptions.penumbraScale = value.penumbraScale
            jsOptions.penumbraRatioScale = value.penumbraRatioScale
            val jsViewExt = jsView.asDynamic()
            if (jsViewExt.setSoftShadowOptions != null) {
                jsViewExt.setSoftShadowOptions(jsOptions)
            }
        }

    actual var guardBandOptions: GuardBandOptions
        get() = _guardBandOptions
        set(value) {
            _guardBandOptions = value
            val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_GuardBandOptions`>()
            jsOptions.enabled = value.enabled
            jsView.setGuardBandOptions(jsOptions)
        }

    actual var stereoscopicOptions: StereoscopicOptions
        get() = _stereoscopicOptions
        set(value) {
            _stereoscopicOptions = value
            val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_StereoscopicOptions`>()
            jsOptions.enabled = value.enabled
            jsView.setStereoscopicOptions(jsOptions)
        }

    actual var multiSampleAntiAliasingOptions: MultiSampleAntiAliasingOptions
        get() = _multiSampleAntiAliasingOptions
        set(value) {
            _multiSampleAntiAliasingOptions = value
            val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_MultiSampleAntiAliasingOptions`>()
            jsOptions.enabled = value.enabled
            jsOptions.sampleCount = value.sampleCount
            jsOptions.customResolve = value.customResolve
            jsView.setMultiSampleAntiAliasingOptions(jsOptions)
        }

    // Only setters are bound on JS for these three; the getters stay tracked locally.
    private var _isFrustumCullingEnabled: Boolean = true
    actual var isFrustumCullingEnabled: Boolean
        get() = _isFrustumCullingEnabled
        set(value) {
            _isFrustumCullingEnabled = value
            // setFrustumCullingEnabled isn't bound in jsbindings.cpp — leave as a no-op so
            // the local mirror still reflects the user's intent for the getter.
        }
    private var _isShadowingEnabled: Boolean = true
    actual var isShadowingEnabled: Boolean
        get() = _isShadowingEnabled
        set(value) {
            _isShadowingEnabled = value
            jsView.setShadowingEnabled(value)
        }
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
        jsView.setMaterialGlobal(index, value.toTypedArray() as Array<Number>)
    }

    actual fun getMaterialGlobal(index: Int): FloatArray {
        val arr = jsView.getMaterialGlobal(index)
        return FloatArray(4) { i -> arr[i].toFloat() }
    }

    actual val fogEntity: Int
        get() = jsView.getFogEntity().getId().toInt()

    actual fun clearFrameHistory(engine: Engine) {
        jsView.clearFrameHistory(engine.jsEngine)
    }

    actual fun setDynamicLightingOptions(zNear: Float, zFar: Float) {
        jsView.setDynamicLightingOptions(zNear, zFar)
    }

    actual var colorGrading: ColorGrading?
        get() {
            val jsColorGrading = jsView.asDynamic().getColorGrading()
            return if (jsColorGrading != null) ColorGrading(jsColorGrading) else null
        }
        set(value) {
            if (value != null) jsView.setColorGrading(value.jsColorGrading)
        }

    actual fun getLastDynamicResolutionScale(): FloatArray {
        return floatArrayOf(1.0f, 1.0f)
    }

    actual fun pick(x: Int, y: Int, callback: (PickingQueryResult) -> Unit) {
        jsView.pick(x, y) { result ->
            callback(PickingQueryResult(
                result.renderable.getId().toInt(),
                result.depth.toFloat(),
                floatArrayOf(result.fragCoords[0].toFloat(), result.fragCoords[1].toFloat(), result.fragCoords[2].toFloat())
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
        actual var densityMap: Texture? = null
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
        actual var radius: Float = 0.3f
        actual var bias: Float = 0.0005f
        actual var intensity: Float = 1.0f
        actual var scale: Float = 0.5f
        actual var power: Float = 1.0f
        actual var minConeAngle: Float = 0.0f
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
        actual var feedback: Float = 0.12f
        actual var lodBias: Float = -1.0f
        actual var sharpness: Float = 0.0f
        actual var enabled: Boolean = false
        actual var upscaling: Float = 1.0f
        actual var filterHistory: Boolean = true
        actual var filterInput: Boolean = true
        actual var useYCoCg: Boolean = false
        actual var hdr: Boolean = true
        actual var boxType: Int = 0          // BoxType.AABB
        actual var boxClipping: Int = 0      // BoxClipping.ACCURATE
        actual var jitterPattern: Int = 3    // JitterPattern.HALTON_23_X16
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