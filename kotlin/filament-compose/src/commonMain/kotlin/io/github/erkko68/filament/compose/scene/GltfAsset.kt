package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.gltfio.AssetLoader
import io.github.erkko68.filament.gltfio.FilamentAsset
import io.github.erkko68.filament.gltfio.ResourceLoader
import kotlin.coroutines.cancellation.CancellationException

/**
 * A loaded glTF asset whose GPU resources are ready. Obtain via [rememberGltfAsset].
 *
 * Pass this to one or more [GltfInstance] composables to place copies of the model in the
 * scene without re-importing the asset each time.
 *
 * ### Instance lifetime
 * `gltfio` has no API to destroy a single instance — every [GltfInstance] created from this
 * asset stays alive until the asset itself is destroyed (i.e. until the [rememberGltfAsset]
 * call that produced it leaves composition). Reusing one asset across many instances is cheap,
 * but avoid churning hundreds of short-lived instances from a single long-lived asset.
 */
class GltfAsset internal constructor(
    internal val filamentAsset: FilamentAsset,
    internal val assetLoader: AssetLoader,
) {
    /**
     * True when all GPU resources (textures, buffers) have been uploaded.
     * [GltfInstance] uses this to avoid rendering incomplete models.
     */
    var isReady by mutableStateOf(false)
        internal set
}

/**
 * Sync overload used internally by the suspend-lambda version once the bytes are ready.
 * Kept internal because in Compose Multiplatform every realistic byte source ([Res.readBytes],
 * disk, network) is suspending — public callers should use the suspend-lambda overload.
 *
 * Returns null (rather than throwing inside composition) when the bytes don't parse as
 * glb/glTF, invoking [onError] once with the failure.
 *
 * @param bytes Raw glb/glTF binary data. Reloaded only when the *reference* changes.
 */
@Composable
internal fun rememberGltfAsset(
    engine: Engine,
    bytes: ByteArray,
    onError: ((Throwable) -> Unit)? = null,
): GltfAsset? {
    val gltfioContext = rememberGltfioContext(engine)
    val assetLoader = gltfioContext.assetLoader

    val gltfAsset = remember(bytes, assetLoader) {
        assetLoader.createAsset(bytes)?.let { GltfAsset(it, assetLoader) }
    }

    if (gltfAsset == null) {
        // Bytes were produced but failed to parse — report once, keyed on the bytes.
        LaunchedEffect(bytes, assetLoader) {
            onError?.invoke(
                IllegalArgumentException("Failed to parse glTF/glb — the bytes are not valid glb/glTF data"),
            )
        }
        return null
    }

    DisposableEffect(gltfAsset) {
        onDispose {
            assetLoader.destroyAsset(gltfAsset.filamentAsset)
        }
    }

    LaunchedEffect(gltfAsset) {
        val resourceLoader = ResourceLoader(engine, true)
        try {
            resourceLoader.asyncBeginLoad(gltfAsset.filamentAsset)
            while (resourceLoader.asyncGetLoadProgress() < 1.0f) {
                resourceLoader.asyncUpdateLoad()
                withFrameNanos { }
            }
            gltfAsset.isReady = true
        } finally {
            resourceLoader.destroy()
        }
    }

    return gltfAsset
}

/**
 * Asynchronously loads a glTF/glb asset and keeps it alive as long as the calling composable
 * is in the composition.
 *
 * Returns null while loading **and** on failure — it never throws inside composition, so a
 * bad model can't crash the app. Pass [onError] to react to failures (show a placeholder,
 * log, retry). Both failure modes are reported: the [load] lambda throwing (missing file,
 * network error) and the bytes failing to parse as glb/glTF.
 *
 * Can be called either inside `rememberFilamentScene { }` (engine is picked up from
 * [LocalFilamentEngine]) or outside it by hoisting the engine via [rememberFilamentEngine]:
 *
 * ```kotlin
 * // Inside rememberFilamentScene — engine implicit
 * GltfInstance(asset = rememberGltfAsset { Res.readBytes("Duck.glb") }, ...)
 *
 * // Outside a scene — engine hoisted, asset can outlive any single scene
 * val engine = rememberFilamentEngine()
 * val duck   = rememberGltfAsset(engine) { Res.readBytes("Duck.glb") }
 * val scene  = rememberFilamentScene(engine = engine) { GltfInstance(asset = duck, ...) }
 *
 * // Distinguishing loading from failure: null + a captured error
 * var error by remember { mutableStateOf<Throwable?>(null) }
 * val model = rememberGltfAsset(onError = { error = it }) { loadFromNetwork() }
 * when {
 *     error != null -> ErrorPlaceholder()       // failed
 *     model == null -> LoadingSpinner()          // still loading
 *     else          -> GltfInstance(asset = model)
 * }
 * ```
 *
 * Each [GltfInstance] created from the returned asset lives until the asset is destroyed
 * (when this call leaves composition) — see [GltfAsset].
 *
 * @param engine The Filament engine that owns the asset's GPU resources. Defaults to the
 *   engine in the current composition scope.
 * @param key Reloads the asset when this value changes. Defaults to [Unit] for static assets.
 * @param onError Invoked once if [load] throws or the bytes don't parse. The asset stays null.
 * @param load Suspend function that produces the raw glb/glTF bytes.
 */
@Composable
fun rememberGltfAsset(
    engine: Engine = LocalFilamentEngine.current,
    key: Any = Unit,
    onError: ((Throwable) -> Unit)? = null,
    load: suspend () -> ByteArray,
): GltfAsset? {
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
    return bytes?.let { rememberGltfAsset(engine, it, onError) }
}
