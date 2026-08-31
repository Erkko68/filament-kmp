package io.github.erkko68.filament.web

/** Texture decoder handed to `gltfio_ResourceLoader.addWebpProvider`. */
@JsName("gltfio\$WebpProvider")
external class gltfio_WebpProvider(engine: Engine) : JsAny {
companion object {
fun isWebpSupported(): Boolean
}
}
