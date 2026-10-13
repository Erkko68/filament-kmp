package io.github.erkko68.filament.jni

/** Desktop-only: the native window behind an AWT component. */
object AwtHelper {
    // The native side finds JAWT_GetAWT in the already-loaded libjawt instead of linking it.
    init { System.loadLibrary("jawt") }

    /**
     * The native window of the displayable `java.awt.Canvas` [canvas]: an `HWND`, an X11 `Window`, or on
     * macOS a `CAMetalLayer` attached to it at ([x], [y]) in its window's content; 0 on failure.
     */
    @JvmStatic external fun nNativeWindow(canvas: Any, x: Int, y: Int): Long
}
