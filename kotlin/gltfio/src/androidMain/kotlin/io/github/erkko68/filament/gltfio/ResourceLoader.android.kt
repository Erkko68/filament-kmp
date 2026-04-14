package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import java.nio.ByteBuffer

actual class ResourceLoader {
    private val nativeObject: com.google.android.filament.gltfio.ResourceLoader

    actual constructor(engine: Engine, normalizeSkinningWeights: Boolean) {
        nativeObject = com.google.android.filament.gltfio.ResourceLoader(engine.nativeEngine, normalizeSkinningWeights)
    }

    actual fun destroy() {
        nativeObject.destroy()
    }

    actual fun addResourceData(url: String, data: ByteArray) {
        val byteBuffer = ByteBuffer.allocateDirect(data.size)
        byteBuffer.put(data)
        byteBuffer.rewind()
        nativeObject.addResourceData(url, byteBuffer)
    }

    actual fun hasResourceData(url: String): Boolean = nativeObject.hasResourceData(url)

    actual fun loadResources(asset: FilamentAsset): Boolean {
        nativeObject.loadResources(asset.nativeObject)
        return true
    }

    actual fun asyncBeginLoad(asset: FilamentAsset): Boolean {
        return nativeObject.asyncBeginLoad(asset.nativeObject)
    }

    actual fun asyncGetLoadProgress(): Float = nativeObject.asyncGetLoadProgress()

    actual fun asyncUpdateLoad() {
        nativeObject.asyncUpdateLoad()
    }

    actual fun asyncCancelLoad() {
        nativeObject.asyncCancelLoad()
    }

    actual fun evictResourceData() {
        nativeObject.evictResourceData()
    }
}
