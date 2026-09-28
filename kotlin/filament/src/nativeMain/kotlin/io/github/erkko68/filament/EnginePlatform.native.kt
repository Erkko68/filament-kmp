@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.github.erkko68.filament

import io.github.erkko68.filament.interop.NativePointer
import kotlinx.cinterop.toLong

internal actual fun enginePlatform(backend: Engine.Backend, sharedContext: Any?): EnginePlatform = NoEnginePlatform

// Metal has no shared context to hand over.
internal actual fun sharedContextPointer(sharedContext: Any): NativePointer = 0L

internal actual fun acquireWindow(surface: NativeSurface): NativePointer = surface.handler.toLong()

internal actual fun releaseWindow(window: NativePointer) {}
