package io.github.erkko68.filament.compose

/**
 * Marks Compose Desktop's experimental GPU-to-GPU frame sharing ([FilamentComposeDesktop.isGpuToGpuFrameSharingEnabled]).
 *
 * It reaches into skiko's internals to hand Filament's frames to Compose's own GPU context, so a
 * skiko update, GPU or driver it wasn't tested on can break it. Failures fall back to CPU readback
 * and print a report to the console.
 */
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "GPU-to-GPU frame sharing with Compose Desktop is experimental: it relies on skiko internals " +
        "and may fall back to CPU readback on untested GPUs, drivers or skiko versions.",
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class ExperimentalGpuToGpuFrameSharing

/** Compose Desktop (JVM) rendering options for filament-compose. */
object FilamentComposeDesktop {
    /**
     * Renders Filament's frames into textures shared with Compose's own GPU context (Metal on macOS,
     * Direct3D 12 on Windows, OpenGL on Linux) instead of copying each frame through the CPU.
     *
     * Off by default: frames then go through CPU readback, which works everywhere but costs a
     * GPU→CPU copy per frame. Set it before the first `rememberFilamentEngine()`, e.g. in `main()`:
     * only engines that `rememberFilamentEngine()` creates afterwards, inside the window that shows
     * them, share frames GPU-to-GPU; others keep CPU readback. If the GPU path fails, filament-compose
     * prints a report to the console and switches to CPU readback for the rest of the session.
     */
    @ExperimentalGpuToGpuFrameSharing
    @Volatile
    @JvmStatic
    var isGpuToGpuFrameSharingEnabled: Boolean = false
}
