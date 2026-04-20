package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.Engine

expect class FilamentAsset {
    fun getRoot(): Int
    fun popRenderable(): Int
    fun popRenderables(entities: IntArray): Int
    
    fun getEntities(): IntArray
    fun getLightEntities(): IntArray
    fun getRenderableEntities(): IntArray
    fun getCameraEntities(): IntArray
    
    fun getEntitiesByName(name: String): IntArray
    fun getEntitiesByPrefix(prefix: String): IntArray
    fun getFirstEntityByName(name: String): Int
    
    fun getEntityCount(): Int
    fun getAssetInstanceCount(): Int
    fun getAssetInstances(): Array<FilamentInstance>
    
    fun getBoundingBox(): Box
    fun getName(entity: Int): String?
    fun getExtras(entity: Int): String?
    fun getMorphTargetNames(entity: Int): Array<String>
    
    fun getResourceUris(): Array<String>
    
    fun releaseSourceData()

    fun getEngine(): Engine
    fun getInstance(): FilamentInstance
}
