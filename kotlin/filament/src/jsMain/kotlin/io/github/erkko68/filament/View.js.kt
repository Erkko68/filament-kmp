package io.github.erkko68.filament

import io.github.erkko68.filament.js.View as JSView

actual class View(internal val jsView: JSView) {
    private var _scene: Scene? = null
    private var _camera: Camera? = null
    private var _viewport: Viewport = Viewport(0, 0, 0, 0)
    private var _visibleLayersSelect: Int = 0
    private var _visibleLayersValues: Int = 0
    private var _dithering: Dithering = Dithering.NONE
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

    actual fun setName(name: String) {
        // Not in JS bindings
    }

    actual fun getName(): String? {
        return null
    }

    actual fun setScene(scene: Scene?) {
        _scene = scene
        if (scene != null) jsView.setScene(scene.jsScene)
    }

    actual fun getScene(): Scene? {
        return _scene
    }

    actual fun setCamera(camera: Camera?) {
        _camera = camera
        if (camera != null) jsView.setCamera(camera.jsCamera)
    }

    actual fun getCamera(): Camera? {
        return _camera
    }

    actual fun hasCamera(): Boolean {
        return _camera != null
    }

    actual fun setViewport(viewport: Viewport) {
        _viewport = viewport
        jsView.setViewport(arrayOf(viewport.left, viewport.bottom, viewport.width, viewport.height) as Array<Number>)
    }

    actual fun getViewport(): Viewport {
        return _viewport
    }

    actual fun setBlendMode(blendMode: BlendMode) {
        jsView.setBlendMode(when (blendMode) {
            View.BlendMode.OPAQUE -> io.github.erkko68.filament.js.View_BlendMode.OPAQUE
            View.BlendMode.TRANSLUCENT -> io.github.erkko68.filament.js.View_BlendMode.TRANSLUCENT
        })
    }

    actual fun getBlendMode(): BlendMode {
        return when (jsView.getBlendMode()) {
            io.github.erkko68.filament.js.View_BlendMode.OPAQUE -> View.BlendMode.OPAQUE
            io.github.erkko68.filament.js.View_BlendMode.TRANSLUCENT -> View.BlendMode.TRANSLUCENT
        }
    }

    actual fun setVisibleLayers(select: Int, values: Int) {
        _visibleLayersSelect = select
        _visibleLayersValues = values
        jsView.setVisibleLayers(select, values)
    }

    actual fun setLayerEnabled(layer: Int, enabled: Boolean) {
        val select = 1 shl layer
        val values = if (enabled) select else 0
        setVisibleLayers(select, values)
    }

    actual fun getVisibleLayers(): Int {
        return _visibleLayersValues
    }

    actual fun setPostProcessingEnabled(enabled: Boolean) {
        jsView.setPostProcessingEnabled(enabled)
    }

    actual fun isPostProcessingEnabled(): Boolean {
        // Not exposed in JS bindings
        return true
    }

    actual fun setAntiAliasing(type: AntiAliasing) {
        _antiAliasing = type
        jsView.setAntiAliasing(when (type) {
            AntiAliasing.NONE -> io.github.erkko68.filament.js.View_AntiAliasing.NONE
            AntiAliasing.FXAA -> io.github.erkko68.filament.js.View_AntiAliasing.FXAA
        })
    }

    actual fun getAntiAliasing(): AntiAliasing {
        return _antiAliasing
    }

    actual fun setDithering(dithering: Dithering) {
        _dithering = dithering
        val jsDith = when(dithering) {
            Dithering.NONE -> io.github.erkko68.filament.js.View_Dithering.NONE
            Dithering.TEMPORAL -> io.github.erkko68.filament.js.View_Dithering.TEMPORAL
        }
        val jsViewExt = jsView.asDynamic()
        if (jsViewExt.setDithering != null) {
            jsViewExt.setDithering(jsDith)
        }
    }

    actual fun getDithering(): Dithering {
        return _dithering
    }

    actual fun setDynamicResolutionOptions(options: DynamicResolutionOptions) {
        _dynamicResolutionOptions = options
        val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_DynamicResolutionOptions`>()
        jsOptions.enabled = options.enabled
        jsOptions.homogeneousScaling = options.homogeneousScaling
        jsOptions.minScale = arrayOf(options.minScale, options.minScale) as Array<Number>
        jsOptions.maxScale = arrayOf(options.maxScale, options.maxScale) as Array<Number>
        jsOptions.sharpness = options.sharpness
        jsOptions.quality = when(options.quality) {
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

    actual fun getDynamicResolutionOptions(): DynamicResolutionOptions {
        return _dynamicResolutionOptions
    }

    actual fun setRenderQuality(renderQuality: RenderQuality) {
        _renderQuality = renderQuality
        val jsQuality = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_RenderQuality`>()
        jsQuality.hdrColorBuffer = when(renderQuality.hdrColorBuffer) {
            View.Quality.LOW -> io.github.erkko68.filament.js.View_QualityLevel.LOW
            View.Quality.MEDIUM -> io.github.erkko68.filament.js.View_QualityLevel.MEDIUM
            View.Quality.HIGH -> io.github.erkko68.filament.js.View_QualityLevel.HIGH
            View.Quality.ULTRA -> io.github.erkko68.filament.js.View_QualityLevel.ULTRA
        }
    }

    actual fun getRenderQuality(): RenderQuality {
        return _renderQuality
    }

    actual fun setBloomOptions(options: BloomOptions) {
        _bloomOptions = options
        val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_BloomOptions`>()
        jsOptions.enabled = options.enabled
        jsOptions.levels = options.levels
        jsOptions.resolution = options.resolution
        jsOptions.strength = options.strength
        jsOptions.threshold = options.threshold
        jsOptions.blendMode = when (options.blendMode) {
            View.BloomOptions.BlendMode.ADD -> io.github.erkko68.filament.js.View_BloomOptions_BlendMode.ADD
            View.BloomOptions.BlendMode.INTERPOLATE -> io.github.erkko68.filament.js.View_BloomOptions_BlendMode.INTERPOLATE
        }
        jsOptions.asDynamic().highlight = options.highlight
        jsOptions.asDynamic().dirt = options.dirt?.jsTexture
        jsOptions.asDynamic().dirtStrength = options.dirtStrength
        jsView.setBloomOptions(jsOptions)
    }

    actual fun getBloomOptions(): BloomOptions {
        return _bloomOptions
    }

    actual fun setFogOptions(options: FogOptions) {
        _fogOptions = options
        val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_FogOptions`>()
        jsOptions.enabled = options.enabled
        jsOptions.distance = options.distance
        jsOptions.density = options.density
        jsOptions.height = options.height
        jsOptions.heightFalloff = options.heightFalloff
        jsOptions.color = arrayOf(options.color[0], options.color[1], options.color[2])
        jsOptions.cutOffDistance = options.cutOffDistance
        jsOptions.maximumOpacity = options.maximumOpacity
        jsOptions.inScatteringStart = options.inScatteringStart
        jsOptions.inScatteringSize = options.inScatteringSize
        jsOptions.fogColorFromIbl = options.fogColorFromIbl
        jsOptions.asDynamic().densityMap = options.densityMap?.jsTexture
        jsView.setFogOptions(jsOptions)
    }

    actual fun getFogOptions(): FogOptions {
        return _fogOptions
    }

    actual fun setDepthOfFieldOptions(options: DepthOfFieldOptions) {
        _depthOfFieldOptions = options
        val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_DepthOfFieldOptions`>()
        jsOptions.enabled = options.enabled
        jsOptions.cocScale = options.cocScale
        jsOptions.maxApertureDiameter = options.maxApertureDiameter
        jsOptions.nativeResolution = options.nativeResolution
        jsOptions.filter = when (options.filter) {
            View.DepthOfFieldOptions.Filter.NONE -> io.github.erkko68.filament.js.View_DepthOfFieldOptions_Filter.NONE
            View.DepthOfFieldOptions.Filter.MEDIAN -> io.github.erkko68.filament.js.View_DepthOfFieldOptions_Filter.MEDIAN
            View.DepthOfFieldOptions.Filter.GAUSSIAN -> io.github.erkko68.filament.js.View_DepthOfFieldOptions_Filter.MEDIAN // GAUSSIAN not in JS bindings
        }
        jsView.setDepthOfFieldOptions(jsOptions)
    }

    actual fun getDepthOfFieldOptions(): DepthOfFieldOptions {
        return _depthOfFieldOptions
    }

    actual fun setVignetteOptions(options: VignetteOptions) {
        _vignetteOptions = options
        val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_VignetteOptions`>()
        jsOptions.enabled = options.enabled
        jsOptions.midPoint = options.midPoint
        jsOptions.roundness = options.roundness
        jsOptions.feather = options.feather
        jsOptions.color = arrayOf(options.color[0], options.color[1], options.color[2], options.color[3])
        jsView.setVignetteOptions(jsOptions)
    }

    actual fun getVignetteOptions(): VignetteOptions {
        return _vignetteOptions
    }

    actual fun setAmbientOcclusionOptions(options: AmbientOcclusionOptions) {
        _ambientOcclusionOptions = options
        val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_AmbientOcclusionOptions`>()
        jsOptions.enabled = options.enabled
        jsOptions.radius = options.radius
        jsOptions.bias = options.bias
        jsOptions.intensity = options.intensity
        jsOptions.power = options.power
        jsOptions.resolution = options.resolution
        jsOptions.bilateralThreshold = options.bilateralThreshold
        
        val jsSsct = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_AmbientOcclusionOptions_Ssct`>()
        jsSsct.enabled = options.ssct.enabled
        jsSsct.lightConeRad = options.ssct.lightConeRad
        jsSsct.shadowDistance = options.ssct.shadowDistance
        jsSsct.contactDistanceMax = options.ssct.contactDistanceMax
        jsOptions.asDynamic().ssct = jsSsct
        
        jsView.setAmbientOcclusionOptions(jsOptions)
    }

    actual fun getAmbientOcclusionOptions(): AmbientOcclusionOptions {
        return _ambientOcclusionOptions
    }

    actual fun setTemporalAntiAliasingOptions(options: TemporalAntiAliasingOptions) {
        _temporalAntiAliasingOptions = options
        val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_TemporalAntiAliasingOptions`>()
        jsOptions.enabled = options.enabled
        jsOptions.feedback = options.feedback
        jsOptions.lodBias = options.lodBias
        jsOptions.sharpness = options.sharpness
        jsOptions.upscaling = options.upscaling
        jsOptions.filterHistory = options.filterHistory
        jsView.setTemporalAntiAliasingOptions(jsOptions)
    }

    actual fun getTemporalAntiAliasingOptions(): TemporalAntiAliasingOptions {
        return _temporalAntiAliasingOptions
    }

    actual fun setScreenSpaceReflectionsOptions(options: ScreenSpaceReflectionsOptions) {
        _screenSpaceReflectionsOptions = options
        val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_ScreenSpaceReflectionsOptions`>()
        jsOptions.enabled = options.enabled
        jsOptions.thickness = options.thickness
        jsOptions.bias = options.bias
        jsOptions.maxDistance = options.maxDistance
        jsOptions.stride = options.stride
        jsView.setScreenSpaceReflectionsOptions(jsOptions)
    }

    actual fun getScreenSpaceReflectionsOptions(): ScreenSpaceReflectionsOptions {
        return _screenSpaceReflectionsOptions
    }

    actual fun setRenderTarget(target: RenderTarget?) {
        _renderTarget = target
        if (target != null) jsView.setRenderTarget(target.jsRenderTarget)
    }

    actual fun getRenderTarget(): RenderTarget? {
        return _renderTarget
    }

    actual fun setShadowType(type: ShadowType) {
        _shadowType = type
        val jsViewExt = jsView.asDynamic()
        if (jsViewExt.setShadowType != null) {
            val jsType = when(type) {
                ShadowType.PCF -> io.github.erkko68.filament.js.View_ShadowType.PCF
                ShadowType.VSM -> io.github.erkko68.filament.js.View_ShadowType.VSM
                ShadowType.DPCF -> io.github.erkko68.filament.js.View_ShadowType.DPCF
                ShadowType.PCSS -> io.github.erkko68.filament.js.View_ShadowType.PCSS
                ShadowType.PCFd -> io.github.erkko68.filament.js.View_ShadowType.PCFd
            }
            jsViewExt.setShadowType(jsType)
        }
    }

    actual fun getShadowType(): ShadowType {
        return _shadowType
    }

    actual fun setVsmShadowOptions(options: VsmShadowOptions) {
        _vsmShadowOptions = options
        val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_VsmShadowOptions`>()
        jsOptions.anisotropy = options.anisotropy
        jsOptions.mipmapping = options.mipmapping
        jsOptions.msaaSamples = options.msaaSamples
        jsOptions.highPrecision = options.highPrecision
        jsOptions.lightBleedReduction = options.lightBleedReduction
        val jsViewExt = jsView.asDynamic()
        if (jsViewExt.setVsmShadowOptions != null) {
            jsViewExt.setVsmShadowOptions(jsOptions)
        }
    }

    actual fun getVsmShadowOptions(): VsmShadowOptions {
        return _vsmShadowOptions
    }

    actual fun setSoftShadowOptions(options: SoftShadowOptions) {
        _softShadowOptions = options
        val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_SoftShadowOptions`>()
        jsOptions.penumbraScale = options.penumbraScale
        jsOptions.penumbraRatioScale = options.penumbraRatioScale
        val jsViewExt = jsView.asDynamic()
        if (jsViewExt.setSoftShadowOptions != null) {
            jsViewExt.setSoftShadowOptions(jsOptions)
        }
    }

    actual fun getSoftShadowOptions(): SoftShadowOptions {
        return _softShadowOptions
    }

    actual fun setGuardBandOptions(options: GuardBandOptions) {
        _guardBandOptions = options
        val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_GuardBandOptions`>()
        jsOptions.enabled = options.enabled
        jsView.setGuardBandOptions(jsOptions)
    }

    actual fun getGuardBandOptions(): GuardBandOptions {
        return _guardBandOptions
    }

    actual fun setStereoscopicOptions(options: StereoscopicOptions) {
        _stereoscopicOptions = options
        val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_StereoscopicOptions`>()
        jsOptions.enabled = options.enabled
        jsView.setStereoscopicOptions(jsOptions)
    }

    actual fun getStereoscopicOptions(): StereoscopicOptions {
        return _stereoscopicOptions
    }

    actual fun setMultiSampleAntiAliasingOptions(options: MultiSampleAntiAliasingOptions) {
        _multiSampleAntiAliasingOptions = options
        val jsOptions = js("{}").unsafeCast<io.github.erkko68.filament.js.`View_MultiSampleAntiAliasingOptions`>()
        jsOptions.enabled = options.enabled
        jsOptions.sampleCount = options.sampleCount
        jsOptions.customResolve = options.customResolve
        jsView.setMultiSampleAntiAliasingOptions(jsOptions)
    }

    actual fun getMultiSampleAntiAliasingOptions(): MultiSampleAntiAliasingOptions {
        return _multiSampleAntiAliasingOptions
    }

    actual fun setFrustumCullingEnabled(enabled: Boolean) {
        // Not in JS bindings
    }

    actual fun isFrustumCullingEnabled(): Boolean {
        return true
    }

    actual fun setShadowingEnabled(enabled: Boolean) {
        // Not in JS bindings
    }

    actual fun setScreenSpaceRefractionEnabled(enabled: Boolean) {
        // Not in JS bindings
    }

    actual fun setStencilBufferEnabled(enabled: Boolean) {
        jsView.setStencilBufferEnabled(enabled)
    }

    actual fun isStencilBufferEnabled(): Boolean {
        return jsView.isStencilBufferEnabled()
    }

    actual fun setFrontFaceWindingInverted(inverted: Boolean) {
        // Not in JS bindings
    }

    actual fun isFrontFaceWindingInverted(): Boolean {
        return false
    }

    actual fun setTransparentPickingEnabled(enabled: Boolean) {
        jsView.setTransparentPickingEnabled(enabled)
    }

    actual fun isTransparentPickingEnabled(): Boolean {
        return jsView.isTransparentPickingEnabled()
    }

    actual fun setMaterialGlobal(index: Int, value: FloatArray) {
    }

    actual fun getMaterialGlobal(index: Int): FloatArray {
        return floatArrayOf()
    }

    actual fun getFogEntity(): Int {
        return 0
    }

    actual fun clearFrameHistory(engine: Engine) {
    }

    actual fun setDynamicLightingOptions(zNear: Float, zFar: Float) {
    }

    actual fun setColorGrading(colorGrading: ColorGrading?) {
        if (colorGrading != null) jsView.setColorGrading(colorGrading.jsColorGrading)
    }

    actual fun getColorGrading(): ColorGrading? {
        val jsColorGrading = jsView.asDynamic().getColorGrading()
        return if (jsColorGrading != null) ColorGrading(jsColorGrading) else null
    }

    actual fun getLastDynamicResolutionScale(): FloatArray {
        return floatArrayOf(1.0f, 1.0f)
    }

    actual fun pick(x: Int, y: Int, callback: (PickingQueryResult) -> Unit) {
        jsView.pick(x, y) { result ->
            callback(PickingQueryResult(
                result.renderable.toInt(),
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
        actual var quality: Quality = Quality.MEDIUM
    }
    actual class RenderQuality {
        actual var hdrColorBuffer: Quality = Quality.MEDIUM
    }
    actual class BloomOptions {
        actual var enabled: Boolean = false
        actual var levels: Int = 6
        actual var resolution: Int = 0
        actual var strength: Float = 0.10f
        actual var threshold: Boolean = true
        actual var dirt: Texture? = null
        actual var dirtStrength: Float = 0.20f
        actual var quality: Quality = Quality.MEDIUM
        actual var lensFlare: Boolean = false
        actual var starburst: Boolean = false
        actual var chromaticAberration: Float = 0.0f
        actual var ghostCount: Int = 0
        actual var ghostSpacing: Float = 0.0f
        actual var ghostThreshold: Float = 0.0f
        actual var haloRadius: Float = 0.0f
        actual var haloThickness: Float = 0.0f
        actual var haloThreshold: Float = 0.0f
        actual var highlight: Float = 0.0f
        actual var blendMode: BlendMode = BlendMode.ADD
        actual enum class BlendMode { ADD, INTERPOLATE }
    }
    actual class FogOptions {
        actual var enabled: Boolean = false
        actual var distance: Float = 0.0f
        actual var density: Float = 0.1f
        actual var height: Float = 0.0f
        actual var heightFalloff: Float = 1.0f
        actual var color: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
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
        actual var filter: Filter = Filter.GAUSSIAN
        actual var nativeResolution: Boolean = false
        actual var foregroundRingCount: Int = 0
        actual var backgroundRingCount: Int = 0
        actual var fastGatherRingCount: Int = 0
        actual var maxForegroundCOC: Int = 0
        actual var maxBackgroundCOC: Int = 0
        actual enum class Filter { NONE, MEDIAN, GAUSSIAN }
    }
    actual class VignetteOptions {
        actual var enabled: Boolean = false
        actual var midPoint: Float = 0.5f
        actual var roundness: Float = 0.0f
        actual var feather: Float = 0.0f
        actual var color: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
    }
    actual class AmbientOcclusionOptions {
        actual var radius: Float = 0.3f
        actual var bias: Float = 0.005f
        actual var intensity: Float = 1.0f
        actual var scale: Float = 1.0f
        actual var power: Float = 1.0f
        actual var minConeAngle: Float = 0.0f
        actual var quality: Quality = Quality.MEDIUM
        actual var lowPassFilter: Quality = Quality.MEDIUM
        actual var upsampling: Quality = Quality.MEDIUM
        actual var enabled: Boolean = false
        actual var bentNormals: Boolean = false
        actual var bilateralThreshold: Float = 0.005f
        actual var resolution: Float = 0.5f
        actual var ssct: Ssct = Ssct()
        actual class Ssct {
            actual var enabled: Boolean = false
            actual var lightConeRad: Float = 1.0f
            actual var shadowDistance: Float = 0.3f
            actual var contactDistanceMax: Float = 1.0f
            actual var intensity: Float = 1.0f
            actual var lightDirection: FloatArray = floatArrayOf(0.0f, -1.0f, 0.0f)
            actual var depthBias: Float = 0.01f
            actual var depthSlopeBias: Float = 0.01f
            actual var sampleCount: Int = 4
            actual var rayCount: Int = 1
        }
    }
    actual class TemporalAntiAliasingOptions {
        actual var feedback: Float = 0.95f
        actual var lodBias: Float = -1.0f
        actual var sharpness: Float = 0.0f
        actual var enabled: Boolean = false
        actual var upscaling: Float = 1.0f
        actual var filterHistory: Boolean = true
        actual var filterInput: Boolean = true
        actual var useYCoCg: Boolean = true
        actual var hdr: Boolean = true
        actual var boxType: Int = 1
        actual var boxClipping: Int = 1
        actual var jitterPattern: Int = 2
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
        actual var lightBleedReduction: Float = 0.0f
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