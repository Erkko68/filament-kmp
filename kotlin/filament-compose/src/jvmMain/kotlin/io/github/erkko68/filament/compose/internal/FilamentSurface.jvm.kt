package io.github.erkko68.filament.compose.internal

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.LocalAwtWindow
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.skiaCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.node.Ref
import androidx.compose.ui.unit.IntSize
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.View
import io.github.erkko68.filament.Viewport
import io.github.erkko68.filament.compose.internal.target.OffscreenTarget
import kotlinx.coroutines.delay
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect
import org.jetbrains.skia.SamplingMode

private const val RESIZE_DEBOUNCE_MS = 150L

@OptIn(ExperimentalComposeUiApi::class)
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
    var layoutSize by remember { mutableStateOf(IntSize.Zero) }
    var textureSize by remember { mutableStateOf(IntSize.Zero) }
    var displayedImage by remember { mutableStateOf<Image?>(null) }
    // The replaced frame stays alive one more frame, until Compose has replayed its last draw.
    val previousImage = remember { Ref<Image>() }
    var target by remember { mutableStateOf<OffscreenTarget?>(null) }
    val window = LocalAwtWindow.current

    // Keep a mutable ref so DisposableEffect(textureSize) always dispatches to the latest lambda.
    val onResizeRef = remember { Ref<(Double) -> Unit>() }
    SideEffect { onResizeRef.value = onResize }

    DisposableEffect(Unit) {
        onDispose {
            displayedImage?.close()
            displayedImage = null
            previousImage.value?.close()
            previousImage.value = null
        }
    }

    LaunchedEffect(layoutSize) {
        val w = layoutSize.width
        val h = layoutSize.height
        if (w <= 0 || h <= 0) return@LaunchedEffect
        if (textureSize.width <= 0) {
            textureSize = IntSize(w, h)
        } else {
            delay(RESIZE_DEBOUNCE_MS)
            textureSize = IntSize(w, h)
        }
    }

    DisposableEffect(textureSize) {
        val w = textureSize.width
        val h = textureSize.height

        if (w > 0 && h > 0) {
            view.viewport = Viewport(0, 0, w, h)
            onResizeRef.value?.invoke(w.toDouble() / h.toDouble())
            target = OffscreenTarget(engine, window, w, h)
        }

        onDispose {
            // The displayed image is a GPU copy, so it keeps showing through the resize.
            target?.close()
            target = null
        }
    }

    FilamentRenderLoop(renderingEnabled) { frameTime ->
        val image = target?.renderFrame(renderer, view, frameTime) ?: return@FilamentRenderLoop
        previousImage.value?.close()
        previousImage.value = displayedImage
        displayedImage = image
    }

    Spacer(
        modifier = modifier
            .onSizeChanged { layoutSize = it }
            .drawBehind {
                val image = displayedImage ?: return@drawBehind
                drawIntoCanvas { canvas ->
                    canvas.skiaCanvas.drawImageRect(
                        image,
                        Rect.makeWH(image.width.toFloat(), image.height.toFloat()),
                        Rect.makeWH(size.width, size.height),
                        SamplingMode.LINEAR,
                        null,
                        true,
                    )
                }
            }
    )
}
