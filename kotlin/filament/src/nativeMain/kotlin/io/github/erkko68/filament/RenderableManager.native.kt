@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*
import cnames.structs.FilaRenderableManager
import cnames.structs.FilaRenderableManagerBuilder

actual class RenderableManager internal constructor(internal val nativeHandle: CPointer<FilaRenderableManager>) {
    actual enum class PrimitiveType { POINTS, LINES, LINE_STRIP, TRIANGLES, TRIANGLE_STRIP }
    actual enum class GeometryType { DYNAMIC, STATIC_BOUNDS, STATIC }

    actual class Builder actual constructor(count: Int) {
        private val nativeBuilder = FilaRenderableManagerBuilder_create(count.toULong())!!

        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer): Builder = apply {
            FilaRenderableManagerBuilder_geometry(nativeBuilder, index.toULong(), type.toNative(), vb.nativeHandle, ib.nativeHandle)
        }
        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, count: Int): Builder = apply {
            FilaRenderableManagerBuilder_geometryAt(nativeBuilder, index.toULong(), type.toNative(), vb.nativeHandle, ib.nativeHandle, offset.toULong(), count.toULong())
        }
        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, minIndex: Int, maxIndex: Int, count: Int): Builder = apply {
            FilaRenderableManagerBuilder_geometryWithIndices(nativeBuilder, index.toULong(), type.toNative(), vb.nativeHandle, ib.nativeHandle, offset.toULong(), minIndex.toULong(), maxIndex.toULong(), count.toULong())
        }
        
        actual fun geometryType(type: GeometryType): Builder = apply {
            FilaRenderableManagerBuilder_geometryType(nativeBuilder, type.toNative())
        }
        actual fun material(index: Int, materialInstance: MaterialInstance): Builder = apply {
            FilaRenderableManagerBuilder_material(nativeBuilder, index.toULong(), materialInstance.nativeHandle)
        }
        actual fun blendOrder(index: Int, blendOrder: Int): Builder = apply {
            FilaRenderableManagerBuilder_blendOrder(nativeBuilder, index.toULong(), blendOrder.toUShort())
        }
        actual fun globalBlendOrderEnabled(index: Int, enabled: Boolean): Builder = apply {
            FilaRenderableManagerBuilder_globalBlendOrderEnabled(nativeBuilder, index.toULong(), enabled)
        }
        actual fun boundingBox(box: Box): Builder = apply {
            FilaRenderableManagerBuilder_boundingBox(nativeBuilder, 
                box.center[0], box.center[1], box.center[2],
                box.halfExtent[0], box.halfExtent[1], box.halfExtent[2])
        }
        actual fun layerMask(select: Int, value: Int): Builder = apply { FilaRenderableManagerBuilder_layerMask(nativeBuilder, select.toUByte(), value.toUByte()) }
        actual fun priority(priority: Int): Builder = apply { FilaRenderableManagerBuilder_priority(nativeBuilder, priority.toUByte()) }
        actual fun channel(channel: Int): Builder = apply { FilaRenderableManagerBuilder_channel(nativeBuilder, channel.toUByte()) }
        actual fun culling(enabled: Boolean): Builder = apply { FilaRenderableManagerBuilder_culling(nativeBuilder, enabled) }
        actual fun castShadows(enabled: Boolean): Builder = apply { FilaRenderableManagerBuilder_castShadows(nativeBuilder, enabled) }
        actual fun receiveShadows(enabled: Boolean): Builder = apply { FilaRenderableManagerBuilder_receiveShadows(nativeBuilder, enabled) }
        actual fun screenSpaceContactShadows(enabled: Boolean): Builder = apply { FilaRenderableManagerBuilder_screenSpaceContactShadows(nativeBuilder, enabled) }
        actual fun skinning(boneCount: Int): Builder = apply { FilaRenderableManagerBuilder_skinning(nativeBuilder, boneCount.toUInt()) }
        actual fun skinning(boneCount: Int, bones: FloatArray): Builder = apply {
            bones.usePinned { pinned ->
                FilaRenderableManagerBuilder_skinningBones(nativeBuilder, boneCount.toUInt(), pinned.addressOf(0))
            }
        }
        actual fun skinning(skinningBuffer: SkinningBuffer, boneCount: Int, offset: Int): Builder = apply {
            FilaRenderableManagerBuilder_skinningBuffer(nativeBuilder, skinningBuffer.nativeHandle, boneCount.toUInt(), offset.toUInt())
        }
        actual fun enableSkinningBuffers(enabled: Boolean): Builder = apply {
            FilaRenderableManagerBuilder_enableSkinningBuffers(nativeBuilder, enabled)
        }
        actual fun morphing(targetCount: Int): Builder = apply { FilaRenderableManagerBuilder_morphing(nativeBuilder, targetCount.toUInt()) }
        actual fun morphing(morphTargetBuffer: MorphTargetBuffer): Builder = apply {
            FilaRenderableManagerBuilder_morphTargetBuffer(nativeBuilder, morphTargetBuffer.nativeHandle)
        }
        actual fun fog(enabled: Boolean): Builder = apply { FilaRenderableManagerBuilder_fog(nativeBuilder, enabled) }
        actual fun lightChannel(channel: Int, enable: Boolean): Builder = apply { FilaRenderableManagerBuilder_lightChannel(nativeBuilder, channel.toUInt(), enable) }
        actual fun instances(instanceCount: Int): Builder = apply { FilaRenderableManagerBuilder_instances(nativeBuilder, instanceCount.toULong()) }
        actual fun build(engine: Engine, entity: Entity) {
            FilaRenderableManagerBuilder_build(nativeBuilder, engine.nativeHandle, entity.toUInt())
            FilaRenderableManagerBuilder_destroy(nativeBuilder)
        }

        private fun PrimitiveType.toNative(): UInt = io.github.erkko68.filament.toNative(this)
        private fun GeometryType.toNative(): UInt = io.github.erkko68.filament.toNative(this)
    }

    actual fun hasComponent(entity: Entity): Boolean = FilaRenderableManager_hasComponent(nativeHandle, entity.toUInt())
    actual fun getInstance(entity: Entity): EntityInstance = FilaRenderableManager_getInstance(nativeHandle, entity.toUInt()).toInt()
    actual fun destroy(entity: Entity) = FilaRenderableManager_destroy(nativeHandle, entity.toUInt())
    
    actual fun setAxisAlignedBoundingBox(instance: EntityInstance, box: Box) {
        FilaRenderableManager_setAxisAlignedBoundingBox(nativeHandle, instance.toUInt(), 
            box.center[0], box.center[1], box.center[2],
            box.halfExtent[0], box.halfExtent[1], box.halfExtent[2])
    }
    actual fun getAxisAlignedBoundingBox(instance: EntityInstance, outBox: Box?): Box {
        memScoped {
            val center = allocArray<FloatVar>(3)
            val halfExtent = allocArray<FloatVar>(3)
            FilaRenderableManager_getAxisAlignedBoundingBox(nativeHandle, instance.toUInt(), center, halfExtent)
            val result = outBox ?: Box()
            result.center[0] = center[0]
            result.center[1] = center[1]
            result.center[2] = center[2]
            result.halfExtent[0] = halfExtent[0]
            result.halfExtent[1] = halfExtent[1]
            result.halfExtent[2] = halfExtent[2]
            return result
        }
    }
    
    actual fun setLayerMask(instance: EntityInstance, select: Int, value: Int) = FilaRenderableManager_setLayerMask(nativeHandle, instance.toUInt(), select.toUByte(), value.toUByte())
    actual fun setPriority(instance: EntityInstance, priority: Int) = FilaRenderableManager_setPriority(nativeHandle, instance.toUInt(), priority.toUByte())
    actual fun getPriority(instance: EntityInstance): Int = FilaRenderableManager_getPriority(nativeHandle, instance.toUInt()).toInt()
    actual fun setChannel(instance: EntityInstance, channel: Int) = FilaRenderableManager_setChannel(nativeHandle, instance.toUInt(), channel.toUByte())
    actual fun getChannel(instance: EntityInstance): Int = FilaRenderableManager_getChannel(nativeHandle, instance.toUInt()).toInt()
    actual fun setCulling(instance: EntityInstance, enabled: Boolean) = FilaRenderableManager_setCulling(nativeHandle, instance.toUInt(), enabled)
    actual fun isCullingEnabled(instance: EntityInstance): Boolean = FilaRenderableManager_isCullingEnabled(nativeHandle, instance.toUInt())
    actual fun setFogEnabled(instance: EntityInstance, enabled: Boolean) = FilaRenderableManager_setFogEnabled(nativeHandle, instance.toUInt(), enabled)
    actual fun getFogEnabled(instance: EntityInstance): Boolean = FilaRenderableManager_getFogEnabled(nativeHandle, instance.toUInt())
    actual fun setCastShadows(instance: EntityInstance, enabled: Boolean) = FilaRenderableManager_setCastShadows(nativeHandle, instance.toUInt(), enabled)
    actual fun setReceiveShadows(instance: EntityInstance, enabled: Boolean) = FilaRenderableManager_setReceiveShadows(nativeHandle, instance.toUInt(), enabled)
    actual fun setScreenSpaceContactShadows(instance: EntityInstance, enabled: Boolean) = FilaRenderableManager_setScreenSpaceContactShadows(nativeHandle, instance.toUInt(), enabled)
    actual fun isShadowCaster(instance: EntityInstance): Boolean = FilaRenderableManager_isShadowCaster(nativeHandle, instance.toUInt())
    actual fun isShadowReceiver(instance: EntityInstance): Boolean = FilaRenderableManager_isShadowReceiver(nativeHandle, instance.toUInt())
    actual fun isScreenSpaceContactShadowsEnabled(instance: EntityInstance): Boolean = FilaRenderableManager_isScreenSpaceContactShadowsEnabled(nativeHandle, instance.toUInt())
    
    actual fun getPrimitiveCount(instance: EntityInstance): Int = FilaRenderableManager_getPrimitiveCount(nativeHandle, instance.toUInt()).toInt()
    actual fun getInstanceCount(instance: EntityInstance): Int = FilaRenderableManager_getInstanceCount(nativeHandle, instance.toUInt()).toInt()
    
    actual fun setMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int, materialInstance: MaterialInstance) {
        FilaRenderableManager_setMaterialInstanceAt(nativeHandle, instance.toUInt(), primitiveIndex.toULong(), materialInstance.nativeHandle)
    }
        
    actual fun getMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int): MaterialInstance? {
        val handle = FilaRenderableManager_getMaterialInstanceAt(nativeHandle, instance.toUInt(), primitiveIndex.toULong())
        return if (handle != null) MaterialInstance(handle) else null
    }
    
    actual fun setGeometryAt(instance: EntityInstance, primitiveIndex: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, count: Int) = 
        FilaRenderableManager_setGeometryAt(nativeHandle, instance.toUInt(), primitiveIndex.toULong(), type.toNative(), vb.nativeHandle, ib.nativeHandle, offset.toULong(), count.toULong())
    
    actual fun setBlendOrderAt(instance: EntityInstance, primitiveIndex: Int, blendOrder: Int) = 
        FilaRenderableManager_setBlendOrderAt(nativeHandle, instance.toUInt(), primitiveIndex.toULong(), blendOrder.toUShort())
    actual fun getBlendOrderAt(instance: EntityInstance, primitiveIndex: Int): Int = FilaRenderableManager_getBlendOrderAt(nativeHandle, instance.toUInt(), primitiveIndex.toULong()).toInt()
    actual fun setGlobalBlendOrderEnabledAt(instance: EntityInstance, primitiveIndex: Int, enabled: Boolean) = 
        FilaRenderableManager_setGlobalBlendOrderEnabledAt(nativeHandle, instance.toUInt(), primitiveIndex.toULong(), enabled)
    actual fun isGlobalBlendOrderEnabledAt(instance: EntityInstance, primitiveIndex: Int): Boolean = 
        FilaRenderableManager_isGlobalBlendOrderEnabledAt(nativeHandle, instance.toUInt(), primitiveIndex.toULong())
    
    actual fun setLightChannel(instance: EntityInstance, channel: Int, enable: Boolean) = FilaRenderableManager_setLightChannel(nativeHandle, instance.toUInt(), channel.toUInt(), enable)
    actual fun getLightChannel(instance: EntityInstance, channel: Int): Boolean = FilaRenderableManager_getLightChannel(nativeHandle, instance.toUInt(), channel.toUInt())
 
    actual fun getMorphTargetCount(instance: EntityInstance): Int = FilaRenderableManager_getMorphTargetCount(nativeHandle, instance.toUInt()).toInt()
    
    actual fun setSkinningBuffer(instance: EntityInstance, skinningBuffer: SkinningBuffer, count: Int, offset: Int) {
        FilaRenderableManager_setSkinningBuffer(nativeHandle, instance.toUInt(), skinningBuffer.nativeHandle, count.toUInt(), offset.toUInt())
    }

    actual fun setMorphWeights(instance: EntityInstance, weights: FloatArray, offset: Int) {
        weights.usePinned { pinned ->
            FilaRenderableManager_setMorphWeights(nativeHandle, instance.toUInt(), pinned.addressOf(offset), (weights.size - offset).toUInt(), offset.toUInt())
        }
    }
 
    actual fun setMorphTargetBufferOffsetAt(instance: EntityInstance, level: Int, primitiveIndex: Int, offset: Int) {
        FilaRenderableManager_setMorphTargetBufferOffsetAt(nativeHandle, instance.toUInt(), level.toUByte(), primitiveIndex.toULong(), offset.toULong())
    }

    actual fun setBonesAsMatrices(instance: EntityInstance, matrices: FloatArray, boneCount: Int, offset: Int) {
        matrices.usePinned { pinned ->
            FilaRenderableManager_setBonesAsMatrices(
                nativeHandle, instance.toUInt(),
                pinned.addressOf(0),
                boneCount.toUInt(), offset.toUInt()
            )
        }
    }

    actual fun setBonesAsQuaternions(instance: EntityInstance, quaternions: FloatArray, boneCount: Int, offset: Int) {
        quaternions.usePinned { pinned ->
            FilaRenderableManager_setBonesAsQuaternions(
                nativeHandle, instance.toUInt(),
                pinned.addressOf(0),
                boneCount.toUInt(), offset.toUInt()
            )
        }
    }

    actual fun clearMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int) {
        FilaRenderableManager_clearMaterialInstanceAt(nativeHandle, instance.toUInt(), primitiveIndex.toULong())
    }

}

private fun toNative(type: RenderableManager.PrimitiveType): UInt = when (type) {
    RenderableManager.PrimitiveType.POINTS -> 0u
    RenderableManager.PrimitiveType.LINES -> 1u
    RenderableManager.PrimitiveType.LINE_STRIP -> 3u
    RenderableManager.PrimitiveType.TRIANGLES -> 4u
    RenderableManager.PrimitiveType.TRIANGLE_STRIP -> 5u
}

private fun toNative(type: RenderableManager.GeometryType): UInt = when (type) {
    RenderableManager.GeometryType.DYNAMIC -> FILA_RENDERABLE_MANAGER_GEOMETRY_TYPE_DYNAMIC
    RenderableManager.GeometryType.STATIC_BOUNDS -> FILA_RENDERABLE_MANAGER_GEOMETRY_TYPE_STATIC_BOUNDS
    RenderableManager.GeometryType.STATIC -> FILA_RENDERABLE_MANAGER_GEOMETRY_TYPE_STATIC
}

private fun RenderableManager.PrimitiveType.toNative(): UInt = toNative(this)
private fun RenderableManager.GeometryType.toNative(): UInt = toNative(this)
