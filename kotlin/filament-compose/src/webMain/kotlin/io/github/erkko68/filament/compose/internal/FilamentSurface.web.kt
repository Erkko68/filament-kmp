@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package io.github.erkko68.filament.compose.internal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.webgl.WebGLRenderTarget
import androidx.compose.ui.platform.webgl.rememberWebGLRenderTarget
import androidx.compose.ui.unit.IntSize
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.NativeSurface
import io.github.erkko68.filament.RenderTarget
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.SwapChain
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.View
import io.github.erkko68.filament.Viewport
import io.github.erkko68.filament.canvas
import io.github.erkko68.filament.sharesContext
import io.github.erkko68.filament.wasm.fila
import io.github.erkko68.filament.wasm.registerGlTexture
import io.github.erkko68.filament.wasm.unregisterGlTexture

/**
 * Filament draws into a Compose [WebGLRenderTarget], on Compose's own WebGL context, and an [Image] shows
 * it: the view is an ordinary Compose layer, alpha included. The engine must have adopted that context,
 * as the one from `rememberFilamentEngine` has.
 */
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
    var size by remember { mutableStateOf(IntSize(1, 1)) }
    var onScreen by remember { mutableStateOf(true) }
    // Null without a Compose window on a WebGL2 canvas (a headless test host): the view stays empty.
    val target = rememberWebGLRenderTarget(size)
    check(target == null || engine.sharesContext) {
        "On web a FilamentView renders on Compose's WebGL context: use the engine from rememberFilamentEngine()"
    }
    val layout = modifier.onGloballyPositioned {
        onScreen = !it.boundsInWindow().isEmpty
        val now = it.size
        if (now.width > 0 && now.height > 0 && now != size) {
            size = now
            onResize(now.width.toDouble() / now.height.toDouble())
        }
    }
    if (target == null) {
        Box(layout)
        return
    }

    val binding = remember(target, engine, view) { TargetBinding(engine, view, target) }
    DisposableEffect(binding) { onDispose { binding.destroy() } }

    val gate = rememberPausedFrameGate(renderingEnabled, 1, binding, size)
    // A view scrolled or clipped out of sight isn't drawn: don't render it either.
    FilamentRenderLoop(onScreen && gate.loopEnabled(renderingEnabled)) {
        var rendered = false
        target.render { rendered = binding.renderFrame(renderer) }
        if (rendered) gate.delivered(paused = !renderingEnabled)
    }

    Box(layout) {
        Image(target.painter, contentDescription = null, contentScale = ContentScale.FillBounds, modifier = Modifier.fillMaxSize())
    }
}

/** The Filament side of a [WebGLRenderTarget]: its texture imported as a [RenderTarget] for [view]. */
private class TargetBinding(private val engine: Engine, private val view: View, private val target: WebGLRenderTarget) {
    private val swapChain: SwapChain = engine.createSwapChain(NativeSurface(checkNotNull(engine.canvas)))
    private var generation = -1
    private var textureId = 0
    private var color: Texture? = null
    private var depth: Texture? = null
    private var renderTarget: RenderTarget? = null

    init {
        // Compose replaces its texture on resize: let go of the import first.
        target.onTextureWillBeInvalidated = ::release
    }

    /** Whether a frame was rendered; Filament skips one when the GPU is behind. */
    fun renderFrame(renderer: Renderer): Boolean {
        if (generation != target.generation) attach()
        val began = renderer.beginFrame(swapChain, Engine.steadyClockTimeNano)
        if (began) {
            renderer.render(view)
            renderer.endFrame()
        }
        // Skia uses the context next: queued first, so Filament's next run drops its cached GL state.
        engine.resetBackendState()
        return began
    }

    private fun attach() {
        release()
        val (w, h) = target.size
        textureId = fila.registerGlTexture(target.webGlTexture)
        val color = Texture.Builder().width(w).height(h).levels(1).sampler(Texture.Sampler.SAMPLER_2D)
            .format(Texture.InternalFormat.RGBA8)
            .usage(Texture.Usage.COLOR_ATTACHMENT or Texture.Usage.SAMPLEABLE)
            .import(textureId.toLong()).build(engine)
        val depth = Texture.Builder().width(w).height(h).levels(1).sampler(Texture.Sampler.SAMPLER_2D)
            .format(Texture.InternalFormat.DEPTH24).usage(Texture.Usage.DEPTH_ATTACHMENT).build(engine)
        val rt = RenderTarget.Builder()
            .texture(RenderTarget.AttachmentPoint.COLOR, color)
            .texture(RenderTarget.AttachmentPoint.DEPTH, depth)
            .build(engine)
        this.color = color
        this.depth = depth
        renderTarget = rt
        view.renderTarget = rt
        view.viewport = Viewport(0, 0, w, h)
        generation = target.generation
    }

    private fun release() {
        renderTarget?.let { view.renderTarget = null; engine.destroy(it) }
        color?.let(engine::destroy)
        depth?.let(engine::destroy)
        if (textureId != 0) fila.unregisterGlTexture(textureId)
        renderTarget = null; color = null; depth = null; textureId = 0
        generation = -1
    }

    fun destroy() {
        target.onTextureWillBeInvalidated = null
        release()
        engine.destroy(swapChain)
        // Runs the destroys now; the engine's onExecuted resyncs Compose's GL state.
        engine.flushAndWait()
    }
}
