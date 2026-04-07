@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaView

actual class View internal constructor(internal var nativeHandle: CPointer<FilaView>?) {
    actual enum class Dithering { NONE, TEMPORAL }
    actual enum class BlendMode { OPAQUE, TRANSLUCENT }
    actual enum class Quality { LOW, MEDIUM, HIGH, ULTRA }
    actual enum class ShadowType { PCF, VSM, DPCF, PCSS, PCFd }
    
    private var mScene: Scene? = null
    private var mCamera: Camera? = null
    private var mRenderTarget: RenderTarget? = null

    private var mShadowType: ShadowType = ShadowType.PCF

    actual class DynamicResolutionOptions actual constructor() {
        actual var enabled: Boolean = false
        actual var homogeneousScaling: Boolean = false
        actual var minScale: Float = 0.5f
        actual var maxScale: Float = 1.0f
        actual var sharpness: Float = 1.0f
        actual var quality: Quality = Quality.LOW
    }

    actual class RenderQuality actual constructor() {
        actual var hdrColorBuffer: Quality = Quality.HIGH
    }

    actual class BloomOptions actual constructor() {
        actual var enabled: Boolean = false
        actual var levels: Int = 6
        actual var resolution: Int = 0
        actual var strength: Float = 0.10f
        actual var threshold: Boolean = true
        actual var dirt: Texture? = null
        actual var dirtStrength: Float = 0.2f
        actual var quality: Quality = Quality.LOW
        actual var lensFlare: Boolean = false
        actual var starburst: Boolean = false
        actual var chromaticAberration: Float = 0.005f
        actual var ghostCount: Int = 4
        actual var ghostSpacing: Float = 0.3f
        actual var ghostThreshold: Float = 10.0f
        actual var haloRadius: Float = 0.45f
        actual var haloThickness: Float = 0.1f
        actual var haloThreshold: Float = 10.0f
        actual var highlight: Float = 1000.0f
        actual var blendMode: BlendMode = BlendMode.ADD
        actual enum class BlendMode { ADD, INTERPOLATE }
    }

    actual class FogOptions actual constructor() {
        actual var enabled: Boolean = false
        actual var distance: Float = 0.0f
        actual var density: Float = 0.1f
        actual var height: Float = 0.0f
        actual var heightFalloff: Float = 1.0f
        actual var color: FloatArray = floatArrayOf(1.0f, 1.0f, 1.0f)
        actual var densityMap: Texture? = null
        actual var cutOffDistance: Float = Float.POSITIVE_INFINITY
        actual var maximumOpacity: Float = 1.0f
        actual var inScatteringStart: Float = 0.0f
        actual var inScatteringSize: Float = -1.0f
        actual var fogColorFromIbl: Boolean = false
        actual var skyColor: Texture? = null
    }

    actual class DepthOfFieldOptions actual constructor() {
        actual var enabled: Boolean = false
        actual var cocScale: Float = 1.0f
        actual var maxApertureDiameter: Float = 0.01f
        actual var filter: Filter = Filter.MEDIAN
        actual var nativeResolution: Boolean = false
        actual var foregroundRingCount: Int = 0
        actual var backgroundRingCount: Int = 0
        actual var fastGatherRingCount: Int = 0
        actual var maxForegroundCOC: Int = 0
        actual var maxBackgroundCOC: Int = 0
        actual enum class Filter { NONE, MEDIAN, GAUSSIAN }
    }

    actual class VignetteOptions actual constructor() {
        actual var enabled: Boolean = false
        actual var midPoint: Float = 0.5f
        actual var roundness: Float = 0.5f
        actual var feather: Float = 0.5f
        actual var color: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
    }

    actual class AmbientOcclusionOptions actual constructor() {
        actual var radius: Float = 0.3f
        actual var bias: Float = 0.0005f
        actual var intensity: Float = 1.0f
        actual var scale: Float = 0.5f
        actual var power: Float = 1.0f
        actual var minConeAngle: Float = 0.0f
        actual var quality: Quality = Quality.LOW
        actual var lowPassFilter: Quality = Quality.MEDIUM
        actual var upsampling: Quality = Quality.LOW
        actual var enabled: Boolean = false
        actual var bentNormals: Boolean = false
        actual var bilateralThreshold: Float = 0.05f
        actual var resolution: Float = 0.5f
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
        actual var feedback: Float = 0.12f
        actual var enabled: Boolean = false
        actual var lodBias: Float = -1.0f
        actual var sharpness: Float = 0.0f
        actual var upscaling: Float = 1.0f
        actual var filterHistory: Boolean = true
        actual var filterInput: Boolean = true
        actual var useYCoCg: Boolean = false
        actual var hdr: Boolean = true
        actual var boxType: Int = 0
        actual var boxClipping: Int = 0
        actual var jitterPattern: Int = 2
        actual var varianceGamma: Float = 1.0f
        actual var preventFlickering: Boolean = false
        actual var historyReprojection: Boolean = true
    }

    actual class ScreenSpaceReflectionsOptions actual constructor() {
        actual var enabled: Boolean = false
        actual var thickness: Float = 0.1f
        actual var bias: Float = 0.01f
        actual var maxDistance: Float = 3.0f
        actual var stride: Float = 2.0f
    }

    actual class VsmShadowOptions actual constructor() {
        actual var anisotropy: Int = 0
        actual var mipmapping: Boolean = false
        actual var msaaSamples: Int = 1
        actual var highPrecision: Boolean = false
        actual var lightBleedReduction: Float = 0.15f
    }

    actual class SoftShadowOptions actual constructor() {
        actual var penumbraScale: Float = 1.0f
        actual var penumbraRatioScale: Float = 1.0f
    }

    actual class GuardBandOptions actual constructor() {
        actual var enabled: Boolean = false
    }

    actual class StereoscopicOptions actual constructor() {
        actual var enabled: Boolean = false
    }

    actual class MultiSampleAntiAliasingOptions actual constructor() {
        actual var enabled: Boolean = false
        actual var sampleCount: Int = 4
        actual var customResolve: Boolean = false
    }

    actual fun setName(name: String) { FilaView_setName(nativeHandle, name) }
    actual fun getName(): String? = null

    actual fun setScene(scene: Scene?) { 
        mScene = scene
        FilaView_setScene(nativeHandle, scene?.nativeHandle) 
    }
    actual fun getScene(): Scene? = mScene
    
    actual fun setCamera(camera: Camera?) { 
        mCamera = camera
        FilaView_setCamera(nativeHandle, camera?.nativeHandle) 
    }
    actual fun getCamera(): Camera? = mCamera
    actual fun hasCamera(): Boolean = FilaView_hasCamera(nativeHandle)

    actual fun setViewport(viewport: Viewport) {
        FilaView_setViewport(nativeHandle, viewport.left, viewport.bottom, viewport.width.toUInt(), viewport.height.toUInt())
    }
    actual fun getViewport(): Viewport = Viewport(0, 0, 0, 0)

    actual fun setBlendMode(blendMode: BlendMode) {
        FilaView_setBlendMode(nativeHandle, blendMode.ordinal.toUInt())
    }
    actual fun getBlendMode(): BlendMode = BlendMode.values()[FilaView_getBlendMode(nativeHandle).toInt()]

    actual fun setVisibleLayers(select: Int, values: Int) {
        FilaView_setVisibleLayers(nativeHandle, select.toUByte(), values.toUByte())
    }
    actual fun setLayerEnabled(layer: Int, enabled: Boolean) {
        val mask = (1 shl layer).toUByte()
        FilaView_setVisibleLayers(nativeHandle, mask, if (enabled) mask else 0.toUByte())
    }
    actual fun getVisibleLayers(): Int = FilaView_getVisibleLayers(nativeHandle).toInt()

    actual fun setPostProcessingEnabled(enabled: Boolean) { FilaView_setPostProcessingEnabled(nativeHandle, enabled) }
    actual fun isPostProcessingEnabled(): Boolean = FilaView_isPostProcessingEnabled(nativeHandle)

    actual fun setDithering(dithering: Dithering) {
        FilaView_setDithering(nativeHandle, dithering.ordinal.toUInt())
    }
    actual fun getDithering(): Dithering = Dithering.values()[FilaView_getDithering(nativeHandle).toInt()]

    actual fun setDynamicResolutionOptions(options: DynamicResolutionOptions) {
        memScoped {
            val cOptions = alloc<FilaViewDynamicResolutionOptions>()
            cOptions.enabled = options.enabled
            cOptions.homogeneousScaling = options.homogeneousScaling
            cOptions.minScale[0] = options.minScale
            cOptions.minScale[1] = options.minScale
            cOptions.maxScale[0] = options.maxScale
            cOptions.maxScale[1] = options.maxScale
            cOptions.sharpness = options.sharpness
            cOptions.quality = options.quality.ordinal.toUInt()
            FilaView_setDynamicResolutionOptions(nativeHandle, cOptions.ptr)
        }
    }
    actual fun getDynamicResolutionOptions(): DynamicResolutionOptions = DynamicResolutionOptions()

    actual fun setRenderQuality(renderQuality: RenderQuality) {
        FilaView_setRenderQuality(nativeHandle, renderQuality.hdrColorBuffer.ordinal.toUInt())
    }
    actual fun getRenderQuality(): RenderQuality = RenderQuality()
    
    actual fun setBloomOptions(options: BloomOptions) {
        memScoped {
            val cOptions = alloc<FilaViewBloomOptions>()
            cOptions.enabled = options.enabled
            cOptions.levels = options.levels.toUByte()
            cOptions.resolution = options.resolution.toUInt()
            cOptions.strength = options.strength
            cOptions.threshold = options.threshold
            cOptions.dirt = options.dirt?.nativeHandle
            cOptions.dirtStrength = options.dirtStrength
            cOptions.quality = options.quality.ordinal.toUInt()
            cOptions.highlight = options.highlight
            cOptions.blendMode = options.blendMode.ordinal
            cOptions.chromaticAberration = options.chromaticAberration
            cOptions.lensFlare = options.lensFlare
            cOptions.starburst = options.starburst
            cOptions.ghostCount = options.ghostCount.toUByte()
            cOptions.ghostSpacing = options.ghostSpacing
            cOptions.ghostThreshold = options.ghostThreshold
            cOptions.haloRadius = options.haloRadius
            cOptions.haloThickness = options.haloThickness
            cOptions.haloThreshold = options.haloThreshold
            FilaView_setBloomOptions(nativeHandle, cOptions.ptr)
        }
    }
    actual fun getBloomOptions(): BloomOptions = BloomOptions()

    actual fun setFogOptions(options: FogOptions) {
        memScoped {
            val cOptions = alloc<FilaViewFogOptions>()
            cOptions.enabled = options.enabled
            cOptions.distance = options.distance
            cOptions.density = options.density
            cOptions.height = options.height
            cOptions.heightFalloff = options.heightFalloff
            cOptions.color[0] = options.color[0]; cOptions.color[1] = options.color[1]; cOptions.color[2] = options.color[2]
            cOptions.cutOffDistance = options.cutOffDistance
            cOptions.maximumOpacity = options.maximumOpacity
            cOptions.inScatteringStart = options.inScatteringStart
            cOptions.inScatteringSize = options.inScatteringSize
            cOptions.fogColorFromIbl = options.fogColorFromIbl
            cOptions.skyColor = options.skyColor?.nativeHandle
            FilaView_setFogOptions(nativeHandle, cOptions.ptr)
        }
    }
    actual fun getFogOptions(): FogOptions = FogOptions()

    actual fun setDepthOfFieldOptions(options: DepthOfFieldOptions) {
        memScoped {
            val cOptions = alloc<FilaViewDepthOfFieldOptions>()
            cOptions.enabled = options.enabled
            cOptions.cocScale = options.cocScale
            cOptions.maxApertureDiameter = options.maxApertureDiameter
            cOptions.filter = options.filter.ordinal
            cOptions.nativeResolution = options.nativeResolution
            cOptions.foregroundRingCount = options.foregroundRingCount.toUByte()
            cOptions.backgroundRingCount = options.backgroundRingCount.toUByte()
            cOptions.fastGatherRingCount = options.fastGatherRingCount.toUByte()
            cOptions.maxForegroundCOC = options.maxForegroundCOC.toUShort()
            cOptions.maxBackgroundCOC = options.maxBackgroundCOC.toUShort()
            FilaView_setDepthOfFieldOptions(nativeHandle, cOptions.ptr)
        }
    }
    actual fun getDepthOfFieldOptions(): DepthOfFieldOptions = DepthOfFieldOptions()

    actual fun setVignetteOptions(options: VignetteOptions) {
        memScoped {
            val cOptions = alloc<FilaViewVignetteOptions>()
            cOptions.enabled = options.enabled
            cOptions.midPoint = options.midPoint
            cOptions.roundness = options.roundness
            cOptions.feather = options.feather
            cOptions.color[0] = options.color[0]; cOptions.color[1] = options.color[1]; cOptions.color[2] = options.color[2]; cOptions.color[3] = options.color[3]
            FilaView_setVignetteOptions(nativeHandle, cOptions.ptr)
        }
    }
    actual fun getVignetteOptions(): VignetteOptions = VignetteOptions()

    actual fun setAmbientOcclusionOptions(options: AmbientOcclusionOptions) {
        memScoped {
            val cOptions = alloc<FilaViewAmbientOcclusionOptions>()
            cOptions.enabled = options.enabled
            cOptions.radius = options.radius
            cOptions.bias = options.bias
            cOptions.intensity = options.intensity
            cOptions.resolution = options.resolution
            cOptions.power = options.power
            cOptions.minHorizonAngleRad = options.minConeAngle
            cOptions.quality = options.quality.ordinal.toUInt()
            cOptions.lowPassFilter = options.lowPassFilter.ordinal.toUInt()
            cOptions.upsampling = options.upsampling.ordinal.toUInt()
            cOptions.bentNormals = options.bentNormals
            cOptions.bilateralThreshold = options.bilateralThreshold
            cOptions.ssct.enabled = options.ssct.enabled
            cOptions.ssct.lightConeRad = options.ssct.lightConeRad
            cOptions.ssct.shadowDistance = options.ssct.shadowDistance
            cOptions.ssct.contactDistanceMax = options.ssct.contactDistanceMax
            cOptions.ssct.intensity = options.ssct.intensity
            cOptions.ssct.lightDirection[0] = options.ssct.lightDirection[0]
            cOptions.ssct.lightDirection[1] = options.ssct.lightDirection[1]
            cOptions.ssct.lightDirection[2] = options.ssct.lightDirection[2]
            cOptions.ssct.depthBias = options.ssct.depthBias
            cOptions.ssct.depthSlopeBias = options.ssct.depthSlopeBias
            cOptions.ssct.sampleCount = options.ssct.sampleCount.toUByte()
            cOptions.ssct.rayCount = options.ssct.rayCount.toUByte()
            FilaView_setAmbientOcclusionOptions(nativeHandle, cOptions.ptr)
        }
    }
    actual fun getAmbientOcclusionOptions(): AmbientOcclusionOptions = AmbientOcclusionOptions()

    actual fun setTemporalAntiAliasingOptions(options: TemporalAntiAliasingOptions) {
        memScoped {
            val cOptions = alloc<FilaViewTemporalAntiAliasingOptions>()
            cOptions.enabled = options.enabled
            cOptions.feedback = options.feedback
            cOptions.lodBias = options.lodBias
            cOptions.sharpness = options.sharpness
            cOptions.upscaling = options.upscaling
            cOptions.filterHistory = options.filterHistory
            cOptions.filterInput = options.filterInput
            cOptions.useYCoCg = options.useYCoCg
            cOptions.hdr = options.hdr
            cOptions.boxType = options.boxType
            cOptions.boxClipping = options.boxClipping
            cOptions.jitterPattern = options.jitterPattern
            cOptions.varianceGamma = options.varianceGamma
            cOptions.preventFlickering = options.preventFlickering
            cOptions.historyReprojection = options.historyReprojection
            FilaView_setTemporalAntiAliasingOptions(nativeHandle, cOptions.ptr)
        }
    }
    actual fun getTemporalAntiAliasingOptions(): TemporalAntiAliasingOptions = TemporalAntiAliasingOptions()

    actual fun setScreenSpaceReflectionsOptions(options: ScreenSpaceReflectionsOptions) {
        memScoped {
            val cOptions = alloc<FilaViewScreenSpaceReflectionsOptions>()
            cOptions.enabled = options.enabled
            cOptions.thickness = options.thickness
            cOptions.bias = options.bias
            cOptions.maxDistance = options.maxDistance
            cOptions.stride = options.stride
            FilaView_setScreenSpaceReflectionsOptions(nativeHandle, cOptions.ptr)
        }
    }
    actual fun getScreenSpaceReflectionsOptions(): ScreenSpaceReflectionsOptions = ScreenSpaceReflectionsOptions()

    actual fun setRenderTarget(target: RenderTarget?) {
        mRenderTarget = target
        FilaView_setRenderTarget(nativeHandle, target?.nativeHandle)
    }
    actual fun getRenderTarget(): RenderTarget? = mRenderTarget

    actual fun setShadowType(type: ShadowType) {
        mShadowType = type
        FilaView_setShadowType(nativeHandle, type.ordinal.toUInt())
    }
    actual fun getShadowType(): ShadowType = mShadowType

    actual fun setVsmShadowOptions(options: VsmShadowOptions) {
        memScoped {
            val cOptions = alloc<FilaViewVsmShadowOptions>()
            cOptions.anisotropy = options.anisotropy.toUByte()
            cOptions.mipmapping = options.mipmapping
            cOptions.msaaSamples = options.msaaSamples.toUByte()
            cOptions.highPrecision = options.highPrecision
            cOptions.lightBleedReduction = options.lightBleedReduction
            FilaView_setVsmShadowOptions(nativeHandle, cOptions.ptr)
        }
    }
    actual fun getVsmShadowOptions(): VsmShadowOptions = VsmShadowOptions()

    actual fun setSoftShadowOptions(options: SoftShadowOptions) {
        memScoped {
            val cOptions = alloc<FilaViewSoftShadowOptions>()
            cOptions.penumbraScale = options.penumbraScale
            cOptions.penumbraRatioScale = options.penumbraRatioScale
            FilaView_setSoftShadowOptions(nativeHandle, cOptions.ptr)
        }
    }
    actual fun getSoftShadowOptions(): SoftShadowOptions = SoftShadowOptions()

    actual fun setGuardBandOptions(options: GuardBandOptions) {
        memScoped {
            val cOptions = alloc<FilaViewGuardBandOptions>()
            cOptions.enabled = options.enabled
            FilaView_setGuardBandOptions(nativeHandle, cOptions.ptr)
        }
    }
    actual fun getGuardBandOptions(): GuardBandOptions = GuardBandOptions()

    actual fun setStereoscopicOptions(options: StereoscopicOptions) {
        memScoped {
            val cOptions = alloc<FilaViewStereoscopicOptions>()
            cOptions.enabled = options.enabled
            FilaView_setStereoscopicOptions(nativeHandle, cOptions.ptr)
        }
    }
    actual fun getStereoscopicOptions(): StereoscopicOptions = StereoscopicOptions()

    actual fun setMultiSampleAntiAliasingOptions(options: MultiSampleAntiAliasingOptions) {
        memScoped {
            val cOptions = alloc<FilaViewMultiSampleAntiAliasingOptions>()
            cOptions.enabled = options.enabled
            cOptions.sampleCount = options.sampleCount.toUByte()
            cOptions.customResolve = options.customResolve
            FilaView_setMultiSampleAntiAliasingOptions(nativeHandle, cOptions.ptr)
        }
    }
    actual fun getMultiSampleAntiAliasingOptions(): MultiSampleAntiAliasingOptions = MultiSampleAntiAliasingOptions()

    actual fun setFrustumCullingEnabled(enabled: Boolean) { FilaView_setFrustumCullingEnabled(nativeHandle, enabled) }
    actual fun isFrustumCullingEnabled(): Boolean = FilaView_isFrustumCullingEnabled(nativeHandle)
    actual fun setShadowingEnabled(enabled: Boolean) { FilaView_setShadowingEnabled(nativeHandle, enabled) }
    actual fun setScreenSpaceRefractionEnabled(enabled: Boolean) { FilaView_setScreenSpaceRefractionEnabled(nativeHandle, enabled) }
    actual fun setStencilBufferEnabled(enabled: Boolean) { FilaView_setStencilBufferEnabled(nativeHandle, enabled) }
    actual fun isStencilBufferEnabled(): Boolean = FilaView_isStencilBufferEnabled(nativeHandle)
    actual fun setFrontFaceWindingInverted(inverted: Boolean) { FilaView_setFrontFaceWindingInverted(nativeHandle, inverted) }
    actual fun isFrontFaceWindingInverted(): Boolean = FilaView_isFrontFaceWindingInverted(nativeHandle)
    actual fun setTransparentPickingEnabled(enabled: Boolean) { FilaView_setTransparentPickingEnabled(nativeHandle, enabled) }
    actual fun isTransparentPickingEnabled(): Boolean = FilaView_isTransparentPickingEnabled(nativeHandle)

    actual fun setMaterialGlobal(index: Int, value: FloatArray) {
        FilaView_setMaterialGlobal(nativeHandle, index.toUInt(), value[0], value[1], value[2], value[3])
    }
    actual fun getMaterialGlobal(index: Int): FloatArray {
        val result = FloatArray(4)
        memScoped {
            val out = allocArray<FloatVar>(4)
            FilaView_getMaterialGlobal(nativeHandle, index.toUInt(), out)
            result[0] = out[0]; result[1] = out[1]; result[2] = out[2]; result[3] = out[3]
        }
        return result
    }
    actual fun getFogEntity(): Int = FilaView_getFogEntity(nativeHandle).toInt()
    actual fun clearFrameHistory(engine: Engine) { FilaView_clearFrameHistory(nativeHandle, engine.nativeHandle) }
}
