package io.github.erkko68.filament

import io.github.erkko68.filament.wasm.*

actual class Scene @InternalFilamentApi constructor(internal var nativeHandle: Int) {
    private var _skybox: Skybox? = null
    private var _indirectLight: IndirectLight? = null

    actual var skybox: Skybox?
        get() = _skybox
        set(value) {
            _skybox = value
            FilaScene_setSkybox(nativeHandle, value?.nativeHandle ?: 0)
        }

    actual var indirectLight: IndirectLight?
        get() = _indirectLight
        set(value) {
            _indirectLight = value
            FilaScene_setIndirectLight(nativeHandle, value?.nativeHandle ?: 0)
        }

    actual fun addEntity(entity: Entity) = FilaScene_addEntity(nativeHandle, entity)

    actual fun addEntities(entities: IntArray) {
        entities.usePinned { pinned ->
            FilaScene_addEntities(nativeHandle, pinned, entities.size)
        }
    }

    actual fun removeEntity(entity: Entity) = FilaScene_remove(nativeHandle, entity)
    actual fun remove(entity: Entity) = FilaScene_remove(nativeHandle, entity)

    actual fun removeEntities(entities: IntArray) {
        entities.usePinned { pinned ->
            FilaScene_removeEntities(nativeHandle, pinned, entities.size)
        }
    }

    actual val entityCount: Int get() = FilaScene_getEntityCount(nativeHandle).toInt()
    actual val renderableCount: Int get() = FilaScene_getRenderableCount(nativeHandle).toInt()
    actual val lightCount: Int get() = FilaScene_getLightCount(nativeHandle).toInt()
    actual fun hasEntity(entity: Entity): Boolean = FilaScene_hasEntity(nativeHandle, entity)

    actual fun getEntities(out: IntArray?): IntArray {
        val count = entityCount
        val result = if (out != null && out.size >= count) out else IntArray(count)
        if (count > 0) {
            result.usePinned { pinned ->
                FilaScene_getEntities(nativeHandle, pinned, count)
            }
        }
        return result
    }

    actual fun forEach(block: (Entity) -> Unit) {
        getEntities().forEach(block)
    }
}
