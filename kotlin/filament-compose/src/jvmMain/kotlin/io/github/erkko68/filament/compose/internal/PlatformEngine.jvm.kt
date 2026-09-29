package io.github.erkko68.filament.compose.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.awt.LocalAwtWindow
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.compose.internal.target.glx.GlxEngines

/** On Linux the engine shares the window's GL context; without one (e.g. no window) it's a plain engine. */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun rememberPlatformEngine(backend: Engine.Backend): Engine {
    val window = LocalAwtWindow.current
    return remember(backend) {
        Filament.init()
        val isLinux = "linux" in System.getProperty("os.name").orEmpty().lowercase()
        (if (isLinux) GlxEngines.create(backend, window) else null) ?: Engine.create(backend)
    }
}
