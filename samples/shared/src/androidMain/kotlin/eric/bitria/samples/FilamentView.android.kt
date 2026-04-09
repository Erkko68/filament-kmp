package eric.bitria.samples

import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import dev.filament.kmp.NativeSurface
import kotlinx.coroutines.isActive

@Composable
actual fun FilamentView(
    modifier: Modifier,
    renderer: FilamentRenderer
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            SurfaceView(context).apply {
                holder.addCallback(object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                        renderer.onSurfaceAvailable(
                            NativeSurface(holder.surface),
                            width,
                            height
                        )
                    }

                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                        renderer.onSurfaceResized(width, height)
                    }

                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                        // SwapChain cleanup is handled by engine destroy or new surface
                    }
                })
            }
        },
        update = {}
    )

    LaunchedEffect(renderer) {
        while (isActive) {
            withFrameNanos { frameTimeNanos ->
                renderer.render(frameTimeNanos)
            }
        }
    }
}
