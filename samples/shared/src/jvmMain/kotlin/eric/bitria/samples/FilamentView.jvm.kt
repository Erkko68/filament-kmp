package eric.bitria.samples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel

@Composable
actual fun FilamentView(
    modifier: Modifier,
    renderer: FilamentViewRenderer
) {
    // We reuse our FilamentPanel but host it in Compose using SwingPanel
    SwingPanel(
        modifier = modifier,
        factory = {
            FilamentPanel(renderer).apply {
                // Initialize the native surface logic (without the timer)
                setupNativeSurface()
            }
        },
        update = {
            // No-op
        }
    )

    // Use the common render loop
    FilamentRenderLoop(renderer)
}
