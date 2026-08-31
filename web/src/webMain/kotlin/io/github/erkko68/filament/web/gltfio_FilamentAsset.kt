// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

@JsName("gltfio\$FilamentAsset")
external class gltfio_FilamentAsset : JsAny {
fun loadResources(onDone: () -> Unit, onFetched: (s: String) -> Unit, basePath: String?, asyncInterval: Double?, options: JsAny = definedExternally): Unit
fun getEntities(): js.array.ReadonlyArray<Entity>
fun getEntitiesByName(name: String): js.array.ReadonlyArray<Entity>
fun getEntityByName(name: String): Entity
fun getEntitiesByPrefix(name: String): js.array.ReadonlyArray<Entity>
fun getLightEntities(): js.array.ReadonlyArray<Entity>
fun getRenderableEntities(): js.array.ReadonlyArray<Entity>
fun getCameraEntities(): js.array.ReadonlyArray<Entity>
fun getRoot(): Entity
fun popRenderable(): Entity
fun getInstance(): gltfio_FilamentInstance
fun getAssetInstances(): js.array.ReadonlyArray<gltfio_FilamentInstance>
fun getResourceUris(): js.array.ReadonlyArray<JsString>
fun getBoundingBox(): Aabb
fun getName(entity: Entity): String
fun getExtras(entity: Entity): String
fun getWireframe(): Entity
fun getEngine(): Engine
fun releaseSourceData(): Unit
fun getFirstEntityByName(name: String): Entity
fun getMorphTargetNames(entity: Entity): js.array.ReadonlyArray<JsString>
}

// ── gltfio ────────────────────────────────────────────────────────────────────
