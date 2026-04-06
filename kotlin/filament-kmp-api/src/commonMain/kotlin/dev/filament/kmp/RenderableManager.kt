package dev.filament.kmp

/**
 * Factory and manager for renderables, which are entities that can be drawn.
 *
 * <p>Renderables are bundles of primitives, each of which has its own geometry and material. All
 * primitives in a particular renderable share a set of rendering attributes, such as whether they
 * cast shadows or use vertex skinning.</p>
 *
 * <pre>
 * val entity = EntityManager.get().create()
 *
 * RenderableManager.Builder(1)
 *         .boundingBox(Box(0.0f, 0.0f, 0.0f, 9000.0f, 9000.0f, 9000.0f))
 *         .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib)
 *         .material(0, material)
 *         .build(engine, entity)
 *
 * scene.addEntity(entity)
 * </pre>
 *
 * <p>To modify the state of an existing renderable, clients should first use RenderableManager
 * to get a temporary handle called an instance. The instance can then be used to get or set
 * the renderable's state. Please note that instances are ephemeral; clients should store entities,
 * not instances.</p>
 */
expect class RenderableManager {

    /**
     * Primitive types used in [RenderableManager.Builder.geometry].
     */
    enum class PrimitiveType {
        POINTS,
        LINES,
        LINE_STRIP,
        TRIANGLES,
        TRIANGLE_STRIP
    }

    /**
     * Adds renderable components to entities using a builder pattern.
     */
    class Builder(count: Int) {
        /**
         * Type of geometry for a Renderable
         */
        enum class GeometryType {
            DYNAMIC,
            STATIC_BOUNDS,
            STATIC
        }

        fun geometry(index: Int, type: PrimitiveType, vertices: VertexBuffer, indices: IndexBuffer): Builder
        fun geometry(index: Int, type: PrimitiveType, vertices: VertexBuffer, indices: IndexBuffer, offset: Int, count: Int): Builder
        fun geometry(index: Int, type: PrimitiveType, vertices: VertexBuffer, indices: IndexBuffer, offset: Int, minIndex: Int, maxIndex: Int, count: Int): Builder
        fun geometryType(type: GeometryType): Builder
        fun material(index: Int, material: MaterialInstance): Builder
        fun boundingBox(aabb: Box): Builder
        fun layerMask(select: Int, value: Int): Builder
        fun priority(priority: Int): Builder
        fun channel(channel: Int): Builder
        fun culling(enabled: Boolean): Builder
        fun castShadows(enabled: Boolean): Builder
        fun receiveShadows(enabled: Boolean): Builder
        fun screenSpaceContactShadows(enabled: Boolean): Builder
        fun lightChannel(channel: Int, enable: Boolean): Builder
        fun instances(instanceCount: Int): Builder
        fun morphing(targetCount: Int): Builder
        fun morphing(morphTargetBuffer: MorphTargetBuffer): Builder
        fun morphing(level: Int, primitiveIndex: Int, offset: Int): Builder
        fun skinning(boneCount: Int): Builder
        fun skinning(boneCount: Int, bones: Buffer): Builder
        fun skinning(skinningBuffer: SkinningBuffer, boneCount: Int, offset: Int): Builder
        fun enableSkinningBuffers(enabled: Boolean): Builder
        fun fog(enabled: Boolean): Builder
        fun blendOrder(index: Int, blendOrder: Int): Builder
        fun globalBlendOrderEnabled(index: Int, enabled: Boolean): Builder
        fun build(engine: Engine, entity: Int)
    }

    fun hasComponent(entity: Int): Boolean
    fun getInstance(entity: Int): Int
    fun destroy(entity: Int)

    fun setAxisAlignedBoundingBox(instance: Int, aabb: Box)
    fun getAxisAlignedBoundingBox(instance: Int, out: Box?): Box
    fun setLayerMask(instance: Int, select: Int, value: Int)
    fun setPriority(instance: Int, priority: Int)
    fun getPriority(instance: Int): Int
    fun setChannel(instance: Int, channel: Int)
    fun getChannel(instance: Int): Int
    fun setCulling(instance: Int, enabled: Boolean)
    fun isCullingEnabled(instance: Int): Boolean
    fun setCastShadows(instance: Int, enabled: Boolean)
    fun isShadowCaster(instance: Int): Boolean
    fun setReceiveShadows(instance: Int, enabled: Boolean)
    fun isShadowReceiver(instance: Int): Boolean
    fun setScreenSpaceContactShadows(instance: Int, enabled: Boolean)
    fun isScreenSpaceContactShadowsEnabled(instance: Int): Boolean
    fun setFogEnabled(instance: Int, enabled: Boolean)
    fun getFogEnabled(instance: Int): Boolean
    fun setLightChannel(instance: Int, channel: Int, enable: Boolean)
    fun getLightChannel(instance: Int, channel: Int): Boolean

    fun setMaterialInstanceAt(instance: Int, primitiveIndex: Int, materialInstance: MaterialInstance)
    fun getMaterialInstanceAt(instance: Int, primitiveIndex: Int): MaterialInstance
    fun clearMaterialInstanceAt(instance: Int, primitiveIndex: Int)
    fun setGeometryAt(instance: Int, primitiveIndex: Int, type: PrimitiveType, vertices: VertexBuffer, indices: IndexBuffer)
    fun setGeometryAt(instance: Int, primitiveIndex: Int, type: PrimitiveType, vertices: VertexBuffer, indices: IndexBuffer, offset: Int, count: Int)

    fun getPrimitiveCount(instance: Int): Int
    fun getInstanceCount(instance: Int): Int

    fun setBonesAsMatrices(instance: Int, matrices: Buffer, boneCount: Int, offset: Int)
    fun setBonesAsQuaternions(instance: Int, quaternions: Buffer, boneCount: Int, offset: Int)
    fun setSkinningBuffer(instance: Int, skinningBuffer: SkinningBuffer, count: Int, offset: Int)

    fun setMorphWeights(instance: Int, weights: FloatArray, offset: Int)
    fun setMorphTargetBufferOffsetAt(instance: Int, level: Int, primitiveIndex: Int, offset: Int)
    fun getMorphTargetCount(instance: Int): Int

    fun setBlendOrderAt(instance: Int, primitiveIndex: Int, blendOrder: Int)
    fun getBlendOrderAt(instance: Int, primitiveIndex: Int): Int
    fun setGlobalBlendOrderEnabledAt(instance: Int, primitiveIndex: Int, enabled: Boolean)
    fun isGlobalBlendOrderEnabledAt(instance: Int, primitiveIndex: Int): Boolean

    fun getEnabledAttributesAt(instance: Int, primitiveIndex: Int): Set<VertexBuffer.VertexAttribute>
}
