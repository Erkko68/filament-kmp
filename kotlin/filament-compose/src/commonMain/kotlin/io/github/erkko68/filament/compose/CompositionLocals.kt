package io.github.erkko68.filament.compose

import androidx.compose.runtime.compositionLocalOf
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Scene

val LocalFilamentEngine = compositionLocalOf<Engine> {
    error("No FilamentEngine in scope — declare scene content inside rememberFilamentScene { }")
}

val LocalFilamentScene = compositionLocalOf<Scene> {
    error("No FilamentScene in scope — declare scene content inside rememberFilamentScene { }")
}
