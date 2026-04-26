package io.github.erkko68.filament

expect class Scene {
    fun setSkybox(skybox: Skybox?)
    fun getSkybox(): Skybox?
    fun setIndirectLight(ibl: IndirectLight?)
    fun getIndirectLight(): IndirectLight?
    
    fun addEntity(entity: Entity)
    fun addEntities(entities: IntArray)
    
    fun removeEntity(entity: Entity)
    fun remove(entity: Entity)
    fun removeEntities(entities: IntArray)
    
    fun getEntityCount(): Int
    fun getRenderableCount(): Int
    fun getLightCount(): Int
    fun hasEntity(entity: Entity): Boolean
    
    fun getEntities(): IntArray
    fun getEntities(outArray: IntArray?): IntArray
    
    fun forEach(block: (Entity) -> Unit)
}
