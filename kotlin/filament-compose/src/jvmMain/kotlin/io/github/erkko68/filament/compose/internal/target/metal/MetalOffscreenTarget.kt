package io.github.erkko68.filament.compose.internal.target.metal

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.RenderTarget
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.SwapChain
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.View
import io.github.erkko68.filament.compose.internal.target.OffscreenTarget
import io.github.erkko68.filament.compose.internal.target.unavailable
import io.github.erkko68.filament.jni.MetalHelper
import java.awt.Window
import org.jetbrains.skia.BackendRenderTarget
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.Image
import org.jetbrains.skia.Surface
import org.jetbrains.skia.SurfaceColorFormat
import org.jetbrains.skia.SurfaceOrigin

/**
 * Filament renders into `MTLTexture`s on skiko's device; once the GPU finishes a frame, Skia wraps
 * that texture on skiko's [org.jetbrains.skia.DirectContext] and snapshots it (a GPU-side copy).
 */
internal class MetalOffscreenTarget private constructor(
    private val engine: Engine,
    private val skiko: SkikoMetal,
    private val width: Int,
    private val height: Int,
) : OffscreenTarget {

    private class Slot(val texturePtr: Long, val color: Texture, val depth: Texture, val target: RenderTarget)

    private val swapChain: SwapChain = engine.createSwapChain(width, height, 0L)

    private val slots = Array(2) {
        val texturePtr = MetalHelper.nCreateMetalTexture(skiko.devicePtr, width, height)
        check(texturePtr != 0L) { "Failed to create a ${width}x$height MTLTexture" }
        val color = Texture.Builder()
            .width(width).height(height)
            .sampler(Texture.Sampler.SAMPLER_2D)
            .format(Texture.InternalFormat.RGBA8)
            .usage(Texture.Usage.COLOR_ATTACHMENT or Texture.Usage.SAMPLEABLE)
            .import(texturePtr)
            .build(engine)
        val depth = Texture.Builder()
            .width(width).height(height)
            .sampler(Texture.Sampler.SAMPLER_2D)
            .format(Texture.InternalFormat.DEPTH24)
            .usage(Texture.Usage.DEPTH_ATTACHMENT)
            .build(engine)
        val target = RenderTarget.Builder()
            .texture(RenderTarget.AttachmentPoint.COLOR, color)
            .texture(RenderTarget.AttachmentPoint.DEPTH, depth)
            .build(engine)
        Slot(texturePtr, color, depth, target)
    }

    // Rendered slots awaiting GPU completion, oldest first; completions arrive in submit order.
    private val inFlight = ArrayDeque<Slot>()
    private var finished: Slot? = null

    init {
        // Fires on this thread from engine.flush(), replacing a per-frame flushAndWait stall.
        swapChain.setFrameCompletedCallback { finished = inFlight.removeFirst() }
    }

    override fun renderFrame(renderer: Renderer, view: View, frameTimeNanos: Long): Image? {
        engine.flush()
        val image = finished?.let(::snapshot)
        finished = null

        val slot = slots.firstOrNull { it !in inFlight } ?: return image
        view.renderTarget = slot.target
        if (renderer.beginFrame(swapChain, frameTimeNanos)) {
            renderer.render(view)
            renderer.endFrame()
            inFlight.addLast(slot)
        }
        return image
    }

    private fun snapshot(slot: Slot): Image? {
        val context = skiko.context ?: return null
        return synchronized(skiko.lock) {
            // Fresh wrappers each frame: Skia caches snapshots of surfaces it didn't draw to.
            val brt = BackendRenderTarget.makeMetal(width, height, slot.texturePtr)
            val surface = Surface.makeFromBackendRenderTarget(
                context, brt, SurfaceOrigin.TOP_LEFT, SurfaceColorFormat.BGRA_8888, ColorSpace.sRGB,
            )
            val image = surface?.makeImageSnapshot()
            surface?.close()
            brt.close()
            // ponytail: CPU-waits for the copy so Filament can't overwrite the slot mid-copy;
            // an MTLSharedEvent on skiko's queue would avoid it, but skiko's Skia binding exposes no semaphores.
            context.flush()
            context.submit(true)
            image
        }
    }

    override fun close() {
        engine.flushAndWait()
        swapChain.setFrameCompletedCallback(null)
        engine.destroy(swapChain)
        slots.forEach {
            engine.destroy(it.target)
            engine.destroy(it.color)
            engine.destroy(it.depth)
        }
    }

    companion object {
        fun create(engine: Engine, window: Window?, width: Int, height: Int): MetalOffscreenTarget {
            if (engine.backend != Engine.Backend.METAL) {
                unavailable("on macOS it needs Engine.Backend.METAL, not ${engine.backend}")
            }
            val skiko = SkikoMetal.find(window) ?: unavailable("Compose isn't rendering this window with Metal")
            // Filament always renders on the system default GPU; Skia can only sample textures from its own.
            if (!MetalHelper.nIsSystemDefaultDevice(skiko.devicePtr)) {
                unavailable("Compose picked a non-default GPU; set -Dskiko.gpu.priority so it matches Filament's")
            }
            return MetalOffscreenTarget(engine, skiko, width, height)
        }
    }
}
