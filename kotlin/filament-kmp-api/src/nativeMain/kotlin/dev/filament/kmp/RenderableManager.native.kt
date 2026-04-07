@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaRenderableManager
import cnames.structs.FilaRenderableManagerBuilder

actual class RenderableManager internal constructor(internal var nativeHandle: CPointer<FilaRenderableManager>?) {
    actual enum class PrimitiveType(val value: Int) {
        POINTS(0), LINES(1), TRIANGLES(4), NONE(0xFF);
        internal fun toNative(): FilaRenderableManagerPrimitiveType = when (this) {
            POINTS -> FILA_RENDERABLE_MANAGER_PRIMITIVE_TYPE_POINTS
            LINES -> FILA_RENDERABLE_MANAGER_PRIMITIVE_TYPE_LINES
            TRIANGLES -> FILA_RENDERABLE_MANAGER_PRIMITIVE_TYPE_TRIANGLES
            NONE -> FILA_RENDERABLE_MANAGER_PRIMITIVE_TYPE_NONE
        }
    }

    actual enum class GeometryType {
        STATIC, DYNAMIC;
        internal fun toNative(): FilaRenderableManagerGeometryType = when (this) {
            STATIC -> FILA_RENDERABLE_MANAGER_GEOMETRY_TYPE_STATIC
            DYNAMIC -> FILA_RENDERABLE_MANAGER_GEOMETRY_TYPE_DYNAMIC
        }
    }

    actual class Builder actual constructor(count: Int) {
        private val nativeBuilder = FilaRenderableManagerBuilder_create(count.toULong())!!

        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer): Builder {
            FilaRenderableManagerBuilder_geometry(nativeBuilder, index.toULong(), type.toNative(), vb.nativeHandle, ib.nativeHandle)
            return this
        }

        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, count: Int): Builder {
            FilaRenderableManagerBuilder_geometryAt(nativeBuilder, index.toULong(), type.toNative(), vb.nativeHandle, ib.nativeHandle, offset.toULong(), count.toULong())
            return this
        }

        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, minIndex: Int, maxIndex: Int, count: Int): Builder {
            FilaRenderableManagerBuilder_geometryWithIndices(nativeBuilder, index.toULong(), type.toNative(), vb.nativeHandle, ib.nativeHandle, offset.toULong(), minIndex.toULong(), maxIndex.toULong(), count.toULong())
            return this
        }

        actual fun geometryType(type: GeometryType): Builder {
            FilaRenderableManagerBuilder_geometryType(nativeBuilder, type.toNative())
            return this
        }

        actual fun material(index: Int, materialInstance: MaterialInstance): Builder {
            FilaRenderableManagerBuilder_material(nativeBuilder, index.toULong(), materialInstance.nativeHandle)
            return this
        }

        actual fun blendOrder(index: Int, blendOrder: Int): Builder {
            FilaRenderableManagerBuilder_blendOrder(nativeBuilder, index.toULong(), blendOrder.toUShort())
            return this
        }

        actual fun globalBlendOrderEnabled(index: Int, enabled: Boolean): Builder {
            FilaRenderableManagerBuilder_globalBlendOrderEnabled(nativeBuilder, index.toULong(), enabled)
            return this
        }

        actual fun boundingBox(box: Box): Builder {
            FilaRenderableManagerBuilder_boundingBox(nativeBuilder,
                box.center[0], box.center[1], box.center[2],
                box.halfExtent[0], box.halfExtent[1], box.halfExtent[2]
            )
            return this
        }

        actual fun layerMask(select: Int, value: Int): Builder {
            FilaRenderableManagerBuilder_layerMask(nativeBuilder, select.toUByte(), value.toUByte())
            return this
        }

        actual fun priority(priority: Int): Builder {
            FilaRenderableManagerBuilder_priority(nativeBuilder, priority.toUByte())
            return this
        }

        actual fun channel(channel: Int): Builder {
            FilaRenderableManagerBuilder_channel(nativeBuilder, channel.toUByte())
            return this
        }

        actual fun culling(enabled: Boolean): Builder {
            FilaRenderableManagerBuilder_culling(nativeBuilder, enabled)
            return this
        }

        actual fun castShadows(enabled: Boolean): Builder {
            FilaRenderableManagerBuilder_castShadows(nativeBuilder, enabled)
            return this
        }

        actual fun receiveShadows(enabled: Boolean): Builder {
            FilaRenderableManagerBuilder_receiveShadows(nativeBuilder, enabled)
            return this
        }

        actual fun screenSpaceContactShadows(enabled: Boolean): Builder {
            FilaRenderableManagerBuilder_screenSpaceContactShadows(nativeBuilder, enabled)
            return this
        }

        actual fun skinning(boneCount: Int): Builder {
            FilaRenderableManagerBuilder_skinning(nativeBuilder, boneCount.toUInt())
            return this
        }

        actual fun skinning(boneCount: Int, bones: FloatArray): Builder {
            bones.usePinned { 
                FilaRenderableManagerBuilder_skinningBones(nativeBuilder, boneCount.toUInt(), it.addressOf(0))
            }
            return this
        }

        actual fun morphing(targetCount: Int): Builder {
            FilaRenderableManagerBuilder_morphing(nativeBuilder, targetCount.toUInt())
            return this
        }

        actual fun fog(enabled: Boolean): Builder {
            FilaRenderableManagerBuilder_fog(nativeBuilder, enabled)
            return this
        }

        actual fun lightChannel(channel: Int, enable: Boolean): Builder {
            FilaRenderableManagerBuilder_lightChannel(nativeBuilder, channel.toUInt(), enable)
            return this
        }

        actual fun instances(instanceCount: Int): Builder {
            FilaRenderableManagerBuilder_instances(nativeBuilder, instanceCount.toULong())
            return this
        }

        actual fun build(engine: Engine, entity: Entity) {
            FilaRenderableManagerBuilder_build(nativeBuilder, engine.nativeHandle, entity.toUInt())
            FilaRenderableManagerBuilder_destroy(nativeBuilder)
        }
    }

    actual fun hasComponent(entity: Entity): Boolean = FilaRenderableManager_hasComponent(nativeHandle, entity.toUInt())
    actual fun getInstance(entity: Entity): EntityInstance = FilaRenderableManager_getInstance(nativeHandle, entity.toUInt()).toInt()
    actual fun destroy(entity: Entity) = FilaRenderableManager_destroy(nativeHandle, entity.toUInt())
    
    actual fun setAxisAlignedBoundingBox(instance: EntityInstance, box: Box) {
        FilaRenderableManager_setAxisAlignedBoundingBox(nativeHandle, instance.toUInt(),
            box.center[0], box.center[1], box.center[2],
            box.halfExtent[0], box.halfExtent[1], box.halfExtent[2]
        )
    }
    
    actual fun getAxisAlignedBoundingBox(instance: EntityInstance, outBox: Box?): Box {
        val result = outBox ?: Box()
        result.center.usePinned { centerPtr ->
            result.halfExtent.usePinned { halfPtr ->
                FilaRenderableManager_getAxisAlignedBoundingBox(nativeHandle, instance.toUInt(), centerPtr.addressOf(0), halfPtr.addressOf(0))
            }
        }
        return result
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
    
    actual fun setMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int, materialInstance: MaterialInstance) = 
        FilaRenderableManager_setMaterialInstanceAt(nativeHandle, instance.toUInt(), primitiveIndex.toULong(), materialInstance.nativeHandle)
        
    actual fun getMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int): MaterialInstance? {
        val handle = FilaRenderableManager_getMaterialInstanceAt(nativeHandle, instance.toUInt(), primitiveIndex.toULong())
        return if (handle != null) MaterialInstance(handle) else null
    }
    
    actual fun setGeometryAt(instance: EntityInstance, primitiveIndex: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, count: Int) = 
        FilaRenderableManager_setGeometryAt(nativeHandle, instance.toUInt(), primitiveIndex.toULong(), type.toNative(), vb.nativeHandle, ib.nativeHandle, offset.toULong(), count.toULong())
    
    actual fun setBlendOrderAt(instance: EntityInstance, primitiveIndex: Int, blendOrder: Int) = 
        FilaRenderableManager_setBlendOrderAt(nativeHandle, instance.toUInt(), primitiveIndex.toULong(), blendOrder.toUShort())
        
    actual fun getBlendOrderAt(instance: EntityInstance, primitiveIndex: Int): Int = 
        FilaRenderableManager_getBlendOrderAt(nativeHandle, instance.toUInt(), primitiveIndex.toULong()).toInt()
        
    actual fun setGlobalBlendOrderEnabledAt(instance: EntityInstance, primitiveIndex: Int, enabled: Boolean) = 
        FilaRenderableManager_setGlobalBlendOrderEnabledAt(nativeHandle, instance.toUInt(), primitiveIndex.toULong(), enabled)
        
    actual fun isGlobalBlendOrderEnabledAt(instance: EntityInstance, primitiveIndex: Int): Boolean = 
        FilaRenderableManager_isGlobalBlendOrderEnabledAt(nativeHandle, instance.toUInt(), primitiveIndex.toULong())
    
    actual fun setLightChannel(instance: EntityInstance, channel: Int, enable: Boolean) = 
        FilaRenderableManager_setLightChannel(nativeHandle, instance.toUInt(), channel.toUInt(), enable)
        
    actual fun getLightChannel(instance: EntityInstance, channel: Int): Boolean = 
        FilaRenderableManager_getLightChannel(nativeHandle, instance.toUInt(), channel.toUInt())
}
