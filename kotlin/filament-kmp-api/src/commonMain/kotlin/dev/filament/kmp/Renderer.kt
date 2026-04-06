package dev.filament.kmp

/**
 * A Renderer instance represents an operating system's window.
 */
expect class Renderer {

    /**
     * Information about the display this renderer is associated to
     */
    class DisplayInfo {
        var refreshRate: Float

        @Deprecated("this value is ignored")
        var presentationDeadlineNanos: Long

        @Deprecated("this value is ignored")
        var vsyncOffsetNanos: Long

        constructor()
    }

    /**
     * Use FrameRateOptions to set the desired frame rate and control how quickly the system
     * reacts to GPU load changes.
     */
    class FrameRateOptions {
        var interval: Float
        var headRoomRatio: Float
        var scaleRate: Float
        var history: Int

        constructor()
    }

    /**
     * ClearOptions are used at the beginning of a frame to clear or retain the SwapChain content.
     */
    class ClearOptions {
        var clearColor: FloatArray
        var clear: Boolean
        var discard: Boolean

        constructor()
    }

    companion object {
        const val MIRROR_FRAME_FLAG_COMMIT: Int
        const val MIRROR_FRAME_FLAG_SET_PRESENTATION_TIME: Int
        const val MIRROR_FRAME_FLAG_CLEAR: Int
    }

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

    @Deprecated("Use copyFrame instead", ReplaceWith("copyFrame(dstSwapChain, dstViewport, srcViewport, flags)"))
    fun mirrorFrame(dstSwapChain: SwapChain, dstViewport: Viewport, srcViewport: Viewport, flags: Int)

    fun readPixels(xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Texture.PixelBufferDescriptor)
    fun readPixels(renderTarget: RenderTarget, xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Texture.PixelBufferDescriptor)
    fun getUserTime(): Double
    fun resetUserTime()
    fun skipNextFrames(frameCount: Int)
    fun getFrameToSkipCount(): Int
}
