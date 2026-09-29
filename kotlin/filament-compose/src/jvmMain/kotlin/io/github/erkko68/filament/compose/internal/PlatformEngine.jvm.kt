package io.github.erkko68.filament.compose.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.awt.LocalAwtWindow
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.compose.internal.target.d3d.D3DEngines
import io.github.erkko68.filament.compose.internal.target.glx.GlxEngines

/**
 * On Linux the engine shares the window's GL context; on Windows it runs on the window's D3D12 GPU.
 * Without one (e.g. no window) it's a plain engine.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun rememberPlatformEngine(backend: Engine.Backend): Engine {
    val window = LocalAwtWindow.current
    val engine = remember(backend) {
        Filament.init()
        val os = System.getProperty("os.name").orEmpty().lowercase()
        when {
            "linux" in os -> GlxEngines.create(backend, window)
            "win" in os -> D3DEngines.create(backend, window)
            else -> null
        } ?: Engine.create(backend)
    }
    // The Windows engine's platform outlives it; Engine.destroy() is idempotent, so this is safe
    // whichever of this and rememberFilamentEngine's own dispose runs first.
    DisposableEffect(engine) {
        onDispose { D3DEngines.destroy(engine) }
    }
    return engine
}
