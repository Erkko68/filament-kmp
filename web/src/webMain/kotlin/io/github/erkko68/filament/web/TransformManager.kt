// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class TransformManager : JsAny {
fun hasComponent(entity: Entity): Boolean
fun getInstance(entity: Entity): TransformManager_Instance
fun create(entity: Entity): Unit
fun destroy(entity: Entity): Unit
fun setParent(instance: TransformManager_Instance, parent: TransformManager_Instance): Unit
fun setTransform(instance: TransformManager_Instance, xform: mat4): Unit
fun getTransform(instance: TransformManager_Instance): mat4
fun getWorldTransform(instance: TransformManager_Instance): mat4
fun openLocalTransformTransaction(): Unit
fun commitLocalTransformTransaction(): Unit
fun getParent(instance: TransformManager_Instance): Entity
fun getChildren(instance: TransformManager_Instance): Vector<Entity>
}

// ── TransformManager ──────────────────────────────────────────────────────────
