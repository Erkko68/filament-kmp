package dev.filament.kmp

expect class EntityManager {
    @Entity
    fun create(): Int

    fun destroy(@Entity entity: Int)

    @Entity
    fun create(n: Int): IntArray

    fun create(@Entity entities: IntArray): IntArray

    fun destroy(@Entity entities: IntArray)

    fun isAlive(@Entity entity: Int): Boolean

    fun getNativeObject(): Long

    companion object {
        fun get(): EntityManager
    }
}

