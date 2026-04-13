package io.github.erkko68.filament

expect class View {
    enum class Dithering { NONE, TEMPORAL }
    enum class BlendMode { OPAQUE, TRANSLUCENT }
    enum class Quality { LOW, MEDIUM, HIGH, ULTRA }
    enum class ShadowType { PCF, VSM, DPCF, PCSS, PCFd }

    class DynamicResolutionOptions() {
        var enabled: Boolean
        var homogeneousScaling: Boolean
        var minScale: Float
        var maxScale: Float
        var sharpness: Float
        var quality: Quality
    }

    class RenderQuality() {
        var hdrColorBuffer: Quality
    }

    class BloomOptions() {
        var enabled: Boolean
        var levels: Int
        var resolution: Int
        var strength: Float
        var threshold: Boolean
        var dirt: Texture?
        var dirtStrength: Float
        var quality: Quality
        var lensFlare: Boolean
        var starburst: Boolean
        var chromaticAberration: Float
        var ghostCount: Int
        var ghostSpacing: Float
        var ghostThreshold: Float
        var haloRadius: Float
        var haloThickness: Float
        var haloThreshold: Float
        var highlight: Float
        var blendMode: BlendMode
        enum class BlendMode { ADD, INTERPOLATE }
    }

    class FogOptions() {
        var enabled: Boolean
        var distance: Float
        var density: Float
        var height: Float
        var heightFalloff: Float
        var color: FloatArray
        var densityMap: Texture?
        var cutOffDistance: Float
        var maximumOpacity: Float
        var inScatteringStart: Float
        var inScatteringSize: Float
        var fogColorFromIbl: Boolean
        var skyColor: Texture?
    }

    class DepthOfFieldOptions() {
        var enabled: Boolean
        var cocScale: Float
        var maxApertureDiameter: Float
        var filter: Filter
        var nativeResolution: Boolean
        var foregroundRingCount: Int
        var backgroundRingCount: Int
        var fastGatherRingCount: Int
        var maxForegroundCOC: Int
        var maxBackgroundCOC: Int
        enum class Filter { NONE, MEDIAN, GAUSSIAN }
    }

    class VignetteOptions() {
        var enabled: Boolean
        var midPoint: Float
        var roundness: Float
        var feather: Float
        var color: FloatArray
    }

    class AmbientOcclusionOptions() {
        var radius: Float
        var bias: Float
        var intensity: Float
        var scale: Float
        var power: Float
        var minConeAngle: Float
        var quality: Quality
        var lowPassFilter: Quality
        var upsampling: Quality
        var enabled: Boolean
        var bentNormals: Boolean
        var bilateralThreshold: Float
        var resolution: Float
        var ssct: Ssct
        class Ssct() {
            var enabled: Boolean
            var lightConeRad: Float
            var shadowDistance: Float
            var contactDistanceMax: Float
            var intensity: Float
            var lightDirection: FloatArray
            var depthBias: Float
            var depthSlopeBias: Float
            var sampleCount: Int
            var rayCount: Int
        }
    }

    class TemporalAntiAliasingOptions() {
        var feedback: Float
        var lodBias: Float
        var sharpness: Float
        var enabled: Boolean
        var upscaling: Float
        var filterHistory: Boolean
        var filterInput: Boolean
        var useYCoCg: Boolean
        var hdr: Boolean
        var boxType: Int
        var boxClipping: Int
        var jitterPattern: Int
        var varianceGamma: Float
        var preventFlickering: Boolean
        var historyReprojection: Boolean
    }

    class ScreenSpaceReflectionsOptions() {
        var enabled: Boolean
        var thickness: Float
        var bias: Float
        var maxDistance: Float
        var stride: Float
    }

    class VsmShadowOptions() {
        var anisotropy: Int
        var mipmapping: Boolean
        var msaaSamples: Int
        var highPrecision: Boolean
        var lightBleedReduction: Float
    }

    class SoftShadowOptions() {
        var penumbraScale: Float
        var penumbraRatioScale: Float
    }

    class GuardBandOptions() {
        var enabled: Boolean
    }

    class StereoscopicOptions() {
        var enabled: Boolean
    }

    class MultiSampleAntiAliasingOptions() {
        var enabled: Boolean
        var sampleCount: Int
        var customResolve: Boolean
    }

    fun setName(name: String)
    fun getName(): String?
    fun setScene(scene: Scene?)
    fun getScene(): Scene?
    fun setCamera(camera: Camera?)
    fun getCamera(): Camera?
    fun hasCamera(): Boolean
    fun setViewport(viewport: Viewport)
    fun getViewport(): Viewport
    fun setBlendMode(blendMode: BlendMode)
    fun getBlendMode(): BlendMode
    fun setVisibleLayers(select: Int, values: Int)
    fun setLayerEnabled(layer: Int, enabled: Boolean)
    fun getVisibleLayers(): Int
    fun setPostProcessingEnabled(enabled: Boolean)
    fun isPostProcessingEnabled(): Boolean
    fun setDithering(dithering: Dithering)
    fun getDithering(): Dithering
    fun setDynamicResolutionOptions(options: DynamicResolutionOptions)
    fun getDynamicResolutionOptions(): DynamicResolutionOptions
    fun setRenderQuality(renderQuality: RenderQuality)
    fun getRenderQuality(): RenderQuality
    
    fun setBloomOptions(options: BloomOptions)
    fun getBloomOptions(): BloomOptions
    fun setFogOptions(options: FogOptions)
    fun getFogOptions(): FogOptions
    fun setDepthOfFieldOptions(options: DepthOfFieldOptions)
    fun getDepthOfFieldOptions(): DepthOfFieldOptions
    fun setVignetteOptions(options: VignetteOptions)
    fun getVignetteOptions(): VignetteOptions
    fun setAmbientOcclusionOptions(options: AmbientOcclusionOptions)
    fun getAmbientOcclusionOptions(): AmbientOcclusionOptions
    fun setTemporalAntiAliasingOptions(options: TemporalAntiAliasingOptions)
    fun getTemporalAntiAliasingOptions(): TemporalAntiAliasingOptions
    fun setScreenSpaceReflectionsOptions(options: ScreenSpaceReflectionsOptions)
    fun getScreenSpaceReflectionsOptions(): ScreenSpaceReflectionsOptions

    fun setRenderTarget(target: RenderTarget?)
    fun getRenderTarget(): RenderTarget?

    fun setShadowType(type: ShadowType)
    fun getShadowType(): ShadowType
    fun setVsmShadowOptions(options: VsmShadowOptions)
    fun getVsmShadowOptions(): VsmShadowOptions
    fun setSoftShadowOptions(options: SoftShadowOptions)
    fun getSoftShadowOptions(): SoftShadowOptions
    fun setGuardBandOptions(options: GuardBandOptions)
    fun getGuardBandOptions(): GuardBandOptions
    fun setStereoscopicOptions(options: StereoscopicOptions)
    fun getStereoscopicOptions(): StereoscopicOptions
    fun setMultiSampleAntiAliasingOptions(options: MultiSampleAntiAliasingOptions)
    fun getMultiSampleAntiAliasingOptions(): MultiSampleAntiAliasingOptions

    fun setFrustumCullingEnabled(enabled: Boolean)
    fun isFrustumCullingEnabled(): Boolean
    fun setShadowingEnabled(enabled: Boolean)
    fun setScreenSpaceRefractionEnabled(enabled: Boolean)
    fun setStencilBufferEnabled(enabled: Boolean)
    fun isStencilBufferEnabled(): Boolean
    fun setFrontFaceWindingInverted(inverted: Boolean)
    fun isFrontFaceWindingInverted(): Boolean
    fun setTransparentPickingEnabled(enabled: Boolean)
    fun isTransparentPickingEnabled(): Boolean

    fun setMaterialGlobal(index: Int, value: FloatArray)
    fun getMaterialGlobal(index: Int): FloatArray
    fun getFogEntity(): Int
    fun clearFrameHistory(engine: Engine)
    
    fun setDynamicLightingOptions(zNear: Float, zFar: Float)
}

