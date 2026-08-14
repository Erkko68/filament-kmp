package io.github.erkko68.filament

import io.github.erkko68.filament.web.RenderableManager_Instance as JSRenderableManagerInstance

import io.github.erkko68.filament.web.interop.toFloatArray

import io.github.erkko68.filament.web.interop.toJsArray

import io.github.erkko68.filament.web.interop.emptyJsObject

import io.github.erkko68.filament.web.interop.jsNumbers
import io.github.erkko68.filament.web.interop.toJsNumbers

import io.github.erkko68.filament.web.RenderableManager as JSRenderableManager
import io.github.erkko68.filament.web.`RenderableManager_Builder` as JSRenderableManagerBuilder
import io.github.erkko68.filament.web.RenderableManager_PrimitiveType
import io.github.erkko68.filament.web.Entity as JSEntity

actual class RenderableManager(internal val jsRenderableManager: JSRenderableManager) {
    actual fun hasComponent(entity: Entity): Boolean {
        return jsRenderableManager.hasComponent(EntityManager.jsEntityOf(entity))
    }

    actual fun getInstance(entity: Entity): EntityInstance {
        return InstanceRegistry.register(jsRenderableManager.getInstance(EntityManager.jsEntityOf(entity)))
    }

    actual fun destroy(entity: Entity) {
        jsRenderableManager.destroy(EntityManager.jsEntityOf(entity))
    }

    actual fun setAxisAlignedBoundingBox(
        instance: EntityInstance,
        box: Box
    ) {
        val jsBox = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.Box>()
        jsBox.center = box.center.toJsNumbers()
        jsBox.halfExtent = box.halfExtent.toJsNumbers()
        jsRenderableManager.setAxisAlignedBoundingBox(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), jsBox)
    }

    actual fun getAxisAlignedBoundingBox(
        instance: EntityInstance,
        outBox: Box?
    ): Box {
        val jsBox = jsRenderableManager.getAxisAlignedBoundingBox(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>())
        val center = jsBox.center!!.toFloatArray(3)
        val halfExtent = jsBox.halfExtent!!.toFloatArray(3)
        
        val result = outBox ?: Box()
        for (i in 0 until 3) {
            result.center[i] = center[i]
            result.halfExtent[i] = halfExtent[i]
        }
        return result
    }

    actual fun setLayerMask(
        instance: EntityInstance,
        select: Int,
        value: Int
    ) {
        jsRenderableManager.setLayerMask(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), select.toDouble(), value.toDouble())
    }

    actual fun setPriority(instance: EntityInstance, priority: Int) {
        jsRenderableManager.setPriority(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), priority.toDouble())
    }

    actual fun getPriority(instance: EntityInstance): Int =
        jsRenderableManager.getPriority(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>()).toInt()

    actual fun setChannel(instance: EntityInstance, channel: Int) {
        jsRenderableManager.setChannel(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), channel.toDouble())
    }

    actual fun getChannel(instance: EntityInstance): Int =
        jsRenderableManager.getChannel(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>()).toInt()

    actual fun setCulling(instance: EntityInstance, enabled: Boolean) {
        jsRenderableManager.setCulling(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), enabled)
    }

    actual fun isCullingEnabled(instance: EntityInstance): Boolean =
        jsRenderableManager.isCullingEnabled(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>())

    actual fun setFogEnabled(instance: EntityInstance, enabled: Boolean) {
        jsRenderableManager.setFogEnabled(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), enabled)
    }

    actual fun getFogEnabled(instance: EntityInstance): Boolean =
        jsRenderableManager.getFogEnabled(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>())

    actual fun setCastShadows(instance: EntityInstance, enabled: Boolean) {
        jsRenderableManager.setCastShadows(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), enabled)
    }

    actual fun setReceiveShadows(instance: EntityInstance, enabled: Boolean) {
        jsRenderableManager.setReceiveShadows(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), enabled)
    }

    actual fun setScreenSpaceContactShadows(
        instance: EntityInstance,
        enabled: Boolean
    ) {
        jsRenderableManager.setScreenSpaceContactShadows(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), enabled)
    }

    actual fun isShadowCaster(instance: EntityInstance): Boolean {
        return jsRenderableManager.isShadowCaster(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>())
    }

    actual fun setBonesAsMatrices(
        instance: EntityInstance,
        matrices: FloatArray,
        boneCount: Int,
        offset: Int
    ) {
        jsRenderableManager.setBonesFromMatrices(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), matrices.toJsNumbers(), offset.toDouble())
    }

    actual fun setBonesAsQuaternions(
        instance: EntityInstance,
        quaternions: FloatArray,
        boneCount: Int,
        offset: Int
    ) {
        // jsRenderableManager.setBones accepts an Array<{ unitQuaternion, translation }>.
        // Pack each 4-float slice of the flat input into one Bone object with an
        // identity translation (this overload only carries rotations).
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        val bones = List(boneCount) { i ->
            val b = i * 4
            val bone = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`RenderableManager_Bone`>()
            bone.unitQuaternion = jsNumbers(quaternions[b + 0], quaternions[b + 1],
                quaternions[b + 2], quaternions[b + 3])
            bone.translation = jsNumbers(0f, 0f, 0f)
            bone
        }
        jsRenderableManager.setBones(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), bones.toJsArray(), offset.toDouble())
    }

    actual fun clearMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int) {
        jsRenderableManager.clearMaterialInstanceAt(
            InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), primitiveIndex.toDouble())
    }

    actual fun isShadowReceiver(instance: EntityInstance): Boolean {
        return jsRenderableManager.isShadowReceiver(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>())
    }

    actual fun isScreenSpaceContactShadowsEnabled(instance: EntityInstance): Boolean =
        jsRenderableManager.isScreenSpaceContactShadowsEnabled(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>())

    actual fun getPrimitiveCount(instance: EntityInstance): Int {
        return jsRenderableManager.getPrimitiveCount(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>()).toInt()
    }

    actual fun getInstanceCount(instance: EntityInstance): Int =
        jsRenderableManager.getInstanceCount(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>()).toInt()

    actual fun setMaterialInstanceAt(
        instance: EntityInstance,
        primitiveIndex: Int,
        materialInstance: MaterialInstance
    ) {
        jsRenderableManager.setMaterialInstanceAt(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), primitiveIndex.toDouble(), materialInstance.jsMaterialInstance)
    }

    actual fun getMaterialInstanceAt(
        instance: EntityInstance,
        primitiveIndex: Int
    ): MaterialInstance? {
        val jsMat = jsRenderableManager.getMaterialInstanceAt(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), primitiveIndex.toDouble())
        return if (jsMat != null) MaterialInstance(jsMat) else null
    }

    actual fun getEnabledAttributesAt(instance: EntityInstance, primitiveIndex: Int): Set<VertexBuffer.VertexAttribute> =
        attributeBitsetToSet(jsRenderableManager.getEnabledAttributesAt(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), primitiveIndex.toDouble()).toInt())

    actual fun setGeometryAt(
        instance: EntityInstance,
        primitiveIndex: Int,
        type: PrimitiveType,
        vb: VertexBuffer,
        ib: IndexBuffer,
        offset: Int,
        count: Int
    ) {
        val jsType = when (type) {
            PrimitiveType.POINTS -> RenderableManager_PrimitiveType.POINTS
            PrimitiveType.LINES -> RenderableManager_PrimitiveType.LINES
            PrimitiveType.LINE_STRIP -> RenderableManager_PrimitiveType.LINE_STRIP
            PrimitiveType.TRIANGLES -> RenderableManager_PrimitiveType.TRIANGLES
            PrimitiveType.TRIANGLE_STRIP -> RenderableManager_PrimitiveType.TRIANGLE_STRIP
        }
        jsRenderableManager.setGeometryAt(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), primitiveIndex.toDouble(), jsType, vb.jsVertexBuffer, ib.jsIndexBuffer, offset.toDouble(), count.toDouble())
    }

    actual fun setGeometryAt(
        instance: EntityInstance,
        primitiveIndex: Int,
        type: PrimitiveType,
        vb: VertexBuffer,
        offset: Int,
        count: Int
    ) {
        val jsType = when (type) {
            PrimitiveType.POINTS -> RenderableManager_PrimitiveType.POINTS
            PrimitiveType.LINES -> RenderableManager_PrimitiveType.LINES
            PrimitiveType.LINE_STRIP -> RenderableManager_PrimitiveType.LINE_STRIP
            PrimitiveType.TRIANGLES -> RenderableManager_PrimitiveType.TRIANGLES
            PrimitiveType.TRIANGLE_STRIP -> RenderableManager_PrimitiveType.TRIANGLE_STRIP
        }
        jsRenderableManager.setGeometryNoIndicesAt(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), primitiveIndex.toDouble(), jsType, vb.jsVertexBuffer, offset.toDouble(), count.toDouble())
    }

    actual fun setBlendOrderAt(
        instance: EntityInstance,
        primitiveIndex: Int,
        blendOrder: Int
    ) {
        jsRenderableManager.setBlendOrderAt(InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), primitiveIndex.toDouble(), blendOrder.toDouble())
    }

    actual fun getBlendOrderAt(
        instance: EntityInstance,
        primitiveIndex: Int
    ): Int = jsRenderableManager.getBlendOrderAt(
        InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), primitiveIndex.toDouble()).toInt()

    actual fun setGlobalBlendOrderEnabledAt(
        instance: EntityInstance,
        primitiveIndex: Int,
        enabled: Boolean
    ) {
        jsRenderableManager.setGlobalBlendOrderEnabledAt(
            InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), primitiveIndex.toDouble(), enabled)
    }

    actual fun isGlobalBlendOrderEnabledAt(
        instance: EntityInstance,
        primitiveIndex: Int
    ): Boolean = jsRenderableManager.isGlobalBlendOrderEnabledAt(
        InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), primitiveIndex.toDouble())

    actual fun setLightChannel(
        instance: EntityInstance,
        channel: Int,
        enable: Boolean
    ) {
        jsRenderableManager.setLightChannel(
            InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), channel.toDouble(), enable)
    }

    actual fun getLightChannel(
        instance: EntityInstance,
        channel: Int
    ): Boolean = jsRenderableManager.getLightChannel(
        InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(), channel.toDouble())

    actual fun getMorphTargetCount(instance: EntityInstance): Int =
        jsRenderableManager.getMorphTargetCount(
            InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>()).toInt()

    actual fun setSkinningBuffer(
        instance: EntityInstance,
        skinningBuffer: SkinningBuffer,
        count: Int,
        offset: Int
    ) {
        jsRenderableManager.setSkinningBuffer(
            InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(),
            skinningBuffer.jsSkinningBuffer, count.toDouble(), offset.toDouble())
    }

    actual fun setMorphWeights(
        instance: EntityInstance,
        weights: FloatArray,
        offset: Int
    ) {
        // The 4-argument setMorphWeights takes neither an offset nor a variable count.
        jsRenderableManager.setMorphWeightsOffset(
            InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(),
            weights.toJsNumbers(), offset.toDouble())
    }

    actual fun setMorphTargetBufferOffsetAt(
        instance: EntityInstance,
        level: Int,
        primitiveIndex: Int,
        offset: Int
    ) {
        jsRenderableManager.setMorphTargetBufferOffsetAt(
            InstanceRegistry.get(instance).unsafeCast<JSRenderableManagerInstance>(),
            level.toDouble(), primitiveIndex.toDouble(), offset.toDouble())
    }

    actual enum class PrimitiveType { POINTS, LINES, LINE_STRIP, TRIANGLES, TRIANGLE_STRIP }
    actual enum class GeometryType { DYNAMIC, STATIC_BOUNDS, STATIC }
    actual class Builder actual constructor(count: Int) {
        private val jsBuilder: JSRenderableManagerBuilder = JSRenderableManager.Builder(count.toDouble())

        actual fun geometry(
            index: Int,
            type: PrimitiveType,
            vb: VertexBuffer,
            ib: IndexBuffer
        ): Builder {
            val jsType = when (type) {
                PrimitiveType.POINTS -> RenderableManager_PrimitiveType.POINTS
                PrimitiveType.LINES -> RenderableManager_PrimitiveType.LINES
                PrimitiveType.LINE_STRIP -> RenderableManager_PrimitiveType.LINE_STRIP
                PrimitiveType.TRIANGLES -> RenderableManager_PrimitiveType.TRIANGLES
                PrimitiveType.TRIANGLE_STRIP -> RenderableManager_PrimitiveType.TRIANGLE_STRIP
            }
            jsBuilder.geometry(index.toDouble(), jsType, vb.jsVertexBuffer, ib.jsIndexBuffer)
            return this
        }

        actual fun geometry(
            index: Int,
            type: PrimitiveType,
            vb: VertexBuffer,
            ib: IndexBuffer,
            offset: Int,
            count: Int
        ): Builder {
            val jsType = when (type) {
                PrimitiveType.POINTS -> RenderableManager_PrimitiveType.POINTS
                PrimitiveType.LINES -> RenderableManager_PrimitiveType.LINES
                PrimitiveType.LINE_STRIP -> RenderableManager_PrimitiveType.LINE_STRIP
                PrimitiveType.TRIANGLES -> RenderableManager_PrimitiveType.TRIANGLES
                PrimitiveType.TRIANGLE_STRIP -> RenderableManager_PrimitiveType.TRIANGLE_STRIP
            }
            jsBuilder.geometryOffset(index.toDouble(), jsType, vb.jsVertexBuffer, ib.jsIndexBuffer, offset.toDouble(), count.toDouble())
            return this
        }

        actual fun geometry(
            index: Int,
            type: PrimitiveType,
            vb: VertexBuffer,
            ib: IndexBuffer,
            offset: Int,
            minIndex: Int,
            maxIndex: Int,
            count: Int
        ): Builder {
            val jsType = when (type) {
                PrimitiveType.POINTS -> RenderableManager_PrimitiveType.POINTS
                PrimitiveType.LINES -> RenderableManager_PrimitiveType.LINES
                PrimitiveType.LINE_STRIP -> RenderableManager_PrimitiveType.LINE_STRIP
                PrimitiveType.TRIANGLES -> RenderableManager_PrimitiveType.TRIANGLES
                PrimitiveType.TRIANGLE_STRIP -> RenderableManager_PrimitiveType.TRIANGLE_STRIP
            }
            jsBuilder.geometryMinMax(index.toDouble(), jsType, vb.jsVertexBuffer, ib.jsIndexBuffer, offset.toDouble(), minIndex.toDouble(), maxIndex.toDouble(), count.toDouble())
            return this
        }

        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, offset: Int, count: Int): Builder {
            jsBuilder.geometryNoIndicesOffset(index.toDouble(), mapPrimitiveType(type), vb.jsVertexBuffer, offset.toDouble(), count.toDouble())
            return this
        }

        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer): Builder {
            jsBuilder.geometryNoIndices(index.toDouble(), mapPrimitiveType(type), vb.jsVertexBuffer)
            return this
        }

        actual fun geometryType(type: GeometryType): Builder {
            jsBuilder.geometryType(when (type) {
                GeometryType.DYNAMIC -> io.github.erkko68.filament.web.RenderableManager_Builder_GeometryType.DYNAMIC
                GeometryType.STATIC_BOUNDS -> io.github.erkko68.filament.web.RenderableManager_Builder_GeometryType.STATIC_BOUNDS
                GeometryType.STATIC -> io.github.erkko68.filament.web.RenderableManager_Builder_GeometryType.STATIC
            })
            return this
        }

        actual fun material(
            index: Int,
            materialInstance: MaterialInstance
        ): Builder {
            jsBuilder.material(index.toDouble(), materialInstance.jsMaterialInstance)
            return this
        }

        actual fun blendOrder(
            index: Int,
            blendOrder: Int
        ): Builder {
            jsBuilder.blendOrder(index.toDouble(), blendOrder.toDouble())
            return this
        }

        actual fun globalBlendOrderEnabled(
            index: Int,
            enabled: Boolean
        ): Builder {
            jsBuilder.globalBlendOrderEnabled(index.toDouble(), enabled)
            return this
        }

        actual fun boundingBox(box: Box): Builder {
            val jsBox = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.Box>()
            jsBox.center = box.center.toJsNumbers()
            jsBox.halfExtent = box.halfExtent.toJsNumbers()
            jsBuilder.boundingBox(jsBox)
            return this
        }

        actual fun layerMask(
            select: Int,
            value: Int
        ): Builder {
            jsBuilder.layerMask(select.toDouble(), value.toDouble())
            return this
        }

        actual fun priority(priority: Int): Builder {
            jsBuilder.priority(priority.toDouble())
            return this
        }

        actual fun channel(channel: Int): Builder {
            jsBuilder.channel(channel.toDouble())
            return this
        }

        actual fun culling(enabled: Boolean): Builder {
            jsBuilder.culling(enabled)
            return this
        }

        actual fun castShadows(enabled: Boolean): Builder {
            jsBuilder.castShadows(enabled)
            return this
        }

        actual fun receiveShadows(enabled: Boolean): Builder {
            jsBuilder.receiveShadows(enabled)
            return this
        }

        actual fun screenSpaceContactShadows(enabled: Boolean): Builder {
            jsBuilder.screenSpaceContactShadows(enabled)
            return this
        }

        actual fun skinning(boneCount: Int): Builder {
            jsBuilder.skinning(boneCount.toDouble())
            return this
        }

        actual fun skinning(
            boneCount: Int,
            bones: FloatArray
        ): Builder {
            // 7 floats per bone: unit quaternion (x, y, z, w) followed by translation (x, y, z).
            @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
            val jsBones = List(boneCount) { i ->
                val b = i * 7
                val bone = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.RenderableManager_Bone>()
                bone.unitQuaternion = jsNumbers(bones[b + 0], bones[b + 1], bones[b + 2], bones[b + 3])
                bone.translation = jsNumbers(bones[b + 4], bones[b + 5], bones[b + 6])
                bone
            }
            jsBuilder.skinningBones(jsBones.toJsArray())
            return this
        }

        actual fun skinning(
            skinningBuffer: SkinningBuffer,
            boneCount: Int,
            offset: Int
        ): Builder {
            jsBuilder.skinningBuffer(skinningBuffer.jsSkinningBuffer, boneCount.toDouble(), offset.toDouble())
            return this
        }

        actual fun morphing(targetCount: Int): Builder {
            jsBuilder.morphingTargetCount(targetCount.toDouble())
            return this
        }

        actual fun morphing(morphTargetBuffer: MorphTargetBuffer): Builder {
            jsBuilder.morphingBuffer(morphTargetBuffer.jsMorphTargetBuffer)
            return this
        }

        actual fun fog(enabled: Boolean): Builder {
            jsBuilder.fog(enabled)
            return this
        }

        actual fun lightChannel(
            channel: Int,
            enable: Boolean
        ): Builder {
            jsBuilder.lightChannel(channel.toDouble(), enable)
            return this
        }

        actual fun enableSkinningBuffers(enabled: Boolean): Builder {
            jsBuilder.enableSkinningBuffers(enabled)
            return this
        }

        actual fun instances(instanceCount: Int): Builder {
            jsBuilder.instances(instanceCount.toDouble())
            return this
        }

        actual fun build(engine: Engine, entity: Entity) {
            jsBuilder.build(engine.jsEngine, EntityManager.jsEntityOf(entity))
        }
    }
}
private fun mapPrimitiveType(type: RenderableManager.PrimitiveType): RenderableManager_PrimitiveType = when (type) {
    RenderableManager.PrimitiveType.POINTS -> RenderableManager_PrimitiveType.POINTS
    RenderableManager.PrimitiveType.LINES -> RenderableManager_PrimitiveType.LINES
    RenderableManager.PrimitiveType.LINE_STRIP -> RenderableManager_PrimitiveType.LINE_STRIP
    RenderableManager.PrimitiveType.TRIANGLES -> RenderableManager_PrimitiveType.TRIANGLES
    RenderableManager.PrimitiveType.TRIANGLE_STRIP -> RenderableManager_PrimitiveType.TRIANGLE_STRIP
}
