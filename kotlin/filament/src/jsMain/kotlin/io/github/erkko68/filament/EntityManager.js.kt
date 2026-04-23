package io.github.erkko68.filament

import io.github.erkko68.filament.js.Entity as JSEntity
import io.github.erkko68.filament.js.EntityManager as JSEntityManager

actual class EntityManager(internal val jsEntityManager: JSEntityManager) {
    actual fun create(): Entity {
        val jsEntity = jsEntityManager.create()
        val id = jsEntity.getId().toInt()
        registry[id] = jsEntity
        return id
    }

    actual fun create(n: Int): IntArray = IntArray(n) { create() }

    actual fun create(entities: IntArray): IntArray {
        for (i in entities.indices) entities[i] = create()
        return entities
    }

    actual fun destroy(entity: Entity) {
        registry.remove(entity)
    }

    actual fun destroy(entities: IntArray) {
        for (e in entities) registry.remove(e)
    }

    actual fun isAlive(entity: Entity): Boolean = registry.containsKey(entity)

    actual companion object {
        private val registry = HashMap<Int, JSEntity>()

        fun register(id: Int, jsEntity: JSEntity) {
            registry[id] = jsEntity
        }

        fun jsEntityOf(id: Int): JSEntity =
            registry[id] ?: error("No JS Entity wrapper for id $id — was it created through EntityManager?")

        actual fun get(): EntityManager = EntityManager(JSEntityManager.get())
    }
}