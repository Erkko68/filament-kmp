package io.github.erkko68.filament

actual class Scene internal constructor(val nativeScene: com.google.android.filament.Scene) {
    private var skybox: Skybox? = null
    private var indirectLight: IndirectLight? = null

    actual fun setSkybox(skybox: Skybox?) {
        this.skybox = skybox
        nativeScene.skybox = skybox?.nativeSkybox
    }
    actual fun getSkybox(): Skybox? = skybox

    actual fun setIndirectLight(ibl: IndirectLight?) {
        this.indirectLight = ibl
        nativeScene.indirectLight = ibl?.nativeIndirectLight
    }
    actual fun getIndirectLight(): IndirectLight? = indirectLight

    actual fun addEntity(entity: Int) = nativeScene.addEntity(entity)
    actual fun addEntities(entities: IntArray) = nativeScene.addEntities(entities)

    actual fun removeEntity(entity: Int) = nativeScene.removeEntity(entity)
    actual fun removeEntities(entities: IntArray) = nativeScene.removeEntities(entities)

    actual fun getEntityCount(): Int = nativeScene.entityCount
    actual fun getRenderableCount(): Int = nativeScene.renderableCount
    actual fun getLightCount(): Int = nativeScene.lightCount
    actual fun hasEntity(entity: Int): Boolean = nativeScene.hasEntity(entity)

    actual fun getEntities(): IntArray = nativeScene.getEntities()
    actual fun getEntities(outArray: IntArray?): IntArray = nativeScene.getEntities(outArray)

    actual fun forEach(block: (Int) -> Unit) {
        nativeScene.getEntities(null).forEach(block)
    }
}
