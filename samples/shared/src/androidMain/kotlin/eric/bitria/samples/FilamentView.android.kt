package eric.bitria.samples

import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.github.erkko68.filament.NativeSurface


@Composable
actual fun FilamentView(
    modifier: Modifier,
    controller: FilamentController
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            controller.initialize()
            SurfaceView(context).apply {
                holder.addCallback(object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                        controller.setSurface(
                            NativeSurface(holder.surface),
                            width,
                            height
                        )
                    }

                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                        controller.updateViewport(width, height)
                    }

                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                        // Cleanup handled by controller
                    }
                })
            }
        },
        update = {}
    )

    FilamentRenderLoop { frameTime ->
        controller.render(frameTime)
    }
}
