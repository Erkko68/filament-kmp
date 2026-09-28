package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.*
import io.github.erkko68.filament.jni.*

actual class ResourceLoader : AutoCloseable {
    internal var nativeHandle: Long
    private val providers = mutableListOf<Long>()
    // gltfio keeps addResourceData buffers by pointer until they're evicted, so the native copies live until then.
    private val resourceCopies = mutableListOf<Long>()

    actual constructor(engine: Engine, normalizeSkinningWeights: Boolean) {
        val loader = FilaResourceLoader_create(engine.nativeObject, normalizeSkinningWeights)
        nativeHandle = loader
        
        // Register the stb/ktx2 texture providers up front, as filament-android's ResourceLoader does.
        val stbProvider = FilaResourceLoader_createStbProvider(engine.nativeObject)
        if (stbProvider != 0L) {
            FilaResourceLoader_addTextureProvider(loader, "image/jpeg", stbProvider)
            FilaResourceLoader_addTextureProvider(loader, "image/png", stbProvider)
            providers.add(stbProvider)
        }
        
        val ktx2Provider = FilaResourceLoader_createKtx2Provider(engine.nativeObject)
        if (ktx2Provider != 0L) {
            FilaResourceLoader_addTextureProvider(loader, "image/ktx2", ktx2Provider)
            providers.add(ktx2Provider)
        }
    }

    actual override fun close() = destroy()


    actual fun destroy() {
        if (nativeHandle != 0L) FilaResourceLoader_destroy(nativeHandle)
        nativeHandle = 0
        providers.forEach { FilaResourceLoader_destroyTextureProvider(it) }
        providers.clear()
        freeResourceCopies()
    }

    actual fun addResourceData(url: String, data: ByteArray) {
        val copy = allocZeroed(data.size).also { writeBytes(it, data) }
        resourceCopies.add(copy)
        FilaResourceLoader_addResourceData(nativeHandle, url, copy, data.size.toLong())
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
        resourceCopies.forEach { FilaJni.free(it) }
        resourceCopies.clear()
    }
}
