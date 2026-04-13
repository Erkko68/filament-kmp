package eric.bitria.samples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import io.github.erkko68.filament.NativeSurface
import kotlinx.coroutines.isActive

interface FilamentViewRenderer {
    fun onSurfaceAvailable(surface: NativeSurface, width: Int, height: Int)
    fun onSurfaceResized(width: Int, height: Int)
    fun onSurfaceDetached()
    fun render(frameTimeNanos: Long)
}

@Composable
expect fun FilamentView(
    modifier: Modifier = Modifier,
    renderer: FilamentViewRenderer
)

@Composable
internal fun FilamentRenderLoop(renderer: FilamentViewRenderer) {
    LaunchedEffect(renderer) {
        while (isActive) {
            withFrameNanos { frameTimeNanos ->
                renderer.render(frameTimeNanos)
            }
        }
    }
}

