package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.js.assets
import org.khronos.webgl.set

private fun ByteArray.toUint8Array(): org.khronos.webgl.Uint8Array {
    val int8 = org.khronos.webgl.Int8Array(size)
    forEachIndexed { i, b -> int8[i] = b }
    return org.khronos.webgl.Uint8Array(int8.buffer)
}

// The generated external types loadResources with all callbacks required; the real
// filament.js signature has them optional. Re-type it permissively so callers can omit
// args without dropping to `asDynamic()`.
private external interface JsLoadResources {
    fun loadResources(
        onDone: (() -> Unit)? = definedExternally,
        onFetched: ((url: String) -> Unit)? = definedExternally,
        basePath: String? = definedExternally,
        asyncInterval: Number? = definedExternally,
    )
}

actual class ResourceLoader actual constructor(engine: Engine, normalizeSkinningWeights: Boolean) {
    private val resourceData = mutableMapOf<String, ByteArray>()
    private var loadProgress = 0f

    actual fun destroy() {
        evictResourceData()
    }

    actual fun addResourceData(url: String, data: ByteArray) {
        resourceData[url] = data
        // `assets` is generated as a read-only Record; write through dynamically.
        assets.asDynamic()[url] = data.toUint8Array()
    }

    actual fun hasResourceData(url: String): Boolean {
        return resourceData.containsKey(url)
    }

    actual fun loadResources(asset: FilamentAsset): Boolean {
        // Delegate to the JS FilamentAsset's built-in loadResources which handles
        // WASM resource loader creation, buffer uploads, and async texture decoding.
        // This is required even for .glb files to upload vertex data to WebGL.
        asset.jsAsset.unsafeCast<JsLoadResources>().loadResources()
        return true
    }

    private var currentAsset: FilamentAsset? = null
    private var isLoadStarted = false

    actual fun asyncBeginLoad(asset: FilamentAsset): Boolean {
        currentAsset = asset
        loadProgress = 0f
        isLoadStarted = false
        return true
    }

    actual fun asyncGetLoadProgress(): Float {
        return loadProgress
    }

    actual fun asyncUpdateLoad() {
        val asset = currentAsset ?: return
        if (!isLoadStarted) {
            isLoadStarted = true
            // On JS, loadResources is asynchronous. We use the callback to set progress to 1.0.
            // We set it to 0.1 initially so the calling loop knows we are working.
            loadProgress = 0.1f
            asset.jsAsset.unsafeCast<JsLoadResources>().loadResources(onDone = {
                loadProgress = 1f
            })
        }
    }

    actual fun asyncCancelLoad() {
        currentAsset = null
        loadProgress = 0f
        isLoadStarted = false
    }

    actual fun evictResourceData() {
        for (url in resourceData.keys) {
            assets.asDynamic()[url] = null
        }
        resourceData.clear()
    }
}