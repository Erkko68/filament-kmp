@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package io.github.erkko68.filament.compose.internal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.node.Ref
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.HtmlElementView
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.View
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import kotlin.math.roundToInt

/**
 * Web has one WebGL context per engine, so all views share [WebViewCompositor], which renders each
 * into the offscreen engine canvas and blits its slice onto a per-view 2D canvas. This surface owns
 * that 2D canvas, hosts it through Compose HTML interop, and reports its window-space bounds to the
 * compositor. The per-view [renderer] is unused here.
 *
 * Stacking depends on [transparent]. Opaque: the canvas sits *behind* the Compose canvas, revealed
 * by a hole punched in it — so Compose content behind the view is erased. Transparent: the canvas
 * sits *in front* with no hole punch, and its own alpha composites over the Compose UI (which is
 * why Compose content drawn above the view shows through, rather than covering it).
 */
@Composable
internal actual fun FilamentSurface(
    modifier: Modifier,
    engine: Engine,
    renderer: Renderer,
    view: View,
    transparent: Boolean,
    onResize: (aspect: Double) -> Unit,
) {
    val compositor = remember(engine) { WebViewCompositor.of(engine) }
    val target = remember { document.createElement("canvas") as HTMLCanvasElement }
    val entry = remember(compositor, view, target) { compositor.register(view, target) }

    // Keep a mutable ref so the size callback always dispatches to the latest lambda.
    val onResizeRef = remember { Ref<(Double) -> Unit>() }
    SideEffect { onResizeRef.value = onResize }

    DisposableEffect(compositor, entry) {
        onDispose {
            entry.disposed = true
            compositor.unregister(entry)
        }
    }

    var lastSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier.onGloballyPositioned { coords ->
            val pos = coords.positionInWindow()
            val size = coords.size
            val left = pos.x.roundToInt()
            val top = pos.y.roundToInt()
            entry.rect = IntRect(left, top, left + size.width, top + size.height)

            if (size != lastSize) {
                lastSize = size
                if (size.width > 0 && size.height > 0) {
                    onResizeRef.value?.invoke(size.width.toDouble() / size.height.toDouble())
                }
            }
        }
    ) {
        // factory runs once, so the stacking set up below is fixed at creation — key() rebuilds it
        // when transparency is toggled.
        key(transparent) {
            HtmlElementView(factory = {
                val container = document.createElement("div") as HTMLElement
                container.style.width = "100%"
                container.style.height = "100%"
                if (transparent) container.style.setProperty("pointer-events", "none")
                window.requestAnimationFrame {
                    (container.parentElement as? HTMLElement)?.let { parent ->
                        parent.style.zIndex = if (transparent) "1" else "-1"
                        if (transparent) parent.style.setProperty("pointer-events", "none")
                    }
                }
                if (target.parentNode !== container) {
                    target.style.position = "absolute"
                    target.style.left = "0"
                    target.style.top = "0"
                    target.style.width = "100%"
                    target.style.height = "100%"
                    container.appendChild(target)
                }
                container
            }, modifier = Modifier.fillMaxSize().let { m ->
                // The hole-punch is what reveals a canvas placed behind the Compose canvas; in front
                // it would just erase the Compose UI we want to show through.
                if (transparent) m else m.drawBehind {
                    drawRect(color = Color.Transparent, blendMode = BlendMode.Clear)
                }
            }, update = {})
        }
    }
}
