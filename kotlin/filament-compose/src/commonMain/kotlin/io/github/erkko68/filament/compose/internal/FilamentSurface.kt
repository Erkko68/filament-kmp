package io.github.erkko68.filament.compose.internal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.View

/**
 * Platform-specific rendering surface.
 * Manages SwapChain lifecycle, viewport updates, and the render loop.
 * Calls [onResize] with the new aspect ratio whenever the drawable size changes.
 */
@Composable
internal expect fun FilamentSurface(
    modifier: Modifier,
    engine: Engine,
    renderer: Renderer,
    view: View,
    onResize: (aspect: Double) -> Unit,
)
