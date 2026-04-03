package dev.filament.kmp

/**
 * A Renderer is responsible for rendering one frame into a SwapChain.
 */
expect class Renderer {
    class DisplayInfo {
        var refreshRate: Float
        @Deprecated("Ignored by Filament")
        var presentationDeadlineNanos: Long
        @Deprecated("Ignored by Filament")
        var vsyncOffsetNanos: Long
    }

    class FrameRateOptions {
        var interval: Float
        var headRoomRatio: Float
        var scaleRate: Float
        var history: Int
    }

    class ClearOptions {
        var clearColor: FloatArray
        var clear: Boolean
        var discard: Boolean
    }

    /**
     * Returns whether this Renderer wrapper currently points to a valid native instance.
     */
    val isValid: Boolean

    fun setDisplayInfo(info: DisplayInfo)

    fun getDisplayInfo(): DisplayInfo

    fun setFrameRateOptions(options: FrameRateOptions)

    fun getFrameRateOptions(): FrameRateOptions

    fun setClearOptions(options: ClearOptions)

    fun getClearOptions(): ClearOptions

    fun getEngine(): Engine

    fun setPresentationTime(monotonicClockNanos: Long)

    fun setVsyncTime(steadyClockTimeNano: Long)

    fun skipFrame(vsyncSteadyClockTimeNano: Long)

    fun shouldRenderFrame(): Boolean

    fun beginFrame(swapChain: SwapChain, frameTimeNanos: Long): Boolean

    fun endFrame()

    fun render(view: View)

    fun renderStandaloneView(view: View)

    fun copyFrame(dstSwapChain: SwapChain, dstViewport: Viewport, srcViewport: Viewport, flags: Int)

    @Deprecated("Use copyFrame")
    fun mirrorFrame(dstSwapChain: SwapChain, dstViewport: Viewport, srcViewport: Viewport, flags: Int)

    fun readPixels(xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Any)

    fun readPixels(renderTarget: RenderTarget, xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Any)

    fun getUserTime(): Double

    fun resetUserTime()

    fun skipNextFrames(frameCount: Int)

    fun getFrameToSkipCount(): Int

    fun getNativeObject(): Long

    internal fun invalidate()

    companion object {
        val MIRROR_FRAME_FLAG_COMMIT: Int
        val MIRROR_FRAME_FLAG_SET_PRESENTATION_TIME: Int
        val MIRROR_FRAME_FLAG_CLEAR: Int
    }
}
