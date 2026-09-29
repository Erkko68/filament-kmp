package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.InternalFilamentApi
import io.github.erkko68.filament.gltfio.capi.*
import io.github.erkko68.filament.interop.*

/**
 * Construction parameters for a [ResourceLoader].
 *
 * @property engine The engine the loader passes to builder objects (e.g. Texture.Builder).
 * @property normalizeSkinningWeights Adjusts skinning weights to sum to 1. Well-formed glTF files
 * don't need this, but it's useful for robustness.
 */
class ResourceConfiguration(
    var engine: Engine,
    var normalizeSkinningWeights: Boolean = false,
)

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
 * val stb = createStbProvider(engine)
 * val resourceLoader = ResourceLoader(ResourceConfiguration(engine))
 * resourceLoader.addTextureProvider("image/png", stb)
 * resourceLoader.addTextureProvider("image/jpeg", stb)
 * resourceLoader.loadResources(asset)  // Synchronously load all resources
 * resourceLoader.destroy()
 * stb.destroy()
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
class ResourceLoader(config: ResourceConfiguration) : AutoCloseable {
    internal var nativeHandle: NativePointer = config.useNative { FilaGltfioResourceLoader_create(it) }

    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /** Replaces the configuration the loader was created with. */
    fun setConfiguration(config: ResourceConfiguration) = config.useNative { FilaGltfioResourceLoader_setConfiguration(nativeHandle, it) }

    /**
     * Destroys this loader and frees its internal caches. Must happen on the same thread that
     * calls `Renderer.render()`, because the loader listens to buffer callbacks to know when
     * CPU-side data blobs can be freed.
     */
    fun destroy() {
        if (nativeHandle != NullPointer) FilaGltfioResourceLoader_destroy(nativeHandle)
        nativeHandle = NullPointer
    }

    /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
    override fun close() = destroy()

    /**
     * Feeds the binary content of an external resource into the loader's URI cache.
     *
     * Every resource returned by [FilamentAsset.resourceUris] should be added before calling
     * [loadResources] or [asyncBeginLoad]. Self-contained GLB files typically need no calls.
     */
    fun addResourceData(uri: String, buffer: ByteArray) {
        // The loader keeps the buffer until it's evicted; Filament's release callback frees the copy then.
        val u = upload(buffer, buffer.size, null)
        uri.useCString { FilaGltfioResourceLoader_addResourceData(nativeHandle, it, u.ptr, u.size, u.callback, u.userData) }
    }

    /**
     * Registers [provider] to decode textures of [mimeType] (e.g. `image/png`). The loader doesn't
     * own it: destroy it after the loader.
     */
    fun addTextureProvider(mimeType: String, provider: TextureProvider) =
        mimeType.useCString { FilaGltfioResourceLoader_addTextureProvider(nativeHandle, it, provider.nativeObject) }

    /** Checks whether the given resource URI has already been added via [addResourceData]. */
    fun hasResourceData(uri: String): Boolean = uri.useCString { FilaGltfioResourceLoader_hasResourceData(nativeHandle, it) }

    /**
     * Synchronously loads resources for [asset] from the URI cache and finalizes the asset:
     * transforms vertex data if necessary, decodes images, and supplies tangent data.
     *
     * @return false if resources were already loaded, or if one or more could not be loaded.
     * @see asyncBeginLoad
     */
    fun loadResources(asset: FilamentAsset): Boolean {
        return FilaGltfioResourceLoader_loadResources(nativeHandle, asset.nativeHandle)
    }

    /**
     * Starts an asynchronous resource load (texture decoding may use worker threads).
     * Requires periodic calls to [asyncUpdateLoad] until [asyncGetLoadProgress] reaches 1.0.
     *
     * @return false if the loading process could not start.
     */
    fun asyncBeginLoad(asset: FilamentAsset): Boolean {
        return FilaGltfioResourceLoader_asyncBeginLoad(nativeHandle, asset.nativeHandle)
    }

    /** Gets the status of an asynchronous load as a percentage in `[0, 1]`. */
    fun asyncGetLoadProgress(): Float = FilaGltfioResourceLoader_asyncGetLoadProgress(nativeHandle)

    /**
     * Performs any pending main-thread work of an asynchronous load. Call periodically until
     * [asyncGetLoadProgress] returns 1.0; harmless after that.
     */
    fun asyncUpdateLoad() {
        FilaGltfioResourceLoader_asyncUpdateLoad(nativeHandle)
    }

    /**
     * Cancels pending decoder jobs, frees all CPU-side texel data, and flushes the Engine.
     * Only needed if [asyncBeginLoad] was used and cancellation is required before completion.
     */
    fun asyncCancelLoad() {
        FilaGltfioResourceLoader_asyncCancelLoad(nativeHandle)
    }

    /**
     * Frees memory by evicting the URI cache populated via [addResourceData]. Call only after a
     * model is fully loaded or loading has been cancelled.
     */
    fun evictResourceData() {
        FilaGltfioResourceLoader_evictResourceData(nativeHandle)
    }
}

/** A native copy of this configuration for [block]. */
private inline fun <R> ResourceConfiguration.useNative(block: (NativePointer) -> R): R {
    val c = FilaGltfioResourceConfiguration_create()
    try {
        FilaGltfioResourceConfiguration_setEngine(c, engine.nativeObject)
        FilaGltfioResourceConfiguration_setNormalizeSkinningWeights(c, normalizeSkinningWeights)
        return block(c)
    } finally {
        FilaGltfioResourceConfiguration_destroy(c)
    }
}
