package io.github.erkko68.filament.compose.internal.target

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.SwapChain
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.View
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Data
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo

private const val SWAP_CHAIN_CONFIG_READABLE = 0x2L
private const val MAX_IN_FLIGHT = 2

/**
 * CPU readback: Filament renders into a readable headless swap chain and `readPixels` copies each
 * frame into Skia-owned memory, wrapped without a further copy as a raster [Image]. Works with any
 * engine and backend; the fallback when GPU-to-GPU frame sharing is off or fails.
 */
internal class ReadbackOffscreenTarget(
    private val engine: Engine,
    private val width: Int,
    private val height: Int,
    transparent: Boolean,
) : OffscreenTarget {

    private val swapChain: SwapChain = engine.createSwapChain(width, height, SWAP_CHAIN_CONFIG_READABLE)
    private val info = ImageInfo(
        width, height, ColorType.RGBA_8888,
        if (transparent) ColorAlphaType.PREMUL else ColorAlphaType.OPAQUE,
    )
    private val byteCount = width * height * 4

    // Written by readPixels callbacks, possibly on the backend thread.
    private val inFlight = AtomicInteger(0)
    private val newest = AtomicReference<Image?>(null)

    // OpenGL delivers readPixels rows bottom-up.
    override val bottomUp: Boolean = engine.backend == Engine.Backend.OPENGL

    override fun renderFrame(renderer: Renderer, view: View, frameTimeNanos: Long): Image? {
        val image = newest.getAndSet(null)
        if (inFlight.get() >= MAX_IN_FLIGHT) {
            // Keeps the backend polling its fences so the in-flight readbacks complete.
            renderer.skipFrame(frameTimeNanos)
            return image
        }
        view.renderTarget = null
        if (renderer.beginFrame(swapChain, frameTimeNanos)) {
            renderer.render(view)
            // Each frame gets its own memory, which the image keeps alive: it outlives this target
            // and the caller closes it whenever it's done drawing it.
            val data = Data.makeUninitialized(byteCount)
            inFlight.incrementAndGet()
            // Swap chain readback must happen inside the frame, after render.
            renderer.readPixels(
                0, 0, width, height, data.writableData(), byteCount,
                Texture.Format.RGBA, Texture.Type.UBYTE, width,
            ) {
                val frame = Image.makeRaster(info, data, width * 4)
                data.close()
                // A frame the UI never picked up is superseded by this one.
                newest.getAndSet(frame)?.close()
                inFlight.decrementAndGet()
            }
            renderer.endFrame()
        }
        return image
    }

    override fun close() {
        // Runs the pending readback callbacks before the swap chain goes.
        engine.flushAndWait()
        engine.destroy(swapChain)
        newest.getAndSet(null)?.close()
    }
}
