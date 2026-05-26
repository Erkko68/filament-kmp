package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.EntityManager
import io.github.erkko68.filament.gltfio.jni.AssetLoader as JniAssetLoader
import io.github.erkko68.filament.gltfio.jni.FilamentInstance as JniFilamentInstance
import java.nio.ByteBuffer
import java.nio.ByteOrder

actual class AssetLoader(val jni: JniAssetLoader) {
    actual companion object {
        actual fun create(engine: Engine, materials: MaterialProvider, entities: EntityManager?): AssetLoader {
            // Need to map MaterialProvider to JNI MaterialProvider. Since UbershaderProvider is the only implementation for now:
            val jniProvider = (materials as UbershaderProvider).jni
            val jniLoader = JniAssetLoader(engine.nativeEngine, jniProvider, entities!!.nativeEntityManager)
            return AssetLoader(jniLoader)
        }

        actual fun destroy(loader: AssetLoader) {
            loader.jni.destroy()
        }
    }

    actual fun createAsset(buffer: ByteArray): FilamentAsset? {
        val byteBuffer = ByteBuffer.allocateDirect(buffer.size).order(ByteOrder.nativeOrder())
        byteBuffer.put(buffer)
        byteBuffer.rewind()
        val jniAsset = jni.createAsset(byteBuffer)
        return jniAsset?.let { FilamentAsset(it) }
    }

    actual fun createInstancedAsset(buffer: ByteArray, instances: Array<FilamentInstance>): FilamentAsset? {
        val byteBuffer = ByteBuffer.allocateDirect(buffer.size).order(ByteOrder.nativeOrder())
        byteBuffer.put(buffer)
        byteBuffer.rewind()
        // The JNI wrapper writes a fresh JniFilamentInstance into each slot; we then
        // hand those back to the Kotlin wrappers (which may have been constructed empty).
        val jniInstances = arrayOfNulls<JniFilamentInstance>(instances.size)
        @Suppress("UNCHECKED_CAST")
        val jniAsset = jni.createInstancedAsset(byteBuffer, jniInstances as Array<JniFilamentInstance>)
        if (jniAsset == null) return null
        for (i in instances.indices) {
            instances[i].jni = jniInstances[i]
        }
        return FilamentAsset(jniAsset)
    }

    actual fun createInstance(asset: FilamentAsset): FilamentInstance? {
        val jniInstance = jni.createInstance(asset.jni)
        return jniInstance?.let { FilamentInstance(it) }
    }

    actual fun enableDiagnostics(enable: Boolean) {
        jni.enableDiagnostics(enable)
    }

    actual fun destroyAsset(asset: FilamentAsset) {
        jni.destroyAsset(asset.jni)
    }
}
