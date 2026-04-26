package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Entity

expect class FilamentAsset {
    fun getRoot(): Entity
    fun popRenderable(): Entity
    fun popRenderables(entities: IntArray): Int
    
    fun getEntities(): IntArray
    fun getLightEntities(): IntArray
    fun getRenderableEntities(): IntArray
    fun getCameraEntities(): IntArray
    
    fun getEntitiesByName(name: String): IntArray
    fun getEntitiesByPrefix(prefix: String): IntArray
    fun getFirstEntityByName(name: String): Entity
    
    fun getEntityCount(): Int
    fun getAssetInstanceCount(): Int
    fun getAssetInstances(): Array<FilamentInstance>
    
    fun getBoundingBox(): Box
    fun getName(entity: Entity): String?
    fun getExtras(entity: Entity): String?
    fun getMorphTargetNames(entity: Entity): Array<String>
    
    fun getResourceUris(): Array<String>
    
    fun releaseSourceData()

    fun getEngine(): Engine
    fun getInstance(): FilamentInstance
}
