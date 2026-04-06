@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaEntityManager

actual class EntityManager internal constructor(internal var nativePtr: CPointer<FilaEntityManager>?) {
    @Entity
    actual fun create(): Int {
        return FilaEntityManager_create(nativePtr).toInt()
    }

    actual fun destroy(@Entity entity: Int) {
        FilaEntityManager_destroy(nativePtr, entity.toUInt())
    }

    @Entity
    actual fun create(n: Int): IntArray {
        val entities = IntArray(n)
        entities.usePinned { pinned ->
            FilaEntityManager_createArray(nativePtr, n.toULong(), pinned.addressOf(0).reinterpret())
        }
        return entities
    }

    actual fun create(@Entity entities: IntArray): IntArray {
        entities.usePinned { pinned ->
            FilaEntityManager_createArray(nativePtr, entities.size.toULong(), pinned.addressOf(0).reinterpret())
        }
        return entities
    }

    actual fun destroy(@Entity entities: IntArray) {
        entities.usePinned { pinned ->
            FilaEntityManager_destroyArray(nativePtr, entities.size.toULong(), pinned.addressOf(0).reinterpret())
        }
    }

    actual fun isAlive(@Entity entity: Int): Boolean {
        return FilaEntityManager_isAlive(nativePtr, entity.toUInt())
    }

    actual val nativeObject: Long
        get() = nativePtr?.rawValue?.toLong() ?: 0L

    actual companion object {
        private val instance by lazy { EntityManager(FilaEntityManager_get()) }
        actual fun get(): EntityManager {
            return instance
        }
    }
}
