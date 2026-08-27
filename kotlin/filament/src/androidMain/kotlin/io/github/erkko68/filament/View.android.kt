package io.github.erkko68.filament

import com.google.android.filament.View as FilamentView
import com.google.android.filament.Texture as FilamentTexture
import com.google.android.filament.Viewport as FilamentViewport

actual class View @InternalFilamentApi constructor(internal val nativeView: FilamentView) {
    internal val getNativeObject: Long get() = nativeView.nativeObject
    
    private var mScene: Scene? = null
    private var mCamera: Camera? = null
    private var mRenderTarget: RenderTarget? = null
    private var _isShadowingEnabled: Boolean = true
    private var _isScreenSpaceRefractionEnabled: Boolean = false

    actual enum class Dithering { NONE, TEMPORAL }
    actual enum class BlendMode { OPAQUE, TRANSLUCENT }
    actual enum class Quality { LOW, MEDIUM, HIGH, ULTRA }
    actual enum class ShadowType { PCF, VSM, DPCF, PCSS, PCFd }
    actual enum class AntiAliasing { NONE, FXAA }

    actual class PickingQueryResult actual constructor(
        actual val renderable: Int,
        actual val depth: Float,
        actual val fragCoords: FloatArray
    )

    private var mShadowType: ShadowType = ShadowType.PCF
    private var mColorGrading: ColorGrading? = null

    actual var name: String?
        get() = this@View.nativeView.name
        set(value) { this@View.nativeView.setName(value ?: "") }

    actual var scene: Scene?
        get() = mScene
        set(value) {
            this@View.mScene = value
            this@View.nativeView.scene = value?.nativeScene 
        }

    actual var camera: Camera?
        get() = mCamera
        set(value) {
            this@View.mCamera = value
            this@View.nativeView.camera = value?.nativeCamera 
        }
    actual val hasCamera: Boolean get() = this@View.nativeView.hasCamera()

    actual var viewport: Viewport
        get() {
            val vp = this@View.nativeView.viewport
            return Viewport(vp.left, vp.bottom, vp.width, vp.height)
        }
        set(value) {
            val nativeVp = FilamentViewport(value.left, value.bottom, value.width, value.height)
            this@View.nativeView.setViewport(nativeVp)
        }

    actual var blendMode: BlendMode
        get() = io.github.erkko68.filament.View.BlendMode.entries[this@View.nativeView.blendMode.ordinal]
        set(value) {
            this@View.nativeView.blendMode = FilamentView.BlendMode.entries[value.ordinal]
        }

    actual fun setVisibleLayers(select: Int, values: Int) {
        this@View.nativeView.setVisibleLayers(select, values)
    }
    actual fun setLayerEnabled(layer: Int, enabled: Boolean) {
        this@View.nativeView.setLayerEnabled(layer, enabled)
    }
    actual val visibleLayers: Int get() = this@View.nativeView.visibleLayers

    actual var isPostProcessingEnabled: Boolean
        get() = this@View.nativeView.isPostProcessingEnabled
        set(value) { this@View.nativeView.isPostProcessingEnabled = value }


    actual var dithering: Dithering
        get() = io.github.erkko68.filament.View.Dithering.entries[this@View.nativeView.dithering.ordinal]
        set(value) { this@View.nativeView.dithering = FilamentView.Dithering.entries[value.ordinal] }

    actual var dynamicResolutionOptions: DynamicResolutionOptions
        get() {
            val o = this@View.nativeView.dynamicResolutionOptions
            val kmp = DynamicResolutionOptions()
            kmp.enabled = o.enabled
            kmp.homogeneousScaling = o.homogeneousScaling
            kmp.minScale = o.minScale
            kmp.maxScale = o.maxScale
            kmp.sharpness = o.sharpness
            kmp.quality = io.github.erkko68.filament.View.Quality.entries[o.quality.ordinal]
            return kmp
        }
        set(value) { this@View.nativeView.setDynamicResolutionOptions(value.toAndroid()) }

    actual val lastDynamicResolutionScale: FloatArray get() {
        val out = FloatArray(2)
        this@View.nativeView.getLastDynamicResolutionScale(out)
        return out
    }

    actual var renderQuality: RenderQuality
        get() {
            val o = this@View.nativeView.renderQuality
            val kmp = RenderQuality()
            kmp.hdrColorBuffer = io.github.erkko68.filament.View.Quality.entries[o.hdrColorBuffer.ordinal]
            return kmp
        }
        set(value) { this@View.nativeView.setRenderQuality(value.toAndroid()) }
    
    actual var bloomOptions: BloomOptions
        get() {
            val o = this@View.nativeView.bloomOptions
            val kmp = BloomOptions()
            kmp.enabled = o.enabled
            kmp.levels = o.levels
            kmp.resolution = o.resolution
            kmp.strength = o.strength
            kmp.threshold = o.threshold
            kmp.dirt = o.dirt?.let { Texture(it) }
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
            kmp.blendMode = io.github.erkko68.filament.BloomOptions.BlendMode.entries[o.blendMode.ordinal]
            kmp.quality = io.github.erkko68.filament.View.Quality.entries[o.quality.ordinal]
            return kmp
        }
        set(value) { this@View.nativeView.setBloomOptions(value.toAndroid()) }

    actual var fogOptions: FogOptions
        get() {
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
        set(value) { this@View.nativeView.setFogOptions(value.toAndroid()) }

    actual var depthOfFieldOptions: DepthOfFieldOptions
        get() {
            val o = this@View.nativeView.depthOfFieldOptions
            val kmp = DepthOfFieldOptions()
            kmp.enabled = o.enabled
            kmp.cocScale = o.cocScale
            // Reflects what was set, not what the engine has: nSetDepthOfFieldOptions never
            // marshals cocAspectRatio (upstream). @PlatformGap on the common declaration.
            kmp.cocAspectRatio = o.cocAspectRatio
            kmp.maxApertureDiameter = o.maxApertureDiameter
            kmp.filter = io.github.erkko68.filament.DepthOfFieldOptions.Filter.entries[o.filter.ordinal]
            kmp.nativeResolution = o.nativeResolution
            kmp.foregroundRingCount = o.foregroundRingCount
            kmp.backgroundRingCount = o.backgroundRingCount
            kmp.fastGatherRingCount = o.fastGatherRingCount
            kmp.maxForegroundCOC = o.maxForegroundCOC
            kmp.maxBackgroundCOC = o.maxBackgroundCOC
            return kmp
        }
        set(value) { this@View.nativeView.setDepthOfFieldOptions(value.toAndroid()) }

    actual var vignetteOptions: VignetteOptions
        get() {
            val o = this@View.nativeView.vignetteOptions
            val kmp = VignetteOptions()
            kmp.enabled = o.enabled
            kmp.midPoint = o.midPoint
            kmp.roundness = o.roundness
            kmp.feather = o.feather
            kmp.color = o.color
            return kmp
        }
        set(value) { this@View.nativeView.setVignetteOptions(value.toAndroid()) }

    actual var ambientOcclusionOptions: AmbientOcclusionOptions
        get() {
            val o = this@View.nativeView.ambientOcclusionOptions
            val kmp = AmbientOcclusionOptions()
            kmp.radius = o.radius
            kmp.bias = o.bias
            kmp.intensity = o.intensity
            kmp.power = o.power
            kmp.minHorizonAngleRad = o.minHorizonAngleRad
            kmp.quality = io.github.erkko68.filament.View.Quality.entries[o.quality.ordinal]
            kmp.lowPassFilter = io.github.erkko68.filament.View.Quality.entries[o.lowPassFilter.ordinal]
            kmp.upsampling = io.github.erkko68.filament.View.Quality.entries[o.upsampling.ordinal]
            kmp.enabled = o.enabled
            kmp.aoType = AmbientOcclusionOptions.AmbientOcclusionType.entries[o.aoType.ordinal]
            kmp.bentNormals = o.bentNormals
            kmp.resolution = o.resolution

            val kmpSsct = io.github.erkko68.filament.AmbientOcclusionOptions.Ssct()
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

            val kmpGtao = io.github.erkko68.filament.AmbientOcclusionOptions.Gtao()
            kmpGtao.sampleSliceCount = o.gtaoSampleSliceCount
            kmpGtao.sampleStepsPerSlice = o.gtaoSampleStepsPerSlice
            kmpGtao.thicknessHeuristic = o.gtaoThicknessHeuristic
            kmpGtao.useVisibilityBitmasks = o.gtaoUseVisibilityBitmasks
            kmpGtao.constThickness = o.gtaoConstThickness
            kmpGtao.linearThickness = o.gtaoLinearThickness
            kmp.gtao = kmpGtao

            return kmp
        }
        set(value) {
            val n = value.toAndroid()
            n.radius = value.radius
            n.bias = value.bias
            n.intensity = value.intensity
            n.power = value.power
            n.minHorizonAngleRad = value.minHorizonAngleRad
            n.quality = FilamentView.QualityLevel.entries[value.quality.ordinal]
            n.lowPassFilter = FilamentView.QualityLevel.entries[value.lowPassFilter.ordinal]
            n.upsampling = FilamentView.QualityLevel.entries[value.upsampling.ordinal]
            n.enabled = value.enabled
            n.aoType = FilamentView.AmbientOcclusionOptions.AmbientOcclusionType.entries[value.aoType.ordinal]
            n.bentNormals = value.bentNormals
            n.resolution = value.resolution
            // Map flattened
            n.ssctEnabled = value.ssct.enabled
            n.ssctLightConeRad = value.ssct.lightConeRad
            n.ssctShadowDistance = value.ssct.shadowDistance
            n.ssctContactDistanceMax = value.ssct.contactDistanceMax
            n.ssctIntensity = value.ssct.intensity
            n.ssctLightDirection = value.ssct.lightDirection
            n.ssctDepthBias = value.ssct.depthBias
            n.ssctDepthSlopeBias = value.ssct.depthSlopeBias
            n.ssctSampleCount = value.ssct.sampleCount
            n.ssctRayCount = value.ssct.rayCount
            n.gtaoSampleSliceCount = value.gtao.sampleSliceCount
            n.gtaoSampleStepsPerSlice = value.gtao.sampleStepsPerSlice
            n.gtaoThicknessHeuristic = value.gtao.thicknessHeuristic
            n.gtaoUseVisibilityBitmasks = value.gtao.useVisibilityBitmasks
            n.gtaoConstThickness = value.gtao.constThickness
            n.gtaoLinearThickness = value.gtao.linearThickness
            this@View.nativeView.setAmbientOcclusionOptions(n)
        }

    actual var temporalAntiAliasingOptions: TemporalAntiAliasingOptions
        get() {
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
            kmp.boxType = TemporalAntiAliasingOptions.BoxType.entries[o.boxType.ordinal]
            kmp.boxClipping = TemporalAntiAliasingOptions.BoxClipping.entries[o.boxClipping.ordinal]
            kmp.jitterPattern = TemporalAntiAliasingOptions.JitterPattern.entries[o.jitterPattern.ordinal]
            kmp.varianceGamma = o.varianceGamma
            kmp.preventFlickering = o.preventFlickering
            kmp.historyReprojection = o.historyReprojection
            return kmp
        }
        set(value) { this@View.nativeView.setTemporalAntiAliasingOptions(value.toAndroid()) }

    actual var screenSpaceReflectionsOptions: ScreenSpaceReflectionsOptions
        get() {
            val o = this@View.nativeView.screenSpaceReflectionsOptions
            val kmp = ScreenSpaceReflectionsOptions()
            kmp.enabled = o.enabled
            kmp.thickness = o.thickness
            kmp.bias = o.bias
            kmp.maxDistance = o.maxDistance
            kmp.stride = o.stride
            return kmp
        }
        set(value) { this@View.nativeView.setScreenSpaceReflectionsOptions(value.toAndroid()) }

    actual var gridSize: Double
        get() = this@View.nativeView.gridSize
        set(value) { this@View.nativeView.setGridSize(value) }

    actual val effectiveGridSize: Double
        get() = this@View.nativeView.effectiveGridSize

    actual var renderTarget: RenderTarget?
        get() = mRenderTarget
        set(value) {
            this@View.mRenderTarget = value
            this@View.nativeView.setRenderTarget(value?.nativeRenderTarget)
        }

    actual var shadowType: ShadowType
        get() = this@View.mShadowType
        set(value) {
            this@View.mShadowType = value
            this@View.nativeView.setShadowType(FilamentView.ShadowType.entries[value.ordinal])
        }

    actual var vsmShadowOptions: VsmShadowOptions
        get() {
            val o = this@View.nativeView.vsmShadowOptions
            val kmp = VsmShadowOptions()
            kmp.anisotropy = o.anisotropy
            kmp.mipmapping = o.mipmapping
            kmp.msaaSamples = o.msaaSamples
            kmp.highPrecision = o.highPrecision
            kmp.lightBleedReduction = o.lightBleedReduction
            return kmp
        }
        set(value) { this@View.nativeView.setVsmShadowOptions(value.toAndroid()) }
    actual var softShadowOptions: SoftShadowOptions
        get() {
            val o = this@View.nativeView.softShadowOptions
            val kmp = SoftShadowOptions()
            kmp.penumbraScale = o.penumbraScale
            kmp.penumbraRatioScale = o.penumbraRatioScale
            kmp.maxPenumbraRatio = o.maxPenumbraRatio
            kmp.maxSearchRadius = o.maxSearchRadius
            return kmp
        }
        set(value) { this@View.nativeView.setSoftShadowOptions(value.toAndroid()) }
    actual var guardBandOptions: GuardBandOptions
        get() {
            val o = this@View.nativeView.guardBandOptions
            val kmp = GuardBandOptions()
            kmp.enabled = o.enabled
            return kmp
        }
        set(value) { this@View.nativeView.setGuardBandOptions(value.toAndroid()) }
    actual var stereoscopicOptions: StereoscopicOptions
        get() {
            val o = this@View.nativeView.stereoscopicOptions
            val kmp = StereoscopicOptions()
            kmp.enabled = o.enabled
            return kmp
        }
        set(value) { this@View.nativeView.setStereoscopicOptions(value.toAndroid()) }
    actual var multiSampleAntiAliasingOptions: MultiSampleAntiAliasingOptions
        get() {
            val o = this@View.nativeView.multiSampleAntiAliasingOptions
            val kmp = MultiSampleAntiAliasingOptions()
            kmp.enabled = o.enabled
            kmp.sampleCount = o.sampleCount
            kmp.customResolve = o.customResolve
            return kmp
        }
        set(value) { this@View.nativeView.setMultiSampleAntiAliasingOptions(value.toAndroid()) }

    actual var isFrustumCullingEnabled: Boolean
        get() = this@View.nativeView.isFrustumCullingEnabled
        set(value) { this@View.nativeView.setFrustumCullingEnabled(value) }
    actual var isShadowingEnabled: Boolean
        get() = _isShadowingEnabled
        set(value) { _isShadowingEnabled = value; this@View.nativeView.setShadowingEnabled(value) }
    actual var isScreenSpaceRefractionEnabled: Boolean
        get() = _isScreenSpaceRefractionEnabled
        set(value) { _isScreenSpaceRefractionEnabled = value; this@View.nativeView.setScreenSpaceRefractionEnabled(value) }
    actual var isStencilBufferEnabled: Boolean
        get() = this@View.nativeView.isStencilBufferEnabled
        set(value) { this@View.nativeView.setStencilBufferEnabled(value) }
    actual var isFrontFaceWindingInverted: Boolean
        get() = this@View.nativeView.isFrontFaceWindingInverted
        set(value) { this@View.nativeView.setFrontFaceWindingInverted(value) }
    actual var isTransparentPickingEnabled: Boolean
        get() = this@View.nativeView.isTransparentPickingEnabled
        set(value) { this@View.nativeView.setTransparentPickingEnabled(value) }

    actual fun setMaterialGlobal(index: Int, value: FloatArray) {
        this@View.nativeView.setMaterialGlobal(index, value)
    }
    actual fun getMaterialGlobal(index: Int): FloatArray = this@View.nativeView.getMaterialGlobal(index, null)
    actual val fogEntity: Entity get() = this@View.nativeView.fogEntity
    actual val visibleRenderableCount: Int get() = this@View.nativeView.visibleRenderableCount
    actual fun clearFrameHistory(engine: Engine) { this@View.nativeView.clearFrameHistory(engine.nativeEngine) }

    actual fun setDynamicLightingOptions(zNear: Float, zFar: Float) {
        nativeView.setDynamicLightingOptions(zNear, zFar)
    }

    actual var antiAliasing: AntiAliasing
        get() = AntiAliasing.entries[nativeView.antiAliasing.ordinal]
        set(value) { nativeView.antiAliasing = FilamentView.AntiAliasing.entries[value.ordinal] }

    actual var colorGrading: ColorGrading?
        get() = mColorGrading
        set(value) {
            mColorGrading = value
            nativeView.setColorGrading(value?.nativeColorGrading)
        }

    actual fun pick(x: Int, y: Int, callback: (PickingQueryResult) -> Unit) {
        // Filament's JNI bridge only delivers the callback when `handler` is an Executor;
        // null silently drops results. See View.jvm.kt for the longer note.
        nativeView.pick(x, y, directExecutor) { r ->
            callback(PickingQueryResult(r.renderable, r.depth, r.fragCoords.copyOf()))
        }
    }

    private companion object {
        private val directExecutor = java.util.concurrent.Executor { it.run() }
    }
}
