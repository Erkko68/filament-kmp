package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
import io.github.erkko68.filament.interop.*

/**
 * Stream is used to attach a video stream to a Filament Texture.
 *
 * The Stream class is fairly platform-centric. It supports two different configurations:
 *
 * - **NATIVE**: Connects to a native OS stream (e.g., Android SurfaceTexture)
 * - **ACQUIRED**: Connects to an external source (e.g., Android AHardwareBuffer)
 *
 * Before explaining these different configurations, let's review the high-level structure of an AR
 * or video application that uses Filament:
 *
 * ```
 * while (true) {
 *     // Application work: write frame data, move camera, etc.
 *
 *     if (renderer.beginFrame(swapChain)) {
 *         renderer.render(view)
 *         renderer.endFrame()
 *     }
 * }
 * ```
 *
 * Let's say that the video image data at the time of a particular invocation of `beginFrame()`
 * becomes visible to users at time A. The 3D scene state (including camera) at that same
 * invocation becomes apparent to users at time B.
 *
 * - If time A matches time B, the stream is **synchronized**.
 * - Filament invokes low-level graphics commands on the **driver thread**.
 * - The thread that calls `beginFrame()` is called the **main thread**.
 *
 * For ACQUIRED streams, Filament explicitly acquires the stream, then releases it later via a
 * callback function. This configuration is especially useful when the Vulkan backend is enabled.
 *
 * NATIVE streams are deprecated because they are backend-specific and do not make any
 * synchronization guarantee.
 *
 * @see Texture
 * @see TextureSampler
 */
@PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "setDimensions throws — FStream waits on a fence internally, which single-threaded wasm rejects; external video streams have no WebGL source anyway.")
class Stream @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * Indicates the type of stream source.
     */
    enum class StreamType {
        /** Native OS stream (e.g., Android SurfaceTexture) */
        NATIVE,
        /** Stream acquired from external source (e.g., AHardwareBuffer) */
        ACQUIRED
    }

    /**
     * Builder for creating Stream instances.
     *
     * By default, Stream objects are ACQUIRED and must have external images pushed to them.
     * To create a NATIVE stream, call the deprecated stream() method on the builder.
     */
    class Builder() {
        private val nativeBuilder = FilaStreamBuilder_create()

        /**
         * Sets the initial width of the incoming stream in pixels.
         *
         * Whether this value is used is stream-dependent. On Android, it must be set when using
         * a native stream.
         *
         * @param width Stream width in pixels.
         * @return This Builder, for chaining calls.
         */
        fun width(width: Int): Builder {
            FilaStreamBuilder_width(nativeBuilder, width)
            return this
        }

        /**
         * Sets the initial height of the incoming stream in pixels.
         *
         * Whether this value is used is stream-dependent. On Android, it must be set when using
         * a native stream.
         *
         * @param height Stream height in pixels.
         * @return This Builder, for chaining calls.
         */
        fun height(height: Int): Builder {
            FilaStreamBuilder_height(nativeBuilder, height)
            return this
        }

        /**
         * Associates an optional name with this Stream for debugging purposes.
         *
         * The name will show in error messages and should be kept as short as possible.
         *
         * @param name A string to identify this Stream.
         * @return This Builder, for chaining calls.
         */
        fun name(name: String): Builder = apply { interopScope { FilaStreamBuilder_name(nativeBuilder, toInterop(name)) } }

        /**
         * Creates the Stream object and associates it with the given Engine.
         *
         * @param engine Engine to associate this Stream with.
         * @return The newly created Stream.
         * @throws UnsupportedOperationException on JS — Stream is unbound in the web wrapper.
         */
        fun build(engine: Engine): Stream {
            val handle = FilaStreamBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaStreamBuilder_destroy(nativeBuilder)
            return Stream(handle)
        }
    }

    /**
     * Indicates whether this stream is a NATIVE stream or ACQUIRED stream.
     *
     * @return The StreamType of this stream.
     */
    val streamType: StreamType get() = StreamType.entries[FilaStream_getStreamType(nativeHandle)]

    /**
     * Updates an ACQUIRED stream with an image that is guaranteed to be used in the next frame.
     *
     * Filament immediately "acquires" the image and calls [release] when it is done with it. Call
     * this outside of beginFrame / endFrame, once per frame: if several images are pushed in one
     * frame, only the last is used, but every callback fires.
     *
     * Call it on the thread that calls Renderer.beginFrame, which is also where [release] runs.
     *
     * @param image Platform image (e.g. an AHardwareBuffer on Android).
     * @param transform Transform matrix applied to the image, as 9 floats (column-major 3x3); identity by default.
     * @param release Called with [image] when Filament releases it.
     */
    fun setAcquiredImage(image: NativePointer, transform: FloatArray = IDENTITY, release: (image: NativePointer) -> Unit) {
        val userData = Callbacks.register(once = true, release)
        transform.usePinned { FilaStream_setAcquiredImage_Callback_void_mat3f(nativeHandle, image, Callbacks.argUser, userData, it) }
    }

    /**
     * Updates the size of the incoming stream.
     *
     * Whether this value is used is stream-dependent. On Android, it must be set when using
     * a native stream. The dimensions should match the associated Texture's dimensions.
     *
     * @param width New width in pixels.
     * @param height New height in pixels.
     */
    fun setDimensions(width: Int, height: Int) {
        FilaStream_setDimensions(nativeHandle, width, height)
    }

    /**
     * Returns the presentation timestamp of the currently displayed frame in nanoseconds.
     *
     * This value can change at any time and represents the time when the current frame
     * was acquired or presented.
     *
     * @return Timestamp in nanoseconds (monotonically increasing).
     */
    val timestamp: Long get() = LongArray(1).also { t -> t.usePinned { FilaStream_getTimestamp(nativeHandle, it) } }[0]

    private companion object {
        val IDENTITY = floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
    }
}
