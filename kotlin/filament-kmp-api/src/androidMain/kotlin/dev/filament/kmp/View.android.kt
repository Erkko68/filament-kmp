package dev.filament.kmp

actual class View internal constructor(val nativeView: com.google.android.filament.View) {
    actual enum class AntiAliasing { NONE, FXAA }
    actual enum class Dithering { NONE, TEMPORAL }
    actual enum class AmbientOcclusion { NONE, SSAO }
    actual enum class ToneMapping { LINEAR, ACES }
    actual enum class BlendMode { OPAQUE, TRANSLUCENT }

    actual class DynamicResolutionOptions actual constructor() {
        val nativeOptions = com.google.android.filament.View.DynamicResolutionOptions()
        actual var enabled: Boolean
            get() = nativeOptions.enabled
            set(value) { nativeOptions.enabled = value }
        actual var homogeneousScaling: Boolean
            get() = nativeOptions.homogeneousScaling
            set(value) { nativeOptions.homogeneousScaling = value }
        actual var minScale: Float
            get() = nativeOptions.minScale
            set(value) { nativeOptions.minScale = value }
        actual var maxScale: Float
            get() = nativeOptions.maxScale
            set(value) { nativeOptions.maxScale = value }
        actual var sharpness: Float
            get() = nativeOptions.sharpness
            set(value) { nativeOptions.sharpness = value }
        actual var quality: Quality
            get() = DynamicResolutionOptions.Quality.values()[nativeOptions.quality.ordinal]
            set(value) { nativeOptions.quality = com.google.android.filament.View.QualityLevel.values()[value.ordinal] }
            
        actual enum class Quality { LOW, MEDIUM, HIGH, ULTRA }
    }

    actual class RenderQuality actual constructor() {
        val nativeQuality = com.google.android.filament.View.RenderQuality()
        actual var hdrColorBuffer: Quality
            get() = RenderQuality.Quality.values()[nativeQuality.hdrColorBuffer.ordinal]
            set(value) { nativeQuality.hdrColorBuffer = com.google.android.filament.View.QualityLevel.values()[value.ordinal] }
            
        actual enum class Quality { LOW, MEDIUM, HIGH, ULTRA }
    }

    private var scene: Scene? = null
    private var camera: Camera? = null
    private var renderTarget: RenderTarget? = null

    actual fun setName(name: String) { nativeView.setName(name) }
    actual fun getName(): String? = nativeView.name

    actual fun setScene(scene: Scene?) {
        this.scene = scene
        nativeView.setScene(scene?.nativeScene)
    }
    actual fun getScene(): Scene? = scene

    actual fun setCamera(camera: Camera?) {
        this.camera = camera
        nativeView.setCamera(camera?.nativeCamera)
    }
    actual fun getCamera(): Camera? = camera

    actual fun setViewport(viewport: Viewport) { nativeView.setViewport(viewport.nativeViewport) }
    actual fun getViewport(): Viewport = Viewport(nativeView.viewport.left, nativeView.viewport.bottom, nativeView.viewport.width, nativeView.viewport.height)

    actual fun setBlendMode(blendMode: BlendMode) {
        nativeView.blendMode = com.google.android.filament.View.BlendMode.values()[blendMode.ordinal]
    }
    actual fun getBlendMode(): BlendMode = BlendMode.values()[nativeView.blendMode.ordinal]

    actual fun setVisibleLayers(select: Int, values: Int) = nativeView.setVisibleLayers(select, values)
    actual fun getVisibleLayers(): Int = nativeView.visibleLayers

    actual fun setShadowingEnabled(enabled: Boolean) { nativeView.setShadowingEnabled(enabled) }
    actual fun setPostProcessingEnabled(enabled: Boolean) { nativeView.setPostProcessingEnabled(enabled) }
    actual fun isPostProcessingEnabled(): Boolean = nativeView.isPostProcessingEnabled

    actual fun setAntiAliasing(type: AntiAliasing) {
        nativeView.antiAliasing = com.google.android.filament.View.AntiAliasing.values()[type.ordinal]
    }
    actual fun getAntiAliasing(): AntiAliasing = AntiAliasing.values()[nativeView.antiAliasing.ordinal]

    actual fun setDithering(dithering: Dithering) {
        nativeView.dithering = com.google.android.filament.View.Dithering.values()[dithering.ordinal]
    }
    actual fun getDithering(): Dithering = Dithering.values()[nativeView.dithering.ordinal]

    actual fun setDynamicResolutionOptions(options: DynamicResolutionOptions) = nativeView.setDynamicResolutionOptions(options.nativeOptions)
    actual fun getDynamicResolutionOptions(): DynamicResolutionOptions = DynamicResolutionOptions().apply {
        val o = nativeView.dynamicResolutionOptions
        enabled = o.enabled
        homogeneousScaling = o.homogeneousScaling
        minScale = o.minScale
        maxScale = o.maxScale
        sharpness = o.sharpness
        quality = DynamicResolutionOptions.Quality.values()[o.quality.ordinal]
    }

    actual fun setRenderQuality(renderQuality: RenderQuality) = nativeView.setRenderQuality(renderQuality.nativeQuality)
    actual fun getRenderQuality(): RenderQuality = RenderQuality().apply {
        val q = nativeView.renderQuality
        hdrColorBuffer = RenderQuality.Quality.values()[q.hdrColorBuffer.ordinal]
    }

    actual fun setRenderTarget(target: RenderTarget?) {
        this.renderTarget = target
        nativeView.setRenderTarget(target?.nativeRenderTarget)
    }
    actual fun getRenderTarget(): RenderTarget? = renderTarget
}
