package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.*
import io.github.erkko68.filament.wasm.*
import io.github.erkko68.filament.wasm.*
import io.github.erkko68.filament.InternalFilamentApi

actual class FilamentAsset @InternalFilamentApi constructor(internal var nativeHandle: Int) {
    actual val root: Entity get() = FilaFilamentAsset_getRoot(nativeHandle).toInt()

    actual fun popRenderable(): Entity = FilaFilamentAsset_popRenderable(nativeHandle).toInt()

    actual fun popRenderables(entities: IntArray): Int {
        val count = entities.size
        fila.heapScoped {
            val filaEntities = I32Array(alloc((count) * 4))
            val popped = FilaFilamentAsset_popRenderables(nativeHandle, filaEntities.ptr, count).toInt()
            for (i in 0 until popped) {
                entities[i] = filaEntities[i].toInt()
            }
            return popped
        }
    }

    actual val entities: IntArray get() {
        val count = FilaFilamentAsset_getEntityCount(nativeHandle).toInt()
        if (count == 0) return IntArray(0)
        fila.heapScoped {
            val entities = I32Array(alloc((count) * 4))
            FilaFilamentAsset_getEntities(nativeHandle, entities.ptr)
            return IntArray(count) { entities[it].toInt() }
        }
    }

    actual val lightEntities: IntArray get() {
        val count = FilaFilamentAsset_getLightEntityCount(nativeHandle).toInt()
        if (count == 0) return IntArray(0)
        fila.heapScoped {
            val entities = I32Array(alloc((count) * 4))
            FilaFilamentAsset_getLightEntities(nativeHandle, entities.ptr)
            return IntArray(count) { entities[it].toInt() }
        }
    }

    actual val renderableEntities: IntArray get() {
        val count = FilaFilamentAsset_getRenderableEntityCount(nativeHandle).toInt()
        if (count == 0) return IntArray(0)
        fila.heapScoped {
            val entities = I32Array(alloc((count) * 4))
            FilaFilamentAsset_getRenderableEntities(nativeHandle, entities.ptr)
            return IntArray(count) { entities[it].toInt() }
        }
    }

    actual val cameraEntities: IntArray get() {
        val count = FilaFilamentAsset_getCameraEntityCount(nativeHandle).toInt()
        if (count == 0) return IntArray(0)
        fila.heapScoped {
            val entities = I32Array(alloc((count) * 4))
            FilaFilamentAsset_getCameraEntities(nativeHandle, entities.ptr)
            return IntArray(count) { entities[it].toInt() }
        }
    }

    actual fun getEntitiesByName(name: String): IntArray {
        fila.heapScoped {
            val maxCount = FilaFilamentAsset_getEntityCount(nativeHandle)
            if (maxCount == 0) return IntArray(0)
            val entities = I32Array(alloc((maxCount.toInt()) * 4))
            val actualCount = FilaFilamentAsset_getEntitiesByName(nativeHandle, name, entities.ptr, maxCount)
            return IntArray(actualCount.toInt()) { entities[it].toInt() }
        }
    }

    actual fun getEntitiesByPrefix(prefix: String): IntArray {
         fila.heapScoped {
            val maxCount = FilaFilamentAsset_getEntityCount(nativeHandle)
            if (maxCount == 0) return IntArray(0)
            val entities = I32Array(alloc((maxCount.toInt()) * 4))
            val actualCount = FilaFilamentAsset_getEntitiesByPrefix(nativeHandle, prefix, entities.ptr, maxCount)
            return IntArray(actualCount.toInt()) { entities[it].toInt() }
        }
    }
    
    actual fun getFirstEntityByName(name: String): Entity = FilaFilamentAsset_getFirstEntityByName(nativeHandle, name).toInt()

    actual val entityCount: Int get() = FilaFilamentAsset_getEntityCount(nativeHandle).toInt()

    actual val assetInstanceCount: Int get() = FilaFilamentAsset_getAssetInstanceCount(nativeHandle).toInt()

    actual val assetInstances: List<FilamentInstance> get() {
        val count = FilaFilamentAsset_getAssetInstanceCount(nativeHandle).toInt()
        if (count == 0) return emptyList()
        fila.heapScoped {
            val instances = I32Array(alloc((count) * 4))
            FilaFilamentAsset_getAssetInstances(nativeHandle, instances.ptr)
            return List(count) { FilamentInstance(instances[it]) }
        }
    }

    actual val boundingBox: Box get() {
        return fila.heapScoped {
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
            FilaFilamentAsset_getMorphTargetNameAt(nativeHandle, entity, it) ?: ""
        }
    }

    actual val resourceUris: List<String> get() {
        val count = FilaFilamentAsset_getResourceUriCount(nativeHandle).toInt()
        if (count == 0) return emptyList()
        fila.heapScoped {
            val uris = I32Array(alloc((count) * 4))
            FilaFilamentAsset_getResourceUris(nativeHandle, uris.ptr)
            return List(count) { fila.readString(uris[it]) ?: "" }
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
