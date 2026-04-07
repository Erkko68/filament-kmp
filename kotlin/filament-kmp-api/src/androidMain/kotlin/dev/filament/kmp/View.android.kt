package dev.filament.kmp

private fun View.Quality.toAndroid() = com.google.android.filament.View.QualityLevel.values()[ordinal]
private fun com.google.android.filament.View.QualityLevel.toKmp() = View.Quality.values()[ordinal]

actual class View internal constructor(val nativeView: com.google.android.filament.View) {
    actual enum class AntiAliasing { NONE, FXAA }
    actual enum class Dithering { NONE, TEMPORAL }
    actual enum class AmbientOcclusion { NONE, SSAO }
    actual enum class ToneMapping { LINEAR, ACES }
    actual enum class BlendMode { OPAQUE, TRANSLUCENT }
    actual enum class Quality { LOW, MEDIUM, HIGH, ULTRA }

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
            get() = Quality.values()[nativeOptions.quality.ordinal]
            set(value) { nativeOptions.quality = value.toAndroid() }
    }
 
    actual class RenderQuality actual constructor() {
        val nativeQuality = com.google.android.filament.View.RenderQuality()
        actual var hdrColorBuffer: Quality
            get() = Quality.values()[nativeQuality.hdrColorBuffer.ordinal]
            set(value) { nativeQuality.hdrColorBuffer = value.toAndroid() }
    }

    actual class BloomOptions actual constructor() {
        val native = com.google.android.filament.View.BloomOptions()
        actual var enabled: Boolean get() = native.enabled; set(v) { native.enabled = v }
        actual var levels: Int get() = native.levels; set(v) { native.levels = v }
        actual var resolution: Int get() = native.resolution; set(v) { native.resolution = v }
        actual var strength: Float get() = native.strength; set(v) { native.strength = v }
        actual var threshold: Boolean get() = native.threshold; set(v) { native.threshold = v }
        actual var dirt: Texture? = null; set(v) { field = v; native.dirt = v?.nativeTexture }
        actual var dirtStrength: Float get() = native.dirtStrength; set(v) { native.dirtStrength = v }
        actual var quality: Quality get() = native.quality.toKmp(); set(v) { native.quality = v.toAndroid() }
        actual var lensFlare: Boolean get() = native.lensFlare; set(v) { native.lensFlare = v }
        actual var starburst: Boolean get() = native.starburst; set(v) { native.starburst = v }
        actual var chromaticAberration: Float get() = native.chromaticAberration; set(v) { native.chromaticAberration = v }
        actual var ghostCount: Int get() = native.ghostCount; set(v) { native.ghostCount = v }
        actual var ghostSpacing: Float get() = native.ghostSpacing; set(v) { native.ghostSpacing = v }
        actual var ghostThreshold: Float get() = native.ghostThreshold; set(v) { native.ghostThreshold = v }
        actual var haloRadius: Float get() = native.haloRadius; set(v) { native.haloRadius = v }
        actual var haloThickness: Float get() = native.haloThickness; set(v) { native.haloThickness = v }
        actual var haloThreshold: Float get() = native.haloThreshold; set(v) { native.haloThreshold = v }
    }

    actual class FogOptions actual constructor() {
        val native = com.google.android.filament.View.FogOptions()
        actual var enabled: Boolean get() = native.enabled; set(v) { native.enabled = v }
        actual var distance: Float get() = native.distance; set(v) { native.distance = v }
        actual var density: Float get() = native.density; set(v) { native.density = v }
        actual var height: Float get() = native.height; set(v) { native.height = v }
        actual var heightFalloff: Float get() = native.heightFalloff; set(v) { native.heightFalloff = v }
        actual var color: FloatArray get() = native.color; set(v) { native.color = v }
        actual var densityMap: Texture? = null; set(v) { field = v; native.skyColor = v?.nativeTexture }
    }

    actual class DepthOfFieldOptions actual constructor() {
        val native = com.google.android.filament.View.DepthOfFieldOptions()
        actual var enabled: Boolean get() = native.enabled; set(v) { native.enabled = v }
        actual var cocScale: Float get() = native.cocScale; set(v) { native.cocScale = v }
        actual var maxApertureDiameter: Float get() = native.maxApertureDiameter; set(v) { native.maxApertureDiameter = v }
        actual var filter: Filter get() = Filter.values()[native.filter.ordinal]; set(v) { native.filter = com.google.android.filament.View.DepthOfFieldOptions.Filter.values()[v.ordinal] }
        actual var nativeResolution: Boolean get() = native.nativeResolution; set(v) { native.nativeResolution = v }
        actual var foregroundRingCount: Int get() = native.foregroundRingCount; set(v) { native.foregroundRingCount = v }
        actual var backgroundRingCount: Int get() = native.backgroundRingCount; set(v) { native.backgroundRingCount = v }
        actual var fastGatherRingCount: Int get() = native.fastGatherRingCount; set(v) { native.fastGatherRingCount = v }
        actual enum class Filter { NONE, MEDIAN, GAUSSIAN }
    }

    actual class VignetteOptions actual constructor() {
        val native = com.google.android.filament.View.VignetteOptions()
        actual var enabled: Boolean get() = native.enabled; set(v) { native.enabled = v }
        actual var midPoint: Float get() = native.midPoint; set(v) { native.midPoint = v }
        actual var roundness: Float get() = native.roundness; set(v) { native.roundness = v }
        actual var feather: Float get() = native.feather; set(v) { native.feather = v }
        actual var color: FloatArray get() = native.color; set(v) { native.color = v }
    }

    actual class AmbientOcclusionOptions actual constructor() {
        val native = com.google.android.filament.View.AmbientOcclusionOptions()
        actual var radius: Float get() = native.radius; set(v) { native.radius = v }
        actual var bias: Float get() = native.bias; set(v) { native.bias = v }
        actual var intensity: Float get() = native.intensity; set(v) { native.intensity = v }
        actual var scale: Float get() = native.resolution; set(v) { native.resolution = v }
        actual var power: Float get() = native.power; set(v) { native.power = v }
        actual var minConeAngle: Float get() = native.minHorizonAngleRad; set(v) { native.minHorizonAngleRad = v }
        actual var quality: Quality get() = native.quality.toKmp(); set(v) { native.quality = v.toAndroid() }
        actual var lowPassFilter: Quality get() = native.lowPassFilter.toKmp(); set(v) { native.lowPassFilter = v.toAndroid() }
        actual var upsampling: Quality get() = native.upsampling.toKmp(); set(v) { native.upsampling = v.toAndroid() }
        actual var enabled: Boolean get() = native.enabled; set(v) { native.enabled = v }
        actual var bentNormals: Boolean get() = native.bentNormals; set(v) { native.bentNormals = v }
    }

    actual class TemporalAntiAliasingOptions actual constructor() {
        val native = com.google.android.filament.View.TemporalAntiAliasingOptions()
        actual var filterWidth: Float get() = native.filterWidth; set(v) { native.filterWidth = v }
        actual var feedback: Float get() = native.feedback; set(v) { native.feedback = v }
        actual var enabled: Boolean get() = native.enabled; set(v) { native.enabled = v }
    }

    actual class ScreenSpaceReflectionsOptions actual constructor() {
        val native = com.google.android.filament.View.ScreenSpaceReflectionsOptions()
        actual var enabled: Boolean get() = native.enabled; set(v) { native.enabled = v }
        actual var thickness: Float get() = native.thickness; set(v) { native.thickness = v }
        actual var bias: Float get() = native.bias; set(v) { native.bias = v }
        actual var maxDistance: Float get() = native.maxDistance; set(v) { native.maxDistance = v }
        actual var stride: Float get() = native.stride; set(v) { native.stride = v }
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

    actual fun setViewport(viewport: Viewport) { 
        nativeView.setViewport(com.google.android.filament.Viewport(viewport.left, viewport.bottom, viewport.width, viewport.height))
    }
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
        quality = o.quality.toKmp()
    }

    actual fun setRenderQuality(renderQuality: RenderQuality) = nativeView.setRenderQuality(renderQuality.nativeQuality)
    actual fun getRenderQuality(): RenderQuality = RenderQuality().apply {
        val q = nativeView.renderQuality
        hdrColorBuffer = q.hdrColorBuffer.toKmp()
    }

    actual fun setBloomOptions(options: BloomOptions) { nativeView.setBloomOptions(options.native) }
    actual fun getBloomOptions(): BloomOptions = BloomOptions().apply {
        val o = nativeView.bloomOptions
        enabled = o.enabled; levels = o.levels; resolution = o.resolution; strength = o.strength; threshold = o.threshold
        dirtStrength = o.dirtStrength; quality = o.quality.toKmp()
        lensFlare = o.lensFlare; starburst = o.starburst; chromaticAberration = o.chromaticAberration
        ghostCount = o.ghostCount; ghostSpacing = o.ghostSpacing; ghostThreshold = o.ghostThreshold
        haloRadius = o.haloRadius; haloThickness = o.haloThickness; haloThreshold = o.haloThreshold
    }
    actual fun setFogOptions(options: FogOptions) { nativeView.setFogOptions(options.native) }
    actual fun getFogOptions(): FogOptions = FogOptions().apply {
        val o = nativeView.fogOptions
        enabled = o.enabled; distance = o.distance; density = o.density; height = o.height; heightFalloff = o.heightFalloff; color = o.color
    }
    actual fun setDepthOfFieldOptions(options: DepthOfFieldOptions) { nativeView.setDepthOfFieldOptions(options.native) }
    actual fun getDepthOfFieldOptions(): DepthOfFieldOptions = DepthOfFieldOptions().apply {
        val o = nativeView.depthOfFieldOptions
        enabled = o.enabled; cocScale = o.cocScale; maxApertureDiameter = o.maxApertureDiameter
        filter = DepthOfFieldOptions.Filter.values()[o.filter.ordinal]
        nativeResolution = o.nativeResolution; foregroundRingCount = o.foregroundRingCount; backgroundRingCount = o.backgroundRingCount; fastGatherRingCount = o.fastGatherRingCount
    }
    actual fun setVignetteOptions(options: VignetteOptions) { nativeView.setVignetteOptions(options.native) }
    actual fun getVignetteOptions(): VignetteOptions = VignetteOptions().apply {
        val o = nativeView.vignetteOptions
        enabled = o.enabled; midPoint = o.midPoint; roundness = o.roundness; feather = o.feather; color = o.color
    }
    actual fun setAmbientOcclusionOptions(options: AmbientOcclusionOptions) { nativeView.setAmbientOcclusionOptions(options.native) }
    actual fun getAmbientOcclusionOptions(): AmbientOcclusionOptions = AmbientOcclusionOptions().apply {
        val o = nativeView.ambientOcclusionOptions
        radius = o.radius; bias = o.bias; intensity = o.intensity; scale = o.resolution; power = o.power; minConeAngle = o.minHorizonAngleRad
        quality = o.quality.toKmp(); lowPassFilter = o.lowPassFilter.toKmp(); upsampling = o.upsampling.toKmp()
        enabled = o.enabled; bentNormals = o.bentNormals
    }
    actual fun setTemporalAntiAliasingOptions(options: TemporalAntiAliasingOptions) { nativeView.setTemporalAntiAliasingOptions(options.native) }
    actual fun getTemporalAntiAliasingOptions(): TemporalAntiAliasingOptions = TemporalAntiAliasingOptions().apply {
        val o = nativeView.temporalAntiAliasingOptions
        filterWidth = o.filterWidth; feedback = o.feedback; enabled = o.enabled
    }
    actual fun setScreenSpaceReflectionsOptions(options: ScreenSpaceReflectionsOptions) { nativeView.setScreenSpaceReflectionsOptions(options.native) }
    actual fun getScreenSpaceReflectionsOptions(): ScreenSpaceReflectionsOptions = ScreenSpaceReflectionsOptions().apply {
        val o = nativeView.screenSpaceReflectionsOptions
        enabled = o.enabled; thickness = o.thickness; bias = o.bias; maxDistance = o.maxDistance; stride = o.stride
    }

    actual fun setRenderTarget(target: RenderTarget?) {
        this.renderTarget = target
        nativeView.setRenderTarget(target?.nativeRenderTarget)
    }
    actual fun getRenderTarget(): RenderTarget? = renderTarget
}
