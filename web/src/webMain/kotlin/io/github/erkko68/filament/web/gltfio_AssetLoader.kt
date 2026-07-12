// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class gltfio_AssetLoader : JsAny {
fun createAsset(urlOrBuffer: BufferReference): gltfio_FilamentAsset
fun createInstancedAsset(urlOrBuffer: BufferReference, instances: js.array.ReadonlyArray<(gltfio_FilamentInstance?)>): gltfio_FilamentAsset
fun destroyAsset(asset: gltfio_FilamentAsset): Unit
fun createInstance(asset: gltfio_FilamentAsset): (gltfio_FilamentInstance?)
fun delete(): Unit
}
