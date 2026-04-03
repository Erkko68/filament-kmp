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

    actual fun setLayerMask(@EntityInstance i: Int, select: Int, value: Int) {
        TODO("Not yet implemented")
    }

    actual fun setPriority(@EntityInstance i: Int, priority: Int) {
        TODO("Not yet implemented")
    }

    actual fun setMaterialInstanceAt(@EntityInstance i: Int, primitiveIndex: Int, materialInstance: MaterialInstance) {
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

    actual fun getNativeObject(): Long = TODO("Not yet implemented")

    actual class Builder actual constructor(count: Int) {
        actual fun geometry(index: Int, type: PrimitiveType, vertices: VertexBuffer, indices: IndexBuffer): Builder = TODO("Not yet implemented")

        actual fun material(index: Int, material: MaterialInstance): Builder = TODO("Not yet implemented")

        actual fun boundingBox(aabb: Box): Builder = TODO("Not yet implemented")

        actual fun build(engine: Engine, @Entity entity: Int) {
            TODO("Not yet implemented")
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

