package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine

expect class ResourceLoader {
    constructor(engine: Engine, normalizeSkinningWeights: Boolean = false)
    
    fun destroy()
    
    fun addResourceData(url: String, data: ByteArray)
    fun hasResourceData(url: String): Boolean
    fun loadResources(asset: FilamentAsset): Boolean
    
    fun asyncBeginLoad(asset: FilamentAsset): Boolean
    fun asyncGetLoadProgress(): Float
    fun asyncUpdateLoad()
    fun asyncCancelLoad()
    
    fun evictResourceData()
}
