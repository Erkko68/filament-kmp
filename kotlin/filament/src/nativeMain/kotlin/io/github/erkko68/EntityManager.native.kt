@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68

import kotlinx.cinterop.*
import io.github.erkko68.cinterop.*
import cnames.structs.FilaEntityManager

actual class EntityManager internal constructor(internal var nativeHandle: CPointer<FilaEntityManager>?) {
    actual companion object {
        private val instance = EntityManager(FilaEntityManager_get())
        actual fun get(): EntityManager = instance
    }

    actual fun create(): Entity = FilaEntityManager_create(nativeHandle).toInt()
    
    actual fun create(n: Int): IntArray {
        val result = IntArray(n)
        result.usePinned { 
            FilaEntityManager_createArray(nativeHandle, n.toULong(), it.addressOf(0).reinterpret())
        }
        return result
    }
    
    actual fun create(entities: IntArray): IntArray {
        entities.usePinned { 
            FilaEntityManager_createArray(nativeHandle, entities.size.toULong(), it.addressOf(0).reinterpret())
        }
        return entities
    }

    actual fun destroy(entity: Entity) = FilaEntityManager_destroy(nativeHandle, entity.toUInt())
    
    actual fun destroy(entities: IntArray) {
        entities.usePinned { 
            FilaEntityManager_destroyArray(nativeHandle, entities.size.toULong(), it.addressOf(0).reinterpret())
        }
    }

    actual fun isAlive(entity: Entity): Boolean = FilaEntityManager_isAlive(nativeHandle, entity.toUInt())
}
