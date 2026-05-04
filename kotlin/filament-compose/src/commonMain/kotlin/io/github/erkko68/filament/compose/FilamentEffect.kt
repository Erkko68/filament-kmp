package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import io.github.erkko68.filament.Camera
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.Scene
import io.github.erkko68.filament.View
import kotlinx.coroutines.isActive

/**
 * Escape hatch for direct Filament API access inside a [FilamentView].
 *
 * The [block] runs on entry and re-runs whenever any [keys] change, with
 * [DisposableEffect]-style semantics: create resources in [block], destroy them in
 * [FilamentEffectScope.onDispose]. Use [FilamentEffectScope.onFrame] for per-frame work.
 *
 * Example — register a one-off entity and animate a uniform every frame:
 * ```kotlin
 * FilamentEffect {
 *     val entity = engine.getEntityManager().create()
 *     scene.addEntity(entity)
 *     onFrame { nanos ->
 *         // drive a material parameter, transform, etc.
 *     }
 *     onDispose {
 *         scene.removeEntity(entity)
 *         engine.destroyEntity(entity)
 *     }
 * }
 * ```
 */
@Composable
fun FilamentEffect(
    vararg keys: Any?,
    block: FilamentEffectScope.() -> Unit,
) {
    val engine = LocalFilamentEngine.current
    val scene = LocalFilamentScene.current
    val camera = LocalFilamentCamera.current
    val view = LocalFilamentView.current
    val renderer = LocalFilamentRenderer.current

    val scope = remember { FilamentEffectScopeImpl(engine, scene, camera, view, renderer) }

    DisposableEffect(keys = keys) {
        scope.disposeAction = null
        scope.frameAction = null
        scope.block()
        onDispose { scope.disposeAction?.invoke() }
    }

    val frameAction = scope.frameAction
    if (frameAction != null) {
        LaunchedEffect(frameAction) {
            while (isActive) {
                withFrameNanos { nanos -> frameAction(nanos) }
            }
        }
    }
}

interface FilamentEffectScope {
    val engine: Engine
    val scene: Scene
    val camera: Camera
    val view: View
    val renderer: Renderer

    /** Register a callback to run when this effect is disposed (effect re-keyed or composable removed). */
    fun onDispose(block: () -> Unit)

    /**
     * Register a per-frame callback driven by [withFrameNanos]. Replaces any previously
     * registered frame callback for this effect. Pass null indirectly by re-running the
     * effect block without calling this.
     */
    fun onFrame(block: (frameTimeNanos: Long) -> Unit)
}

private class FilamentEffectScopeImpl(
    override val engine: Engine,
    override val scene: Scene,
    override val camera: Camera,
    override val view: View,
    override val renderer: Renderer,
) : FilamentEffectScope {
    var disposeAction: (() -> Unit)? = null
    var frameAction: ((Long) -> Unit)? by mutableStateOf(null)

    override fun onDispose(block: () -> Unit) { disposeAction = block }
    override fun onFrame(block: (Long) -> Unit) { frameAction = block }
}
