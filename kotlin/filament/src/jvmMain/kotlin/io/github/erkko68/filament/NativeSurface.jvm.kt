package io.github.erkko68.filament

/**
 * JVM implementation of NativeSurface: a raw native window/layer pointer (HWND, X11 Window, NSView*,
 * CAMetalLayer*) as a Long or Int address, handed to `FilaEngine_createSwapChain` as a `void*`.
 */
actual class NativeSurface(val nativeWindow: Any) {
    internal val handle: Long
        get() = when (nativeWindow) {
            is Long -> nativeWindow
            is Int -> nativeWindow.toLong()
            else -> 0L
        }
}
