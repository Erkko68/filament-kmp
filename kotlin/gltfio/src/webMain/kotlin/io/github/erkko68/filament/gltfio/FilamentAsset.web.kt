package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.web.interop.toFloatArray

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Entity
import io.github.erkko68.filament.EntityManager
import io.github.erkko68.filament.web.`gltfio_FilamentAsset` as JSFilamentAsset
import io.github.erkko68.filament.web.Entity as JSEntity
import io.github.erkko68.filament.web.Aabb
import io.github.erkko68.filament.InternalFilamentApi

actual class FilamentAsset @InternalFilamentApi constructor(
    internal val jsAsset: JSFilamentAsset,
    private val _engine: Engine? = null
) {
    private fun JSEntity.registerAndGetId(): Entity {
        val id = getId().toInt()
        EntityManager.register(id, this)
        return id
    }

    private fun js.array.ReadonlyArray<JSEntity>.registerAndGetIds(): IntArray {
        return IntArray(size) { i -> this[i]!!.registerAndGetId() }
    }

    actual fun getRoot(): Entity = jsAsset.getRoot().registerAndGetId()

    actual fun popRenderable(): Entity = jsAsset.popRenderable().registerAndGetId()

    actual fun popRenderables(entities: IntArray): Int {
        val popped = jsAsset.popRenderables(entities.size.toDouble())
        val count = popped.size().toInt()
        for (i in 0 until count) entities[i] = popped.get(i.toDouble()).registerAndGetId()
        return count
    }

    actual fun getEntities(): IntArray = jsAsset.getEntities().registerAndGetIds()

    actual fun getLightEntities(): IntArray = jsAsset.getLightEntities().registerAndGetIds()

    actual fun getRenderableEntities(): IntArray = jsAsset.getRenderableEntities().registerAndGetIds()

    actual fun getCameraEntities(): IntArray = jsAsset.getCameraEntities().registerAndGetIds()

    actual fun getEntitiesByName(name: String): IntArray = jsAsset.getEntitiesByName(name).registerAndGetIds()

    actual fun getEntitiesByPrefix(prefix: String): IntArray = jsAsset.getEntitiesByPrefix(prefix).registerAndGetIds()

    actual fun getFirstEntityByName(name: String): Entity = jsAsset.getFirstEntityByName(name).registerAndGetId()

    actual fun getEntityCount(): Int = jsAsset.getEntityCount().toInt()

    actual fun getAssetInstanceCount(): Int = jsAsset.getAssetInstanceCount().toInt()

    actual fun getAssetInstances(): Array<FilamentInstance> {
        val jsInstances = jsAsset.getAssetInstances()
        return Array(jsInstances.size) { i -> FilamentInstance(jsInstances[i]) }
    }

    actual fun getBoundingBox(): Box {
        val aabb = jsAsset.getBoundingBox()
        val minArr = aabb.min!!.toFloatArray(3)
        val maxArr = aabb.max!!.toFloatArray(3)
        return Box(
            (minArr[0] + maxArr[0]) / 2f,
            (minArr[1] + maxArr[1]) / 2f,
            (minArr[2] + maxArr[2]) / 2f,
            (maxArr[0] - minArr[0]) / 2f,
            (maxArr[1] - minArr[1]) / 2f,
            (maxArr[2] - minArr[2]) / 2f
        )
    }

    actual fun getName(entity: Entity): String? {
        // JS binding expects Entity, but KMP API uses Int. Entity ID is passed directly via unsafeCast
        return jsAsset.getName(EntityManager.jsEntityOf(entity)).let { if (it.isEmpty()) null else it }
    }

    actual fun getExtras(entity: Entity): String? {
        // JS binding expects Entity, but KMP API uses Int. Entity ID is passed directly via unsafeCast
        return jsAsset.getExtras(EntityManager.jsEntityOf(entity)).let { if (it.isEmpty()) null else it }
    }

    actual fun getMorphTargetNames(entity: Entity): Array<String> {
        val names = jsAsset.getMorphTargetNames(EntityManager.jsEntityOf(entity))
        return Array(names.size) { names[it].toString() }
    }

    actual fun getResourceUris(): Array<String> {
        val uris = jsAsset.getResourceUris()
        return Array(uris.size) { uris[it].toString() }
    }

    actual fun releaseSourceData() {
        jsAsset.releaseSourceData()
    }

    actual fun getEngine(): Engine {
        return _engine ?: throw UnsupportedOperationException("Engine reference not available - FilamentAsset was not created with Engine context")
    }

    actual fun getInstance(): FilamentInstance {
        return FilamentInstance(jsAsset.getInstance())
    }

    fun getWireframe(): Int {
        // Available in JS binding but not in expect definition
        return jsAsset.getWireframe().getId().toInt()
    }
}