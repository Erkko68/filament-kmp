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
         * Binds a material instance to the specified primitive.
         */
        fun material(index: Int, material: MaterialInstance): Builder

        /**
         * The axis-aligned bounding box of the renderable.
         */
        fun boundingBox(aabb: Box): Builder

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

    /**
     * Changes the material instance binding for the given primitive.
     *
     * @see Builder#material
     */
    fun setMaterialInstanceAt(@EntityInstance i: Int, primitiveIndex: Int, materialInstance: MaterialInstance)

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

    fun getNativeObject(): Long
}

