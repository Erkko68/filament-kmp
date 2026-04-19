package io.github.erkko68.filament

import io.github.erkko68.filament.jni.View as JniView
import io.github.erkko68.filament.jni.Viewport as JniViewport

actual class View(val nativeView: JniView) {
    actual enum class Dithering {
        NONE, TEMPORAL;
        internal fun toJni() = JniView.Dithering.values()[ordinal]
    }
    actual enum class BlendMode {
        OPAQUE, TRANSLUCENT;
        internal fun toJni() = JniView.BlendMode.values()[ordinal]
    }
    actual enum class Quality {
        LOW, MEDIUM, HIGH, ULTRA;
        internal fun toJni() = JniView.QualityLevel.values()[ordinal]
    }
    actual enum class ShadowType {
        PCF, VSM, DPCF, PCSS, PCFd;
        internal fun toJni() = JniView.ShadowType.values()[Math.min(ordinal, JniView.ShadowType.values().size - 1)]
    }
    actual enum class AntiAliasing {
        NONE, FXAA;
        internal fun toJni() = JniView.AntiAliasing.values()[ordinal]
    }

    actual class PickingQueryResult actual constructor(
        actual val renderable: Int,
        actual val depth: Float,
        actual val fragCoords: FloatArray
    )

    private var mColorGrading: ColorGrading? = null

    actual class DynamicResolutionOptions actual constructor() {
        val jni = JniView.DynamicResolutionOptions()
        actual var enabled: Boolean get() = jni.enabled; set(v) { jni.enabled = v }
        actual var homogeneousScaling: Boolean get() = jni.homogeneousScaling; set(v) { jni.homogeneousScaling = v }
        actual var minScale: Float get() = jni.minScale; set(v) { jni.minScale = v }
        actual var maxScale: Float get() = jni.maxScale; set(v) { jni.maxScale = v }
        actual var sharpness: Float get() = jni.sharpness; set(v) { jni.sharpness = v }
        actual var quality: Quality 
            get() = Quality.values()[jni.quality.ordinal]
            set(v) { jni.quality = v.toJni() }
    }

    actual class RenderQuality actual constructor() {
        val jni = JniView.RenderQuality()
        actual var hdrColorBuffer: Quality 
            get() = Quality.values()[jni.hdrColorBuffer.ordinal]
            set(v) { jni.hdrColorBuffer = v.toJni() }
    }

    actual class BloomOptions actual constructor() {
        val jni = JniView.BloomOptions()
        actual var enabled: Boolean get() = jni.enabled; set(v) { jni.enabled = v }
        actual var levels: Int get() = jni.levels; set(v) { jni.levels = v }
        actual var resolution: Int get() = jni.resolution; set(v) { jni.resolution = v }
        actual var strength: Float get() = jni.strength; set(v) { jni.strength = v }
        actual var threshold: Boolean get() = jni.threshold; set(v) { jni.threshold = v }
        actual var dirt: Texture? = null // Handled manually for now
        actual var dirtStrength: Float get() = jni.dirtStrength; set(v) { jni.dirtStrength = v }
        actual var quality: Quality 
            get() = Quality.values()[jni.quality.ordinal]
            set(v) { jni.quality = v.toJni() }
        actual var lensFlare: Boolean get() = jni.lensFlare; set(v) { jni.lensFlare = v }
        actual var starburst: Boolean get() = jni.starburst; set(v) { jni.starburst = v }
        actual var chromaticAberration: Float get() = jni.chromaticAberration; set(v) { jni.chromaticAberration = v }
        actual var ghostCount: Int get() = jni.ghostCount; set(v) { jni.ghostCount = v }
        actual var ghostSpacing: Float get() = jni.ghostSpacing; set(v) { jni.ghostSpacing = v }
        actual var ghostThreshold: Float get() = jni.ghostThreshold; set(v) { jni.ghostThreshold = v }
        actual var haloRadius: Float get() = jni.haloRadius; set(v) { jni.haloRadius = v }
        actual var haloThickness: Float get() = jni.haloThickness; set(v) { jni.haloThickness = v }
        actual var haloThreshold: Float get() = jni.haloThreshold; set(v) { jni.haloThreshold = v }
        actual var highlight: Float get() = jni.highlight; set(v) { jni.highlight = v }
        actual var blendMode: BloomOptions.BlendMode 
            get() = BloomOptions.BlendMode.values()[jni.blendMode.ordinal]
            set(v) { jni.blendMode = JniView.BloomOptions.BlendMode.values()[v.ordinal] }
        actual enum class BlendMode { ADD, INTERPOLATE }
    }

    actual class FogOptions actual constructor() {
        val jni = JniView.FogOptions()
        actual var enabled: Boolean get() = jni.enabled; set(v) { jni.enabled = v }
        actual var distance: Float get() = jni.distance; set(v) { jni.distance = v }
        actual var density: Float get() = jni.density; set(v) { jni.density = v }
        actual var height: Float get() = jni.height; set(v) { jni.height = v }
        actual var heightFalloff: Float get() = jni.heightFalloff; set(v) { jni.heightFalloff = v }
        actual var color: FloatArray get() = jni.color; set(v) { jni.color = v }
        actual var densityMap: Texture? = null
        actual var cutOffDistance: Float get() = jni.cutOffDistance; set(v) { jni.cutOffDistance = v }
        actual var maximumOpacity: Float get() = jni.maximumOpacity; set(v) { jni.maximumOpacity = v }
        actual var inScatteringStart: Float get() = jni.inScatteringStart; set(v) { jni.inScatteringStart = v }
        actual var inScatteringSize: Float get() = jni.inScatteringSize; set(v) { jni.inScatteringSize = v }
        actual var fogColorFromIbl: Boolean get() = jni.fogColorFromIbl; set(v) { jni.fogColorFromIbl = v }
        actual var skyColor: Texture? = null
    }

    actual class DepthOfFieldOptions actual constructor() {
        val jni = JniView.DepthOfFieldOptions()
        actual var enabled: Boolean get() = jni.enabled; set(v) { jni.enabled = v }
        actual var cocScale: Float get() = jni.cocScale; set(v) { jni.cocScale = v }
        actual var maxApertureDiameter: Float get() = jni.maxApertureDiameter; set(v) { jni.maxApertureDiameter = v }
        actual var filter: Filter 
            get() = Filter.values()[jni.filter.ordinal]
            set(v) { jni.filter = JniView.DepthOfFieldOptions.Filter.values()[Math.min(v.ordinal, JniView.DepthOfFieldOptions.Filter.values().size - 1)] }
        actual var nativeResolution: Boolean get() = jni.nativeResolution; set(v) { jni.nativeResolution = v }
        actual var foregroundRingCount: Int get() = jni.foregroundRingCount; set(v) { jni.foregroundRingCount = v }
        actual var backgroundRingCount: Int get() = jni.backgroundRingCount; set(v) { jni.backgroundRingCount = v }
        actual var fastGatherRingCount: Int get() = jni.fastGatherRingCount; set(v) { jni.fastGatherRingCount = v }
        actual var maxForegroundCOC: Int get() = jni.maxForegroundCOC; set(v) { jni.maxForegroundCOC = v }
        actual var maxBackgroundCOC: Int get() = jni.maxBackgroundCOC; set(v) { jni.maxBackgroundCOC = v }
        actual enum class Filter { NONE, MEDIAN, GAUSSIAN }
    }

    actual class VignetteOptions actual constructor() {
        val jni = JniView.VignetteOptions()
        actual var enabled: Boolean get() = jni.enabled; set(v) { jni.enabled = v }
        actual var midPoint: Float get() = jni.midPoint; set(v) { jni.midPoint = v }
        actual var roundness: Float get() = jni.roundness; set(v) { jni.roundness = v }
        actual var feather: Float get() = jni.feather; set(v) { jni.feather = v }
        actual var color: FloatArray get() = jni.color; set(v) { jni.color = v }
    }

    actual class AmbientOcclusionOptions actual constructor() {
        val jni = JniView.AmbientOcclusionOptions()
        actual var radius: Float get() = jni.radius; set(v) { jni.radius = v }
        actual var bias: Float get() = jni.bias; set(v) { jni.bias = v }
        actual var intensity: Float get() = jni.intensity; set(v) { jni.intensity = v }
        actual var scale: Float = 0.5f
        actual var power: Float get() = jni.power; set(v) { jni.power = v }
        actual var minConeAngle: Float get() = jni.minHorizonAngleRad; set(v) { jni.minHorizonAngleRad = v }
        actual var quality: Quality 
            get() = Quality.values()[jni.quality.ordinal]
            set(v) { jni.quality = v.toJni() }
        actual var lowPassFilter: Quality 
            get() = Quality.values()[jni.lowPassFilter.ordinal]
            set(v) { jni.lowPassFilter = v.toJni() }
        actual var upsampling: Quality 
            get() = Quality.values()[jni.upsampling.ordinal]
            set(v) { jni.upsampling = v.toJni() }
        actual var enabled: Boolean get() = jni.enabled; set(v) { jni.enabled = v }
        actual var bentNormals: Boolean get() = jni.bentNormals; set(v) { jni.bentNormals = v }
        actual var bilateralThreshold: Float get() = jni.bilateralThreshold; set(v) { jni.bilateralThreshold = v }
        actual var resolution: Float get() = jni.resolution; set(v) { jni.resolution = v }
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
        val jni = JniView.TemporalAntiAliasingOptions()
        actual var feedback: Float get() = jni.feedback; set(v) { jni.feedback = v }
        actual var lodBias: Float get() = jni.lodBias; set(v) { jni.lodBias = v }
        actual var sharpness: Float get() = jni.sharpness; set(v) { jni.sharpness = v }
        actual var enabled: Boolean get() = jni.enabled; set(v) { jni.enabled = v }
        actual var upscaling: Float get() = jni.upscaling; set(v) { jni.upscaling = v }
        actual var filterHistory: Boolean get() = jni.filterHistory; set(v) { jni.filterHistory = v }
        actual var filterInput: Boolean get() = jni.filterInput; set(v) { jni.filterInput = v }
        actual var useYCoCg: Boolean get() = jni.useYCoCg; set(v) { jni.useYCoCg = v }
        actual var hdr: Boolean get() = jni.hdr; set(v) { jni.hdr = v }
        actual var boxType: Int get() = jni.boxType.ordinal; set(v) { jni.boxType = JniView.TemporalAntiAliasingOptions.BoxType.values()[v] }
        actual var boxClipping: Int get() = jni.boxClipping.ordinal; set(v) { jni.boxClipping = JniView.TemporalAntiAliasingOptions.BoxClipping.values()[v] }
        actual var jitterPattern: Int get() = jni.jitterPattern.ordinal; set(v) { jni.jitterPattern = JniView.TemporalAntiAliasingOptions.JitterPattern.values()[v] }
        actual var varianceGamma: Float get() = jni.varianceGamma; set(v) { jni.varianceGamma = v }
        actual var preventFlickering: Boolean get() = jni.preventFlickering; set(v) { jni.preventFlickering = v }
        actual var historyReprojection: Boolean get() = jni.historyReprojection; set(v) { jni.historyReprojection = v }
    }

    actual class ScreenSpaceReflectionsOptions actual constructor() {
        val jni = JniView.ScreenSpaceReflectionsOptions()
        actual var enabled: Boolean get() = jni.enabled; set(v) { jni.enabled = v }
        actual var thickness: Float get() = jni.thickness; set(v) { jni.thickness = v }
        actual var bias: Float get() = jni.bias; set(v) { jni.bias = v }
        actual var maxDistance: Float get() = jni.maxDistance; set(v) { jni.maxDistance = v }
        actual var stride: Float get() = jni.stride; set(v) { jni.stride = v }
    }

    actual class VsmShadowOptions actual constructor() {
        val jni = JniView.VsmShadowOptions()
        actual var anisotropy: Int get() = jni.anisotropy; set(v) { jni.anisotropy = v }
        actual var mipmapping: Boolean get() = jni.mipmapping; set(v) { jni.mipmapping = v }
        actual var msaaSamples: Int get() = 0; set(v) {} // Missing in JNI
        actual var highPrecision: Boolean get() = jni.highPrecision; set(v) { jni.highPrecision = v }
        actual var lightBleedReduction: Float get() = jni.lightBleedReduction; set(v) { jni.lightBleedReduction = v }
    }

    actual class SoftShadowOptions actual constructor() {
        val jni = JniView.SoftShadowOptions()
        actual var penumbraScale: Float get() = jni.penumbraScale; set(v) { jni.penumbraScale = v }
        actual var penumbraRatioScale: Float get() = jni.penumbraRatioScale; set(v) { jni.penumbraRatioScale = v }
    }

    actual class GuardBandOptions actual constructor() {
        val jni = JniView.GuardBandOptions()
        actual var enabled: Boolean get() = jni.enabled; set(v) { jni.enabled = v }
    }

    actual class StereoscopicOptions actual constructor() {
        val jni = JniView.StereoscopicOptions()
        actual var enabled: Boolean get() = jni.enabled; set(v) { jni.enabled = v }
    }

    actual class MultiSampleAntiAliasingOptions actual constructor() {
        val jni = JniView.MultiSampleAntiAliasingOptions()
        actual var enabled: Boolean get() = jni.enabled; set(v) { jni.enabled = v }
        actual var sampleCount: Int get() = jni.sampleCount; set(v) { jni.sampleCount = v }
        actual var customResolve: Boolean get() = jni.customResolve; set(v) { jni.customResolve = v }
    }

    actual fun setName(name: String) : Unit { nativeView.setName(name) }
    actual fun getName(): String? = nativeView.getName()
    actual fun setScene(scene: Scene?) : Unit { nativeView.setScene(scene?.nativeScene) }
    actual fun getScene(): Scene? = null // TODO: Track scene
    actual fun setCamera(camera: Camera?) : Unit { nativeView.setCamera(camera?.nativeCamera) }
    actual fun getCamera(): Camera? = null // TODO: Track camera
    actual fun hasCamera(): Boolean = nativeView.hasCamera()
    
    actual fun setViewport(viewport: Viewport) : Unit { nativeView.setViewport(JniViewport(viewport.left, viewport.bottom, viewport.width, viewport.height)) }

    actual fun setBlendMode(blendMode: BlendMode) : Unit { nativeView.setBlendMode(blendMode.toJni()) }
    actual fun getBlendMode(): BlendMode = nativeView.getBlendMode()?.let { BlendMode.values()[it.ordinal] } ?: BlendMode.OPAQUE

    actual fun setVisibleLayers(select: Int, values: Int) : Unit { nativeView.setVisibleLayers(select, values) }
    actual fun setLayerEnabled(layer: Int, enabled: Boolean) : Unit { nativeView.setLayerEnabled(layer, enabled) }
    actual fun getVisibleLayers(): Int = nativeView.getVisibleLayers()

    actual fun setPostProcessingEnabled(enabled: Boolean) : Unit { nativeView.setPostProcessingEnabled(enabled) }
    actual fun isPostProcessingEnabled(): Boolean = nativeView.isPostProcessingEnabled()
    
    actual fun setDithering(dithering: Dithering) : Unit { nativeView.setDithering(dithering.toJni()) }
    actual fun getDithering(): Dithering = Dithering.values()[nativeView.getDithering().ordinal]

    actual fun setDynamicResolutionOptions(options: DynamicResolutionOptions) : Unit { nativeView.setDynamicResolutionOptions(options.jni) }
    actual fun getDynamicResolutionOptions(): DynamicResolutionOptions {
        val o = nativeView.getDynamicResolutionOptions()
        val k = DynamicResolutionOptions()
        k.enabled = o.enabled
        k.homogeneousScaling = o.homogeneousScaling
        k.minScale = o.minScale
        k.maxScale = o.maxScale
        k.sharpness = o.sharpness
        k.quality = Quality.values()[o.quality.ordinal]
        return k
    }

    actual fun setRenderQuality(renderQuality: RenderQuality) : Unit { nativeView.setRenderQuality(renderQuality.jni) }
    actual fun getRenderQuality(): RenderQuality {
        val o = nativeView.getRenderQuality()
        val k = RenderQuality()
        k.hdrColorBuffer = Quality.values()[o.hdrColorBuffer.ordinal]
        return k
    }
    
    actual fun setBloomOptions(options: BloomOptions) : Unit { nativeView.setBloomOptions(options.jni) }
    actual fun getBloomOptions(): BloomOptions {
        val o = nativeView.getBloomOptions()
        val k = BloomOptions()
        k.enabled = o.enabled
        k.levels = o.levels
        k.resolution = o.resolution
        k.strength = o.strength
        k.threshold = o.threshold
        k.dirtStrength = o.dirtStrength
        k.quality = Quality.values()[o.quality.ordinal]
        k.lensFlare = o.lensFlare
        k.starburst = o.starburst
        k.chromaticAberration = o.chromaticAberration
        k.ghostCount = o.ghostCount
        k.ghostSpacing = o.ghostSpacing
        k.ghostThreshold = o.ghostThreshold
        k.haloRadius = o.haloRadius
        k.haloThickness = o.haloThickness
        k.haloThreshold = o.haloThreshold
        k.highlight = o.highlight
        k.blendMode = BloomOptions.BlendMode.values()[o.blendMode.ordinal]
        return k
    }

    actual fun setFogOptions(options: FogOptions) : Unit { nativeView.setFogOptions(options.jni) }
    actual fun getFogOptions(): FogOptions {
        val o = nativeView.getFogOptions()
        val k = FogOptions()
        k.enabled = o.enabled
        k.distance = o.distance
        k.maximumOpacity = o.maximumOpacity
        k.height = o.height
        k.heightFalloff = o.heightFalloff
        k.cutOffDistance = o.cutOffDistance
        k.color = o.color
        k.density = o.density
        k.inScatteringStart = o.inScatteringStart
        k.inScatteringSize = o.inScatteringSize
        k.fogColorFromIbl = o.fogColorFromIbl
        return k
    }

    actual fun setDepthOfFieldOptions(options: DepthOfFieldOptions) : Unit { nativeView.setDepthOfFieldOptions(options.jni) }
    actual fun getDepthOfFieldOptions(): DepthOfFieldOptions {
        val o = nativeView.getDepthOfFieldOptions()
        val k = DepthOfFieldOptions()
        k.enabled = o.enabled
        k.cocScale = o.cocScale
        k.maxApertureDiameter = o.maxApertureDiameter
        k.filter = DepthOfFieldOptions.Filter.values()[Math.min(o.filter.ordinal, DepthOfFieldOptions.Filter.values().size - 1)]
        k.nativeResolution = o.nativeResolution
        k.foregroundRingCount = o.foregroundRingCount
        k.backgroundRingCount = o.backgroundRingCount
        k.fastGatherRingCount = o.fastGatherRingCount
        k.maxForegroundCOC = o.maxForegroundCOC
        k.maxBackgroundCOC = o.maxBackgroundCOC
        return k
    }

    actual fun setVignetteOptions(options: VignetteOptions) : Unit { nativeView.setVignetteOptions(options.jni) }
    actual fun getVignetteOptions(): VignetteOptions {
        val o = nativeView.getVignetteOptions()
        val k = VignetteOptions()
        k.enabled = o.enabled
        k.midPoint = o.midPoint
        k.roundness = o.roundness
        k.feather = o.feather
        k.color = o.color
        return k
    }

    actual fun setAmbientOcclusionOptions(options: AmbientOcclusionOptions) : Unit {
        // Map ssct manually
        options.jni.ssctEnabled = options.ssct.enabled
        options.jni.ssctLightConeRad = options.ssct.lightConeRad
        options.jni.ssctShadowDistance = options.ssct.shadowDistance
        options.jni.ssctContactDistanceMax = options.ssct.contactDistanceMax
        options.jni.ssctIntensity = options.ssct.intensity
        options.jni.ssctLightDirection = options.ssct.lightDirection
        options.jni.ssctDepthBias = options.ssct.depthBias
        options.jni.ssctDepthSlopeBias = options.ssct.depthSlopeBias
        options.jni.ssctSampleCount = options.ssct.sampleCount
        options.jni.ssctRayCount = options.ssct.rayCount
        nativeView.setAmbientOcclusionOptions(options.jni)
    }

    actual fun getAmbientOcclusionOptions(): AmbientOcclusionOptions {
        val o = nativeView.getAmbientOcclusionOptions()
        val k = AmbientOcclusionOptions()
        k.radius = o.radius
        k.bias = o.bias
        k.intensity = o.intensity
        k.power = o.power
        k.minConeAngle = o.minHorizonAngleRad
        k.quality = Quality.values()[o.quality.ordinal]
        k.lowPassFilter = Quality.values()[o.lowPassFilter.ordinal]
        k.upsampling = Quality.values()[o.upsampling.ordinal]
        k.enabled = o.enabled
        k.bentNormals = o.bentNormals
        k.bilateralThreshold = o.bilateralThreshold
        k.resolution = o.resolution
        k.ssct.enabled = o.ssctEnabled
        k.ssct.lightConeRad = o.ssctLightConeRad
        k.ssct.shadowDistance = o.ssctShadowDistance
        k.ssct.contactDistanceMax = o.ssctContactDistanceMax
        k.ssct.intensity = o.ssctIntensity
        k.ssct.lightDirection = o.ssctLightDirection
        k.ssct.depthBias = o.ssctDepthBias
        k.ssct.depthSlopeBias = o.ssctDepthSlopeBias
        k.ssct.sampleCount = o.ssctSampleCount
        k.ssct.rayCount = o.ssctRayCount
        return k
    }

    actual fun setTemporalAntiAliasingOptions(options: TemporalAntiAliasingOptions) : Unit { nativeView.setTemporalAntiAliasingOptions(options.jni) }
    actual fun getTemporalAntiAliasingOptions(): TemporalAntiAliasingOptions {
        val o = nativeView.getTemporalAntiAliasingOptions()
        val k = TemporalAntiAliasingOptions()
        k.enabled = o.enabled
        k.feedback = o.feedback
        k.lodBias = o.lodBias
        k.sharpness = o.sharpness
        k.upscaling = o.upscaling
        k.filterHistory = o.filterHistory
        k.filterInput = o.filterInput
        k.useYCoCg = o.useYCoCg
        k.hdr = o.hdr
        k.boxType = o.boxType.ordinal
        k.boxClipping = o.boxClipping.ordinal
        k.jitterPattern = o.jitterPattern.ordinal
        k.varianceGamma = o.varianceGamma
        k.preventFlickering = o.preventFlickering
        k.historyReprojection = o.historyReprojection
        return k
    }

    actual fun setScreenSpaceReflectionsOptions(options: ScreenSpaceReflectionsOptions) : Unit { nativeView.setScreenSpaceReflectionsOptions(options.jni) }
    actual fun getScreenSpaceReflectionsOptions(): ScreenSpaceReflectionsOptions {
        val o = nativeView.getScreenSpaceReflectionsOptions()
        val k = ScreenSpaceReflectionsOptions()
        k.enabled = o.enabled
        k.thickness = o.thickness
        k.bias = o.bias
        k.maxDistance = o.maxDistance
        k.stride = o.stride
        return k
    }

    actual fun setRenderTarget(target: RenderTarget?) : Unit { nativeView.setRenderTarget(target?.nativeRenderTarget) }
    actual fun getRenderTarget(): RenderTarget? = nativeView.getRenderTarget()?.let { RenderTarget(it) }

    actual fun setShadowType(type: ShadowType) : Unit { nativeView.setShadowType(type.toJni()) }
    actual fun getShadowType(): ShadowType = ShadowType.values()[Math.min(nativeView.getShadowType().ordinal, ShadowType.values().size - 1)]
    
    actual fun setVsmShadowOptions(options: VsmShadowOptions) : Unit { nativeView.setVsmShadowOptions(options.jni) }
    actual fun getVsmShadowOptions(): VsmShadowOptions {
        val o = nativeView.getVsmShadowOptions()
        val k = VsmShadowOptions()
        k.anisotropy = o.anisotropy
        k.mipmapping = o.mipmapping
        k.highPrecision = o.highPrecision
        // k.msaaSamples missing?
        k.lightBleedReduction = o.lightBleedReduction
        return k
    }

    actual fun setSoftShadowOptions(options: SoftShadowOptions) : Unit { nativeView.setSoftShadowOptions(options.jni) }
    actual fun getSoftShadowOptions(): SoftShadowOptions {
        val o = nativeView.getSoftShadowOptions()
        val k = SoftShadowOptions()
        k.penumbraScale = o.penumbraScale
        k.penumbraRatioScale = o.penumbraRatioScale
        return k
    }

    actual fun setGuardBandOptions(options: GuardBandOptions) : Unit { nativeView.setGuardBandOptions(options.jni) }
    actual fun getGuardBandOptions(): GuardBandOptions {
        val o = nativeView.getGuardBandOptions()
        val k = GuardBandOptions()
        k.enabled = o.enabled
        return k
    }

    actual fun setStereoscopicOptions(options: StereoscopicOptions) : Unit { nativeView.setStereoscopicOptions(options.jni) }
    actual fun getStereoscopicOptions(): StereoscopicOptions {
        val o = nativeView.getStereoscopicOptions()
        val k = StereoscopicOptions()
        k.enabled = o.enabled
        return k
    }

    actual fun setMultiSampleAntiAliasingOptions(options: MultiSampleAntiAliasingOptions) : Unit { nativeView.setMultiSampleAntiAliasingOptions(options.jni) }
    actual fun getMultiSampleAntiAliasingOptions(): MultiSampleAntiAliasingOptions {
        val o = nativeView.getMultiSampleAntiAliasingOptions()
        val k = MultiSampleAntiAliasingOptions()
        k.enabled = o.enabled
        k.sampleCount = o.sampleCount
        k.customResolve = o.customResolve
        return k
    }

    actual fun setFrustumCullingEnabled(enabled: Boolean) : Unit { nativeView.setFrustumCullingEnabled(enabled) }
    actual fun isFrustumCullingEnabled(): Boolean = nativeView.isFrustumCullingEnabled()
    actual fun setShadowingEnabled(enabled: Boolean) : Unit { nativeView.setShadowingEnabled(enabled) }
    actual fun setScreenSpaceRefractionEnabled(enabled: Boolean) : Unit { nativeView.setScreenSpaceRefractionEnabled(enabled) }
    actual fun setStencilBufferEnabled(enabled: Boolean) : Unit { nativeView.setStencilBufferEnabled(enabled) }
    actual fun isStencilBufferEnabled(): Boolean = nativeView.isStencilBufferEnabled()
    actual fun setFrontFaceWindingInverted(inverted: Boolean) : Unit { nativeView.setFrontFaceWindingInverted(inverted) }
    actual fun isFrontFaceWindingInverted(): Boolean = nativeView.isFrontFaceWindingInverted()
    actual fun setTransparentPickingEnabled(enabled: Boolean) : Unit { nativeView.setTransparentPickingEnabled(enabled) }
    actual fun isTransparentPickingEnabled(): Boolean = nativeView.isTransparentPickingEnabled()

    actual fun setMaterialGlobal(index: Int, value: FloatArray) : Unit { nativeView.setMaterialGlobal(index, value) }
    actual fun getMaterialGlobal(index: Int): FloatArray {
        val out = FloatArray(4)
        nativeView.getMaterialGlobal(index, out)
        return out
    }
    actual fun getFogEntity(): Int = nativeView.getFogEntity()
    actual fun clearFrameHistory(engine: Engine) : Unit { nativeView.clearFrameHistory(engine.nativeEngine) }
    actual fun setDynamicLightingOptions(zNear: Float, zFar: Float) : Unit { nativeView.setDynamicLightingOptions(zNear, zFar) }

    actual fun setAntiAliasing(type: AntiAliasing) : Unit { nativeView.setAntiAliasing(type.toJni()) }
    actual fun getAntiAliasing(): AntiAliasing = AntiAliasing.values()[nativeView.getAntiAliasing().ordinal]

    actual fun setColorGrading(colorGrading: ColorGrading?) {
        mColorGrading = colorGrading
        nativeView.setColorGrading(colorGrading?.nativeColorGrading)
    }
    actual fun getColorGrading(): ColorGrading? = mColorGrading

    actual fun pick(x: Int, y: Int, callback: (PickingQueryResult) -> Unit) {
        nativeView.pick(x, y, null) { r ->
            callback(PickingQueryResult(r.renderable, r.depth, r.fragCoords.copyOf()))
        }
    }

    actual fun getViewport(): Viewport {
        val v = nativeView.getViewport()
        return Viewport(v.left, v.bottom, v.width, v.height)
    }
}
