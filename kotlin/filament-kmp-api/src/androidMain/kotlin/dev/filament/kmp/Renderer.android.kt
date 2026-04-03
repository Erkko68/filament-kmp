package dev.filament.kmp

import com.google.android.filament.Renderer as AndroidFilamentRenderer
import com.google.android.filament.Texture as AndroidTexture

actual class Renderer internal constructor(
    internal var androidRenderer: AndroidFilamentRenderer?,
    internal var engineRef: Engine?,
) {
    actual val isValid: Boolean
        get() = androidRenderer != null

    actual fun setDisplayInfo(info: DisplayInfo) {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        val androidInfo = AndroidFilamentRenderer.DisplayInfo().apply {
            refreshRate = info.refreshRate
            presentationDeadlineNanos = info.presentationDeadlineNanos
            vsyncOffsetNanos = info.vsyncOffsetNanos
        }
        renderer.setDisplayInfo(androidInfo)
    }

    actual fun getDisplayInfo(): DisplayInfo {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        val androidInfo = renderer.displayInfo
        return DisplayInfo().also {
            it.refreshRate = androidInfo.refreshRate
            it.presentationDeadlineNanos = androidInfo.presentationDeadlineNanos
            it.vsyncOffsetNanos = androidInfo.vsyncOffsetNanos
        }
    }

    actual fun setFrameRateOptions(options: FrameRateOptions) {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        val androidOptions = AndroidFilamentRenderer.FrameRateOptions().apply {
            interval = options.interval
            headRoomRatio = options.headRoomRatio
            scaleRate = options.scaleRate
            history = options.history
        }
        renderer.setFrameRateOptions(androidOptions)
    }

    actual fun getFrameRateOptions(): FrameRateOptions {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        val androidOptions = renderer.frameRateOptions
        return FrameRateOptions().also {
            it.interval = androidOptions.interval
            it.headRoomRatio = androidOptions.headRoomRatio
            it.scaleRate = androidOptions.scaleRate
            it.history = androidOptions.history
        }
    }

    actual fun setClearOptions(options: ClearOptions) {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        require(options.clearColor.size >= 4) { "clearColor must have at least 4 components" }
        val androidOptions = AndroidFilamentRenderer.ClearOptions().apply {
            clearColor = options.clearColor
            clear = options.clear
            discard = options.discard
        }
        renderer.setClearOptions(androidOptions)
    }

    actual fun getClearOptions(): ClearOptions {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        val androidOptions = renderer.clearOptions
        return ClearOptions().also {
            it.clearColor = androidOptions.clearColor
            it.clear = androidOptions.clear
            it.discard = androidOptions.discard
        }
    }

    actual fun getEngine(): Engine = requireNotNull(engineRef) { "Calling method on destroyed Renderer" }

    actual fun setPresentationTime(monotonicClockNanos: Long) {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        renderer.setPresentationTime(monotonicClockNanos)
    }

    actual fun setVsyncTime(steadyClockTimeNano: Long) {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        renderer.setVsyncTime(steadyClockTimeNano)
    }

    actual fun skipFrame(vsyncSteadyClockTimeNano: Long) {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        renderer.skipFrame(vsyncSteadyClockTimeNano)
    }

    actual fun shouldRenderFrame(): Boolean {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        return renderer.shouldRenderFrame()
    }

    actual fun beginFrame(swapChain: SwapChain, frameTimeNanos: Long): Boolean {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        val androidSwapChain = requireNotNull(swapChain.androidSwapChain) { "Calling method on destroyed SwapChain" }
        return renderer.beginFrame(androidSwapChain, frameTimeNanos)
    }

    actual fun endFrame() {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        renderer.endFrame()
    }

    actual fun render(view: View) {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        val androidView = requireNotNull(view.androidView) { "View is invalid." }
        renderer.render(androidView)
    }

    actual fun renderStandaloneView(view: View) {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        val androidView = requireNotNull(view.androidView) { "View is invalid." }
        renderer.renderStandaloneView(androidView)
    }

    actual fun copyFrame(dstSwapChain: SwapChain, dstViewport: Viewport, srcViewport: Viewport, flags: Int) {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        val androidSwapChain = requireNotNull(dstSwapChain.androidSwapChain) { "Calling method on destroyed SwapChain" }
        renderer.copyFrame(androidSwapChain, dstViewport.androidViewport, srcViewport.androidViewport, flags)
    }

    @Deprecated("Use copyFrame")
    actual fun mirrorFrame(dstSwapChain: SwapChain, dstViewport: Viewport, srcViewport: Viewport, flags: Int) {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        val androidSwapChain = requireNotNull(dstSwapChain.androidSwapChain) { "Calling method on destroyed SwapChain" }
        renderer.mirrorFrame(androidSwapChain, dstViewport.androidViewport, srcViewport.androidViewport, flags)
    }

    actual fun readPixels(xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Any) {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        renderer.readPixels(xoffset, yoffset, width, height, buffer as AndroidTexture.PixelBufferDescriptor)
    }

    actual fun readPixels(renderTarget: RenderTarget, xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Any) {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        val androidTarget = requireNotNull(renderTarget.androidRenderTarget) { "Calling method on destroyed RenderTarget" }
        renderer.readPixels(androidTarget, xoffset, yoffset, width, height, buffer as AndroidTexture.PixelBufferDescriptor)
    }

    actual fun getUserTime(): Double {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        return renderer.userTime
    }

    actual fun resetUserTime() {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        renderer.resetUserTime()
    }

    actual fun skipNextFrames(frameCount: Int) {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        renderer.skipNextFrames(frameCount)
    }

    actual fun getFrameToSkipCount(): Int {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        return renderer.frameToSkipCount
    }

    actual fun getNativeObject(): Long {
        val renderer = requireNotNull(androidRenderer) { "Calling method on destroyed Renderer" }
        return renderer.nativeObject
    }

    actual internal fun invalidate() {
        androidRenderer = null
        engineRef = null
    }

    actual class DisplayInfo {
        actual var refreshRate: Float = 60.0f
        @Deprecated("Ignored by Filament")
        actual var presentationDeadlineNanos: Long = 0L
        @Deprecated("Ignored by Filament")
        actual var vsyncOffsetNanos: Long = 0L
    }

    actual class FrameRateOptions {
        actual var interval: Float = 1.0f
        actual var headRoomRatio: Float = 0.0f
        actual var scaleRate: Float = 1.0f / 15.0f
        actual var history: Int = 15
    }

    actual class ClearOptions {
        actual var clearColor: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)
        actual var clear: Boolean = false
        actual var discard: Boolean = true
    }

    actual companion object {
        actual val MIRROR_FRAME_FLAG_COMMIT: Int = AndroidFilamentRenderer.MIRROR_FRAME_FLAG_COMMIT
        actual val MIRROR_FRAME_FLAG_SET_PRESENTATION_TIME: Int = AndroidFilamentRenderer.MIRROR_FRAME_FLAG_SET_PRESENTATION_TIME
        actual val MIRROR_FRAME_FLAG_CLEAR: Int = AndroidFilamentRenderer.MIRROR_FRAME_FLAG_CLEAR
    }
}
