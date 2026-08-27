package io.github.erkko68.filament

import com.google.android.filament.Renderer as AndroidRenderer
import com.google.android.filament.Viewport as AndroidViewport

actual class Renderer @InternalFilamentApi constructor(
    private val _engine: Engine,
    internal val nativeRenderer: AndroidRenderer
) {
    private var mDisplayInfo: DisplayInfo? = null
    private var mFrameRateOptions: FrameRateOptions? = null
    private var mClearOptions: ClearOptions? = null
 
    actual object MirrorFrameFlag {
        actual val COMMIT: Int = AndroidRenderer.MIRROR_FRAME_FLAG_COMMIT
        actual val SET_PRESENTATION_TIME: Int = AndroidRenderer.MIRROR_FRAME_FLAG_SET_PRESENTATION_TIME
        actual val CLEAR: Int = AndroidRenderer.MIRROR_FRAME_FLAG_CLEAR
    }

    actual val engine: Engine get() = _engine

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "state is only tracked locally — setDisplayInfo is not bound in filament.js; frame pacing is managed by the browser.")
    actual var displayInfo: DisplayInfo
        get() {
            if (mDisplayInfo == null) mDisplayInfo = DisplayInfo()
            return mDisplayInfo!!
        }
        set(value) {
            mDisplayInfo = value
            nativeRenderer.setDisplayInfo(value.toAndroid())
        }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "state is only tracked locally — setFrameRateOptions is not bound in filament.js; frame pacing is managed by the browser.")
    actual var frameRateOptions: FrameRateOptions
        get() {
            if (mFrameRateOptions == null) mFrameRateOptions = FrameRateOptions()
            return mFrameRateOptions!!
        }
        set(value) {
            mFrameRateOptions = value
            nativeRenderer.setFrameRateOptions(value.toAndroid())
        }

    actual var clearOptions: ClearOptions
        get() {
            if (mClearOptions == null) mClearOptions = ClearOptions()
            return mClearOptions!!
        }
        set(value) {
            mClearOptions = value
            nativeRenderer.setClearOptions(value.toAndroid())
        }

    actual fun setPresentationTime(monotonicClockNanos: Long) = nativeRenderer.setPresentationTime(monotonicClockNanos)
    actual fun setDesiredPresentationTime(monotonicClockNanos: Long) = nativeRenderer.setDesiredPresentationTime(monotonicClockNanos)
    actual fun setRenderingDeadline(monotonicClockNanos: Long) = nativeRenderer.setRenderingDeadline(monotonicClockNanos)
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
    actual val materialTime: Double get() = nativeRenderer.materialTime
    actual fun setMaterialTimeEpoch(timeEpochInNs: Long) = nativeRenderer.setMaterialTimeEpoch(timeEpochInNs)
    actual fun pauseRenderThread(timeNs: Long) = nativeRenderer.pauseRenderThread(timeNs)
    actual fun skipNextFrames(frameCount: Int) = nativeRenderer.skipNextFrames(frameCount)
    actual val frameToSkipCount: Int get() = nativeRenderer.frameToSkipCount
}
