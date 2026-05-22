@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*
import cnames.structs.FilaView

actual class View internal constructor(internal var nativeHandle: CPointer<FilaView>?) {
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

    private var mScene: Scene? = null
    private var mCamera: Camera? = null
    private var mRenderTarget: RenderTarget? = null
    private var mShadowType: ShadowType = ShadowType.PCF
    private var mColorGrading: ColorGrading? = null

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
        actual var color: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)
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

    actual var name: String?
        get() = FilaView_getName(nativeHandle)?.toKString()
        set(value) { FilaView_setName(nativeHandle, value ?: "") }

    actual var scene: Scene?
        get() = mScene
        set(value) {
            mScene = value
            FilaView_setScene(nativeHandle, value?.nativeHandle)
        }
    
    actual var camera: Camera?
        get() = mCamera
        set(value) {
            mCamera = value
            FilaView_setCamera(nativeHandle, value?.nativeHandle)
        }
    actual val hasCamera: Boolean get() = FilaView_hasCamera(nativeHandle)

    actual var viewport: Viewport
        get() = memScoped {
            val left   = alloc<IntVar>()
            val bottom = alloc<IntVar>()
            val width  = alloc<UIntVar>()
            val height = alloc<UIntVar>()
            FilaView_getViewport(nativeHandle, left.ptr, bottom.ptr, width.ptr, height.ptr)
            Viewport(left.value, bottom.value, width.value.toInt(), height.value.toInt())
        }
        set(value) { FilaView_setViewport(nativeHandle, value.left, value.bottom, value.width.toUInt(), value.height.toUInt()) }

    actual var blendMode: BlendMode
        get() = BlendMode.values()[FilaView_getBlendMode(nativeHandle).toInt()]
        set(value) { FilaView_setBlendMode(nativeHandle, value.ordinal.toUInt()) }

    actual fun setVisibleLayers(select: Int, values: Int) { FilaView_setVisibleLayers(nativeHandle, select.toUByte(), values.toUByte()) }
    actual fun setLayerEnabled(layer: Int, enabled: Boolean) {
        val mask = (1 shl layer).toUByte()
        FilaView_setVisibleLayers(nativeHandle, mask, if (enabled) mask else 0u)
    }
    actual fun getVisibleLayers(): Int = FilaView_getVisibleLayers(nativeHandle).toInt()

    actual var isPostProcessingEnabled: Boolean
        get() = FilaView_isPostProcessingEnabled(nativeHandle)
        set(value) { FilaView_setPostProcessingEnabled(nativeHandle, value) }

    actual var dithering: Dithering
        get() = Dithering.values()[FilaView_getDithering(nativeHandle).toInt()]
        set(value) { FilaView_setDithering(nativeHandle, value.ordinal.toUInt()) }

    actual var dynamicResolutionOptions: DynamicResolutionOptions
        get() = DynamicResolutionOptions()
        set(value) {
            memScoped {
                val cOptions = alloc<FilaViewDynamicResolutionOptions>()
                cOptions.enabled = value.enabled
                cOptions.homogeneousScaling = value.homogeneousScaling
                cOptions.minScale[0] = value.minScale
                cOptions.minScale[1] = value.minScale
                cOptions.maxScale[0] = value.maxScale
                cOptions.maxScale[1] = value.maxScale
                cOptions.sharpness = value.sharpness
                cOptions.quality = value.quality.ordinal.toUInt()
                FilaView_setDynamicResolutionOptions(nativeHandle, cOptions.ptr)
            }
        }

    actual fun getLastDynamicResolutionScale(): FloatArray = memScoped {
        val out = allocArray<FloatVar>(2)
        FilaView_getLastDynamicResolutionScale(nativeHandle, out)
        floatArrayOf(out[0], out[1])
    }

    actual var renderQuality: RenderQuality
        get() = RenderQuality()
        set(value) { FilaView_setRenderQuality(nativeHandle, value.hdrColorBuffer.ordinal.toUInt()) }
    
    actual var bloomOptions: BloomOptions
        get() = BloomOptions()
        set(value) {
            memScoped {
                val cOptions = alloc<FilaViewBloomOptions>()
                cOptions.enabled = value.enabled
                cOptions.levels = value.levels.toUByte()
                cOptions.resolution = value.resolution.toUInt()
                cOptions.strength = value.strength
                cOptions.threshold = value.threshold
                cOptions.dirt = value.dirt?.nativeHandle
                cOptions.dirtStrength = value.dirtStrength
                cOptions.quality = value.quality.ordinal.toUInt()
                cOptions.highlight = value.highlight
                cOptions.blendMode = value.blendMode.ordinal
                cOptions.chromaticAberration = value.chromaticAberration
                cOptions.lensFlare = value.lensFlare
                cOptions.starburst = value.starburst
                cOptions.ghostCount = value.ghostCount.toUByte()
                cOptions.ghostSpacing = value.ghostSpacing
                cOptions.ghostThreshold = value.ghostThreshold
                cOptions.haloRadius = value.haloRadius
                cOptions.haloThickness = value.haloThickness
                cOptions.haloThreshold = value.haloThreshold
                FilaView_setBloomOptions(nativeHandle, cOptions.ptr)
            }
        }

    actual var fogOptions: FogOptions
        get() = FogOptions()
        set(value) {
            memScoped {
                val cOptions = alloc<FilaViewFogOptions>()
                cOptions.enabled = value.enabled
                cOptions.distance = value.distance
                cOptions.density = value.density
                cOptions.height = value.height
                cOptions.heightFalloff = value.heightFalloff
                cOptions.color[0] = value.color[0]; cOptions.color[1] = value.color[1]; cOptions.color[2] = value.color[2]
                cOptions.cutOffDistance = value.cutOffDistance
                cOptions.maximumOpacity = value.maximumOpacity
                cOptions.inScatteringStart = value.inScatteringStart
                cOptions.inScatteringSize = value.inScatteringSize
                cOptions.fogColorFromIbl = value.fogColorFromIbl
                cOptions.skyColor = value.skyColor?.nativeHandle
                FilaView_setFogOptions(nativeHandle, cOptions.ptr)
            }
        }

    actual var depthOfFieldOptions: DepthOfFieldOptions
        get() = DepthOfFieldOptions()
        set(value) {
            memScoped {
                val cOptions = alloc<FilaViewDepthOfFieldOptions>()
                cOptions.enabled = value.enabled
                cOptions.cocScale = value.cocScale
                cOptions.maxApertureDiameter = value.maxApertureDiameter
                cOptions.filter = value.filter.ordinal
                cOptions.nativeResolution = value.nativeResolution
                cOptions.foregroundRingCount = value.foregroundRingCount.toUByte()
                cOptions.backgroundRingCount = value.backgroundRingCount.toUByte()
                cOptions.fastGatherRingCount = value.fastGatherRingCount.toUByte()
                cOptions.maxForegroundCOC = value.maxForegroundCOC.toUShort()
                cOptions.maxBackgroundCOC = value.maxBackgroundCOC.toUShort()
                FilaView_setDepthOfFieldOptions(nativeHandle, cOptions.ptr)
            }
        }

    actual var vignetteOptions: VignetteOptions
        get() = VignetteOptions()
        set(value) {
            memScoped {
                val cOptions = alloc<FilaViewVignetteOptions>()
                cOptions.enabled = value.enabled
                cOptions.midPoint = value.midPoint
                cOptions.roundness = value.roundness
                cOptions.feather = value.feather
                cOptions.color[0] = value.color[0]; cOptions.color[1] = value.color[1]; cOptions.color[2] = value.color[2]; cOptions.color[3] = value.color[3]
                FilaView_setVignetteOptions(nativeHandle, cOptions.ptr)
            }
        }

    actual var ambientOcclusionOptions: AmbientOcclusionOptions
        get() = AmbientOcclusionOptions()
        set(value) {
            memScoped {
                val cOptions = alloc<FilaViewAmbientOcclusionOptions>()
                cOptions.enabled = value.enabled
                cOptions.radius = value.radius
                cOptions.bias = value.bias
                cOptions.intensity = value.intensity
                cOptions.resolution = value.resolution
                cOptions.power = value.power
                cOptions.minHorizonAngleRad = value.minConeAngle
                cOptions.quality = value.quality.ordinal.toUInt()
                cOptions.lowPassFilter = value.lowPassFilter.ordinal.toUInt()
                cOptions.upsampling = value.upsampling.ordinal.toUInt()
                cOptions.bentNormals = value.bentNormals
                cOptions.bilateralThreshold = value.bilateralThreshold
                cOptions.ssct.enabled = value.ssct.enabled
                cOptions.ssct.lightConeRad = value.ssct.lightConeRad
                cOptions.ssct.shadowDistance = value.ssct.shadowDistance
                cOptions.ssct.contactDistanceMax = value.ssct.contactDistanceMax
                cOptions.ssct.intensity = value.ssct.intensity
                cOptions.ssct.lightDirection[0] = value.ssct.lightDirection[0]
                cOptions.ssct.lightDirection[1] = value.ssct.lightDirection[1]
                cOptions.ssct.lightDirection[2] = value.ssct.lightDirection[2]
                cOptions.ssct.depthBias = value.ssct.depthBias
                cOptions.ssct.depthSlopeBias = value.ssct.depthSlopeBias
                cOptions.ssct.sampleCount = value.ssct.sampleCount.toUByte()
                cOptions.ssct.rayCount = value.ssct.rayCount.toUByte()
                FilaView_setAmbientOcclusionOptions(nativeHandle, cOptions.ptr)
            }
        }

    actual var temporalAntiAliasingOptions: TemporalAntiAliasingOptions
        get() = TemporalAntiAliasingOptions()
        set(value) {
            memScoped {
                val cOptions = alloc<FilaViewTemporalAntiAliasingOptions>()
                cOptions.enabled = value.enabled
                cOptions.feedback = value.feedback
                cOptions.lodBias = value.lodBias
                cOptions.sharpness = value.sharpness
                cOptions.upscaling = value.upscaling
                cOptions.filterHistory = value.filterHistory
                cOptions.filterInput = value.filterInput
                cOptions.useYCoCg = value.useYCoCg
                cOptions.hdr = value.hdr
                cOptions.boxType = value.boxType
                cOptions.boxClipping = value.boxClipping
                cOptions.jitterPattern = value.jitterPattern
                cOptions.varianceGamma = value.varianceGamma
                cOptions.preventFlickering = value.preventFlickering
                cOptions.historyReprojection = value.historyReprojection
                FilaView_setTemporalAntiAliasingOptions(nativeHandle, cOptions.ptr)
            }
        }

    actual var screenSpaceReflectionsOptions: ScreenSpaceReflectionsOptions
        get() = ScreenSpaceReflectionsOptions()
        set(value) {
            memScoped {
                val cOptions = alloc<FilaViewScreenSpaceReflectionsOptions>()
                cOptions.enabled = value.enabled
                cOptions.thickness = value.thickness
                cOptions.bias = value.bias
                cOptions.maxDistance = value.maxDistance
                cOptions.stride = value.stride
                FilaView_setScreenSpaceReflectionsOptions(nativeHandle, cOptions.ptr)
            }
        }

    actual var renderTarget: RenderTarget?
        get() = mRenderTarget
        set(value) {
            mRenderTarget = value
            FilaView_setRenderTarget(nativeHandle, value?.nativeHandle)
        }

    actual var shadowType: ShadowType
        get() = mShadowType
        set(value) {
            mShadowType = value
            FilaView_setShadowType(nativeHandle, value.ordinal.toUInt())
        }

    actual var vsmShadowOptions: VsmShadowOptions
        get() = VsmShadowOptions()
        set(value) {
            memScoped {
                val cOptions = alloc<FilaViewVsmShadowOptions>()
                cOptions.anisotropy = value.anisotropy.toUByte()
                cOptions.mipmapping = value.mipmapping
                cOptions.msaaSamples = value.msaaSamples.toUByte()
                cOptions.highPrecision = value.highPrecision
                cOptions.lightBleedReduction = value.lightBleedReduction
                FilaView_setVsmShadowOptions(nativeHandle, cOptions.ptr)
            }
        }

    actual var softShadowOptions: SoftShadowOptions
        get() = SoftShadowOptions()
        set(value) {
            memScoped {
                val cOptions = alloc<FilaViewSoftShadowOptions>()
                cOptions.penumbraScale = value.penumbraScale
                cOptions.penumbraRatioScale = value.penumbraRatioScale
                FilaView_setSoftShadowOptions(nativeHandle, cOptions.ptr)
            }
        }

    actual var guardBandOptions: GuardBandOptions
        get() = GuardBandOptions()
        set(value) {
            memScoped {
                val cOptions = alloc<FilaViewGuardBandOptions>()
                cOptions.enabled = value.enabled
                FilaView_setGuardBandOptions(nativeHandle, cOptions.ptr)
            }
        }

    actual var stereoscopicOptions: StereoscopicOptions
        get() = StereoscopicOptions()
        set(value) {
            memScoped {
                val cOptions = alloc<FilaViewStereoscopicOptions>()
                cOptions.enabled = value.enabled
                FilaView_setStereoscopicOptions(nativeHandle, cOptions.ptr)
            }
        }

    actual var multiSampleAntiAliasingOptions: MultiSampleAntiAliasingOptions
        get() = MultiSampleAntiAliasingOptions()
        set(value) {
            memScoped {
                val cOptions = alloc<FilaViewMultiSampleAntiAliasingOptions>()
                cOptions.enabled = value.enabled
                cOptions.sampleCount = value.sampleCount.toUByte()
                cOptions.customResolve = value.customResolve
                FilaView_setMultiSampleAntiAliasingOptions(nativeHandle, cOptions.ptr)
            }
        }

    actual var isFrustumCullingEnabled: Boolean
        get() = FilaView_isFrustumCullingEnabled(nativeHandle)
        set(value) { FilaView_setFrustumCullingEnabled(nativeHandle, value) }
    actual var isShadowingEnabled: Boolean
        get() = FilaView_isShadowingEnabled(nativeHandle)
        set(value) { FilaView_setShadowingEnabled(nativeHandle, value) }
    actual var isScreenSpaceRefractionEnabled: Boolean
        get() = FilaView_isScreenSpaceRefractionEnabled(nativeHandle)
        set(value) { FilaView_setScreenSpaceRefractionEnabled(nativeHandle, value) }
    actual var isStencilBufferEnabled: Boolean
        get() = FilaView_isStencilBufferEnabled(nativeHandle)
        set(value) { FilaView_setStencilBufferEnabled(nativeHandle, value) }
    actual var isFrontFaceWindingInverted: Boolean
        get() = FilaView_isFrontFaceWindingInverted(nativeHandle)
        set(value) { FilaView_setFrontFaceWindingInverted(nativeHandle, value) }
    actual var isTransparentPickingEnabled: Boolean
        get() = FilaView_isTransparentPickingEnabled(nativeHandle)
        set(value) { FilaView_setTransparentPickingEnabled(nativeHandle, value) }

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
    actual val fogEntity: Int get() = FilaView_getFogEntity(nativeHandle).toInt()
    actual fun clearFrameHistory(engine: Engine) { FilaView_clearFrameHistory(nativeHandle, engine.nativeHandle) }

    actual fun setDynamicLightingOptions(zNear: Float, zFar: Float) {
        FilaView_setDynamicLightingOptions(nativeHandle, zNear, zFar)
    }

    actual var antiAliasing: AntiAliasing
        get() = AntiAliasing.values()[FilaView_getAntiAliasing(nativeHandle).toInt()]
        set(value) { FilaView_setAntiAliasing(nativeHandle, value.ordinal.toUInt()) }

    actual var colorGrading: ColorGrading?
        get() = mColorGrading
        set(value) {
            mColorGrading = value
            FilaView_setColorGrading(nativeHandle, value?.nativeHandle)
        }

    actual fun pick(x: Int, y: Int, callback: (PickingQueryResult) -> Unit) {
        val stableRef = kotlinx.cinterop.StableRef.create(callback)
        val cCallback = staticCFunction { result: CPointer<FilaViewPickingQueryResult>?, user: COpaquePointer? ->
            val ref = user!!.asStableRef<(PickingQueryResult) -> Unit>()
            result?.pointed?.let { r ->
                ref.get().invoke(PickingQueryResult(
                    r.renderable.toInt(),
                    r.depth,
                    floatArrayOf(r.fragCoords[0], r.fragCoords[1], r.fragCoords[2])
                ))
            }
            ref.dispose()
        }
        FilaView_pick(nativeHandle, x.toUInt(), y.toUInt(), null, cCallback, stableRef.asCPointer())
    }
}
