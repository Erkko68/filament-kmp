package io.github.erkko68.filament

import io.github.erkko68.filament.js.Renderer as JSRenderer
import io.github.erkko68.filament.js.`Renderer_ClearOptions` as JSRendererClearOptions

actual class Renderer(internal val jsRenderer: JSRenderer, private val _engine: Engine? = null) {
    actual var displayInfo: DisplayInfo = DisplayInfo()
        set(value) {
            field = value
            // Renderer_DisplayInfo not in JS bindings
        }

    actual var frameRateOptions: FrameRateOptions = FrameRateOptions()
        set(value) {
            field = value
            // JS bindings for setFrameRateOptions are often missing or simplified
        }

    actual var clearOptions: ClearOptions = ClearOptions()
        set(value) {
            field = value
            val jsOptions = js("{}").unsafeCast<JSRendererClearOptions>()
            jsOptions.clearColor = value.clearColor.toTypedArray() as Array<Number>
            jsOptions.clear = value.clear
            jsRenderer.setClearOptions(jsOptions)
        }

    actual fun shouldRenderFrame(): Boolean {
        return jsRenderer.shouldRenderFrame()
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

    actual val userTime: Double
        get() = jsRenderer.getUserTime().toDouble()

    actual fun resetUserTime() {
        jsRenderer.resetUserTime()
    }

    actual val frameToSkipCount: Int
        get() = jsRenderer.getFrameToSkipCount().toInt()

    actual val engine: Engine
        get() = _engine ?: throw UnsupportedOperationException("Engine reference not available - Renderer was not created with Engine context")

    actual fun setVsyncTime(steadyClockTimeNano: Long) {
        val jsVal = js("BigInt")(steadyClockTimeNano.toString())
        jsRenderer.setVsyncTime(jsVal.unsafeCast<Number>())
    }

    actual fun skipFrame(vsyncSteadyClockTimeNano: Long) {
        // The JS binding's `skipFrame` takes no parameter — the C++ overload that
        // accepts a vsync time isn't exposed via embind. Pass-through anyway so
        // the common API stays consistent across platforms.
        jsRenderer.skipFrame()
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