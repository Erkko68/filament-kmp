@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package io.github.erkko68.filament.compose.internal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.WebElementView
import io.github.erkko68.filament.Camera
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.NativeSurface
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.SwapChain
import io.github.erkko68.filament.View
import io.github.erkko68.filament.Viewport
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

@Composable
internal actual fun FilamentSurface(
    modifier: Modifier,
    engine: Engine,
    renderer: Renderer,
    view: View,
    camera: Camera,
) {
    var layoutSize by remember { mutableStateOf(IntSize.Zero) }
    var renderSize by remember { mutableStateOf(IntSize.Zero) }
    val swapChainHolder = remember { arrayOfNulls<SwapChain>(1) }

    LaunchedEffect(layoutSize) {
        val w = layoutSize.width
        val h = layoutSize.height
        if (w <= 0 || h <= 0) return@LaunchedEffect
        if (renderSize.width <= 0) {
            renderSize = layoutSize
        } else {
            kotlinx.coroutines.delay(150L)
            renderSize = layoutSize
        }
    }

    LaunchedEffect(renderSize) {
        val w = renderSize.width
        val h = renderSize.height
        if (w <= 0 || h <= 0) return@LaunchedEffect
        val canvas = engine.jsCanvas ?: return@LaunchedEffect
        
        swapChainHolder[0]?.let { engine.destroySwapChain(it) }
        
        canvas.width = w
        canvas.height = h
        val sc = engine.createSwapChain(NativeSurface(canvas))
        swapChainHolder[0] = sc
        view.setViewport(Viewport(0, 0, w, h))
        camera.setProjection(45.0, w.toDouble() / h.toDouble(), 0.1, 100.0, Camera.Fov.VERTICAL)

        // Force an immediate render to fill the newly resized canvas before the next browser paint
        if (renderer.beginFrame(sc, Engine.getSteadyClockTimeNano())) {
            renderer.render(view)
            renderer.endFrame()
        }
        engine.flush()
    }

    FilamentRenderLoop { frameTime ->
        val sc = swapChainHolder[0] ?: return@FilamentRenderLoop
        if (renderer.beginFrame(sc, frameTime)) {
            renderer.render(view)
            renderer.endFrame()
        }
    }

    Box(
        modifier = modifier.onGloballyPositioned { layoutSize = it.size }
    ) {
        WebElementView(
            factory = {
                val container = document.createElement("div") as HTMLElement
                container.style.width = "100%"
                container.style.height = "100%"
                window.requestAnimationFrame {
                    (container.parentElement as? HTMLElement)?.style?.zIndex = "-1"
                }
                container
            },
            modifier = Modifier.fillMaxSize().drawBehind {
                drawRect(color = Color.Transparent, blendMode = BlendMode.Clear)
            },
            update = { container ->
                val canvas = engine.jsCanvas ?: return@WebElementView
                if (canvas.parentNode !== container) {
                    canvas.style.position = "absolute"
                    canvas.style.left = "0"
                    canvas.style.top = "0"
                    canvas.style.width = "100%"
                    canvas.style.height = "100%"
                    canvas.style.asDynamic().pointerEvents = ""
                    container.appendChild(canvas)
                }
            },
        )
    }
}
