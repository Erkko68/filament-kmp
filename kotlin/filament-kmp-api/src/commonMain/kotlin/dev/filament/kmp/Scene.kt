package dev.filament.kmp

expect class Scene {
    fun setSkybox(skybox: Skybox?)
    fun getSkybox(): Skybox?
    fun setIndirectLight(ibl: IndirectLight?)
    fun getIndirectLight(): IndirectLight?
    fun addEntity(entity: Int)
    fun removeEntity(entity: Int)
    fun getEntityCount(): Int
    fun getRenderableCount(): Int
    fun getLightCount(): Int
    fun hasEntity(entity: Int): Boolean
}
