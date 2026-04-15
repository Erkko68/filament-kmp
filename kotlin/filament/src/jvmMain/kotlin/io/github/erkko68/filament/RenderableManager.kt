package io.github.erkko68.filament

import io.github.erkko68.filament.jni.Box as JniBox
import java.nio.Buffer

actual class RenderableManager(val nativeRenderableManager: io.github.erkko68.filament.jni.RenderableManager) {
    actual enum class PrimitiveType { 
        POINTS, LINES, LINE_STRIP, TRIANGLES, TRIANGLE_STRIP;
        internal fun toJni() = io.github.erkko68.filament.jni.RenderableManager.PrimitiveType.values()[ordinal]
    }
    actual enum class GeometryType { 
        DYNAMIC, STATIC_BOUNDS, STATIC;
        internal fun toJni() = io.github.erkko68.filament.jni.RenderableManager.Builder.GeometryType.values()[ordinal]
    }

    actual class Builder actual constructor(count: Int) {
        private val jni = io.github.erkko68.filament.jni.RenderableManager.Builder(count)

        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer): Builder {
            jni.geometry(index, type.toJni(), vb.nativeVertexBuffer, ib.nativeIndexBuffer)
            return this
        }

        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, count: Int): Builder {
            jni.geometry(index, type.toJni(), vb.nativeVertexBuffer, ib.nativeIndexBuffer, offset, count)
            return this
        }

        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, minIndex: Int, maxIndex: Int, count: Int): Builder {
            jni.geometry(index, type.toJni(), vb.nativeVertexBuffer, ib.nativeIndexBuffer, offset, minIndex, maxIndex, count)
            return this
        }
        
        actual fun geometryType(type: GeometryType): Builder {
            jni.geometryType(type.toJni())
            return this
        }

        actual fun material(index: Int, materialInstance: MaterialInstance): Builder {
            jni.material(index, materialInstance.nativeMaterialInstance)
            return this
        }

        actual fun blendOrder(index: Int, blendOrder: Int): Builder {
            jni.blendOrder(index, blendOrder)
            return this
        }

        actual fun globalBlendOrderEnabled(index: Int, enabled: Boolean): Builder {
            jni.globalBlendOrderEnabled(index, enabled)
            return this
        }

        actual fun boundingBox(box: Box): Builder {
            jni.boundingBox(JniBox(box.center, box.halfExtent))
            return this
        }

        actual fun layerMask(select: Int, value: Int): Builder {
            jni.layerMask(select, value)
            return this
        }

        actual fun priority(priority: Int): Builder {
            jni.priority(priority)
            return this
        }

        actual fun channel(channel: Int): Builder {
            jni.channel(channel)
            return this
        }

        actual fun culling(enabled: Boolean): Builder {
            jni.culling(enabled)
            return this
        }

        actual fun castShadows(enabled: Boolean): Builder {
            jni.castShadows(enabled)
            return this
        }

        actual fun receiveShadows(enabled: Boolean): Builder {
            jni.receiveShadows(enabled)
            return this
        }

        actual fun screenSpaceContactShadows(enabled: Boolean): Builder {
            jni.screenSpaceContactShadows(enabled)
            return this
        }

        actual fun skinning(boneCount: Int): Builder {
            jni.skinning(boneCount)
            return this
        }

        actual fun skinning(boneCount: Int, bones: FloatArray): Builder {
            val buffer = java.nio.ByteBuffer.allocateDirect(bones.size * 4).order(java.nio.ByteOrder.nativeOrder()).asFloatBuffer()
            buffer.put(bones)
            buffer.rewind()
            jni.skinning(boneCount, buffer)
            return this
        }

        actual fun skinning(skinningBuffer: SkinningBuffer, boneCount: Int, offset: Int): Builder {
            jni.skinning(skinningBuffer.nativeSkinningBuffer, boneCount, offset)
            return this
        }

        actual fun morphing(targetCount: Int): Builder {
            jni.morphing(targetCount)
            return this
        }

        actual fun morphing(morphTargetBuffer: MorphTargetBuffer): Builder {
            jni.morphing(morphTargetBuffer.nativeMorphTargetBuffer)
            return this
        }

        actual fun fog(enabled: Boolean): Builder {
            jni.fog(enabled)
            return this
        }

        actual fun lightChannel(channel: Int, enable: Boolean): Builder {
            jni.lightChannel(channel, enable)
            return this
        }

        actual fun instances(instanceCount: Int): Builder {
            jni.instances(instanceCount)
            return this
        }

        actual fun build(engine: Engine, entity: Entity) {
            jni.build(engine.nativeEngine, entity)
        }
    }

    actual fun hasComponent(entity: Entity): Boolean = nativeRenderableManager.hasComponent(entity)
    actual fun getInstance(entity: Entity): EntityInstance = nativeRenderableManager.getInstance(entity)
    actual fun destroy(entity: Entity) = nativeRenderableManager.destroy(entity)
    
    actual fun setAxisAlignedBoundingBox(instance: EntityInstance, box: Box) {
        nativeRenderableManager.setAxisAlignedBoundingBox(instance, JniBox(box.center, box.halfExtent))
    }
    
    actual fun getAxisAlignedBoundingBox(instance: EntityInstance, outBox: Box?): Box {
        val result = outBox ?: Box()
        val jniBox = nativeRenderableManager.getAxisAlignedBoundingBox(instance, null)
        val center = jniBox.center
        val halfExtent = jniBox.halfExtent
        result.center[0] = center[0]
        result.center[1] = center[1]
        result.center[2] = center[2]
        result.halfExtent[0] = halfExtent[0]
        result.halfExtent[1] = halfExtent[1]
        result.halfExtent[2] = halfExtent[2]
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
        val jni = nativeRenderableManager.getMaterialInstanceAt(instance, primitiveIndex) ?: return null
        return MaterialInstance(jni)
    }
    
    actual fun setGeometryAt(instance: EntityInstance, primitiveIndex: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, count: Int) =
        nativeRenderableManager.setGeometryAt(instance, primitiveIndex, type.toJni(), vb.nativeVertexBuffer, ib.nativeIndexBuffer, offset, count)
    
    actual fun setBlendOrderAt(instance: EntityInstance, primitiveIndex: Int, blendOrder: Int) = nativeRenderableManager.setBlendOrderAt(instance, primitiveIndex, blendOrder)
    actual fun getBlendOrderAt(instance: EntityInstance, primitiveIndex: Int): Int = nativeRenderableManager.getBlendOrderAt(instance, primitiveIndex)
    actual fun setGlobalBlendOrderEnabledAt(instance: EntityInstance, primitiveIndex: Int, enabled: Boolean) = nativeRenderableManager.setGlobalBlendOrderEnabledAt(instance, primitiveIndex, enabled)
    actual fun isGlobalBlendOrderEnabledAt(instance: EntityInstance, primitiveIndex: Int): Boolean = nativeRenderableManager.isGlobalBlendOrderEnabledAt(instance, primitiveIndex)
    
    actual fun setLightChannel(instance: EntityInstance, channel: Int, enable: Boolean) =
        nativeRenderableManager.setLightChannel(instance, channel, enable)

    actual fun getLightChannel(instance: EntityInstance, channel: Int): Boolean =
        nativeRenderableManager.getLightChannel(instance, channel)
 
    actual fun getMorphTargetCount(instance: EntityInstance): Int = nativeRenderableManager.getMorphTargetCount(instance)
    actual fun setSkinningBuffer(instance: EntityInstance, skinningBuffer: SkinningBuffer, count: Int, offset: Int) =
        nativeRenderableManager.setSkinningBuffer(instance, skinningBuffer.nativeSkinningBuffer, count, offset)
    
    actual fun setMorphWeights(instance: EntityInstance, weights: FloatArray, offset: Int) = nativeRenderableManager.setMorphWeights(instance, weights, offset)
    actual fun setMorphTargetBufferOffsetAt(instance: EntityInstance, level: Int, primitiveIndex: Int, offset: Int) =
        nativeRenderableManager.setMorphTargetBufferOffsetAt(instance, level, primitiveIndex, offset)
}
