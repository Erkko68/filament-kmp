package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.web.driver_BufferDescriptor
import io.github.erkko68.filament.web.gltfio_Ktx2Provider
import io.github.erkko68.filament.web.gltfio_ResourceLoader
import io.github.erkko68.filament.web.gltfio_StbProvider
import org.khronos.webgl.set

actual class ResourceLoader actual constructor(engine: Engine, normalizeSkinningWeights: Boolean) {
    private val jsLoader = gltfio_ResourceLoader(engine.jsEngine, normalizeSkinningWeights)

    init {
        // Same providers extensions.js registers in its loadResources helper; without them
        // the loader has no way to decode embedded or external textures.
        val stb = gltfio_StbProvider(engine.jsEngine)
        jsLoader.addStbProvider("image/jpeg", stb)
        jsLoader.addStbProvider("image/png", stb)
        jsLoader.addKtx2Provider("image/ktx2", gltfio_Ktx2Provider(engine.jsEngine))
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
