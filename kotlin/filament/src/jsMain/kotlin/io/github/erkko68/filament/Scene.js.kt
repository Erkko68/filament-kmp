package io.github.erkko68.filament

import io.github.erkko68.filament.js.Scene as JSScene
import io.github.erkko68.filament.js.Entity as JSEntity

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

    actual fun addEntity(entity: Int) {
        if (_entities.add(entity)) {
            jsScene.addEntity(entity.unsafeCast<JSEntity>())
        }
    }

    actual fun addEntities(entities: IntArray) {
        val toAdd = mutableListOf<JSEntity>()
        for (entity in entities) {
            if (_entities.add(entity)) {
                toAdd.add(entity.unsafeCast<JSEntity>())
            }
        }
        if (toAdd.isNotEmpty()) {
            jsScene.addEntities(toAdd.toTypedArray())
        }
    }

    actual fun removeEntity(entity: Int) {
        if (_entities.remove(entity)) {
            jsScene.remove(entity.unsafeCast<JSEntity>())
        }
    }

    actual fun remove(entity: Int) {
        removeEntity(entity)
    }

    actual fun removeEntities(entities: IntArray) {
        val toRemove = mutableListOf<JSEntity>()
        for (entity in entities) {
            if (_entities.remove(entity)) {
                toRemove.add(entity.unsafeCast<JSEntity>())
            }
        }
        if (toRemove.isNotEmpty()) {
            jsScene.removeEntities(toRemove.toTypedArray())
        }
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

    actual fun hasEntity(entity: Int): Boolean {
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

    actual fun forEach(block: (Int) -> Unit) {
        _entities.forEach(block)
    }
}