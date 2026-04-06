package dev.filament.kmp

import kotlinx.cinterop.*
import filament.*

actual class Scene internal constructor(
    internal val nativeObject: CPointer<FilaScene>
) {
    actual fun setSkybox(skybox: Skybox?) {
        FilaScene_setSkybox(nativeObject, skybox?.nativeObject)
    }

    actual fun setIndirectLight(indirectLight: IndirectLight?) {
        FilaScene_setIndirectLight(nativeObject, indirectLight?.nativeObject)
    }

    actual fun addEntity(@Entity entity: Int) {
        FilaScene_addEntity(nativeObject, entity.toUInt())
    }

    actual fun addEntities(entities: IntArray) {
        memScoped {
            val nativeEntities = allocArray<FilaEntityVar>(entities.size)
            for (i in entities.indices) {
                nativeEntities[i] = entities[i].toUInt()
            }
            FilaScene_addEntities(nativeObject, nativeEntities, entities.size.toULong())
        }
    }

    actual fun remove(@Entity entity: Int) {
        FilaScene_remove(nativeObject, entity.toUInt())
    }

    actual fun removeEntities(entities: IntArray) {
        memScoped {
            val nativeEntities = allocArray<FilaEntityVar>(entities.size)
            for (i in entities.indices) {
                nativeEntities[i] = entities[i].toUInt()
            }
            FilaScene_removeEntities(nativeObject, nativeEntities, entities.size.toULong())
        }
    }

    actual fun getEntityCount(): Int = FilaScene_getEntityCount(nativeObject).toInt()
    actual fun getRenderableCount(): Int = FilaScene_getRenderableCount(nativeObject).toInt()
    actual fun getLightCount(): Int = FilaScene_getLightCount(nativeObject).toInt()

    actual fun hasEntity(@Entity entity: Int): Boolean = FilaScene_hasEntity(nativeObject, entity.toUInt())

    actual fun getEntities(out: IntArray?): IntArray {
        val count = getEntityCount()
        val result = if (out != null && out.size >= count) out else IntArray(count)
        memScoped {
            val nativeEntities = allocArray<FilaEntityVar>(count)
            FilaScene_getEntities(nativeObject, nativeEntities, count.toULong())
            for (i in 0 until count) {
                result[i] = nativeEntities[i].toInt()
            }
        }
        return result
    }
}
