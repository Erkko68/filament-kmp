package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.*
import io.github.erkko68.filament.jni.*
import io.github.erkko68.filament.jni.*
import io.github.erkko68.filament.InternalFilamentApi

actual class FilamentAsset @InternalFilamentApi constructor(internal var nativeHandle: Long) {
    actual val root: Entity get() = FilaFilamentAsset_getRoot(nativeHandle)

    actual fun popRenderable(): Entity = FilaFilamentAsset_popRenderable(nativeHandle)

    actual fun popRenderables(entities: IntArray): Int {
        val count = entities.size
        heapScoped {
            val filaEntities = I32Array(alloc((count) * 4))
            val popped = FilaFilamentAsset_popRenderables(nativeHandle, filaEntities.ptr, count.toLong()).toInt()
            for (i in 0 until popped) {
                entities[i] = filaEntities[i]
            }
            return popped
        }
    }

    actual val entities: IntArray get() {
        val count = FilaFilamentAsset_getEntityCount(nativeHandle).toInt()
        if (count == 0) return IntArray(0)
        heapScoped {
            val entities = I32Array(alloc((count) * 4))
            FilaFilamentAsset_getEntities(nativeHandle, entities.ptr)
            return IntArray(count) { entities[it] }
        }
    }

    actual val lightEntities: IntArray get() {
        val count = FilaFilamentAsset_getLightEntityCount(nativeHandle).toInt()
        if (count == 0) return IntArray(0)
        heapScoped {
            val entities = I32Array(alloc((count) * 4))
            FilaFilamentAsset_getLightEntities(nativeHandle, entities.ptr)
            return IntArray(count) { entities[it] }
        }
    }

    actual val renderableEntities: IntArray get() {
        val count = FilaFilamentAsset_getRenderableEntityCount(nativeHandle).toInt()
        if (count == 0) return IntArray(0)
        heapScoped {
            val entities = I32Array(alloc((count) * 4))
            FilaFilamentAsset_getRenderableEntities(nativeHandle, entities.ptr)
            return IntArray(count) { entities[it] }
        }
    }

    actual val cameraEntities: IntArray get() {
        val count = FilaFilamentAsset_getCameraEntityCount(nativeHandle).toInt()
        if (count == 0) return IntArray(0)
        heapScoped {
            val entities = I32Array(alloc((count) * 4))
            FilaFilamentAsset_getCameraEntities(nativeHandle, entities.ptr)
            return IntArray(count) { entities[it] }
        }
    }

    actual fun getEntitiesByName(name: String): IntArray {
        heapScoped {
            val maxCount = FilaFilamentAsset_getEntityCount(nativeHandle)
            if (maxCount == 0L) return IntArray(0)
            val entities = I32Array(alloc((maxCount.toInt()) * 4))
            val actualCount = FilaFilamentAsset_getEntitiesByName(nativeHandle, name, entities.ptr, maxCount)
            return IntArray(actualCount.toInt()) { entities[it] }
        }
    }

    actual fun getEntitiesByPrefix(prefix: String): IntArray {
         heapScoped {
            val maxCount = FilaFilamentAsset_getEntityCount(nativeHandle)
            if (maxCount == 0L) return IntArray(0)
            val entities = I32Array(alloc((maxCount.toInt()) * 4))
            val actualCount = FilaFilamentAsset_getEntitiesByPrefix(nativeHandle, prefix, entities.ptr, maxCount)
            return IntArray(actualCount.toInt()) { entities[it] }
        }
    }
    
    actual fun getFirstEntityByName(name: String): Entity = FilaFilamentAsset_getFirstEntityByName(nativeHandle, name)

    actual val entityCount: Int get() = FilaFilamentAsset_getEntityCount(nativeHandle).toInt()

    actual val assetInstanceCount: Int get() = FilaFilamentAsset_getAssetInstanceCount(nativeHandle).toInt()

    actual val assetInstances: List<FilamentInstance> get() {
        val count = FilaFilamentAsset_getAssetInstanceCount(nativeHandle).toInt()
        if (count == 0) return emptyList()
        heapScoped {
            val instances = PtrArray(alloc(count * PtrArray.SIZE))
            FilaFilamentAsset_getAssetInstances(nativeHandle, instances.ptr)
            return List(count) { FilamentInstance(instances[it]) }
        }
    }

    actual val boundingBox: Box get() {
        return heapScoped {
            val box = FilaBox(alloc(FilaBox.SIZE))
            FilaFilamentAsset_getBoundingBox(box.ptr, nativeHandle)
            Box(box.centerX, box.centerY, box.centerZ, box.halfExtentX, box.halfExtentY, box.halfExtentZ)
        }
    }

    actual fun getName(entity: Entity): String? = FilaFilamentAsset_getName(nativeHandle, entity)

    actual fun getExtras(entity: Entity): String? = FilaFilamentAsset_getExtras(nativeHandle, entity)

    actual fun getMorphTargetNames(entity: Entity): List<String> {
        val count = FilaFilamentAsset_getMorphTargetCountAt(nativeHandle, entity).toInt()
        if (count == 0) return emptyList()
        return List(count) {
            FilaFilamentAsset_getMorphTargetNameAt(nativeHandle, entity, it.toLong()) ?: ""
        }
    }

    actual val resourceUris: List<String> get() {
        val count = FilaFilamentAsset_getResourceUriCount(nativeHandle).toInt()
        if (count == 0) return emptyList()
        heapScoped {
            val uris = PtrArray(alloc(count * PtrArray.SIZE))
            FilaFilamentAsset_getResourceUris(nativeHandle, uris.ptr)
            return List(count) { readString(uris[it]) ?: "" }
        }
    }

    actual fun releaseSourceData() {
        FilaFilamentAsset_releaseSourceData(nativeHandle)
    }

    actual val engine: io.github.erkko68.filament.Engine get() =
        io.github.erkko68.filament.Engine(FilaFilamentAsset_getEngine(nativeHandle))

    actual val instance: FilamentInstance get() =
        FilamentInstance(FilaFilamentAsset_getInstance(nativeHandle))
}
