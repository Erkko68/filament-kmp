package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.EntityManager
import io.github.erkko68.filament.web.interop.toJsArray
import org.khronos.webgl.set
import io.github.erkko68.filament.web.gltfio_AssetLoader as JSAssetLoader
import io.github.erkko68.filament.nativeObject
import io.github.erkko68.filament.InternalFilamentApi

private fun ByteArray.toUint8Array(): org.khronos.webgl.Uint8Array {
    val int8 = org.khronos.webgl.Int8Array(size)
    forEachIndexed { i, b -> int8[i] = b }
    return org.khronos.webgl.Uint8Array(int8.buffer)
}

actual class AssetLoader @InternalFilamentApi constructor(internal val jsLoader: JSAssetLoader, private val engine: Engine) {
    actual fun createAsset(buffer: ByteArray): FilamentAsset? {
        val jsAsset = jsLoader.createAsset(buffer.toUint8Array().unsafeCast<org.khronos.webgl.ArrayBufferView>())
        return if (jsAsset != null) FilamentAsset(jsAsset, engine) else null
    }

    actual fun createInstancedAsset(
        buffer: ByteArray,
        instances: Array<FilamentInstance>
    ): FilamentAsset? {
        val jsInstances = instances.map { it.jsInstance }.toJsArray()
        val jsAsset = jsLoader.createInstancedAsset(buffer.toUint8Array().unsafeCast<org.khronos.webgl.ArrayBufferView>(), jsInstances)
        return if (jsAsset != null) FilamentAsset(jsAsset, engine) else null
    }

    actual fun createInstance(asset: FilamentAsset): FilamentInstance? {
        val jsInstance = jsLoader.createInstance(asset.jsAsset)
        return if (jsInstance != null) FilamentInstance(jsInstance) else null
    }

    actual fun enableDiagnostics(enable: Boolean) {
        jsLoader.enableDiagnostics(enable)
    }

    actual fun destroyAsset(asset: FilamentAsset) {
        jsLoader.destroyAsset(asset.jsAsset)
    }

    actual fun gc() {
        jsLoader.gc()
    }

    actual companion object {
        actual fun create(
            engine: Engine,
            materials: MaterialProvider,
            entities: EntityManager?
        ): AssetLoader {
            // Route through the caller's provider when it is one we can hand to embind;
            // createAssetLoader() would build a second UbershaderProvider of its own.
            val jsLoader = if (materials is UbershaderProvider) {
                JSAssetLoader(engine.nativeObject, materials.jsProvider)
            } else {
                engine.nativeObject.createAssetLoader()
            }
            return AssetLoader(jsLoader, engine)
        }

        actual fun destroy(loader: AssetLoader) {
            // AssetLoader's embind raw_destructor is a no-op, so `delete()` leaks the
            // loader; the static destroy() is the one that actually frees it.
            io.github.erkko68.filament.web.gltfio_AssetLoader.destroy(loader.jsLoader)
        }
    }
}