// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

@JsName("gltfio\$AssetLoader")
external class gltfio_AssetLoader : JsAny {
// Nullable: both return null when the bytes don't parse as glb/glTF. wasmJs throws NPE at the
// interop boundary if that null lands in a non-null type (js silently passes it through).
fun createAsset(urlOrBuffer: BufferReference): (gltfio_FilamentAsset?)
fun createInstancedAsset(urlOrBuffer: BufferReference, instances: js.array.ReadonlyArray<(gltfio_FilamentInstance?)>): (gltfio_FilamentAsset?)
fun destroyAsset(asset: gltfio_FilamentAsset): Unit
fun createInstance(asset: gltfio_FilamentAsset): (gltfio_FilamentInstance?)
fun delete(): Unit
}
