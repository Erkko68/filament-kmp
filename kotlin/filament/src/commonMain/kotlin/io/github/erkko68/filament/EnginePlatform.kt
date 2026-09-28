package io.github.erkko68.filament

import io.github.erkko68.filament.interop.NativePointer

// The few things an Engine needs from its platform beyond the Fila* C API: web binds each engine to a
// WebGL context on a canvas, Android turns a Surface into an ANativeWindow.

/** Per-engine platform state; inert everywhere but web. */
internal interface EnginePlatform {
    /** Makes this engine's graphics context current before driving it. */
    fun makeCurrent() {}

    /** Releases what [enginePlatform] set up, once the engine is destroyed. */
    fun release() {}
}

internal object NoEnginePlatform : EnginePlatform

/** Called right before the engine is built, with the builder's backend and shared context. */
internal expect fun enginePlatform(backend: Engine.Backend, sharedContext: Any?): EnginePlatform

/** FilaEngineBuilder_sharedContext's argument for [sharedContext], or NullPointer to pass none. */
internal expect fun sharedContextPointer(sharedContext: Any): NativePointer

/** The native window a swapchain over [surface] renders into; hand it back to [releaseWindow]. */
internal expect fun acquireWindow(surface: NativeSurface): NativePointer

internal expect fun releaseWindow(window: NativePointer)
