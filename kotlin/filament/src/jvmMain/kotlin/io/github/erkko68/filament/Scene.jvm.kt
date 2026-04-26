package io.github.erkko68.filament

import io.github.erkko68.filament.jni.Scene as JniScene

actual class Scene(val nativeScene: JniScene) {
    actual fun addEntity(entity: Entity) : Unit { nativeScene.addEntity(entity) }
    actual fun addEntities(entities: IntArray) : Unit { nativeScene.addEntities(entities) }
    actual fun removeEntity(entity: Entity) : Unit { nativeScene.removeEntity(entity) }
    actual fun remove(entity: Entity) : Unit { nativeScene.removeEntity(entity) }
    actual fun removeEntities(entities: IntArray) : Unit { nativeScene.removeEntities(entities) }
    
    actual fun getEntityCount(): Int = nativeScene.getEntityCount()
    actual fun getRenderableCount(): Int = nativeScene.getRenderableCount()
    actual fun getLightCount(): Int = nativeScene.getLightCount()
    actual fun hasEntity(entity: Entity): Boolean = nativeScene.hasEntity(entity)

    actual fun getEntities(): IntArray = nativeScene.getEntities()
    actual fun getEntities(outArray: IntArray?): IntArray = nativeScene.getEntities(outArray)

    actual fun forEach(block: (Entity) -> Unit) {
        nativeScene.forEach { entity -> block(entity) }
    }

    actual fun getSkybox(): Skybox? = nativeScene.getSkybox()?.let { Skybox(it) }
    actual fun setSkybox(skybox: Skybox?) : Unit { nativeScene.setSkybox(skybox?.nativeSkybox) }
    
    actual fun getIndirectLight(): IndirectLight? = nativeScene.getIndirectLight()?.let { IndirectLight(it) }
    actual fun setIndirectLight(ibl: IndirectLight?) : Unit { nativeScene.setIndirectLight(ibl?.nativeIndirectLight) }
}
