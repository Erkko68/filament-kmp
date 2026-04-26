package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Entity
import io.github.erkko68.filament.EntityManager
import io.github.erkko68.filament.js.`gltfio_FilamentAsset` as JSFilamentAsset
import io.github.erkko68.filament.js.Entity as JSEntity
import io.github.erkko68.filament.js.Aabb

actual class FilamentAsset(
    internal val jsAsset: JSFilamentAsset,
    private val _engine: Engine? = null
) {
    private fun JSEntity.registerAndGetId(): Entity {
        val id = getId().toInt()
        EntityManager.register(id, this)
        return id
    }

    private fun Array<JSEntity>.registerAndGetIds(): IntArray {
        return IntArray(size) { i -> this[i].registerAndGetId() }
    }

    actual fun getRoot(): Entity = jsAsset.getRoot().registerAndGetId()

    actual fun popRenderable(): Entity = jsAsset.popRenderable().registerAndGetId()

    actual fun popRenderables(entities: IntArray): Int = 0

    actual fun getEntities(): IntArray = jsAsset.getEntities().registerAndGetIds()

    actual fun getLightEntities(): IntArray = jsAsset.getLightEntities().registerAndGetIds()

    actual fun getRenderableEntities(): IntArray = jsAsset.getRenderableEntities().registerAndGetIds()

    actual fun getCameraEntities(): IntArray = jsAsset.getCameraEntities().registerAndGetIds()

    actual fun getEntitiesByName(name: String): IntArray = jsAsset.getEntitiesByName(name).registerAndGetIds()

    actual fun getEntitiesByPrefix(prefix: String): IntArray = jsAsset.getEntitiesByPrefix(prefix).registerAndGetIds()

    actual fun getFirstEntityByName(name: String): Entity = jsAsset.getEntityByName(name).registerAndGetId()

    actual fun getEntityCount(): Int {
        return jsAsset.getEntities().size
    }

    actual fun getAssetInstanceCount(): Int {
        return jsAsset.geAssetInstances().size
    }

    actual fun getAssetInstances(): Array<FilamentInstance> {
        val jsInstances = jsAsset.geAssetInstances()
        return Array(jsInstances.size) { i -> FilamentInstance(jsInstances[i]) }
    }

    actual fun getBoundingBox(): Box {
        val aabb = jsAsset.getBoundingBox()
        val minArr = aabb.min.unsafeCast<Array<Number>>()
        val maxArr = aabb.max.unsafeCast<Array<Number>>()
        return Box(
            (minArr[0].toFloat() + maxArr[0].toFloat()) / 2f,
            (minArr[1].toFloat() + maxArr[1].toFloat()) / 2f,
            (minArr[2].toFloat() + maxArr[2].toFloat()) / 2f,
            (maxArr[0].toFloat() - minArr[0].toFloat()) / 2f,
            (maxArr[1].toFloat() - minArr[1].toFloat()) / 2f,
            (maxArr[2].toFloat() - minArr[2].toFloat()) / 2f
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
        // getMorphTargetNames is not available in JS bindings
        return emptyArray()
    }

    actual fun getResourceUris(): Array<String> {
        return jsAsset.getResourceUris()
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