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

/**
 * A loaded glTF asset whose GPU resources are ready. Obtain via [rememberGltfAsset].
 *
 * Pass this to one or more [GltfInstance] composables to place copies of the model in the
 * scene without re-importing the asset each time.
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
 * @param bytes Raw glb/glTF binary data. Reloaded only when the *reference* changes.
 */
@Composable
internal fun rememberGltfAsset(engine: Engine, bytes: ByteArray): GltfAsset {
    val gltfioContext = rememberGltfioContext(engine)
    val assetLoader = gltfioContext.assetLoader

    val gltfAsset = remember(bytes, assetLoader) {
        val filamentAsset = requireNotNull(assetLoader.createAsset(bytes)) {
            "AssetLoader.createAsset returned null — check that the bytes are valid glb/glTF"
        }
        GltfAsset(filamentAsset, assetLoader)
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
 * is in the composition. Returns null while the bytes are still loading.
 *
 * Can be called either inside `FilamentView { }` (engine is picked up from
 * [LocalFilamentEngine]) or outside it by hoisting the engine via [rememberFilamentEngine]:
 *
 * ```kotlin
 * // Inside FilamentView — engine implicit
 * GltfInstance(asset = rememberGltfAsset { Res.readBytes("Duck.glb") }, ...)
 *
 * // Outside FilamentView — engine hoisted, asset can outlive any single view
 * val engine = rememberFilamentEngine()
 * val duck   = rememberGltfAsset(engine) { Res.readBytes("Duck.glb") }
 * FilamentView(engine = engine, ...) { GltfInstance(asset = duck, ...) }
 * ```
 *
 * @param engine The Filament engine that owns the asset's GPU resources. Defaults to the
 *   engine in the current composition scope.
 * @param key Reloads the asset when this value changes. Defaults to [Unit] for static assets.
 * @param load Suspend function that produces the raw glb/glTF bytes.
 */
@Composable
fun rememberGltfAsset(
    engine: Engine = LocalFilamentEngine.current,
    key: Any = Unit,
    load: suspend () -> ByteArray,
): GltfAsset? {
    val bytes by produceState<ByteArray?>(initialValue = null, key) {
        value = load()
    }
    return bytes?.let { rememberGltfAsset(engine, it) }
}
