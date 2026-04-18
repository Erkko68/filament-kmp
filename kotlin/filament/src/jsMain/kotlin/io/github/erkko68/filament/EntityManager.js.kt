package io.github.erkko68.filament

import io.github.erkko68.filament.js.EntityManager as JSEntityManager

actual class EntityManager(internal val jsEntityManager: JSEntityManager) {
    actual fun create(): Entity {
        return jsEntityManager.create().getId().toInt()
    }

    actual fun create(n: Int): IntArray {
        return IntArray(n) { jsEntityManager.create().getId().toInt() }
    }

    actual fun create(entities: IntArray): IntArray {
        for (i in entities.indices) {
            entities[i] = jsEntityManager.create().getId().toInt()
        }
        return entities
    }

    actual fun destroy(entity: Entity) {
    }

    actual fun destroy(entities: IntArray) {
    }

    actual fun isAlive(entity: Entity): Boolean {
        // JS EntityManager doesn't have isAlive in the bindings, but usually we can assume true if not destroyed
        return true
    }

    actual companion object {
        actual fun get(): EntityManager {
            return EntityManager(JSEntityManager.get())
        }
    }
}