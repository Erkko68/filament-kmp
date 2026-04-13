package io.github.erkko68.filament

import com.google.android.filament.EntityManager as AndroidEntityManager

actual class EntityManager internal constructor(val nativeEntityManager: AndroidEntityManager) {
    actual companion object {
        private val instance = EntityManager(AndroidEntityManager.get())
        actual fun get(): EntityManager = instance
    }

    actual fun create(): Entity = nativeEntityManager.create()
    
    actual fun create(n: Int): IntArray = nativeEntityManager.create(n)
    
    actual fun create(entities: IntArray): IntArray = nativeEntityManager.create(entities)

    actual fun destroy(entity: Entity) = nativeEntityManager.destroy(entity)
    
    actual fun destroy(entities: IntArray) = nativeEntityManager.destroy(entities)

    actual fun isAlive(entity: Entity): Boolean = nativeEntityManager.isAlive(entity)
}
