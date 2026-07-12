// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

@JsName("RenderableManager\$Builder")
external class RenderableManager_Builder : JsAny {
fun geometry(slot: Double, ptype: RenderableManager_PrimitiveType, vb: VertexBuffer, ib: IndexBuffer): RenderableManager_Builder
fun geometryOffset(slot: Double, ptype: RenderableManager_PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Double, count: Double): RenderableManager_Builder
fun geometryMinMax(slot: Double, ptype: RenderableManager_PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Double, minIndex: Double, maxIndex: Double, count: Double): RenderableManager_Builder
fun material(geo: Double, minstance: MaterialInstance): RenderableManager_Builder
fun boundingBox(box: Box): RenderableManager_Builder
fun layerMask(select: Double, values: Double): RenderableManager_Builder
fun priority(value: Double): RenderableManager_Builder
fun culling(enable: Boolean): RenderableManager_Builder
fun castShadows(enable: Boolean): RenderableManager_Builder
fun receiveShadows(enable: Boolean): RenderableManager_Builder
fun skinning(boneCount: Double): RenderableManager_Builder
fun skinningBones(transforms: js.array.ReadonlyArray<RenderableManager_Bone>): RenderableManager_Builder
fun skinningMatrices(transforms: js.array.ReadonlyArray<mat4>): RenderableManager_Builder
fun morphing(enable: Boolean): RenderableManager_Builder
fun blendOrder(index: Double, order: Double): RenderableManager_Builder
fun build(engine: Engine, entity: Entity): Unit
fun geometryType(type: Double): RenderableManager_Builder
fun channel(value: Double): RenderableManager_Builder
fun fog(enable: Boolean): RenderableManager_Builder
fun lightChannel(channel: Double, enable: Boolean): RenderableManager_Builder
fun instances(instanceCount: Double): RenderableManager_Builder
fun globalBlendOrderEnabled(index: Double, enabled: Boolean): RenderableManager_Builder
fun screenSpaceContactShadows(enable: Boolean): RenderableManager_Builder
}

// ── Builders (the d.ts under-reports these too) ───────────────────────────────
