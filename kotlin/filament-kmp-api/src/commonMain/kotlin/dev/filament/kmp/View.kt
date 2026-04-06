package dev.filament.kmp

/**
 * Encompasses all the state needed for rendering a [Scene].
 */
expect class View {

    @Deprecated("Use ColorGrading instead")
    enum class ToneMapping {
        LINEAR,
        ACES
    }

    enum class TargetBufferFlags(val flags: Int) {
        COLOR0(0x1),
        COLOR1(0x2),
        COLOR2(0x4),
        COLOR3(0x8),
        DEPTH(0x10),
        STENCIL(0x20);

        companion object {
            val NONE: Set<TargetBufferFlags>
            val ALL_COLOR: Set<TargetBufferFlags>
            val DEPTH_STENCIL: Set<TargetBufferFlags>
            val ALL: Set<TargetBufferFlags>
        }
    }

    enum class AntiAliasing {
        NONE,
        FXAA,
    }

    enum class Dithering {
        NONE,
        TEMPORAL,
    }

    enum class ShadowType {
        PCF,
        VSM,
        DPCF,
        PCSS,
        PCFd,
    }

    enum class QualityLevel {
        LOW,
        MEDIUM,
        HIGH,
        ULTRA,
    }

    enum class BlendMode {
        OPAQUE,
        TRANSLUCENT,
    }

    class DynamicResolutionOptions {
        var minScale: Float
        var maxScale: Float
        var sharpness: Float
        var enabled: Boolean
        var homogeneousScaling: Boolean
        var quality: QualityLevel

        constructor()
    }

    class BloomOptions {
        enum class BlendMode {
            ADD,
            INTERPOLATE,
        }

        var dirt: Texture?
        var dirtStrength: Float
        var strength: Float
        var resolution: Int
        var levels: Int
        var blendMode: BlendMode
        var threshold: Boolean
        var enabled: Boolean
        var highlight: Float
        var quality: QualityLevel
        var lensFlare: Boolean
        var starburst: Boolean
        var chromaticAberration: Float
        var ghostCount: Int
        var ghostSpacing: Float
        var ghostThreshold: Float
        var haloThickness: Float
        var haloRadius: Float
        var haloThreshold: Float

        constructor()
    }

    class FogOptions {
        var distance: Float
        var cutOffDistance: Float
        var maximumOpacity: Float
        var height: Float
        var heightFalloff: Float
        var color: FloatArray
        var density: Float
        var inScatteringStart: Float
        var inScatteringSize: Float
        var fogColorFromIbl: Boolean
        var skyColor: Texture?
        var enabled: Boolean

        constructor()
    }

    class DepthOfFieldOptions {
        enum class Filter {
            NONE,
            UNUSED,
            MEDIAN,
        }

        var cocScale: Float
        var cocAspectRatio: Float
        var maxApertureDiameter: Float
        var enabled: Boolean
        var filter: Filter
        var nativeResolution: Boolean
        var foregroundRingCount: Int
        var backgroundRingCount: Int
        var fastGatherRingCount: Int
        var maxForegroundCOC: Int
        var maxBackgroundCOC: Int

        constructor()
    }

    class VignetteOptions {
        var midPoint: Float
        var roundness: Float
        var feather: Float
        var color: FloatArray
        var enabled: Boolean

        constructor()
    }

    class RenderQuality {
        var hdrColorBuffer: QualityLevel

        constructor()
    }

    class AmbientOcclusionOptions {
        enum class AmbientOcclusionType {
            SAO,
            GTAO,
        }

        var aoType: AmbientOcclusionType
        var radius: Float
        var power: Float
        var bias: Float
        var resolution: Float
        var intensity: Float
        var bilateralThreshold: Float
        var quality: QualityLevel
        var lowPassFilter: QualityLevel
        var upsampling: QualityLevel
        var enabled: Boolean
        var bentNormals: Boolean
        var minHorizonAngleRad: Float
        var ssctLightConeRad: Float
        var ssctShadowDistance: Float
        var ssctContactDistanceMax: Float
        var ssctIntensity: Float
        var ssctLightDirection: FloatArray
        var ssctDepthBias: Float
        var ssctDepthSlopeBias: Float
        var ssctSampleCount: Int
        var ssctRayCount: Int
        var ssctEnabled: Boolean
        var gtaoSampleSliceCount: Int
        var gtaoSampleStepsPerSlice: Int
        var gtaoThicknessHeuristic: Float
        var gtaoUseVisibilityBitmasks: Boolean
        var gtaoConstThickness: Float
        var gtaoLinearThickness: Boolean

        constructor()
    }

    class MultiSampleAntiAliasingOptions {
        var enabled: Boolean
        var sampleCount: Int
        var customResolve: Boolean

        constructor()
    }

    class TemporalAntiAliasingOptions {
        enum class BoxType {
            AABB,
            AABB_VARIANCE,
        }

        enum class BoxClipping {
            ACCURATE,
            CLAMP,
            NONE,
        }

        enum class JitterPattern {
            RGSS_X4,
            UNIFORM_HELIX_X4,
            HALTON_23_X8,
            HALTON_23_X16,
            HALTON_23_X32,
        }

        var feedback: Float
        var lodBias: Float
        var sharpness: Float
        var enabled: Boolean
        var upscaling: Float
        var filterHistory: Boolean
        var filterInput: Boolean
        var useYCoCg: Boolean
        var hdr: Boolean
        var boxType: BoxType
        var boxClipping: BoxClipping
        var jitterPattern: JitterPattern
        var varianceGamma: Float
        var preventFlickering: Boolean
        var historyReprojection: Boolean

        constructor()
    }

    class ScreenSpaceReflectionsOptions {
        var thickness: Float
        var bias: Float
        var maxDistance: Float
        var stride: Float
        var enabled: Boolean

        constructor()
    }

    class GuardBandOptions {
        var enabled: Boolean

        constructor()
    }

    class VsmShadowOptions {
        var anisotropy: Int
        var mipmapping: Boolean
        var msaaSamples: Int
        var highPrecision: Boolean
        var minVarianceScale: Float
        var lightBleedReduction: Float

        constructor()
    }

    class SoftShadowOptions {
        var penumbraScale: Float
        var penumbraRatioScale: Float

        constructor()
    }

    class StereoscopicOptions {
        var enabled: Boolean

        constructor()
    }

    class PickingQueryResult {
        var renderable: Int
        var depth: Float
        var fragCoords: FloatArray
    }

    interface OnPickCallback {
        fun onPick(result: PickingQueryResult)
    }

    fun setName(name: String)
    fun getName(): String?
    fun setScene(scene: Scene?)
    fun getScene(): Scene?
    fun setCamera(camera: Camera?)
    fun hasCamera(): Boolean
    fun getCamera(): Camera?
    fun setViewport(viewport: Viewport)
    fun getViewport(): Viewport
    fun setBlendMode(blendMode: BlendMode)
    fun getBlendMode(): BlendMode?
    fun setVisibleLayers(select: Int, values: Int)
    fun getVisibleLayers(): Int
    fun setLayerEnabled(layer: Int, enabled: Boolean)
    fun setShadowingEnabled(enabled: Boolean)
    fun isShadowingEnabled(): Boolean
    fun setFrustumCullingEnabled(enabled: Boolean)
    fun isFrustumCullingEnabled(): Boolean
    fun setScreenSpaceRefractionEnabled(enabled: Boolean)
    fun isScreenSpaceRefractionEnabled(): Boolean
    fun setRenderTarget(target: RenderTarget?)
    fun getRenderTarget(): RenderTarget?
    fun setAntiAliasing(type: AntiAliasing)
    fun getAntiAliasing(): AntiAliasing
    fun setMultiSampleAntiAliasingOptions(options: MultiSampleAntiAliasingOptions)
    fun getMultiSampleAntiAliasingOptions(): MultiSampleAntiAliasingOptions
    fun setTemporalAntiAliasingOptions(options: TemporalAntiAliasingOptions)
    fun getTemporalAntiAliasingOptions(): TemporalAntiAliasingOptions
    fun setScreenSpaceReflectionsOptions(options: ScreenSpaceReflectionsOptions)
    fun getScreenSpaceReflectionsOptions(): ScreenSpaceReflectionsOptions
    fun setGuardBandOptions(options: GuardBandOptions)
    fun getGuardBandOptions(): GuardBandOptions
    fun setColorGrading(colorGrading: ColorGrading?)
    fun getColorGrading(): ColorGrading?
    fun setDithering(dithering: Dithering)
    fun getDithering(): Dithering
    fun setDynamicResolutionOptions(options: DynamicResolutionOptions)
    fun getDynamicResolutionOptions(): DynamicResolutionOptions
    fun getLastDynamicResolutionScale(out: FloatArray?): FloatArray
    fun setRenderQuality(renderQuality: RenderQuality)
    fun getRenderQuality(): RenderQuality
    fun isPostProcessingEnabled(): Boolean
    fun setPostProcessingEnabled(enabled: Boolean)
    fun isFrontFaceWindingInverted(): Boolean
    fun setFrontFaceWindingInverted(inverted: Boolean)
    fun isTransparentPickingEnabled(): Boolean
    fun setTransparentPickingEnabled(enabled: Boolean)
    fun setDynamicLightingOptions(zLightNear: Float, zLightFar: Float)
    fun setShadowType(type: ShadowType)
    fun setVsmShadowOptions(options: VsmShadowOptions)
    fun getVsmShadowOptions(): VsmShadowOptions
    fun setSoftShadowOptions(options: SoftShadowOptions)
    fun getSoftShadowOptions(): SoftShadowOptions
    fun setAmbientOcclusionOptions(options: AmbientOcclusionOptions)
    fun getAmbientOcclusionOptions(): AmbientOcclusionOptions
    fun setBloomOptions(options: BloomOptions)
    fun getBloomOptions(): BloomOptions
    fun setVignetteOptions(options: VignetteOptions)
    fun getVignetteOptions(): VignetteOptions
    fun setFogOptions(options: FogOptions)
    fun getFogOptions(): FogOptions
    fun setDepthOfFieldOptions(options: DepthOfFieldOptions)
    fun getDepthOfFieldOptions(): DepthOfFieldOptions
    fun setStencilBufferEnabled(enabled: Boolean)
    fun isStencilBufferEnabled(): Boolean
    fun setStereoscopicOptions(options: StereoscopicOptions)
    fun getStereoscopicOptions(): StereoscopicOptions
    fun pick(x: Int, y: Int, handler: Any?, callback: OnPickCallback?)
    fun setMaterialGlobal(index: Int, value: FloatArray)
    fun getMaterialGlobal(index: Int, out: FloatArray?): FloatArray
    fun getFogEntity(): Int
    fun clearFrameHistory(engine: Engine)
}
