package dev.filament.kmp

import com.google.android.filament.Scene as AndroidFilamentScene

actual class Scene internal constructor(
    internal var androidScene: AndroidFilamentScene?,
) {
    actual val isValid: Boolean
        get() = androidScene != null

    actual fun getSkybox(): Skybox? {
        val scene = requireNotNull(androidScene) { "Calling method on destroyed Scene" }
        val skybox = scene.skybox ?: return null
        return Skybox(skybox)
    }

    actual fun setSkybox(skybox: Skybox?) {
        val scene = requireNotNull(androidScene) { "Calling method on destroyed Scene" }
        scene.skybox = skybox?.androidSkybox
    }

    actual fun getIndirectLight(): IndirectLight? {
        val scene = requireNotNull(androidScene) { "Calling method on destroyed Scene" }
        val indirect = scene.indirectLight ?: return null
        return IndirectLight(indirect)
    }

    actual fun setIndirectLight(ibl: IndirectLight?) {
        val scene = requireNotNull(androidScene) { "Calling method on destroyed Scene" }
        scene.indirectLight = ibl?.androidIndirectLight
    }

    actual fun addEntity(@Entity entity: Int) {
        val scene = requireNotNull(androidScene) { "Calling method on destroyed Scene" }
        scene.addEntity(entity)
    }

    actual fun addEntities(@Entity entities: IntArray) {
        val scene = requireNotNull(androidScene) { "Calling method on destroyed Scene" }
        scene.addEntities(entities)
    }

    actual fun removeEntity(@Entity entity: Int) {
        val scene = requireNotNull(androidScene) { "Calling method on destroyed Scene" }
        scene.removeEntity(entity)
    }

    actual fun remove(@Entity entity: Int) {
        val scene = requireNotNull(androidScene) { "Calling method on destroyed Scene" }
        scene.removeEntity(entity)
    }

    actual fun removeEntities(@Entity entities: IntArray) {
        val scene = requireNotNull(androidScene) { "Calling method on destroyed Scene" }
        scene.removeEntities(entities)
    }

    actual fun getEntityCount(): Int {
        val scene = requireNotNull(androidScene) { "Calling method on destroyed Scene" }
        return scene.entityCount
    }

    actual fun getRenderableCount(): Int {
        val scene = requireNotNull(androidScene) { "Calling method on destroyed Scene" }
        return scene.renderableCount
    }

    actual fun getLightCount(): Int {
        val scene = requireNotNull(androidScene) { "Calling method on destroyed Scene" }
        return scene.lightCount
    }

    actual fun hasEntity(@Entity entity: Int): Boolean {
        val scene = requireNotNull(androidScene) { "Calling method on destroyed Scene" }
        return scene.hasEntity(entity)
    }

    actual fun getEntities(outArray: IntArray?): IntArray {
        val scene = requireNotNull(androidScene) { "Calling method on destroyed Scene" }
        return scene.getEntities(outArray)
    }

    actual fun getEntities(): IntArray {
        val scene = requireNotNull(androidScene) { "Calling method on destroyed Scene" }
        return scene.entities
    }

    actual fun forEach(entityProcessor: Scene.EntityProcessor) {
        val scene = requireNotNull(androidScene) { "Calling method on destroyed Scene" }
        scene.forEach { entity -> entityProcessor.process(entity) }
    }

    actual fun getNativeObject(): Long {
        val scene = requireNotNull(androidScene) { "Calling method on destroyed Scene" }
        return scene.nativeObject
    }

    actual internal fun invalidate() {
        androidScene = null
    }

    actual interface EntityProcessor {
        actual fun process(@Entity entity: Int)
    }
}

