package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.*
import io.github.erkko68.filament.wasm.*
import io.github.erkko68.filament.nativeObject
import io.github.erkko68.filament.InternalFilamentApi

actual class AssetLoader @InternalFilamentApi constructor(internal var nativeHandle: Int) {
    actual companion object {
        actual fun create(engine: Engine, materials: MaterialProvider, entities: EntityManager?): AssetLoader {
            val handle = FilaAssetLoader_create(
                engine.nativeObject,
                materials.nativeObject(),
                entities?.nativeObject ?: 0
            )
            return AssetLoader(handle)
        }

        actual fun destroy(loader: AssetLoader) {
            FilaAssetLoader_destroy(loader.nativeHandle)
            loader.nativeHandle = 0
        }
    }

    actual fun createAsset(buffer: ByteArray): FilamentAsset? {
        val handle = buffer.usePinned { pinned ->
            FilaAssetLoader_createAsset(nativeHandle, pinned, buffer.size)
        }
        return handle.takeIf { it != 0 }?.let { FilamentAsset(it) }
    }

    actual fun createInstancedAsset(buffer: ByteArray, instances: Array<FilamentInstance>): FilamentAsset? {
        return buffer.usePinned { pinned ->
            fila.heapScoped {
                val nativeInstances = I32Array(alloc((instances.size) * 4))
                val handle = FilaAssetLoader_createInstancedAsset(
                    nativeHandle,
                    pinned,
                    buffer.size,
                    nativeInstances.ptr,
                    instances.size
                )
                if (handle == 0) return@heapScoped null
                val asset = FilamentAsset(handle)
                for (i in instances.indices) {
                    instances[i].nativeHandle = nativeInstances[i]
                }
                asset
            }
        }
    }

    actual fun createInstance(asset: FilamentAsset): FilamentInstance? {
        val handle = FilaAssetLoader_createInstance(nativeHandle, asset.nativeHandle).takeIf { it != 0 } ?: return null
        return FilamentInstance(handle)
    }

    actual fun enableDiagnostics(enable: Boolean) {
        FilaAssetLoader_enableDiagnostics(nativeHandle, enable)
    }

    actual fun destroyAsset(asset: FilamentAsset) {
        FilaAssetLoader_destroyAsset(nativeHandle, asset.nativeHandle)
        asset.nativeHandle = 0
    }

    actual fun gc() {
        FilaAssetLoader_gc(nativeHandle)
    }
}
