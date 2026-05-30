package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.utils.TextureLoader
import kotlin.coroutines.cancellation.CancellationException

/**
 * Asynchronously loads a Filament [Material] and keeps it alive as long as the calling
 * composable is in the composition. Returns null while loading and on failure — it never
 * throws inside composition. Pass [onError] to react when [load] throws (missing file,
 * network error).
 *
 * Can be called either inside `rememberFilamentScene { }` (engine is picked up from
 * [LocalFilamentEngine]) or outside it by hoisting the engine via [rememberFilamentEngine]:
 *
 * ```kotlin
 * // Inside rememberFilamentScene — engine implicit
 * rememberMaterial { Res.readBytes("…") }
 *
 * // Outside a scene — engine hoisted, can be shared across scenes
 * val engine = rememberFilamentEngine()
 * val mat    = rememberMaterial(engine) { Res.readBytes("…") }
 * val scene  = rememberFilamentScene(engine = engine) { ... }
 * ```
 *
 * @param engine The Filament engine to allocate the material on. Defaults to the engine in
 *   the current composition scope, which only exists inside `rememberFilamentScene { }`.
 * @param key Reloads the material when this value changes. Defaults to [Unit] for static assets.
 * @param onError Invoked once if [load] throws. The material stays null.
 * @param load Suspend function that produces the raw `.filamat` bytes.
 */
@Composable
fun rememberMaterial(
    engine: Engine = LocalFilamentEngine.current,
    key: Any = Unit,
    onError: ((Throwable) -> Unit)? = null,
    load: suspend () -> ByteArray,
): Material? {
    val bytes by produceState<ByteArray?>(initialValue = null, key) {
        value = try {
            load()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            onError?.invoke(e)
            null
        }
    }
    return bytes?.let { rememberMaterial(engine, it, onError) }
}

/**
 * Sync overload used internally by the suspend-lambda version once the bytes are ready.
 * Kept internal because in Compose Multiplatform every realistic byte source ([Res.readBytes],
 * disk, network) is suspending — public callers should use the suspend-lambda overload.
 *
 * Returns null (rather than throwing inside composition) when the bytes aren't a valid compiled
 * `.filamat` payload, invoking [onError] once with the failure.
 */
@Composable
internal fun rememberMaterial(
    engine: Engine,
    bytes: ByteArray,
    onError: ((Throwable) -> Unit)? = null,
): Material? {
    val material = remember(engine, bytes) {
        try {
            Material.Builder().payload(bytes).build(engine)
        } catch (e: Exception) {
            null
        }
    }

    if (material == null) {
        LaunchedEffect(bytes) {
            onError?.invoke(
                IllegalArgumentException("Failed to build material — the bytes are not a valid compiled .filamat payload"),
            )
        }
        return null
    }

    DisposableEffect(material) {
        onDispose {
            engine.destroyMaterial(material)
        }
    }

    return material
}

/**
 * Asynchronously loads and manages a Filament [Texture]. Returns null while loading and on
 * failure — it never throws inside composition. Pass [onError] to react when [load] throws
 * (missing file, network error) or the bytes can't be decoded on this platform. See
 * [rememberMaterial] for the engine-hoisting pattern when calling outside `rememberFilamentScene { }`.
 *
 * @param engine The Filament engine to allocate the texture on. Defaults to the engine in
 *   the current composition scope.
 * @param type Hints the loader about the texture's content (Color, Normal map, etc.).
 * @param key  Reloads the texture when this value changes. Defaults to [Unit] for static assets.
 * @param onError Invoked once if [load] throws or the image can't be decoded. The texture stays null.
 * @param load Suspend function that produces the raw image bytes.
 */
@Composable
fun rememberTexture(
    engine: Engine = LocalFilamentEngine.current,
    type: TextureLoader.TextureType = TextureLoader.TextureType.COLOR,
    key: Any = Unit,
    onError: ((Throwable) -> Unit)? = null,
    load: suspend () -> ByteArray,
): Texture? {
    val bytes by produceState<ByteArray?>(initialValue = null, key) {
        value = try {
            load()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            onError?.invoke(e)
            null
        }
    }
    return bytes?.let { rememberTexture(engine, it, type, onError) }
}

/** Sync overload used internally by the suspend-lambda version. See [rememberMaterial]. */
@Composable
internal fun rememberTexture(
    engine: Engine,
    bytes: ByteArray,
    type: TextureLoader.TextureType = TextureLoader.TextureType.COLOR,
    onError: ((Throwable) -> Unit)? = null,
): Texture? {
    val texture = remember(engine, bytes, type) {
        TextureLoader.loadTexture(engine, bytes, type)
    }

    if (texture == null) {
        LaunchedEffect(bytes, type) {
            onError?.invoke(
                IllegalArgumentException("Failed to decode image — unsupported or corrupt data for this platform"),
            )
        }
        return null
    }

    DisposableEffect(texture) {
        onDispose {
            engine.destroyTexture(texture)
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
 *   current composition scope; pass an explicit engine when calling outside `rememberFilamentScene { }`.
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
