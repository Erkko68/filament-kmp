package io.github.erkko68.filament

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
        return jsRenderableManager.getInstance(EntityManager.jsEntityOf(entity)).unsafeCast<EntityInstance>()
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
        jsRenderableManager.setAxisAlignedBoundingBox(instance.toDouble(), jsBox)
    }

    actual fun getAxisAlignedBoundingBox(
        instance: EntityInstance,
        outBox: Box?
    ): Box {
        val jsBox = jsRenderableManager.getAxisAlignedBoundingBox(instance.toDouble())
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
        jsRenderableManager.setLayerMask(instance.toDouble(), select.toDouble(), value.toDouble())
    }

    actual fun setPriority(instance: EntityInstance, priority: Int) {
        jsRenderableManager.setPriority(instance.toDouble(), priority.toDouble())
    }

    actual fun getPriority(instance: EntityInstance): Int =
        jsRenderableManager.getPriority(instance.toDouble()).toInt()

    actual fun setChannel(instance: EntityInstance, channel: Int) {
        jsRenderableManager.setChannel(instance.toDouble(), channel.toDouble())
    }

    actual fun getChannel(instance: EntityInstance): Int =
        jsRenderableManager.getChannel(instance.toDouble()).toInt()

    actual fun setCulling(instance: EntityInstance, enabled: Boolean) {
        jsRenderableManager.setCulling(instance.toDouble(), enabled)
    }

    actual fun isCullingEnabled(instance: EntityInstance): Boolean =
        jsRenderableManager.isCullingEnabled(instance.toDouble())

    actual fun setFogEnabled(instance: EntityInstance, enabled: Boolean) {
        jsRenderableManager.setFogEnabled(instance.toDouble(), enabled)
    }

    actual fun getFogEnabled(instance: EntityInstance): Boolean =
        jsRenderableManager.getFogEnabled(instance.toDouble())

    actual fun setCastShadows(instance: EntityInstance, enabled: Boolean) {
        jsRenderableManager.setCastShadows(instance.toDouble(), enabled)
    }

    actual fun setReceiveShadows(instance: EntityInstance, enabled: Boolean) {
        jsRenderableManager.setReceiveShadows(instance.toDouble(), enabled)
    }

    actual fun setScreenSpaceContactShadows(
        instance: EntityInstance,
        enabled: Boolean
    ) {
        jsRenderableManager.setScreenSpaceContactShadows(instance.toDouble(), enabled)
    }

    actual fun isShadowCaster(instance: EntityInstance): Boolean {
        return jsRenderableManager.isShadowCaster(instance.toDouble())
    }

    actual fun setBonesAsMatrices(
        instance: EntityInstance,
        matrices: FloatArray,
        boneCount: Int,
        offset: Int
    ) {
        @Suppress("UNCHECKED_CAST")
        val boneMatrices = matrices.toTypedArray() as Array<Any?>
        jsRenderableManager.setBonesFromMatrices(
            instance.toDouble(), boneMatrices, offset.toDouble())
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
        val bones = Array<io.github.erkko68.filament.web.`RenderableManager_Bone`>(boneCount) { i ->
            val b = i * 4
            val bone = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.`RenderableManager_Bone`>()
            bone.unitQuaternion = arrayOf(
                quaternions[b + 0], quaternions[b + 1],
                quaternions[b + 2], quaternions[b + 3]
            )
            bone.translation = arrayOf(0f, 0f, 0f)
            bone
        }
        jsRenderableManager.setBones(instance.toDouble(), bones, offset.toDouble())
    }

    actual fun clearMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int) {
        jsRenderableManager.clearMaterialInstanceAt(
            instance.toDouble(), primitiveIndex.toDouble())
    }

    actual fun isShadowReceiver(instance: EntityInstance): Boolean {
        return jsRenderableManager.isShadowReceiver(instance.toDouble())
    }

    actual fun isScreenSpaceContactShadowsEnabled(instance: EntityInstance): Boolean =
        jsRenderableManager.isScreenSpaceContactShadowsEnabled(instance.toDouble())

    actual fun getPrimitiveCount(instance: EntityInstance): Int {
        return jsRenderableManager.getPrimitiveCount(instance.toDouble()).toInt()
    }

    actual fun getInstanceCount(instance: EntityInstance): Int =
        jsRenderableManager.getInstanceCount(instance.toDouble()).toInt()

    actual fun setMaterialInstanceAt(
        instance: EntityInstance,
        primitiveIndex: Int,
        materialInstance: MaterialInstance
    ) {
        jsRenderableManager.setMaterialInstanceAt(instance.toDouble(), primitiveIndex.toDouble(), materialInstance.jsMaterialInstance)
    }

    actual fun getMaterialInstanceAt(
        instance: EntityInstance,
        primitiveIndex: Int
    ): MaterialInstance? {
        val jsMat = jsRenderableManager.getMaterialInstanceAt(instance.toDouble(), primitiveIndex.toDouble())
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
        jsRenderableManager.setGeometryAt(instance.toDouble(), primitiveIndex.toDouble(), jsType, vb.jsVertexBuffer, ib.jsIndexBuffer, offset.toDouble(), count.toDouble())
    }

    // TODO(js): non-indexed setGeometryAt is not exposed in jsbindings.cpp as of
    // Filament 1.71.5 (only the indexed overload is bound). Web/WASM can't reach this.
    actual fun setGeometryAt(
        instance: EntityInstance,
        primitiveIndex: Int,
        type: PrimitiveType,
        vb: VertexBuffer,
        offset: Int,
        count: Int
    ) {
        throw UnsupportedOperationException(
            "Non-indexed setGeometryAt is not available on web — Filament.js does not bind this overload."
        )
    }

    actual fun setBlendOrderAt(
        instance: EntityInstance,
        primitiveIndex: Int,
        blendOrder: Int
    ) {
        jsRenderableManager.setBlendOrderAt(instance.toDouble(), primitiveIndex.toDouble(), blendOrder.toDouble())
    }

    actual fun getBlendOrderAt(
        instance: EntityInstance,
        primitiveIndex: Int
    ): Int = jsRenderableManager.getBlendOrderAt(
        instance.toDouble(), primitiveIndex.toDouble()).toInt()

    actual fun setGlobalBlendOrderEnabledAt(
        instance: EntityInstance,
        primitiveIndex: Int,
        enabled: Boolean
    ) {
        jsRenderableManager.setGlobalBlendOrderEnabledAt(
            instance.toDouble(), primitiveIndex.toDouble(), enabled)
    }

    actual fun isGlobalBlendOrderEnabledAt(
        instance: EntityInstance,
        primitiveIndex: Int
    ): Boolean = jsRenderableManager.isGlobalBlendOrderEnabledAt(
        instance.toDouble(), primitiveIndex.toDouble())

    actual fun setLightChannel(
        instance: EntityInstance,
        channel: Int,
        enable: Boolean
    ) {
        jsRenderableManager.setLightChannel(
            instance.toDouble(), channel.toDouble(), enable)
    }

    actual fun getLightChannel(
        instance: EntityInstance,
        channel: Int
    ): Boolean = jsRenderableManager.getLightChannel(
        instance.toDouble(), channel.toDouble())

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
            jsRenderableManager.setMorphWeights(instance.toDouble(), weights[0].toDouble(), weights[1].toDouble(), weights[2].toDouble(), weights[3].toDouble())
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

        // TODO(js): non-indexed (attribute-less / procedural) geometry overloads are not
        // bound in jsbindings.cpp as of Filament 1.71.5 — only the indexed overloads exist.
        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, offset: Int, count: Int): Builder {
            throw UnsupportedOperationException(
                "Non-indexed RenderableManager.Builder.geometry is not available on web — Filament.js does not bind this overload."
            )
        }

        actual fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer): Builder {
            throw UnsupportedOperationException(
                "Non-indexed RenderableManager.Builder.geometry is not available on web — Filament.js does not bind this overload."
            )
        }

        actual fun geometryType(type: GeometryType): Builder {
            // GeometryType isn't bound as a JS enum upstream — pass the ordinal.
            // Filament's C++ enum order: DYNAMIC=0, STATIC_BOUNDS=1, STATIC=2.
            jsBuilder.geometryType(type.ordinal.toDouble())
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
            jsBuilder.lightChannel(channel.toDouble(), enable)
            return this
        }

        actual fun enableSkinningBuffers(enabled: Boolean): Builder {
            // TODO(js): not exposed in the JS Builder; SkinningBuffer itself isn't
            // bound, so the underlying API is unreachable on web.
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