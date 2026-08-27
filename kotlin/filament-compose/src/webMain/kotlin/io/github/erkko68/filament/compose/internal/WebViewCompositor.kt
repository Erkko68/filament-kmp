package io.github.erkko68.filament.compose.internal

import androidx.compose.ui.unit.IntRect
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.NativeSurface
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.SwapChain
import io.github.erkko68.filament.View
import io.github.erkko68.filament.Viewport
import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.math.max
import kotlin.math.roundToInt
import io.github.erkko68.filament.canvas

/**
 * On web a Filament [Engine] is bound to a single WebGL context/canvas — `createSwapChain` takes no
 * canvas and `readPixels` isn't bound — so each `FilamentView` cannot own its own GL surface.
 *
 * So every view of one engine shares this compositor. It renders each registered view into its own
 * region of the engine's (offscreen) canvas in a single frame, then `drawImage`-blits each region
 * onto that view's own 2D canvas. Blitting straight from the WebGL canvas needs no readback API and
 * stays on the GPU. The blit clears first, so a translucent view keeps its alpha. Each view's 2D
 * canvas is displayed through the normal Compose HTML-interop path (see `FilamentSurface`).
 */
internal class WebViewCompositor private constructor(private val engine: Engine) {

    /** A view, its destination 2D canvas, and its current window-space bounds (Compose px). */
    class Entry(val view: View, val target: HTMLCanvasElement) {
        var rect: IntRect = IntRect(0, 0, 0, 0)
        val ctx: CanvasRenderingContext2D? = target.getContext("2d") as? CanvasRenderingContext2D
        /**
         * Defensive guard skipped over in [renderFrame]. Teardown is synchronous — [unregister]
         * removes the entry (and [stop]s the RAF loop) before the owning FilamentView destroys the
         * view — so in practice the loop never iterates a disposed entry. Kept so a future change to
         * the disposal-effect ordering can't render or blit a view that's already been destroyed.
         */
        var disposed: Boolean = false
    }

    private val canvas: HTMLCanvasElement = engine.canvas
        ?: error("WebViewCompositor requires an Engine created with a canvas")
    // The engine canvas is offscreen, so the browser's implicit post-composite clear may never run
    // (it doesn't on Android Chrome) and frames accumulate. Clear it ourselves every beginFrame.
    private val renderer: Renderer = engine.createRenderer().apply {
        clearOptions = Renderer.ClearOptions().apply {
            clearColor = doubleArrayOf(0.0, 0.0, 0.0, 0.0)
            clear = true
        }
    }
    private val entries = ArrayList<Entry>()
    private var swapChain: SwapChain? = null
    private var rafId: Int = 0
    private var running = false

    private val frameCallback: (Double) -> Unit = {
        if (running) {
            renderFrame()
            rafId = window.requestAnimationFrame(frameCallback)
        }
    }

    fun register(view: View, target: HTMLCanvasElement): Entry {
        val entry = Entry(view, target)
        entries.add(entry)
        if (!running) start()
        return entry
    }

    fun unregister(entry: Entry) {
        entry.disposed = true
        entries.remove(entry)
        if (entries.isEmpty()) destroy()
    }

    private fun start() {
        running = true
        rafId = window.requestAnimationFrame(frameCallback)
    }

    private fun stop() {
        running = false
        if (rafId != 0) {
            window.cancelAnimationFrame(rafId)
            rafId = 0
        }
    }

    /**
     * Full teardown: stop the RAF loop, destroy engine-owned resources (swap-chain, renderer),
     * and remove this compositor from the static [instances] map so a fresh one is created if
     * the engine is reused later.
     */
    private fun destroy() {
        stop()
        swapChain?.let { engine.destroySwapChain(it) }
        swapChain = null
        engine.destroyRenderer(renderer)
        instances.remove(engine)
    }

    private fun renderFrame() {
        if (entries.isEmpty()) return

        val dpr = window.devicePixelRatio.coerceAtLeast(1.0)

        // The offscreen canvas must span every view's window rect so each can be rendered into its
        // own slice in one frame; views sit at distinct screen positions so they don't overlap.
        var unionW = 0
        var unionH = 0
        for (e in entries) {
            val physRight = (e.rect.right * dpr).roundToInt()
            val physBottom = (e.rect.bottom * dpr).roundToInt()
            unionW = max(unionW, physRight)
            unionH = max(unionH, physBottom)
        }
        if (unionW <= 0 || unionH <= 0) return
        if (canvas.width != unionW || canvas.height != unionH) {
            canvas.width = unionW
            canvas.height = unionH
        }

        val sc = swapChain ?: engine.createSwapChain(NativeSurface(canvas)).also { swapChain = it }
        if (renderer.beginFrame(sc, Engine.getSteadyClockTimeNano())) {
            for (e in entries) {
                if (e.disposed) continue
                val r = e.rect
                if (r.width <= 0 || r.height <= 0) continue
                val physLeft = (r.left * dpr).roundToInt()
                val physTop = (r.top * dpr).roundToInt()
                val physRight = (r.right * dpr).roundToInt()
                val physBottom = (r.bottom * dpr).roundToInt()
                val physWidth = physRight - physLeft
                val physHeight = physBottom - physTop
                if (physWidth <= 0 || physHeight <= 0) continue

                // Compose rect is top-left origin; Filament viewport origin is bottom-left.
                e.view.viewport = Viewport(physLeft, unionH - (physTop + physHeight), physWidth, physHeight)
                renderer.render(e.view)
            }
            renderer.endFrame()
        }

        // Blit each view's slice onto its own canvas, before the browser composites/clears the GL
        // drawing buffer. The GL canvas reads top-left origin as an image source, so srcY == physTop.
        for (e in entries) {
            if (e.disposed) continue
            val r = e.rect
            val ctx = e.ctx ?: continue
            if (r.width <= 0 || r.height <= 0) continue
            val physLeft = (r.left * dpr).roundToInt()
            val physTop = (r.top * dpr).roundToInt()
            val physRight = (r.right * dpr).roundToInt()
            val physBottom = (r.bottom * dpr).roundToInt()
            val physWidth = physRight - physLeft
            val physHeight = physBottom - physTop
            if (physWidth <= 0 || physHeight <= 0) continue

            if (e.target.width != physWidth || e.target.height != physHeight) {
                e.target.width = physWidth
                e.target.height = physHeight
            }
            ctx.clearRect(0.0, 0.0, physWidth.toDouble(), physHeight.toDouble())
            ctx.drawImage(
                canvas,
                physLeft.toDouble(), physTop.toDouble(), physWidth.toDouble(), physHeight.toDouble(),
                0.0, 0.0, physWidth.toDouble(), physHeight.toDouble(),
            )
        }
    }

    companion object {
        private val instances = HashMap<Engine, WebViewCompositor>()

        /** The compositor for [engine], created on first use. One per engine (one canvas). */
        fun of(engine: Engine): WebViewCompositor =
            instances.getOrPut(engine) { WebViewCompositor(engine) }
    }
}
