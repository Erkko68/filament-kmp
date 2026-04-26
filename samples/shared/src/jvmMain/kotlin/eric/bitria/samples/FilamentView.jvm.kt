package eric.bitria.samples

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.RenderTarget
import io.github.erkko68.filament.SwapChain
import io.github.erkko68.filament.Texture
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo

private const val INITIAL_CAPACITY = 1024
private const val MAX_CAPACITY = 16384

private fun nextPow2(v: Int): Int {
    var n = 1
    while (n < v) n = n shl 1
    return n.coerceAtMost(MAX_CAPACITY)
}

private class OffscreenSurface(
    val capacity: Int,
    val colorTexture: Texture,
    val depthTexture: Texture,
    val renderTarget: RenderTarget,
    val swapChain: SwapChain,
) {
    fun destroy(engine: Engine) {
        engine.destroySwapChain(swapChain)
        engine.destroyRenderTarget(renderTarget)
        engine.destroyTexture(depthTexture)
        engine.destroyTexture(colorTexture)
    }
}

private fun createSurface(engine: Engine, capacity: Int): OffscreenSurface {
    val colorTexture = Texture.Builder()
        .width(capacity).height(capacity).levels(1)
        .usage(Texture.Usage.COLOR_ATTACHMENT or Texture.Usage.BLIT_SRC or Texture.Usage.SAMPLEABLE)
        .format(Texture.InternalFormat.RGBA8)
        .build(engine)
    val depthTexture = Texture.Builder()
        .width(capacity).height(capacity)
        .usage(Texture.Usage.DEPTH_ATTACHMENT)
        .format(Texture.InternalFormat.DEPTH32F)
        .build(engine)
    val renderTarget = RenderTarget.Builder()
        .texture(RenderTarget.AttachmentPoint.COLOR, colorTexture)
        .texture(RenderTarget.AttachmentPoint.DEPTH, depthTexture)
        .build(engine)
    val swapChain = engine.createSwapChain(capacity, capacity, 0)
    return OffscreenSurface(capacity, colorTexture, depthTexture, renderTarget, swapChain)
}

@Composable
actual fun FilamentView(modifier: Modifier, controller: FilamentController) {
    var layoutSize by remember { mutableStateOf(IntSize.Zero) }
    var snapshotImage by remember { mutableStateOf<Image?>(null) }
    var surface by remember { mutableStateOf<OffscreenSurface?>(null) }

    // Two pixel buffers + descriptors so the GPU can be writing one while Skia
    // displays the other. `pending` tracks in-flight reads per slot. A generation
    // counter discards readPixels callbacks that land after a capacity reallocation.
    val pixelBuffers = remember { arrayOfNulls<ByteArray>(2) }
    val descriptors = remember { arrayOfNulls<Texture.PixelBufferDescriptor>(2) }
    val pending = remember { booleanArrayOf(false, false) }
    val nextSlot = remember { intArrayOf(0) }
    val generation = remember { intArrayOf(0) }

    fun ensureCapacity(engine: Engine, requested: Int): OffscreenSurface? {
        val current = surface
        if (current != null && requested <= current.capacity) return current
        val newCap = nextPow2(maxOf(requested, current?.capacity ?: INITIAL_CAPACITY))
        // Tear down the old surface and any in-flight read.
        if (current != null) {
            controller.view?.setRenderTarget(null)
            engine.flushAndWait()
            current.destroy(engine)
        }
        generation[0]++
        val gen = generation[0]
        val fresh = createSurface(engine, newCap)
        for (i in 0..1) {
            val slot = i
            val buffer = ByteArray(newCap * newCap * 4)
            val descriptor = Texture.PixelBufferDescriptor(
                storage = buffer,
                sizeInBytes = buffer.size,
                format = Texture.Format.RGBA,
                type = Texture.Type.UBYTE,
                alignment = 1,
                left = 0,
                top = 0,
                stride = newCap,
                callback = {
                    // Drop callbacks from a previous capacity generation.
                    if (generation[0] == gen) {
                        val w = layoutSize.width
                        val h = layoutSize.height
                        if (w > 0 && h > 0 && w <= newCap && h <= newCap) {
                            val info = ImageInfo(w, h, ColorType.RGBA_8888, ColorAlphaType.PREMUL)
                            val newImage = Image.makeRaster(info, buffer, newCap * 4)
                            val old = snapshotImage
                            snapshotImage = newImage
                            old?.close()
                        }
                        pending[slot] = false
                    }
                }
            )
            pixelBuffers[slot] = buffer
            descriptors[slot] = descriptor
            pending[slot] = false
        }
        nextSlot[0] = 0
        snapshotImage?.close()
        snapshotImage = null
        controller.view?.setRenderTarget(fresh.renderTarget)
        surface = fresh
        return fresh
    }

    DisposableEffect(Unit) {
        controller.initialize()
        val engine = controller.engine
        if (engine != null) ensureCapacity(engine, INITIAL_CAPACITY)

        onDispose {
            val s = surface ?: return@onDispose
            val eng = controller.engine ?: return@onDispose
            controller.view?.setRenderTarget(null)
            pending[0] = false; pending[1] = false
            eng.flushAndWait()
            s.destroy(eng)
            surface = null
            snapshotImage?.close()
            snapshotImage = null
        }
    }

    LaunchedEffect(layoutSize) {
        val w = layoutSize.width
        val h = layoutSize.height
        if (w > 0 && h > 0) {
            val engine = controller.engine
            val needed = maxOf(w, h)
            val current = surface
            if (engine != null && (current == null || needed > current.capacity)) {
                ensureCapacity(engine, needed)
            }
            controller.updateViewport(w, h)
        }
    }

    FilamentRenderLoop { frameTime ->
        val s = surface ?: return@FilamentRenderLoop
        val w = layoutSize.width
        val h = layoutSize.height
        if (w <= 0 || h <= 0 || w > s.capacity || h > s.capacity) return@FilamentRenderLoop
        controller.render(frameTime, s.swapChain, flush = false)
        val slot = nextSlot[0]
        if (!pending[slot]) {
            val descriptor = descriptors[slot] ?: return@FilamentRenderLoop
            pending[slot] = true
            nextSlot[0] = slot xor 1
            controller.renderer?.readPixels(
                renderTarget = s.renderTarget,
                xoffset = 0,
                yoffset = 0,
                width = w,
                height = h,
                buffer = descriptor
            )
        }
    }

    Spacer(
        modifier = modifier
            .onGloballyPositioned { layoutSize = it.size }
            .drawBehind {
                val image = snapshotImage ?: return@drawBehind
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawImage(image, 0f, 0f)
                }
            }
    )
}
