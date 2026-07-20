package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
     * internally. No-op while not attached.
     */
    fun pick(x: Int, y: Int, onResult: (View.PickingQueryResult) -> Unit) {
        val v = view ?: return
        v.pick(x, v.viewport.height - y, onResult)
    }

    internal fun attach(view: View, renderer: Renderer) {
        this.view = view
        this.renderer = renderer
    }

    internal fun detach() {
        this.view = null
        this.renderer = null
    }
}

/** Creates and remembers a [FilamentViewState]. */
@Composable
fun rememberFilamentViewState(): FilamentViewState = remember { FilamentViewState() }
