package io.github.erkko68.filament

import io.github.erkko68.filament.js.Scene as JSScene

actual class Scene(internal val jsScene: JSScene) {
    private var _skybox: Skybox? = null
    private var _indirectLight: IndirectLight? = null
    private val _entities = mutableSetOf<Int>()

    actual fun setSkybox(skybox: Skybox?) {
        _skybox = skybox
        jsScene.setSkybox(skybox?.jsSkybox)
    }

    actual fun getSkybox(): Skybox? {
        return _skybox
    }

    actual fun setIndirectLight(ibl: IndirectLight?) {
        _indirectLight = ibl
        jsScene.setIndirectLight(ibl?.jsIndirectLight)
    }

    actual fun getIndirectLight(): IndirectLight? {
        return _indirectLight
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

    actual fun getEntityCount(): Int {
        return _entities.size
    }

    actual fun getRenderableCount(): Int {
        return jsScene.getRenderableCount().toInt()
    }

    actual fun getLightCount(): Int {
        return jsScene.getLightCount().toInt()
    }

    actual fun hasEntity(entity: Entity): Boolean {
        return _entities.contains(entity)
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