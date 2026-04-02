package dev.filament.kmp

expect class EntityManager {
    @Entity
    /**
     * Creates a new entity.
     */
    fun create(): Int

    /**
     * Destroys an entity.
     */
    fun destroy(@Entity entity: Int)

    @Entity
    /**
     * Creates an array of entities.
     */
    fun create(n: Int): IntArray

    /**
     * Fills the given array with newly created entities.
     */
    fun create(@Entity entities: IntArray): IntArray

    /**
     * Destroys an array of entities.
     */
    fun destroy(@Entity entities: IntArray)

    /**
     * Returns whether an entity is alive.
     */
    fun isAlive(@Entity entity: Int): Boolean

    /**
     * Returns the native object handle.
     */
    fun getNativeObject(): Long

    companion object {
        /**
         * Returns the singleton EntityManager.
         */
        fun get(): EntityManager
    }
}

