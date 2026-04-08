@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaRenderer

actual class Renderer internal constructor(internal var nativeHandle: CPointer<FilaRenderer>?) {
    actual class DisplayInfo actual constructor() {
        actual var refreshRate: Float = 60.0f
    }

    actual class FrameRateOptions actual constructor() {
        actual var interval: Float = 1.0f
        actual var headRoomRatio: Float = 0.0f
        actual var scaleRate: Float = 1.0f / 15.0f
        actual var history: Int = 15
    }

    actual class ClearOptions actual constructor() {
        actual var clearColor: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)
        actual var clear: Boolean = false
        actual var discard: Boolean = true
    }

    actual companion object {
        actual val MIRROR_FRAME_FLAG_COMMIT: Int = 0x1
        actual val MIRROR_FRAME_FLAG_SET_PRESENTATION_TIME: Int = 0x2
        actual val MIRROR_FRAME_FLAG_CLEAR: Int = 0x4
    }

    private lateinit var engine: Engine
    internal fun setEngine(engine: Engine): Renderer {
        this.engine = engine
        return this
    }
    actual fun getEngine(): Engine = engine

    actual fun setDisplayInfo(info: DisplayInfo) {
        memScoped {
            val cInfo = alloc<FilaRendererDisplayInfo>()
            cInfo.refreshRate = info.refreshRate
            FilaRenderer_setDisplayInfo(nativeHandle, cInfo.ptr)
        }
    }

    actual fun getDisplayInfo(): DisplayInfo = DisplayInfo() // Cache not implemented

    actual fun setFrameRateOptions(options: FrameRateOptions) {
        memScoped {
            val cOptions = alloc<FilaRendererFrameRateOptions>()
            cOptions.interval = options.interval
            cOptions.headRoomRatio = options.headRoomRatio
            cOptions.scaleRate = options.scaleRate
            cOptions.history = options.history.toUByte()
            FilaRenderer_setFrameRateOptions(nativeHandle, cOptions.ptr)
        }
    }

    actual fun getFrameRateOptions(): FrameRateOptions = FrameRateOptions()

    actual fun setClearOptions(options: ClearOptions) {
        memScoped {
            val cOptions = alloc<FilaRendererClearOptions>()
            options.clearColor.forEachIndexed { i, v -> cOptions.clearColor[i] = v }
            cOptions.clear = options.clear
            cOptions.discard = options.discard
            FilaRenderer_setClearOptions(nativeHandle, cOptions.ptr)
        }
    }

    actual fun getClearOptions(): ClearOptions = ClearOptions()

    actual fun setPresentationTime(monotonicClockNanos: Long) = FilaRenderer_setPresentationTime(nativeHandle, monotonicClockNanos.toULong())
    actual fun setVsyncTime(steadyClockTimeNano: Long) = FilaRenderer_setVsyncTime(nativeHandle, steadyClockTimeNano.toULong())
    actual fun skipFrame(vsyncSteadyClockTimeNano: Long) = FilaRenderer_skipFrame(nativeHandle, vsyncSteadyClockTimeNano.toULong())
    actual fun shouldRenderFrame(): Boolean = FilaRenderer_shouldRenderFrame(nativeHandle)
    actual fun beginFrame(swapChain: SwapChain, frameTimeNanos: Long): Boolean = FilaRenderer_beginFrame(nativeHandle, swapChain.nativeHandle, frameTimeNanos.toULong())
    actual fun endFrame() = FilaRenderer_endFrame(nativeHandle)
    actual fun render(view: View) = FilaRenderer_render(nativeHandle, view.nativeHandle)
    actual fun renderStandaloneView(view: View) = FilaRenderer_renderStandaloneView(nativeHandle, view.nativeHandle)
    actual fun copyFrame(dstSwapChain: SwapChain, dstViewport: Viewport, srcViewport: Viewport, flags: Int) {
        FilaRenderer_copyFrame(nativeHandle, dstSwapChain.nativeHandle, 
            dstViewport.left, dstViewport.bottom, dstViewport.width, dstViewport.height,
            srcViewport.left, srcViewport.bottom, srcViewport.width, srcViewport.height,
            flags.toUInt())
    }

    actual fun readPixels(xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Texture.PixelBufferDescriptor) {
        val size = buffer.sizeInBytes.toULong()
        val pinned = buffer.storage.pin()
        val ptr = pinned.addressOf(0).reinterpret<ByteVar>()

        FilaRenderer_readPixels(
            nativeHandle, 
            xoffset.toUInt(), yoffset.toUInt(), width.toUInt(), height.toUInt(),
            ptr, size,
            buffer.format.toNative(), buffer.type.toNative(),
            buffer.alignment.toUByte(), buffer.left.toUInt(), buffer.top.toUInt(), buffer.stride.toUInt(),
            null, null, null
        )
        pinned.unpin()
    }

    actual fun readPixels(renderTarget: RenderTarget, xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Texture.PixelBufferDescriptor) {
        val size = buffer.sizeInBytes.toULong()
        val pinned = buffer.storage.pin()
        val ptr = pinned.addressOf(0).reinterpret<ByteVar>()

        FilaRenderer_readPixelsRenderTarget(
            nativeHandle, renderTarget.nativeHandle,
            xoffset.toUInt(), yoffset.toUInt(), width.toUInt(), height.toUInt(),
            ptr, size,
            buffer.format.toNative(), buffer.type.toNative(),
            buffer.alignment.toUByte(), buffer.left.toUInt(), buffer.top.toUInt(), buffer.stride.toUInt(),
            null, null, null
        )
        pinned.unpin()
    }

    actual fun getUserTime(): Double = FilaRenderer_getUserTime(nativeHandle)
    actual fun resetUserTime() = FilaRenderer_resetUserTime(nativeHandle)
    actual fun skipNextFrames(frameCount: Int) = FilaRenderer_skipNextFrames(nativeHandle, frameCount.toUInt())
    actual fun getFrameToSkipCount(): Int = FilaRenderer_getFrameToSkipCount(nativeHandle).toInt()
}
