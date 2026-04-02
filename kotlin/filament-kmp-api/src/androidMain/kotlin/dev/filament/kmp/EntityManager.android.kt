package dev.filament.kmp

import com.google.android.filament.EntityManager as AndroidEntityManager

actual class EntityManager internal constructor(
    internal val androidEntityManager: AndroidEntityManager,
) {
    @Entity
    actual fun create(): Int = androidEntityManager.create()

    actual fun destroy(@Entity entity: Int) {
        androidEntityManager.destroy(entity)
    }

    @Entity
    actual fun create(n: Int): IntArray = androidEntityManager.create(n)

    actual fun create(@Entity entities: IntArray): IntArray = androidEntityManager.create(entities)

    actual fun destroy(@Entity entities: IntArray) {
        androidEntityManager.destroy(entities)
    }

    actual fun isAlive(@Entity entity: Int): Boolean = androidEntityManager.isAlive(entity)

    actual fun getNativeObject(): Long = androidEntityManager.nativeObject

    actual companion object {
        actual fun get(): EntityManager = EntityManager(AndroidEntityManager.get())
    }
}

