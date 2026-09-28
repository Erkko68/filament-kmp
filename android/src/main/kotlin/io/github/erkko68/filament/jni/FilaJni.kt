package io.github.erkko68.filament.jni

import java.nio.ByteBuffer

/**
 * Hand-written half of the JNI layer (native side: src/main/cpp/FilaJni.cpp). [FilamentJni] holds the
 * generated Fila* functions; this object loads libfilament-c and covers what they can't express.
 */
object FilaJni {
    init { System.loadLibrary("filament-c") }

    /** Forces the library load; [FilamentJni] calls it from its initializer. */
    fun load() {}

    @JvmStatic external fun malloc(size: Long): Long
    @JvmStatic external fun free(ptr: Long)

    /** Native address of a direct [buffer], to pass where the C API takes a pointer. */
    @JvmStatic external fun address(buffer: ByteBuffer): Long

    /** A direct ByteBuffer over [size] bytes of native memory at [ptr] (native byte order is up to the caller). */
    @JvmStatic external fun view(ptr: Long, size: Long): ByteBuffer

    /** `ANativeWindow*` for an `android.view.Surface`, for FilaEngine_createSwapChain; release with [releaseWindow]. */
    @JvmStatic external fun windowFromSurface(surface: Any): Long
    @JvmStatic external fun releaseWindow(window: Long)

    /** userData for a one-shot C callback: [runnable] runs once on the calling native thread, then is released. */
    @JvmStatic external fun newCallback(runnable: Runnable): Long

    /** `FilaBufferCallback` that runs the [newCallback] userData. */
    @JvmStatic external fun bufferCallback(): Long

    /** `void (*)(void* userData)` callback (engine compile, frame scheduled) that runs the [newCallback] userData. */
    @JvmStatic external fun userDataCallback(): Long
}
