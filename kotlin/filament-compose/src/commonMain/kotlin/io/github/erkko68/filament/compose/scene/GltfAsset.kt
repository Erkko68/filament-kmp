package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
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
)

/**
 * Loads a glTF/glb asset once and keeps it alive as long as the calling composable is in the
 * composition. Pass the returned [GltfAsset] to [GltfInstance] to place one or more copies of
 * the model in the scene.
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
        val resourceLoader = ResourceLoader(engine, true)
        resourceLoader.loadResources(gltfAsset.filamentAsset)
        onDispose {
            assetLoader.destroyAsset(gltfAsset.filamentAsset)
            resourceLoader.destroy()
        }
    }

    return gltfAsset
}
