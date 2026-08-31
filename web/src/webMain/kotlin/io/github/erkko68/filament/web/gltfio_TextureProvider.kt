package io.github.erkko68.filament.web

@JsName("gltfio\$TextureProvider")
external class gltfio_TextureProvider : JsAny {
companion object {
fun createStbProvider(engine: Engine): gltfio_TextureProvider
fun createKtx2Provider(engine: Engine): gltfio_TextureProvider
fun createWebpProvider(engine: Engine): gltfio_TextureProvider
fun isWebpSupported(): Boolean
}
}
