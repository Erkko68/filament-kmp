package dev.filament.kmp

import java.nio.Buffer
import com.google.android.filament.RenderableManager as AndroidRenderableManager

actual class RenderableManager internal constructor(
    internal val androidRenderableManager: AndroidRenderableManager,
) {
    actual fun hasComponent(@Entity entity: Int): Boolean = androidRenderableManager.hasComponent(entity)

    @EntityInstance
    actual fun getInstance(@Entity entity: Int): Int = androidRenderableManager.getInstance(entity)

    actual fun destroy(@Entity entity: Int) {
        androidRenderableManager.destroy(entity)
    }

    actual fun setAxisAlignedBoundingBox(@EntityInstance i: Int, aabb: Box) {
        androidRenderableManager.setAxisAlignedBoundingBox(i, aabb.androidBox)
    }

    actual fun getAxisAlignedBoundingBox(@EntityInstance i: Int, out: Box?): Box {
        val target = out ?: Box()
        androidRenderableManager.getAxisAlignedBoundingBox(i, target.androidBox)
        return target
    }

    actual fun setLayerMask(@EntityInstance i: Int, select: Int, value: Int) {
        androidRenderableManager.setLayerMask(i, select, value)
    }

    actual fun setPriority(@EntityInstance i: Int, priority: Int) {
        androidRenderableManager.setPriority(i, priority)
    }

    actual fun getPriority(@EntityInstance i: Int): Int = androidRenderableManager.getPriority(i)

    actual fun setChannel(@EntityInstance i: Int, channel: Int) {
        androidRenderableManager.setChannel(i, channel)
    }

    actual fun getChannel(@EntityInstance i: Int): Int = androidRenderableManager.getChannel(i)

    actual fun setCulling(@EntityInstance i: Int, enabled: Boolean) {
        androidRenderableManager.setCulling(i, enabled)
    }

    actual fun isCullingEnabled(@EntityInstance i: Int): Boolean = androidRenderableManager.isCullingEnabled(i)

    actual fun setFogEnabled(@EntityInstance i: Int, enabled: Boolean) {
        androidRenderableManager.setFogEnabled(i, enabled)
    }

    actual fun getFogEnabled(@EntityInstance i: Int): Boolean = androidRenderableManager.getFogEnabled(i)

    actual fun setLightChannel(@EntityInstance i: Int, channel: Int, enable: Boolean) {
        androidRenderableManager.setLightChannel(i, channel, enable)
    }

    actual fun getLightChannel(@EntityInstance i: Int, channel: Int): Boolean = androidRenderableManager.getLightChannel(i, channel)

    actual fun setCastShadows(@EntityInstance i: Int, enabled: Boolean) {
        androidRenderableManager.setCastShadows(i, enabled)
    }

    actual fun setReceiveShadows(@EntityInstance i: Int, enabled: Boolean) {
        androidRenderableManager.setReceiveShadows(i, enabled)
    }

    actual fun setScreenSpaceContactShadows(@EntityInstance i: Int, enabled: Boolean) {
        androidRenderableManager.setScreenSpaceContactShadows(i, enabled)
    }

    actual fun isScreenSpaceContactShadowsEnabled(@EntityInstance i: Int): Boolean =
        androidRenderableManager.isScreenSpaceContactShadowsEnabled(i)

    actual fun isShadowCaster(@EntityInstance i: Int): Boolean = androidRenderableManager.isShadowCaster(i)

    actual fun isShadowReceiver(@EntityInstance i: Int): Boolean = androidRenderableManager.isShadowReceiver(i)

    actual fun setSkinningBuffer(@EntityInstance i: Int, skinningBuffer: SkinningBuffer, count: Int, offset: Int) {
        val sb = requireNotNull(skinningBuffer.androidSkinningBuffer) { "Calling method on destroyed SkinningBuffer" }
        androidRenderableManager.setSkinningBuffer(i, sb, count, offset)
    }

    actual fun setBonesAsMatrices(@EntityInstance i: Int, matrices: Any, boneCount: Int, offset: Int) {
        androidRenderableManager.setBonesAsMatrices(i, matrices as Buffer, boneCount, offset)
    }

    actual fun setBonesAsQuaternions(@EntityInstance i: Int, quaternions: Any, boneCount: Int, offset: Int) {
        androidRenderableManager.setBonesAsQuaternions(i, quaternions as Buffer, boneCount, offset)
    }

    actual fun setMorphWeights(@EntityInstance i: Int, weights: FloatArray, offset: Int) {
        androidRenderableManager.setMorphWeights(i, weights, offset)
    }

    actual fun setMorphTargetBufferOffsetAt(@EntityInstance i: Int, level: Int, primitiveIndex: Int, offset: Int) {
        androidRenderableManager.setMorphTargetBufferOffsetAt(i, level, primitiveIndex, offset)
    }

    actual fun getMorphTargetCount(@EntityInstance i: Int): Int = androidRenderableManager.getMorphTargetCount(i)

    actual fun getPrimitiveCount(@EntityInstance i: Int): Int = androidRenderableManager.getPrimitiveCount(i)

    actual fun getInstanceCount(@EntityInstance i: Int): Int = androidRenderableManager.getInstanceCount(i)

    actual fun setMaterialInstanceAt(@EntityInstance i: Int, primitiveIndex: Int, materialInstance: MaterialInstance) {
        val mi = requireNotNull(materialInstance.androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }
        androidRenderableManager.setMaterialInstanceAt(i, primitiveIndex, mi)
    }

    actual fun clearMaterialInstanceAt(@EntityInstance i: Int, primitiveIndex: Int) {
        androidRenderableManager.clearMaterialInstanceAt(i, primitiveIndex)
    }

    actual fun getMaterialInstanceAt(@EntityInstance i: Int, primitiveIndex: Int): MaterialInstance {
        return MaterialInstance(androidRenderableManager.getMaterialInstanceAt(i, primitiveIndex))
    }

    actual fun setGeometryAt(
        @EntityInstance i: Int,
        primitiveIndex: Int,
        type: PrimitiveType,
        vertices: VertexBuffer,
        indices: IndexBuffer,
    ) {
        val vb = requireNotNull(vertices.androidVertexBuffer) { "Calling method on destroyed VertexBuffer" }
        val ib = requireNotNull(indices.androidIndexBuffer) { "Calling method on destroyed IndexBuffer" }
        androidRenderableManager.setGeometryAt(i, primitiveIndex, type.toAndroid(), vb, ib)
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
        val vb = requireNotNull(vertices.androidVertexBuffer) { "Calling method on destroyed VertexBuffer" }
        val ib = requireNotNull(indices.androidIndexBuffer) { "Calling method on destroyed IndexBuffer" }
        androidRenderableManager.setGeometryAt(i, primitiveIndex, type.toAndroid(), vb, ib, offset, count)
    }

    actual fun setBlendOrderAt(@EntityInstance instance: Int, primitiveIndex: Int, blendOrder: Int) {
        androidRenderableManager.setBlendOrderAt(instance, primitiveIndex, blendOrder)
    }

    actual fun getBlendOrderAt(@EntityInstance instance: Int, primitiveIndex: Int): Int =
        androidRenderableManager.getBlendOrderAt(instance, primitiveIndex)

    actual fun setGlobalBlendOrderEnabledAt(@EntityInstance instance: Int, primitiveIndex: Int, enabled: Boolean) {
        androidRenderableManager.setGlobalBlendOrderEnabledAt(instance, primitiveIndex, enabled)
    }

    actual fun isGlobalBlendOrderEnabledAt(@EntityInstance instance: Int, primitiveIndex: Int): Boolean =
        androidRenderableManager.isGlobalBlendOrderEnabledAt(instance, primitiveIndex)

    actual fun getEnabledAttributesAt(@EntityInstance i: Int, primitiveIndex: Int): Set<VertexBuffer.VertexAttribute> {
        return androidRenderableManager.getEnabledAttributesAt(i, primitiveIndex).mapTo(linkedSetOf()) {
            VertexBuffer.VertexAttribute.valueOf(it.name)
        }
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

        actual fun geometry(
            index: Int,
            type: PrimitiveType,
            vertices: VertexBuffer,
            indices: IndexBuffer,
            offset: Int,
            count: Int,
        ): Builder {
            val vb = requireNotNull(vertices.androidVertexBuffer) { "Calling method on destroyed VertexBuffer" }
            val ib = requireNotNull(indices.androidIndexBuffer) { "Calling method on destroyed IndexBuffer" }
            androidBuilder.geometry(index, type.toAndroid(), vb, ib, offset, count)
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
            val vb = requireNotNull(vertices.androidVertexBuffer) { "Calling method on destroyed VertexBuffer" }
            val ib = requireNotNull(indices.androidIndexBuffer) { "Calling method on destroyed IndexBuffer" }
            androidBuilder.geometry(index, type.toAndroid(), vb, ib, offset, minIndex, maxIndex, count)
            return this
        }

        actual fun geometryType(type: GeometryType): Builder {
            androidBuilder.geometryType(AndroidRenderableManager.Builder.GeometryType.valueOf(type.name))
            return this
        }

        actual fun material(index: Int, material: MaterialInstance): Builder {
            val mi = requireNotNull(material.androidMaterialInstance) { "Calling method on destroyed MaterialInstance" }
            androidBuilder.material(index, mi)
            return this
        }

        actual fun blendOrder(index: Int, blendOrder: Int): Builder {
            androidBuilder.blendOrder(index, blendOrder)
            return this
        }

        actual fun globalBlendOrderEnabled(index: Int, enabled: Boolean): Builder {
            androidBuilder.globalBlendOrderEnabled(index, enabled)
            return this
        }

        actual fun boundingBox(aabb: Box): Builder {
            androidBuilder.boundingBox(aabb.androidBox)
            return this
        }

        actual fun layerMask(select: Int, value: Int): Builder {
            androidBuilder.layerMask(select, value)
            return this
        }

        actual fun priority(priority: Int): Builder {
            androidBuilder.priority(priority)
            return this
        }

        actual fun channel(channel: Int): Builder {
            androidBuilder.channel(channel)
            return this
        }

        actual fun culling(enabled: Boolean): Builder {
            androidBuilder.culling(enabled)
            return this
        }

        actual fun lightChannel(channel: Int, enable: Boolean): Builder {
            androidBuilder.lightChannel(channel, enable)
            return this
        }

        actual fun instances(instanceCount: Int): Builder {
            androidBuilder.instances(instanceCount)
            return this
        }

        actual fun castShadows(enabled: Boolean): Builder {
            androidBuilder.castShadows(enabled)
            return this
        }

        actual fun receiveShadows(enabled: Boolean): Builder {
            androidBuilder.receiveShadows(enabled)
            return this
        }

        actual fun screenSpaceContactShadows(enabled: Boolean): Builder {
            androidBuilder.screenSpaceContactShadows(enabled)
            return this
        }

        actual fun enableSkinningBuffers(enabled: Boolean): Builder {
            androidBuilder.enableSkinningBuffers(enabled)
            return this
        }

        actual fun fog(enabled: Boolean): Builder {
            androidBuilder.fog(enabled)
            return this
        }

        actual fun skinning(skinningBuffer: SkinningBuffer?, boneCount: Int, offset: Int): Builder {
            androidBuilder.skinning(skinningBuffer?.androidSkinningBuffer, boneCount, offset)
            return this
        }

        actual fun skinning(boneCount: Int): Builder {
            androidBuilder.skinning(boneCount)
            return this
        }

        actual fun skinning(boneCount: Int, bones: Any): Builder {
            androidBuilder.skinning(boneCount, bones as Buffer)
            return this
        }

        actual fun morphing(targetCount: Int): Builder {
            androidBuilder.morphing(targetCount)
            return this
        }

        actual fun morphing(morphTargetBuffer: MorphTargetBuffer): Builder {
            val mtb = requireNotNull(morphTargetBuffer.androidMorphTargetBuffer) { "Calling method on destroyed MorphTargetBuffer" }
            androidBuilder.morphing(mtb)
            return this
        }

        actual fun morphing(level: Int, primitiveIndex: Int, offset: Int): Builder {
            androidBuilder.morphing(level, primitiveIndex, offset)
            return this
        }

        actual fun build(engine: Engine, @Entity entity: Int) {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            androidBuilder.build(androidEngine, entity)
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

private fun RenderableManager.PrimitiveType.toAndroid(): AndroidRenderableManager.PrimitiveType = when (this) {
    RenderableManager.PrimitiveType.POINTS -> AndroidRenderableManager.PrimitiveType.POINTS
    RenderableManager.PrimitiveType.LINES -> AndroidRenderableManager.PrimitiveType.LINES
    RenderableManager.PrimitiveType.LINE_STRIP -> AndroidRenderableManager.PrimitiveType.LINE_STRIP
    RenderableManager.PrimitiveType.TRIANGLES -> AndroidRenderableManager.PrimitiveType.TRIANGLES
    RenderableManager.PrimitiveType.TRIANGLE_STRIP -> AndroidRenderableManager.PrimitiveType.TRIANGLE_STRIP
}

