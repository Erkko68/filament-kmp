package io.github.erkko68.filament

import io.github.erkko68.filament.js.Renderer as JSRenderer
import io.github.erkko68.filament.js.`Renderer_ClearOptions` as JSRendererClearOptions
import io.github.erkko68.filament.js.Renderer_DisplayInfo as JSRendererDisplayInfo
import io.github.erkko68.filament.js.Renderer_FrameRateOptions as JSRendererFrameRateOptions

actual class Renderer(internal val jsRenderer: JSRenderer) {
    private var _userTime: Double = 0.0
    private var _displayInfo = DisplayInfo()
    private var _frameRateOptions = FrameRateOptions()
    private var _clearOptions = ClearOptions()

    actual fun render(swapChain: SwapChain, view: View) {
        jsRenderer.render(swapChain.jsSwapChain, view.jsView)
    }

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
        jsOptions.clearStencil = options.clearStencil
        jsOptions.clear = options.clear
        jsOptions.discardStart = options.discardStart
        jsOptions.discardEnd = options.discardEnd
        jsRenderer.setClearOptions(jsOptions)
    }

    actual fun getClearOptions(): ClearOptions {
        return _clearOptions
    }

    actual fun renderView(view: View) {
        jsRenderer.renderView(view.jsView)
    }

    actual fun shouldRenderFrame(): Boolean {
        return true
    }

    actual fun beginFrame(
        swapChain: SwapChain,
        frameTime: Long
    ): Boolean {
        // JS beginFrame doesn't take frameTime in current bindings
        return jsRenderer.beginFrame(swapChain.jsSwapChain)
    }

    actual fun setPresentationTime(frameTime: Long) {
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
        actual var clearStencil: Boolean = false
        actual var clear: Boolean = false
        actual var discardStart: Boolean = false
        actual var discardEnd: Boolean = false
    }

    actual companion object {
        actual val MIRROR_FRAME_FLAG_COMMIT: Int = 1
        actual val MIRROR_FRAME_FLAG_SET_PRESENTATION_TIME: Int = 2
        actual val MIRROR_FRAME_FLAG_CLEAR: Int = 4
    }
}