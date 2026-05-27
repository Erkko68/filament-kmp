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

/**
 * Default engine used by [FilamentView] when the caller does not pass one in.
 *
 * On JVM this defers Engine creation until Skiko's on-screen renderer is up so the
 * Engine can be initialized with that renderer's GPU device/context as its
 * shared context. That makes GPU textures created by Filament directly visible to
 * Skia (no readback). While the renderer is still booting this returns null —
 * [FilamentView] short-circuits until it becomes available.
 *
 * On Android / iOS / JS this is just an eager [rememberFilamentEngine] wrapper.
 */
@Composable
internal expect fun rememberDefaultViewEngine(backend: Engine.Backend = Engine.Backend.DEFAULT): Engine?
