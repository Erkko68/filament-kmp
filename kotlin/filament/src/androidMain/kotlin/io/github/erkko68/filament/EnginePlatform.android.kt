package io.github.erkko68.filament

import android.opengl.EGLContext
import android.view.Surface
import io.github.erkko68.filament.interop.NativePointer
import io.github.erkko68.filament.jni.FilaAndroid

internal actual fun enginePlatform(backend: Engine.Backend, sharedContext: Any?): EnginePlatform = NoEnginePlatform

/** An [EGLContext] or its native handle as a Long, as in filament-android. */
internal actual fun sharedContextPointer(sharedContext: Any): NativePointer = when (sharedContext) {
    is EGLContext -> sharedContext.nativeHandle
    is Long -> sharedContext
    else -> throw IllegalArgumentException("sharedContext must be an EGLContext, got ${sharedContext::class}")
}

// Holds an ANativeWindow reference until the swapchain is destroyed; EGL takes its own when it builds the surface.
internal actual fun acquireWindow(surface: NativeSurface): NativePointer {
    val androidSurface = requireNotNull(surface.surface as? Surface) {
        "NativeSurface must wrap an android.view.Surface, got ${surface.surface::class}"
    }
    val window = FilaAndroid.windowFromSurface(androidSurface)
    check(window != 0L) { "No ANativeWindow for $androidSurface (released?)" }
    return window
}

internal actual fun releaseWindow(window: NativePointer) {
    if (window != 0L) FilaAndroid.releaseWindow(window)
}
