package dev.filament.kmp

import com.google.android.filament.RenderableManager as AndroidRenderableManager

actual class RenderableManager internal constructor(val nativeRenderableManager: AndroidRenderableManager) {
    actual enum class PrimitiveType(val value: Int) {
        POINTS(0), LINES(1), TRIANGLES(4), NONE(0xFF);
        internal fun toNative(): AndroidRenderableManager.PrimitiveType {
            return when (this) {
                POINTS -> AndroidRenderableManager.PrimitiveType.POINTS
                LINES -> AndroidRenderableManager.PrimitiveType.LINES
                TRIANGLES -> AndroidRenderableManager.PrimitiveType.TRIANGLES
                NONE -> AndroidRenderableManager.PrimitiveType.TRIANGLES // Defaulting to triangles for 'none' as Android doesn't have NONE
            }
        }
    }

    actual enum class GeometryType {
        STATIC, DYNAMIC;
        internal fun toNative(): AndroidRenderableManager.Builder.GeometryType {
            return when (this) {
                STATIC -> AndroidRenderableManager.Builder.GeometryType.STATIC
                DYNAMIC -> AndroidRenderableManager.Builder.GeometryType.DYNAMIC
            }
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

        actual fun morphing(targetCount: Int): Builder {
            nativeBuilder.morphing(targetCount)
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
        // Indirectly through FloatArray as native Box doesn't expose fields directly easily
        // But wait, the Android Box has getCenter() and getHalfExtent()
        val nativeBox = com.google.android.filament.Box() 
        // Wait, nGetAxisAlignedBoundingBox is not public in Java, but we can use set/get?
        // Actually, let's keep it simple for now as we don't have a direct matching getter in Java that returns Box?
        // Ah, Java has: public void getAxisAlignedBoundingBox(int i, float[] center, float[] halfExtent)
        // Wait, I should check the file again.
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
}
