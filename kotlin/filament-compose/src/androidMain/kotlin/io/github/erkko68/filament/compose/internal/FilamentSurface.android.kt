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

/** `SwapChain::CONFIG_TRANSPARENT` — not exposed as a constant by the bindings yet. */
private const val SWAP_CHAIN_CONFIG_TRANSPARENT = 0x1L

@Composable
internal actual fun FilamentSurface(
    modifier: Modifier,
    engine: Engine,
    renderer: Renderer,
    view: View,
    transparent: Boolean,
    onResize: (aspect: Double) -> Unit,
) {
    val swapChainRef = remember { Ref<SwapChain>() }

    // Keep a mutable ref so callbacks always dispatch to the latest lambda.
    val onResizeRef = remember { Ref<(Double) -> Unit>() }
    SideEffect { onResizeRef.value = onResize }

    fun updateViewport(width: Int, height: Int) {
        if (width <= 0 || height <= 0) return
        view.viewport = Viewport(0, 0, width, height)
        onResizeRef.value?.invoke(width.toDouble() / height.toDouble())
    }

    // factory runs once, so the surface type and its swapchain flags are fixed at creation —
    // key() rebuilds both when transparency is toggled.
    key(transparent) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                if (transparent) {
                    TextureView(context).apply {
                        isOpaque = false
                        surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                            override fun onSurfaceTextureAvailable(
                                surfaceTexture: SurfaceTexture,
                                width: Int,
                                height: Int
                            ) {
                                val surface = Surface(surfaceTexture)
                                swapChainRef.value = engine.createSwapChain(
                                    NativeSurface(surface),
                                    SWAP_CHAIN_CONFIG_TRANSPARENT,
                                )
                                updateViewport(width, height)
                            }

                            override fun onSurfaceTextureSizeChanged(
                                surfaceTexture: SurfaceTexture,
                                width: Int,
                                height: Int
                            ) {
                                updateViewport(width, height)
                            }

                            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                                swapChainRef.value?.let { engine.destroySwapChain(it) }
                                swapChainRef.value = null
                                return true
                            }

                            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
                        }
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
                                swapChainRef.value?.let { engine.destroySwapChain(it) }
                                swapChainRef.value = null
                            }
                        })
                    }
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
    }

    FilamentRenderLoop { frameTime ->
        val sc = swapChainRef.value ?: return@FilamentRenderLoop
        if (renderer.beginFrame(sc, frameTime)) {
            renderer.render(view)
            renderer.endFrame()
        }
    }
}
