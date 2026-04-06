package dev.filament.kmp

actual class Renderer internal constructor(
    private val engine: Engine,
    val nativeRenderer: com.google.android.filament.Renderer
) {
    actual class DisplayInfo actual constructor() {
        val nativeInfo = com.google.android.filament.Renderer.DisplayInfo()
        actual var refreshRate: Float
            get() = nativeInfo.refreshRate
            set(value) { nativeInfo.refreshRate = value }
    }

    actual class FrameRateOptions actual constructor() {
        val nativeOptions = com.google.android.filament.Renderer.FrameRateOptions()
        actual var interval: Float
            get() = nativeOptions.interval
            set(value) { nativeOptions.interval = value }
        actual var headRoomRatio: Float
            get() = nativeOptions.headRoomRatio
            set(value) { nativeOptions.headRoomRatio = value }
        actual var scaleRate: Float
            get() = nativeOptions.scaleRate
            set(value) { nativeOptions.scaleRate = value }
        actual var history: Int
            get() = nativeOptions.history
            set(value) { nativeOptions.history = value }
    }

    actual class ClearOptions actual constructor() {
        val nativeOptions = com.google.android.filament.Renderer.ClearOptions()
        actual var clearColor: FloatArray
            get() = nativeOptions.clearColor
            set(value) { nativeOptions.clearColor = value }
        actual var clear: Boolean
            get() = nativeOptions.clear
            set(value) { nativeOptions.clear = value }
        actual var discard: Boolean
            get() = nativeOptions.discard
            set(value) { nativeOptions.discard = value }
    }

    actual companion object {
        actual val MIRROR_FRAME_FLAG_COMMIT: Int = com.google.android.filament.Renderer.MIRROR_FRAME_FLAG_COMMIT
        actual val MIRROR_FRAME_FLAG_SET_PRESENTATION_TIME: Int = com.google.android.filament.Renderer.MIRROR_FRAME_FLAG_SET_PRESENTATION_TIME
        actual val MIRROR_FRAME_FLAG_CLEAR: Int = com.google.android.filament.Renderer.MIRROR_FRAME_FLAG_CLEAR
    }

    actual fun getEngine(): Engine = engine

    actual fun setDisplayInfo(info: DisplayInfo) = nativeRenderer.setDisplayInfo(info.nativeInfo)
    actual fun getDisplayInfo(): DisplayInfo = DisplayInfo().apply {
        val info = nativeRenderer.displayInfo
        refreshRate = info.refreshRate
    }

    actual fun setFrameRateOptions(options: FrameRateOptions) = nativeRenderer.setFrameRateOptions(options.nativeOptions)
    actual fun getFrameRateOptions(): FrameRateOptions = FrameRateOptions().apply {
        val options = nativeRenderer.frameRateOptions
        interval = options.interval
        headRoomRatio = options.headRoomRatio
        scaleRate = options.scaleRate
        history = options.history
    }

    actual fun setClearOptions(options: ClearOptions) = nativeRenderer.setClearOptions(options.nativeOptions)
    actual fun getClearOptions(): ClearOptions = ClearOptions().apply {
        val options = nativeRenderer.clearOptions
        clearColor = options.clearColor
        clear = options.clear
        discard = options.discard
    }

    actual fun setPresentationTime(monotonicClockNanos: Long) = nativeRenderer.setPresentationTime(monotonicClockNanos)
    actual fun setVsyncTime(steadyClockTimeNano: Long) = nativeRenderer.setVsyncTime(steadyClockTimeNano)
    actual fun skipFrame(vsyncSteadyClockTimeNano: Long) = nativeRenderer.skipFrame(vsyncSteadyClockTimeNano)
    actual fun shouldRenderFrame(): Boolean = nativeRenderer.shouldRenderFrame()
    actual fun beginFrame(swapChain: SwapChain, frameTimeNanos: Long): Boolean = nativeRenderer.beginFrame(swapChain.nativeSwapChain, frameTimeNanos)
    actual fun endFrame() = nativeRenderer.endFrame()
    actual fun render(view: View) = nativeRenderer.render(view.nativeView)
    actual fun renderStandaloneView(view: View) = nativeRenderer.renderStandaloneView(view.nativeView)
    actual fun copyFrame(dstSwapChain: SwapChain, dstViewport: Viewport, srcViewport: Viewport, flags: Int) =
        nativeRenderer.copyFrame(dstSwapChain.nativeSwapChain, dstViewport.nativeViewport, srcViewport.nativeViewport, flags)

    actual fun getUserTime(): Double = nativeRenderer.userTime
    actual fun resetUserTime() = nativeRenderer.resetUserTime()
    actual fun skipNextFrames(frameCount: Int) = nativeRenderer.skipNextFrames(frameCount)
    actual fun getFrameToSkipCount(): Int = nativeRenderer.frameToSkipCount
}
