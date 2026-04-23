package eric.bitria.samples

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// On JS, Filament renders directly to the HTML <canvas> element in the DOM.
// The Compose FilamentView is a transparent spacer — rendering happens via
// the controller's render() call in FilamentRenderLoop, which runs through
// requestAnimationFrame. The canvas is positioned behind the Compose UI layer.
@Composable
actual fun FilamentView(modifier: Modifier, controller: FilamentController) {
    Spacer(modifier = modifier)
}
