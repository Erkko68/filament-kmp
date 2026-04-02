package dev.filament.kmp

actual class EntityManager {
    @Entity
    actual fun create(): Int {
        TODO("Not yet implemented")
    }

    actual fun destroy(@Entity entity: Int) {
        TODO("Not yet implemented")
    }

    @Entity
    actual fun create(n: Int): IntArray {
        TODO("Not yet implemented")
    }

    actual fun create(@Entity entities: IntArray): IntArray {
        TODO("Not yet implemented")
    }

    actual fun destroy(@Entity entities: IntArray) {
        TODO("Not yet implemented")
    }

    actual fun isAlive(@Entity entity: Int): Boolean {
        TODO("Not yet implemented")
    }

    actual fun getNativeObject(): Long {
        TODO("Not yet implemented")
    }

    actual companion object {
        actual fun get(): EntityManager {
            TODO("Not yet implemented")
        }
    }
}

