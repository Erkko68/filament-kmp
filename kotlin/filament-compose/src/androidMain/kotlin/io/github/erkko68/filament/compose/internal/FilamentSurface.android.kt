package io.github.erkko68.filament.compose.internal

import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.Ref
import androidx.compose.ui.viewinterop.AndroidView
import io.github.erkko68.filament.*

@Composable
internal actual fun FilamentSurface(
    modifier: Modifier,
    engine: Engine,
    renderer: Renderer,
    view: View,
    onResize: (aspect: Double) -> Unit,
) {
    val swapChainRef = remember { Ref<SwapChain>() }

    // Keep a mutable ref so the SurfaceHolder callback always dispatches to the latest lambda.
    val onResizeRef = remember { Ref<(Double) -> Unit>() }
    SideEffect { onResizeRef.value = onResize }

    fun updateViewport(width: Int, height: Int) {
        if (width <= 0 || height <= 0) return
        view.viewport = Viewport(0, 0, width, height)
        onResizeRef.value?.invoke(width.toDouble() / height.toDouble())
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            SurfaceView(context).apply {
                holder.addCallback(object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                        swapChainRef.value = engine.createSwapChain(NativeSurface(holder.surface))
                        updateViewport(width, height)
                    }
                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                        updateViewport(width, height)
                    }
                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                        swapChainRef.value?.let { engine.destroySwapChain(it) }
                        swapChainRef.value = null
                    }
                })
            }
        },
        update = {},
    )

    DisposableEffect(Unit) {
        onDispose {
            swapChainRef.value?.let { engine.destroySwapChain(it) }
            swapChainRef.value = null
        }
    }

    FilamentRenderLoop { frameTime ->
        val sc = swapChainRef.value ?: return@FilamentRenderLoop
        if (renderer.beginFrame(sc, frameTime)) {
            renderer.render(view)
            renderer.endFrame()
        }
    }
}
