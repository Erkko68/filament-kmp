@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package eric.bitria.samples

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.WebElementView
import io.github.erkko68.filament.NativeSurface
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

@Composable
actual fun FilamentView(modifier: Modifier, controller: FilamentController) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(size) {
        val w = size.width
        val h = size.height
        if (w <= 0 || h <= 0) return@LaunchedEffect
        val engine = controller.engine ?: return@LaunchedEffect
        val canvas = engine.jsCanvas ?: return@LaunchedEffect
        canvas.width = w
        canvas.height = h
        controller.setSurface(NativeSurface(canvas), w, h)
    }

    var frameTime by remember { mutableStateOf(0L) }
    FilamentRenderLoop { frameTime = it }

    Box(
        modifier = modifier
            .onGloballyPositioned { size = it.size }
            .drawBehind { controller.render(frameTime) }
    ) {
        WebElementView(
            factory = {
                val container = document.createElement("div") as HTMLElement
                container.style.width = "100%"
                container.style.height = "100%"
                container
            },
            modifier = Modifier.fillMaxSize(),
            update = { container ->
                val canvas = controller.engine?.jsCanvas ?: return@WebElementView
                if (canvas.parentNode !== container) {
                    canvas.style.position = "absolute"
                    canvas.style.left = "0"
                    canvas.style.top = "0"
                    canvas.style.width = "100%"
                    canvas.style.height = "100%"
                    canvas.style.asDynamic().pointerEvents = ""
                    container.appendChild(canvas)
                }
            }
        )
    }
}
