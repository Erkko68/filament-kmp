package dev.filament.kmp

import com.google.android.filament.RenderableManager as AndroidRenderableManager

actual class RenderableManager internal constructor(val nativeRenderableManager: AndroidRenderableManager) {
    actual enum class PrimitiveType(val value: Int) {
        POINTS(0), LINES(1), LINE_STRIP(3), TRIANGLES(4), TRIANGLE_STRIP(5);
        internal fun toNative(): AndroidRenderableManager.PrimitiveType {
            return AndroidRenderableManager.PrimitiveType.entries[ordinal]
        }
    }

    actual enum class GeometryType {
        DYNAMIC, STATIC_BOUNDS, STATIC;
        internal fun toNative(): AndroidRenderableManager.Builder.GeometryType {
            return AndroidRenderableManager.Builder.GeometryType.entries[ordinal]
        }
    }

    actual class Builder actual constructor(count: Int) {
        private val nativeBuilder = AndroidRenderableManager.Builder(count)

        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer): Builder {
            nativeBuilder.geometry(index, type.toNative(), vb.nativeVertexBuffer, ib.nativeIndexBuffer)
            return this
        }

        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, count: Int): Builder {
            nativeBuilder.geometry(index, type.toNative(), vb.nativeVertexBuffer, ib.nativeIndexBuffer, offset, count)
            return this
        }

        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, minIndex: Int, maxIndex: Int, count: Int): Builder {
            nativeBuilder.geometry(index, type.toNative(), vb.nativeVertexBuffer, ib.nativeIndexBuffer, offset, minIndex, maxIndex, count)
            return this
        }

        actual fun geometryType(type: GeometryType): Builder {
            nativeBuilder.geometryType(type.toNative())
            return this
        }

        actual fun material(index: Int, materialInstance: MaterialInstance): Builder {
            nativeBuilder.material(index, materialInstance.nativeMaterialInstance)
            return this
        }

        actual fun blendOrder(index: Int, blendOrder: Int): Builder {
            nativeBuilder.blendOrder(index, blendOrder)
            return this
        }

        actual fun globalBlendOrderEnabled(index: Int, enabled: Boolean): Builder {
            nativeBuilder.globalBlendOrderEnabled(index, enabled)
            return this
        }

        actual fun boundingBox(box: Box): Builder {
            nativeBuilder.boundingBox(com.google.android.filament.Box(
                box.center[0], box.center[1], box.center[2],
                box.halfExtent[0], box.halfExtent[1], box.halfExtent[2]
            ))
            return this
        }

        actual fun layerMask(select: Int, value: Int): Builder {
            nativeBuilder.layerMask(select, value)
            return this
        }

        actual fun priority(priority: Int): Builder {
            nativeBuilder.priority(priority)
            return this
        }

        actual fun channel(channel: Int): Builder {
            nativeBuilder.channel(channel)
            return this
        }

        actual fun culling(enabled: Boolean): Builder {
            nativeBuilder.culling(enabled)
            return this
        }

        actual fun castShadows(enabled: Boolean): Builder {
            nativeBuilder.castShadows(enabled)
            return this
        }

        actual fun receiveShadows(enabled: Boolean): Builder {
            nativeBuilder.receiveShadows(enabled)
            return this
        }

        actual fun screenSpaceContactShadows(enabled: Boolean): Builder {
            nativeBuilder.screenSpaceContactShadows(enabled)
            return this
        }

        actual fun skinning(boneCount: Int): Builder {
            nativeBuilder.skinning(boneCount)
            return this
        }

        actual fun skinning(boneCount: Int, bones: FloatArray): Builder {
            // Android uses Buffer, but we can wrap it
            nativeBuilder.skinning(boneCount, java.nio.FloatBuffer.wrap(bones))
            return this
        }

        actual fun skinning(skinningBuffer: SkinningBuffer, boneCount: Int, offset: Int): Builder {
            nativeBuilder.skinning(skinningBuffer.nativeSkinningBuffer, boneCount, offset)
            return this
        }

        actual fun morphing(targetCount: Int): Builder {
            nativeBuilder.morphing(targetCount)
            return this
        }

        actual fun morphing(morphTargetBuffer: MorphTargetBuffer): Builder {
            nativeBuilder.morphing(morphTargetBuffer.nativeMorphTargetBuffer)
            return this
        }

        actual fun fog(enabled: Boolean): Builder {
            nativeBuilder.fog(enabled)
            return this
        }

        actual fun lightChannel(channel: Int, enable: Boolean): Builder {
            nativeBuilder.lightChannel(channel, enable)
            return this
        }

        actual fun instances(instanceCount: Int): Builder {
            nativeBuilder.instances(instanceCount)
            return this
        }

        actual fun build(engine: Engine, entity: Entity) {
            nativeBuilder.build(engine.nativeEngine, entity)
        }
    }

    actual fun hasComponent(entity: Entity): Boolean = nativeRenderableManager.hasComponent(entity)
    actual fun getInstance(entity: Entity): EntityInstance = nativeRenderableManager.getInstance(entity)
    actual fun destroy(entity: Entity) = nativeRenderableManager.destroy(entity)
    
    actual fun setAxisAlignedBoundingBox(instance: EntityInstance, box: Box) {
        nativeRenderableManager.setAxisAlignedBoundingBox(instance, com.google.android.filament.Box(
            box.center[0], box.center[1], box.center[2],
            box.halfExtent[0], box.halfExtent[1], box.halfExtent[2]
        ))
    }
    
    actual fun getAxisAlignedBoundingBox(instance: EntityInstance, outBox: Box?): Box {
        val result = outBox ?: Box()
        val androidBox = com.google.android.filament.Box()
        nativeRenderableManager.getAxisAlignedBoundingBox(instance, androidBox)
        result.center[0] = androidBox.center[0]
        result.center[1] = androidBox.center[1]
        result.center[2] = androidBox.center[2]
        result.halfExtent[0] = androidBox.halfExtent[0]
        result.halfExtent[1] = androidBox.halfExtent[1]
        result.halfExtent[2] = androidBox.halfExtent[2]
        return result
    }
    
    actual fun setLayerMask(instance: EntityInstance, select: Int, value: Int) = nativeRenderableManager.setLayerMask(instance, select, value)
    actual fun setPriority(instance: EntityInstance, priority: Int) = nativeRenderableManager.setPriority(instance, priority)
    actual fun getPriority(instance: EntityInstance): Int = nativeRenderableManager.getPriority(instance)
    actual fun setChannel(instance: EntityInstance, channel: Int) = nativeRenderableManager.setChannel(instance, channel)
    actual fun getChannel(instance: EntityInstance): Int = nativeRenderableManager.getChannel(instance)
    actual fun setCulling(instance: EntityInstance, enabled: Boolean) = nativeRenderableManager.setCulling(instance, enabled)
    actual fun isCullingEnabled(instance: EntityInstance): Boolean = nativeRenderableManager.isCullingEnabled(instance)
    actual fun setFogEnabled(instance: EntityInstance, enabled: Boolean) = nativeRenderableManager.setFogEnabled(instance, enabled)
    actual fun getFogEnabled(instance: EntityInstance): Boolean = nativeRenderableManager.getFogEnabled(instance)
    actual fun setCastShadows(instance: EntityInstance, enabled: Boolean) = nativeRenderableManager.setCastShadows(instance, enabled)
    actual fun setReceiveShadows(instance: EntityInstance, enabled: Boolean) = nativeRenderableManager.setReceiveShadows(instance, enabled)
    actual fun setScreenSpaceContactShadows(instance: EntityInstance, enabled: Boolean) = nativeRenderableManager.setScreenSpaceContactShadows(instance, enabled)
    actual fun isShadowCaster(instance: EntityInstance): Boolean = nativeRenderableManager.isShadowCaster(instance)
    actual fun isShadowReceiver(instance: EntityInstance): Boolean = nativeRenderableManager.isShadowReceiver(instance)
    actual fun isScreenSpaceContactShadowsEnabled(instance: EntityInstance): Boolean = nativeRenderableManager.isScreenSpaceContactShadowsEnabled(instance)
    
    actual fun getPrimitiveCount(instance: EntityInstance): Int = nativeRenderableManager.getPrimitiveCount(instance)
    actual fun getInstanceCount(instance: EntityInstance): Int = nativeRenderableManager.getInstanceCount(instance)
    
    actual fun setMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int, materialInstance: MaterialInstance) = 
        nativeRenderableManager.setMaterialInstanceAt(instance, primitiveIndex, materialInstance.nativeMaterialInstance)
        
    actual fun getMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int): MaterialInstance? {
        val nativeInstance = nativeRenderableManager.getMaterialInstanceAt(instance, primitiveIndex)
        return if (nativeInstance != null) MaterialInstance(nativeInstance) else null
    }
    
    actual fun setGeometryAt(instance: EntityInstance, primitiveIndex: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, count: Int) = 
        nativeRenderableManager.setGeometryAt(instance, primitiveIndex, type.toNative(), vb.nativeVertexBuffer, ib.nativeIndexBuffer, offset, count)
    
    actual fun setBlendOrderAt(instance: EntityInstance, primitiveIndex: Int, blendOrder: Int) = 
        nativeRenderableManager.setBlendOrderAt(instance, primitiveIndex, blendOrder)
        
    actual fun getBlendOrderAt(instance: EntityInstance, primitiveIndex: Int): Int = 
        nativeRenderableManager.getBlendOrderAt(instance, primitiveIndex)
        
    actual fun setGlobalBlendOrderEnabledAt(instance: EntityInstance, primitiveIndex: Int, enabled: Boolean) = 
        nativeRenderableManager.setGlobalBlendOrderEnabledAt(instance, primitiveIndex, enabled)
        
    actual fun isGlobalBlendOrderEnabledAt(instance: EntityInstance, primitiveIndex: Int): Boolean = 
        nativeRenderableManager.isGlobalBlendOrderEnabledAt(instance, primitiveIndex)
    
    actual fun setLightChannel(instance: EntityInstance, channel: Int, enable: Boolean) = 
        nativeRenderableManager.setLightChannel(instance, channel, enable)
        
    actual fun getLightChannel(instance: EntityInstance, channel: Int): Boolean = 
        nativeRenderableManager.getLightChannel(instance, channel)
 
    actual fun getMorphTargetCount(instance: EntityInstance): Int = nativeRenderableManager.getMorphTargetCount(instance)
    
    actual fun setSkinningBuffer(instance: EntityInstance, skinningBuffer: SkinningBuffer, count: Int, offset: Int) {
        nativeRenderableManager.setSkinningBuffer(instance, skinningBuffer.nativeSkinningBuffer, count, offset)
    }
    
    actual fun setMorphWeights(instance: EntityInstance, weights: FloatArray, offset: Int) {
        nativeRenderableManager.setMorphWeights(instance, weights, offset)
    }
 
    actual fun setMorphTargetBufferOffsetAt(instance: EntityInstance, level: Int, primitiveIndex: Int, offset: Int) {
        nativeRenderableManager.setMorphTargetBufferOffsetAt(instance, level, primitiveIndex, offset)
    }
}
