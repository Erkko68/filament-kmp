package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Scene
import kotlinx.coroutines.isActive

/**
 * Escape hatch for direct Filament API access inside [rememberFilamentScene]. Use it to add
 * custom renderables to the scene or drive per-frame updates that the scene composables don't
 * cover. For per-view, imperative access (picking, the raw `View`/`Renderer`), use
 * [FilamentViewState] instead; for the camera, use [io.github.erkko68.filament.compose.scene.CameraState].
 *
 * The [block] runs on entry and re-runs whenever any [keys] change, with [DisposableEffect]
 * semantics: create resources in [block], destroy them in [FilamentEffectScope.onDispose]. Use
 * [FilamentEffectScope.onFrame] for per-frame work.
 *
 * ```kotlin
 * rememberFilamentScene {
 *     FilamentEffect {
 *         val entity = engine.getEntityManager().create()
 *         scene.addEntity(entity)
 *         onFrame { nanos -> /* drive a material parameter, transform, etc. */ }
 *         onDispose {
 *             scene.removeEntity(entity)
 *             engine.getEntityManager().destroy(entity)
 *         }
 *     }
 * }
 * ```
 */
@Composable
fun FilamentSceneScope.FilamentEffect(
    vararg keys: Any?,
    block: FilamentEffectScope.() -> Unit,
) {
    val engine = LocalFilamentEngine.current
    val scene = LocalFilamentScene.current

    val scope = remember { FilamentEffectScopeImpl(engine, scene) }

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

    /** Register a callback to run when this effect is disposed (effect re-keyed or removed). */
    fun onDispose(block: () -> Unit)

    /**
     * Register a per-frame callback driven by [withFrameNanos]. Replaces any previously
     * registered frame callback for this effect.
     */
    fun onFrame(block: (frameTimeNanos: Long) -> Unit)
}

private class FilamentEffectScopeImpl(
    override val engine: Engine,
    override val scene: Scene,
) : FilamentEffectScope {
    var disposeAction: (() -> Unit)? = null
    var frameAction: ((Long) -> Unit)? by mutableStateOf(null)

    override fun onDispose(block: () -> Unit) { disposeAction = block }
    override fun onFrame(block: (Long) -> Unit) { frameAction = block }
}
