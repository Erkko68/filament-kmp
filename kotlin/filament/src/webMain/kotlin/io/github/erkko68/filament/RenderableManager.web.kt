package io.github.erkko68.filament

import io.github.erkko68.filament.wasm.*

actual class RenderableManager @InternalFilamentApi constructor(internal val nativeHandle: Int) {
    actual enum class PrimitiveType { POINTS, LINES, LINE_STRIP, TRIANGLES, TRIANGLE_STRIP }
    actual enum class GeometryType { DYNAMIC, STATIC_BOUNDS, STATIC }

    actual class Builder actual constructor(count: Int) {
        private val nativeBuilder = FilaRenderableManagerBuilder_create(count)

        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer): Builder = apply {
            FilaRenderableManagerBuilder_geometry(nativeBuilder, index, type.toNative(), vb.nativeHandle, ib.nativeHandle)
        }
        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, count: Int): Builder = apply {
            FilaRenderableManagerBuilder_geometryAt(nativeBuilder, index, type.toNative(), vb.nativeHandle, ib.nativeHandle, offset, count)
        }
        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, minIndex: Int, maxIndex: Int, count: Int): Builder = apply {
            FilaRenderableManagerBuilder_geometryWithIndices(nativeBuilder, index, type.toNative(), vb.nativeHandle, ib.nativeHandle, offset, minIndex, maxIndex, count)
        }
        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, offset: Int, count: Int): Builder = apply {
            FilaRenderableManagerBuilder_geometryNonIndexed(nativeBuilder, index, type.toNative(), vb.nativeHandle, offset, count)
        }
        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer): Builder = apply {
            FilaRenderableManagerBuilder_geometryNonIndexedNone(nativeBuilder, index, type.toNative(), vb.nativeHandle)
        }

        actual fun geometryType(type: GeometryType): Builder = apply {
            FilaRenderableManagerBuilder_geometryType(nativeBuilder, type.toNative())
        }
        actual fun material(index: Int, materialInstance: MaterialInstance): Builder = apply {
            FilaRenderableManagerBuilder_material(nativeBuilder, index, materialInstance.nativeHandle)
        }
        actual fun blendOrder(index: Int, blendOrder: Int): Builder = apply {
            FilaRenderableManagerBuilder_blendOrder(nativeBuilder, index, blendOrder)
        }
        actual fun globalBlendOrderEnabled(index: Int, enabled: Boolean): Builder = apply {
            FilaRenderableManagerBuilder_globalBlendOrderEnabled(nativeBuilder, index, enabled)
        }
        actual fun boundingBox(box: Box): Builder = apply {
            FilaRenderableManagerBuilder_boundingBox(nativeBuilder, 
                box.center[0], box.center[1], box.center[2],
                box.halfExtent[0], box.halfExtent[1], box.halfExtent[2])
        }
        actual fun layerMask(select: Int, value: Int): Builder = apply { FilaRenderableManagerBuilder_layerMask(nativeBuilder, select, value) }
        actual fun priority(priority: Int): Builder = apply { FilaRenderableManagerBuilder_priority(nativeBuilder, priority) }
        actual fun channel(channel: Int): Builder = apply { FilaRenderableManagerBuilder_channel(nativeBuilder, channel) }
        actual fun culling(enabled: Boolean): Builder = apply { FilaRenderableManagerBuilder_culling(nativeBuilder, enabled) }
        actual fun castShadows(enabled: Boolean): Builder = apply { FilaRenderableManagerBuilder_castShadows(nativeBuilder, enabled) }
        actual fun receiveShadows(enabled: Boolean): Builder = apply { FilaRenderableManagerBuilder_receiveShadows(nativeBuilder, enabled) }
        actual fun screenSpaceContactShadows(enabled: Boolean): Builder = apply { FilaRenderableManagerBuilder_screenSpaceContactShadows(nativeBuilder, enabled) }
        actual fun skinning(boneCount: Int): Builder = apply { FilaRenderableManagerBuilder_skinning(nativeBuilder, boneCount) }
        actual fun skinning(boneCount: Int, bones: FloatArray): Builder = apply {
            bones.usePinned { pinned ->
                FilaRenderableManagerBuilder_skinningBones(nativeBuilder, boneCount, pinned)
            }
        }
        actual fun skinning(skinningBuffer: SkinningBuffer, boneCount: Int, offset: Int): Builder = apply {
            FilaRenderableManagerBuilder_skinningBuffer(nativeBuilder, skinningBuffer.nativeHandle, boneCount, offset)
        }
        actual fun enableSkinningBuffers(enabled: Boolean): Builder = apply {
            FilaRenderableManagerBuilder_enableSkinningBuffers(nativeBuilder, enabled)
        }
        actual fun morphing(targetCount: Int): Builder = apply { FilaRenderableManagerBuilder_morphing(nativeBuilder, targetCount) }
        actual fun morphing(morphTargetBuffer: MorphTargetBuffer): Builder = apply {
            FilaRenderableManagerBuilder_morphTargetBuffer(nativeBuilder, morphTargetBuffer.nativeHandle)
        }
        actual fun fog(enabled: Boolean): Builder = apply { FilaRenderableManagerBuilder_fog(nativeBuilder, enabled) }
        actual fun lightChannel(channel: Int, enable: Boolean): Builder = apply { FilaRenderableManagerBuilder_lightChannel(nativeBuilder, channel, enable) }
        actual fun instances(instanceCount: Int): Builder = apply { FilaRenderableManagerBuilder_instances(nativeBuilder, instanceCount) }
        actual fun build(engine: Engine, entity: Entity) {
            FilaRenderableManagerBuilder_build(nativeBuilder, engine.nativeHandle, entity)
            FilaRenderableManagerBuilder_destroy(nativeBuilder)
        }

        private fun PrimitiveType.toNative(): Int = io.github.erkko68.filament.toNative(this)
        private fun GeometryType.toNative(): Int = io.github.erkko68.filament.toNative(this)
    }

    actual fun hasComponent(entity: Entity): Boolean = FilaRenderableManager_hasComponent(nativeHandle, entity)
    actual fun getInstance(entity: Entity): EntityInstance = FilaRenderableManager_getInstance(nativeHandle, entity).toInt()
    actual fun destroy(entity: Entity) = FilaRenderableManager_destroy(nativeHandle, entity)
    
    actual fun setAxisAlignedBoundingBox(instance: EntityInstance, box: Box) {
        FilaRenderableManager_setAxisAlignedBoundingBox(nativeHandle, instance, 
            box.center[0], box.center[1], box.center[2],
            box.halfExtent[0], box.halfExtent[1], box.halfExtent[2])
    }
    actual fun getAxisAlignedBoundingBox(instance: EntityInstance, out: Box?): Box {
        fila.heapScoped {
            val center = F32Array(alloc((3) * 4))
            val halfExtent = F32Array(alloc((3) * 4))
            FilaRenderableManager_getAxisAlignedBoundingBox(nativeHandle, instance, center.ptr, halfExtent.ptr)
            val result = out ?: Box()
            result.center[0] = center[0]
            result.center[1] = center[1]
            result.center[2] = center[2]
            result.halfExtent[0] = halfExtent[0]
            result.halfExtent[1] = halfExtent[1]
            result.halfExtent[2] = halfExtent[2]
            return result
        }
    }
    
    actual fun setLayerMask(instance: EntityInstance, select: Int, value: Int) = FilaRenderableManager_setLayerMask(nativeHandle, instance, select, value)
    actual fun setPriority(instance: EntityInstance, priority: Int) = FilaRenderableManager_setPriority(nativeHandle, instance, priority)
    actual fun getPriority(instance: EntityInstance): Int = FilaRenderableManager_getPriority(nativeHandle, instance).toInt()
    actual fun setChannel(instance: EntityInstance, channel: Int) = FilaRenderableManager_setChannel(nativeHandle, instance, channel)
    actual fun getChannel(instance: EntityInstance): Int = FilaRenderableManager_getChannel(nativeHandle, instance).toInt()
    actual fun setCullingEnabled(instance: EntityInstance, enabled: Boolean) = FilaRenderableManager_setCulling(nativeHandle, instance, enabled)
    actual fun isCullingEnabled(instance: EntityInstance): Boolean = FilaRenderableManager_isCullingEnabled(nativeHandle, instance)
    actual fun setFogEnabled(instance: EntityInstance, enabled: Boolean) = FilaRenderableManager_setFogEnabled(nativeHandle, instance, enabled)
    actual fun isFogEnabled(instance: EntityInstance): Boolean = FilaRenderableManager_getFogEnabled(nativeHandle, instance)
    actual fun setShadowCaster(instance: EntityInstance, enabled: Boolean) = FilaRenderableManager_setCastShadows(nativeHandle, instance, enabled)
    actual fun setShadowReceiver(instance: EntityInstance, enabled: Boolean) = FilaRenderableManager_setReceiveShadows(nativeHandle, instance, enabled)
    actual fun setScreenSpaceContactShadows(instance: EntityInstance, enabled: Boolean) = FilaRenderableManager_setScreenSpaceContactShadows(nativeHandle, instance, enabled)
    actual fun isShadowCaster(instance: EntityInstance): Boolean = FilaRenderableManager_isShadowCaster(nativeHandle, instance)
    actual fun isShadowReceiver(instance: EntityInstance): Boolean = FilaRenderableManager_isShadowReceiver(nativeHandle, instance)
    actual fun isScreenSpaceContactShadowsEnabled(instance: EntityInstance): Boolean = FilaRenderableManager_isScreenSpaceContactShadowsEnabled(nativeHandle, instance)
    
    actual fun getPrimitiveCount(instance: EntityInstance): Int = FilaRenderableManager_getPrimitiveCount(nativeHandle, instance).toInt()
    actual fun getInstanceCount(instance: EntityInstance): Int = FilaRenderableManager_getInstanceCount(nativeHandle, instance).toInt()
    
    actual fun setMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int, materialInstance: MaterialInstance) {
        FilaRenderableManager_setMaterialInstanceAt(nativeHandle, instance, primitiveIndex, materialInstance.nativeHandle)
    }
        
    actual fun getMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int): MaterialInstance? {
        val handle = FilaRenderableManager_getMaterialInstanceAt(nativeHandle, instance, primitiveIndex)
        return if (handle != 0) MaterialInstance(handle) else null
    }

    actual fun getEnabledAttributesAt(instance: EntityInstance, primitiveIndex: Int): Set<VertexBuffer.VertexAttribute> =
        attributeBitsetToSet(FilaRenderableManager_getEnabledAttributesAt(nativeHandle, instance, primitiveIndex).toInt())
    
    actual fun setGeometryAt(instance: EntityInstance, primitiveIndex: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, count: Int) =
        FilaRenderableManager_setGeometryAt(nativeHandle, instance, primitiveIndex, type.toNative(), vb.nativeHandle, ib.nativeHandle, offset, count)

    actual fun setGeometryAt(instance: EntityInstance, primitiveIndex: Int, type: PrimitiveType, vb: VertexBuffer, offset: Int, count: Int) =
        FilaRenderableManager_setGeometryAtNonIndexed(nativeHandle, instance, primitiveIndex, type.toNative(), vb.nativeHandle, offset, count)
    
    actual fun setBlendOrderAt(instance: EntityInstance, primitiveIndex: Int, blendOrder: Int) = 
        FilaRenderableManager_setBlendOrderAt(nativeHandle, instance, primitiveIndex, blendOrder)
    actual fun getBlendOrderAt(instance: EntityInstance, primitiveIndex: Int): Int = FilaRenderableManager_getBlendOrderAt(nativeHandle, instance, primitiveIndex).toInt()
    actual fun setGlobalBlendOrderEnabledAt(instance: EntityInstance, primitiveIndex: Int, enabled: Boolean) = 
        FilaRenderableManager_setGlobalBlendOrderEnabledAt(nativeHandle, instance, primitiveIndex, enabled)
    actual fun isGlobalBlendOrderEnabledAt(instance: EntityInstance, primitiveIndex: Int): Boolean = 
        FilaRenderableManager_isGlobalBlendOrderEnabledAt(nativeHandle, instance, primitiveIndex)
    
    actual fun setLightChannel(instance: EntityInstance, channel: Int, enable: Boolean) = FilaRenderableManager_setLightChannel(nativeHandle, instance, channel, enable)
    actual fun getLightChannel(instance: EntityInstance, channel: Int): Boolean = FilaRenderableManager_getLightChannel(nativeHandle, instance, channel)
 
    actual fun getMorphTargetCount(instance: EntityInstance): Int = FilaRenderableManager_getMorphTargetCount(nativeHandle, instance).toInt()
    
    actual fun setSkinningBuffer(instance: EntityInstance, skinningBuffer: SkinningBuffer, count: Int, offset: Int) {
        FilaRenderableManager_setSkinningBuffer(nativeHandle, instance, skinningBuffer.nativeHandle, count, offset)
    }

    actual fun setMorphWeights(instance: EntityInstance, weights: FloatArray, offset: Int) {
        weights.usePinned { pinned ->
            FilaRenderableManager_setMorphWeights(nativeHandle, instance, pinned + offset * 4, (weights.size - offset), offset)
        }
    }
 
    actual fun setMorphTargetBufferOffsetAt(instance: EntityInstance, level: Int, primitiveIndex: Int, offset: Int) {
        FilaRenderableManager_setMorphTargetBufferOffsetAt(nativeHandle, instance, level, primitiveIndex, offset)
    }

    actual fun setBonesAsMatrices(instance: EntityInstance, matrices: FloatArray, boneCount: Int, offset: Int) {
        matrices.usePinned { pinned ->
            FilaRenderableManager_setBonesAsMatrices(
                nativeHandle, instance,
                pinned,
                boneCount, offset
            )
        }
    }

    actual fun setBonesAsQuaternions(instance: EntityInstance, quaternions: FloatArray, boneCount: Int, offset: Int) {
        quaternions.usePinned { pinned ->
            FilaRenderableManager_setBonesAsQuaternions(
                nativeHandle, instance,
                pinned,
                boneCount, offset
            )
        }
    }

    actual fun clearMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int) {
        FilaRenderableManager_clearMaterialInstanceAt(nativeHandle, instance, primitiveIndex)
    }

}

private fun toNative(type: RenderableManager.PrimitiveType): Int = when (type) {
    RenderableManager.PrimitiveType.POINTS -> 0
    RenderableManager.PrimitiveType.LINES -> 1
    RenderableManager.PrimitiveType.LINE_STRIP -> 3
    RenderableManager.PrimitiveType.TRIANGLES -> 4
    RenderableManager.PrimitiveType.TRIANGLE_STRIP -> 5
}

private fun toNative(type: RenderableManager.GeometryType): Int = when (type) {
    RenderableManager.GeometryType.DYNAMIC -> FILA_RENDERABLE_MANAGER_GEOMETRY_TYPE_DYNAMIC
    RenderableManager.GeometryType.STATIC_BOUNDS -> FILA_RENDERABLE_MANAGER_GEOMETRY_TYPE_STATIC_BOUNDS
    RenderableManager.GeometryType.STATIC -> FILA_RENDERABLE_MANAGER_GEOMETRY_TYPE_STATIC
}

private fun RenderableManager.PrimitiveType.toNative(): Int = toNative(this)
private fun RenderableManager.GeometryType.toNative(): Int = toNative(this)
