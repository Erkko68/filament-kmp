package io.github.erkko68.filament

actual class EntityManager {
    actual fun create(): Entity {
        TODO("Not yet implemented")
    }

    actual fun create(n: Int): IntArray {
        TODO("Not yet implemented")
    }

    actual fun create(entities: IntArray): IntArray {
        TODO("Not yet implemented")
    }

    actual fun destroy(entity: Entity) {
    }

    actual fun destroy(entities: IntArray) {
    }

    actual fun isAlive(entity: Entity): Boolean {
        TODO("Not yet implemented")
    }

    actual companion object {
        actual fun get(): EntityManager {
            TODO("Not yet implemented")
        }
    }
}