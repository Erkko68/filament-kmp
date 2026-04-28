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
import io.github.erkko68.filament.compose.LocalAssetLoader
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
 * Loads a glTF/glb asset once and keeps it alive as long as the calling composable is in the
 * composition. Pass the returned [GltfAsset] to [GltfInstance] to place one or more copies of
 * the model in the scene.
 *
 * For async loading (resources, network) prefer the suspend-lambda overload:
 * `rememberGltfAsset { Res.readBytes("model.glb") }`.
 *
 * @param bytes Raw glb/glTF binary data. The asset is reloaded only when the reference changes.
 */
@Composable
fun rememberGltfAsset(bytes: ByteArray): GltfAsset {
    val engine = LocalFilamentEngine.current
    val assetLoader = LocalAssetLoader.current

    val gltfAsset = remember(bytes) {
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
 * Typical usage:
 * ```kotlin
 * GltfInstance(
 *     asset = rememberGltfAsset { Res.readBytes("files/models/Duck.glb") },
 *     position = Position(0f, 0f, 0f),
 * )
 * ```
 *
 * @param key Reloads the asset when this value changes. Defaults to [Unit] for static assets.
 * @param load Suspend function that produces the raw glb/glTF bytes.
 */
@Composable
fun rememberGltfAsset(key: Any = Unit, load: suspend () -> ByteArray): GltfAsset? {
    val bytes by produceState<ByteArray?>(initialValue = null, key) {
        value = load()
    }
    return bytes?.let { rememberGltfAsset(it) }
}
