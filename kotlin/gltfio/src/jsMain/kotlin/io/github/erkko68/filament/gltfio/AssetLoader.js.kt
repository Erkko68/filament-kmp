package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.EntityManager
import io.github.erkko68.filament.js.`gltfio_AssetLoader` as JSAssetLoader
import io.github.erkko68.filament.js.Engine as JSEngine

private fun ByteArray.toUint8Array(): org.khronos.webgl.Uint8Array {
    val int8 = org.khronos.webgl.Int8Array(size)
    forEachIndexed { i, b -> int8.asDynamic()[i] = b }
    return org.khronos.webgl.Uint8Array(int8.buffer)
}

actual class AssetLoader(private val jsLoader: JSAssetLoader, private val engine: Engine) {
    actual fun createAsset(buffer: ByteArray): FilamentAsset? {
        val jsAsset = jsLoader.createAsset(buffer.toUint8Array().unsafeCast<org.khronos.webgl.ArrayBufferView>())
        return if (jsAsset != null) FilamentAsset(jsAsset, engine) else null
    }

    actual fun createInstancedAsset(
        buffer: ByteArray,
        instances: Array<FilamentInstance>
    ): FilamentAsset? {
        val jsInstances: Array<io.github.erkko68.filament.js.`gltfio_FilamentInstance`?> = instances.map { it.jsInstance }.toTypedArray()
        val jsAsset = jsLoader.createInstancedAsset(buffer.toUint8Array().unsafeCast<org.khronos.webgl.ArrayBufferView>(), jsInstances)
        return if (jsAsset != null) FilamentAsset(jsAsset, engine) else null
    }

    actual fun createInstance(asset: FilamentAsset): FilamentInstance? {
        val jsInstance = jsLoader.createInstance(asset.jsAsset)
        return if (jsInstance != null) FilamentInstance(jsInstance) else null
    }

    actual fun enableDiagnostics(enable: Boolean) {
    }

    actual fun destroyAsset(asset: FilamentAsset) {
        jsLoader.destroyAsset(asset.jsAsset)
    }

    actual companion object {
        actual fun create(
            engine: Engine,
            materials: MaterialProvider,
            entities: EntityManager?
        ): AssetLoader {
            val jsLoader = engine.jsEngine.createAssetLoader()
            return AssetLoader(jsLoader, engine)
        }

        actual fun destroy(loader: AssetLoader) {
            loader.jsLoader.delete()
        }
    }
}