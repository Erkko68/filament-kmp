package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class RenderableManager : JsAny {
fun hasComponent(entity: Entity): Boolean
fun getInstance(entity: Entity): RenderableManager_Instance
fun destroy(entity: Entity): Unit
fun setAxisAlignedBoundingBox(instance: RenderableManager_Instance, aabb: Box): Unit
fun setLayerMask(instance: RenderableManager_Instance, select: Double, values: Double): Unit
fun setPriority(instance: RenderableManager_Instance, priority: Double): Unit
fun setCastShadows(instance: RenderableManager_Instance, enable: Boolean): Unit
fun setReceiveShadows(inst: RenderableManager_Instance, enable: Boolean): Unit
fun isShadowCaster(instance: RenderableManager_Instance): Boolean
fun isShadowReceiver(instance: RenderableManager_Instance): Boolean
fun setBones(instance: RenderableManager_Instance, transforms: js.array.ReadonlyArray<RenderableManager_Bone>, offset: Double): Unit
fun setBonesFromMatrices(instance: RenderableManager_Instance, transforms: js.array.ReadonlyArray<mat4>, offset: Double): Unit
fun setMorphWeights(instance: RenderableManager_Instance, a: Double, b: Double, c: Double, d: Double): Unit
fun setMorphWeightsOffset(instance: RenderableManager_Instance, weights: JsAny?, offset: Double): Unit
fun getMorphTargetCount(instance: RenderableManager_Instance): Double
fun setMorphTargetBufferOffsetAt(instance: RenderableManager_Instance, level: Double, primitiveIndex: Double, offset: Double): Unit
fun setSkinningBuffer(instance: RenderableManager_Instance, skinningBuffer: SkinningBuffer, count: Double, offset: Double): Unit
fun getAxisAlignedBoundingBox(instance: RenderableManager_Instance): Box
fun getPrimitiveCount(instance: RenderableManager_Instance): Double
fun setMaterialInstanceAt(instance: RenderableManager_Instance, primitiveIndex: Double, materialInstance: MaterialInstance): Unit
fun getMaterialInstanceAt(instance: RenderableManager_Instance, primitiveIndex: Double): MaterialInstance
fun setGeometryAt(instance: RenderableManager_Instance, primitiveIndex: Double, type: RenderableManager_PrimitiveType, vertices: VertexBuffer, indices: IndexBuffer, offset: Double, count: Double): Unit
fun setGeometryNoIndicesAt(instance: RenderableManager_Instance, primitiveIndex: Double, type: RenderableManager_PrimitiveType, vertices: VertexBuffer, offset: Double, count: Double): Unit
fun setBlendOrderAt(instance: RenderableManager_Instance, primitiveIndex: Double, order: Double): Unit
fun getEnabledAttributesAt(instance: RenderableManager_Instance, primitiveIndex: Double): Double
fun getBlendOrderAt(instance: RenderableManager_Instance, primitiveIndex: Double): Double
fun setGlobalBlendOrderEnabledAt(instance: RenderableManager_Instance, primitiveIndex: Double, enabled: Boolean): Unit
fun isGlobalBlendOrderEnabledAt(instance: RenderableManager_Instance, primitiveIndex: Double): Boolean
fun clearMaterialInstanceAt(instance: RenderableManager_Instance, primitiveIndex: Double): Unit
fun getInstanceCount(instance: RenderableManager_Instance): Double
fun getPriority(instance: RenderableManager_Instance): Double
fun getChannel(instance: RenderableManager_Instance): Double
fun setChannel(instance: RenderableManager_Instance, channel: Double): Unit
fun getLightChannel(instance: RenderableManager_Instance, channel: Double): Boolean
fun setLightChannel(instance: RenderableManager_Instance, channel: Double, enable: Boolean): Unit
fun getFogEnabled(instance: RenderableManager_Instance): Boolean
fun setFogEnabled(instance: RenderableManager_Instance, enabled: Boolean): Unit
fun isCullingEnabled(instance: RenderableManager_Instance): Boolean
fun setCulling(instance: RenderableManager_Instance, enable: Boolean): Unit
fun isScreenSpaceContactShadowsEnabled(instance: RenderableManager_Instance): Boolean
fun setScreenSpaceContactShadows(instance: RenderableManager_Instance, enabled: Boolean): Unit
companion object {
fun Builder(ngeos: Double): RenderableManager_Builder
}
}

// ── RenderableManager ─────────────────────────────────────────────────────────
