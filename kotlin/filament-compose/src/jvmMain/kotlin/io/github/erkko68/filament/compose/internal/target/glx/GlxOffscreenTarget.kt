package io.github.erkko68.filament.compose.internal.target.glx

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.RenderTarget
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.SwapChain
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.View
import io.github.erkko68.filament.compose.internal.target.OffscreenTarget
import io.github.erkko68.filament.compose.internal.target.unavailable
import io.github.erkko68.filament.jni.GlxHelper
import java.awt.Window
import org.jetbrains.skia.BackendRenderTarget
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.FramebufferFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.Surface
import org.jetbrains.skia.SurfaceColorFormat
import org.jetbrains.skia.SurfaceOrigin

/**
 * Filament's OpenGL engine shares skiko's GLX context ([GlxEngines]), so it renders into textures
 * skiko's context can see; once the GPU finishes a frame, Skia wraps that texture's framebuffer on
 * skiko's [org.jetbrains.skia.DirectContext] and snapshots it (a GPU-side copy).
 */
internal class GlxOffscreenTarget private constructor(
    private val engine: Engine,
    private val skiko: SkikoGlx,
    private val width: Int,
    private val height: Int,
) : OffscreenTarget {

    private class Slot(
        val textureId: Int,
        /** skiko's framebuffer over [textureId]; framebuffers aren't shared between contexts. */
        val framebufferId: Int,
        val color: Texture,
        val depth: Texture,
        val target: RenderTarget,
    ) {
        val probe = ByteArray(4)

        /** Set once the GPU finished the frame rendered into this slot; may be set off the UI thread. */
        @Volatile var done = false
    }

    // Headless (a pbuffer): beginFrame needs a swap chain, the frames go to the slots' render targets.
    private val swapChain: SwapChain = engine.createSwapChain(width, height, 0L)

    private val slots: List<Slot> = checkNotNull(skiko.withCurrent { createSlots() }) {
        "Couldn't make Compose's GL context current"
    }

    // Rendered slots awaiting GPU completion, oldest first.
    private val inFlight = ArrayDeque<Slot>()

    private fun createSlots(): List<Slot> {
        val ids = List(2) {
            val texture = GlxHelper.nCreateTexture(width, height)
            check(texture != 0) { "Failed to create a ${width}x$height GL texture" }
            val framebuffer = GlxHelper.nCreateFramebuffer(texture)
            check(framebuffer != 0) { "Failed to create a framebuffer over GL texture $texture" }
            texture to framebuffer
        }
        return ids.map { (texture, framebuffer) ->
            val color = Texture.Builder()
                .width(width).height(height)
                .sampler(Texture.Sampler.SAMPLER_2D)
                .format(Texture.InternalFormat.RGBA8)
                .usage(Texture.Usage.COLOR_ATTACHMENT or Texture.Usage.SAMPLEABLE or Texture.Usage.BLIT_SRC)
                .importTexture(texture.toLong())
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
            Slot(texture, framebuffer, color, depth, target)
        }
    }

    override fun renderFrame(renderer: Renderer, view: View, frameTimeNanos: Long): Image? {
        // Delivers finished probes, replacing a per-frame flushAndWait stall.
        engine.flush()
        var finished: Slot? = null
        while (inFlight.firstOrNull()?.done == true) finished = inFlight.removeFirst()
        val image = finished?.let(::snapshot)

        val slot = slots.firstOrNull { it !in inFlight } ?: run {
            // The GL backend checks its GPU fences (the probes) only when it ticks, in beginFrame or
            // skipFrame: without a free slot, skip the frame so the in-flight probes can complete.
            renderer.skipFrame(frameTimeNanos)
            return image
        }
        view.renderTarget = slot.target
        if (renderer.beginFrame(swapChain, frameTimeNanos)) {
            renderer.render(view)
            // The GL backend has no frame-completed callback, but it runs readPixels' callback only
            // once a fence after the frame's commands signals: a 1×1 probe reports GPU completion.
            slot.done = false
            renderer.readPixels(
                slot.target, 0, 0, 1, 1,
                Texture.PixelBufferDescriptor(slot.probe, slot.probe.size, Texture.Format.RGBA, Texture.Type.UBYTE) {
                    slot.done = true
                },
            )
            renderer.endFrame()
            inFlight.addLast(slot)
        }
        return image
    }

    private fun snapshot(slot: Slot): Image? {
        val context = skiko.context ?: return null
        return skiko.withCurrent {
            // Fresh wrappers each frame: Skia caches snapshots of surfaces it didn't draw to.
            val brt = BackendRenderTarget.makeGL(width, height, 0, 0, slot.framebufferId, FramebufferFormat.GR_GL_RGBA8)
            // GL rows run bottom-up; BOTTOM_LEFT makes the snapshot upright.
            val surface = Surface.makeFromBackendRenderTarget(
                context, brt, SurfaceOrigin.BOTTOM_LEFT, SurfaceColorFormat.RGBA_8888, ColorSpace.sRGB,
            )
            val image = surface?.makeImageSnapshot()
            surface?.close()
            brt.close()
            // ponytail: CPU-waits for the copy so Filament can't overwrite the slot mid-copy; a GLsync
            // polled before reusing the slot would avoid it.
            context.flush()
            context.submit(true)
            image
        }
    }

    override fun close() {
        engine.flushAndWait()
        engine.destroySwapChain(swapChain)
        slots.forEach {
            engine.destroyRenderTarget(it.target)
            engine.destroyTexture(it.color)
            engine.destroyTexture(it.depth)
        }
        // Filament's framebuffers must let go of the imported textures before they're deleted.
        engine.flushAndWait()
        skiko.withCurrent {
            slots.forEach { GlxHelper.nDestroy(it.textureId, it.framebufferId) }
        }
    }

    companion object {
        fun create(engine: Engine, window: Window?, width: Int, height: Int): GlxOffscreenTarget {
            val skiko = SkikoGlx.find(window) ?: unavailable("Compose isn't rendering this window with OpenGL")
            if (!GlxEngines.sharesContext(engine, skiko)) {
                unavailable("the engine must come from rememberFilamentEngine() inside the window that shows it")
            }
            return GlxOffscreenTarget(engine, skiko, width, height)
        }
    }
}
