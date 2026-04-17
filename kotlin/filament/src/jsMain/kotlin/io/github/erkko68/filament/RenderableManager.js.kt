package io.github.erkko68.filament

actual class RenderableManager {
    actual fun hasComponent(entity: Entity): Boolean {
        TODO("Not yet implemented")
    }

    actual fun getInstance(entity: Entity): EntityInstance {
        TODO("Not yet implemented")
    }

    actual fun destroy(entity: Entity) {
    }

    actual fun setAxisAlignedBoundingBox(
        instance: EntityInstance,
        box: Box
    ) {
    }

    actual fun getAxisAlignedBoundingBox(
        instance: EntityInstance,
        outBox: Box?
    ): Box {
        TODO("Not yet implemented")
    }

    actual fun setLayerMask(
        instance: EntityInstance,
        select: Int,
        value: Int
    ) {
    }

    actual fun setPriority(instance: EntityInstance, priority: Int) {
    }

    actual fun getPriority(instance: EntityInstance): Int {
        TODO("Not yet implemented")
    }

    actual fun setChannel(instance: EntityInstance, channel: Int) {
    }

    actual fun getChannel(instance: EntityInstance): Int {
        TODO("Not yet implemented")
    }

    actual fun setCulling(instance: EntityInstance, enabled: Boolean) {
    }

    actual fun isCullingEnabled(instance: EntityInstance): Boolean {
        TODO("Not yet implemented")
    }

    actual fun setFogEnabled(instance: EntityInstance, enabled: Boolean) {
    }

    actual fun getFogEnabled(instance: EntityInstance): Boolean {
        TODO("Not yet implemented")
    }

    actual fun setCastShadows(instance: EntityInstance, enabled: Boolean) {
    }

    actual fun setReceiveShadows(instance: EntityInstance, enabled: Boolean) {
    }

    actual fun setScreenSpaceContactShadows(
        instance: EntityInstance,
        enabled: Boolean
    ) {
    }

    actual fun isShadowCaster(instance: EntityInstance): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isShadowReceiver(instance: EntityInstance): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isScreenSpaceContactShadowsEnabled(instance: EntityInstance): Boolean {
        TODO("Not yet implemented")
    }

    actual fun getPrimitiveCount(instance: EntityInstance): Int {
        TODO("Not yet implemented")
    }

    actual fun getInstanceCount(instance: EntityInstance): Int {
        TODO("Not yet implemented")
    }

    actual fun setMaterialInstanceAt(
        instance: EntityInstance,
        primitiveIndex: Int,
        materialInstance: MaterialInstance
    ) {
    }

    actual fun getMaterialInstanceAt(
        instance: EntityInstance,
        primitiveIndex: Int
    ): MaterialInstance? {
        TODO("Not yet implemented")
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
    }

    actual fun setBlendOrderAt(
        instance: EntityInstance,
        primitiveIndex: Int,
        blendOrder: Int
    ) {
    }

    actual fun getBlendOrderAt(
        instance: EntityInstance,
        primitiveIndex: Int
    ): Int {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    actual fun getMorphTargetCount(instance: EntityInstance): Int {
        TODO("Not yet implemented")
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
        actual fun geometry(
            index: Int,
            type: PrimitiveType,
            vb: VertexBuffer,
            ib: IndexBuffer
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun geometry(
            index: Int,
            type: PrimitiveType,
            vb: VertexBuffer,
            ib: IndexBuffer,
            offset: Int,
            count: Int
        ): Builder {
            TODO("Not yet implemented")
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
            TODO("Not yet implemented")
        }

        actual fun geometryType(type: GeometryType): Builder {
            TODO("Not yet implemented")
        }

        actual fun material(
            index: Int,
            materialInstance: MaterialInstance
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun blendOrder(
            index: Int,
            blendOrder: Int
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun globalBlendOrderEnabled(
            index: Int,
            enabled: Boolean
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun boundingBox(box: Box): Builder {
            TODO("Not yet implemented")
        }

        actual fun layerMask(
            select: Int,
            value: Int
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun priority(priority: Int): Builder {
            TODO("Not yet implemented")
        }

        actual fun channel(channel: Int): Builder {
            TODO("Not yet implemented")
        }

        actual fun culling(enabled: Boolean): Builder {
            TODO("Not yet implemented")
        }

        actual fun castShadows(enabled: Boolean): Builder {
            TODO("Not yet implemented")
        }

        actual fun receiveShadows(enabled: Boolean): Builder {
            TODO("Not yet implemented")
        }

        actual fun screenSpaceContactShadows(enabled: Boolean): Builder {
            TODO("Not yet implemented")
        }

        actual fun skinning(boneCount: Int): Builder {
            TODO("Not yet implemented")
        }

        actual fun skinning(
            boneCount: Int,
            bones: FloatArray
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun skinning(
            skinningBuffer: SkinningBuffer,
            boneCount: Int,
            offset: Int
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun morphing(targetCount: Int): Builder {
            TODO("Not yet implemented")
        }

        actual fun morphing(morphTargetBuffer: MorphTargetBuffer): Builder {
            TODO("Not yet implemented")
        }

        actual fun fog(enabled: Boolean): Builder {
            TODO("Not yet implemented")
        }

        actual fun lightChannel(
            channel: Int,
            enable: Boolean
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun instances(instanceCount: Int): Builder {
            TODO("Not yet implemented")
        }

        actual fun build(engine: Engine, entity: Entity) {
        }
    }
}