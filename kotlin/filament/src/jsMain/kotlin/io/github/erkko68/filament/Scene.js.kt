package io.github.erkko68.filament

import io.github.erkko68.filament.js.Scene as JSScene

actual class Scene(internal val jsScene: JSScene) {
    private var _skybox: Skybox? = null
    private var _indirectLight: IndirectLight? = null
    private val _entities = mutableSetOf<Int>()

    actual var skybox: Skybox?
        get() = _skybox
        set(value) {
            _skybox = value
            jsScene.setSkybox(value?.jsSkybox)
        }

    actual var indirectLight: IndirectLight?
        get() = _indirectLight
        set(value) {
            _indirectLight = value
            jsScene.setIndirectLight(value?.jsIndirectLight)
        }

    actual fun addEntity(entity: Entity) {
        if (_entities.add(entity)) {
            jsScene.addEntity(EntityManager.jsEntityOf(entity))
        }
    }

    actual fun addEntities(entities: IntArray) {
        val toAdd = entities.filter { _entities.add(it) }
            .map { EntityManager.jsEntityOf(it) }
            .toTypedArray()
        if (toAdd.isNotEmpty()) jsScene.addEntities(toAdd)
    }

    actual fun removeEntity(entity: Entity) {
        if (_entities.remove(entity)) {
            jsScene.remove(EntityManager.jsEntityOf(entity))
        }
    }

    actual fun remove(entity: Entity) {
        removeEntity(entity)
    }

    actual fun removeEntities(entities: IntArray) {
        val toRemove = entities.filter { _entities.remove(it) }
            .map { EntityManager.jsEntityOf(it) }
            .toTypedArray()
        if (toRemove.isNotEmpty()) jsScene.removeEntities(toRemove)
    }

    actual val entityCount: Int
        get() = jsScene.getEntityCount().toInt()

    actual val renderableCount: Int
        get() = jsScene.getRenderableCount().toInt()

    actual val lightCount: Int
        get() = jsScene.getLightCount().toInt()

    actual fun hasEntity(entity: Entity): Boolean {
        return jsScene.hasEntity(EntityManager.jsEntityOf(entity))
    }

    actual fun getEntities(): IntArray {
        return _entities.toIntArray()
    }

    actual fun getEntities(outArray: IntArray?): IntArray {
        val result = outArray ?: IntArray(_entities.size)
        _entities.toIntArray().copyInto(result)
        return result
    }

    actual fun forEach(block: (Entity) -> Unit) {
        _entities.forEach(block)
    }
}