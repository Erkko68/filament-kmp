package dev.filament.kmp

actual class RenderableManager {
    actual fun hasComponent(@Entity entity: Int): Boolean = TODO("Not yet implemented")

    @EntityInstance
    actual fun getInstance(@Entity entity: Int): Int = TODO("Not yet implemented")

    actual fun destroy(@Entity entity: Int) {
        TODO("Not yet implemented")
    }

    actual fun setAxisAlignedBoundingBox(@EntityInstance i: Int, aabb: Box) {
        TODO("Not yet implemented")
    }

    actual fun getAxisAlignedBoundingBox(@EntityInstance i: Int, out: Box?): Box = TODO("Not yet implemented")

    actual fun setLayerMask(@EntityInstance i: Int, select: Int, value: Int) {
        TODO("Not yet implemented")
    }

    actual fun setPriority(@EntityInstance i: Int, priority: Int) {
        TODO("Not yet implemented")
    }

    actual fun getPriority(@EntityInstance i: Int): Int = TODO("Not yet implemented")

    actual fun setChannel(@EntityInstance i: Int, channel: Int) {
        TODO("Not yet implemented")
    }

    actual fun getChannel(@EntityInstance i: Int): Int = TODO("Not yet implemented")

    actual fun setCulling(@EntityInstance i: Int, enabled: Boolean) {
        TODO("Not yet implemented")
    }

    actual fun isCullingEnabled(@EntityInstance i: Int): Boolean = TODO("Not yet implemented")

    actual fun setFogEnabled(@EntityInstance i: Int, enabled: Boolean) {
        TODO("Not yet implemented")
    }

    actual fun getFogEnabled(@EntityInstance i: Int): Boolean = TODO("Not yet implemented")

    actual fun setLightChannel(@EntityInstance i: Int, channel: Int, enable: Boolean) {
        TODO("Not yet implemented")
    }

    actual fun getLightChannel(@EntityInstance i: Int, channel: Int): Boolean = TODO("Not yet implemented")

    actual fun setCastShadows(@EntityInstance i: Int, enabled: Boolean) {
        TODO("Not yet implemented")
    }

    actual fun setReceiveShadows(@EntityInstance i: Int, enabled: Boolean) {
        TODO("Not yet implemented")
    }

    actual fun setScreenSpaceContactShadows(@EntityInstance i: Int, enabled: Boolean) {
        TODO("Not yet implemented")
    }

    actual fun isScreenSpaceContactShadowsEnabled(@EntityInstance i: Int): Boolean = TODO("Not yet implemented")

    actual fun isShadowCaster(@EntityInstance i: Int): Boolean = TODO("Not yet implemented")

    actual fun isShadowReceiver(@EntityInstance i: Int): Boolean = TODO("Not yet implemented")

    actual fun setSkinningBuffer(@EntityInstance i: Int, skinningBuffer: SkinningBuffer, count: Int, offset: Int) {
        TODO("Not yet implemented")
    }

    actual fun setBonesAsMatrices(@EntityInstance i: Int, matrices: Any, boneCount: Int, offset: Int) {
        TODO("Not yet implemented")
    }

    actual fun setBonesAsQuaternions(@EntityInstance i: Int, quaternions: Any, boneCount: Int, offset: Int) {
        TODO("Not yet implemented")
    }

    actual fun setMorphWeights(@EntityInstance i: Int, weights: FloatArray, offset: Int) {
        TODO("Not yet implemented")
    }

    actual fun setMorphTargetBufferOffsetAt(@EntityInstance i: Int, level: Int, primitiveIndex: Int, offset: Int) {
        TODO("Not yet implemented")
    }

    actual fun getMorphTargetCount(@EntityInstance i: Int): Int = TODO("Not yet implemented")

    actual fun getPrimitiveCount(@EntityInstance i: Int): Int = TODO("Not yet implemented")

    actual fun getInstanceCount(@EntityInstance i: Int): Int = TODO("Not yet implemented")

    actual fun setMaterialInstanceAt(@EntityInstance i: Int, primitiveIndex: Int, materialInstance: MaterialInstance) {
        TODO("Not yet implemented")
    }

    actual fun clearMaterialInstanceAt(@EntityInstance i: Int, primitiveIndex: Int) {
        TODO("Not yet implemented")
    }

    actual fun getMaterialInstanceAt(@EntityInstance i: Int, primitiveIndex: Int): MaterialInstance = TODO("Not yet implemented")

    actual fun setGeometryAt(
        @EntityInstance i: Int,
        primitiveIndex: Int,
        type: PrimitiveType,
        vertices: VertexBuffer,
        indices: IndexBuffer,
    ) {
        TODO("Not yet implemented")
    }

    actual fun setGeometryAt(
        @EntityInstance i: Int,
        primitiveIndex: Int,
        type: PrimitiveType,
        vertices: VertexBuffer,
        indices: IndexBuffer,
        offset: Int,
        count: Int,
    ) {
        TODO("Not yet implemented")
    }

    actual fun setBlendOrderAt(@EntityInstance instance: Int, primitiveIndex: Int, blendOrder: Int) {
        TODO("Not yet implemented")
    }

    actual fun getBlendOrderAt(@EntityInstance instance: Int, primitiveIndex: Int): Int = TODO("Not yet implemented")

    actual fun setGlobalBlendOrderEnabledAt(@EntityInstance instance: Int, primitiveIndex: Int, enabled: Boolean) {
        TODO("Not yet implemented")
    }

    actual fun isGlobalBlendOrderEnabledAt(@EntityInstance instance: Int, primitiveIndex: Int): Boolean = TODO("Not yet implemented")

    actual fun getEnabledAttributesAt(@EntityInstance i: Int, primitiveIndex: Int): Set<VertexBuffer.VertexAttribute> = TODO("Not yet implemented")

    actual fun getNativeObject(): Long = TODO("Not yet implemented")

    actual class Builder actual constructor(count: Int) {
        actual fun geometry(index: Int, type: PrimitiveType, vertices: VertexBuffer, indices: IndexBuffer): Builder = TODO("Not yet implemented")

        actual fun geometry(
            index: Int,
            type: PrimitiveType,
            vertices: VertexBuffer,
            indices: IndexBuffer,
            offset: Int,
            count: Int,
        ): Builder = TODO("Not yet implemented")

        actual fun geometry(
            index: Int,
            type: PrimitiveType,
            vertices: VertexBuffer,
            indices: IndexBuffer,
            offset: Int,
            minIndex: Int,
            maxIndex: Int,
            count: Int,
        ): Builder = TODO("Not yet implemented")

        actual fun geometryType(type: GeometryType): Builder = TODO("Not yet implemented")

        actual fun material(index: Int, material: MaterialInstance): Builder = TODO("Not yet implemented")

        actual fun blendOrder(index: Int, blendOrder: Int): Builder = TODO("Not yet implemented")

        actual fun globalBlendOrderEnabled(index: Int, enabled: Boolean): Builder = TODO("Not yet implemented")

        actual fun boundingBox(aabb: Box): Builder = TODO("Not yet implemented")

        actual fun layerMask(select: Int, value: Int): Builder = TODO("Not yet implemented")

        actual fun priority(priority: Int): Builder = TODO("Not yet implemented")

        actual fun channel(channel: Int): Builder = TODO("Not yet implemented")

        actual fun culling(enabled: Boolean): Builder = TODO("Not yet implemented")

        actual fun lightChannel(channel: Int, enable: Boolean): Builder = TODO("Not yet implemented")

        actual fun instances(instanceCount: Int): Builder = TODO("Not yet implemented")

        actual fun castShadows(enabled: Boolean): Builder = TODO("Not yet implemented")

        actual fun receiveShadows(enabled: Boolean): Builder = TODO("Not yet implemented")

        actual fun screenSpaceContactShadows(enabled: Boolean): Builder = TODO("Not yet implemented")

        actual fun enableSkinningBuffers(enabled: Boolean): Builder = TODO("Not yet implemented")

        actual fun fog(enabled: Boolean): Builder = TODO("Not yet implemented")

        actual fun skinning(skinningBuffer: SkinningBuffer?, boneCount: Int, offset: Int): Builder = TODO("Not yet implemented")

        actual fun skinning(boneCount: Int): Builder = TODO("Not yet implemented")

        actual fun skinning(boneCount: Int, bones: Any): Builder = TODO("Not yet implemented")

        actual fun morphing(targetCount: Int): Builder = TODO("Not yet implemented")

        actual fun morphing(morphTargetBuffer: MorphTargetBuffer): Builder = TODO("Not yet implemented")

        actual fun morphing(level: Int, primitiveIndex: Int, offset: Int): Builder = TODO("Not yet implemented")

        actual fun build(engine: Engine, @Entity entity: Int) {
            TODO("Not yet implemented")
        }

        actual enum class GeometryType {
            DYNAMIC,
            STATIC_BOUNDS,
            STATIC,
        }
    }

    actual enum class PrimitiveType {
        POINTS,
        LINES,
        LINE_STRIP,
        TRIANGLES,
        TRIANGLE_STRIP,
    }
}

