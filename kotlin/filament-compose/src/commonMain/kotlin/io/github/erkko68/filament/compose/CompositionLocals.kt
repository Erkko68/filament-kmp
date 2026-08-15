package io.github.erkko68.filament.compose

import androidx.compose.runtime.compositionLocalOf
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Scene

// Null (not error()) defaults so resource loaders can take `engine = LocalFilamentEngine.current`
// as a default argument and report the failure themselves, at the call site.
val LocalFilamentEngine = compositionLocalOf<Engine?> { null }

val LocalFilamentScene = compositionLocalOf<Scene?> { null }

internal fun noFilamentEngine(): Nothing =
    error("No FilamentEngine in scope — declare scene content inside rememberFilamentScene { }, or pass engine = …")

internal fun noFilamentScene(): Nothing =
    error("No FilamentScene in scope — declare scene content inside rememberFilamentScene { }")
