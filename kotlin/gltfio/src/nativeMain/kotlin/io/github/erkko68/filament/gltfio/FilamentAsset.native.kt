@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament.gltfio

import kotlinx.cinterop.*
import io.github.erkko68.filament.*
import io.github.erkko68.filament.cinterop.*
import io.github.erkko68.filament.gltfio.cinterop.*
import cnames.structs.FilaFilamentAsset
import cnames.structs.FilaFilamentInstance
import io.github.erkko68.filament.InternalFilamentApi

actual class FilamentAsset @InternalFilamentApi constructor(internal var nativeHandle: CPointer<FilaFilamentAsset>?) {
    actual val root: Entity get() = FilaFilamentAsset_getRoot(nativeHandle).toInt()

    actual fun popRenderable(): Entity = FilaFilamentAsset_popRenderable(nativeHandle).toInt()

    actual fun popRenderables(entities: IntArray): Int {
        val count = entities.size
        memScoped {
            val filaEntities = allocArray<FilaEntityVar>(count)
            val popped = FilaFilamentAsset_popRenderables(nativeHandle, filaEntities, count.toULong()).toInt()
            for (i in 0 until popped) {
                entities[i] = filaEntities[i].toInt()
            }
            return popped
        }
    }

    actual val entities: IntArray get() {
        val count = FilaFilamentAsset_getEntityCount(nativeHandle).toInt()
        if (count == 0) return IntArray(0)
        memScoped {
            val entities = allocArray<FilaEntityVar>(count)
            FilaFilamentAsset_getEntities(nativeHandle, entities)
            return IntArray(count) { entities[it].toInt() }
        }
    }

    actual val lightEntities: IntArray get() {
        val count = FilaFilamentAsset_getLightEntityCount(nativeHandle).toInt()
        if (count == 0) return IntArray(0)
        memScoped {
            val entities = allocArray<FilaEntityVar>(count)
            FilaFilamentAsset_getLightEntities(nativeHandle, entities)
            return IntArray(count) { entities[it].toInt() }
        }
    }

    actual val renderableEntities: IntArray get() {
        val count = FilaFilamentAsset_getRenderableEntityCount(nativeHandle).toInt()
        if (count == 0) return IntArray(0)
        memScoped {
            val entities = allocArray<FilaEntityVar>(count)
            FilaFilamentAsset_getRenderableEntities(nativeHandle, entities)
            return IntArray(count) { entities[it].toInt() }
        }
    }

    actual val cameraEntities: IntArray get() {
        val count = FilaFilamentAsset_getCameraEntityCount(nativeHandle).toInt()
        if (count == 0) return IntArray(0)
        memScoped {
            val entities = allocArray<FilaEntityVar>(count)
            FilaFilamentAsset_getCameraEntities(nativeHandle, entities)
            return IntArray(count) { entities[it].toInt() }
        }
    }

    actual fun getEntitiesByName(name: String): IntArray {
        memScoped {
            val maxCount = FilaFilamentAsset_getEntityCount(nativeHandle)
            if (maxCount == 0uL) return IntArray(0)
            val entities = allocArray<FilaEntityVar>(maxCount.toInt())
            val actualCount = FilaFilamentAsset_getEntitiesByName(nativeHandle, name, entities, maxCount)
            return IntArray(actualCount.toInt()) { entities[it].toInt() }
        }
    }

    actual fun getEntitiesByPrefix(prefix: String): IntArray {
         memScoped {
            val maxCount = FilaFilamentAsset_getEntityCount(nativeHandle)
            if (maxCount == 0uL) return IntArray(0)
            val entities = allocArray<FilaEntityVar>(maxCount.toInt())
            val actualCount = FilaFilamentAsset_getEntitiesByPrefix(nativeHandle, prefix, entities, maxCount)
            return IntArray(actualCount.toInt()) { entities[it].toInt() }
        }
    }
    
    actual fun getFirstEntityByName(name: String): Entity = FilaFilamentAsset_getFirstEntityByName(nativeHandle, name).toInt()

    actual val entityCount: Int get() = FilaFilamentAsset_getEntityCount(nativeHandle).toInt()

    actual val assetInstanceCount: Int get() = FilaFilamentAsset_getAssetInstanceCount(nativeHandle).toInt()

    actual val assetInstances: List<FilamentInstance> get() {
        val count = FilaFilamentAsset_getAssetInstanceCount(nativeHandle).toInt()
        if (count == 0) return emptyList()
        memScoped {
            val instances = allocArray<CPointerVar<FilaFilamentInstance>>(count)
            FilaFilamentAsset_getAssetInstances(nativeHandle, instances)
            return List(count) { FilamentInstance(instances[it]) }
        }
    }

    actual val boundingBox: Box get() {
        return FilaFilamentAsset_getBoundingBox(nativeHandle).useContents {
            Box(
                centerX, centerY, centerZ,
                halfExtentX, halfExtentY, halfExtentZ
            )
        }
    }

    actual fun getName(entity: Entity): String? = FilaFilamentAsset_getName(nativeHandle, entity.toUInt())?.toKString()

    actual fun getExtras(entity: Entity): String? = FilaFilamentAsset_getExtras(nativeHandle, entity.toUInt())?.toKString()

    actual fun getMorphTargetNames(entity: Entity): List<String> {
        val count = FilaFilamentAsset_getMorphTargetCountAt(nativeHandle, entity.toUInt()).toInt()
        if (count == 0) return emptyList()
        return List(count) {
            FilaFilamentAsset_getMorphTargetNameAt(nativeHandle, entity.toUInt(), it.toULong())?.toKString() ?: ""
        }
    }

    actual val resourceUris: List<String> get() {
        val count = FilaFilamentAsset_getResourceUriCount(nativeHandle).toInt()
        if (count == 0) return emptyList()
        memScoped {
            val uris = allocArray<CPointerVar<ByteVar>>(count)
            FilaFilamentAsset_getResourceUris(nativeHandle, uris)
            return List(count) { uris[it]?.toKString() ?: "" }
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
