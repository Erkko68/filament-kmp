package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.EntityManager

expect class AssetLoader {
    companion object {
        fun create(engine: Engine, materials: MaterialProvider, entities: EntityManager? = null): AssetLoader
        fun destroy(loader: AssetLoader)
    }

    fun createAsset(buffer: ByteArray): FilamentAsset?
    fun createInstancedAsset(buffer: ByteArray, instances: Array<FilamentInstance>): FilamentAsset?
    fun createInstance(asset: FilamentAsset): FilamentInstance?
    
    fun enableDiagnostics(enable: Boolean)
    
    fun destroyAsset(asset: FilamentAsset)
}
