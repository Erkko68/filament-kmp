package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.EntityManager

actual class AssetLoader {
    actual fun createAsset(buffer: ByteArray): FilamentAsset? {
        TODO("Not yet implemented")
    }

    actual fun createInstancedAsset(
        buffer: ByteArray,
        instances: Array<FilamentInstance>
    ): FilamentAsset? {
        TODO("Not yet implemented")
    }

    actual fun createInstance(asset: FilamentAsset): FilamentInstance? {
        TODO("Not yet implemented")
    }

    actual fun enableDiagnostics(enable: Boolean) {
    }

    actual fun destroyAsset(asset: FilamentAsset) {
    }

    actual companion object {
        actual fun create(
            engine: Engine,
            materials: MaterialProvider,
            entities: EntityManager?
        ): AssetLoader {
            TODO("Not yet implemented")
        }

        actual fun destroy(loader: AssetLoader) {
        }
    }
}