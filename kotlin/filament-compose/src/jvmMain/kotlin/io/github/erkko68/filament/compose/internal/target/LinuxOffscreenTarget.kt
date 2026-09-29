package io.github.erkko68.filament.compose.internal.target

import io.github.erkko68.filament.Engine
import java.awt.Window

/**
 * skiko draws with OpenGL here. Plan: create Filament's OpenGL engine with skiko's GLX context as
 * `sharedContext`, import a shared GL texture, wrap it with `BackendRenderTarget.makeGL` on skiko's
 * [org.jetbrains.skia.DirectContext] (found like the Metal one), and flip rows (GL is bottom-up).
 */
internal fun linuxOffscreenTarget(engine: Engine, window: Window?, width: Int, height: Int): OffscreenTarget =
    TODO("GPU-to-GPU rendering is not implemented on Linux yet")
