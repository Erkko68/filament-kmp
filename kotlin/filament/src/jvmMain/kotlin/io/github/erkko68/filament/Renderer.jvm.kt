package io.github.erkko68.filament

import io.github.erkko68.filament.jni.Renderer as JniRenderer
import io.github.erkko68.filament.jni.Viewport as JniViewport

actual class Renderer(private val engineRef: Engine, val nativeRenderer: JniRenderer) {
    actual class DisplayInfo actual constructor() {
        actual var refreshRate: Float = 60.0f
        
        internal fun toJni(): JniRenderer.DisplayInfo {
            val jni = JniRenderer.DisplayInfo()
            jni.refreshRate = refreshRate
            return jni
        }
    }

    actual class FrameRateOptions actual constructor() {
        private val jni = JniRenderer.FrameRateOptions()
        
        actual var interval: Float
            get() = jni.interval
            set(value) { jni.interval = value }
        actual var headRoomRatio: Float
            get() = jni.headRoomRatio
            set(value) { jni.headRoomRatio = value }
        actual var scaleRate: Float
            get() = jni.scaleRate
            set(value) { jni.scaleRate = value }
        actual var history: Int
            get() = jni.history
            set(value) { jni.history = value }
            
        internal fun toJni() = jni
    }

    actual class ClearOptions actual constructor() {
        private val jni = JniRenderer.ClearOptions()

        actual var clearColor: DoubleArray
            get() = jni.clearColor
            set(value) { jni.clearColor = value }
        actual var clear: Boolean
            get() = jni.clear
            set(value) { jni.clear = value }
        actual var discard: Boolean
            get() = jni.discard
            set(value) { jni.discard = value }

        internal fun toJni() = jni
    }

    actual companion object {
        actual val MIRROR_FRAME_FLAG_COMMIT: Int = JniRenderer.MIRROR_FRAME_FLAG_COMMIT
        actual val MIRROR_FRAME_FLAG_SET_PRESENTATION_TIME: Int = JniRenderer.MIRROR_FRAME_FLAG_SET_PRESENTATION_TIME
        actual val MIRROR_FRAME_FLAG_CLEAR: Int = JniRenderer.MIRROR_FRAME_FLAG_CLEAR
    }

    actual val engine: Engine get() = engineRef

    actual var displayInfo: DisplayInfo
        get() {
            val jni = nativeRenderer.getDisplayInfo()
            val info = DisplayInfo()
            info.refreshRate = jni.refreshRate
            return info
        }
        set(value) { nativeRenderer.setDisplayInfo(value.toJni()) }

    actual var frameRateOptions: FrameRateOptions
        get() {
            val jni = nativeRenderer.getFrameRateOptions()
            val options = FrameRateOptions()
            options.interval = jni.interval
            options.headRoomRatio = jni.headRoomRatio
            options.scaleRate = jni.scaleRate
            options.history = jni.history
            return options
        }
        set(value) { nativeRenderer.setFrameRateOptions(value.toJni()) }

    actual var clearOptions: ClearOptions
        get() {
            val jni = nativeRenderer.getClearOptions()
            val options = ClearOptions()
            options.clearColor = jni.clearColor
            options.clear = jni.clear
            options.discard = jni.discard
            return options
        }
        set(value) { nativeRenderer.setClearOptions(value.toJni()) }

    actual fun setPresentationTime(monotonicClockNanos: Long) : Unit { nativeRenderer.setPresentationTime(monotonicClockNanos) }
    actual fun setVsyncTime(steadyClockTimeNano: Long) : Unit { nativeRenderer.setVsyncTime(steadyClockTimeNano) }
    actual fun skipFrame(vsyncSteadyClockTimeNano: Long) : Unit { nativeRenderer.skipFrame(vsyncSteadyClockTimeNano) }
    actual fun shouldRenderFrame(): Boolean = nativeRenderer.shouldRenderFrame()

    actual fun beginFrame(swapChain: SwapChain, frameTimeNanos: Long): Boolean = 
        nativeRenderer.beginFrame(swapChain.nativeSwapChain, frameTimeNanos)

    actual fun endFrame() : Unit { nativeRenderer.endFrame() }
    
    actual fun render(view: View) : Unit { nativeRenderer.render(view.nativeView) }
    actual fun renderStandaloneView(view: View) : Unit { nativeRenderer.renderStandaloneView(view.nativeView) }

    actual fun copyFrame(dstSwapChain: SwapChain, dstViewport: Viewport, srcViewport: Viewport, flags: Int) : Unit {
        nativeRenderer.copyFrame(
            dstSwapChain.nativeSwapChain,
            JniViewport(dstViewport.left, dstViewport.bottom, dstViewport.width, dstViewport.height),
            JniViewport(srcViewport.left, srcViewport.bottom, srcViewport.width, srcViewport.height),
            flags
        )
    }

    actual fun readPixels(xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Texture.PixelBufferDescriptor) : Unit {
        nativeRenderer.readPixels(xoffset, yoffset, width, height, buffer.jni)
    }

    actual fun readPixels(renderTarget: RenderTarget, xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Texture.PixelBufferDescriptor) : Unit {
        nativeRenderer.readPixels(renderTarget.nativeRenderTarget, xoffset, yoffset, width, height, buffer.jni)
    }

    actual val userTime: Double get() = nativeRenderer.getUserTime()
    actual fun resetUserTime() : Unit { nativeRenderer.resetUserTime() }
    actual fun skipNextFrames(frameCount: Int) : Unit { nativeRenderer.skipNextFrames(frameCount) }
    actual val frameToSkipCount: Int get() = nativeRenderer.getFrameToSkipCount()
}
