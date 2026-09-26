package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.*
import io.github.erkko68.filament.wasm.*
import io.github.erkko68.filament.nativeObject

actual class ResourceLoader : AutoCloseable {
    internal var nativeHandle: Int
    private val providers = mutableListOf<Int>()
    // gltfio keeps addResourceData buffers by pointer until they're evicted, so the heap copies live until then.
    private val resourceCopies = mutableListOf<Int>()

    actual constructor(engine: Engine, normalizeSkinningWeights: Boolean) {
        val loader = FilaResourceLoader_create(engine.nativeObject, normalizeSkinningWeights)
        nativeHandle = loader
        
        // Auto-initialize texture providers to match Android behavior
        val stbProvider = FilaResourceLoader_createStbProvider(engine.nativeObject)
        if (stbProvider != 0) {
            FilaResourceLoader_addTextureProvider(loader, "image/jpeg", stbProvider)
            FilaResourceLoader_addTextureProvider(loader, "image/png", stbProvider)
            providers.add(stbProvider)
        }
        
        val ktx2Provider = FilaResourceLoader_createKtx2Provider(engine.nativeObject)
        if (ktx2Provider != 0) {
            FilaResourceLoader_addTextureProvider(loader, "image/ktx2", ktx2Provider)
            providers.add(ktx2Provider)
        }
    }

    actual override fun close() = destroy()


    actual fun destroy() {
        if (nativeHandle != 0) FilaResourceLoader_destroy(nativeHandle)
        nativeHandle = 0
        providers.forEach { FilaResourceLoader_destroyTextureProvider(it) }
        providers.clear()
        freeResourceCopies()
    }

    actual fun addResourceData(url: String, data: ByteArray) {
        val copy = fila.allocZeroed(data.size).also { fila.writeBytes(it, data) }
        resourceCopies.add(copy)
        FilaResourceLoader_addResourceData(nativeHandle, url, copy, data.size)
    }

    actual fun hasResourceData(url: String): Boolean = FilaResourceLoader_hasResourceData(nativeHandle, url)

    actual fun loadResources(asset: FilamentAsset): Boolean {
        return FilaResourceLoader_loadResources(nativeHandle, asset.nativeHandle)
    }

    actual fun asyncBeginLoad(asset: FilamentAsset): Boolean {
        return FilaResourceLoader_asyncBeginLoad(nativeHandle, asset.nativeHandle)
    }

    actual fun asyncGetLoadProgress(): Float = FilaResourceLoader_asyncGetLoadProgress(nativeHandle)

    actual fun asyncUpdateLoad() {
        FilaResourceLoader_asyncUpdateLoad(nativeHandle)
    }

    actual fun asyncCancelLoad() {
        FilaResourceLoader_asyncCancelLoad(nativeHandle)
    }

    actual fun evictResourceData() {
        FilaResourceLoader_evictResourceData(nativeHandle)
        freeResourceCopies()
    }

    private fun freeResourceCopies() {
        resourceCopies.forEach { fila._free(it) }
        resourceCopies.clear()
    }
}
