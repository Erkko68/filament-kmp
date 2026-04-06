package dev.filament.kmp

expect class View {
    enum class AntiAliasing { NONE, FXAA }
    enum class Dithering { NONE, TEMPORAL }
    enum class AmbientOcclusion { NONE, SSAO }
    enum class ToneMapping { LINEAR, ACES }
    enum class BlendMode { OPAQUE, TRANSLUCENT }

    class DynamicResolutionOptions() {
        var enabled: Boolean
        var homogeneousScaling: Boolean
        var minScale: Float
        var maxScale: Float
        var sharpness: Float
        var quality: Quality
        enum class Quality { LOW, MEDIUM, HIGH, ULTRA }
    }

    class RenderQuality() {
        var hdrColorBuffer: Quality
        enum class Quality { LOW, MEDIUM, HIGH, ULTRA }
    }

    // ... many more options ...

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
    fun setRenderTarget(target: RenderTarget?)
    fun getRenderTarget(): RenderTarget?
}
