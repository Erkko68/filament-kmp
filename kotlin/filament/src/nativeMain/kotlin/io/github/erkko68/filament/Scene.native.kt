@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*
import cnames.structs.FilaScene

actual class Scene internal constructor(internal var nativeHandle: CPointer<FilaScene>?) {
    private var _skybox: Skybox? = null
    private var _indirectLight: IndirectLight? = null

    actual var skybox: Skybox?
        get() = _skybox
        set(value) {
            _skybox = value
            FilaScene_setSkybox(nativeHandle, value?.nativeHandle)
        }

    actual var indirectLight: IndirectLight?
        get() = _indirectLight
        set(value) {
            _indirectLight = value
            FilaScene_setIndirectLight(nativeHandle, value?.nativeHandle)
        }

    actual fun addEntity(entity: Entity) = FilaScene_addEntity(nativeHandle, entity.toUInt())

    actual fun addEntities(entities: IntArray) {
        entities.usePinned { pinned ->
            FilaScene_addEntities(nativeHandle, pinned.addressOf(0).reinterpret(), entities.size.toULong())
        }
    }

    actual fun removeEntity(entity: Entity) = FilaScene_remove(nativeHandle, entity.toUInt())
    actual fun remove(entity: Entity) = FilaScene_remove(nativeHandle, entity.toUInt())

    actual fun removeEntities(entities: IntArray) {
        entities.usePinned { pinned ->
            FilaScene_removeEntities(nativeHandle, pinned.addressOf(0).reinterpret(), entities.size.toULong())
        }
    }

    actual val entityCount: Int get() = FilaScene_getEntityCount(nativeHandle).toInt()
    actual val renderableCount: Int get() = FilaScene_getRenderableCount(nativeHandle).toInt()
    actual val lightCount: Int get() = FilaScene_getLightCount(nativeHandle).toInt()
    actual fun hasEntity(entity: Entity): Boolean = FilaScene_hasEntity(nativeHandle, entity.toUInt())

    actual fun getEntities(): IntArray = getEntities(null)

    actual fun getEntities(outArray: IntArray?): IntArray {
        val count = entityCount
        val result = if (outArray != null && outArray.size >= count) outArray else IntArray(count)
        if (count > 0) {
            result.usePinned { pinned ->
                FilaScene_getEntities(nativeHandle, pinned.addressOf(0).reinterpret(), count.toULong())
            }
        }
        return result
    }

    actual fun forEach(block: (Entity) -> Unit) {
        getEntities().forEach(block)
    }
}
