@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament.gltfio

import kotlinx.cinterop.*
import io.github.erkko68.filament.*
import io.github.erkko68.filament.gltfio.cinterop.*
import cnames.structs.FilaAssetLoader

actual class AssetLoader(public var nativeHandle: CPointer<FilaAssetLoader>?) {
    actual companion object {
        actual fun create(engine: Engine, materials: MaterialProvider, entities: EntityManager?): AssetLoader {
            val handle = FilaAssetLoader_create(
                engine.nativeHandle,
                materials.getNativeHandle(),
                entities?.nativeHandle
            )
            return AssetLoader(handle)
        }

        actual fun destroy(loader: AssetLoader) {
            FilaAssetLoader_destroy(loader.nativeHandle)
            loader.nativeHandle = null
        }
    }

    actual fun createAsset(buffer: ByteArray): FilamentAsset? {
        val handle = buffer.usePinned { pinned ->
            FilaAssetLoader_createAsset(nativeHandle, pinned.addressOf(0), buffer.size.toULong())
        }
        return handle?.let { FilamentAsset(it) }
    }

    actual fun createInstancedAsset(buffer: ByteArray, instances: Array<FilamentInstance>): FilamentAsset? {
        return buffer.usePinned { pinned ->
            memScoped {
                val nativeInstances = allocArray<CPointerVar<cnames.structs.FilaFilamentInstance>>(instances.size)
                val handle = FilaAssetLoader_createInstancedAsset(
                    nativeHandle,
                    pinned.addressOf(0),
                    buffer.size.toULong(),
                    nativeInstances,
                    instances.size.toULong()
                )
                if (handle == null) return@memScoped null
                val asset = FilamentAsset(handle)
                for (i in instances.indices) {
                    instances[i].nativeHandle = nativeInstances[i]
                }
                asset
            }
        }
    }

    actual fun createInstance(asset: FilamentAsset): FilamentInstance? {
        val handle = FilaAssetLoader_createInstance(nativeHandle, asset.nativeHandle) ?: return null
        return FilamentInstance(handle)
    }

    actual fun enableDiagnostics(enable: Boolean) {
        FilaAssetLoader_enableDiagnostics(nativeHandle, enable)
    }

    actual fun destroyAsset(asset: FilamentAsset) {
        FilaAssetLoader_destroyAsset(nativeHandle, asset.nativeHandle)
        asset.nativeHandle = null
    }
}
