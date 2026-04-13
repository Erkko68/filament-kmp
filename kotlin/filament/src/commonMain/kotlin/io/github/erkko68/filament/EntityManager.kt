package io.github.erkko68.filament

expect class EntityManager {
    companion object {
        fun get(): EntityManager
    }

    fun create(): Entity
    fun create(n: Int): IntArray
    fun create(entities: IntArray): IntArray
    
    fun destroy(entity: Entity)
    fun destroy(entities: IntArray)
    
    fun isAlive(entity: Entity): Boolean
}
