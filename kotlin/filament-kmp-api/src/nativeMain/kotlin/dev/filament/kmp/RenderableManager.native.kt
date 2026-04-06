@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package dev.filament.kmp

import dev.filament.kmp.cinterop.*
import kotlinx.cinterop.*
import cnames.structs.FilaRenderableManager
import cnames.structs.FilaRenderableManagerBuilder

actual class RenderableManager internal constructor(internal var nativeHandle: CPointer<FilaRenderableManager>?) {
    actual enum class PrimitiveType {
        POINTS,
        LINES,
        LINE_STRIP,
        TRIANGLES,
        TRIANGLE_STRIP,
    }

    actual class Builder actual constructor(count: Int) {
        private val builder = FilaRenderableManagerBuilder_create(count.toULong())

        actual fun geometry(index: Int, type: PrimitiveType, vertices: VertexBuffer, indices: IndexBuffer): Builder {
            FilaRenderableManagerBuilder_geometry(builder, index.toULong(), type.toFila(), vertices.nativeHandle, indices.nativeHandle)
            return this
        }

        actual fun geometry(index: Int, type: PrimitiveType, vertices: VertexBuffer, indices: IndexBuffer, offset: Int, count: Int): Builder {
            FilaRenderableManagerBuilder_geometryAt(builder, index.toULong(), type.toFila(), vertices.nativeHandle, indices.nativeHandle, offset.toULong(), count.toULong())
            return this
        }

        actual fun geometry(
            index: Int,
            type: PrimitiveType,
            vertices: VertexBuffer,
            indices: IndexBuffer,
            offset: Int,
            minIndex: Int,
            maxIndex: Int,
            count: Int,
        ): Builder {
            FilaRenderableManagerBuilder_geometryWithIndices(builder, index.toULong(), type.toFila(), vertices.nativeHandle, indices.nativeHandle, offset.toULong(), minIndex.toULong(), maxIndex.toULong(), count.toULong())
            return this
        }

        actual enum class GeometryType {
            DYNAMIC,
            STATIC_BOUNDS,
            STATIC,
        }

        actual fun geometryType(type: GeometryType): Builder {
            FilaRenderableManagerBuilder_geometryType(builder, type.toFila())
            return this
        }

        actual fun material(index: Int, material: MaterialInstance): Builder {
            FilaRenderableManagerBuilder_material(builder, index.toULong(), material.nativeHandle)
            return this
        }

        actual fun blendOrder(index: Int, blendOrder: Int): Builder {
            FilaRenderableManagerBuilder_blendOrder(builder, index.toULong(), blendOrder.toShort().toUShort())
            return this
        }

        actual fun globalBlendOrderEnabled(index: Int, enabled: Boolean): Builder {
            FilaRenderableManagerBuilder_globalBlendOrderEnabled(builder, index.toULong(), enabled)
            return this
        }

        actual fun boundingBox(aabb: Box): Builder {
            val c = aabb.center
            val e = aabb.halfExtent
            FilaRenderableManagerBuilder_boundingBox(builder, c[0], c[1], c[2], e[0], e[1], e[2])
            return this
        }

        actual fun layerMask(select: Int, value: Int): Builder {
            FilaRenderableManagerBuilder_layerMask(builder, select.toUByte(), value.toUByte())
            return this
        }

        actual fun priority(priority: Int): Builder {
            FilaRenderableManagerBuilder_priority(builder, priority.toUByte())
            return this
        }

        actual fun channel(channel: Int): Builder {
            FilaRenderableManagerBuilder_channel(builder, channel.toUByte())
            return this
        }

        actual fun culling(enabled: Boolean): Builder {
            FilaRenderableManagerBuilder_culling(builder, enabled)
            return this
        }

        actual fun lightChannel(channel: Int, enable: Boolean): Builder {
            FilaRenderableManagerBuilder_lightChannel(builder, channel.toUInt(), enable)
            return this
        }

        actual fun instances(instanceCount: Int): Builder {
            FilaRenderableManagerBuilder_instances(builder, instanceCount.toULong())
            return this
        }

        actual fun castShadows(enabled: Boolean): Builder {
            FilaRenderableManagerBuilder_castShadows(builder, enabled)
            return this
        }

        actual fun receiveShadows(enabled: Boolean): Builder {
            FilaRenderableManagerBuilder_receiveShadows(builder, enabled)
            return this
        }

        actual fun screenSpaceContactShadows(enabled: Boolean): Builder {
            FilaRenderableManagerBuilder_screenSpaceContactShadows(builder, enabled)
            return this
        }

        actual fun enableSkinningBuffers(enabled: Boolean): Builder {
            FilaRenderableManagerBuilder_enableSkinningBuffers(builder, enabled)
            return this
        }

        actual fun fog(enabled: Boolean): Builder {
            FilaRenderableManagerBuilder_fog(builder, enabled)
            return this
        }

        actual fun skinning(skinningBuffer: SkinningBuffer?, boneCount: Int, offset: Int): Builder {
            FilaRenderableManagerBuilder_skinningBuffer(builder, skinningBuffer?.nativeHandle, boneCount.toUInt(), offset.toUInt())
            return this
        }

        actual fun skinning(boneCount: Int): Builder {
            FilaRenderableManagerBuilder_skinning(builder, boneCount.toUInt())
            return this
        }

        actual fun skinning(boneCount: Int, bones: Any): Builder {
            // This needs careful handling of the bones pointer.
            return this
        }

        actual fun morphing(targetCount: Int): Builder {
            FilaRenderableManagerBuilder_morphing(builder, targetCount.toUInt())
            return this
        }

        actual fun morphing(morphTargetBuffer: MorphTargetBuffer): Builder {
            FilaRenderableManagerBuilder_morphTargetBuffer(builder, morphTargetBuffer.nativeHandle)
            return this
        }

        actual fun morphing(level: Int, primitiveIndex: Int, offset: Int): Builder {
            FilaRenderableManagerBuilder_morphTargetBufferOffsetAt(builder, level.toUInt(), primitiveIndex.toUInt(), offset.toUInt())
            return this
        }

        actual fun build(engine: Engine, @Entity entity: Int) {
            if (!FilaRenderableManagerBuilder_build(builder, engine.nativeHandle, entity.toUInt())) {
                throw Exception("Failed to build renderable")
            }
            FilaRenderableManagerBuilder_destroy(builder)
        }
    }

    actual fun hasComponent(@Entity entity: Int): Boolean =
        FilaRenderableManager_hasComponent(nativeHandle, entity.toUInt())

    actual fun getInstance(@Entity entity: Int): Int =
        FilaRenderableManager_getInstance(nativeHandle, entity.toUInt()).toInt()

    actual fun destroy(@Entity entity: Int) =
        FilaRenderableManager_destroy(nativeHandle, entity.toUInt())

    actual fun setAxisAlignedBoundingBox(@EntityInstance i: Int, aabb: Box) {
        val c = aabb.center
        val e = aabb.halfExtent
        FilaRenderableManager_setAxisAlignedBoundingBox(nativeHandle, i.toUInt(), c[0], c[1], c[2], e[0], e[1], e[2])
    }

    actual fun getAxisAlignedBoundingBox(@EntityInstance i: Int, out: Box?): Box {
        val result = out ?: Box()
        memScoped {
            val center = allocArray<FloatVar>(3)
            val halfExtent = allocArray<FloatVar>(3)
            FilaRenderableManager_getAxisAlignedBoundingBox(nativeHandle, i.toUInt(), center, halfExtent)
            result.setCenter(center[0], center[1], center[2])
            result.setHalfExtent(halfExtent[0], halfExtent[1], halfExtent[2])
        }
        return result
    }

    actual fun setLayerMask(@EntityInstance i: Int, select: Int, value: Int) =
        FilaRenderableManager_setLayerMask(nativeHandle, i.toUInt(), select.toUByte(), value.toUByte())

    actual fun setPriority(@EntityInstance i: Int, priority: Int) =
        FilaRenderableManager_setPriority(nativeHandle, i.toUInt(), priority.toUByte())

    actual fun getPriority(@EntityInstance i: Int): Int =
        FilaRenderableManager_getPriority(nativeHandle, i.toUInt()).toInt()

    actual fun setChannel(@EntityInstance i: Int, channel: Int) =
        FilaRenderableManager_setChannel(nativeHandle, i.toUInt(), channel.toUByte())

    actual fun getChannel(@EntityInstance i: Int): Int =
        FilaRenderableManager_getChannel(nativeHandle, i.toUInt()).toInt()

    actual fun setCulling(@EntityInstance i: Int, enabled: Boolean) =
        FilaRenderableManager_setCulling(nativeHandle, i.toUInt(), enabled)

    actual fun isCullingEnabled(@EntityInstance i: Int): Boolean =
        FilaRenderableManager_isCullingEnabled(nativeHandle, i.toUInt())

    actual fun setFogEnabled(@EntityInstance i: Int, enabled: Boolean) =
        FilaRenderableManager_setFogEnabled(nativeHandle, i.toUInt(), enabled)

    actual fun getFogEnabled(@EntityInstance i: Int): Boolean =
        FilaRenderableManager_getFogEnabled(nativeHandle, i.toUInt())

    actual fun setLightChannel(@EntityInstance i: Int, channel: Int, enable: Boolean) =
        FilaRenderableManager_setLightChannel(nativeHandle, i.toUInt(), channel.toUInt(), enable)

    actual fun getLightChannel(@EntityInstance i: Int, channel: Int): Boolean =
        FilaRenderableManager_getLightChannel(nativeHandle, i.toUInt(), channel.toUInt())

    actual fun setCastShadows(@EntityInstance i: Int, enabled: Boolean) =
        FilaRenderableManager_setCastShadows(nativeHandle, i.toUInt(), enabled)

    actual fun setReceiveShadows(@EntityInstance i: Int, enabled: Boolean) =
        FilaRenderableManager_setReceiveShadows(nativeHandle, i.toUInt(), enabled)

    actual fun setScreenSpaceContactShadows(@EntityInstance i: Int, enabled: Boolean) =
        FilaRenderableManager_setScreenSpaceContactShadows(nativeHandle, i.toUInt(), enabled)

    actual fun isScreenSpaceContactShadowsEnabled(@EntityInstance i: Int): Boolean =
        FilaRenderableManager_isScreenSpaceContactShadowsEnabled(nativeHandle, i.toUInt())

    actual fun isShadowCaster(@EntityInstance i: Int): Boolean =
        FilaRenderableManager_isShadowCaster(nativeHandle, i.toUInt())

    actual fun isShadowReceiver(@EntityInstance i: Int): Boolean =
        FilaRenderableManager_isShadowReceiver(nativeHandle, i.toUInt())

    actual fun setSkinningBuffer(@EntityInstance i: Int, skinningBuffer: SkinningBuffer, count: Int, offset: Int) =
        FilaRenderableManager_setSkinningBuffer(nativeHandle, i.toUInt(), skinningBuffer.nativeHandle, count.toUInt(), offset.toUInt())

    actual fun setBonesAsMatrices(@EntityInstance i: Int, matrices: Any, boneCount: Int, offset: Int) {
        // This needs careful handling.
    }

    actual fun setBonesAsQuaternions(@EntityInstance i: Int, quaternions: Any, boneCount: Int, offset: Int) {
        // This needs careful handling.
    }

    actual fun setMorphWeights(@EntityInstance i: Int, weights: FloatArray, offset: Int) {
        weights.usePinned {
            FilaRenderableManager_setMorphWeights(nativeHandle, i.toUInt(), it.addressOf(0), weights.size.toUInt(), offset.toUInt())
        }
    }

    actual fun setMorphTargetBufferOffsetAt(@EntityInstance i: Int, level: Int, primitiveIndex: Int, offset: Int) =
        FilaRenderableManager_setMorphTargetBufferOffsetAt(nativeHandle, i.toUInt(), level.toUByte(), primitiveIndex.toULong(), offset.toULong())

    actual fun getMorphTargetCount(@EntityInstance i: Int): Int =
        FilaRenderableManager_getMorphTargetCount(nativeHandle, i.toUInt()).toInt()

    actual fun getPrimitiveCount(@EntityInstance i: Int): Int =
        FilaRenderableManager_getPrimitiveCount(nativeHandle, i.toUInt()).toInt()

    actual fun getInstanceCount(@EntityInstance i: Int): Int =
        FilaRenderableManager_getInstanceCount(nativeHandle, i.toUInt()).toInt()

    actual fun setMaterialInstanceAt(@EntityInstance i: Int, primitiveIndex: Int, materialInstance: MaterialInstance) =
        FilaRenderableManager_setMaterialInstanceAt(nativeHandle, i.toUInt(), primitiveIndex.toULong(), materialInstance.nativeHandle)

    actual fun clearMaterialInstanceAt(@EntityInstance i: Int, primitiveIndex: Int) =
        FilaRenderableManager_clearMaterialInstanceAt(nativeHandle, i.toUInt(), primitiveIndex.toULong())

    actual fun getMaterialInstanceAt(@EntityInstance i: Int, primitiveIndex: Int): MaterialInstance {
        val ptr = FilaRenderableManager_getMaterialInstanceAt(nativeHandle, i.toUInt(), primitiveIndex.toULong())
            ?: throw Exception("Failed to get MaterialInstance")
        return MaterialInstance(ptr)
    }

    actual fun setGeometryAt(
        @EntityInstance i: Int,
        primitiveIndex: Int,
        type: PrimitiveType,
        vertices: VertexBuffer,
        indices: IndexBuffer,
    ) = FilaRenderableManager_setGeometryAt(nativeHandle, i.toUInt(), primitiveIndex.toULong(), type.toFila(), vertices.nativeHandle, indices.nativeHandle, 0u, 0u)

    actual fun setGeometryAt(
        @EntityInstance i: Int,
        primitiveIndex: Int,
        type: PrimitiveType,
        vertices: VertexBuffer,
        indices: IndexBuffer,
        offset: Int,
        count: Int,
    ) = FilaRenderableManager_setGeometryAt(nativeHandle, i.toUInt(), primitiveIndex.toULong(), type.toFila(), vertices.nativeHandle, indices.nativeHandle, offset.toULong(), count.toULong())

    actual fun setBlendOrderAt(@EntityInstance instance: Int, primitiveIndex: Int, blendOrder: Int) =
        FilaRenderableManager_setBlendOrderAt(nativeHandle, instance.toUInt(), primitiveIndex.toULong(), blendOrder.toShort().toUShort())

    actual fun getBlendOrderAt(@EntityInstance instance: Int, primitiveIndex: Int): Int =
        FilaRenderableManager_getBlendOrderAt(nativeHandle, instance.toUInt(), primitiveIndex.toULong()).toInt()

    actual fun setGlobalBlendOrderEnabledAt(@EntityInstance instance: Int, primitiveIndex: Int, enabled: Boolean) =
        FilaRenderableManager_setGlobalBlendOrderEnabledAt(nativeHandle, instance.toUInt(), primitiveIndex.toULong(), enabled)

    actual fun isGlobalBlendOrderEnabledAt(@EntityInstance instance: Int, primitiveIndex: Int): Boolean =
        FilaRenderableManager_isGlobalBlendOrderEnabledAt(nativeHandle, instance.toUInt(), primitiveIndex.toULong())

    actual fun getEnabledAttributesAt(@EntityInstance i: Int, primitiveIndex: Int): Set<VertexBuffer.VertexAttribute> {
        val bitmask = FilaRenderableManager_getEnabledAttributesAt(nativeHandle, i.toUInt(), primitiveIndex.toULong())
        val result = mutableSetOf<VertexBuffer.VertexAttribute>()
        VertexBuffer.VertexAttribute.entries.forEach { attr ->
            if (((1u shl attr.ordinal).toUInt() and bitmask) != 0u) {
                result.add(attr)
            }
        }
        return result
    }

    actual val nativeObject: Long
        get() = nativeHandle?.rawValue?.toLong() ?: 0L

    actual internal fun invalidate() {
        nativeHandle = null
    }

    private fun PrimitiveType.toFila(): FilaRenderableManagerPrimitiveType = when (this) {
        PrimitiveType.POINTS -> FILA_RENDERABLE_MANAGER_PRIMITIVE_TYPE_POINTS
        PrimitiveType.LINES -> FILA_RENDERABLE_MANAGER_PRIMITIVE_TYPE_LINES
        PrimitiveType.LINE_STRIP -> FILA_RENDERABLE_MANAGER_PRIMITIVE_TYPE_LINES // TODO: check if LINE_STRIP exists in c-wrapper
        PrimitiveType.TRIANGLES -> FILA_RENDERABLE_MANAGER_PRIMITIVE_TYPE_TRIANGLES
        PrimitiveType.TRIANGLE_STRIP -> FILA_RENDERABLE_MANAGER_PRIMITIVE_TYPE_TRIANGLES // TODO: check if TRIANGLE_STRIP exists in c-wrapper
    }

    private fun Builder.GeometryType.toFila(): FilaRenderableManagerGeometryType = when (this) {
        Builder.GeometryType.DYNAMIC -> FILA_RENDERABLE_GEOMETRY_TYPE_DYNAMIC
        Builder.GeometryType.STATIC_BOUNDS -> FILA_RENDERABLE_GEOMETRY_TYPE_STATIC
        Builder.GeometryType.STATIC -> FILA_RENDERABLE_GEOMETRY_TYPE_STATIC
    }
}
