package io.github.erkko68.filament.compose.internal

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
    // Whether displayedImage's rows run bottom-up; set with it, as it may outlive its target.
    var displayedBottomUp by remember { mutableStateOf(false) }
    // The replaced frame stays alive one more frame, until Compose has replayed its last draw.
    val previousImage = remember { Ref<Image>() }
    var target by remember { mutableStateOf<OffscreenTarget?>(null) }
    val window = LocalAwtWindow.current

    // Updated in composition, not in a SideEffect: those run after the effect below, which would then
    // call the previous lambda (and its previous, destroyed camera) when the view changes.
    val currentOnResize by rememberUpdatedState(onResize)

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

    // Keyed on the engine and view too: a target outliving them would be rendered into by the next ones.
    DisposableEffect(engine, view, textureSize, transparent) {
        val w = textureSize.width
        val h = textureSize.height

        if (w > 0 && h > 0) {
            view.viewport = Viewport(0, 0, w, h)
            currentOnResize(w.toDouble() / h.toDouble())
            target = OffscreenTarget(engine, window, w, h, transparent)
        }

        onDispose {
            // The displayed image is a GPU copy, so it keeps showing through the resize.
            target?.close()
            target = null
        }
    }

    // Targets keep up to two frames in flight, so the third shown after a pause was rendered paused.
    val gate = rememberPausedFrameGate(renderingEnabled, framesToSettle = 3, target)
    FilamentRenderLoop(gate.loopEnabled(renderingEnabled)) { frameTime ->
        val current = target ?: return@FilamentRenderLoop
        val image = current.renderFrame(renderer, view, frameTime) ?: return@FilamentRenderLoop
        gate.delivered(paused = !renderingEnabled)
        previousImage.value?.close()
        previousImage.value = displayedImage
        displayedImage = image
        displayedBottomUp = current.bottomUp
    }

    Spacer(
        modifier = modifier
            .onSizeChanged { layoutSize = it }
            .drawBehind {
                val image = displayedImage ?: return@drawBehind
                drawIntoCanvas { canvas ->
                    val skia = canvas.skiaCanvas
                    skia.save()
                    if (displayedBottomUp) {
                        skia.translate(0f, size.height)
                        skia.scale(1f, -1f)
                    }
                    skia.drawImageRect(
                        image,
                        Rect.makeWH(image.width.toFloat(), image.height.toFloat()),
                        Rect.makeWH(size.width, size.height),
                        SamplingMode.LINEAR,
                        null,
                        true,
                    )
                    skia.restore()
                }
            }
    )
}
