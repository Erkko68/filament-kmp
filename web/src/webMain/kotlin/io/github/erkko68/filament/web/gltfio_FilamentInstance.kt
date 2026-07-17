package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

@JsName("gltfio\$FilamentInstance")
external class gltfio_FilamentInstance : JsAny {
fun getAsset(): gltfio_FilamentAsset
fun getEntities(): Vector<Entity>
fun getRoot(): Entity
fun getAnimator(): gltfio_Animator
fun getSkinNames(): Vector<JsString>
fun getSkinCount(): Double
fun getJointCountAt(skinIndex: Double): Double
fun getJointsAt(skinIndex: Double): js.array.ReadonlyArray<Entity>
fun attachSkin(skinIndex: Double, entity: Entity): Unit
fun detachSkin(skinIndex: Double, entity: Entity): Unit
fun getMaterialInstances(): Vector<MaterialInstance>
fun detachMaterialInstances(): Unit
fun getMaterialVariantNames(): js.array.ReadonlyArray<JsString>
fun applyMaterialVariant(index: Double): Unit
}
