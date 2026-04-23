package io.github.erkko68.filament

import io.github.erkko68.filament.js.Renderer as JSRenderer
import io.github.erkko68.filament.js.`Renderer_ClearOptions` as JSRendererClearOptions

actual class Renderer(internal val jsRenderer: JSRenderer, private val _engine: Engine? = null) {
    private var _userTime: Double = 0.0
    private var _displayInfo = DisplayInfo()
    private var _frameRateOptions = FrameRateOptions()
    private var _clearOptions = ClearOptions()

    actual fun getDisplayInfo(): DisplayInfo {
        return _displayInfo
    }

    actual fun setFrameRateOptions(options: FrameRateOptions) {
        _frameRateOptions = options
        // JS bindings for setFrameRateOptions are often missing or simplified
    }

    actual fun getFrameRateOptions(): FrameRateOptions {
        return _frameRateOptions
    }

    actual fun setClearOptions(options: ClearOptions) {
        _clearOptions = options
        val jsOptions = js("{}").unsafeCast<JSRendererClearOptions>()
        jsOptions.clearColor = options.clearColor.toTypedArray() as Array<Number>
        jsOptions.clear = options.clear
        jsRenderer.setClearOptions(jsOptions)
    }

    actual fun getClearOptions(): ClearOptions {
        return _clearOptions
    }

    actual fun shouldRenderFrame(): Boolean {
        return true
    }

    actual fun beginFrame(
        swapChain: SwapChain,
        frameTimeNanos: Long
    ): Boolean {
        // JS beginFrame doesn't take frameTimeNanos in current bindings
        return jsRenderer.beginFrame(swapChain.jsSwapChain)
    }

    actual fun setPresentationTime(monotonicClockNanos: Long) {
    }

    actual fun endFrame() {
        jsRenderer.endFrame()
    }

    actual fun getUserTime(): Double {
        return _userTime
    }

    actual fun resetUserTime() {
        _userTime = 0.0
    }

    actual fun getFrameToSkipCount(): Int {
        return 0
    }

    actual fun getEngine(): Engine {
        return _engine ?: throw UnsupportedOperationException("Engine reference not available - Renderer was not created with Engine context")
    }

    actual fun setDisplayInfo(info: DisplayInfo) {
        _displayInfo = info
        // Renderer_DisplayInfo not in JS bindings
    }

    actual fun setVsyncTime(steadyClockTimeNano: Long) {
        val jsRend = jsRenderer.asDynamic()
        if (jsRend.setVsyncTime != null) {
            jsRend.setVsyncTime(steadyClockTimeNano)
        }
    }

    actual fun skipFrame(vsyncSteadyClockTimeNano: Long) {
        val jsRend = jsRenderer.asDynamic()
        if (jsRend.skipFrame != null) {
            jsRend.skipFrame(vsyncSteadyClockTimeNano)
        }
    }

    actual fun render(view: View) {
        jsRenderer.renderView(view.jsView)
    }

    actual fun renderStandaloneView(view: View) {
        jsRenderer.renderView(view.jsView)
    }

    actual fun copyFrame(
        dstSwapChain: SwapChain,
        dstViewport: Viewport,
        srcViewport: Viewport,
        flags: Int
    ) {
        val jsRend = jsRenderer.asDynamic()
        if (jsRend.copyFrame != null) {
            jsRend.copyFrame(dstSwapChain.jsSwapChain, dstViewport, srcViewport, flags)
        }
    }

    actual fun readPixels(
        xoffset: Int,
        yoffset: Int,
        width: Int,
        height: Int,
        buffer: Texture.PixelBufferDescriptor
    ) {
        val jsRend = jsRenderer.asDynamic()
        if (jsRend.readPixels != null) {
            jsRend.readPixels(xoffset, yoffset, width, height, buffer)
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
        val jsRend = jsRenderer.asDynamic()
        if (jsRend.readPixels != null) {
            jsRend.readPixels(renderTarget.jsRenderTarget, xoffset, yoffset, width, height, buffer)
        }
    }

    actual fun skipNextFrames(frameCount: Int) {
        val jsRend = jsRenderer.asDynamic()
        if (jsRend.skipNextFrames != null) {
            jsRend.skipNextFrames(frameCount)
        }
    }

    actual class DisplayInfo {
        actual var refreshRate: Float = 60.0f
            get() = field
            set(value) { field = value }
    }

    actual class FrameRateOptions {
        actual var interval: Float = 1.0f
            get() = field
            set(value) { field = value }
        actual var headRoomRatio: Float = 1.0f
            get() = field
            set(value) { field = value }
        actual var scaleRate: Float = 1.0f
            get() = field
            set(value) { field = value }
        actual var history: Int = 1
            get() = field
            set(value) { field = value }
    }

    actual class ClearOptions {
        actual var clearColor: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)
        actual var clear: Boolean = false
        actual var discard: Boolean = false
    }

    actual companion object {
        actual val MIRROR_FRAME_FLAG_COMMIT: Int = 1
        actual val MIRROR_FRAME_FLAG_SET_PRESENTATION_TIME: Int = 2
        actual val MIRROR_FRAME_FLAG_CLEAR: Int = 4
    }
}