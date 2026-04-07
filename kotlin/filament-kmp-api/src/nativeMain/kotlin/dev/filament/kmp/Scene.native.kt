@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaScene

actual class Scene internal constructor(internal var nativeHandle: CPointer<FilaScene>?) {
    private var skybox: Skybox? = null
    private var indirectLight: IndirectLight? = null

    actual fun setSkybox(skybox: Skybox?) {
        this.skybox = skybox
        FilaScene_setSkybox(nativeHandle, skybox?.nativeHandle)
    }
    actual fun getSkybox(): Skybox? = skybox

    actual fun setIndirectLight(ibl: IndirectLight?) {
        this.indirectLight = ibl
        FilaScene_setIndirectLight(nativeHandle, ibl?.nativeHandle)
    }
    actual fun getIndirectLight(): IndirectLight? = indirectLight

    actual fun addEntity(entity: Int) = FilaScene_addEntity(nativeHandle, entity.toUInt())

    actual fun addEntities(entities: IntArray) {
        entities.usePinned { pinned ->
            FilaScene_addEntities(nativeHandle, pinned.addressOf(0).reinterpret(), entities.size.toULong())
        }
    }

    actual fun removeEntity(entity: Int) = FilaScene_remove(nativeHandle, entity.toUInt())

    actual fun removeEntities(entities: IntArray) {
        entities.usePinned { pinned ->
            FilaScene_removeEntities(nativeHandle, pinned.addressOf(0).reinterpret(), entities.size.toULong())
        }
    }

    actual fun getEntityCount(): Int = FilaScene_getEntityCount(nativeHandle).toInt()
    actual fun getRenderableCount(): Int = FilaScene_getRenderableCount(nativeHandle).toInt()
    actual fun getLightCount(): Int = FilaScene_getLightCount(nativeHandle).toInt()
    actual fun hasEntity(entity: Int): Boolean = FilaScene_hasEntity(nativeHandle, entity.toUInt())

    actual fun getEntities(): IntArray = getEntities(null)

    actual fun getEntities(outArray: IntArray?): IntArray {
        val count = getEntityCount()
        val result = if (outArray != null && outArray.size >= count) outArray else IntArray(count)
        if (count > 0) {
            result.usePinned { pinned ->
                FilaScene_getEntities(nativeHandle, pinned.addressOf(0).reinterpret(), count.toULong())
            }
        }
        return result
    }
}
