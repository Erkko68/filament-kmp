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
 * // Pick on tap (Filament's viewport origin is bottom-left, so flip Y):
 * Modifier.pointerInput(Unit) {
 *     detectTapGestures { offset ->
 *         val v = viewState.view ?: return@detectTapGestures
 *         viewState.pick(offset.x.toInt(), v.viewport.height - offset.y.toInt()) { result -> ... }
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
     * Issues a Filament picking query at viewport pixel ([x], [y]) and delivers the result to
     * [onResult] on the render thread. No-op while not attached. Note Filament's viewport
     * origin is bottom-left — flip Compose's top-left Y against `view.viewport.height`.
     */
    fun pick(x: Int, y: Int, onResult: (View.PickingQueryResult) -> Unit) {
        view?.pick(x, y, onResult)
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
