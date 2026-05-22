package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.utils.TextureLoader

/**
 * Asynchronously loads a Filament [Material] and keeps it alive as long as the calling
 * composable is in the composition. Returns null while [load] is still running.
 *
 * Can be called either inside `FilamentView { }` (engine is picked up from
 * [LocalFilamentEngine]) or outside it by hoisting the engine via [rememberFilamentEngine]:
 *
 * ```kotlin
 * // Inside FilamentView — engine implicit
 * rememberMaterial { Res.readBytes("…") }
 *
 * // Outside FilamentView — engine hoisted, can be shared across views
 * val engine = rememberFilamentEngine()
 * val mat    = rememberMaterial(engine) { Res.readBytes("…") }
 * FilamentView(engine = engine, ...) { ... }
 * ```
 *
 * @param engine The Filament engine to allocate the material on. Defaults to the engine in
 *   the current composition scope, which only exists inside `FilamentView { }`.
 * @param key Reloads the material when this value changes. Defaults to [Unit] for static assets.
 * @param load Suspend function that produces the raw `.filamat` bytes.
 */
@Composable
fun rememberMaterial(
    engine: Engine = LocalFilamentEngine.current,
    key: Any = Unit,
    load: suspend () -> ByteArray,
): Material? {
    val bytes by produceState<ByteArray?>(initialValue = null, key) {
        value = load()
    }
    return bytes?.let { rememberMaterial(engine, it) }
}

/**
 * Sync overload used internally by the suspend-lambda version once the bytes are ready.
 * Kept internal because in Compose Multiplatform every realistic byte source ([Res.readBytes],
 * disk, network) is suspending — public callers should use the suspend-lambda overload.
 */
@Composable
internal fun rememberMaterial(engine: Engine, bytes: ByteArray): Material {
    val material = remember(engine, bytes) {
        Material.Builder()
            .payload(bytes)
            .build(engine)
    }

    DisposableEffect(material) {
        onDispose {
            engine.destroyMaterial(material)
        }
    }

    return material
}

/**
 * Asynchronously loads and manages a Filament [Texture]. Returns null while [load] is still
 * running. See [rememberMaterial] for the engine-hoisting pattern when calling outside
 * `FilamentView { }`.
 *
 * @param engine The Filament engine to allocate the texture on. Defaults to the engine in
 *   the current composition scope.
 * @param type Hints the loader about the texture's content (Color, Normal map, etc.).
 * @param key  Reloads the texture when this value changes. Defaults to [Unit] for static assets.
 * @param load Suspend function that produces the raw image bytes.
 */
@Composable
fun rememberTexture(
    engine: Engine = LocalFilamentEngine.current,
    type: TextureLoader.TextureType = TextureLoader.TextureType.COLOR,
    key: Any = Unit,
    load: suspend () -> ByteArray,
): Texture? {
    val bytes by produceState<ByteArray?>(initialValue = null, key) {
        value = load()
    }
    return bytes?.let { rememberTexture(engine, it, type) }
}

/** Sync overload used internally by the suspend-lambda version. See [rememberMaterial]. */
@Composable
internal fun rememberTexture(
    engine: Engine,
    bytes: ByteArray,
    type: TextureLoader.TextureType = TextureLoader.TextureType.COLOR,
): Texture? {
    val texture = remember(engine, bytes, type) {
        TextureLoader.loadTexture(engine, bytes, type)
    }

    DisposableEffect(texture) {
        onDispose {
            texture?.let { engine.destroyTexture(it) }
        }
    }

    return texture
}

/**
 * Creates and manages a [MaterialInstance] from a [Material]. The instance is destroyed when
 * this leaves the composition.
 *
 * @param material The base material to instantiate.
 * @param engine The Filament engine that owns the material. Defaults to the engine in the
 *   current composition scope; pass an explicit engine when calling outside `FilamentView { }`.
 */
@Composable
fun rememberMaterialInstance(
    material: Material,
    engine: Engine = LocalFilamentEngine.current,
): MaterialInstance {
    val instance = remember(material) {
        material.createInstance()
    }

    DisposableEffect(instance) {
        onDispose {
            engine.destroyMaterialInstance(instance)
        }
    }

    return instance
}
