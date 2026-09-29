package io.github.erkko68.filament.compose.internal

import androidx.compose.runtime.Composable
import io.github.erkko68.filament.Engine

/** Creates and remembers an [Engine] that can present into this platform's [FilamentSurface]. */
@Composable
internal expect fun rememberPlatformEngine(backend: Engine.Backend): Engine
