package io.github.erkko68.filament

import io.github.erkko68.filament.jni.Scene as JniScene

actual class Scene(val nativeScene: JniScene) {
    actual fun addEntity(entity: Entity) : Unit { nativeScene.addEntity(entity) }
    actual fun addEntities(entities: IntArray) : Unit { nativeScene.addEntities(entities) }
    actual fun removeEntity(entity: Entity) : Unit { nativeScene.removeEntity(entity) }
    actual fun remove(entity: Entity) : Unit { nativeScene.removeEntity(entity) }
    actual fun removeEntities(entities: IntArray) : Unit { nativeScene.removeEntities(entities) }
    
    actual val entityCount: Int get() = nativeScene.getEntityCount()
    actual val renderableCount: Int get() = nativeScene.getRenderableCount()
    actual val lightCount: Int get() = nativeScene.getLightCount()
    actual fun hasEntity(entity: Entity): Boolean = nativeScene.hasEntity(entity)

    actual fun getEntities(): IntArray = nativeScene.getEntities()
    actual fun getEntities(outArray: IntArray?): IntArray = nativeScene.getEntities(outArray)

    actual fun forEach(block: (Entity) -> Unit) {
        nativeScene.forEach { entity -> block(entity) }
    }

    actual var skybox: Skybox?
        get() = nativeScene.getSkybox()?.let { Skybox(it) }
        set(value) { nativeScene.setSkybox(value?.nativeSkybox) }
    
    actual var indirectLight: IndirectLight?
        get() = nativeScene.getIndirectLight()?.let { IndirectLight(it) }
        set(value) { nativeScene.setIndirectLight(value?.nativeIndirectLight) }
}
