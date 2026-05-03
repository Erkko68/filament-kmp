package io.github.erkko68.filament

expect class Scene {
    var skybox: Skybox?
    var indirectLight: IndirectLight?

    fun addEntity(entity: Entity)
    fun addEntities(entities: IntArray)

    fun removeEntity(entity: Entity)
    fun remove(entity: Entity)
    fun removeEntities(entities: IntArray)

    val entityCount: Int
    val renderableCount: Int
    val lightCount: Int
    fun hasEntity(entity: Entity): Boolean

    fun getEntities(): IntArray
    fun getEntities(outArray: IntArray?): IntArray

    fun forEach(block: (Entity) -> Unit)
}
