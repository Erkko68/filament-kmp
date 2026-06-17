package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine

/**
 * ResourceLoader loads external resources referenced by glTF assets.
 *
 * ResourceLoader handles:
 * - Loading textures from URIs
 * - Uploading vertex/index buffer data
 * - Computing tangent quaternions (via Mikktspace when needed)
 * - Async resource loading for progressive rendering
 *
 * **Typical usage:**
 * ```
 * val resourceLoader = ResourceLoader(engine)
 * resourceLoader.loadResources(asset)  // Synchronously load all resources
 * resourceLoader.destroy()
 * ```
 *
 * **Async usage:**
 * ```
 * resourceLoader.asyncBeginLoad(asset)
 * while (resourceLoader.asyncGetLoadProgress() < 1.0f) {
 *     resourceLoader.asyncUpdateLoad()  // Load in chunks
 * }
 * ```
 *
 * @see FilamentAsset
 * @see AssetLoader
 */
expect class ResourceLoader {
    /**
     * Create a ResourceLoader.
     *
     * @param engine Filament Engine to use for loading resources.
     * @param normalizeSkinningWeights Whether to normalize skinning weights to [0, 1] range.
     */
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
