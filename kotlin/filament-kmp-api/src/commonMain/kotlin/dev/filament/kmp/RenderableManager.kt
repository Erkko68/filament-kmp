package dev.filament.kmp

/**
 * Factory and manager for renderables, which are entities that can be drawn.
 */
expect class RenderableManager {
    fun hasComponent(@Entity entity: Int): Boolean

    @EntityInstance
    fun getInstance(@Entity entity: Int): Int

    fun destroy(@Entity entity: Int)

    enum class PrimitiveType {
        POINTS,
        LINES,
        LINE_STRIP,
        TRIANGLES,
        TRIANGLE_STRIP,
    }

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
         * Adds a renderable component to an entity.
         */
        fun build(engine: Engine, @Entity entity: Int)
    }

    fun setAxisAlignedBoundingBox(@EntityInstance i: Int, aabb: Box)

    fun setLayerMask(@EntityInstance i: Int, select: Int, value: Int)

    fun setPriority(@EntityInstance i: Int, priority: Int)

    fun setMaterialInstanceAt(@EntityInstance i: Int, primitiveIndex: Int, materialInstance: MaterialInstance)

    fun getMaterialInstanceAt(@EntityInstance i: Int, primitiveIndex: Int): MaterialInstance

    fun setGeometryAt(
        @EntityInstance i: Int,
        primitiveIndex: Int,
        type: PrimitiveType,
        vertices: VertexBuffer,
        indices: IndexBuffer,
    )

    fun getNativeObject(): Long
}

