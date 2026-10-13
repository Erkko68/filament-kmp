package io.github.erkko68.filament

import io.github.erkko68.filament.jni.AwtHelper
import java.awt.Canvas
import javax.swing.SwingUtilities

/**
 * JVM implementation of NativeSurface: a raw native window/layer pointer (HWND, X11 Window, NSView*,
 * CAMetalLayer*) as a Long or Int address, handed to `FilaEngine_createSwapChain` as a `void*`.
 */
actual class NativeSurface(val nativeWindow: Any) {
    /**
     * The surface of an AWT [canvas], to render straight into an AWT or Swing window (or a Compose
     * `SwingPanel`). The canvas must be displayable, and its swap chain destroyed before it is removed.
     * The viewport is in pixels: the canvas size times its `graphicsConfiguration.defaultTransform` scale.
     * On macOS only the Metal backend can render into it.
     */
    constructor(canvas: Canvas) : this(awtWindow(canvas))

    internal val handle: Long
        get() = when (nativeWindow) {
            is Long -> nativeWindow
            is Int -> nativeWindow.toLong()
            else -> 0L
        }
}

private fun awtWindow(canvas: Canvas): Any {
    check(canvas.isDisplayable) { "The Canvas has no native window yet: add it to a visible window first" }
    Filament.init()
    // Where the canvas sits in its window's content: macOS places its layer by it.
    val window = checkNotNull(SwingUtilities.getWindowAncestor(canvas))
    val origin = SwingUtilities.convertPoint(canvas, 0, 0, window)
    val insets = window.insets
    return AwtHelper.nNativeWindow(canvas, origin.x - insets.left, origin.y - insets.top).also { check(it != 0L) { "AWT gave no native window for $canvas" } }
}
