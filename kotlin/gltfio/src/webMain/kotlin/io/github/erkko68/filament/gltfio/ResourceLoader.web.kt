package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.FilamentPlatform
import io.github.erkko68.filament.PlatformGap
import io.github.erkko68.filament.web.driver_BufferDescriptor
import io.github.erkko68.filament.web.gltfio_Ktx2Provider
import io.github.erkko68.filament.web.gltfio_ResourceLoader
import io.github.erkko68.filament.web.gltfio_StbProvider
import io.github.erkko68.filament.web.gltfio_WebpProvider
import org.khronos.webgl.set

actual class ResourceLoader actual constructor(engine: Engine, normalizeSkinningWeights: Boolean) {
    private val jsLoader = gltfio_ResourceLoader(engine.jsEngine, normalizeSkinningWeights)
    private val stbProvider = gltfio_StbProvider(engine.jsEngine)

    init {
        // The same decoders extensions.js registers in its own loadResources helper; without
        // them the loader cannot decode embedded or external textures.
        jsLoader.addStbProvider("image/jpeg", stbProvider)
        jsLoader.addStbProvider("image/png", stbProvider)
        jsLoader.addKtx2Provider("image/ktx2", gltfio_Ktx2Provider(engine.jsEngine))
        if (gltfio_WebpProvider.isWebpSupported()) {
            jsLoader.addWebpProvider("image/webp", gltfio_WebpProvider(engine.jsEngine))
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

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — filament.js binds no asyncCancelLoad; let the load finish instead.")
    actual fun asyncCancelLoad() {
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — filament.js binds no evictResourceData; the loader frees its copies when destroyed.")
    actual fun evictResourceData() {
    }
}

/** Copies into a wasm-heap BufferDescriptor, which addResourceData takes ownership of. */
private fun ByteArray.toBufferDescriptor(): driver_BufferDescriptor {
    val bd = driver_BufferDescriptor(size.toDouble())
    val dst = org.khronos.webgl.Uint8Array(bd.getBytes())
    forEachIndexed { i, b -> dst[i] = b }
    return bd
}
