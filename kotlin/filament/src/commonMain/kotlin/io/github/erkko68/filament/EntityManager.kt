package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
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
        private val instance = Filament.init().let { EntityManager(FilaUtilsEntityManager_get()) }

        /**
         * Get the global EntityManager instance.
         *
         * It is recommended to cache this value.
         *
         * @return The global EntityManager instance (thread-safe).
         */
        fun get(): EntityManager = instance

        /** The maximum number of entities that can exist at the same time. */
        val maxEntityCount: Int get() = FilaUtilsEntityManager_getMaxEntityCount()

        /** Returns the index of [e], for use as an array index. */
        fun getIndex(e: Entity): Int = FilaUtilsEntityManager_getIndex(e)
    }

    /** The number of entities currently alive. */
    val entityCount: Int get() = FilaUtilsEntityManager_getEntityCount(nativeHandle)

    /**
     * Create a new Entity.
     *
     * @return A new Entity ID, or NULL_ENTITY if allocation fails.
     */
    fun create(): Entity = FilaUtilsEntityManager_create(nativeHandle)

    /**
     * Creates `entities.size` entities into [entities]. Thread safe.
     *
     * @param entities The array to populate with newly created Entity IDs.
     */
    fun create(entities: IntArray) = entities.usePinned { FilaUtilsEntityManager_create_size_t_Entity(nativeHandle, entities.size, it) }

    /**
     * Destroy an entity. The entity ID becomes invalid and should not be used after destruction.
     *
     * @param e The entity to destroy.
     */
    fun destroy(e: Entity) = FilaUtilsEntityManager_destroy_Entity(nativeHandle, e)

    /**
     * Destroys every entity in [entities].
     *
     * @param entities Array of entities to destroy.
     */
    fun destroy(entities: IntArray) = entities.usePinned { FilaUtilsEntityManager_destroy_size_t_Entity(nativeHandle, entities.size, it) }

    /**
     * Check whether an entity is alive (not destroyed).
     *
     * @param e The entity to check.
     * @return true if the entity is alive, false if it has been destroyed.
     */
    fun isAlive(e: Entity): Boolean = FilaUtilsEntityManager_isAlive(nativeHandle, e)

    /** Flushes pending entity lifecycle notifications to registered listeners. */
    fun flushNotifications() = FilaUtilsEntityManager_flushNotifications(nativeHandle)

    /** Seals the current epoch and starts a new one, recording later destructions there; recycles safe epochs. */
    fun advanceEpoch() = FilaUtilsEntityManager_advanceEpoch(nativeHandle)

    /** Recycles the indices of epochs that are safe to reclaim; [advanceEpoch] already does this, so it is for tests. */
    fun reclaimSafeEpochs() = FilaUtilsEntityManager_reclaimSafeEpochs(nativeHandle)

    /** The current epoch's ID. */
    val latestEpochID: Long get() = LongArray(1).also { out -> out.usePinned { FilaUtilsEntityManager_getLatestEpochID(nativeHandle, it) } }[0]
}
