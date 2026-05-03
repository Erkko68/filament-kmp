package io.github.erkko68.filament.compose.internal

import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.NativeSurface
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.SwapChain
import io.github.erkko68.filament.View
import io.github.erkko68.filament.Viewport

@Composable
internal actual fun FilamentSurface(
    modifier: Modifier,
    engine: Engine,
    renderer: Renderer,
    view: View,
    onResize: (aspect: Double) -> Unit,
) {
    var swapChain: SwapChain? = null

    // Keep a mutable ref so the SurfaceHolder callback always dispatches to the latest lambda.
    val onResizeRef = remember { Array<(Double) -> Unit>(1) { onResize } }
    SideEffect { onResizeRef[0] = onResize }

    fun updateViewport(width: Int, height: Int) {
        if (width <= 0 || height <= 0) return
        view.viewport = Viewport(0, 0, width, height)
        onResizeRef[0](width.toDouble() / height.toDouble())
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            SurfaceView(context).apply {
                holder.addCallback(object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                        swapChain = engine.createSwapChain(NativeSurface(holder.surface))
                        updateViewport(width, height)
                    }
                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                        updateViewport(width, height)
                    }
                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                        swapChain?.let { engine.destroySwapChain(it) }
                        swapChain = null
                    }
                })
            }
        },
        update = {},
    )

    DisposableEffect(Unit) {
        onDispose {
            swapChain?.let { engine.destroySwapChain(it) }
            swapChain = null
        }
    }

    FilamentRenderLoop { frameTime ->
        val sc = swapChain ?: return@FilamentRenderLoop
        if (renderer.beginFrame(sc, frameTime)) {
            renderer.render(view)
            renderer.endFrame()
        }
    }
}
