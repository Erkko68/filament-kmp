package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.*
import io.github.erkko68.filament.jni.*
import io.github.erkko68.filament.nativeObject
import io.github.erkko68.filament.InternalFilamentApi

actual class AssetLoader @InternalFilamentApi constructor(internal var nativeHandle: Long) {
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
            FilaAssetLoader_createAsset(nativeHandle, pinned, buffer.size.toLong())
        }
        return handle.takeIf { it != 0L }?.let { FilamentAsset(it) }
    }

    actual fun createInstancedAsset(buffer: ByteArray, instances: Array<FilamentInstance>): FilamentAsset? {
        return buffer.usePinned { pinned ->
            heapScoped {
                val nativeInstances = PtrArray(alloc(instances.size * PtrArray.SIZE))
                val handle = FilaAssetLoader_createInstancedAsset(
                    nativeHandle,
                    pinned,
                    buffer.size.toLong(),
                    nativeInstances.ptr,
                    instances.size.toLong()
                )
                if (handle == 0L) return@heapScoped null
                val asset = FilamentAsset(handle)
                for (i in instances.indices) {
                    instances[i].nativeHandle = nativeInstances[i]
                }
                asset
            }
        }
    }

    actual fun createInstance(asset: FilamentAsset): FilamentInstance? {
        val handle = FilaAssetLoader_createInstance(nativeHandle, asset.nativeHandle).takeIf { it != 0L } ?: return null
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
