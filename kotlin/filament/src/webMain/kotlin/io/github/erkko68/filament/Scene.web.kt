package io.github.erkko68.filament

import io.github.erkko68.filament.web.interop.toJsArray

import io.github.erkko68.filament.web.Scene as JSScene

actual class Scene @InternalFilamentApi constructor(internal val jsScene: JSScene) {
    // Cached for wrapper identity, as on the other platforms; the engine-side getters back
    // the case where the scene was populated outside this wrapper.
    private var _skybox: Skybox? = null
    private var _indirectLight: IndirectLight? = null

    // Scene has no getEntities() in C++ at all — only forEach(Invocable), which embind
    // cannot bind — so membership is mirrored here to back getEntities()/forEach().
    private val _entities = mutableSetOf<Int>()

    actual var skybox: Skybox?
        get() = _skybox ?: jsScene.getSkybox()?.let { Skybox(it) }
        set(value) {
            _skybox = value
            jsScene.setSkybox(value?.jsSkybox)
        }

    actual var indirectLight: IndirectLight?
        get() = _indirectLight ?: jsScene.getIndirectLight()?.let { IndirectLight(it) }
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
        if (toAdd.isNotEmpty()) jsScene.addEntities(toAdd.toJsArray())
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
        if (toRemove.isNotEmpty()) jsScene.removeEntities(toRemove.toJsArray())
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