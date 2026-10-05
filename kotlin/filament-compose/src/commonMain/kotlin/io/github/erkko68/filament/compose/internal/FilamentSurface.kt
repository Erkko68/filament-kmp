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
 * While [renderingEnabled] is false the surface renders only until the current scene is on screen, then keeps
 * it displayed without rendering (see [PausedFrameGate]).
 */
@Composable
internal expect fun FilamentSurface(
    modifier: Modifier,
    engine: Engine,
    renderer: Renderer,
    view: View,
    transparent: Boolean = false,
    renderingEnabled: Boolean = true,
    onResize: (aspect: Double) -> Unit,
)
