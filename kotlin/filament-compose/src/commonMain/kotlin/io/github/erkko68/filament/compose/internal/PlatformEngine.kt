package io.github.erkko68.filament.compose.internal

import androidx.compose.runtime.Composable
import io.github.erkko68.filament.Engine

/**
 * Creates and remembers an [Engine] that can present into this platform's [FilamentSurface], [Owned] so it's
 * destroyed only after everything created from it.
 */
@Composable
internal expect fun rememberPlatformEngine(backend: Engine.Backend): Engine
