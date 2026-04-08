package dev.filament.kmp

expect class Scene {
    fun setSkybox(skybox: Skybox?)
    fun getSkybox(): Skybox?
    fun setIndirectLight(ibl: IndirectLight?)
    fun getIndirectLight(): IndirectLight?
    
    fun addEntity(entity: Int)
    fun addEntities(entities: IntArray)
    
    fun removeEntity(entity: Int)
    fun removeEntities(entities: IntArray)
    
    fun getEntityCount(): Int
    fun getRenderableCount(): Int
    fun getLightCount(): Int
    fun hasEntity(entity: Int): Boolean
    
    fun getEntities(): IntArray
    fun getEntities(outArray: IntArray?): IntArray
    
    fun forEach(block: (Int) -> Unit)
}
