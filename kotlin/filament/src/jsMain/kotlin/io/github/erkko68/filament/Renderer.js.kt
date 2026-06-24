package io.github.erkko68.filament

import io.github.erkko68.filament.js.Renderer as JSRenderer
import io.github.erkko68.filament.js.`Renderer_ClearOptions` as JSRendererClearOptions

// Renderer methods present only in some filament.js builds. Declared as methods (not function-typed
// properties) so they're invoked as `obj.method(...)` and keep their `this` binding — embind throws
// BindingError if the bound function is detached from its receiver. Presence is probed before calling.
// readPixels has two arities (with/without a RenderTarget), so it gets one interface each.
private external interface JsRendererExt {
    fun copyFrame(dstSwapChain: io.github.erkko68.filament.js.SwapChain, dst: Viewport, src: Viewport, flags: Int)
    fun skipNextFrames(frameCount: Int)
}
private external interface JsReadPixels {
    fun readPixels(x: Int, y: Int, w: Int, h: Int, buffer: Texture.PixelBufferDescriptor)
}
private external interface JsReadPixelsRt {
    fun readPixels(rt: io.github.erkko68.filament.js.RenderTarget, x: Int, y: Int, w: Int, h: Int, buffer: Texture.PixelBufferDescriptor)
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
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
        jsRenderer.skipFrame(js("BigInt")(vsyncSteadyClockTimeNano.toString()))
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
        if (jsHasMember(jsRenderer, "copyFrame"))
            jsRenderer.unsafeCast<JsRendererExt>().copyFrame(dstSwapChain.jsSwapChain, dstViewport, srcViewport, flags)
    }

    actual fun readPixels(
        xoffset: Int,
        yoffset: Int,
        width: Int,
        height: Int,
        buffer: Texture.PixelBufferDescriptor
    ) {
        if (jsHasMember(jsRenderer, "readPixels"))
            jsRenderer.unsafeCast<JsReadPixels>().readPixels(xoffset, yoffset, width, height, buffer)
    }

    actual fun readPixels(
        renderTarget: RenderTarget,
        xoffset: Int,
        yoffset: Int,
        width: Int,
        height: Int,
        buffer: Texture.PixelBufferDescriptor
    ) {
        if (jsHasMember(jsRenderer, "readPixels"))
            jsRenderer.unsafeCast<JsReadPixelsRt>().readPixels(renderTarget.jsRenderTarget, xoffset, yoffset, width, height, buffer)
    }

    actual fun skipNextFrames(frameCount: Int) {
        if (jsHasMember(jsRenderer, "skipNextFrames"))
            jsRenderer.unsafeCast<JsRendererExt>().skipNextFrames(frameCount)
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
        actual var clearColor: DoubleArray = doubleArrayOf(0.0, 0.0, 0.0, 0.0)
        actual var clear: Boolean = false
        actual var discard: Boolean = false
    }

    actual companion object {
        actual val MIRROR_FRAME_FLAG_COMMIT: Int = 1
        actual val MIRROR_FRAME_FLAG_SET_PRESENTATION_TIME: Int = 2
        actual val MIRROR_FRAME_FLAG_CLEAR: Int = 4
    }
}