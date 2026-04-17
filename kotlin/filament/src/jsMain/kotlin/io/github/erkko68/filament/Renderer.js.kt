package io.github.erkko68.filament

actual class Renderer {
    actual fun getEngine(): Engine {
        TODO("Not yet implemented")
    }

    actual fun setDisplayInfo(info: DisplayInfo) {
    }

    actual fun getDisplayInfo(): DisplayInfo {
        TODO("Not yet implemented")
    }

    actual fun setFrameRateOptions(options: FrameRateOptions) {
    }

    actual fun getFrameRateOptions(): FrameRateOptions {
        TODO("Not yet implemented")
    }

    actual fun setClearOptions(options: ClearOptions) {
    }

    actual fun getClearOptions(): ClearOptions {
        TODO("Not yet implemented")
    }

    actual fun setPresentationTime(monotonicClockNanos: Long) {
    }

    actual fun setVsyncTime(steadyClockTimeNano: Long) {
    }

    actual fun skipFrame(vsyncSteadyClockTimeNano: Long) {
    }

    actual fun shouldRenderFrame(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun beginFrame(
        swapChain: SwapChain,
        frameTimeNanos: Long
    ): Boolean {
        TODO("Not yet implemented")
    }

    actual fun endFrame() {
    }

    actual fun render(view: View) {
    }

    actual fun renderStandaloneView(view: View) {
    }

    actual fun copyFrame(
        dstSwapChain: SwapChain,
        dstViewport: Viewport,
        srcViewport: Viewport,
        flags: Int
    ) {
    }

    actual fun readPixels(
        xoffset: Int,
        yoffset: Int,
        width: Int,
        height: Int,
        buffer: Texture.PixelBufferDescriptor
    ) {
    }

    actual fun readPixels(
        renderTarget: RenderTarget,
        xoffset: Int,
        yoffset: Int,
        width: Int,
        height: Int,
        buffer: Texture.PixelBufferDescriptor
    ) {
    }

    actual fun getUserTime(): Double {
        TODO("Not yet implemented")
    }

    actual fun resetUserTime() {
    }

    actual fun skipNextFrames(frameCount: Int) {
    }

    actual fun getFrameToSkipCount(): Int {
        TODO("Not yet implemented")
    }

    actual class DisplayInfo {
        actual var refreshRate: Float
            get() = TODO("Not yet implemented")
            set(value) {}
    }

    actual class FrameRateOptions {
        actual var interval: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var headRoomRatio: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var scaleRate: Float
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var history: Int
            get() = TODO("Not yet implemented")
            set(value) {}
    }

    actual class ClearOptions {
        actual var clearColor: FloatArray
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var clear: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        actual var discard: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
    }

    actual companion object {
        actual val MIRROR_FRAME_FLAG_COMMIT: Int
            get() = TODO("Not yet implemented")
        actual val MIRROR_FRAME_FLAG_SET_PRESENTATION_TIME: Int
            get() = TODO("Not yet implemented")
        actual val MIRROR_FRAME_FLAG_CLEAR: Int
            get() = TODO("Not yet implemented")
    }
}