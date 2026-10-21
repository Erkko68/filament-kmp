package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.RenderTarget
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.View
import io.github.erkko68.filament.compose.internal.imageBitmapOf
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Hoisted handle to a [FilamentView]'s live Filament objects. Create with
 * [rememberFilamentViewState], pass to a [FilamentView], and use it for imperative access —
 * picking, [capture]s, reading the viewport, or reaching the raw [View]/[Renderer] for advanced work.
 *
 * [view] and [renderer] are null until the state is attached to an on-screen [FilamentView],
 * and become null again when that view leaves composition.
 *
 * ```kotlin
 * val viewState = rememberFilamentViewState()
 * FilamentView(scene = scene, viewState = viewState, ...)
 *
 * // Pick on tap — pick() takes Compose coordinates (top-left origin), no flipping needed:
 * Modifier.pointerInput(Unit) {
 *     detectTapGestures { offset ->
 *         viewState.pick(offset.x.toInt(), offset.y.toInt()) { result -> ... }
 *     }
 * }
 * ```
 */
class FilamentViewState internal constructor() {
    var view: View? by mutableStateOf(null)
        internal set
    var renderer: Renderer? by mutableStateOf(null)
        internal set

    /**
     * Issues a Filament picking query at viewport pixel ([x], [y]) **in Compose coordinates**
     * (origin top-left, like pointer-input offsets) and delivers the result to [onResult] on
     * the render thread. The conversion to Filament's bottom-left viewport origin happens
     * internally. No-op while not attached, or for a pixel outside the view. A view paused with
     * `renderingEnabled = false` renders until the result is in, as Filament answers a query by rendering.
     */
    fun pick(x: Int, y: Int, onResult: (View.PickingQueryResult) -> Unit) {
        val v = view ?: return
        val viewport = v.viewport
        val layout = layoutSize.takeIf { it.width > 0 && it.height > 0 } ?: IntSize(viewport.width, viewport.height)
        if (viewport.width <= 0 || viewport.height <= 0) return
        // Filament reads the pixel back unchecked: one outside the viewport aborts the process.
        if (x !in 0 until layout.width || y !in 0 until layout.height) return
        // The surface can lag the layout (desktop debounces a resize) and is stretched over it meanwhile.
        val px = x * viewport.width / layout.width
        val py = y * viewport.height / layout.height
        val answered = readback()
        v.pick(px, viewport.height - 1 - py) {
            answered()
            onResult(it)
        }
    }

    /**
     * The view as it looks now, at its size in pixels: its scene rendered once more off-screen and read back, for
     * saving or sharing. Null while not attached. A view paused with `renderingEnabled = false` renders until the
     * image is in, as Filament reads it back on a later frame. Call it from the main thread, like every Filament call.
     */
    suspend fun capture(): ImageBitmap? {
        val v = view ?: return null
        val r = renderer ?: return null
        val e = engine ?: return null
        val width = v.viewport.width
        val height = v.viewport.height
        if (width <= 0 || height <= 0) return null
        val color = Texture.Builder().width(width).height(height).levels(1).sampler(Texture.Sampler.SAMPLER_2D)
            .format(Texture.InternalFormat.RGBA8).usage(Texture.Usage.COLOR_ATTACHMENT or Texture.Usage.BLIT_SRC).build(e)
        val depth = Texture.Builder().width(width).height(height).levels(1).sampler(Texture.Sampler.SAMPLER_2D)
            .format(Texture.InternalFormat.DEPTH24).usage(Texture.Usage.DEPTH_ATTACHMENT).build(e)
        val target = RenderTarget.Builder().texture(RenderTarget.AttachmentPoint.COLOR, color)
            .texture(RenderTarget.AttachmentPoint.DEPTH, depth).build(e)
        val pixels = ByteArray(width * height * 4)
        // OpenGL reads rows bottom-up, the other backends top-down.
        val bottomUp = e.backend == Engine.Backend.OPENGL
        val answered = readback()
        val previous = v.renderTarget
        v.renderTarget = target
        r.renderStandaloneView(v)
        v.renderTarget = previous
        return suspendCancellableCoroutine { done ->
            // Freed by the callback, not on cancellation: the readback still writes into them.
            r.readPixels(target, 0, 0, width, height, Texture.PixelBufferDescriptor(pixels, pixels.size, Texture.Format.RGBA, Texture.Type.UBYTE) {
                e.destroy(target)
                e.destroy(color)
                e.destroy(depth)
                answered()
                if (done.isActive) done.resume(imageBitmapOf(width, height, if (bottomUp) pixels.flippedRows(width * 4) else pixels))
            })
        }
    }

    /** Picks and captures the attached view hasn't answered yet: it keeps rendering until they're in. */
    internal var pendingReadbacks by mutableIntStateOf(0)
        private set
    private var attachments = 0

    /** Counts a readback in; call the result when it's answered. */
    private fun readback(): () -> Unit {
        val attachment = attachments
        pendingReadbacks++
        // A view destroyed since has answered its readbacks; they aren't the next view's.
        return { if (attachment == attachments) pendingReadbacks-- }
    }

    private var engine: Engine? = null

    /** The view's size in the layout, in pixels. */
    internal var layoutSize = IntSize.Zero

    internal fun attach(engine: Engine, view: View, renderer: Renderer) {
        this.engine = engine
        this.view = view
        this.renderer = renderer
    }

    internal fun detach() {
        this.engine = null
        this.view = null
        this.renderer = null
        attachments++
        pendingReadbacks = 0
    }
}

private fun ByteArray.flippedRows(rowBytes: Int): ByteArray {
    val rows = size / rowBytes
    return ByteArray(size).also { out -> for (row in 0 until rows) copyInto(out, (rows - 1 - row) * rowBytes, row * rowBytes, (row + 1) * rowBytes) }
}

/** Creates and remembers a [FilamentViewState]. */
@Composable
fun rememberFilamentViewState(): FilamentViewState = remember { FilamentViewState() }
