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
import androidx.compose.ui.node.Ref
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import io.github.erkko68.filament.Completions
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.SwapChain
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.View
import io.github.erkko68.filament.Viewport
import io.github.erkko68.filament.ffm.FilamentC
import java.lang.foreign.MemorySegment
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.delay
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Data
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Rect
import org.jetbrains.skia.SamplingMode

private const val RESIZE_DEBOUNCE_MS = 150L

private const val SWAP_CHAIN_CONFIG_READABLE = 0x2L

// Slot lifecycle: FREE →(ui) IN_FLIGHT →(readback callback) PUBLISHED →(ui) DISPLAYED →(ui) FREE.
private const val SLOT_FREE = 0
private const val SLOT_IN_FLIGHT = 1
private const val SLOT_PUBLISHED = 2
private const val SLOT_DISPLAYED = 3

/**
 * Double-buffered zero-copy readback target. Filament's readPixels writes straight into
 * each slot's Skia [Data] memory, which is then wrapped without copying in an [Image]
 * (the [Data] overload of [Image.makeRaster] shares the pixels; the ByteArray one copies).
 * One slot backs the image on screen while the other's GPU→CPU copy is in flight, so
 * readbacks pipeline with rendering. Calls FilamentC directly to keep the segment-based
 * readPixels fast path private to this file instead of widening the public bindings.
 */
private class Readback(val width: Int, val height: Int) {
    val imageInfo = ImageInfo(width, height, ColorType.RGBA_8888, ColorAlphaType.OPAQUE)

    class Slot(sizeInBytes: Int) {
        val data: Data = Data.makeUninitialized(sizeInBytes)

        // Zero-length segment is fine: FilaRenderer_readPixels takes the size separately.
        val address: MemorySegment = MemorySegment.ofAddress(data.writableData())

        /** The readback callback may fire on the backend thread; this atomic publishes [image]. */
        val state = AtomicInteger(SLOT_FREE)
        var image: Image? = null
        var seq = 0L
    }

    val slots = Array(2) { Slot(width * height * 4) }
    private var issueSeq = 0L

    /** UI thread. Starts an async GPU→CPU copy of the current frame into a free slot, if any. */
    fun issueReadPixels(renderer: Renderer) {
        val rendererHandle = renderer.nativeHandle ?: return
        val slot = slots.firstOrNull { it.state.get() == SLOT_FREE } ?: return
        slot.seq = ++issueSeq
        slot.state.set(SLOT_IN_FLIGHT)
        FilamentC.FilaRenderer_readPixels(
            rendererHandle,
            0, 0, width, height,
            slot.address, (width * height * 4).toLong(),
            // The jvm actuals map these enums to native by ordinal (see Texture.jvm.kt).
            Texture.Format.RGBA.ordinal, Texture.Type.UBYTE.ordinal,
            1.toByte(), 0, 0, width,
            MemorySegment.NULL, Completions.bufferStub,
            Completions.register {
                // Possibly the backend thread: wrap the slot's pixels zero-copy; the
                // state store publishes image to the UI thread. No Compose state here.
                slot.image = Image.makeRaster(imageInfo, slot.data, width * 4)
                slot.state.set(SLOT_PUBLISHED)
            },
        )
    }

    /** UI thread. Adopts the newest published slot (retiring stale ones), or null to keep current. */
    fun adoptPublished(displayedSlot: Slot?): Slot? {
        var newest: Slot? = null
        for (slot in slots) {
            if (slot.state.get() != SLOT_PUBLISHED) continue
            if (newest == null || slot.seq > newest.seq) newest = slot
        }
        val adopted = newest ?: return null
        for (slot in slots) {
            // A published slot that lost the race was never shown — recycle it.
            if (slot !== adopted && slot.state.get() == SLOT_PUBLISHED) free(slot)
        }
        if (displayedSlot != null) free(displayedSlot)
        adopted.state.set(SLOT_DISPLAYED)
        return adopted
    }

    /** UI thread only — closing an [Image] the UI thread might be drawing must happen there too. */
    fun free(slot: Slot) {
        slot.image?.close()
        slot.image = null
        slot.state.set(SLOT_FREE)
    }

    /** UI thread, only after flushAndWait: no readback may still be in flight. */
    fun destroy() {
        slots.forEach { slot ->
            slot.image?.close()
            slot.image = null
            slot.data.close()
        }
    }
}

private class OffscreenSurface(val swapChain: SwapChain, val readback: Readback)

/** Which slot backs the on-screen image; UI thread only. */
private class DisplayRef {
    var slot: Readback.Slot? = null
}

/** Detaches [image] from slot memory by copying it to the heap; closes the original. */
private fun heapCopy(image: Image, info: ImageInfo): Image? {
    val bitmap = Bitmap()
    if (!bitmap.allocPixels(info)) {
        bitmap.close()
        image.close()
        return null
    }
    val ok = image.readPixels(bitmap)
    image.close()
    if (!ok) {
        bitmap.close()
        return null
    }
    bitmap.setImmutable()
    return Image.makeFromBitmap(bitmap).also { bitmap.close() }
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
    var displayedImage by remember { mutableStateOf<Image?>(null) }
    var surface by remember { mutableStateOf<OffscreenSurface?>(null) }
    val display = remember { DisplayRef() }

    // Metal delivers readPixels rows top-down, OpenGL bottom-up (GL convention) — pinned
    // by FrameSemanticsTest.readPixelsRowOrderMatchesBackendConvention.
    val flipVertically = remember(engine) { engine.backend == Engine.Backend.OPENGL }

    // Keep a mutable ref so DisposableEffect(textureSize) always dispatches to the latest lambda.
    val onResizeRef = remember { Ref<(Double) -> Unit>() }
    SideEffect { onResizeRef.value = onResize }

    // Declared before the textureSize effect: effects dispose in reverse order, so at
    // teardown the readback generation is drained and destroyed first, then this closes
    // whatever image ended up displayed (possibly one published during that drain).
    DisposableEffect(Unit) {
        onDispose {
            displayedImage?.close()
            displayedImage = null
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
            val swapChain = engine.createSwapChain(w, h, SWAP_CHAIN_CONFIG_READABLE)
            view.viewport = Viewport(0, 0, w, h)
            onResizeRef.value?.invoke(w.toDouble() / h.toDouble())
            surface = OffscreenSurface(swapChain, Readback(w, h))
        }

        onDispose {
            val s = surface ?: return@onDispose
            surface = null
            // Drains in-flight readbacks so no callback touches slot memory afterwards.
            engine.flushAndWait()
            engine.destroySwapChain(s.swapChain)
            val shown = display.slot
            if (shown != null) {
                display.slot = null
                // Keep the last frame on screen through the resize; its backing slot
                // memory dies with the generation, so detach it to the heap first.
                // heapCopy closes the slot-backed image — drop the slot's reference so
                // readback.destroy() doesn't close it again.
                displayedImage = displayedImage?.let { heapCopy(it, s.readback.imageInfo) }
                shown.image = null
            }
            s.readback.destroy()
        }
    }

    FilamentRenderLoop { frameTime ->
        val s = surface ?: return@FilamentRenderLoop
        // Adopt before rendering so the freed slot can take this frame's readback.
        s.readback.adoptPublished(display.slot)?.let { slot ->
            display.slot = slot
            displayedImage = slot.image
        }
        if (renderer.beginFrame(s.swapChain, frameTime)) {
            renderer.render(view)
            // Swapchain readback must happen inside the frame (after render, before endFrame).
            s.readback.issueReadPixels(renderer)
            renderer.endFrame()
        }
    }

    Spacer(
        modifier = modifier
            .onSizeChanged { layoutSize = it }
            .drawBehind {
                val image = displayedImage ?: return@drawBehind
                drawIntoCanvas { canvas ->
                    val nativeCanvas = canvas.nativeCanvas
                    nativeCanvas.save()
                    if (flipVertically) {
                        nativeCanvas.translate(0f, size.height)
                        nativeCanvas.scale(1f, -1f)
                    }
                    nativeCanvas.drawImageRect(
                        image,
                        Rect.makeWH(image.width.toFloat(), image.height.toFloat()),
                        Rect.makeWH(size.width, size.height),
                        SamplingMode.LINEAR,
                        null,
                        true,
                    )
                    nativeCanvas.restore()
                }
            }
    )
}
