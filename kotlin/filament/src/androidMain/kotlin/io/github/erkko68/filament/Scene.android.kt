package io.github.erkko68.filament

actual class Scene internal constructor(val nativeScene: com.google.android.filament.Scene) {
    private var _skybox: Skybox? = null
    private var _indirectLight: IndirectLight? = null

    actual var skybox: Skybox?
        get() = _skybox
        set(value) {
            _skybox = value
            nativeScene.skybox = value?.nativeSkybox
        }

    actual var indirectLight: IndirectLight?
        get() = _indirectLight
        set(value) {
            _indirectLight = value
            nativeScene.indirectLight = value?.nativeIndirectLight
        }

    actual fun addEntity(entity: Entity) = nativeScene.addEntity(entity)
    actual fun addEntities(entities: IntArray) = nativeScene.addEntities(entities)

    actual fun removeEntity(entity: Entity) = nativeScene.removeEntity(entity)
    actual fun remove(entity: Entity) = nativeScene.removeEntity(entity)
    actual fun removeEntities(entities: IntArray) = nativeScene.removeEntities(entities)

    actual val entityCount: Int get() = nativeScene.entityCount
    actual val renderableCount: Int get() = nativeScene.renderableCount
    actual val lightCount: Int get() = nativeScene.lightCount
    actual fun hasEntity(entity: Entity): Boolean = nativeScene.hasEntity(entity)

    actual fun getEntities(): IntArray = nativeScene.getEntities()
    actual fun getEntities(outArray: IntArray?): IntArray = nativeScene.getEntities(outArray)

    actual fun forEach(block: (Entity) -> Unit) {
        nativeScene.getEntities(null).forEach(block)
    }
}
