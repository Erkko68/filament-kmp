package dev.filament.kmp

expect class Renderer {
    class DisplayInfo() {
        var refreshRate: Float
    }

    class FrameRateOptions() {
        var interval: Float
        var headRoomRatio: Float
        var scaleRate: Float
        var history: Int
    }

    class ClearOptions() {
        var clearColor: FloatArray
        var clear: Boolean
        var discard: Boolean
    }

    companion object {
        val MIRROR_FRAME_FLAG_COMMIT: Int
        val MIRROR_FRAME_FLAG_SET_PRESENTATION_TIME: Int
        val MIRROR_FRAME_FLAG_CLEAR: Int
    }

    fun getEngine(): Engine
    fun setDisplayInfo(info: DisplayInfo)
    fun getDisplayInfo(): DisplayInfo
    fun setFrameRateOptions(options: FrameRateOptions)
    fun getFrameRateOptions(): FrameRateOptions
    fun setClearOptions(options: ClearOptions)
    fun getClearOptions(): ClearOptions
    fun setPresentationTime(monotonicClockNanos: Long)
    fun setVsyncTime(steadyClockTimeNano: Long)
    fun skipFrame(vsyncSteadyClockTimeNano: Long)
    fun shouldRenderFrame(): Boolean
    fun beginFrame(swapChain: SwapChain, frameTimeNanos: Long): Boolean
    fun endFrame()
    fun render(view: View)
    fun renderStandaloneView(view: View)
    fun copyFrame(dstSwapChain: SwapChain, dstViewport: Viewport, srcViewport: Viewport, flags: Int)
    
    fun readPixels(xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Texture.PixelBufferDescriptor)
    fun readPixels(renderTarget: RenderTarget, xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Texture.PixelBufferDescriptor)

    fun getUserTime(): Double
    fun resetUserTime()
    fun skipNextFrames(frameCount: Int)
    fun getFrameToSkipCount(): Int
}
