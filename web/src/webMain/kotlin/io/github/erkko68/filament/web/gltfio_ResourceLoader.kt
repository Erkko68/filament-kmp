package io.github.erkko68.filament.web

@JsName("gltfio\$ResourceLoader")
external class gltfio_ResourceLoader(engine: Engine, normalizeSkinningWeights: Boolean) : JsAny {
fun addResourceData(url: String, buffer: driver_BufferDescriptor): Unit
fun hasResourceData(url: String): Boolean
fun addTextureProvider(mimeType: String, provider: gltfio_TextureProvider): Unit
fun loadResources(asset: gltfio_FilamentAsset): Boolean
fun asyncBeginLoad(asset: gltfio_FilamentAsset): Boolean
fun asyncGetLoadProgress(): Double
fun asyncUpdateLoad(): Unit
fun asyncCancelLoad(): Unit
fun evictResourceData(): Unit
fun delete(): Unit
}
