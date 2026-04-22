package io.github.erkko68.filament

import io.github.erkko68.filament.jni.EntityManager as JniEntityManager

actual class EntityManager(val nativeEntityManager: JniEntityManager) {
    actual companion object {
        actual fun get(): EntityManager = EntityManager(JniEntityManager.get())
    }

    actual fun create(): Int = nativeEntityManager.create()
    
    actual fun create(n: Int): IntArray {
        val entities = IntArray(n)
        for (i in 0 until n) {
            entities[i] = nativeEntityManager.create()
        }
        return entities
    }

    actual fun create(entities: IntArray): IntArray {
        for (i in entities.indices) {
            entities[i] = nativeEntityManager.create()
        }
        return entities
    }

    actual fun destroy(entity: Int) = nativeEntityManager.destroy(entity)
    
    actual fun destroy(entities: IntArray) {
        for (entity in entities) {
            nativeEntityManager.destroy(entity)
        }
    }

    actual fun isAlive(entity: Int): Boolean = nativeEntityManager.isAlive(entity)
}
