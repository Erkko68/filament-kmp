@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament.gltfio

import kotlinx.cinterop.*
import io.github.erkko68.filament.*
import io.github.erkko68.filament.gltfio.cinterop.*
import cnames.structs.FilaResourceLoader
import cnames.structs.FilaTextureProvider

actual class ResourceLoader {
    public var nativeHandle: CPointer<FilaResourceLoader>?
    private val providers = mutableListOf<CPointer<FilaTextureProvider>>()

    actual constructor(engine: Engine, normalizeSkinningWeights: Boolean) {
        val loader = FilaResourceLoader_create(engine.nativeHandle, normalizeSkinningWeights)
        nativeHandle = loader
        
        // Auto-initialize texture providers to match Android behavior
        val stbProvider = FilaResourceLoader_createStbProvider(engine.nativeHandle)
        if (stbProvider != null) {
            FilaResourceLoader_addTextureProvider(loader, "image/jpeg", stbProvider)
            FilaResourceLoader_addTextureProvider(loader, "image/png", stbProvider)
            providers.add(stbProvider)
        }
        
        val ktx2Provider = FilaResourceLoader_createKtx2Provider(engine.nativeHandle)
        if (ktx2Provider != null) {
            FilaResourceLoader_addTextureProvider(loader, "image/ktx2", ktx2Provider)
            providers.add(ktx2Provider)
        }
    }

    actual fun destroy() {
        nativeHandle?.let { FilaResourceLoader_destroy(it) }
        nativeHandle = null
        providers.forEach { FilaResourceLoader_destroyTextureProvider(it) }
        providers.clear()
    }

    actual fun addResourceData(url: String, data: ByteArray) {
        data.usePinned { pinned ->
            FilaResourceLoader_addResourceData(nativeHandle, url, pinned.addressOf(0), data.size.toULong())
        }
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
    }
}
