@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaView

actual class View internal constructor(internal var nativeHandle: CPointer<FilaView>?) {
    actual enum class AntiAliasing { NONE, FXAA }
    actual enum class Dithering { NONE, TEMPORAL }
    actual enum class AmbientOcclusion { NONE, SSAO }
    actual enum class ToneMapping { LINEAR, ACES }
    actual enum class BlendMode { OPAQUE, TRANSLUCENT }
    actual enum class Quality { LOW, MEDIUM, HIGH, ULTRA }

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
    }

    actual class FogOptions actual constructor() {
        actual var enabled: Boolean = false
        actual var distance: Float = 0.0f
        actual var density: Float = 0.1f
        actual var height: Float = 0.0f
        actual var heightFalloff: Float = 1.0f
        actual var color: FloatArray = floatArrayOf(0.5f, 0.5f, 0.5f)
        actual var densityMap: Texture? = null
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
        actual var bias: Float = 0.005f
        actual var intensity: Float = 1.0f
        actual var scale: Float = 1.0f
        actual var power: Float = 1.0f
        actual var minConeAngle: Float = 0.0f
        actual var quality: Quality = Quality.LOW
        actual var lowPassFilter: Quality = Quality.MEDIUM
        actual var upsampling: Quality = Quality.LOW
        actual var enabled: Boolean = false
        actual var bentNormals: Boolean = false
    }

    actual class TemporalAntiAliasingOptions actual constructor() {
        actual var filterWidth: Float = 1.0f
        actual var feedback: Float = 0.12f
        actual var enabled: Boolean = false
    }

    actual class ScreenSpaceReflectionsOptions actual constructor() {
        actual var enabled: Boolean = false
        actual var thickness: Float = 0.1f
        actual var bias: Float = 0.01f
        actual var maxDistance: Float = 3.0f
        actual var stride: Float = 2.0f
    }

    actual fun setName(name: String) { FilaView_setName(nativeHandle, name) }
    actual fun getName(): String? = null

    actual fun setScene(scene: Scene?) { FilaView_setScene(nativeHandle, scene?.nativeHandle) }
    actual fun getScene(): Scene? = null

    actual fun setCamera(camera: Camera?) { FilaView_setCamera(nativeHandle, camera?.nativeHandle) }
    actual fun getCamera(): Camera? = null

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
    actual fun getVisibleLayers(): Int = FilaView_getVisibleLayers(nativeHandle).toInt()

    actual fun setShadowingEnabled(enabled: Boolean) { FilaView_setShadowingEnabled(nativeHandle, enabled) }
    actual fun setPostProcessingEnabled(enabled: Boolean) { FilaView_setPostProcessingEnabled(nativeHandle, enabled) }
    actual fun isPostProcessingEnabled(): Boolean = FilaView_isPostProcessingEnabled(nativeHandle)

    actual fun setAntiAliasing(type: AntiAliasing) {
        FilaView_setAntiAliasing(nativeHandle, type.ordinal.toUInt())
    }
    actual fun getAntiAliasing(): AntiAliasing = AntiAliasing.values()[FilaView_getAntiAliasing(nativeHandle).toInt()]

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
            cOptions.resolution = options.scale
            cOptions.power = options.power
            cOptions.minHorizonAngleRad = options.minConeAngle
            cOptions.quality = options.quality.ordinal.toUInt()
            cOptions.lowPassFilter = options.lowPassFilter.ordinal.toUInt()
            cOptions.upsampling = options.upsampling.ordinal.toUInt()
            cOptions.bentNormals = options.bentNormals
            FilaView_setAmbientOcclusionOptions(nativeHandle, cOptions.ptr)
        }
    }
    actual fun getAmbientOcclusionOptions(): AmbientOcclusionOptions = AmbientOcclusionOptions()

    actual fun setTemporalAntiAliasingOptions(options: TemporalAntiAliasingOptions) {
        memScoped {
            val cOptions = alloc<FilaViewTemporalAntiAliasingOptions>()
            cOptions.enabled = options.enabled
            cOptions.filterWidth = options.filterWidth
            cOptions.feedback = options.feedback
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
        FilaView_setRenderTarget(nativeHandle, target?.nativeHandle)
    }
    actual fun getRenderTarget(): RenderTarget? = null
}
