package io.github.erkko68.filament

import com.google.android.filament.View as FilamentView
import com.google.android.filament.Texture as FilamentTexture
import com.google.android.filament.Viewport as FilamentViewport

actual class View internal constructor(internal val nativeView: FilamentView) {
    internal val getNativeObject: Long get() = nativeView.nativeObject
    
    private var mScene: Scene? = null
    private var mCamera: Camera? = null
    private var mRenderTarget: RenderTarget? = null

    actual enum class Dithering { NONE, TEMPORAL }
    actual enum class BlendMode { OPAQUE, TRANSLUCENT }
    actual enum class Quality { LOW, MEDIUM, HIGH, ULTRA }
    actual enum class ShadowType { PCF, VSM, DPCF, PCSS, PCFd }

    private var mShadowType: ShadowType = ShadowType.PCF

    actual class DynamicResolutionOptions actual constructor() {
        val native = FilamentView.DynamicResolutionOptions()
        actual var enabled: Boolean get() = native.enabled; set(v) { native.enabled = v }
        actual var homogeneousScaling: Boolean get() = native.homogeneousScaling; set(v) { native.homogeneousScaling = v }
        actual var minScale: Float get() = native.minScale; set(v) { native.minScale = v }
        actual var maxScale: Float get() = native.maxScale; set(v) { native.maxScale = v }
        actual var sharpness: Float get() = native.sharpness; set(v) { native.sharpness = v }
        actual var quality: Quality 
            get() = Quality.values()[native.quality.ordinal]
            set(v) { native.quality = FilamentView.QualityLevel.values()[v.ordinal] }
    }

    actual class RenderQuality actual constructor() {
        val native = FilamentView.RenderQuality()
        actual var hdrColorBuffer: Quality 
            get() = Quality.values()[native.hdrColorBuffer.ordinal]
            set(v) { native.hdrColorBuffer = FilamentView.QualityLevel.values()[v.ordinal] }
    }

    actual class BloomOptions actual constructor() {
        val native = FilamentView.BloomOptions()
        actual var enabled: Boolean get() = native.enabled; set(v) { native.enabled = v }
        actual var levels: Int get() = native.levels; set(v) { native.levels = v }
        actual var resolution: Int get() = native.resolution; set(v) { native.resolution = v }
        actual var strength: Float get() = native.strength; set(v) { native.strength = v }
        actual var threshold: Boolean get() = native.threshold; set(v) { native.threshold = v }
        actual var dirt: Texture? = null
        actual var dirtStrength: Float get() = native.dirtStrength; set(v) { native.dirtStrength = v }
        actual var quality: Quality 
            get() = Quality.values()[native.quality.ordinal]
            set(v) { native.quality = FilamentView.QualityLevel.values()[v.ordinal] }
        actual var lensFlare: Boolean get() = native.lensFlare; set(v) { native.lensFlare = v }
        actual var starburst: Boolean get() = native.starburst; set(v) { native.starburst = v }
        actual var chromaticAberration: Float get() = native.chromaticAberration; set(v) { native.chromaticAberration = v }
        actual var ghostCount: Int get() = native.ghostCount; set(v) { native.ghostCount = v }
        actual var ghostSpacing: Float get() = native.ghostSpacing; set(v) { native.ghostSpacing = v }
        actual var ghostThreshold: Float get() = native.ghostThreshold; set(v) { native.ghostThreshold = v }
        actual var haloRadius: Float get() = native.haloRadius; set(v) { native.haloRadius = v }
        actual var haloThickness: Float get() = native.haloThickness; set(v) { native.haloThickness = v }
        actual var haloThreshold: Float get() = native.haloThreshold; set(v) { native.haloThreshold = v }
        actual var highlight: Float get() = native.highlight; set(v) { native.highlight = v }
        actual var blendMode: BloomOptions.BlendMode 
            get() = BloomOptions.BlendMode.values()[native.blendMode.ordinal]
            set(v) { native.blendMode = FilamentView.BloomOptions.BlendMode.values()[v.ordinal] }
        actual enum class BlendMode { ADD, INTERPOLATE }
    }

    actual class FogOptions actual constructor() {
        val native = FilamentView.FogOptions()
        actual var enabled: Boolean get() = native.enabled; set(v) { native.enabled = v }
        actual var distance: Float get() = native.distance; set(v) { native.distance = v }
        actual var density: Float get() = native.density; set(v) { native.density = v }
        actual var height: Float get() = native.height; set(v) { native.height = v }
        actual var heightFalloff: Float get() = native.heightFalloff; set(v) { native.heightFalloff = v }
        actual var color: FloatArray get() = native.color; set(v) { native.color = v }
        actual var densityMap: Texture? = null
        actual var cutOffDistance: Float get() = native.cutOffDistance; set(v) { native.cutOffDistance = v }
        actual var maximumOpacity: Float get() = native.maximumOpacity; set(v) { native.maximumOpacity = v }
        actual var inScatteringStart: Float get() = native.inScatteringStart; set(v) { native.inScatteringStart = v }
        actual var inScatteringSize: Float get() = native.inScatteringSize; set(v) { native.inScatteringSize = v }
        actual var fogColorFromIbl: Boolean get() = native.fogColorFromIbl; set(v) { native.fogColorFromIbl = v }
        actual var skyColor: Texture? = null
    }

    actual class DepthOfFieldOptions actual constructor() {
        val native = FilamentView.DepthOfFieldOptions()
        actual var enabled: Boolean get() = native.enabled; set(v) { native.enabled = v }
        actual var cocScale: Float get() = native.cocScale; set(v) { native.cocScale = v }
        actual var maxApertureDiameter: Float get() = native.maxApertureDiameter; set(v) { native.maxApertureDiameter = v }
        actual var filter: Filter 
            get() = Filter.values()[native.filter.ordinal]
            set(v) { native.filter = FilamentView.DepthOfFieldOptions.Filter.values()[v.ordinal] }
        actual var nativeResolution: Boolean get() = native.nativeResolution; set(v) { native.nativeResolution = v }
        actual var foregroundRingCount: Int get() = native.foregroundRingCount; set(v) { native.foregroundRingCount = v }
        actual var backgroundRingCount: Int get() = native.backgroundRingCount; set(v) { native.backgroundRingCount = v }
        actual var fastGatherRingCount: Int get() = native.fastGatherRingCount; set(v) { native.fastGatherRingCount = v }
        actual var maxForegroundCOC: Int get() = native.maxForegroundCOC; set(v) { native.maxForegroundCOC = v }
        actual var maxBackgroundCOC: Int get() = native.maxBackgroundCOC; set(v) { native.maxBackgroundCOC = v }
        actual enum class Filter { NONE, MEDIAN, GAUSSIAN }
    }

    actual class VignetteOptions actual constructor() {
        val native = FilamentView.VignetteOptions()
        actual var enabled: Boolean get() = native.enabled; set(v) { native.enabled = v }
        actual var midPoint: Float get() = native.midPoint; set(v) { native.midPoint = v }
        actual var roundness: Float get() = native.roundness; set(v) { native.roundness = v }
        actual var feather: Float get() = native.feather; set(v) { native.feather = v }
        actual var color: FloatArray get() = native.color; set(v) { native.color = v }
    }

    actual class AmbientOcclusionOptions actual constructor() {
        val native = FilamentView.AmbientOcclusionOptions()
        actual var radius: Float get() = native.radius; set(v) { native.radius = v }
        actual var bias: Float get() = native.bias; set(v) { native.bias = v }
        actual var intensity: Float get() = native.intensity; set(v) { native.intensity = v }
        actual var scale: Float = 0.5f 
        actual var power: Float get() = native.power; set(v) { native.power = v }
        actual var minConeAngle: Float get() = native.minHorizonAngleRad; set(v) { native.minHorizonAngleRad = v }
        actual var quality: Quality 
            get() = Quality.values()[native.quality.ordinal]
            set(v) { native.quality = FilamentView.QualityLevel.values()[v.ordinal] }
        actual var lowPassFilter: Quality 
            get() = Quality.values()[native.lowPassFilter.ordinal]
            set(v) { native.lowPassFilter = FilamentView.QualityLevel.values()[v.ordinal] }
        actual var upsampling: Quality 
            get() = Quality.values()[native.upsampling.ordinal]
            set(v) { native.upsampling = FilamentView.QualityLevel.values()[v.ordinal] }
        actual var enabled: Boolean get() = native.enabled; set(v) { native.enabled = v }
        actual var bentNormals: Boolean get() = native.bentNormals; set(v) { native.bentNormals = v }
        actual var bilateralThreshold: Float get() = native.bilateralThreshold; set(v) { native.bilateralThreshold = v }
        actual var resolution: Float get() = native.resolution; set(v) { native.resolution = v }
        actual var ssct: Ssct = Ssct()
        actual class Ssct actual constructor() {
            actual var enabled: Boolean = false
            actual var lightConeRad: Float = 1.0f
            actual var shadowDistance: Float = 0.3f
            actual var contactDistanceMax: Float = 1.0f
            actual var intensity: Float = 0.8f
            actual var lightDirection: FloatArray = floatArrayOf(0f, -1f, 0f)
            actual var depthBias: Float = 0.01f
            actual var depthSlopeBias: Float = 0.01f
            actual var sampleCount: Int = 4
            actual var rayCount: Int = 1
        }
    }

    actual class TemporalAntiAliasingOptions actual constructor() {
        val native = FilamentView.TemporalAntiAliasingOptions()
        actual var feedback: Float get() = native.feedback; set(v) { native.feedback = v }
        actual var lodBias: Float get() = native.lodBias; set(v) { native.lodBias = v }
        actual var sharpness: Float get() = native.sharpness; set(v) { native.sharpness = v }
        actual var enabled: Boolean get() = native.enabled; set(v) { native.enabled = v }
        actual var upscaling: Float get() = native.upscaling; set(v) { native.upscaling = v }
        actual var filterHistory: Boolean get() = native.filterHistory; set(v) { native.filterHistory = v }
        actual var filterInput: Boolean get() = native.filterInput; set(v) { native.filterInput = v }
        actual var useYCoCg: Boolean get() = native.useYCoCg; set(v) { native.useYCoCg = v }
        actual var hdr: Boolean get() = native.hdr; set(v) { native.hdr = v }
        actual var boxType: Int get() = native.boxType.ordinal; set(v) { native.boxType = FilamentView.TemporalAntiAliasingOptions.BoxType.values()[v] }
        actual var boxClipping: Int get() = native.boxClipping.ordinal; set(v) { native.boxClipping = FilamentView.TemporalAntiAliasingOptions.BoxClipping.values()[v] }
        actual var jitterPattern: Int get() = native.jitterPattern.ordinal; set(v) { native.jitterPattern = FilamentView.TemporalAntiAliasingOptions.JitterPattern.values()[v] }
        actual var varianceGamma: Float get() = native.varianceGamma; set(v) { native.varianceGamma = v }
        actual var preventFlickering: Boolean get() = native.preventFlickering; set(v) { native.preventFlickering = v }
        actual var historyReprojection: Boolean get() = native.historyReprojection; set(v) { native.historyReprojection = v }
    }

    actual class ScreenSpaceReflectionsOptions actual constructor() {
        val native = FilamentView.ScreenSpaceReflectionsOptions()
        actual var enabled: Boolean get() = native.enabled; set(v) { native.enabled = v }
        actual var thickness: Float get() = native.thickness; set(v) { native.thickness = v }
        actual var bias: Float get() = native.bias; set(v) { native.bias = v }
        actual var maxDistance: Float get() = native.maxDistance; set(v) { native.maxDistance = v }
        actual var stride: Float get() = native.stride; set(v) { native.stride = v }
    }

    actual class VsmShadowOptions actual constructor() {
        val native = FilamentView.VsmShadowOptions()
        actual var anisotropy: Int get() = native.anisotropy; set(v) { native.anisotropy = v }
        actual var mipmapping: Boolean get() = native.mipmapping; set(v) { native.mipmapping = v }
        actual var msaaSamples: Int get() = native.msaaSamples; set(v) { native.msaaSamples = v }
        actual var highPrecision: Boolean get() = native.highPrecision; set(v) { native.highPrecision = v }
        actual var lightBleedReduction: Float get() = native.lightBleedReduction; set(v) { native.lightBleedReduction = v }
    }

    actual class SoftShadowOptions actual constructor() {
        val native = FilamentView.SoftShadowOptions()
        actual var penumbraScale: Float get() = native.penumbraScale; set(v) { native.penumbraScale = v }
        actual var penumbraRatioScale: Float get() = native.penumbraRatioScale; set(v) { native.penumbraRatioScale = v }
    }

    actual class GuardBandOptions actual constructor() {
        val native = FilamentView.GuardBandOptions()
        actual var enabled: Boolean get() = native.enabled; set(v) { native.enabled = v }
    }

    actual class StereoscopicOptions actual constructor() {
        val native = FilamentView.StereoscopicOptions()
        actual var enabled: Boolean get() = native.enabled; set(v) { native.enabled = v }
    }

    actual class MultiSampleAntiAliasingOptions actual constructor() {
        val native = FilamentView.MultiSampleAntiAliasingOptions()
        actual var enabled: Boolean get() = native.enabled; set(v) { native.enabled = v }
        actual var sampleCount: Int get() = native.sampleCount; set(v) { native.sampleCount = v }
        actual var customResolve: Boolean get() = native.customResolve; set(v) { native.customResolve = v }
    }

    actual fun setName(name: String) { this@View.nativeView.setName(name) }
    actual fun getName(): String? = this@View.nativeView.name

    actual fun setScene(scene: Scene?) { 
        this@View.mScene = scene
        this@View.nativeView.scene = scene?.nativeScene 
    }
    actual fun getScene(): Scene? = mScene

    actual fun setCamera(camera: Camera?) { 
        this@View.mCamera = camera
        this@View.nativeView.camera = camera?.nativeCamera 
    }
    actual fun getCamera(): Camera? = mCamera
    actual fun hasCamera(): Boolean = this@View.nativeView.hasCamera()

    actual fun setViewport(viewport: Viewport) {
        val nativeVp = FilamentViewport(viewport.left, viewport.bottom, viewport.width, viewport.height)
        this@View.nativeView.setViewport(nativeVp)
    }
    actual fun getViewport(): Viewport {
        val vp = this@View.nativeView.viewport
        return Viewport(vp.left, vp.bottom, vp.width, vp.height)
    }

    actual fun setBlendMode(blendMode: BlendMode) {
        this@View.nativeView.blendMode = FilamentView.BlendMode.values()[blendMode.ordinal]
    }
    actual fun getBlendMode(): BlendMode {
        return io.github.erkko68.filament.View.BlendMode.values()[this@View.nativeView.blendMode.ordinal]
    }

    actual fun setVisibleLayers(select: Int, values: Int) {
        this@View.nativeView.setVisibleLayers(select, values)
    }
    actual fun setLayerEnabled(layer: Int, enabled: Boolean) {
        this@View.nativeView.setLayerEnabled(layer, enabled)
    }
    actual fun getVisibleLayers(): Int = this@View.nativeView.visibleLayers

    actual fun setPostProcessingEnabled(enabled: Boolean) { this@View.nativeView.setPostProcessingEnabled(enabled) }
    actual fun isPostProcessingEnabled(): Boolean = this@View.nativeView.isPostProcessingEnabled


    actual fun setDithering(dithering: Dithering) {
        this@View.nativeView.dithering = FilamentView.Dithering.values()[dithering.ordinal]
    }
    actual fun getDithering(): Dithering {
        return io.github.erkko68.filament.View.Dithering.values()[this@View.nativeView.dithering.ordinal]
    }

    actual fun setDynamicResolutionOptions(options: DynamicResolutionOptions) {
        this@View.nativeView.setDynamicResolutionOptions(options.native)
    }
    actual fun getDynamicResolutionOptions(): DynamicResolutionOptions {
        val o = this@View.nativeView.dynamicResolutionOptions
        val kmp = DynamicResolutionOptions()
        kmp.enabled = o.enabled
        kmp.homogeneousScaling = o.homogeneousScaling
        kmp.minScale = o.minScale
        kmp.maxScale = o.maxScale
        kmp.sharpness = o.sharpness
        kmp.quality = io.github.erkko68.filament.View.Quality.values()[o.quality.ordinal]
        return kmp
    }

    actual fun setRenderQuality(renderQuality: RenderQuality) {
        this@View.nativeView.setRenderQuality(renderQuality.native)
    }
    actual fun getRenderQuality(): RenderQuality {
        val o = this@View.nativeView.renderQuality
        val kmp = RenderQuality()
        kmp.hdrColorBuffer = io.github.erkko68.filament.View.Quality.values()[o.hdrColorBuffer.ordinal]
        return kmp
    }
    
    actual fun setBloomOptions(options: BloomOptions) {
        this@View.nativeView.setBloomOptions(options.native)
    }
    actual fun getBloomOptions(): BloomOptions {
        val o = this@View.nativeView.bloomOptions
        val kmp = BloomOptions()
        kmp.enabled = o.enabled
        kmp.levels = o.levels
        kmp.resolution = o.resolution
        kmp.strength = o.strength
        kmp.threshold = o.threshold
        kmp.dirtStrength = o.dirtStrength
        kmp.lensFlare = o.lensFlare
        kmp.starburst = o.starburst
        kmp.chromaticAberration = o.chromaticAberration
        kmp.ghostCount = o.ghostCount
        kmp.ghostSpacing = o.ghostSpacing
        kmp.ghostThreshold = o.ghostThreshold
        kmp.haloRadius = o.haloRadius
        kmp.haloThickness = o.haloThickness
        kmp.haloThreshold = o.haloThreshold
        kmp.highlight = o.highlight
        kmp.blendMode = io.github.erkko68.filament.View.BloomOptions.BlendMode.values()[o.blendMode.ordinal]
        kmp.quality = io.github.erkko68.filament.View.Quality.values()[o.quality.ordinal]
        return kmp
    }

    actual fun setFogOptions(options: FogOptions) {
        this@View.nativeView.setFogOptions(options.native)
    }
    actual fun getFogOptions(): FogOptions {
        val o = this@View.nativeView.fogOptions
        val kmp = FogOptions()
        kmp.enabled = o.enabled
        kmp.distance = o.distance
        kmp.density = o.density
        kmp.height = o.height
        kmp.heightFalloff = o.heightFalloff
        kmp.color = o.color
        kmp.cutOffDistance = o.cutOffDistance
        kmp.maximumOpacity = o.maximumOpacity
        kmp.inScatteringStart = o.inScatteringStart
        kmp.inScatteringSize = o.inScatteringSize
        kmp.fogColorFromIbl = o.fogColorFromIbl
        return kmp
    }

    actual fun setDepthOfFieldOptions(options: DepthOfFieldOptions) {
        this@View.nativeView.setDepthOfFieldOptions(options.native)
    }
    actual fun getDepthOfFieldOptions(): DepthOfFieldOptions {
        val o = this@View.nativeView.depthOfFieldOptions
        val kmp = DepthOfFieldOptions()
        kmp.enabled = o.enabled
        kmp.cocScale = o.cocScale
        kmp.maxApertureDiameter = o.maxApertureDiameter
        kmp.filter = io.github.erkko68.filament.View.DepthOfFieldOptions.Filter.values()[o.filter.ordinal]
        kmp.nativeResolution = o.nativeResolution
        kmp.foregroundRingCount = o.foregroundRingCount
        kmp.backgroundRingCount = o.backgroundRingCount
        kmp.fastGatherRingCount = o.fastGatherRingCount
        kmp.maxForegroundCOC = o.maxForegroundCOC
        kmp.maxBackgroundCOC = o.maxBackgroundCOC
        return kmp
    }

    actual fun setVignetteOptions(options: VignetteOptions) {
        this@View.nativeView.setVignetteOptions(options.native)
    }
    actual fun getVignetteOptions(): VignetteOptions {
        val o = this@View.nativeView.vignetteOptions
        val kmp = VignetteOptions()
        kmp.enabled = o.enabled
        kmp.midPoint = o.midPoint
        kmp.roundness = o.roundness
        kmp.feather = o.feather
        kmp.color = o.color
        return kmp
    }

    actual fun setAmbientOcclusionOptions(options: AmbientOcclusionOptions) {
        val n = options.native
        n.radius = options.radius
        n.bias = options.bias
        n.intensity = options.intensity
        n.power = options.power
        n.minHorizonAngleRad = options.minConeAngle
        n.quality = FilamentView.QualityLevel.values()[options.quality.ordinal]
        n.lowPassFilter = FilamentView.QualityLevel.values()[options.lowPassFilter.ordinal]
        n.upsampling = FilamentView.QualityLevel.values()[options.upsampling.ordinal]
        n.enabled = options.enabled
        n.bentNormals = options.bentNormals
        n.resolution = options.resolution
        // Map flattened
        n.ssctEnabled = options.ssct.enabled
        n.ssctLightConeRad = options.ssct.lightConeRad
        n.ssctShadowDistance = options.ssct.shadowDistance
        n.ssctContactDistanceMax = options.ssct.contactDistanceMax
        n.ssctIntensity = options.ssct.intensity
        n.ssctLightDirection = options.ssct.lightDirection
        n.ssctDepthBias = options.ssct.depthBias
        n.ssctDepthSlopeBias = options.ssct.depthSlopeBias
        n.ssctSampleCount = options.ssct.sampleCount
        n.ssctRayCount = options.ssct.rayCount
        this@View.nativeView.setAmbientOcclusionOptions(n)
    }
    actual fun getAmbientOcclusionOptions(): AmbientOcclusionOptions {
        val o = this@View.nativeView.ambientOcclusionOptions
        val kmp = AmbientOcclusionOptions()
        kmp.radius = o.radius
        kmp.bias = o.bias
        kmp.intensity = o.intensity
        kmp.power = o.power
        kmp.minConeAngle = o.minHorizonAngleRad
        kmp.quality = io.github.erkko68.filament.View.Quality.values()[o.quality.ordinal]
        kmp.lowPassFilter = io.github.erkko68.filament.View.Quality.values()[o.lowPassFilter.ordinal]
        kmp.upsampling = io.github.erkko68.filament.View.Quality.values()[o.upsampling.ordinal]
        kmp.enabled = o.enabled
        kmp.bentNormals = o.bentNormals
        kmp.resolution = o.resolution
        
        val kmpSsct = io.github.erkko68.filament.View.AmbientOcclusionOptions.Ssct()
        kmpSsct.enabled = o.ssctEnabled
        kmpSsct.lightConeRad = o.ssctLightConeRad
        kmpSsct.shadowDistance = o.ssctShadowDistance
        kmpSsct.contactDistanceMax = o.ssctContactDistanceMax
        kmpSsct.intensity = o.ssctIntensity
        kmpSsct.lightDirection = o.ssctLightDirection
        kmpSsct.depthBias = o.ssctDepthBias
        kmpSsct.depthSlopeBias = o.ssctDepthSlopeBias
        kmpSsct.sampleCount = o.ssctSampleCount
        kmpSsct.rayCount = o.ssctRayCount
        kmp.ssct = kmpSsct
        
        return kmp
    }

    actual fun setTemporalAntiAliasingOptions(options: TemporalAntiAliasingOptions) {
        this@View.nativeView.setTemporalAntiAliasingOptions(options.native)
    }
    actual fun getTemporalAntiAliasingOptions(): TemporalAntiAliasingOptions {
        val o = this@View.nativeView.temporalAntiAliasingOptions
        val kmp = TemporalAntiAliasingOptions()
        kmp.enabled = o.enabled
        kmp.feedback = o.feedback
        kmp.lodBias = o.lodBias
        kmp.sharpness = o.sharpness
        kmp.upscaling = o.upscaling
        kmp.filterHistory = o.filterHistory
        kmp.filterInput = o.filterInput
        kmp.useYCoCg = o.useYCoCg
        kmp.hdr = o.hdr
        kmp.boxType = o.boxType.ordinal
        kmp.boxClipping = o.boxClipping.ordinal
        kmp.jitterPattern = o.jitterPattern.ordinal
        kmp.varianceGamma = o.varianceGamma
        kmp.preventFlickering = o.preventFlickering
        kmp.historyReprojection = o.historyReprojection
        return kmp
    }

    actual fun setScreenSpaceReflectionsOptions(options: ScreenSpaceReflectionsOptions) {
        this@View.nativeView.setScreenSpaceReflectionsOptions(options.native)
    }
    actual fun getScreenSpaceReflectionsOptions(): ScreenSpaceReflectionsOptions {
        val o = this@View.nativeView.screenSpaceReflectionsOptions
        val kmp = ScreenSpaceReflectionsOptions()
        kmp.enabled = o.enabled
        kmp.thickness = o.thickness
        kmp.bias = o.bias
        kmp.maxDistance = o.maxDistance
        kmp.stride = o.stride
        return kmp
    }

    actual fun setRenderTarget(target: RenderTarget?) {
        this@View.mRenderTarget = target
        this@View.nativeView.setRenderTarget(target?.nativeRenderTarget)
    }
    actual fun getRenderTarget(): RenderTarget? = mRenderTarget

    actual fun setShadowType(type: ShadowType) {
        this@View.mShadowType = type
        this@View.nativeView.setShadowType(FilamentView.ShadowType.values()[type.ordinal])
    }
    actual fun getShadowType(): ShadowType = this@View.mShadowType

    actual fun setVsmShadowOptions(options: VsmShadowOptions) {
        this@View.nativeView.setVsmShadowOptions(options.native)
    }
    actual fun getVsmShadowOptions(): VsmShadowOptions {
        val o = this@View.nativeView.vsmShadowOptions
        val kmp = VsmShadowOptions()
        kmp.anisotropy = o.anisotropy
        kmp.mipmapping = o.mipmapping
        kmp.msaaSamples = o.msaaSamples
        kmp.highPrecision = o.highPrecision
        kmp.lightBleedReduction = o.lightBleedReduction
        return kmp
    }
    actual fun setSoftShadowOptions(options: SoftShadowOptions) {
        this@View.nativeView.setSoftShadowOptions(options.native)
    }
    actual fun getSoftShadowOptions(): SoftShadowOptions {
        val o = this@View.nativeView.softShadowOptions
        val kmp = SoftShadowOptions()
        kmp.penumbraScale = o.penumbraScale
        kmp.penumbraRatioScale = o.penumbraRatioScale
        return kmp
    }
    actual fun setGuardBandOptions(options: GuardBandOptions) {
        this@View.nativeView.setGuardBandOptions(options.native)
    }
    actual fun getGuardBandOptions(): GuardBandOptions {
        val o = this@View.nativeView.guardBandOptions
        val kmp = GuardBandOptions()
        kmp.enabled = o.enabled
        return kmp
    }
    actual fun setStereoscopicOptions(options: StereoscopicOptions) {
        this@View.nativeView.setStereoscopicOptions(options.native)
    }
    actual fun getStereoscopicOptions(): StereoscopicOptions {
        val o = this@View.nativeView.stereoscopicOptions
        val kmp = StereoscopicOptions()
        kmp.enabled = o.enabled
        return kmp
    }
    actual fun setMultiSampleAntiAliasingOptions(options: MultiSampleAntiAliasingOptions) {
        this@View.nativeView.setMultiSampleAntiAliasingOptions(options.native)
    }
    actual fun getMultiSampleAntiAliasingOptions(): MultiSampleAntiAliasingOptions {
        val o = this@View.nativeView.multiSampleAntiAliasingOptions
        val kmp = MultiSampleAntiAliasingOptions()
        kmp.enabled = o.enabled
        kmp.sampleCount = o.sampleCount
        kmp.customResolve = o.customResolve
        return kmp
    }

    actual fun setFrustumCullingEnabled(enabled: Boolean) { this@View.nativeView.setFrustumCullingEnabled(enabled) }
    actual fun isFrustumCullingEnabled(): Boolean = this@View.nativeView.isFrustumCullingEnabled
    actual fun setShadowingEnabled(enabled: Boolean) { this@View.nativeView.setShadowingEnabled(enabled) }
    actual fun setScreenSpaceRefractionEnabled(enabled: Boolean) { this@View.nativeView.setScreenSpaceRefractionEnabled(enabled) }
    actual fun setStencilBufferEnabled(enabled: Boolean) { this@View.nativeView.setStencilBufferEnabled(enabled) }
    actual fun isStencilBufferEnabled(): Boolean = this@View.nativeView.isStencilBufferEnabled
    actual fun setFrontFaceWindingInverted(inverted: Boolean) { this@View.nativeView.setFrontFaceWindingInverted(inverted) }
    actual fun isFrontFaceWindingInverted(): Boolean = this@View.nativeView.isFrontFaceWindingInverted
    actual fun setTransparentPickingEnabled(enabled: Boolean) { this@View.nativeView.setTransparentPickingEnabled(enabled) }
    actual fun isTransparentPickingEnabled(): Boolean = this@View.nativeView.isTransparentPickingEnabled

    actual fun setMaterialGlobal(index: Int, value: FloatArray) {
        this@View.nativeView.setMaterialGlobal(index, value)
    }
    actual fun getMaterialGlobal(index: Int): FloatArray = this@View.nativeView.getMaterialGlobal(index, null)
    actual fun getFogEntity(): Int = this@View.nativeView.fogEntity
    actual fun clearFrameHistory(engine: Engine) { this@View.nativeView.clearFrameHistory(engine.nativeEngine) }

    actual fun setDynamicLightingOptions(zNear: Float, zFar: Float) {
        nativeView.setDynamicLightingOptions(zNear, zFar)
    }
}
