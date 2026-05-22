package eric.bitria.samples.scenes

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Back button used by every scene. Sits inside the system safe area (handles iOS notch /
 * Android status bar), uses Material3's tonal button so it's clearly visible on top of any
 * Filament background. Apply [Modifier.align] from the calling Box.
 */
@Composable
fun BackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    FilledTonalButton(
        onClick  = onClick,
        modifier = modifier
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(8.dp),
    ) {
        Text("← Back")
    }
}
