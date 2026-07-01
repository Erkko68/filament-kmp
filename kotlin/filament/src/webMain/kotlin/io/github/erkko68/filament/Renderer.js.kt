package io.github.erkko68.filament


import io.github.erkko68.filament.web.interop.emptyJsObject

import io.github.erkko68.filament.web.interop.jsNumbers
import io.github.erkko68.filament.web.interop.toJsNumbers

import io.github.erkko68.filament.web.Renderer as JSRenderer
import io.github.erkko68.filament.web.`Renderer_ClearOptions` as JSRendererClearOptions

// skipNextFrames is present only in some filament.js builds. Declared as a method (not a
// function-typed property) so it's invoked as `obj.method(...)` and keeps its `this` binding —
// embind throws BindingError if the bound function is detached. Presence is probed before calling.
// (copyFrame / readPixels are not bound by jsbindings.cpp — see the no-op actuals below.)
private external interface JsRendererExt : JsAny  {
    fun skipNextFrames(frameCount: Int)
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
            val jsOptions = emptyJsObject().unsafeCast<JSRendererClearOptions>()
            jsOptions.clearColor = value.clearColor.toJsNumbers()
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
        jsRenderer.setVsyncTime(steadyClockTimeNano.toDouble())
    }

    actual fun skipFrame(vsyncSteadyClockTimeNano: Long) {
        jsRenderer.skipFrame(vsyncSteadyClockTimeNano.toDouble())
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
        // TODO(web): Renderer.copyFrame is not registered in jsbindings.cpp — no-op.
    }

    actual fun readPixels(
        xoffset: Int,
        yoffset: Int,
        width: Int,
        height: Int,
        buffer: Texture.PixelBufferDescriptor
    ) {
        // TODO(web): Renderer.readPixels is not registered in jsbindings.cpp — no-op.
    }

    actual fun readPixels(
        renderTarget: RenderTarget,
        xoffset: Int,
        yoffset: Int,
        width: Int,
        height: Int,
        buffer: Texture.PixelBufferDescriptor
    ) {
        // TODO(web): Renderer.readPixels(RenderTarget) is not registered in jsbindings.cpp — no-op.
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