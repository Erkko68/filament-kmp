package io.github.erkko68.filament

/**
 * Renderer represents an OS window and manages frame rendering and pacing.
 *
 * Typically, create one Renderer per window. The Renderer generates drawing commands
 * from Views (which contain Scenes) and manages frame latency to keep latency low.
 *
 * **Typical render loop:**
 * ```
 * while (!quit) {
 *   if (renderer.beginFrame(swapChain)) {
 *     renderer.render(view)
 *     renderer.endFrame()
 *   }
 * }
 * ```
 *
 * **Frame pacing:** beginFrame() manages frame pacing and returns false if the GPU is
 * falling behind (to skip frames and reduce latency). When false is returned, either
 * skip the frame or proceed anyway; if proceeding, you must still call endFrame().
 */
expect class Renderer {
    /**
     * Display refresh-rate information for frame pacing and dynamic resolution scaling.
     */
    class DisplayInfo() {
        /** Refresh rate in Hz. Set to 0 for offscreen rendering or to disable frame pacing. Default: 60. */
        var refreshRate: Float
    }

    /**
     * Frame rate control and dynamic resolution scaling options.
     *
     * interval: desired frame interval as a multiple of the refresh period (1/refreshRate).
     * headRoomRatio: GPU headroom ratio for dynamic resolution (accounts for post-processing, drivers).
     * scaleRate: rate at which GPU load adjusts to reach target frame rate; higher = faster reaction.
     * history: history size for filtering GPU load (max 31).
     */
    class FrameRateOptions() {
        /** Desired frame interval (1 = render every vsync). */
        var interval: Float
        /** Additional GPU headroom as fraction of target frame time. */
        var headRoomRatio: Float
        /** Rate at which GPU load is adjusted; computed as 1/N frames to reach 64% target. */
        var scaleRate: Float
        /** History size for load filtering (clamped to 31). */
        var history: Int
    }

    /**
     * Render target clear and discard behavior for SwapChain at frame start.
     *
     * clear: whether to clear the SwapChain using clearColor.
     * discard: whether to discard the SwapChain content (clear implies discard).
     * clearColor: RGBA clear color as [0,1] or backend-appropriate range.
     */
    class ClearOptions() {
        /** RGBA clear color (tone-mapping applies to opaque Views). */
        var clearColor: DoubleArray
        /** Clear the SwapChain with clearColor. Default: false. */
        var clear: Boolean
        /** Discard SwapChain content (set false to preserve existing content). Default: true. */
        var discard: Boolean
    }

    companion object {
        /** Commit the dstSwapChain after copyFrame. */
        val MIRROR_FRAME_FLAG_COMMIT: Int
        /** Set presentation time on dstSwapChain in copyFrame. */
        val MIRROR_FRAME_FLAG_SET_PRESENTATION_TIME: Int
        /** Clear dstSwapChain to black before copyFrame. */
        val MIRROR_FRAME_FLAG_CLEAR: Int
    }

    /** Get the Engine that created this Renderer. */
    val engine: Engine
    /** Get/set display information for frame pacing and dynamic resolution. */
    var displayInfo: DisplayInfo
    /** Get/set frame rate control and dynamic resolution options. */
    var frameRateOptions: FrameRateOptions
    /** Get/set clear behavior for the SwapChain. */
    var clearOptions: ClearOptions

    /** Set the time at which the frame must be presented. Must be between beginFrame() and endFrame(). */
    fun setPresentationTime(monotonicClockNanos: Long)

    /** Set the VSYNC time (steady clock). Used by beginFrame if frameTimeNanos=0. */
    fun setVsyncTime(steadyClockTimeNano: Long)

    /** Skip the current frame for frame pacing (e.g., if scene doesn't change). */
    fun skipFrame(vsyncSteadyClockTimeNano: Long)

    /** Check if the current frame should be rendered (same as beginFrame return value). */
    fun shouldRenderFrame(): Boolean

    /**
     * Prepare a frame for rendering.
     *
     * Manages frame pacing and returns whether the frame should be rendered:
     * - true: must render and call endFrame()
     * - false: can skip the frame (don't call endFrame), or proceed anyway
     *
     * @param swapChain SwapChain to render into
     * @param frameTimeNanos VSYNC time in steady_clock ns, or 0 to use setVsyncTime
     * @return true if frame should be rendered, false if behind and frame should be skipped
     */
    fun beginFrame(swapChain: SwapChain, frameTimeNanos: Long): Boolean

    /** Finish the current frame and schedule it for display. Must be called after beginFrame(true). */
    fun endFrame()

    /**
     * Render a View into this Renderer's target.
     *
     * Performs shadow passes, depth pre-pass, color pass, and post-processing.
     * Must be called between beginFrame(true) and endFrame().
     * Can be called multiple times per frame, but typically once.
     *
     * @param view View to render
     */
    fun render(view: View)

    /** Render a View without frame pacing (for off-screen rendering). */
    fun renderStandaloneView(view: View)

    /**
     * Copy the rendered frame to another SwapChain at specified viewport.
     *
     * Call after render() but before endFrame().
     *
     * @param dstSwapChain Destination SwapChain
     * @param dstViewport Destination rectangle
     * @param srcViewport Source rectangle
     * @param flags Behavior flags (COMMIT, SET_PRESENTATION_TIME, CLEAR)
     */
    fun copyFrame(dstSwapChain: SwapChain, dstViewport: Viewport, srcViewport: Viewport, flags: Int)

    /**
     * Read back SwapChain pixels asynchronously.
     *
     * The buffer's callback is invoked on the main thread when complete (usually after
     * several endFrame() calls). Formats RGBA/RGBA_INTEGER and types UBYTE/UINT/INT/FLOAT
     * are always supported.
     *
     * Call between beginFrame() and endFrame(), typically after render().
     * Impacts performance significantly.
     *
     * @param xoffset Left offset in pixels
     * @param yoffset Bottom offset in pixels
     * @param width Width in pixels
     * @param height Height in pixels
     * @param buffer Pixel buffer for result
     */
    fun readPixels(xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Texture.PixelBufferDescriptor)

    /**
     * Read back RenderTarget pixels asynchronously.
     * @param renderTarget RenderTarget to read from
     * @param xoffset Left offset in pixels
     * @param yoffset Bottom offset in pixels
     * @param width Width in pixels
     * @param height Height in pixels
     * @param buffer Pixel buffer for result
     */
    fun readPixels(renderTarget: RenderTarget, xoffset: Int, yoffset: Int, width: Int, height: Int, buffer: Texture.PixelBufferDescriptor)

    /** Get elapsed time since resetUserTime() in seconds. */
    val userTime: Double
    /** Reset the user time clock. */
    fun resetUserTime()
    /** Skip the next N frames for frame pacing. */
    fun skipNextFrames(frameCount: Int)
    /** Get number of frames left to skip. */
    val frameToSkipCount: Int
}
