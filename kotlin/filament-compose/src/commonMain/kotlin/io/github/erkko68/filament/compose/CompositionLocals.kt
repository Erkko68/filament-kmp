package io.github.erkko68.filament.compose

import androidx.compose.runtime.staticCompositionLocalOf
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Scene

/**
 * The [Engine] backing the enclosing [rememberFilamentScene], or null outside one.
 *
 * Read it to give your own scene composables the same `engine` default the built-in loaders use:
 *
 * ```kotlin
 * @Composable
 * fun rememberMyResource(
 *     engine: Engine = LocalFilamentEngine.current ?: error("no engine in scope"),
 * ): MyResource = ...
 * ```
 *
 * For the [Scene] itself — adding raw entities, per-frame work — use [FilamentEffect], which
 * hands you both the engine and the scene and manages the effect's lifetime.
 */
// staticCompositionLocalOf, not compositionLocalOf: the engine never changes within a scene
// subtree, so tracking every read (this is read by every loader, light and primitive) buys
// nothing, and the one case that does change it — a new engine — must rebuild the subtree anyway.
val LocalFilamentEngine = staticCompositionLocalOf<Engine?> { null }

// Internal for the same reason FilamentScene.scene is: FilamentEffect is the supported way to
// reach the raw Scene, and it disposes what you add to it.
internal val LocalFilamentScene = staticCompositionLocalOf<Scene?> { null }

// Null (not error()) defaults so resource loaders can take `engine = LocalFilamentEngine.current`
// as a default argument and report the failure themselves, at the call site.
internal fun noFilamentEngine(): Nothing =
    error("No FilamentEngine in scope — declare scene content inside rememberFilamentScene { }, or pass engine = …")

internal fun noFilamentScene(): Nothing =
    error("No FilamentScene in scope — declare scene content inside rememberFilamentScene { }")
