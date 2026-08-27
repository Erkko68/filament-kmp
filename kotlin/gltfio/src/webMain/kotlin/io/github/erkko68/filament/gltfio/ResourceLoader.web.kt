package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.web.driver_BufferDescriptor
import io.github.erkko68.filament.web.gltfio_ResourceLoader
import io.github.erkko68.filament.web.gltfio_TextureProvider
import org.khronos.webgl.set
import io.github.erkko68.filament.nativeObject

actual class ResourceLoader actual constructor(engine: Engine, normalizeSkinningWeights: Boolean) {
    private val jsLoader = gltfio_ResourceLoader(engine.nativeObject, normalizeSkinningWeights)

    init {
        // Same providers extensions.js registers in its loadResources helper; without them
        // the loader has no way to decode embedded or external textures.
        val stb = gltfio_TextureProvider.createStbProvider(engine.nativeObject)
        jsLoader.addTextureProvider("image/jpeg", stb)
        jsLoader.addTextureProvider("image/png", stb)
        jsLoader.addTextureProvider("image/ktx2", gltfio_TextureProvider.createKtx2Provider(engine.nativeObject))
        if (gltfio_TextureProvider.isWebpSupported()) {
            jsLoader.addTextureProvider("image/webp", gltfio_TextureProvider.createWebpProvider(engine.nativeObject))
        }
    }

    actual fun destroy() {
        jsLoader.delete()
    }

    actual fun addResourceData(url: String, data: ByteArray) {
        jsLoader.addResourceData(url, data.toBufferDescriptor())
    }

    actual fun hasResourceData(url: String): Boolean = jsLoader.hasResourceData(url)

    actual fun loadResources(asset: FilamentAsset): Boolean = jsLoader.loadResources(asset.jsAsset)

    actual fun asyncBeginLoad(asset: FilamentAsset): Boolean = jsLoader.asyncBeginLoad(asset.jsAsset)

    actual fun asyncGetLoadProgress(): Float = jsLoader.asyncGetLoadProgress().toFloat()

    actual fun asyncUpdateLoad() {
        jsLoader.asyncUpdateLoad()
    }

    actual fun asyncCancelLoad() {
        jsLoader.asyncCancelLoad()
    }

    actual fun evictResourceData() {
        jsLoader.evictResourceData()
    }
}

/** Copies into a wasm-heap BufferDescriptor, which addResourceData takes ownership of. */
private fun ByteArray.toBufferDescriptor(): driver_BufferDescriptor {
    val bd = driver_BufferDescriptor(size.toDouble())
    val dst = org.khronos.webgl.Uint8Array(bd.getBytes())
    forEachIndexed { i, b -> dst[i] = b }
    return bd
}
