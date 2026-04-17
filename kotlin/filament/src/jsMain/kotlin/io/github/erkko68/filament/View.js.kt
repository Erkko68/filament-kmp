package io.github.erkko68.filament

actual class View {
    actual fun setName(name: String) {
    }

    actual fun getName(): String? {
        TODO("Not yet implemented")
    }

    actual fun setScene(scene: Scene?) {
    }

    actual fun getScene(): Scene? {
        TODO("Not yet implemented")
    }

    actual fun setCamera(camera: Camera?) {
    }

    actual fun getCamera(): Camera? {
        TODO("Not yet implemented")
    }

    actual fun hasCamera(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun setViewport(viewport: Viewport) {
    }

    actual fun getViewport(): Viewport {
        TODO("Not yet implemented")
    }

    actual fun setBlendMode(blendMode: BlendMode) {
    }

    actual fun getBlendMode(): BlendMode {
        TODO("Not yet implemented")
    }

    actual fun setVisibleLayers(select: Int, values: Int) {
    }

    actual fun setLayerEnabled(layer: Int, enabled: Boolean) {
    }

    actual fun getVisibleLayers(): Int {
        TODO("Not yet implemented")
    }

    actual fun setPostProcessingEnabled(enabled: Boolean) {
    }

    actual fun isPostProcessingEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun setDithering(dithering: Dithering) {
    }

    actual fun getDithering(): Dithering {
        TODO("Not yet implemented")
    }

    actual fun setDynamicResolutionOptions(options: DynamicResolutionOptions) {
    }

    actual fun getDynamicResolutionOptions(): DynamicResolutionOptions {
        TODO("Not yet implemented")
    }

    actual fun setRenderQuality(renderQuality: RenderQuality) {
    }

    actual fun getRenderQuality(): RenderQuality {
        TODO("Not yet implemented")
    }

    actual fun setBloomOptions(options: BloomOptions) {
    }

    actual fun getBloomOptions(): BloomOptions {
        TODO("Not yet implemented")
    }

    actual fun setFogOptions(options: FogOptions) {
    }

    actual fun getFogOptions(): FogOptions {
        TODO("Not yet implemented")
    }

    actual fun setDepthOfFieldOptions(options: DepthOfFieldOptions) {
    }

    actual fun getDepthOfFieldOptions(): DepthOfFieldOptions {
        TODO("Not yet implemented")
    }

    actual fun setVignetteOptions(options: VignetteOptions) {
    }

    actual fun getVignetteOptions(): VignetteOptions {
        TODO("Not yet implemented")
    }

    actual fun setAmbientOcclusionOptions(options: AmbientOcclusionOptions) {
    }

    actual fun getAmbientOcclusionOptions(): AmbientOcclusionOptions {
        TODO("Not yet implemented")
    }

    actual fun setTemporalAntiAliasingOptions(options: TemporalAntiAliasingOptions) {
    }

    actual fun getTemporalAntiAliasingOptions(): TemporalAntiAliasingOptions {
        TODO("Not yet implemented")
    }

    actual fun setScreenSpaceReflectionsOptions(options: ScreenSpaceReflectionsOptions) {
    }

    actual fun getScreenSpaceReflectionsOptions(): ScreenSpaceReflectionsOptions {
        TODO("Not yet implemented")
    }

    actual fun setRenderTarget(target: RenderTarget?) {
    }

    actual fun getRenderTarget(): RenderTarget? {
        TODO("Not yet implemented")
    }

    actual fun setShadowType(type: ShadowType) {
    }

    actual fun getShadowType(): ShadowType {
        TODO("Not yet implemented")
    }

    actual fun setVsmShadowOptions(options: VsmShadowOptions) {
    }

    actual fun getVsmShadowOptions(): VsmShadowOptions {
        TODO("Not yet implemented")
    }

    actual fun setSoftShadowOptions(options: SoftShadowOptions) {
    }

    actual fun getSoftShadowOptions(): SoftShadowOptions {
        TODO("Not yet implemented")
    }

    actual fun setGuardBandOptions(options: GuardBandOptions) {
    }

    actual fun getGuardBandOptions(): GuardBandOptions {
        TODO("Not yet implemented")
    }

    actual fun setStereoscopicOptions(options: StereoscopicOptions) {
    }

    actual fun getStereoscopicOptions(): StereoscopicOptions {
        TODO("Not yet implemented")
    }

    actual fun setMultiSampleAntiAliasingOptions(options: MultiSampleAntiAliasingOptions) {
    }

    actual fun getMultiSampleAntiAliasingOptions(): MultiSampleAntiAliasingOptions {
        TODO("Not yet implemented")
    }

    actual fun setFrustumCullingEnabled(enabled: Boolean) {
    }

    actual fun isFrustumCullingEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun setShadowingEnabled(enabled: Boolean) {
    }

    actual fun setScreenSpaceRefractionEnabled(enabled: Boolean) {
    }

    actual fun setStencilBufferEnabled(enabled: Boolean) {
    }

    actual fun isStencilBufferEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun setFrontFaceWindingInverted(inverted: Boolean) {
    }

    actual fun isFrontFaceWindingInverted(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun setTransparentPickingEnabled(enabled: Boolean) {
    }

    actual fun isTransparentPickingEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun setMaterialGlobal(index: Int, value: FloatArray) {
    }

    actual fun getMaterialGlobal(index: Int): FloatArray {
        TODO("Not yet implemented")
    }

    actual fun getFogEntity(): Int {
        TODO("Not yet implemented")
    }

    actual fun clearFrameHistory(engine: Engine) {
    }

    actual fun setDynamicLightingOptions(zNear: Float, zFar: Float) {
    }

    actual enum class Dithering { NONE, TEMPORAL }
    actual enum class BlendMode { OPAQUE, TRANSLUCENT }
    actual enum class Quality { LOW, MEDIUM, HIGH, ULTRA }
    actual enum class ShadowType { PCF, VSM, DPCF, PCSS, PCFd }
    actual class DynamicResolutionOptions {
        actual var enabled: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var homogeneousScaling: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var minScale: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var maxScale: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var sharpness: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var quality: Quality
            get() = TODO("Not yet implemented")
            set(value) {}
    }

    actual class RenderQuality {
        actual var hdrColorBuffer: Quality
            get() = TODO("Not yet implemented")
            set(value) {}
    }

    actual class BloomOptions {
        actual var enabled: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var levels: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var resolution: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var strength: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var threshold: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var dirt: Texture?
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var dirtStrength: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var quality: Quality
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var lensFlare: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var starburst: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var chromaticAberration: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var ghostCount: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var ghostSpacing: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var ghostThreshold: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var haloRadius: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var haloThickness: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var haloThreshold: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var highlight: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var blendMode: BlendMode
            get() = TODO("Not yet implemented")
            set(value) {}

        actual enum class BlendMode { ADD, INTERPOLATE }
    }

    actual class FogOptions {
        actual var enabled: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var distance: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var density: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var height: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var heightFalloff: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var color: FloatArray
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var densityMap: Texture?
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var cutOffDistance: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var maximumOpacity: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var inScatteringStart: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var inScatteringSize: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var fogColorFromIbl: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var skyColor: Texture?
            get() = TODO("Not yet implemented")
            set(value) {}
    }

    actual class DepthOfFieldOptions {
        actual var enabled: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var cocScale: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var maxApertureDiameter: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var filter: Filter
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var nativeResolution: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var foregroundRingCount: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var backgroundRingCount: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var fastGatherRingCount: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var maxForegroundCOC: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var maxBackgroundCOC: Int
            get() = TODO("Not yet implemented")
            set(value) {}

        actual enum class Filter { NONE, MEDIAN, GAUSSIAN }
    }

    actual class VignetteOptions {
        actual var enabled: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var midPoint: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var roundness: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var feather: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var color: FloatArray
            get() = TODO("Not yet implemented")
            set(value) {}
    }

    actual class AmbientOcclusionOptions {
        actual var radius: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var bias: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var intensity: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var scale: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var power: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var minConeAngle: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var quality: Quality
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var lowPassFilter: Quality
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var upsampling: Quality
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var enabled: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var bentNormals: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var bilateralThreshold: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var resolution: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var ssct: Ssct
            get() = TODO("Not yet implemented")
            set(value) {}

        actual class Ssct {
            actual var enabled: Boolean
                get() = TODO("Not yet implemented")
                set(value) {}
            actual var lightConeRad: Float
                get() = TODO("Not yet implemented")
                set(value) {}
            actual var shadowDistance: Float
                get() = TODO("Not yet implemented")
                set(value) {}
            actual var contactDistanceMax: Float
                get() = TODO("Not yet implemented")
                set(value) {}
            actual var intensity: Float
                get() = TODO("Not yet implemented")
                set(value) {}
            actual var lightDirection: FloatArray
                get() = TODO("Not yet implemented")
                set(value) {}
            actual var depthBias: Float
                get() = TODO("Not yet implemented")
                set(value) {}
            actual var depthSlopeBias: Float
                get() = TODO("Not yet implemented")
                set(value) {}
            actual var sampleCount: Int
                get() = TODO("Not yet implemented")
                set(value) {}
            actual var rayCount: Int
                get() = TODO("Not yet implemented")
                set(value) {}
        }
    }

    actual class TemporalAntiAliasingOptions {
        actual var feedback: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var lodBias: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var sharpness: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var enabled: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var upscaling: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var filterHistory: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var filterInput: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var useYCoCg: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var hdr: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var boxType: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var boxClipping: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var jitterPattern: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var varianceGamma: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var preventFlickering: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var historyReprojection: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
    }

    actual class ScreenSpaceReflectionsOptions {
        actual var enabled: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var thickness: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var bias: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var maxDistance: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var stride: Float
            get() = TODO("Not yet implemented")
            set(value) {}
    }

    actual class VsmShadowOptions {
        actual var anisotropy: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var mipmapping: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var msaaSamples: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var highPrecision: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var lightBleedReduction: Float
            get() = TODO("Not yet implemented")
            set(value) {}
    }

    actual class SoftShadowOptions {
        actual var penumbraScale: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var penumbraRatioScale: Float
            get() = TODO("Not yet implemented")
            set(value) {}
    }

    actual class GuardBandOptions {
        actual var enabled: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
    }

    actual class StereoscopicOptions {
        actual var enabled: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
    }

    actual class MultiSampleAntiAliasingOptions {
        actual var enabled: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var sampleCount: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var customResolve: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
    }
}