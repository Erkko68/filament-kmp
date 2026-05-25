package io.github.erkko68.filament

import io.github.erkko68.filament.js.RenderableManager as JSRenderableManager
import io.github.erkko68.filament.js.`RenderableManager_Builder` as JSRenderableManagerBuilder
import io.github.erkko68.filament.js.RenderableManager_PrimitiveType
import io.github.erkko68.filament.js.Entity as JSEntity
import io.github.erkko68.filament.js.RenderableManager_Instance as JSRenderableManagerInstance

actual class RenderableManager(internal val jsRenderableManager: JSRenderableManager) {
    actual fun hasComponent(entity: Entity): Boolean {
        return jsRenderableManager.hasComponent(EntityManager.jsEntityOf(entity))
    }

    actual fun getInstance(entity: Entity): EntityInstance {
        return jsRenderableManager.getInstance(EntityManager.jsEntityOf(entity)).unsafeCast<EntityInstance>()
    }

    actual fun destroy(entity: Entity) {
        jsRenderableManager.destroy(EntityManager.jsEntityOf(entity))
    }

    actual fun setAxisAlignedBoundingBox(
        instance: EntityInstance,
        box: Box
    ) {
        val jsBox = js("{}").unsafeCast<io.github.erkko68.filament.js.Box>()
        jsBox.center = box.center.toTypedArray() as Array<Number>
        jsBox.halfExtent = box.halfExtent.toTypedArray() as Array<Number>
        jsRenderableManager.setAxisAlignedBoundingBox(instance.unsafeCast<JSRenderableManagerInstance>(), jsBox)
    }

    actual fun getAxisAlignedBoundingBox(
        instance: EntityInstance,
        outBox: Box?
    ): Box {
        val jsBox = jsRenderableManager.getAxisAlignedBoundingBox(instance.unsafeCast<JSRenderableManagerInstance>())
        val center = jsBox.center.unsafeCast<Array<Number>>()
        val halfExtent = jsBox.halfExtent.unsafeCast<Array<Number>>()
        
        val result = outBox ?: Box()
        result.center[0] = center[0].toFloat()
        result.center[1] = center[1].toFloat()
        result.center[2] = center[2].toFloat()
        result.halfExtent[0] = halfExtent[0].toFloat()
        result.halfExtent[1] = halfExtent[1].toFloat()
        result.halfExtent[2] = halfExtent[2].toFloat()
        return result
    }

    actual fun setLayerMask(
        instance: EntityInstance,
        select: Int,
        value: Int
    ) {
        jsRenderableManager.setLayerMask(instance.unsafeCast<JSRenderableManagerInstance>(), select, value)
    }

    actual fun setPriority(instance: EntityInstance, priority: Int) {
        jsRenderableManager.setPriority(instance.unsafeCast<JSRenderableManagerInstance>(), priority)
    }

    actual fun getPriority(instance: EntityInstance): Int =
        jsRenderableManager.getPriority(instance.unsafeCast<JSRenderableManagerInstance>()).toInt()

    actual fun setChannel(instance: EntityInstance, channel: Int) {
        jsRenderableManager.setChannel(instance.unsafeCast<JSRenderableManagerInstance>(), channel)
    }

    actual fun getChannel(instance: EntityInstance): Int =
        jsRenderableManager.getChannel(instance.unsafeCast<JSRenderableManagerInstance>()).toInt()

    actual fun setCulling(instance: EntityInstance, enabled: Boolean) {
        jsRenderableManager.setCulling(instance.unsafeCast<JSRenderableManagerInstance>(), enabled)
    }

    actual fun isCullingEnabled(instance: EntityInstance): Boolean =
        jsRenderableManager.isCullingEnabled(instance.unsafeCast<JSRenderableManagerInstance>())

    actual fun setFogEnabled(instance: EntityInstance, enabled: Boolean) {
        jsRenderableManager.setFogEnabled(instance.unsafeCast<JSRenderableManagerInstance>(), enabled)
    }

    actual fun getFogEnabled(instance: EntityInstance): Boolean =
        jsRenderableManager.getFogEnabled(instance.unsafeCast<JSRenderableManagerInstance>())

    actual fun setCastShadows(instance: EntityInstance, enabled: Boolean) {
        jsRenderableManager.setCastShadows(instance.unsafeCast<JSRenderableManagerInstance>(), enabled)
    }

    actual fun setReceiveShadows(instance: EntityInstance, enabled: Boolean) {
        jsRenderableManager.setReceiveShadows(instance.unsafeCast<JSRenderableManagerInstance>(), enabled)
    }

    actual fun setScreenSpaceContactShadows(
        instance: EntityInstance,
        enabled: Boolean
    ) {
        jsRenderableManager.setScreenSpaceContactShadows(instance.unsafeCast<JSRenderableManagerInstance>(), enabled)
    }

    actual fun isShadowCaster(instance: EntityInstance): Boolean {
        return jsRenderableManager.isShadowCaster(instance.unsafeCast<JSRenderableManagerInstance>())
    }

    actual fun setBonesAsMatrices(
        instance: EntityInstance,
        matrices: FloatArray,
        boneCount: Int,
        offset: Int
    ) {
        @Suppress("UNCHECKED_CAST")
        val boneMatrices = matrices.toTypedArray() as Array<Any>
        jsRenderableManager.setBonesFromMatrices(
            instance.unsafeCast<JSRenderableManagerInstance>(), boneMatrices, offset)
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
        val bones = Array<io.github.erkko68.filament.js.`RenderableManager_Bone`>(boneCount) { i ->
            val b = i * 4
            val bone = js("{}").unsafeCast<io.github.erkko68.filament.js.`RenderableManager_Bone`>()
            bone.unitQuaternion = arrayOf(
                quaternions[b + 0], quaternions[b + 1],
                quaternions[b + 2], quaternions[b + 3]
            )
            bone.translation = arrayOf(0f, 0f, 0f)
            bone
        }
        jsRenderableManager.setBones(instance.unsafeCast<JSRenderableManagerInstance>(), bones, offset)
    }

    actual fun clearMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int) {
        jsRenderableManager.clearMaterialInstanceAt(
            instance.unsafeCast<JSRenderableManagerInstance>(), primitiveIndex)
    }

    actual fun isShadowReceiver(instance: EntityInstance): Boolean {
        return jsRenderableManager.isShadowReceiver(instance.unsafeCast<JSRenderableManagerInstance>())
    }

    actual fun isScreenSpaceContactShadowsEnabled(instance: EntityInstance): Boolean =
        jsRenderableManager.isScreenSpaceContactShadowsEnabled(instance.unsafeCast<JSRenderableManagerInstance>())

    actual fun getPrimitiveCount(instance: EntityInstance): Int {
        return jsRenderableManager.getPrimitiveCount(instance.unsafeCast<JSRenderableManagerInstance>()).toInt()
    }

    actual fun getInstanceCount(instance: EntityInstance): Int =
        jsRenderableManager.getInstanceCount(instance.unsafeCast<JSRenderableManagerInstance>()).toInt()

    actual fun setMaterialInstanceAt(
        instance: EntityInstance,
        primitiveIndex: Int,
        materialInstance: MaterialInstance
    ) {
        jsRenderableManager.setMaterialInstanceAt(instance.unsafeCast<JSRenderableManagerInstance>(), primitiveIndex, materialInstance.jsMaterialInstance)
    }

    actual fun getMaterialInstanceAt(
        instance: EntityInstance,
        primitiveIndex: Int
    ): MaterialInstance? {
        val jsMat = jsRenderableManager.getMaterialInstanceAt(instance.unsafeCast<JSRenderableManagerInstance>(), primitiveIndex)
        return if (jsMat != null) MaterialInstance(jsMat) else null
    }

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
        jsRenderableManager.setGeometryAt(instance.unsafeCast<JSRenderableManagerInstance>(), primitiveIndex, jsType, vb.jsVertexBuffer, ib.jsIndexBuffer, offset, count)
    }

    actual fun setBlendOrderAt(
        instance: EntityInstance,
        primitiveIndex: Int,
        blendOrder: Int
    ) {
        jsRenderableManager.setBlendOrderAt(instance.unsafeCast<JSRenderableManagerInstance>(), primitiveIndex, blendOrder)
    }

    actual fun getBlendOrderAt(
        instance: EntityInstance,
        primitiveIndex: Int
    ): Int = jsRenderableManager.getBlendOrderAt(
        instance.unsafeCast<JSRenderableManagerInstance>(), primitiveIndex).toInt()

    actual fun setGlobalBlendOrderEnabledAt(
        instance: EntityInstance,
        primitiveIndex: Int,
        enabled: Boolean
    ) {
        jsRenderableManager.setGlobalBlendOrderEnabledAt(
            instance.unsafeCast<JSRenderableManagerInstance>(), primitiveIndex, enabled)
    }

    actual fun isGlobalBlendOrderEnabledAt(
        instance: EntityInstance,
        primitiveIndex: Int
    ): Boolean = jsRenderableManager.isGlobalBlendOrderEnabledAt(
        instance.unsafeCast<JSRenderableManagerInstance>(), primitiveIndex)

    actual fun setLightChannel(
        instance: EntityInstance,
        channel: Int,
        enable: Boolean
    ) {
        jsRenderableManager.setLightChannel(
            instance.unsafeCast<JSRenderableManagerInstance>(), channel, enable)
    }

    actual fun getLightChannel(
        instance: EntityInstance,
        channel: Int
    ): Boolean = jsRenderableManager.getLightChannel(
        instance.unsafeCast<JSRenderableManagerInstance>(), channel)

    actual fun getMorphTargetCount(instance: EntityInstance): Int {
        return 0
    }

    actual fun setSkinningBuffer(
        instance: EntityInstance,
        skinningBuffer: SkinningBuffer,
        count: Int,
        offset: Int
    ) {
    }

    actual fun setMorphWeights(
        instance: EntityInstance,
        weights: FloatArray,
        offset: Int
    ) {
        if (weights.size >= 4) {
            jsRenderableManager.setMorphWeights(instance.unsafeCast<JSRenderableManagerInstance>(), weights[0], weights[1], weights[2], weights[3])
        }
    }

    actual fun setMorphTargetBufferOffsetAt(
        instance: EntityInstance,
        level: Int,
        primitiveIndex: Int,
        offset: Int
    ) {
    }

    actual enum class PrimitiveType { POINTS, LINES, LINE_STRIP, TRIANGLES, TRIANGLE_STRIP }
    actual enum class GeometryType { DYNAMIC, STATIC_BOUNDS, STATIC }
    actual class Builder actual constructor(count: Int) {
        private val jsBuilder: JSRenderableManagerBuilder = JSRenderableManager.Builder(count)

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
            jsBuilder.geometry(index, jsType, vb.jsVertexBuffer, ib.jsIndexBuffer)
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
            jsBuilder.geometryOffset(index, jsType, vb.jsVertexBuffer, ib.jsIndexBuffer, offset, count)
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
            jsBuilder.geometryMinMax(index, jsType, vb.jsVertexBuffer, ib.jsIndexBuffer, offset, minIndex, maxIndex, count)
            return this
        }

        actual fun geometryType(type: GeometryType): Builder {
            // GeometryType isn't bound as a JS enum upstream — pass the ordinal.
            // Filament's C++ enum order: DYNAMIC=0, STATIC_BOUNDS=1, STATIC=2.
            jsBuilder.geometryType(type.ordinal)
            return this
        }

        actual fun material(
            index: Int,
            materialInstance: MaterialInstance
        ): Builder {
            jsBuilder.material(index, materialInstance.jsMaterialInstance)
            return this
        }

        actual fun blendOrder(
            index: Int,
            blendOrder: Int
        ): Builder {
            jsBuilder.blendOrder(index, blendOrder)
            return this
        }

        actual fun globalBlendOrderEnabled(
            index: Int,
            enabled: Boolean
        ): Builder {
            jsBuilder.globalBlendOrderEnabled(index, enabled)
            return this
        }

        actual fun boundingBox(box: Box): Builder {
            val jsBox = js("{}").unsafeCast<io.github.erkko68.filament.js.Box>()
            jsBox.center = box.center.toTypedArray() as Array<Number>
            jsBox.halfExtent = box.halfExtent.toTypedArray() as Array<Number>
            jsBuilder.boundingBox(jsBox)
            return this
        }

        actual fun layerMask(
            select: Int,
            value: Int
        ): Builder {
            jsBuilder.layerMask(select, value)
            return this
        }

        actual fun priority(priority: Int): Builder {
            jsBuilder.priority(priority)
            return this
        }

        actual fun channel(channel: Int): Builder {
            jsBuilder.channel(channel)
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
            jsBuilder.skinning(boneCount)
            return this
        }

        actual fun skinning(
            boneCount: Int,
            bones: FloatArray
        ): Builder {
            return this
        }

        actual fun skinning(
            skinningBuffer: SkinningBuffer,
            boneCount: Int,
            offset: Int
        ): Builder {
            return this
        }

        actual fun morphing(targetCount: Int): Builder {
            // The JS Builder's morphing() takes only a boolean enable — the per-target
            // count overload isn't bound. Pass `enable = (count > 0)`.
            jsBuilder.morphing(targetCount > 0)
            return this
        }

        actual fun morphing(morphTargetBuffer: MorphTargetBuffer): Builder {
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
            jsBuilder.lightChannel(channel, enable)
            return this
        }

        actual fun enableSkinningBuffers(enabled: Boolean): Builder {
            // Not exposed in the JS Builder; SkinningBuffer itself isn't bound,
            // so the underlying API is unreachable on web.
            return this
        }

        actual fun instances(instanceCount: Int): Builder {
            jsBuilder.instances(instanceCount)
            return this
        }

        actual fun build(engine: Engine, entity: Entity) {
            jsBuilder.build(engine.jsEngine, EntityManager.jsEntityOf(entity))
        }
    }
}