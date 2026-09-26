package io.github.erkko68.filament

import io.github.erkko68.filament.wasm.*

actual class EntityManager @InternalFilamentApi constructor(internal var nativeHandle: Int) {
    actual companion object {
        private val instance = EntityManager(FilaEntityManager_get())
        actual fun get(): EntityManager = instance
    }

    actual fun create(): Entity = FilaEntityManager_create(nativeHandle).toInt()
    
    actual fun create(n: Int): IntArray {
        val result = IntArray(n)
        result.usePinned { 
            FilaEntityManager_createArray(nativeHandle, n, it)
        }
        return result
    }
    
    actual fun create(entities: IntArray): IntArray {
        entities.usePinned { 
            FilaEntityManager_createArray(nativeHandle, entities.size, it)
        }
        return entities
    }

    actual fun destroy(entity: Entity) = FilaEntityManager_destroy(nativeHandle, entity)
    
    actual fun destroy(entities: IntArray) {
        entities.usePinned { 
            FilaEntityManager_destroyArray(nativeHandle, entities.size, it)
        }
    }

    actual fun isAlive(entity: Entity): Boolean = FilaEntityManager_isAlive(nativeHandle, entity)

    actual fun advanceEpoch() {
        FilaEntityManager_advanceEpoch(nativeHandle)
    }

    actual val maxEntityCount: Int get() = FilaEntityManager_getMaxEntityCount(nativeHandle).toInt()
}
