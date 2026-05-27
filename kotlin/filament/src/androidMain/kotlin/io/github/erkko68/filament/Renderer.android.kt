package io.github.erkko68.filament

import com.google.android.filament.Renderer as AndroidRenderer
import com.google.android.filament.Viewport as AndroidViewport

actual class Renderer internal constructor(
    private val _engine: Engine,
    val nativeRenderer: AndroidRenderer
) {
    private var mDisplayInfo: DisplayInfo? = null
    private var mFrameRateOptions: FrameRateOptions? = null
    private var mClearOptions: ClearOptions? = null
    actual class DisplayInfo actual constructor() {
        val nativeInfo = AndroidRenderer.DisplayInfo()
        actual var refreshRate: Float
            get() = nativeInfo.refreshRate
            set(value) { nativeInfo.refreshRate = value }
    }
 
    actual class FrameRateOptions actual constructor() {
        val nativeOptions = AndroidRenderer.FrameRateOptions()
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
        val nativeOptions = AndroidRenderer.ClearOptions()
        actual var clearColor: DoubleArray
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
        actual val MIRROR_FRAME_FLAG_COMMIT: Int = AndroidRenderer.MIRROR_FRAME_FLAG_COMMIT
        actual val MIRROR_FRAME_FLAG_SET_PRESENTATION_TIME: Int = AndroidRenderer.MIRROR_FRAME_FLAG_SET_PRESENTATION_TIME
        actual val MIRROR_FRAME_FLAG_CLEAR: Int = AndroidRenderer.MIRROR_FRAME_FLAG_CLEAR
    }

    actual val engine: Engine get() = _engine

    actual var displayInfo: DisplayInfo
        get() {
            if (mDisplayInfo == null) mDisplayInfo = DisplayInfo()
            return mDisplayInfo!!
        }
        set(value) {
            mDisplayInfo = value
            nativeRenderer.setDisplayInfo(value.nativeInfo)
        }

    actual var frameRateOptions: FrameRateOptions
        get() {
            if (mFrameRateOptions == null) mFrameRateOptions = FrameRateOptions()
            return mFrameRateOptions!!
        }
        set(value) {
            mFrameRateOptions = value
            nativeRenderer.setFrameRateOptions(value.nativeOptions)
        }

    actual var clearOptions: ClearOptions
        get() {
            if (mClearOptions == null) mClearOptions = ClearOptions()
            return mClearOptions!!
        }
        set(value) {
            mClearOptions = value
            nativeRenderer.setClearOptions(value.nativeOptions)
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
        nativeRenderer.copyFrame(dstSwapChain.nativeSwapChain, 
            AndroidViewport(dstViewport.left, dstViewport.bottom, dstViewport.width, dstViewport.height),
            AndroidViewport(srcViewport.left, srcViewport.bottom, srcViewport.width, srcViewport.height), 
            flags)

    actual fun readPixels(xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Texture.PixelBufferDescriptor) {
        nativeRenderer.readPixels(xoffset, yoffset, width, height, buffer.toNative())
    }

    actual fun readPixels(renderTarget: RenderTarget, xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Texture.PixelBufferDescriptor) {
        nativeRenderer.readPixels(renderTarget.nativeRenderTarget, xoffset, yoffset, width, height, buffer.toNative())
    }

    actual val userTime: Double get() = nativeRenderer.userTime
    actual fun resetUserTime() = nativeRenderer.resetUserTime()
    actual fun skipNextFrames(frameCount: Int) = nativeRenderer.skipNextFrames(frameCount)
    actual val frameToSkipCount: Int get() = nativeRenderer.frameToSkipCount
}
