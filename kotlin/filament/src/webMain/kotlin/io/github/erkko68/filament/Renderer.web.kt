package io.github.erkko68.filament


import io.github.erkko68.filament.web.interop.emptyJsObject
import io.github.erkko68.filament.web.interop.jsNumbers
import io.github.erkko68.filament.web.interop.readNumbersInto
import io.github.erkko68.filament.web.interop.toJsNumbers

import org.khronos.webgl.get
import io.github.erkko68.filament.web.PixelDataFormat
import io.github.erkko68.filament.web.PixelDataType
import io.github.erkko68.filament.web.Renderer as JSRenderer
import io.github.erkko68.filament.web.`Renderer_ClearOptions` as JSRendererClearOptions

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual class Renderer @InternalFilamentApi constructor(internal val jsRenderer: JSRenderer, private val _engine: Engine? = null) {
    private var _displayInfo = DisplayInfo()
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "state is only tracked locally — setDisplayInfo is not bound in filament.js; frame pacing is managed by the browser.")
    actual var displayInfo: DisplayInfo
        get() = _displayInfo
        set(value) {
            _displayInfo = value
        }

    private var _frameRateOptions = FrameRateOptions()
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "state is only tracked locally — setFrameRateOptions is not bound in filament.js; frame pacing is managed by the browser.")
    actual var frameRateOptions: FrameRateOptions
        get() = _frameRateOptions
        set(value) {
            _frameRateOptions = value
        }

    actual var clearOptions: ClearOptions
        get() {
            val jsOptions = jsRenderer.getClearOptions()
            return ClearOptions().apply {
                clearColor = jsOptions.clearColor?.readNumbersInto(DoubleArray(4)) ?: doubleArrayOf(0.0, 0.0, 0.0, 0.0)
                clear = jsOptions.clear ?: false
                discard = jsOptions.discard ?: true
            }
        }
        set(value) {
            val jsOptions = emptyJsObject().unsafeCast<JSRendererClearOptions>()
            jsOptions.clearColor = value.clearColor.toJsNumbers()
            jsOptions.clear = value.clear
            jsOptions.discard = value.discard
            jsRenderer.setClearOptions(jsOptions)
        }

    actual fun shouldRenderFrame(): Boolean {
        return jsRenderer.shouldRenderFrame()
    }

    actual fun beginFrame(
        swapChain: SwapChain,
        frameTimeNanos: Long
    ): Boolean {
        return jsRenderer.beginFrame(swapChain.jsSwapChain)
    }

    actual fun setPresentationTime(monotonicClockNanos: Long) {
        jsRenderer.setPresentationTime(monotonicClockNanos.toDouble())
    }

    actual fun setDesiredPresentationTime(monotonicClockNanos: Long) {
        jsRenderer.setDesiredPresentationTime(monotonicClockNanos.toDouble())
    }

    actual fun setRenderingDeadline(monotonicClockNanos: Long) {
        jsRenderer.setRenderingDeadline(monotonicClockNanos.toDouble())
    }

    actual fun endFrame() {
        jsRenderer.endFrame()
    }

    actual val userTime: Double
        get() = jsRenderer.getUserTime().toDouble()

    actual fun resetUserTime() {
        jsRenderer.resetUserTime()
    }

    actual val materialTime: Double
        get() = jsRenderer.getMaterialTime().toDouble()

    actual fun setMaterialTimeEpoch(timeEpochInNs: Long) {
        jsRenderer.setMaterialTimeEpoch(timeEpochInNs.toDouble())
    }

    actual fun pauseRenderThread(timeNs: Long) {
        jsRenderer.pauseRenderThread(timeNs.toDouble())
    }

    actual val frameToSkipCount: Int
        get() = jsRenderer.getFrameToSkipCount().toInt()

    actual val engine: Engine
        get() = _engine ?: throw UnsupportedOperationException("Engine reference not available - Renderer was not created with Engine context")

    actual fun setVsyncTime(steadyClockTimeNano: Long) {
        jsRenderer.setVsyncTime(steadyClockTimeNano.toDouble())
    }

    actual fun skipFrame(vsyncSteadyClockTimeNano: Long) {
        jsRenderer.skipFrame(vsyncSteadyClockTimeNano.toDouble())
    }

    actual fun render(view: View) {
        jsRenderer.renderView(view.jsView)
    }

    actual fun renderStandaloneView(view: View) {
        jsRenderer.renderStandaloneView(view.jsView)
    }

    actual fun copyFrame(
        dstSwapChain: SwapChain,
        dstViewport: Viewport,
        srcViewport: Viewport,
        flags: Int
    ) {
        jsRenderer.copyFrame(
            dstSwapChain.jsSwapChain,
            jsNumbers(dstViewport.left, dstViewport.bottom, dstViewport.width, dstViewport.height),
            jsNumbers(srcViewport.left, srcViewport.bottom, srcViewport.width, srcViewport.height),
            flags.toDouble(),
        )
    }

    actual fun readPixels(
        xoffset: Int,
        yoffset: Int,
        width: Int,
        height: Int,
        buffer: Texture.PixelBufferDescriptor
    ) {
        jsRenderer.readPixels(
            xoffset.toDouble(), yoffset.toDouble(), width.toDouble(), height.toDouble(),
            mapReadPixelsFormat(buffer.format), mapReadPixelsType(buffer.type),
        ) { pixels ->
            val n = minOf(buffer.storage.size, pixels.length)
            for (i in 0 until n) buffer.storage[i] = pixels[i]
            buffer.callback?.invoke()
        }
    }

    actual fun readPixels(
        renderTarget: RenderTarget,
        xoffset: Int,
        yoffset: Int,
        width: Int,
        height: Int,
        buffer: Texture.PixelBufferDescriptor
    ) {
        jsRenderer.readPixels(
            renderTarget.jsRenderTarget,
            xoffset.toDouble(), yoffset.toDouble(), width.toDouble(), height.toDouble(),
            mapReadPixelsFormat(buffer.format), mapReadPixelsType(buffer.type),
        ) { pixels ->
            val n = minOf(buffer.storage.size, pixels.length)
            for (i in 0 until n) buffer.storage[i] = pixels[i]
            buffer.callback?.invoke()
        }
    }

    actual fun skipNextFrames(frameCount: Int) {
        jsRenderer.skipNextFrames(frameCount.toDouble())
    }

    actual class DisplayInfo {
        actual var refreshRate: Float = 60.0f
            get() = field
            set(value) { field = value }
    }

    actual class FrameRateOptions {
        actual var interval: Float = 1.0f
        actual var headRoomRatio: Float = 0.0f
        actual var scaleRate: Float = 1.0f / 15.0f
        actual var history: Int = 15
    }

    actual class ClearOptions {
        actual var clearColor: DoubleArray = doubleArrayOf(0.0, 0.0, 0.0, 0.0)
        actual var clear: Boolean = false
        actual var discard: Boolean = true
    }

    actual object MirrorFrameFlag {
        actual val COMMIT: Int = 1
        actual val SET_PRESENTATION_TIME: Int = 2
        actual val CLEAR: Int = 4
    }
}

private fun mapReadPixelsFormat(format: Texture.Format): PixelDataFormat = when (format) {
    Texture.Format.R -> PixelDataFormat.R
    Texture.Format.RG -> PixelDataFormat.RG
    Texture.Format.RGB -> PixelDataFormat.RGB
    Texture.Format.RGBA -> PixelDataFormat.RGBA
    Texture.Format.DEPTH_COMPONENT -> PixelDataFormat.DEPTH_COMPONENT
    Texture.Format.DEPTH_STENCIL -> PixelDataFormat.DEPTH_STENCIL
    Texture.Format.ALPHA -> PixelDataFormat.ALPHA
    else -> PixelDataFormat.RGBA
}

private fun mapReadPixelsType(type: Texture.Type): PixelDataType = when (type) {
    Texture.Type.UBYTE -> PixelDataType.UBYTE
    Texture.Type.BYTE -> PixelDataType.BYTE
    Texture.Type.USHORT -> PixelDataType.USHORT
    Texture.Type.SHORT -> PixelDataType.SHORT
    Texture.Type.UINT -> PixelDataType.UINT
    Texture.Type.INT -> PixelDataType.INT
    Texture.Type.HALF -> PixelDataType.HALF
    Texture.Type.FLOAT -> PixelDataType.FLOAT
    else -> PixelDataType.UBYTE
}
