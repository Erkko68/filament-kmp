package io.github.erkko68.filament.compose.internal

import android.graphics.SurfaceTexture
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
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
    transparent: Boolean,
    renderingEnabled: Boolean,
    onResize: (aspect: Double) -> Unit,
) {
    val swapChainRef = remember { Ref<SwapChain>() }

    // Keep a mutable ref so callbacks always dispatch to the latest lambda.
    val onResizeRef = remember { Ref<(Double) -> Unit>() }
    SideEffect { onResizeRef.value = onResize }

    val gate = rememberPausedFrameGate(renderingEnabled)

    fun updateViewport(width: Int, height: Int) {
        if (width <= 0 || height <= 0) return
        gate.reset() // a new or resized surface has shown nothing yet
        view.viewport = Viewport(0, 0, width, height)
        onResizeRef.value?.invoke(width.toDouble() / height.toDouble())
    }

    fun destroySwapChain() {
        val swapChain = swapChainRef.value ?: return
        swapChainRef.value = null
        engine.destroy(swapChain)
        // Android frees the Surface as soon as its callback returns: the backend must be done with it by then.
        engine.flushAndWait()
    }

    // factory runs once, so the surface type, its swapchain flags, and the engine and view it captures are
    // fixed at creation — key() rebuilds it when any of them changes.
    key(engine, view, transparent) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                if (transparent) {
                    TextureView(context).apply {
                        isOpaque = false
                        surfaceTextureListener = filamentSurfaceTextureListener(
                            onAvailable = { surface, width, height ->
                                swapChainRef.value = engine.createSwapChain(
                                    NativeSurface(surface),
                                    SwapChain.CONFIG_TRANSPARENT,
                                )
                                updateViewport(width, height)
                            },
                            onResized = ::updateViewport,
                            onDestroyed = {
                                destroySwapChain()
                            },
                        )
                    }
                } else {
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
                                destroySwapChain()
                            }
                        })
                    }
                }
            },
            update = {},
        )

        DisposableEffect(Unit) {
            onDispose {
                destroySwapChain()
            }
        }
    }

    FilamentRenderLoop(gate.loopEnabled(renderingEnabled)) { frameTime ->
        val sc = swapChainRef.value ?: return@FilamentRenderLoop
        if (renderer.beginFrame(sc, frameTime)) {
            renderer.render(view)
            renderer.endFrame()
            gate.delivered(paused = !renderingEnabled)
        }
    }
}

/**
 * TextureView listener that owns the [Surface] it wraps around the [SurfaceTexture]: [onDestroyed]
 * runs first (the swapchain still needs the surface), then the surface is released instead of being
 * left to the finalizer.
 */
internal fun filamentSurfaceTextureListener(
    onAvailable: (Surface, width: Int, height: Int) -> Unit,
    onResized: (width: Int, height: Int) -> Unit,
    onDestroyed: () -> Unit,
): TextureView.SurfaceTextureListener = object : TextureView.SurfaceTextureListener {
    private var surface: Surface? = null

    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        val s = Surface(surfaceTexture).also { surface = it }
        onAvailable(s, width, height)
    }

    override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        onResized(width, height)
    }

    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
        onDestroyed()
        surface?.release()
        surface = null
        return true
    }

    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
}
