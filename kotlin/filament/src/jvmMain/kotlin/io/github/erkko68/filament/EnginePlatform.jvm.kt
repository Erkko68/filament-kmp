package io.github.erkko68.filament

import io.github.erkko68.filament.interop.NativePointer

internal actual fun enginePlatform(backend: Engine.Backend, sharedContext: Any?): EnginePlatform = NoEnginePlatform

/** Only a native context handle (Long) means something on desktop; anything else is ignored. */
internal actual fun sharedContextPointer(sharedContext: Any): NativePointer = sharedContext as? Long ?: 0L

internal actual fun acquireWindow(surface: NativeSurface): NativePointer = surface.handle

internal actual fun releaseWindow(window: NativePointer) {}
