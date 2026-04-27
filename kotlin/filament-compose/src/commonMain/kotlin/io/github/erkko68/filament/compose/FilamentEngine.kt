package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament

/**
 * Creates and remembers a [Engine] for the lifetime of the composition.
 *
 * Use this when you want to share one engine across multiple [FilamentView] instances.
 * If you only have one view, you can omit this and let [FilamentView] manage its own engine.
 */
@Composable
fun rememberFilamentEngine(backend: Engine.Backend = Engine.Backend.DEFAULT): Engine {
    val engine = remember(backend) { Filament.init(); Engine.create(backend) }
    DisposableEffect(engine) {
        onDispose { engine.destroy() }
    }
    return engine
}
