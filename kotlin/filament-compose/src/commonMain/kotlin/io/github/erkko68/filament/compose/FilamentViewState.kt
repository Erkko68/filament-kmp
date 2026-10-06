package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntSize
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.View

/**
 * Hoisted handle to a [FilamentView]'s live Filament objects. Create with
 * [rememberFilamentViewState], pass to a [FilamentView], and use it for imperative access —
 * picking, reading the viewport, or reaching the raw [View]/[Renderer] for advanced work.
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
        val attachment = attachments
        pendingPicks++
        v.pick(px, viewport.height - 1 - py) {
            // A view destroyed since has answered its queries; they aren't the next view's.
            if (attachment == attachments) pendingPicks--
            onResult(it)
        }
    }

    /** Queries sent to the attached view and not answered yet. */
    internal var pendingPicks by mutableIntStateOf(0)
        private set
    private var attachments = 0

    /** The view's size in the layout, in pixels. */
    internal var layoutSize = IntSize.Zero

    internal fun attach(view: View, renderer: Renderer) {
        this.view = view
        this.renderer = renderer
    }

    internal fun detach() {
        this.view = null
        this.renderer = null
        attachments++
        pendingPicks = 0
    }
}

/** Creates and remembers a [FilamentViewState]. */
@Composable
fun rememberFilamentViewState(): FilamentViewState = remember { FilamentViewState() }
