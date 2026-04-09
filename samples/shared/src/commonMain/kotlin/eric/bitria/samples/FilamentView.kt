package eric.bitria.samples

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun FilamentView(
    modifier: Modifier = Modifier,
    renderer: FilamentRenderer
)
