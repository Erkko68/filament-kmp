package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.js.`gltfio_FilamentAsset` as JSFilamentAsset
import io.github.erkko68.filament.js.Entity
import io.github.erkko68.filament.js.Aabb

actual class FilamentAsset(
    internal val jsAsset: JSFilamentAsset,
    private val _engine: Engine? = null
) {
    actual fun getRoot(): Int {
        return jsAsset.getRoot().getId().toInt()
    }

    actual fun popRenderable(): Int {
        return jsAsset.popRenderable().getId().toInt()
    }

    actual fun popRenderables(entities: IntArray): Int {
        // popRenderables is not available in JS bindings, returning 0
        return 0
    }

    actual fun getEntities(): IntArray {
        val jsEntities = jsAsset.getEntities()
        val result = IntArray(jsEntities.size)
        for (i in jsEntities.indices) {
            result[i] = jsEntities[i].getId().toInt()
        }
        return result
    }

    actual fun getLightEntities(): IntArray {
        val jsEntities = jsAsset.getLightEntities()
        val result = IntArray(jsEntities.size)
        for (i in jsEntities.indices) {
            result[i] = jsEntities[i].getId().toInt()
        }
        return result
    }

    actual fun getRenderableEntities(): IntArray {
        val jsEntities = jsAsset.getRenderableEntities()
        val result = IntArray(jsEntities.size)
        for (i in jsEntities.indices) {
            result[i] = jsEntities[i].getId().toInt()
        }
        return result
    }

    actual fun getCameraEntities(): IntArray {
        val jsEntities = jsAsset.getCameraEntities()
        val result = IntArray(jsEntities.size)
        for (i in jsEntities.indices) {
            result[i] = jsEntities[i].getId().toInt()
        }
        return result
    }

    actual fun getEntitiesByName(name: String): IntArray {
        val jsEntities = jsAsset.getEntitiesByName(name)
        val result = IntArray(jsEntities.size)
        for (i in jsEntities.indices) {
            result[i] = jsEntities[i].getId().toInt()
        }
        return result
    }

    actual fun getEntitiesByPrefix(prefix: String): IntArray {
        val jsEntities = jsAsset.getEntitiesByPrefix(prefix)
        val result = IntArray(jsEntities.size)
        for (i in jsEntities.indices) {
            result[i] = jsEntities[i].getId().toInt()
        }
        return result
    }

    actual fun getFirstEntityByName(name: String): Int {
        // JS binding provides getEntityByName which returns the first matching entity
        return jsAsset.getEntityByName(name).getId().toInt()
    }

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

    actual fun getName(entity: Int): String? {
        // JS binding expects Entity, but KMP API uses Int. Entity ID is passed directly via unsafeCast
        return jsAsset.getName(entity.unsafeCast<Entity>()).let { if (it.isEmpty()) null else it }
    }

    actual fun getExtras(entity: Int): String? {
        // JS binding expects Entity, but KMP API uses Int. Entity ID is passed directly via unsafeCast
        return jsAsset.getExtras(entity.unsafeCast<Entity>()).let { if (it.isEmpty()) null else it }
    }

    actual fun getMorphTargetNames(entity: Int): Array<String> {
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