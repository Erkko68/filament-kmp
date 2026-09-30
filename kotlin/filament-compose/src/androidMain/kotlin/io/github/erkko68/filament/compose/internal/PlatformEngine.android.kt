package io.github.erkko68.filament.compose.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament

@Composable
internal actual fun rememberPlatformEngine(backend: Engine.Backend): Engine =
    remember(backend) { Filament.init(); Owned(checkNotNull(Engine.create(backend)) { "Failed to create a $backend Engine" }, emptyList()) { Engine.destroy(it) } }.value
