package dev.filament.kmp

import kotlinx.cinterop.*
import filament.*

actual class Renderer internal constructor(
    internal val nativeObject: CPointer<FilaRenderer>
) {
    actual class DisplayInfo actual constructor() {
        actual var refreshRate: Float = 60.0f
    }

    actual class FrameRateOptions actual constructor() {
        actual var headRoomRatio: Float = 0.0f
        actual var scaleRate: Float = 0.125f
        actual var history: Int = 15
        actual var interval: Int = 1
    }

    actual class ClearOptions actual constructor() {
        actual var clearColor: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)
        actual var clear: Boolean = false
        actual var discard: Boolean = true
    }

    actual fun beginFrame(swapChain: SwapChain): Boolean = FilaRenderer_beginFrame(nativeObject, swapChain.nativeObject, 0u)
    actual fun beginFrame(swapChain: SwapChain, frameTimeNanos: Long): Boolean = FilaRenderer_beginFrame(nativeObject, swapChain.nativeObject, frameTimeNanos.toULong())
    
    actual fun endFrame() {
        FilaRenderer_endFrame(nativeObject)
    }

    actual fun render(view: View) {
        FilaRenderer_render(nativeObject, view.nativeObject)
    }

    actual fun renderStandaloneView(view: View) {
        FilaRenderer_renderStandaloneView(nativeObject, view.nativeObject)
    }

    actual fun copyFrame(dstSwapChain: SwapChain, dstViewport: Viewport, srcViewport: Viewport, flags: Int) {
        FilaRenderer_copyFrame(nativeObject, dstSwapChain.nativeObject,
            dstViewport.left, dstViewport.bottom, dstViewport.width, dstViewport.height,
            srcViewport.left, srcViewport.bottom, srcViewport.width, srcViewport.height,
            flags.toUInt())
    }

    actual fun readPixels(xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Texture.PixelBufferDescriptor) {
        val storagePtr = buffer.storage.nativePointer() ?: return
        val sizeInBytes = (buffer.storage.limit() * buffer.storage.elementSize()).toULong()
        FilaRenderer_readPixels(nativeObject, xoffset.toUInt(), yoffset.toUInt(), width.toUInt(), height.toUInt(),
            storagePtr, sizeInBytes, buffer.format.toNative(), buffer.type.toNative(),
            buffer.alignment.toUByte(), buffer.left.toUInt(), buffer.top.toUInt(), buffer.stride.toUInt(),
            null, null, null)
    }

    actual fun readPixels(renderTarget: RenderTarget, xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Texture.PixelBufferDescriptor) {
        val storagePtr = buffer.storage.nativePointer() ?: return
        val sizeInBytes = (buffer.storage.limit() * buffer.storage.elementSize()).toULong()
        FilaRenderer_readPixelsRenderTarget(nativeObject, renderTarget.nativeObject,
            xoffset.toUInt(), yoffset.toUInt(), width.toUInt(), height.toUInt(),
            storagePtr, sizeInBytes, buffer.format.toNative(), buffer.type.toNative(),
            buffer.alignment.toUByte(), buffer.left.toUInt(), buffer.top.toUInt(), buffer.stride.toUInt(),
            null, null, null)
    }

    actual fun getUserTime(): Double = FilaRenderer_getUserTime(nativeObject)
    actual fun resetUserTime() { FilaRenderer_resetUserTime(nativeObject) }

    actual fun setDisplayInfo(info: DisplayInfo) {
        memScoped {
            val nativeInfo = alloc<FilaRendererDisplayInfo>()
            nativeInfo.refreshRate = info.refreshRate
            FilaRenderer_setDisplayInfo(nativeObject, nativeInfo.ptr)
        }
    }

    actual fun setFrameRateOptions(options: FrameRateOptions) {
        memScoped {
            val nativeOptions = alloc<FilaRendererFrameRateOptions>()
            nativeOptions.headRoomRatio = options.headRoomRatio
            nativeOptions.scaleRate = options.scaleRate
            nativeOptions.history = options.history.toUByte()
            nativeOptions.interval = options.interval.toUByte()
            FilaRenderer_setFrameRateOptions(nativeObject, nativeOptions.ptr)
        }
    }

    actual fun setClearOptions(options: ClearOptions) {
        memScoped {
            val nativeOptions = alloc<FilaRendererClearOptions>()
            nativeOptions.clearColor[0] = options.clearColor[0]
            nativeOptions.clearColor[1] = options.clearColor[1]
            nativeOptions.clearColor[2] = options.clearColor[2]
            nativeOptions.clearColor[3] = options.clearColor[3]
            nativeOptions.clear = options.clear
            nativeOptions.discard = options.discard
            FilaRenderer_setClearOptions(nativeObject, nativeOptions.ptr)
        }
    }

    actual fun skipFrame(vsyncSteadyClockTimeNano: Long) {
        FilaRenderer_skipFrame(nativeObject, vsyncSteadyClockTimeNano.toULong())
    }

    actual fun setVsyncTime(steadyClockTimeNano: Long) {
        FilaRenderer_setVsyncTime(nativeObject, steadyClockTimeNano.toULong())
    }

    actual val nativeObject: Long get() = nativeObject.toLong()
}
