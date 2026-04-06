package dev.filament.kmp

import kotlinx.cinterop.*
import filament.*

actual class View internal constructor(
    internal val nativeObject: CPointer<FilaView>
) {
    actual enum class AntiAliasing {
        NONE, FXAA;
        internal fun toNative() = when (this) {
            NONE -> FILA_VIEW_ANTI_ALIASING_NONE
            FXAA -> FILA_VIEW_ANTI_ALIASING_FXAA
        }
        internal companion object {
            fun fromNative(v: FilaViewAntiAliasing) = when (v) {
                FILA_VIEW_ANTI_ALIASING_NONE -> NONE
                FILA_VIEW_ANTI_ALIASING_FXAA -> FXAA
                else -> NONE
            }
        }
    }

    actual enum class Dithering {
        NONE, TEMPORAL;
        internal fun toNative() = when (this) {
            NONE -> FILA_VIEW_DITHERING_NONE
            TEMPORAL -> FILA_VIEW_DITHERING_TEMPORAL
        }
    }

    actual enum class ShadowType {
        PCF, VSM, DPCF, PCSS, PCFd;
        internal fun toNative() = when (this) {
            PCF -> FILA_VIEW_SHADOW_TYPE_PCF
            VSM -> FILA_VIEW_SHADOW_TYPE_VSM
            else -> FILA_VIEW_SHADOW_TYPE_PCF
        }
    }

    actual enum class ToneMapping {
        LINEAR, ACES, FILMIC, ACES_LEGACY, DISPLAY_RANGE;
    }

    actual enum class AmbientOcclusion {
        NONE, SSAO;
    }

    actual enum class BlendMode {
        OPAQUE, TRANSLUCENT;
        internal fun toNative() = when (this) {
            OPAQUE -> FILA_VIEW_BLEND_MODE_OPAQUE
            TRANSLUCENT -> FILA_VIEW_BLEND_MODE_TRANSLUCENT
        }
    }

    actual enum class QualityLevel {
        LOW, MEDIUM, HIGH, ULTRA;
        internal fun toNative() = when (this) {
            LOW -> FILA_VIEW_QUALITY_LEVEL_LOW
            MEDIUM -> FILA_VIEW_QUALITY_LEVEL_MEDIUM
            HIGH -> FILA_VIEW_QUALITY_LEVEL_HIGH
            ULTRA -> FILA_VIEW_QUALITY_LEVEL_ULTRA
        }
    }

    actual class DynamicResolutionOptions actual constructor() {
        actual var minScale: FloatArray = floatArrayOf(0.5f, 0.5f)
        actual var maxScale: FloatArray = floatArrayOf(1.0f, 1.0f)
        actual var sharpness: Float = 0.9f
        actual var enabled: Boolean = false
        actual var homogeneousScaling: Boolean = false
        actual var quality: QualityLevel = QualityLevel.LOW
    }

    actual class RenderQuality actual constructor() {
        actual var hdrColorBufferQuality: QualityLevel = QualityLevel.HIGH
    }

    actual class AmbientOcclusionOptions actual constructor() {
        actual var radius: Float = 0.3f
        actual var bias: Float = 0.01f
        actual var power: Float = 1.0f
        actual var resolution: Float = 0.5f
        actual var intensity: Float = 1.0f
        actual var bilateralThreshold: Float = 0.05f
        actual var quality: QualityLevel = QualityLevel.LOW
        actual var lowPassFilter: QualityLevel = QualityLevel.MEDIUM
        actual var upsampling: QualityLevel = QualityLevel.LOW
        actual var enabled: Boolean = false
        actual var bentNormals: Boolean = false
        actual var minHorizonAngleRad: Float = 0.0f
    }

    actual class BloomOptions actual constructor() {
        actual var dirt: Texture? = null
        actual var dirtStrength: Float = 0.2f
        actual var strength: Float = 0.10f
        actual var resolution: Int = 0
        actual var levels: Int = 6
        actual var blendMode: BlendMode = BlendMode.OPAQUE
        actual var threshold: Boolean = true
        actual var enabled: Boolean = false
        actual var highlight: Float = 0.0f
        actual var lensFlare: Boolean = false
        actual var starburst: Boolean = false
        actual var chromaticAberration: Float = 0.0f
        actual var ghostCount: Int = 4
        actual var ghostSpacing: Float = 0.3f
        actual var ghostThreshold: Float = 0.6f
        actual var haloThickness: Float = 0.1f
        actual var haloRadius: Float = 0.15f
        actual var haloThreshold: Float = 0.5f
    }

    // Fog, DOF, Vignette, TAA, MSAA, SSR, VSM, SoftShadow, GuardBand, Stereoscopic etc...
    actual class FogOptions actual constructor() {
        actual var distance: Float = 0.0f
        actual var cutOffDistance: Float = Float.POSITIVE_INFINITY
        actual var maximumOpacity: Float = 1.0f
        actual var height: Float = 0.0f
        actual var heightFalloff: Float = 1.0f
        actual var color: FloatArray = floatArrayOf(0.5f, 0.5f, 0.5f)
        actual var density: Float = 0.1f
        actual var inScatteringStart: Float = 0.0f
        actual var inScatteringSize: Float = -1.0f
        actual var fogColorFromIbl: Boolean = false
        actual var skyColor: Texture? = null
        actual var enabled: Boolean = false
    }

    actual class DepthOfFieldOptions actual constructor() {
        actual var cocScale: Float = 1.0f
        actual var maxApertureDiameter: Float = 0.01f
        actual var enabled: Boolean = false
        actual var filter: Int = 0
        actual var nativeResolution: Boolean = false
        actual var foregroundRingCount: Int = 0
        actual var backgroundRingCount: Int = 0
        actual var fastGatherRingCount: Int = 0
        actual var maxForegroundCOC: Int = 0
        actual var maxBackgroundCOC: Int = 0
    }

    actual class VignetteOptions actual constructor() {
        actual var midPoint: Float = 0.5f
        actual var roundness: Float = 0.0f
        actual var feather: Float = 0.0f
        actual var color: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
        actual var enabled: Boolean = false
    }

    actual class TemporalAntiAliasingOptions actual constructor() {
        actual var filterWidth: Float = 1.0f
        actual var feedback: Float = 0.12f
        actual var enabled: Boolean = false
    }

    actual class MultiSampleAntiAliasingOptions actual constructor() {
        actual var enabled: Boolean = false
        actual var sampleCount: Int = 4
        actual var customResolve: Boolean = false
    }

    actual class ScreenSpaceReflectionsOptions actual constructor() {
        actual var thickness: Float = 0.1f
        actual var bias: Float = 0.01f
        actual var maxDistance: Float = 3.0f
        actual var stride: Float = 2.0f
        actual var enabled: Boolean = false
    }

    actual class VsmShadowOptions actual constructor() {
        actual var anisotropy: Int = 1
        actual var mipmapping: Boolean = false
        actual var highPrecision: Boolean = false
        actual var minVarianceScale: Float = 0.5f
        actual var lightBleedReduction: Float = 0.15f
    }

    actual class SoftShadowOptions actual constructor() {
        actual var penumbraScale: Float = 1.0f
        actual var penumbraRatioScale: Float = 1.0f
    }

    actual class GuardBandOptions actual constructor()
    actual class StereoscopicOptions actual constructor()

    actual class PickingQueryResult actual constructor(actual val renderable: Int, actual val depth: Float, actual val fragCoords: FloatArray)

    actual interface OnPickCallback {
        actual fun onPick(result: PickingQueryResult)
    }

    actual var name: String? = null
        set(value) {
            field = value
            value?.let { FilaView_setName(nativeObject, it) }
        }

    actual var scene: Scene? = null
        set(value) {
            field = value
            FilaView_setScene(nativeObject, value?.nativeObject)
        }

    actual var camera: Camera? = null
        set(value) {
            field = value
            FilaView_setCamera(nativeObject, value?.nativeObject)
        }

    actual fun hasCamera(): Boolean = FilaView_hasCamera(nativeObject)

    actual var viewport: Viewport = Viewport(0, 0, 0, 0)
        set(value) {
            field = value
            FilaView_setViewport(nativeObject, value.left, value.bottom, value.width.toUInt(), value.height.toUInt())
        }

    actual fun setVisibleLayers(select: Int, values: Int) {
        FilaView_setVisibleLayers(nativeObject, select.toUByte(), values.toUByte())
    }

    actual val visibleLayers: Int get() = FilaView_getVisibleLayers(nativeObject).toInt()

    actual fun setLayerEnabled(layer: Int, enabled: Boolean) {
        // ... handled via set_visible_layers if needed or another C helper
    }

    actual fun setShadowingEnabled(enabled: Boolean) {
        FilaView_setShadowingEnabled(nativeObject, enabled)
    }

    actual var isFrustumCullingEnabled: Boolean = true
        get() = FilaView_isFrustumCullingEnabled(nativeObject)
        set(value) {
            field = value
            FilaView_setFrustumCullingEnabled(nativeObject, value)
        }

    actual fun setScreenSpaceRefractionEnabled(enabled: Boolean) {
        FilaView_setScreenSpaceRefractionEnabled(nativeObject, enabled)
    }

    actual var renderTarget: RenderTarget? = null
        set(value) {
            field = value
            FilaView_setRenderTarget(nativeObject, value?.nativeObject)
        }

    actual var sampleCount: Int = 1
        get() = FilaView_getSampleCount(nativeObject).toInt()
        set(value) {
            field = value
            FilaView_setSampleCount(nativeObject, value.toUByte())
        }

    actual var antiAliasing: AntiAliasing = AntiAliasing.NONE
        get() = AntiAliasing.fromNative(FilaView_getAntiAliasing(nativeObject))
        set(value) {
            field = value
            FilaView_setAntiAliasing(nativeObject, value.toNative())
        }

    actual var multiSampleAntiAliasingOptions: MultiSampleAntiAliasingOptions = MultiSampleAntiAliasingOptions()
        set(value) {
            field = value
            memScoped {
                val nativeOptions = alloc<FilaViewMultiSampleAntiAliasingOptions>()
                nativeOptions.enabled = value.enabled
                nativeOptions.sampleCount = value.sampleCount.toUByte()
                nativeOptions.customResolve = value.customResolve
                FilaView_setMultiSampleAntiAliasingOptions(nativeObject, nativeOptions.ptr)
            }
        }

    actual var temporalAntiAliasingOptions: TemporalAntiAliasingOptions = TemporalAntiAliasingOptions()
        set(value) {
            field = value
            memScoped {
                val nativeOptions = alloc<FilaViewTemporalAntiAliasingOptions>()
                nativeOptions.enabled = value.enabled
                nativeOptions.filterWidth = value.filterWidth
                nativeOptions.feedback = value.feedback
                FilaView_setTemporalAntiAliasingOptions(nativeObject, nativeOptions.ptr)
            }
        }

    actual var screenSpaceReflectionsOptions: ScreenSpaceReflectionsOptions = ScreenSpaceReflectionsOptions()
        set(value) {
            field = value
            memScoped {
                val nativeOptions = alloc<FilaViewScreenSpaceReflectionsOptions>()
                nativeOptions.enabled = value.enabled
                nativeOptions.thickness = value.thickness
                nativeOptions.bias = value.bias
                nativeOptions.maxDistance = value.maxDistance
                nativeOptions.stride = value.stride
                FilaView_setScreenSpaceReflectionsOptions(nativeObject, nativeOptions.ptr)
            }
        }

    actual var guardBandOptions: GuardBandOptions = GuardBandOptions()
    actual var toneMapping: ToneMapping = ToneMapping.LINEAR
    actual var colorGrading: ColorGrading? = null
        set(value) {
            field = value
            FilaView_setColorGrading(nativeObject, value?.nativeObject)
        }

    actual var dithering: Dithering = Dithering.NONE
        set(value) {
            field = value
            FilaView_setDithering(nativeObject, value.toNative())
        }

    actual var dynamicResolutionOptions: DynamicResolutionOptions = DynamicResolutionOptions()
        set(value) {
            field = value
            memScoped {
                val nativeOptions = alloc<FilaViewDynamicResolutionOptions>()
                nativeOptions.enabled = value.enabled
                nativeOptions.homogeneousScaling = value.homogeneousScaling
                nativeOptions.minScale[0] = value.minScale[0]; nativeOptions.minScale[1] = value.minScale[1]
                nativeOptions.maxScale[0] = value.maxScale[0]; nativeOptions.maxScale[1] = value.maxScale[1]
                nativeOptions.sharpness = value.sharpness
                nativeOptions.quality = value.quality.toNative()
                FilaView_setDynamicResolutionOptions(nativeObject, nativeOptions.ptr)
            }
        }

    actual fun getLastDynamicResolutionScale(out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(2)
        memScoped {
            val scale = allocArray<FloatVar>(2)
            FilaView_getLastDynamicResolutionScale(nativeObject, scale)
            result[0] = scale[0]; result[1] = scale[1]
        }
        return result
    }

    actual var renderQuality: RenderQuality = RenderQuality()
        set(value) {
            field = value
            FilaView_setRenderQuality(nativeObject, value.hdrColorBufferQuality.toNative())
        }

    actual var isPostProcessingEnabled: Boolean = true
        get() = FilaView_isPostProcessingEnabled(nativeObject)
        set(value) {
            field = value
            FilaView_setPostProcessingEnabled(nativeObject, value)
        }

    actual var isFrontFaceWindingInverted: Boolean = false
        get() = FilaView_isFrontFaceWindingInverted(nativeObject)
        set(value) {
            field = value
            FilaView_setFrontFaceWindingInverted(nativeObject, value)
        }

    actual var isTransparentPickingEnabled: Boolean = false
        get() = FilaView_isTransparentPickingEnabled(nativeObject)
        set(value) {
            field = value
            FilaView_setTransparentPickingEnabled(nativeObject, value)
        }

    actual fun setDynamicLightingOptions(zLightNear: Float, zLightFar: Float) {
        FilaView_setDynamicLightingOptions(nativeObject, zLightNear, zLightFar)
    }

    actual fun setShadowType(type: ShadowType) {
        FilaView_setShadowType(nativeObject, type.toNative())
    }

    actual var vsmShadowOptions: VsmShadowOptions = VsmShadowOptions()
        set(value) {
            field = value
            memScoped {
                val nativeOptions = alloc<FilaViewVsmShadowOptions>()
                nativeOptions.anisotropy = value.anisotropy.toUByte()
                nativeOptions.mipmapping = value.mipmapping
                nativeOptions.highPrecision = value.highPrecision
                nativeOptions.minVarianceScale = value.minVarianceScale
                nativeOptions.lightBleedReduction = value.lightBleedReduction
                FilaView_setVsmShadowOptions(nativeObject, nativeOptions.ptr)
            }
        }

    actual var softShadowOptions: SoftShadowOptions = SoftShadowOptions()
        set(value) {
            field = value
            memScoped {
                val nativeOptions = alloc<FilaViewSoftShadowOptions>()
                nativeOptions.penumbraScale = value.penumbraScale
                nativeOptions.penumbraRatioScale = value.penumbraRatioScale
                FilaView_setSoftShadowOptions(nativeObject, nativeOptions.ptr)
            }
        }

    actual var ambientOcclusion: AmbientOcclusion = AmbientOcclusion.NONE
    actual var ambientOcclusionOptions: AmbientOcclusionOptions = AmbientOcclusionOptions()
        set(value) {
            field = value
            memScoped {
                val nativeOptions = alloc<FilaViewAmbientOcclusionOptions>()
                nativeOptions.enabled = value.enabled
                nativeOptions.radius = value.radius
                nativeOptions.bias = value.bias
                nativeOptions.power = value.power
                nativeOptions.resolution = value.resolution
                nativeOptions.intensity = value.intensity
                nativeOptions.bilateralThreshold = value.bilateralThreshold
                nativeOptions.quality = value.quality.toNative()
                nativeOptions.lowPassFilter = value.lowPassFilter.toNative()
                nativeOptions.upsampling = value.upsampling.toNative()
                nativeOptions.bentNormals = value.bentNormals
                nativeOptions.minHorizonAngleRad = value.minHorizonAngleRad
                FilaView_setAmbientOcclusionOptions(nativeObject, nativeOptions.ptr)
            }
        }

    actual var bloomOptions: BloomOptions = BloomOptions()
        set(value) {
            field = value
            memScoped {
                val nativeOptions = alloc<FilaViewBloomOptions>()
                nativeOptions.enabled = value.enabled
                nativeOptions.strength = value.strength
                nativeOptions.resolution = value.resolution.toUInt()
                nativeOptions.levels = value.levels.toUByte()
                nativeOptions.blendMode = value.blendMode.toNative().value.toInt()
                nativeOptions.threshold = value.threshold
                FilaView_setBloomOptions(nativeObject, nativeOptions.ptr)
            }
        }

    actual var vignetteOptions: VignetteOptions = VignetteOptions()
        set(value) {
            field = value
            memScoped {
                val nativeOptions = alloc<FilaViewVignetteOptions>()
                nativeOptions.enabled = value.enabled
                nativeOptions.midPoint = value.midPoint
                nativeOptions.roundness = value.roundness
                nativeOptions.feather = value.feather
                nativeOptions.color[0] = value.color[0]; nativeOptions.color[1] = value.color[1]
                nativeOptions.color[2] = value.color[2]; nativeOptions.color[3] = value.color[3]
                FilaView_setVignetteOptions(nativeObject, nativeOptions.ptr)
            }
        }

    actual var fogOptions: FogOptions = FogOptions()
        set(value) {
            field = value
            memScoped {
                val nativeOptions = alloc<FilaViewFogOptions>()
                nativeOptions.enabled = value.enabled
                nativeOptions.distance = value.distance
                nativeOptions.cutOffDistance = value.cutOffDistance
                nativeOptions.maximumOpacity = value.maximumOpacity
                nativeOptions.height = value.height
                nativeOptions.heightFalloff = value.heightFalloff
                nativeOptions.color[0] = value.color[0]; nativeOptions.color[1] = value.color[1]; nativeOptions.color[2] = value.color[2]
                nativeOptions.density = value.density
                nativeOptions.inScatteringStart = value.inScatteringStart
                nativeOptions.inScatteringSize = value.inScatteringSize
                FilaView_setFogOptions(nativeObject, nativeOptions.ptr)
            }
        }

    actual var depthOfFieldOptions: DepthOfFieldOptions = DepthOfFieldOptions()
        set(value) {
            field = value
            memScoped {
                val nativeOptions = alloc<FilaViewDepthOfFieldOptions>()
                nativeOptions.enabled = value.enabled
                nativeOptions.cocScale = value.cocScale
                nativeOptions.maxApertureDiameter = value.maxApertureDiameter
                nativeOptions.filter = value.filter
                nativeOptions.nativeResolution = value.nativeResolution
                nativeOptions.foregroundRingCount = value.foregroundRingCount.toUByte()
                nativeOptions.backgroundRingCount = value.backgroundRingCount.toUByte()
                nativeOptions.fastGatherRingCount = value.fastGatherRingCount.toUByte()
                nativeOptions.maxForegroundCOC = value.maxForegroundCOC.toUByte()
                nativeOptions.maxBackgroundCOC = value.maxBackgroundCOC.toUByte()
                FilaView_setDepthOfFieldOptions(nativeObject, nativeOptions.ptr)
            }
        }

    actual var isStencilBufferEnabled: Boolean = false
        get() = FilaView_isStencilBufferEnabled(nativeObject)
        set(value) {
            field = value
            FilaView_setStencilBufferEnabled(nativeObject, value)
        }

    actual var stereoscopicOptions: StereoscopicOptions = StereoscopicOptions()
    actual fun pick(x: Int, y: Int, handler: Any?, callback: OnPickCallback?) {
        // ... handled via C-wrapper if needed
    }

    actual fun setMaterialGlobal(index: Int, value: FloatArray) {
        FilaView_setMaterialGlobal(nativeObject, index.toUInt(), value[0], value[1], value[2], value[3])
    }

    actual fun getMaterialGlobal(index: Int, out: FloatArray?): FloatArray {
        val result = out ?: FloatArray(4)
        memScoped {
            val v = allocArray<FloatVar>(4)
            FilaView_getMaterialGlobal(nativeObject, index.toUInt(), v)
            result[0] = v[0]; result[1] = v[1]; result[2] = v[2]; result[3] = v[3]
        }
        return result
    }

    actual val fogEntity: Int get() = FilaView_getFogEntity(nativeObject).toInt()

    actual fun clearFrameHistory(engine: Engine) {
        FilaView_clearFrameHistory(nativeObject, engine.nativeObject)
    }

    actual val nativeObject: Long get() = nativeObject.toLong()
}
