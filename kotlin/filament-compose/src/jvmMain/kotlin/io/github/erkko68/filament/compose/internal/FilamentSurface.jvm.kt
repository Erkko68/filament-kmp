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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.RenderTarget
import io.github.erkko68.filament.SwapChain
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.View
import io.github.erkko68.filament.Viewport
import kotlinx.coroutines.delay
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Rect

private const val RESIZE_DEBOUNCE_MS = 150L

private class OffscreenSurface(
    val colorTexture: Texture,
    val depthTexture: Texture,
    val renderTarget: RenderTarget,
    val swapChain: SwapChain,
    val width: Int,
    val height: Int,
) {
    fun destroy(engine: Engine) {
        engine.destroySwapChain(swapChain)
        engine.destroyRenderTarget(renderTarget)
        engine.destroyTexture(depthTexture)
        engine.destroyTexture(colorTexture)
    }
}

@Composable
internal actual fun FilamentSurface(
    modifier: Modifier,
    engine: Engine,
    renderer: Renderer,
    view: View,
    onResize: (aspect: Double) -> Unit,
) {
    var layoutSize by remember { mutableStateOf(IntSize.Zero) }
    var textureSize by remember { mutableStateOf(IntSize.Zero) }
    var snapshotImage by remember { mutableStateOf<Image?>(null) }
    var readPixelsPending by remember { mutableStateOf(false) }
    var surface by remember { mutableStateOf<OffscreenSurface?>(null) }

    // Keep a mutable ref so DisposableEffect(textureSize) always dispatches to the latest lambda.
    val onResizeRef = remember { Array<(Double) -> Unit>(1) { onResize } }
    SideEffect { onResizeRef[0] = onResize }

    DisposableEffect(Unit) {
        onDispose {
            snapshotImage?.close()
            snapshotImage = null
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

    val pixelBuffer = remember(textureSize) {
        if (textureSize.width > 0 && textureSize.height > 0)
            ByteArray(textureSize.width * textureSize.height * 4)
        else null
    }

    val pixelBufferDescriptor = remember(pixelBuffer) {
        if (pixelBuffer == null) return@remember null
        val tw = textureSize.width
        val th = textureSize.height
        Texture.PixelBufferDescriptor(
            storage = pixelBuffer,
            sizeInBytes = pixelBuffer.size,
            format = Texture.Format.RGBA,
            type = Texture.Type.UBYTE,
            alignment = 1,
            left = 0,
            top = 0,
            stride = tw,
            callback = {
                val info = ImageInfo(tw, th, ColorType.RGBA_8888, ColorAlphaType.PREMUL)
                val newImage = Image.makeRaster(info, pixelBuffer, tw * 4)
                val old = snapshotImage
                snapshotImage = newImage
                old?.close()
                readPixelsPending = false
            }
        )
    }

    DisposableEffect(textureSize) {
        val w = textureSize.width
        val h = textureSize.height

        if (w > 0 && h > 0) {
            val colorTexture = Texture.Builder()
                .width(w).height(h).levels(1)
                .usage(Texture.Usage.COLOR_ATTACHMENT or Texture.Usage.BLIT_SRC or Texture.Usage.SAMPLEABLE)
                .format(Texture.InternalFormat.RGBA8)
                .build(engine)
            val depthTexture = Texture.Builder()
                .width(w).height(h)
                .usage(Texture.Usage.DEPTH_ATTACHMENT)
                .format(Texture.InternalFormat.DEPTH32F)
                .build(engine)
            val renderTarget = RenderTarget.Builder()
                .texture(RenderTarget.AttachmentPoint.COLOR, colorTexture)
                .texture(RenderTarget.AttachmentPoint.DEPTH, depthTexture)
                .build(engine)
            val swapChain = engine.createSwapChain(w, h, 0)
            view.setRenderTarget(renderTarget)
            view.setViewport(Viewport(0, 0, w, h))
            onResizeRef[0](w.toDouble() / h.toDouble())
            surface = OffscreenSurface(colorTexture, depthTexture, renderTarget, swapChain, w, h)
        }

        onDispose {
            val s = surface ?: return@onDispose
            view.setRenderTarget(null)
            surface = null
            engine.flushAndWait()
            s.destroy(engine)
        }
    }

    FilamentRenderLoop { frameTime ->
        val s = surface ?: return@FilamentRenderLoop
        if (renderer.beginFrame(s.swapChain, frameTime)) {
            renderer.render(view)
            renderer.endFrame()
            engine.flushAndWait()
        }
        if (!readPixelsPending && pixelBufferDescriptor != null) {
            readPixelsPending = true
            renderer.readPixels(
                renderTarget = s.renderTarget,
                xoffset = 0,
                yoffset = 0,
                width = s.width,
                height = s.height,
                buffer = pixelBufferDescriptor,
            )
        }
    }

    Spacer(
        modifier = modifier
            .onGloballyPositioned { layoutSize = it.size }
            .drawBehind {
                val image = snapshotImage ?: return@drawBehind
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawImageRect(
                        image,
                        Rect.makeWH(size.width, size.height),
                    )
                }
            }
    )
}
