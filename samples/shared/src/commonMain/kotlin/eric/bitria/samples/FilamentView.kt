package eric.bitria.samples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import kotlinx.coroutines.isActive

@Composable
expect fun FilamentView(
    modifier: Modifier = Modifier,
    controller: FilamentController
)

@Composable
internal fun FilamentRenderLoop(onFrame: (Long) -> Unit) {
    LaunchedEffect(Unit) {
        while (isActive) {
            withFrameNanos { frameTimeNanos ->
                onFrame(frameTimeNanos)
            }
        }
    }
}

