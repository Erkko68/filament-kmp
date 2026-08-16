@file:OptIn(ExperimentalForeignApi::class, ExperimentalComposeUiApi::class)

package io.github.erkko68.filament.compose.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.Ref
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.NativeSurface
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.SwapChain
import io.github.erkko68.filament.View
import io.github.erkko68.filament.Viewport
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.interpretCPointer
import kotlinx.cinterop.objcPtr
import kotlinx.cinterop.useContents
import objcnames.protocols.MTLDeviceProtocol
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Metal.MTLCreateSystemDefaultDevice
import platform.Metal.MTLPixelFormatBGRA8Unorm
import platform.QuartzCore.CAMetalLayer
import platform.UIKit.UIColor
import platform.UIKit.UIScreen
import platform.UIKit.UIView

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

    // Keep a mutable ref so layoutSubviews always dispatches to the latest lambda.
    val onResizeRef = remember { Ref<(Double) -> Unit>() }
    SideEffect { onResizeRef.value = onResize }

    // factory runs once, so layer opacity and swapchain flags are fixed at creation —
    // key() rebuilds both when transparency is toggled.
    key(transparent) {
        UIKitView(
            factory = {
                object : UIView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)) {
                    private var surfaceAttached = false

                    private val metalLayer = CAMetalLayer().apply {
                        opaque = !transparent
                        pixelFormat = MTLPixelFormatBGRA8Unorm
                        device = MTLCreateSystemDefaultDevice() as MTLDeviceProtocol?
                    }

                    init {
                        userInteractionEnabled = false
                        opaque = !transparent
                        if (transparent) {
                            backgroundColor = UIColor.clearColor
                        }
                        layer.addSublayer(metalLayer)
                    }

                    override fun layoutSubviews() {
                        super.layoutSubviews()

                        metalLayer.frame = bounds

                        val scale = UIScreen.mainScreen.scale
                        metalLayer.contentsScale = scale

                        val width = (bounds.useContents { size.width } * scale).toInt()
                        val height = (bounds.useContents { size.height } * scale).toInt()

                        if (width > 0 && height > 0) {
                            if (!surfaceAttached) {
                                swapChainRef.value = engine.createSwapChain(
                                    NativeSurface(interpretCPointer(metalLayer.objcPtr())),
                                    if (transparent) SWAP_CHAIN_CONFIG_TRANSPARENT else 0L,
                                )
                                surfaceAttached = true
                            }
                            metalLayer.drawableSize = CGSizeMake(width.toDouble(), height.toDouble())
                            view.viewport = Viewport(0, 0, width, height)
                            onResizeRef.value?.invoke(width.toDouble() / height.toDouble())
                        }
                    }
                }
            },
            modifier = modifier,
            update = {},
            onRelease = {},
            // placedAsOverlay: below the Metal canvas Compose punches a transparent hole for the interop
            // view, erasing whatever Compose drew behind it. As an overlay it composites on top instead.
            properties = UIKitInteropProperties(interactionMode = null, placedAsOverlay = transparent),
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
