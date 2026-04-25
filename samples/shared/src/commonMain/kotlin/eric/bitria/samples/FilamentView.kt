package eric.bitria.samples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.isActive

@Composable
expect fun FilamentView(
    modifier: Modifier = Modifier,
    controller: FilamentController
)

expect fun getPlatformBackend(): io.github.erkko68.filament.Engine.Backend

@Composable
internal fun FilamentRenderLoop(onFrame: (Long) -> Unit) {
    val currentOnFrame by rememberUpdatedState(onFrame)
    LaunchedEffect(Unit) {
        while (isActive) {
            withFrameNanos { frameTimeNanos ->
                currentOnFrame(frameTimeNanos)
            }
        }
    }
}

