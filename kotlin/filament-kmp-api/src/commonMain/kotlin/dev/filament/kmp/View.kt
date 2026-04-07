package dev.filament.kmp

expect class View {
    enum class AntiAliasing { NONE, FXAA }
    enum class Dithering { NONE, TEMPORAL }
    enum class AmbientOcclusion { NONE, SSAO }
    enum class ToneMapping { LINEAR, ACES }
    enum class BlendMode { OPAQUE, TRANSLUCENT }
    enum class Quality { LOW, MEDIUM, HIGH, ULTRA }

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
    }

    class FogOptions() {
        var enabled: Boolean
        var distance: Float
        var density: Float
        var height: Float
        var heightFalloff: Float
        var color: FloatArray
        var densityMap: Texture?
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
    }

    class TemporalAntiAliasingOptions() {
        var filterWidth: Float
        var feedback: Float
        var enabled: Boolean
    }

    class ScreenSpaceReflectionsOptions() {
        var enabled: Boolean
        var thickness: Float
        var bias: Float
        var maxDistance: Float
        var stride: Float
    }

    fun setName(name: String)
    fun getName(): String?
    fun setScene(scene: Scene?)
    fun getScene(): Scene?
    fun setCamera(camera: Camera?)
    fun getCamera(): Camera?
    fun setViewport(viewport: Viewport)
    fun getViewport(): Viewport
    fun setBlendMode(blendMode: BlendMode)
    fun getBlendMode(): BlendMode
    fun setVisibleLayers(select: Int, values: Int)
    fun getVisibleLayers(): Int
    fun setShadowingEnabled(enabled: Boolean)
    fun setPostProcessingEnabled(enabled: Boolean)
    fun isPostProcessingEnabled(): Boolean
    fun setAntiAliasing(type: AntiAliasing)
    fun getAntiAliasing(): AntiAliasing
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
}
