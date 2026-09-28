package io.github.erkko68.filament

import io.github.erkko68.filament.interop.*

/**
 * Renderer represents an operating system window and manages frame rendering and pacing.
 *
 * Typically, applications create a Renderer per window. The Renderer generates drawing commands
 * for the render thread and manages frame latency to keep it low.
 *
 * A Renderer generates drawing commands from a View, which itself contains a Scene description.
 *
 * **Typical render loop:**
 * ```
 * while (!quit) {
 *     if (renderer.beginFrame(swapChain)) {
 *         renderer.render(view)
 *         renderer.endFrame()
 *     }
 * }
 * ```
 *
 * **Frame pacing:** beginFrame() manages frame pacing and returns false if the GPU is
 * falling behind (to skip frames and reduce latency). When false is returned, either
 * skip the frame or proceed anyway; if proceeding, you must still call endFrame().
 *
 * @see Engine
 * @see View
 */
class Renderer @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * Display refresh-rate information for frame pacing and dynamic resolution scaling.
     *
     * This is used to achieve correct frame pacing and dynamic resolution scaling.
     */
    class DisplayInfo {
        /** Refresh rate of the display in Hz. Set to 0 for offscreen rendering or to disable frame pacing. Default: 60. */
        var refreshRate: Float = 60.0f
    }

    /**
     * Frame rate control and dynamic resolution scaling options.
     *
     * Controls the desired frame rate and how quickly the system reacts to GPU load changes.
     *
     * interval: Desired frame interval in multiples of the refresh period (1 / DisplayInfo.refreshRate).
     *           Set to 1 to render at the display refresh rate.
     *
     * The parameters below are relevant when some Views are using dynamic resolution scaling:
     *
     * headRoomRatio: Additional headroom for the GPU as a ratio of the target frame time.
     *                Useful for taking into account constant costs like post-processing or
     *                GPU drivers on different platforms.
     * scaleRate: Rate at which the GPU load is adjusted to reach the target frame rate.
     *            This value can be computed as 1 / N, where N is the number of frames
     *            needed to reach 64% of the target scale factor. Higher values make the
     *            dynamic resolution react faster.
     * history: History size for filtering GPU load. Higher values tend to filter more.
     *          Clamped to 31.
     */
    class FrameRateOptions {
        /** Desired frame interval (1 = render every vsync). */
        var interval: Float = 1.0f
        /** Additional headroom for the GPU as a fraction of target frame time. */
        var headRoomRatio: Float = 0.0f
        /** Rate at which GPU load is adjusted; computed as 1/N frames to reach 64% target. */
        var scaleRate: Float = 1.0f / 15.0f
        /** History size for load filtering (clamped to 31). */
        var history: Int = 15
    }

    /**
     * ClearOptions control how the SwapChain is cleared or discarded at the beginning of a frame.
     *
     * The RenderTarget is cleared using the clearColor. The RenderTarget is cleared using this color,
     * which won't be tone-mapped since tone-mapping is part of View rendering (this is not).
     */
    class ClearOptions {
        /** RGBA clear color. Values are stored as doubles. The backend converts them as-is based on format. */
        var clearColor: DoubleArray = doubleArrayOf(0.0, 0.0, 0.0, 0.0)
        /** Whether the SwapChain should be cleared using clearColor. Default: false. */
        var clear: Boolean = false
        /** Whether the SwapChain content should be discarded. Default: true. Set false to preserve existing content. */
        var discard: Boolean = true
    }

    /** Bit flags for the `flags` argument of [copyFrame]. */
    object MirrorFrameFlag {
        /** Indicates dstSwapChain should be committed after copyFrame. */
        val COMMIT: Int = 0x1
        /** Indicates presentation time should be set on dstSwapChain in copyFrame. */
        val SET_PRESENTATION_TIME: Int = 0x2
        /** Indicates dstSwapChain should be cleared to black before copyFrame. */
        val CLEAR: Int = 0x4
    }

    private lateinit var _engine: Engine
    internal fun setEngine(engine: Engine): Renderer {
        this._engine = engine
        return this
    }
    /** Get the Engine that created this Renderer. */
    val engine: Engine get() = _engine

    private var _displayInfo = DisplayInfo()
    /**
     * Get/set display information for frame pacing and dynamic resolution.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var displayInfo: DisplayInfo
        get() = _displayInfo
        set(value) {
            _displayInfo = value
            FilaRenderer_setDisplayInfo(nativeHandle, value.refreshRate)
        }

    private var _frameRateOptions = FrameRateOptions()
    /**
     * Get/set frame rate control and dynamic resolution options.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var frameRateOptions: FrameRateOptions
        get() = _frameRateOptions
        set(value) {
            _frameRateOptions = value
            FilaRenderer_setFrameRateOptions(nativeHandle, value.headRoomRatio, value.scaleRate, value.history, value.interval)
        }

    /**
     * Get/set clear behavior for the SwapChain.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var clearOptions: ClearOptions
        get() = run {
            val outI = IntArray(2)
            val outD = DoubleArray(4)
            outI.usePinned { pi -> outD.usePinned { pd -> FilaRenderer_getClearOptions(nativeHandle, pi, pd) } }
            ClearOptions().apply {
                clearColor = doubleArrayOf(outD[0], outD[1], outD[2], outD[3])
                clear = (outI[0] != 0)
                discard = (outI[1] != 0)
            }
        }
        set(value) {
            FilaRenderer_setClearOptions(nativeHandle, value.clearColor[0], value.clearColor[1], value.clearColor[2], value.clearColor[3], value.clear, value.discard)
        }

    /**
     * Set the time at which the frame must be presented.
     *
     * This is set as the duration in nanoseconds since epoch of std::chrono::steady_clock.
     * Must be called between beginFrame() and endFrame().
     *
     * @param monotonicClockNanos Time in nanoseconds.
     */
    fun setPresentationTime(monotonicClockNanos: Long) = FilaRenderer_setPresentationTime(nativeHandle, monotonicClockNanos)
    /**
     * Set the real desired presentation time targeted for this frame.
     *
     * Unlike setPresentationTime(), which configures hardware headroom, this is the exact target
     * presentation time and is used for frame history reporting.
     * Must be called before endFrame().
     *
     * @param monotonicClockNanos Desired presentation timestamp in steady_clock nanoseconds.
     */
    fun setDesiredPresentationTime(monotonicClockNanos: Long) = FilaRenderer_setDesiredPresentationTime(nativeHandle, monotonicClockNanos)
    /**
     * Set the deadline by which CPU and GPU rendering must complete for the buffer to meet its
     * target display latching window.
     * Must be called before endFrame().
     *
     * @param monotonicClockNanos Deadline timestamp in steady_clock nanoseconds.
     */
    fun setRenderingDeadline(monotonicClockNanos: Long) = FilaRenderer_setRenderingDeadline(nativeHandle, monotonicClockNanos)
    /**
     * Set the VSYNC time expressed as the duration in nanoseconds since epoch of std::chrono::steady_clock.
     *
     * If called, passing 0 to frameTimeNanos in beginFrame() will use this time instead.
     *
     * @param steadyClockTimeNano Duration in nanoseconds since epoch.
     */
    fun setVsyncTime(steadyClockTimeNano: Long) = FilaRenderer_setVsyncTime(nativeHandle, steadyClockTimeNano)
    /**
     * Skip the current frame for frame pacing.
     *
     * Call this when momentarily skipping frames, for instance if the content of the scene doesn't change.
     *
     * @param vsyncSteadyClockTimeNano VSYNC time in steady_clock nanoseconds.
     */
    fun skipFrame(vsyncSteadyClockTimeNano: Long) = FilaRenderer_skipFrame(nativeHandle, vsyncSteadyClockTimeNano)
    /**
     * Check if the current frame should be rendered.
     *
     * Returns the same result as the last call to beginFrame().
     *
     * @return true if frame should be rendered, false if frame should be skipped.
     */
    fun shouldRenderFrame(): Boolean = FilaRenderer_shouldRenderFrame(nativeHandle)
    /**
     * Prepare a frame for rendering and manage frame pacing.
     *
     * Manages frame pacing and returns whether the frame should be rendered:
     * - true: frame should be rendered; must call render() and endFrame().
     * - false: frame is behind; can skip the frame (don't call endFrame()), or proceed anyway.
     *
     * When false is returned, the application can either skip rendering the frame entirely,
     * or proceed at the cost of increased latency.
     *
     * @param swapChain SwapChain to render into.
     * @param frameTimeNanos VSYNC time in steady_clock nanoseconds, or 0 to use setVsyncTime().
     * @return true if frame should be rendered, false if behind schedule.
     */
    fun beginFrame(swapChain: SwapChain, frameTimeNanos: Long): Boolean = FilaRenderer_beginFrame(nativeHandle, swapChain.nativeHandle, frameTimeNanos)
    /**
     * Finish the current frame and schedule it for display.
     *
     * Must be called after beginFrame(true) returned true. This commits the frame and
     * schedules it for presentation.
     */
    fun endFrame() = FilaRenderer_endFrame(nativeHandle)
    /**
     * Render a View into this Renderer's target.
     *
     * Performs shadow passes, depth pre-pass, color pass, and post-processing.
     *
     * Must be called between beginFrame(true) and endFrame().
     * Can be called multiple times per frame, but typically once.
     *
     * @param view The View to render.
     */
    fun render(view: View) = FilaRenderer_render(nativeHandle, view.nativeHandle)
    /**
     * Render a View without frame pacing.
     *
     * Useful for off-screen rendering or when frame pacing is not desired.
     *
     * @param view The View to render.
     */
    fun renderStandaloneView(view: View) = FilaRenderer_renderStandaloneView(nativeHandle, view.nativeHandle)
    /**
     * Copy the rendered frame to another SwapChain at a specified viewport.
     *
     * Call after render() but before endFrame(). This is useful for rendering to multiple
     * displays or for implementing picture-in-picture functionality.
     *
     * @param dstSwapChain Destination SwapChain.
     * @param dstViewport Destination viewport rectangle.
     * @param srcViewport Source viewport rectangle.
     * @param flags Behavior flags (COMMIT, SET_PRESENTATION_TIME, CLEAR).
     */
    fun copyFrame(dstSwapChain: SwapChain, dstViewport: Viewport, srcViewport: Viewport, flags: Int) {
        FilaRenderer_copyFrame(nativeHandle, dstSwapChain.nativeHandle,
            dstViewport.left, dstViewport.bottom, dstViewport.width, dstViewport.height,
            srcViewport.left, srcViewport.bottom, srcViewport.width, srcViewport.height,
            flags)
    }

    /**
     * Read back SwapChain pixels asynchronously.
     *
     * The buffer's callback is invoked on the main thread when complete (usually after
     * several endFrame() calls). Formats RGBA/RGBA_INTEGER and types UBYTE/UINT/INT/FLOAT
     * are always supported.
     *
     * Call between beginFrame() and endFrame(), typically after render().
     * Impacts performance significantly, especially on some mobile platforms.
     *
     * @param xoffset Left offset in pixels.
     * @param yoffset Bottom offset in pixels.
     * @param width Width in pixels.
     * @param height Height in pixels.
     * @param buffer Pixel buffer descriptor for the result.
     */
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "delivers asynchronously — the pixels are copied into the buffer when the frame completes, before its callback runs, rather than on return.")
    fun readPixels(xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Texture.PixelBufferDescriptor) {
        val (ptr, userData) = pixelsInto(buffer)
        FilaRenderer_readPixels(
            nativeHandle,
            xoffset, yoffset, width, height,
            ptr, buffer.sizeInBytes,
            buffer.format.ordinal, buffer.type.ordinal,
            buffer.alignment, buffer.left, buffer.top, buffer.stride,
            NullPointer, Callbacks.keepBuffer, userData,
        )
    }

    /**
     * [readPixels] straight into native memory the caller owns, without a copy (e.g. a Skia bitmap's
     * pixels). [onDone] runs once [address] holds the frame, possibly on Filament's driver thread.
     */
    @InternalFilamentApi
    fun readPixels(xoffset: Int, yoffset: Int, width: Int, height: Int, address: NativePointer, sizeInBytes: Int, format: Texture.Format, type: Texture.Type, stride: Int, onDone: () -> Unit) {
        FilaRenderer_readPixels(
            nativeHandle,
            xoffset, yoffset, width, height,
            address, sizeInBytes,
            format.ordinal, type.ordinal,
            1, 0, 0, stride,
            NullPointer, Callbacks.keepBuffer, Callbacks.register(once = true) { onDone() },
        )
    }

    /**
     * Read back RenderTarget pixels asynchronously.
     *
     * Similar to readPixels(SwapChain), but reads from a RenderTarget instead.
     *
     * @param renderTarget RenderTarget to read from.
     * @param xoffset Left offset in pixels.
     * @param yoffset Bottom offset in pixels.
     * @param width Width in pixels.
     * @param height Height in pixels.
     * @param buffer Pixel buffer descriptor for the result.
     */
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "delivers asynchronously — the pixels are copied into the buffer when the frame completes, before its callback runs, rather than on return.")
    fun readPixels(renderTarget: RenderTarget, xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Texture.PixelBufferDescriptor) {
        val (ptr, userData) = pixelsInto(buffer)
        FilaRenderer_readPixelsRenderTarget(
            nativeHandle, renderTarget.nativeHandle,
            xoffset, yoffset, width, height,
            ptr, buffer.sizeInBytes,
            buffer.format.ordinal, buffer.type.ordinal,
            buffer.alignment, buffer.left, buffer.top, buffer.stride,
            NullPointer, Callbacks.keepBuffer, userData,
        )
    }

    /**
     * Get elapsed time since resetUserTime() in seconds.
     *
     * @return Elapsed time in seconds.
     */
    val userTime: Double get() = FilaRenderer_getUserTime(nativeHandle)
    /** Reset the user time clock to zero. */
    fun resetUserTime() = FilaRenderer_resetUserTime(nativeHandle)
    /**
     * Get material time in seconds.
     */
    val materialTime: Double get() = FilaRenderer_getMaterialTime(nativeHandle)
    /**
     * Set the epoch for material time in nanoseconds.
     *
     * @param timeEpochInNs Epoch timestamp in steady_clock nanoseconds.
     */
    fun setMaterialTimeEpoch(timeEpochInNs: Long) = FilaRenderer_setMaterialTimeEpoch(nativeHandle, timeEpochInNs)
    /**
     * Pause the render thread for a specified duration in nanoseconds.
     *
     * @param timeNs Duration in nanoseconds to pause the render thread.
     */
    fun pauseRenderThread(timeNs: Long) = FilaRenderer_pauseRenderThread(nativeHandle, timeNs)
    /**
     * Skip the next N frames for frame pacing.
     *
     * @param frameCount Number of frames to skip.
     */
    fun skipNextFrames(frameCount: Int) = FilaRenderer_skipNextFrames(nativeHandle, frameCount)
    /**
     * Get the number of frames left to skip.
     *
     * @return Number of frames remaining to skip.
     */
    val frameToSkipCount: Int get() = FilaRenderer_getFrameToSkipCount(nativeHandle)
}

// readPixels fills native memory (the array itself on Native) when the frame completes; copy it into
// the caller's storage once Filament releases it.
private fun pixelsInto(buffer: Texture.PixelBufferDescriptor): Pair<NativePointer, NativePointer> {
    val scope = InteropScope()
    val ptr = scope.toInterop(buffer.storage)
    val userData = Callbacks.register(once = true) { _ ->
        with(scope) { ptr.fromInterop(buffer.storage) }
        scope.release()
        buffer.callback?.invoke()
    }
    return ptr to userData
}

@ExternalSymbolName("FilaRenderer_beginFrame")
private external fun FilaRenderer_beginFrame(renderer: NativePointer, swapChain: NativePointer, frameTimeNanos: Long): Boolean

@ExternalSymbolName("FilaRenderer_copyFrame")
private external fun FilaRenderer_copyFrame(renderer: NativePointer, dstSwapChain: NativePointer, dstLeft: Int, dstBottom: Int, dstWidth: Int, dstHeight: Int, srcLeft: Int, srcBottom: Int, srcWidth: Int, srcHeight: Int, flags: Int)

@ExternalSymbolName("FilaRenderer_endFrame")
private external fun FilaRenderer_endFrame(renderer: NativePointer)

@ExternalSymbolName("FilaRenderer_getClearOptions")
private external fun FilaRenderer_getClearOptions(renderer: NativePointer, ints: NativePointer, doubles: NativePointer)

@ExternalSymbolName("FilaRenderer_getFrameToSkipCount")
private external fun FilaRenderer_getFrameToSkipCount(renderer: NativePointer): Int

@ExternalSymbolName("FilaRenderer_getMaterialTime")
private external fun FilaRenderer_getMaterialTime(renderer: NativePointer): Double

@ExternalSymbolName("FilaRenderer_getUserTime")
private external fun FilaRenderer_getUserTime(renderer: NativePointer): Double

@ExternalSymbolName("FilaRenderer_pauseRenderThread")
private external fun FilaRenderer_pauseRenderThread(renderer: NativePointer, timeNs: Long)

@ExternalSymbolName("FilaRenderer_readPixels")
private external fun FilaRenderer_readPixels(renderer: NativePointer, xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: NativePointer, sizeInBytes: Int, format: Int, type: Int, alignment: Int, left: Int, top: Int, stride: Int, handler: NativePointer, callback: NativePointer, userData: NativePointer)

@ExternalSymbolName("FilaRenderer_readPixelsRenderTarget")
private external fun FilaRenderer_readPixelsRenderTarget(renderer: NativePointer, renderTarget: NativePointer, xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: NativePointer, sizeInBytes: Int, format: Int, type: Int, alignment: Int, left: Int, top: Int, stride: Int, handler: NativePointer, callback: NativePointer, userData: NativePointer)

@ExternalSymbolName("FilaRenderer_render")
private external fun FilaRenderer_render(renderer: NativePointer, view: NativePointer)

@ExternalSymbolName("FilaRenderer_renderStandaloneView")
private external fun FilaRenderer_renderStandaloneView(renderer: NativePointer, view: NativePointer)

@ExternalSymbolName("FilaRenderer_resetUserTime")
private external fun FilaRenderer_resetUserTime(renderer: NativePointer)

@ExternalSymbolName("FilaRenderer_setClearOptions")
private external fun FilaRenderer_setClearOptions(renderer: NativePointer, clearColor_0: Double, clearColor_1: Double, clearColor_2: Double, clearColor_3: Double, clear: Boolean, discard: Boolean)

@ExternalSymbolName("FilaRenderer_setDesiredPresentationTime")
private external fun FilaRenderer_setDesiredPresentationTime(renderer: NativePointer, monotonicClockNanos: Long)

@ExternalSymbolName("FilaRenderer_setDisplayInfo")
private external fun FilaRenderer_setDisplayInfo(renderer: NativePointer, refreshRate: Float)

@ExternalSymbolName("FilaRenderer_setFrameRateOptions")
private external fun FilaRenderer_setFrameRateOptions(renderer: NativePointer, headRoomRatio: Float, scaleRate: Float, history: Int, interval: Float)

@ExternalSymbolName("FilaRenderer_setMaterialTimeEpoch")
private external fun FilaRenderer_setMaterialTimeEpoch(renderer: NativePointer, timeEpochInNs: Long)

@ExternalSymbolName("FilaRenderer_setPresentationTime")
private external fun FilaRenderer_setPresentationTime(renderer: NativePointer, monotonicClockNanos: Long)

@ExternalSymbolName("FilaRenderer_setRenderingDeadline")
private external fun FilaRenderer_setRenderingDeadline(renderer: NativePointer, monotonicClockNanos: Long)

@ExternalSymbolName("FilaRenderer_setVsyncTime")
private external fun FilaRenderer_setVsyncTime(renderer: NativePointer, steadyClockTimeNano: Long)

@ExternalSymbolName("FilaRenderer_shouldRenderFrame")
private external fun FilaRenderer_shouldRenderFrame(renderer: NativePointer): Boolean

@ExternalSymbolName("FilaRenderer_skipFrame")
private external fun FilaRenderer_skipFrame(renderer: NativePointer, vsyncSteadyClockTimeNano: Long)

@ExternalSymbolName("FilaRenderer_skipNextFrames")
private external fun FilaRenderer_skipNextFrames(renderer: NativePointer, frameCount: Int)
