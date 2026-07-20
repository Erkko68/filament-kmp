package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament

/**
 * Creates and remembers an [Engine] for the lifetime of the composition.
 *
 * Each call creates its own engine — hoist one call and pass the value around to share an
 * engine across scenes, views, and loaders ([rememberFilamentScene], `rememberGltfAsset`,
 * `rememberKTXEnvironment`, …). If you don't pass an engine, [rememberFilamentScene] and
 * [FilamentSceneView] create a dedicated one scoped to that call site.
 */
@Composable
fun rememberFilamentEngine(backend: Engine.Backend = Engine.Backend.DEFAULT): Engine {
    val engine = remember(backend) { Filament.init(); Engine.create(backend) }
    DisposableEffect(engine) {
        onDispose { engine.destroy() }
    }
    return engine
}
