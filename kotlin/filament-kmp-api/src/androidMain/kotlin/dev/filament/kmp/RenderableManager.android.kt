package dev.filament.kmp

import com.google.android.filament.RenderableManager as AndroidRenderableManager

actual class RenderableManager internal constructor(
    internal val androidRenderableManager: AndroidRenderableManager,
) {
    actual fun hasComponent(entity: Int): Boolean = androidRenderableManager.hasComponent(entity)

    actual fun getInstance(entity: Int): Int = androidRenderableManager.getInstance(entity)

    actual fun destroy(entity: Int) {
        androidRenderableManager.destroy(entity)
    }

    actual fun setAxisAlignedBoundingBox(i: Int, aabb: Box) {
        androidRenderableManager.setAxisAlignedBoundingBox(i, aabb.androidBox)
    }

    actual fun setLayerMask(i: Int, select: Int, value: Int) {
        androidRenderableManager.setLayerMask(i, select, value)
    }

    actual fun setPriority(i: Int, priority: Int) {
        androidRenderableManager.setPriority(i, priority)
    }

    actual fun setMaterialInstanceAt(i: Int, primitiveIndex: Int, materialInstance: MaterialInstance) {
        val mi = requireNotNull(materialInstance.androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }
        androidRenderableManager.setMaterialInstanceAt(i, primitiveIndex, mi)
    }

    actual fun getMaterialInstanceAt(i: Int, primitiveIndex: Int): MaterialInstance {
        return MaterialInstance(androidRenderableManager.getMaterialInstanceAt(i, primitiveIndex))
    }

    actual fun setGeometryAt(
        i: Int,
        primitiveIndex: Int,
        type: PrimitiveType,
        vertices: VertexBuffer,
        indices: IndexBuffer,
    ) {
        val vb = requireNotNull(vertices.androidVertexBuffer) { "Calling method on destroyed VertexBuffer" }
        val ib = requireNotNull(indices.androidIndexBuffer) { "Calling method on destroyed IndexBuffer" }
        androidRenderableManager.setGeometryAt(i, primitiveIndex, type.toAndroid(), vb, ib)
    }

    actual fun getNativeObject(): Long = androidRenderableManager.nativeObject

    actual class Builder actual constructor(count: Int) {
        private val androidBuilder = AndroidRenderableManager.Builder(count)

        actual fun geometry(index: Int, type: PrimitiveType, vertices: VertexBuffer, indices: IndexBuffer): Builder {
            val vb = requireNotNull(vertices.androidVertexBuffer) { "Calling method on destroyed VertexBuffer" }
            val ib = requireNotNull(indices.androidIndexBuffer) { "Calling method on destroyed IndexBuffer" }
            androidBuilder.geometry(index, type.toAndroid(), vb, ib)
            return this
        }

        actual fun material(index: Int, material: MaterialInstance): Builder {
            val mi = requireNotNull(material.androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }
            androidBuilder.material(index, mi)
            return this
        }

        actual fun boundingBox(aabb: Box): Builder {
            androidBuilder.boundingBox(aabb.androidBox)
            return this
        }

        actual fun build(engine: Engine, entity: Int) {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            androidBuilder.build(androidEngine, entity)
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

private fun RenderableManager.PrimitiveType.toAndroid(): AndroidRenderableManager.PrimitiveType = when (this) {
    RenderableManager.PrimitiveType.POINTS -> AndroidRenderableManager.PrimitiveType.POINTS
    RenderableManager.PrimitiveType.LINES -> AndroidRenderableManager.PrimitiveType.LINES
    RenderableManager.PrimitiveType.LINE_STRIP -> AndroidRenderableManager.PrimitiveType.LINE_STRIP
    RenderableManager.PrimitiveType.TRIANGLES -> AndroidRenderableManager.PrimitiveType.TRIANGLES
    RenderableManager.PrimitiveType.TRIANGLE_STRIP -> AndroidRenderableManager.PrimitiveType.TRIANGLE_STRIP
}

