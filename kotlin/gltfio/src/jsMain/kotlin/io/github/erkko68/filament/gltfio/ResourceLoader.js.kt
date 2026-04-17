package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine

actual class ResourceLoader {
    actual fun destroy() {
    }

    actual fun addResourceData(url: String, data: ByteArray) {
    }

    actual fun hasResourceData(url: String): Boolean {
        TODO("Not yet implemented")
    }

    actual fun loadResources(asset: FilamentAsset): Boolean {
        TODO("Not yet implemented")
    }

    actual fun asyncBeginLoad(asset: FilamentAsset): Boolean {
        TODO("Not yet implemented")
    }

    actual fun asyncGetLoadProgress(): Float {
        TODO("Not yet implemented")
    }

    actual fun asyncUpdateLoad() {
    }

    actual fun asyncCancelLoad() {
    }

    actual fun evictResourceData() {
    }

    actual constructor(engine: Engine, normalizeSkinningWeights: Boolean) {
        TODO("Not yet implemented")
    }
}