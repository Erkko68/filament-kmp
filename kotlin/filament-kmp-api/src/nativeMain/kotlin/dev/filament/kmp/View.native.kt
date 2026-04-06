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

    actual class DynamicResolutionOptions actual constructor() {
        actual var enabled: Boolean = false
        actual var homogeneousScaling: Boolean = false
        actual var minScale: Float = 0.5f
        actual var maxScale: Float = 1.0f
        actual var sharpness: Float = 0.0f
        actual var quality: Quality = Quality.MEDIUM
        actual enum class Quality { LOW, MEDIUM, HIGH, ULTRA }
    }

    actual class RenderQuality actual constructor() {
        actual var hdrColorBuffer: Quality = Quality.HIGH
        actual enum class Quality { LOW, MEDIUM, HIGH, ULTRA }
    }

    private var scene: Scene? = null
    private var camera: Camera? = null
    private var viewport: Viewport = Viewport(0, 0, 0, 0)
    private var renderTarget: RenderTarget? = null

    actual fun setName(name: String) = FilaView_setName(nativeHandle, name)
    actual fun getName(): String? = null // C-wrapper doesn't expose getName

    actual fun setScene(scene: Scene?) {
        this.scene = scene
        FilaView_setScene(nativeHandle, scene?.nativeHandle)
    }
    actual fun getScene(): Scene? = scene

    actual fun setCamera(camera: Camera?) {
        this.camera = camera
        FilaView_setCamera(nativeHandle, camera?.nativeHandle)
    }
    actual fun getCamera(): Camera? = camera

    actual fun setViewport(viewport: Viewport) {
        this.viewport = viewport
        FilaView_setViewport(nativeHandle, viewport.left, viewport.bottom, viewport.width.toUInt(), viewport.height.toUInt())
    }
    actual fun getViewport(): Viewport = viewport

    actual fun setBlendMode(blendMode: BlendMode) = FilaView_setBlendMode(nativeHandle, blendMode.ordinal.toUInt())
    actual fun getBlendMode(): BlendMode = BlendMode.OPAQUE // Cache not implemented

    actual fun setVisibleLayers(select: Int, values: Int) = FilaView_setVisibleLayers(nativeHandle, select.toUByte(), values.toUByte())
    actual fun getVisibleLayers(): Int = FilaView_getVisibleLayers(nativeHandle).toInt()

    actual fun setShadowingEnabled(enabled: Boolean) = FilaView_setShadowingEnabled(nativeHandle, enabled)
    actual fun setPostProcessingEnabled(enabled: Boolean) = FilaView_setPostProcessingEnabled(nativeHandle, enabled)
    actual fun isPostProcessingEnabled(): Boolean = FilaView_isPostProcessingEnabled(nativeHandle)

    actual fun setAntiAliasing(type: AntiAliasing) = FilaView_setAntiAliasing(nativeHandle, type.ordinal.toUInt())
    actual fun getAntiAliasing(): AntiAliasing = AntiAliasing.values()[FilaView_getAntiAliasing(nativeHandle).toInt()]

    actual fun setDithering(dithering: Dithering) = FilaView_setDithering(nativeHandle, dithering.ordinal.toUInt())
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

    actual fun setRenderTarget(target: RenderTarget?) {
        this.renderTarget = target
        FilaView_setRenderTarget(nativeHandle, target?.nativeHandle)
    }
    actual fun getRenderTarget(): RenderTarget? = renderTarget
}
