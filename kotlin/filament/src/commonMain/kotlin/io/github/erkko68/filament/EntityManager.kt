package io.github.erkko68.filament

import io.github.erkko68.filament.interop.*

/**
 * Manages entity creation, destruction, and lifecycle.
 *
 * EntityManager is responsible for allocating and releasing entity IDs, which are
 * opaque 32-bit handles used throughout Filament to refer to scene objects and their
 * components. It is recommended to cache the EntityManager instance.
 *
 * Thread Safe: All operations on EntityManager are thread-safe.
 */
class EntityManager @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    companion object {
        private val instance = EntityManager(FilaEntityManager_get())
        /**
         * Get the global EntityManager instance.
         *
         * It is recommended to cache this value.
         *
         * @return The global EntityManager instance (thread-safe).
         */
        fun get(): EntityManager = instance
    }

    /**
     * Create a new Entity.
     *
     * Returns a new unique entity ID. If the entity cannot be allocated (max entity
     * count reached), the result will be a null entity.
     *
     * @return A new Entity ID, or NULL_ENTITY if allocation fails.
     */
    fun create(): Entity = FilaEntityManager_create(nativeHandle)
    
    /**
     * Create multiple entities.
     *
     * Creates n new entities in a batch. This is more efficient than calling
     * create() multiple times.
     *
     * @param n The number of entities to create.
     * @return An array of n newly created Entity IDs.
     */
    fun create(n: Int): IntArray {
        val result = IntArray(n)
        result.usePinned { 
            FilaEntityManager_createArray(nativeHandle, n, it)
        }
        return result
    }
    
    /**
     * Create entities into an existing array.
     *
     * Creates entities and stores them in the provided array. The array size
     * determines how many entities are created.
     *
     * @param entities The array to populate with newly created Entity IDs.
     * @return The same array that was passed in, now populated with new entities.
     */
    fun create(entities: IntArray): IntArray {
        entities.usePinned { 
            FilaEntityManager_createArray(nativeHandle, entities.size, it)
        }
        return entities
    }

    /**
     * Destroy an entity.
     *
     * Releases the entity ID and marks it as destroyed. The entity ID becomes
     * invalid and should not be used after destruction.
     *
     * @param entity The entity to destroy.
     */
    fun destroy(entity: Entity) = FilaEntityManager_destroy(nativeHandle, entity)
    
    /**
     * Destroy multiple entities.
     *
     * Destroys a batch of entities. This is more efficient than calling
     * destroy() multiple times.
     *
     * @param entities Array of entities to destroy.
     */
    fun destroy(entities: IntArray) {
        entities.usePinned { 
            FilaEntityManager_destroyArray(nativeHandle, entities.size, it)
        }
    }

    /**
     * Check whether an entity is alive (not destroyed).
     *
     * Returns whether the given entity has been destroyed (false) or is still
     * valid (true).
     *
     * @param entity The entity to check.
     * @return true if the entity is alive, false if it has been destroyed.
     */
    fun isAlive(entity: Entity): Boolean = FilaEntityManager_isAlive(nativeHandle, entity)

    /**
     * Advance the entity manager epoch, invalidating all currently active entity IDs.
     */
    fun advanceEpoch() {
        FilaEntityManager_advanceEpoch(nativeHandle)
    }

    /**
     * Get the maximum number of entities that can be created.
     */
    val maxEntityCount: Int get() = FilaEntityManager_getMaxEntityCount(nativeHandle).toInt()
}

@ExternalSymbolName("FilaEntityManager_advanceEpoch")
private external fun FilaEntityManager_advanceEpoch(em: NativePointer)

@ExternalSymbolName("FilaEntityManager_create")
private external fun FilaEntityManager_create(em: NativePointer): Int

@ExternalSymbolName("FilaEntityManager_createArray")
private external fun FilaEntityManager_createArray(em: NativePointer, n: Int, outEntities: NativePointer)

@ExternalSymbolName("FilaEntityManager_destroy")
private external fun FilaEntityManager_destroy(em: NativePointer, entity: Int)

@ExternalSymbolName("FilaEntityManager_destroyArray")
private external fun FilaEntityManager_destroyArray(em: NativePointer, n: Int, entities: NativePointer)

@ExternalSymbolName("FilaEntityManager_get")
private external fun FilaEntityManager_get(): NativePointer

@ExternalSymbolName("FilaEntityManager_getMaxEntityCount")
private external fun FilaEntityManager_getMaxEntityCount(em: NativePointer): Int

@ExternalSymbolName("FilaEntityManager_isAlive")
private external fun FilaEntityManager_isAlive(em: NativePointer, entity: Int): Boolean
