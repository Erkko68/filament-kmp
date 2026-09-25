package io.github.erkko68.filament

import io.github.erkko68.filament.wasm.*

actual class Renderer @InternalFilamentApi constructor(internal var nativeHandle: Int) {
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
        actual var clearColor: DoubleArray = doubleArrayOf(0.0, 0.0, 0.0, 0.0)
        actual var clear: Boolean = false
        actual var discard: Boolean = true
    }

    actual object MirrorFrameFlag {
        actual val COMMIT: Int = 0x1
        actual val SET_PRESENTATION_TIME: Int = 0x2
        actual val CLEAR: Int = 0x4
    }

    private lateinit var _engine: Engine
    internal fun setEngine(engine: Engine): Renderer {
        this._engine = engine
        return this
    }
    actual val engine: Engine get() = _engine

    private var _displayInfo = DisplayInfo()
    actual var displayInfo: DisplayInfo
        get() = _displayInfo
        set(value) {
            _displayInfo = value
            fila.heapScoped {
                val cInfo = FilaRendererDisplayInfo(alloc(FilaRendererDisplayInfo.SIZE))
                cInfo.refreshRate = value.refreshRate
                FilaRenderer_setDisplayInfo(nativeHandle, cInfo.ptr)
            }
        }

    private var _frameRateOptions = FrameRateOptions()
    actual var frameRateOptions: FrameRateOptions
        get() = _frameRateOptions
        set(value) {
            _frameRateOptions = value
            fila.heapScoped {
                val cOptions = FilaRendererFrameRateOptions(alloc(FilaRendererFrameRateOptions.SIZE))
                cOptions.interval = value.interval
                cOptions.headRoomRatio = value.headRoomRatio
                cOptions.scaleRate = value.scaleRate
                cOptions.history = value.history
                FilaRenderer_setFrameRateOptions(nativeHandle, cOptions.ptr)
            }
        }

    actual var clearOptions: ClearOptions
        get() = fila.heapScoped {
            val out = FilaRendererClearOptions(alloc(FilaRendererClearOptions.SIZE))
            FilaRenderer_getClearOptions(nativeHandle, out.ptr)
            ClearOptions().apply {
                clearColor = doubleArrayOf(out.clearColor[0], out.clearColor[1], out.clearColor[2], out.clearColor[3])
                clear = out.clear
                discard = out.discard
            }
        }
        set(value) {
            fila.heapScoped {
                val cOptions = FilaRendererClearOptions(alloc(FilaRendererClearOptions.SIZE))
                value.clearColor.forEachIndexed { i, v -> cOptions.clearColor[i] = v }
                cOptions.clear = value.clear
                cOptions.discard = value.discard
                FilaRenderer_setClearOptions(nativeHandle, cOptions.ptr)
            }
        }

    actual fun setPresentationTime(monotonicClockNanos: Long) = FilaRenderer_setPresentationTime(nativeHandle, monotonicClockNanos)
    actual fun setDesiredPresentationTime(monotonicClockNanos: Long) = FilaRenderer_setDesiredPresentationTime(nativeHandle, monotonicClockNanos)
    actual fun setRenderingDeadline(monotonicClockNanos: Long) = FilaRenderer_setRenderingDeadline(nativeHandle, monotonicClockNanos)
    actual fun setVsyncTime(steadyClockTimeNano: Long) = FilaRenderer_setVsyncTime(nativeHandle, steadyClockTimeNano)
    actual fun skipFrame(vsyncSteadyClockTimeNano: Long) = FilaRenderer_skipFrame(nativeHandle, vsyncSteadyClockTimeNano)
    actual fun shouldRenderFrame(): Boolean = FilaRenderer_shouldRenderFrame(nativeHandle)
    actual fun beginFrame(swapChain: SwapChain, frameTimeNanos: Long): Boolean = FilaRenderer_beginFrame(nativeHandle, swapChain.nativeHandle, frameTimeNanos)
    actual fun endFrame() = FilaRenderer_endFrame(nativeHandle)
    actual fun render(view: View) = FilaRenderer_render(nativeHandle, view.nativeHandle)
    actual fun renderStandaloneView(view: View) = FilaRenderer_renderStandaloneView(nativeHandle, view.nativeHandle)
    actual fun copyFrame(dstSwapChain: SwapChain, dstViewport: Viewport, srcViewport: Viewport, flags: Int) {
        FilaRenderer_copyFrame(nativeHandle, dstSwapChain.nativeHandle, 
            dstViewport.left, dstViewport.bottom, dstViewport.width, dstViewport.height,
            srcViewport.left, srcViewport.bottom, srcViewport.width, srcViewport.height,
            flags)
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "delivers asynchronously — the pixels are copied into the buffer when the frame completes, before its callback runs, rather than on return.")
    actual fun readPixels(xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Texture.PixelBufferDescriptor) {
        val size = buffer.sizeInBytes
        val ptr = fila.allocZeroed(size)
        val userData = pixelsInto(buffer)
        FilaRenderer_readPixels(
            nativeHandle, 
            xoffset, yoffset, width, height,
            ptr, size,
            buffer.format.toNative(), buffer.type.toNative(),
            buffer.alignment, buffer.left, buffer.top, buffer.stride,
            0, Callbacks.keepBuffer, userData
        )
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "delivers asynchronously — the pixels are copied into the buffer when the frame completes, before its callback runs, rather than on return.")
    actual fun readPixels(renderTarget: RenderTarget, xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Texture.PixelBufferDescriptor) {
        val size = buffer.sizeInBytes
        val ptr = fila.allocZeroed(size)
        val userData = pixelsInto(buffer)
        FilaRenderer_readPixelsRenderTarget(
            nativeHandle, renderTarget.nativeHandle,
            xoffset, yoffset, width, height,
            ptr, size,
            buffer.format.toNative(), buffer.type.toNative(),
            buffer.alignment, buffer.left, buffer.top, buffer.stride,
            0, Callbacks.keepBuffer, userData
        )
    }

    actual val userTime: Double get() = FilaRenderer_getUserTime(nativeHandle)
    actual fun resetUserTime() = FilaRenderer_resetUserTime(nativeHandle)
    actual val materialTime: Double get() = FilaRenderer_getMaterialTime(nativeHandle)
    actual fun setMaterialTimeEpoch(timeEpochInNs: Long) = FilaRenderer_setMaterialTimeEpoch(nativeHandle, timeEpochInNs)
    actual fun pauseRenderThread(timeNs: Long) = FilaRenderer_pauseRenderThread(nativeHandle, timeNs)
    actual fun skipNextFrames(frameCount: Int) = FilaRenderer_skipNextFrames(nativeHandle, frameCount)
    actual val frameToSkipCount: Int get() = FilaRenderer_getFrameToSkipCount(nativeHandle).toInt()
}

// readPixels writes into a heap buffer; copy it into the caller's storage when Filament releases it.
private fun pixelsInto(buffer: Texture.PixelBufferDescriptor): Int = Callbacks.register(once = true) { ptr, size ->
    fila.readBytes(ptr, size).copyInto(buffer.storage)
    fila._free(ptr)
    buffer.callback?.invoke()
}
