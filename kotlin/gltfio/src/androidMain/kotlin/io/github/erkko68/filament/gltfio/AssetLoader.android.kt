package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.EntityManager
import java.nio.ByteBuffer

actual class AssetLoader internal constructor(
    internal val nativeObject: com.google.android.filament.gltfio.AssetLoader
) {
    actual companion object {
        actual fun create(engine: Engine, materials: MaterialProvider, entities: EntityManager?): AssetLoader {
            val nativeLoader = com.google.android.filament.gltfio.AssetLoader(
                engine.nativeEngine,
                materials.getNativeProvider(),
                (entities ?: EntityManager.get()).nativeEntityManager
            )
            return AssetLoader(nativeLoader)
        }

        actual fun destroy(loader: AssetLoader) {
            loader.nativeObject.destroy()
        }
    }

    actual fun createAsset(buffer: ByteArray): FilamentAsset? {
        val byteBuffer = ByteBuffer.allocateDirect(buffer.size)
        byteBuffer.put(buffer)
        byteBuffer.rewind()
        val nativeAsset = nativeObject.createAsset(byteBuffer) ?: return null
        return FilamentAsset(nativeAsset)
    }

    actual fun createInstancedAsset(buffer: ByteArray, instances: Array<FilamentInstance>): FilamentAsset? {
        val byteBuffer = ByteBuffer.allocateDirect(buffer.size)
        byteBuffer.put(buffer)
        byteBuffer.rewind()

        val nativeInstances = arrayOfNulls<com.google.android.filament.gltfio.FilamentInstance>(instances.size)
        val nativeAsset = nativeObject.createInstancedAsset(byteBuffer, nativeInstances) ?: return null

        val asset = FilamentAsset(nativeAsset)
        val wrappedInstances = Array(instances.size) { i ->
            val nativeInstance = nativeInstances[i]
                ?: throw IllegalStateException("Missing instance at index $i from createInstancedAsset")
            FilamentInstance(nativeInstance, asset)
        }
        for (i in instances.indices) {
            instances[i] = wrappedInstances[i]
        }
        asset.setKnownInstances(wrappedInstances)
        return asset
    }

    actual fun createInstance(asset: FilamentAsset): FilamentInstance? {
        val nativeInstance = nativeObject.createInstance(asset.nativeObject) ?: return null
        return FilamentInstance(nativeInstance, asset)
    }

    actual fun enableDiagnostics(enable: Boolean) {
        nativeObject.enableDiagnostics(enable)
    }

    actual fun destroyAsset(asset: FilamentAsset) {
        nativeObject.destroyAsset(asset.nativeObject)
    }
}
