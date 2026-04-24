package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine

actual class ResourceLoader actual constructor(engine: Engine, normalizeSkinningWeights: Boolean) {
    private val resourceData = mutableMapOf<String, ByteArray>()
    private var loadProgress = 0f

    actual fun destroy() {
        resourceData.clear()
    }

    actual fun addResourceData(url: String, data: ByteArray) {
        resourceData[url] = data
    }

    actual fun hasResourceData(url: String): Boolean {
        return resourceData.containsKey(url)
    }

    actual fun loadResources(asset: FilamentAsset): Boolean {
        // Delegate to the JS FilamentAsset's built-in loadResources which handles
        // WASM resource loader creation, buffer uploads, and async texture decoding.
        // This is required even for .glb files to upload vertex data to WebGL.
        asset.jsAsset.asDynamic().loadResources()
        return true
    }

    actual fun asyncBeginLoad(asset: FilamentAsset): Boolean {
        loadProgress = 0f
        return true
    }

    actual fun asyncGetLoadProgress(): Float {
        return loadProgress
    }

    actual fun asyncUpdateLoad() {
        loadProgress = 1f
    }

    actual fun asyncCancelLoad() {
        loadProgress = 0f
    }

    actual fun evictResourceData() {
        resourceData.clear()
    }
}