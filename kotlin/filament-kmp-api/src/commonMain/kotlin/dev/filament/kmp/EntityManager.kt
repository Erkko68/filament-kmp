package dev.filament.kmp

/**
 * EntityManager is used to create and destroy entities.
 */
expect class EntityManager {
    companion object {
        fun get(): EntityManager
    }

    fun create(): Int
    fun create(n: Int): IntArray
    fun create(entities: IntArray): IntArray
    fun destroy(entity: Int)
    fun destroy(entities: IntArray)
    fun isAlive(entity: Int): Boolean
}
