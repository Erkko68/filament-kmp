@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament.gltfio

import kotlinx.cinterop.*
import io.github.erkko68.filament.*
import io.github.erkko68.filament.cinterop.*
import io.github.erkko68.filament.gltfio.cinterop.*
import cnames.structs.FilaFilamentAsset
import cnames.structs.FilaFilamentInstance

actual class FilamentAsset(public var nativeHandle: CPointer<FilaFilamentAsset>?) {
    actual fun getRoot(): Entity = FilaFilamentAsset_getRoot(nativeHandle).toInt()

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

    actual fun getEntities(): IntArray {
        val count = FilaFilamentAsset_getEntityCount(nativeHandle).toInt()
        if (count == 0) return IntArray(0)
        memScoped {
            val entities = allocArray<FilaEntityVar>(count)
            FilaFilamentAsset_getEntities(nativeHandle, entities)
            return IntArray(count) { entities[it].toInt() }
        }
    }

    actual fun getLightEntities(): IntArray {
        val count = FilaFilamentAsset_getLightEntityCount(nativeHandle).toInt()
        if (count == 0) return IntArray(0)
        memScoped {
            val entities = allocArray<FilaEntityVar>(count)
            FilaFilamentAsset_getLightEntities(nativeHandle, entities)
            return IntArray(count) { entities[it].toInt() }
        }
    }

    actual fun getRenderableEntities(): IntArray {
        val count = FilaFilamentAsset_getRenderableEntityCount(nativeHandle).toInt()
        if (count == 0) return IntArray(0)
        memScoped {
            val entities = allocArray<FilaEntityVar>(count)
            FilaFilamentAsset_getRenderableEntities(nativeHandle, entities)
            return IntArray(count) { entities[it].toInt() }
        }
    }

    actual fun getCameraEntities(): IntArray {
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

    actual fun getEntityCount(): Int = FilaFilamentAsset_getEntityCount(nativeHandle).toInt()

    actual fun getAssetInstanceCount(): Int = FilaFilamentAsset_getAssetInstanceCount(nativeHandle).toInt()

    actual fun getAssetInstances(): Array<FilamentInstance> {
        val count = FilaFilamentAsset_getAssetInstanceCount(nativeHandle).toInt()
        if (count == 0) return emptyArray()
        memScoped {
            val instances = allocArray<CPointerVar<FilaFilamentInstance>>(count)
            FilaFilamentAsset_getAssetInstances(nativeHandle, instances)
            return Array(count) { FilamentInstance(instances[it]) }
        }
    }

    actual fun getBoundingBox(): Box {
        return FilaFilamentAsset_getBoundingBox(nativeHandle).useContents {
            Box(
                centerX, centerY, centerZ,
                halfExtentX, halfExtentY, halfExtentZ
            )
        }
    }

    actual fun getName(entity: Entity): String? = FilaFilamentAsset_getName(nativeHandle, entity.toUInt())?.toKString()

    actual fun getExtras(entity: Entity): String? = FilaFilamentAsset_getExtras(nativeHandle, entity.toUInt())?.toKString()

    actual fun getMorphTargetNames(entity: Entity): Array<String> {
        val count = FilaFilamentAsset_getMorphTargetCountAt(nativeHandle, entity.toUInt()).toInt()
        if (count == 0) return emptyArray()
        return Array(count) {
            FilaFilamentAsset_getMorphTargetNameAt(nativeHandle, entity.toUInt(), it.toULong())?.toKString() ?: ""
        }
    }

    actual fun getResourceUris(): Array<String> {
        val count = FilaFilamentAsset_getResourceUriCount(nativeHandle).toInt()
        if (count == 0) return emptyArray()
        memScoped {
            val uris = allocArray<CPointerVar<ByteVar>>(count)
            FilaFilamentAsset_getResourceUris(nativeHandle, uris)
            return Array(count) { uris[it]?.toKString() ?: "" }
        }
    }

    actual fun releaseSourceData() {
        FilaFilamentAsset_releaseSourceData(nativeHandle)
    }

    actual fun getEngine(): io.github.erkko68.filament.Engine =
        io.github.erkko68.filament.Engine(FilaFilamentAsset_getEngine(nativeHandle))

    actual fun getInstance(): FilamentInstance =
        FilamentInstance(FilaFilamentAsset_getInstance(nativeHandle))
}
