package dev.filament.kmp

actual class Renderer internal constructor() {
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

    actual val isValid: Boolean
        get() = false

    actual fun setDisplayInfo(info: DisplayInfo) {
        TODO("Not yet implemented")
    }

    actual fun getDisplayInfo(): DisplayInfo = TODO("Not yet implemented")

    actual fun setFrameRateOptions(options: FrameRateOptions) {
        TODO("Not yet implemented")
    }

    actual fun getFrameRateOptions(): FrameRateOptions = TODO("Not yet implemented")

    actual fun setClearOptions(options: ClearOptions) {
        TODO("Not yet implemented")
    }

    actual fun getClearOptions(): ClearOptions = TODO("Not yet implemented")

    actual fun getEngine(): Engine = TODO("Not yet implemented")

    actual fun setPresentationTime(monotonicClockNanos: Long) {
        TODO("Not yet implemented")
    }

    actual fun setVsyncTime(steadyClockTimeNano: Long) {
        TODO("Not yet implemented")
    }

    actual fun skipFrame(vsyncSteadyClockTimeNano: Long) {
        TODO("Not yet implemented")
    }

    actual fun shouldRenderFrame(): Boolean = TODO("Not yet implemented")

    actual fun beginFrame(swapChain: SwapChain, frameTimeNanos: Long): Boolean = TODO("Not yet implemented")

    actual fun endFrame() {
        TODO("Not yet implemented")
    }

    actual fun render(view: View) {
        TODO("Not yet implemented")
    }

    actual fun renderStandaloneView(view: View) {
        TODO("Not yet implemented")
    }

    actual fun copyFrame(dstSwapChain: SwapChain, dstViewport: Viewport, srcViewport: Viewport, flags: Int) {
        TODO("Not yet implemented")
    }

    @Deprecated("Use copyFrame")
    actual fun mirrorFrame(dstSwapChain: SwapChain, dstViewport: Viewport, srcViewport: Viewport, flags: Int) {
        TODO("Not yet implemented")
    }

    actual fun readPixels(xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Any) {
        TODO("Not yet implemented")
    }

    actual fun readPixels(renderTarget: RenderTarget, xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Any) {
        TODO("Not yet implemented")
    }

    actual fun getUserTime(): Double = TODO("Not yet implemented")

    actual fun resetUserTime() {
        TODO("Not yet implemented")
    }

    actual fun skipNextFrames(frameCount: Int) {
        TODO("Not yet implemented")
    }

    actual fun getFrameToSkipCount(): Int = TODO("Not yet implemented")

    actual fun getNativeObject(): Long = TODO("Not yet implemented")

    actual internal fun invalidate() = Unit

    actual companion object {
        actual val MIRROR_FRAME_FLAG_COMMIT: Int = 0x1
        actual val MIRROR_FRAME_FLAG_SET_PRESENTATION_TIME: Int = 0x2
        actual val MIRROR_FRAME_FLAG_CLEAR: Int = 0x4
    }
}
