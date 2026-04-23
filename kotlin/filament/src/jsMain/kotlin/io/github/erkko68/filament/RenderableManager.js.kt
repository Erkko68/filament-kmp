package io.github.erkko68.filament

import io.github.erkko68.filament.js.RenderableManager as JSRenderableManager
import io.github.erkko68.filament.js.`RenderableManager_Builder` as JSRenderableManagerBuilder
import io.github.erkko68.filament.js.RenderableManager_PrimitiveType
import io.github.erkko68.filament.js.Entity as JSEntity
import io.github.erkko68.filament.js.RenderableManager_Instance as JSRenderableManagerInstance

actual class RenderableManager(internal val jsRenderableManager: JSRenderableManager) {
    actual fun hasComponent(entity: Entity): Boolean {
        return jsRenderableManager.hasComponent(entity.unsafeCast<JSEntity>())
    }

    actual fun getInstance(entity: Entity): EntityInstance {
        return jsRenderableManager.getInstance(entity.unsafeCast<JSEntity>()).unsafeCast<EntityInstance>()
    }

    actual fun destroy(entity: Entity) {
        jsRenderableManager.destroy(entity.unsafeCast<JSEntity>())
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

    actual fun getPriority(instance: EntityInstance): Int {
        return 0 
    }

    actual fun setChannel(instance: EntityInstance, channel: Int) {
    }

    actual fun getChannel(instance: EntityInstance): Int {
        return 0
    }

    actual fun setCulling(instance: EntityInstance, enabled: Boolean) {
    }

    actual fun isCullingEnabled(instance: EntityInstance): Boolean {
        return true
    }

    actual fun setFogEnabled(instance: EntityInstance, enabled: Boolean) {
    }

    actual fun getFogEnabled(instance: EntityInstance): Boolean {
        return true
    }

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
        val boneMatrices = matrices.toTypedArray() as Array<Any>
        jsRenderableManager.asDynamic().setBonesFromMatrices(instance.unsafeCast<JSRenderableManagerInstance>(), boneMatrices, offset)
    }

    actual fun setBonesAsQuaternions(
        instance: EntityInstance,
        quaternions: FloatArray,
        boneCount: Int,
        offset: Int
    ) {
        // Not exposed in JS bindings
    }

    actual fun clearMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int) {
        val jsInst = jsRenderableManager.asDynamic()
        if (jsInst.clearMaterialInstanceAt != null) {
            jsInst.clearMaterialInstanceAt(instance.unsafeCast<JSRenderableManagerInstance>(), primitiveIndex)
        }
    }

    actual fun isShadowReceiver(instance: EntityInstance): Boolean {
        return jsRenderableManager.isShadowReceiver(instance.unsafeCast<JSRenderableManagerInstance>())
    }

    actual fun isScreenSpaceContactShadowsEnabled(instance: EntityInstance): Boolean {
        return false
    }

    actual fun getPrimitiveCount(instance: EntityInstance): Int {
        return jsRenderableManager.getPrimitiveCount(instance.unsafeCast<JSRenderableManagerInstance>()).toInt()
    }

    actual fun getInstanceCount(instance: EntityInstance): Int {
        return 1
    }

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
    ): Int {
        return 0
    }

    actual fun setGlobalBlendOrderEnabledAt(
        instance: EntityInstance,
        primitiveIndex: Int,
        enabled: Boolean
    ) {
    }

    actual fun isGlobalBlendOrderEnabledAt(
        instance: EntityInstance,
        primitiveIndex: Int
    ): Boolean {
        return false
    }

    actual fun setLightChannel(
        instance: EntityInstance,
        channel: Int,
        enable: Boolean
    ) {
    }

    actual fun getLightChannel(
        instance: EntityInstance,
        channel: Int
    ): Boolean {
        return true
    }

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
            jsBuilder.asDynamic().blendOrder(index, blendOrder)
            return this
        }

        actual fun globalBlendOrderEnabled(
            index: Int,
            enabled: Boolean
        ): Builder {
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
            return this
        }

        actual fun skinning(boneCount: Int): Builder {
            jsBuilder.asDynamic().skinning(boneCount)
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
            jsBuilder.asDynamic().morphing(targetCount > 0)
            return this
        }

        actual fun morphing(morphTargetBuffer: MorphTargetBuffer): Builder {
            return this
        }

        actual fun fog(enabled: Boolean): Builder {
            return this
        }

        actual fun lightChannel(
            channel: Int,
            enable: Boolean
        ): Builder {
            return this
        }

        actual fun enableSkinningBuffers(enabled: Boolean): Builder {
            return this
        }

        actual fun instances(instanceCount: Int): Builder {
            return this
        }

        actual fun build(engine: Engine, entity: Entity) {
            jsBuilder.build(engine.jsEngine, entity.unsafeCast<JSEntity>())
        }
    }
}