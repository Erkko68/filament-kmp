package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import io.github.erkko68.filament.Camera
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.Scene
import io.github.erkko68.filament.View

/**
 * Escape hatch for direct Filament API access inside a [FilamentView].
 *
 * The [block] receives all core Filament objects. Use [DisposableEffect]-style semantics:
 * create resources in [block], destroy them in the returned [FilamentEffectScope.onDispose].
 *
 * Example:
 * ```
 * FilamentEffect { engine, scene, _, _, _ ->
 *     val entity = engine.getEntityManager().create()
 *     scene.addEntity(entity)
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

    DisposableEffect(keys = keys) {
        val scope = FilamentEffectScopeImpl(engine, scene, camera, view, renderer)
        scope.block()
        onDispose { scope.disposeAction?.invoke() }
    }
}

interface FilamentEffectScope {
    val engine: Engine
    val scene: Scene
    val camera: Camera
    val view: View
    val renderer: Renderer
    fun onDispose(block: () -> Unit)
}

private class FilamentEffectScopeImpl(
    override val engine: Engine,
    override val scene: Scene,
    override val camera: Camera,
    override val view: View,
    override val renderer: Renderer,
) : FilamentEffectScope {
    var disposeAction: (() -> Unit)? = null
    override fun onDispose(block: () -> Unit) { disposeAction = block }
}
