package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
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
        /** Additional headroom for the GPU as a fraction of target frame time. */
        var headRoomRatio: Float = 0.0f
        /** Rate at which GPU load is adjusted; computed as 1/N frames to reach 64% target. */
        var scaleRate: Float = 1.0f / 8.0f
        /** History size for load filtering (clamped to 31). */
        var history: Int = 15
        /** Desired frame interval in units of 1 / [DisplayInfo.refreshRate] (1 = render every vsync). */
        var interval: Int = 1
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
        /** Value to clear the stencil buffer to. Default: 0. */
        var clearStencil: Int = 0
        /** Whether the SwapChain should be cleared using clearColor. Default: false. */
        var clear: Boolean = false
        /** Whether the SwapChain content should be discarded. Default: true. Set false to preserve existing content. */
        var discard: Boolean = true
    }

    /**
     * Timing information about a frame. Times are nanoseconds since the steady-clock epoch; durations are
     * nanoseconds.
     */
    class FrameInfo(
        /** Monotonically increasing frame identifier. */
        val frameId: Int,
        /** Frame duration on the GPU. */
        val gpuFrameDuration: Long,
        /** Denoised frame duration on the GPU. */
        val denoisedGpuFrameDuration: Long,
        /** Renderer.beginFrame() time. */
        val beginFrame: Long,
        /** Renderer.endFrame() time. */
        val endFrame: Long,
        /** Backend thread time of frame start. */
        val backendBeginFrame: Long,
        /** Backend thread time of frame end. */
        val backendEndFrame: Long,
        /** GPU thread time of frame end, or 0. */
        val gpuFrameComplete: Long,
        /** VSYNC time of this frame. */
        val vsync: Long,
        /** Actual presentation time of this frame. */
        val displayPresent: Long,
        /** Deadline for queuing a frame. */
        val presentDeadline: Long,
        /** Display refresh period. */
        val displayPresentInterval: Long,
        /** Time between the start of composition and the expected present time. */
        val compositionToPresentLatency: Long,
        /** Time between vsync and the system's expected presentation time. */
        val expectedPresentLatency: Long,
        /** Frame scheduling callback entry time. */
        val frameScheduleTime: Long,
    )

    companion object {
        /** [copyFrame] flag: commit dstSwapChain after the copy. */
        const val COMMIT: Int = 0x1
        /** [copyFrame] flag: set dstSwapChain's presentation time to when the frame is copied. */
        const val SET_PRESENTATION_TIME: Int = 0x2
        /** [copyFrame] flag: clear dstSwapChain to black before the copy. */
        const val CLEAR: Int = 0x4
    }

    private lateinit var _engine: Engine
    internal fun setEngine(engine: Engine): Renderer {
        this._engine = engine
        return this
    }
    /** Get the Engine that created this Renderer. */
    val engine: Engine get() = _engine

    /** Sets display information for frame pacing and dynamic resolution. */
    fun setDisplayInfo(info: DisplayInfo) = withHandle({ FilaRendererDisplayInfo_create() }, { FilaRendererDisplayInfo_destroy(it) }) { o ->
        FilaRendererDisplayInfo_setRefreshRate(o, info.refreshRate)
        FilaRenderer_setDisplayInfo(nativeHandle, o)
    }

    /** Sets frame rate control and dynamic resolution options. */
    fun setFrameRateOptions(options: FrameRateOptions) = withHandle({ FilaRendererFrameRateOptions_create() }, { FilaRendererFrameRateOptions_destroy(it) }) { o ->
        FilaRendererFrameRateOptions_setHeadRoomRatio(o, options.headRoomRatio)
        FilaRendererFrameRateOptions_setScaleRate(o, options.scaleRate)
        FilaRendererFrameRateOptions_setHistory(o, options.history)
        FilaRendererFrameRateOptions_setInterval(o, options.interval)
        FilaRenderer_setFrameRateOptions(nativeHandle, o)
    }

    /**
     * Get/set clear behavior for the SwapChain.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var clearOptions: ClearOptions
        get() = withHandle({ FilaRendererClearOptions_create() }, { FilaRendererClearOptions_destroy(it) }) { o ->
            FilaRenderer_getClearOptions(nativeHandle, o)
            ClearOptions().apply {
                clearColor.usePinned { FilaRendererClearOptions_getClearColor(o, it) }
                clearStencil = FilaRendererClearOptions_getClearStencil(o)
                clear = FilaRendererClearOptions_getClear(o)
                discard = FilaRendererClearOptions_getDiscard(o)
            }
        }
        set(value) = withHandle({ FilaRendererClearOptions_create() }, { FilaRendererClearOptions_destroy(it) }) { o ->
            value.clearColor.usePinned { FilaRendererClearOptions_setClearColor(o, it) }
            FilaRendererClearOptions_setClearStencil(o, value.clearStencil)
            FilaRendererClearOptions_setClear(o, value.clear)
            FilaRendererClearOptions_setDiscard(o, value.discard)
            FilaRenderer_setClearOptions(nativeHandle, o)
        }

    /**
     * Returns up to [historySize] entries of frame timing history, most recent first. The history can be
     * lost when beginFrame() switches SwapChain.
     */
    fun getFrameInfoHistory(historySize: Int = 1): List<FrameInfo> {
        val handles = List(historySize) { FilaRendererFrameInfo_create() }
        try {
            val n = interopScope { FilaRenderer_getFrameInfoHistory(nativeHandle, historySize, toInterop(handles), handles.size) }
            val ns = LongArray(1)
            fun read(get: (NativePointer) -> Unit): Long { ns.usePinned(get); return ns[0] }
            return handles.take(minOf(n, historySize)).map { p ->
                FrameInfo(
                    FilaRendererFrameInfo_getFrameId(p),
                    read { FilaRendererFrameInfo_getGpuFrameDuration(p, it) },
                    read { FilaRendererFrameInfo_getDenoisedGpuFrameDuration(p, it) },
                    read { FilaRendererFrameInfo_getBeginFrame(p, it) },
                    read { FilaRendererFrameInfo_getEndFrame(p, it) },
                    read { FilaRendererFrameInfo_getBackendBeginFrame(p, it) },
                    read { FilaRendererFrameInfo_getBackendEndFrame(p, it) },
                    read { FilaRendererFrameInfo_getGpuFrameComplete(p, it) },
                    read { FilaRendererFrameInfo_getVsync(p, it) },
                    read { FilaRendererFrameInfo_getDisplayPresent(p, it) },
                    read { FilaRendererFrameInfo_getPresentDeadline(p, it) },
                    read { FilaRendererFrameInfo_getDisplayPresentInterval(p, it) },
                    read { FilaRendererFrameInfo_getCompositionToPresentLatency(p, it) },
                    read { FilaRendererFrameInfo_getExpectedPresentLatency(p, it) },
                    read { FilaRendererFrameInfo_getFrameScheduleTime(p, it) },
                )
            }
        } finally {
            handles.forEach { FilaRendererFrameInfo_destroy(it) }
        }
    }

    /** The maximum supported frame history size. */
    val maxFrameHistorySize: Int get() = FilaRenderer_getMaxFrameHistorySize(nativeHandle)

    /**
     * Set the time at which the frame must be presented.
     *
     * This is set as the duration in nanoseconds since epoch of std::chrono::steady_clock.
     * Must be called between beginFrame() and endFrame().
     *
     * @param monotonicClockNanos Time in nanoseconds.
     */
    fun setPresentationTime(monotonicClockNanos: Long) = FilaRenderer_setPresentationTime_int64_t(nativeHandle, monotonicClockNanos)
    /**
     * Set the real desired presentation time targeted for this frame.
     *
     * Unlike setPresentationTime(), which configures hardware headroom, this is the exact target
     * presentation time and is used for frame history reporting.
     * Must be called before endFrame().
     *
     * @param monotonicClockNanos Desired presentation timestamp in steady_clock nanoseconds.
     */
    fun setDesiredPresentationTime(monotonicClockNanos: Long) = FilaRenderer_setDesiredPresentationTime_int64_t(nativeHandle, monotonicClockNanos)
    /**
     * Set the deadline by which CPU and GPU rendering must complete for the buffer to meet its
     * target display latching window.
     * Must be called before endFrame().
     *
     * @param monotonicClockNanos Deadline timestamp in steady_clock nanoseconds.
     */
    fun setRenderingDeadline(monotonicClockNanos: Long) = FilaRenderer_setRenderingDeadline_int64_t(nativeHandle, monotonicClockNanos)
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
    fun skipFrame(vsyncSteadyClockTimeNano: Long = 0) = FilaRenderer_skipFrame(nativeHandle, vsyncSteadyClockTimeNano)
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
    fun beginFrame(swapChain: SwapChain, frameTimeNanos: Long = 0): Boolean = FilaRenderer_beginFrame(nativeHandle, swapChain.nativeHandle, frameTimeNanos)
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
     * @param flags Behavior flags ([COMMIT], [SET_PRESENTATION_TIME], [CLEAR]).
     */
    fun copyFrame(dstSwapChain: SwapChain, dstViewport: Viewport, srcViewport: Viewport, flags: Int) {
        dstViewport.useNative { dst -> srcViewport.useNative { src -> FilaRenderer_copyFrame(nativeHandle, dstSwapChain.nativeHandle, dst, src, flags) } }
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
        FilaRenderer_readPixels_uint32_t_uint32_t_uint32_t_uint32_t_PixelBufferDescriptor(
            nativeHandle,
            xoffset, yoffset, width, height,
            ptr, buffer.sizeInBytes,
            buffer.format.ordinal, buffer.type.ordinal,
            buffer.alignment, buffer.left, buffer.top, buffer.stride,
            Callbacks.keepBuffer, userData,
        )
    }

    /**
     * [readPixels] straight into native memory the caller owns, without a copy (e.g. a Skia bitmap's
     * pixels). [onDone] runs once [address] holds the frame, possibly on Filament's driver thread.
     */
    @InternalFilamentApi
    fun readPixels(xoffset: Int, yoffset: Int, width: Int, height: Int, address: NativePointer, sizeInBytes: Int, format: Texture.Format, type: Texture.Type, stride: Int, onDone: () -> Unit) {
        FilaRenderer_readPixels_uint32_t_uint32_t_uint32_t_uint32_t_PixelBufferDescriptor(
            nativeHandle,
            xoffset, yoffset, width, height,
            address, sizeInBytes,
            format.ordinal, type.ordinal,
            1, 0, 0, stride,
            Callbacks.keepBuffer, Callbacks.register(once = true) { onDone() },
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
        FilaRenderer_readPixels_RenderTarget_uint32_t_uint32_t_uint32_t_uint32_t_PixelBufferDescriptor(
            nativeHandle, renderTarget.nativeHandle,
            xoffset, yoffset, width, height,
            ptr, buffer.sizeInBytes,
            buffer.format.ordinal, buffer.type.ordinal,
            buffer.alignment, buffer.left, buffer.top, buffer.stride,
            Callbacks.keepBuffer, userData,
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
    fun setMaterialTimeEpoch(timeEpochInNs: Long) = FilaRenderer_setMaterialTimeEpoch_int64_t(nativeHandle, timeEpochInNs)
    /**
     * Pause the render thread for a specified duration in nanoseconds.
     *
     * @param timeNs Duration in nanoseconds to pause the render thread.
     */
    fun pauseRenderThread(timeNs: Long) = FilaRenderer_pauseRenderThread(nativeHandle, timeNs)
    /**
     * Whether GPU execution has fallen behind the CPU, to detect latency build-up when driving the
     * presentation loop manually.
     */
    val hasGpuFallenBehind: Boolean get() = FilaRenderer_hasGpuFallenBehind(nativeHandle)
    /**
     * Sets the steady-clock time the frame scheduling callback was entered, so frame pacing can measure
     * the CPU time spent before beginFrame().
     *
     * @param timeSteadyClockNano Steady-clock time in nanoseconds since epoch.
     */
    fun setFrameScheduleTime(timeSteadyClockNano: Long) = FilaRenderer_setFrameScheduleTime_uint64_t(nativeHandle, timeSteadyClockNano)
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
