package dev.filament.kmp

/**
 * Factory and manager for <em>renderables</em>, which are entities that can be drawn.
 */
expect class RenderableManager {
    /**
     * Checks if the given entity already has a renderable component.
     */
    fun hasComponent(@Entity entity: Int): Boolean

    /**
     * Gets a temporary handle that can be used to access the renderable state.
     */
    @EntityInstance
    fun getInstance(@Entity entity: Int): Int

    /**
     * Destroys the renderable component in the given entity.
     */
    fun destroy(@Entity entity: Int)

    /**
     * Primitive types used in {@link RenderableManager.Builder#geometry}.
     */
    enum class PrimitiveType {
        POINTS,
        LINES,
        LINE_STRIP,
        TRIANGLES,
        TRIANGLE_STRIP,
    }

    /**
     * Adds renderable components to entities using a builder pattern.
     */
    class Builder(count: Int) {
        /**
         * Specifies the geometry data for a primitive.
         */
        fun geometry(index: Int, type: PrimitiveType, vertices: VertexBuffer, indices: IndexBuffer): Builder

        /**
         * Specifies geometry with explicit index offset and count.
         */
        fun geometry(index: Int, type: PrimitiveType, vertices: VertexBuffer, indices: IndexBuffer, offset: Int, count: Int): Builder

        /**
         * Specifies geometry with explicit index range and count.
         */
        fun geometry(
            index: Int,
            type: PrimitiveType,
            vertices: VertexBuffer,
            indices: IndexBuffer,
            offset: Int,
            minIndex: Int,
            maxIndex: Int,
            count: Int,
        ): Builder

        enum class GeometryType {
            DYNAMIC,
            STATIC_BOUNDS,
            STATIC,
        }

        fun geometryType(type: GeometryType): Builder

        /**
         * Binds a material instance to the specified primitive.
         */
        fun material(index: Int, material: MaterialInstance): Builder

        fun blendOrder(index: Int, blendOrder: Int): Builder

        fun globalBlendOrderEnabled(index: Int, enabled: Boolean): Builder

        /**
         * The axis-aligned bounding box of the renderable.
         */
        fun boundingBox(aabb: Box): Builder

        fun layerMask(select: Int, value: Int): Builder

        fun priority(priority: Int): Builder

        fun channel(channel: Int): Builder

        fun culling(enabled: Boolean): Builder

        fun lightChannel(channel: Int, enable: Boolean): Builder

        fun instances(instanceCount: Int): Builder

        fun castShadows(enabled: Boolean): Builder

        fun receiveShadows(enabled: Boolean): Builder

        fun screenSpaceContactShadows(enabled: Boolean): Builder

        fun enableSkinningBuffers(enabled: Boolean): Builder

        fun fog(enabled: Boolean): Builder

        fun skinning(skinningBuffer: SkinningBuffer?, boneCount: Int, offset: Int): Builder

        fun skinning(boneCount: Int): Builder

        fun skinning(boneCount: Int, bones: Any): Builder

        fun morphing(targetCount: Int): Builder

        fun morphing(morphTargetBuffer: MorphTargetBuffer): Builder

        fun morphing(level: Int, primitiveIndex: Int, offset: Int): Builder

        /**
         * Creates entities with renderable components.
         */
        fun build(engine: Engine, @Entity entity: Int)
    }

    /**
     * Changes the bounding box used for frustum culling.
     *
     * @see Builder#boundingBox
     * @see RenderableManager#getAxisAlignedBoundingBox
     */
    fun setAxisAlignedBoundingBox(@EntityInstance i: Int, aabb: Box)

    fun getAxisAlignedBoundingBox(@EntityInstance i: Int, out: Box? = null): Box

    /**
     * Changes the visibility bits.
     *
     * @see Builder#layerMask
     * @see View#setVisibleLayers
     */
    fun setLayerMask(@EntityInstance i: Int, select: Int, value: Int)

    /**
     * Changes the coarse-level draw ordering.
     *
     * @see Builder#priority
     */
    fun setPriority(@EntityInstance i: Int, priority: Int)

    fun getPriority(@EntityInstance i: Int): Int

    fun setChannel(@EntityInstance i: Int, channel: Int)

    fun getChannel(@EntityInstance i: Int): Int

    fun setCulling(@EntityInstance i: Int, enabled: Boolean)

    fun isCullingEnabled(@EntityInstance i: Int): Boolean

    fun setFogEnabled(@EntityInstance i: Int, enabled: Boolean)

    fun getFogEnabled(@EntityInstance i: Int): Boolean

    fun setLightChannel(@EntityInstance i: Int, channel: Int, enable: Boolean)

    fun getLightChannel(@EntityInstance i: Int, channel: Int): Boolean

    fun setCastShadows(@EntityInstance i: Int, enabled: Boolean)

    fun setReceiveShadows(@EntityInstance i: Int, enabled: Boolean)

    fun setScreenSpaceContactShadows(@EntityInstance i: Int, enabled: Boolean)

    fun isScreenSpaceContactShadowsEnabled(@EntityInstance i: Int): Boolean

    fun isShadowCaster(@EntityInstance i: Int): Boolean

    fun isShadowReceiver(@EntityInstance i: Int): Boolean

    fun setSkinningBuffer(@EntityInstance i: Int, skinningBuffer: SkinningBuffer, count: Int, offset: Int)

    fun setBonesAsMatrices(@EntityInstance i: Int, matrices: Any, boneCount: Int, offset: Int)

    fun setBonesAsQuaternions(@EntityInstance i: Int, quaternions: Any, boneCount: Int, offset: Int)

    fun setMorphWeights(@EntityInstance i: Int, weights: FloatArray, offset: Int)

    fun setMorphTargetBufferOffsetAt(@EntityInstance i: Int, level: Int, primitiveIndex: Int, offset: Int)

    fun getMorphTargetCount(@EntityInstance i: Int): Int

    fun getPrimitiveCount(@EntityInstance i: Int): Int

    fun getInstanceCount(@EntityInstance i: Int): Int

    /**
     * Changes the material instance binding for the given primitive.
     *
     * @see Builder#material
     */
    fun setMaterialInstanceAt(@EntityInstance i: Int, primitiveIndex: Int, materialInstance: MaterialInstance)

    fun clearMaterialInstanceAt(@EntityInstance i: Int, primitiveIndex: Int)

    /**
     * Creates a MaterialInstance Java wrapper object for a particular material instance.
     */
    fun getMaterialInstanceAt(@EntityInstance i: Int, primitiveIndex: Int): MaterialInstance

    /**
     * Changes the geometry for the given primitive.
     *
     * @see Builder#geometry Builder.geometry
     */
    fun setGeometryAt(
        @EntityInstance i: Int,
        primitiveIndex: Int,
        type: PrimitiveType,
        vertices: VertexBuffer,
        indices: IndexBuffer,
    )

    fun setGeometryAt(
        @EntityInstance i: Int,
        primitiveIndex: Int,
        type: PrimitiveType,
        vertices: VertexBuffer,
        indices: IndexBuffer,
        offset: Int,
        count: Int,
    )

    fun setBlendOrderAt(@EntityInstance instance: Int, primitiveIndex: Int, blendOrder: Int)

    fun getBlendOrderAt(@EntityInstance instance: Int, primitiveIndex: Int): Int

    fun setGlobalBlendOrderEnabledAt(@EntityInstance instance: Int, primitiveIndex: Int, enabled: Boolean)

    fun isGlobalBlendOrderEnabledAt(@EntityInstance instance: Int, primitiveIndex: Int): Boolean

    fun getEnabledAttributesAt(@EntityInstance i: Int, primitiveIndex: Int): Set<VertexBuffer.VertexAttribute>

    fun getNativeObject(): Long
}

