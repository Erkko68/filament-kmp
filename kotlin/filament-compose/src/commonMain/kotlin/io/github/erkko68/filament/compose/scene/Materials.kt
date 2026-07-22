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
 * val mat    = rememberMaterial(engine = engine) { Res.readBytes("…") }
 * val scene  = rememberFilamentScene(engine = engine) { ... }
 * ```
 *
 * @param key Reloads the material when this value changes. Defaults to [Unit] for static assets.
 * @param onError Invoked once if [load] throws. The material stays null.
 * @param engine The Filament engine to allocate the material on. Defaults to the engine in
 *   the current composition scope, which only exists inside `rememberFilamentScene { }`.
 * @param load Suspend function that produces the raw `.filamat` bytes.
 */
@Composable
fun rememberMaterial(
    key: Any = Unit,
    onError: ((Throwable) -> Unit)? = null,
    engine: Engine = LocalFilamentEngine.current,
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
        } catch (e: Throwable) {
            // The JS/embind backend throws native (non-Kotlin-Exception) errors on a bad or
            // unsupported payload; catch broadly so a failed build never crashes the app.
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
 * @param type Hints the loader about the texture's content (Color, Normal map, etc.).
 * @param key  Reloads the texture when this value changes. Defaults to [Unit] for static assets.
 * @param onError Invoked once if [load] throws or the image can't be decoded. The texture stays null.
 * @param engine The Filament engine to allocate the texture on. Defaults to the engine in
 *   the current composition scope.
 * @param load Suspend function that produces the raw image bytes.
 */
@Composable
fun rememberTexture(
    type: TextureLoader.TextureType = TextureLoader.TextureType.COLOR,
    key: Any = Unit,
    onError: ((Throwable) -> Unit)? = null,
    engine: Engine = LocalFilamentEngine.current,
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
 * Accepts a nullable [material] so it chains directly off [rememberMaterial], which returns
 * null while loading; the result is null exactly while [material] is null. Primitives accept a
 * nullable material and simply don't render until it arrives, so the whole chain needs no
 * unwrapping:
 *
 * ```kotlin
 * val mat = rememberMaterial { Res.readBytes("files/materials/lit_color.filamat") }
 * Cube(material = rememberMaterialInstance(mat))   // renders once the material loads
 * ```
 *
 * @param material The base material to instantiate, or null while it is still loading.
 * @param engine The Filament engine that owns the material. Defaults to the engine in the
 *   current composition scope; pass an explicit engine when calling outside `rememberFilamentScene { }`.
 */
@Composable
fun rememberMaterialInstance(
    material: Material?,
    engine: Engine = LocalFilamentEngine.current,
): MaterialInstance? {
    val instance = remember(material) {
        material?.createInstance()
    }

    if (instance != null) {
        DisposableEffect(instance) {
            onDispose {
                engine.destroyMaterialInstance(instance)
            }
        }
    }

    return instance
}

/**
 * Creates a [MaterialInstance] and (re-)applies [configure] declaratively — the imperative
 * `createInstance()` + `setParameter(...)` + dispose dance done for you. The instance is built
 * once per [material]; [configure] runs once on creation and again whenever any value in [keys]
 * changes, so parameters track Compose state without an `onUpdate`/`SideEffect`. The instance is
 * destroyed when this leaves the composition.
 *
 * Accepts a nullable [material] so it chains directly off [rememberMaterial] (null while
 * loading); the result is null exactly while [material] is null, and primitives accept a
 * nullable material:
 *
 * ```kotlin
 * val mat = rememberMaterial { Res.readBytes("files/materials/lit_color.filamat") }
 * val mi  = rememberMaterialInstance(mat, color) { setParameter("baseColor", color) }
 * Cube(material = mi)
 * ```
 *
 * Because the same instance is updated in place (never swapped), it is safe to keep referenced
 * by a renderable while [keys] change — unlike allocating a fresh instance per colour, which can
 * crash the render thread mid-frame (see docs/compose/materials.md).
 *
 * @param material The base material to instantiate, or null while it is still loading.
 * @param keys Inputs that, when changed, re-run [configure]. Pass every value [configure] reads.
 * @param engine The Filament engine that owns the material. Defaults to the engine in the current
 *   composition scope; pass an explicit engine when calling outside `rememberFilamentScene { }`.
 * @param configure Applies parameters to the instance. Re-invoked on creation and on [keys] change.
 */
@Composable
fun rememberMaterialInstance(
    material: Material?,
    vararg keys: Any?,
    engine: Engine = LocalFilamentEngine.current,
    configure: MaterialInstance.() -> Unit,
): MaterialInstance? {
    if (material == null) return null
    return rememberConfiguredMaterialInstance(material, *keys, engine = engine, configure = configure)
}

/**
 * Non-null core of the configure-style [rememberMaterialInstance]: the standard-material helpers
 * chain off the always-available embedded materials, so routing them here keeps their non-null
 * return type structural instead of asserted with `!!` at every call site.
 */
@Composable
internal fun rememberConfiguredMaterialInstance(
    material: Material,
    vararg keys: Any?,
    engine: Engine = LocalFilamentEngine.current,
    configure: MaterialInstance.() -> Unit,
): MaterialInstance {
    val instance = remember(material) {
        material.createInstance()
    }

    // Re-apply parameters whenever the instance is (re)built or any key changes. A no-op
    // onDispose keeps this a pure setter — the instance's own teardown is the effect below.
    DisposableEffect(instance, *keys) {
        instance.configure()
        onDispose { }
    }

    DisposableEffect(instance) {
        onDispose {
            engine.destroyMaterialInstance(instance)
        }
    }

    return instance
}

/** Sets a `float3` parameter from a [Color], keeping call sites typed against the colour value class. */
fun MaterialInstance.setParameter(name: String, color: Color) =
    setParameter(name, color.r, color.g, color.b)
