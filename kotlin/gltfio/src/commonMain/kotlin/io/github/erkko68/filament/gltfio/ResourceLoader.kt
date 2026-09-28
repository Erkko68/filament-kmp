package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.*
import io.github.erkko68.filament.interop.*

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
class ResourceLoader : AutoCloseable {
    internal var nativeHandle: NativePointer
    private val providers = mutableListOf<NativePointer>()
    // gltfio keeps addResourceData buffers by pointer until they're evicted, so the copies live in this scope until then.
    private val resources = InteropScope()

    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * Create a ResourceLoader.
     *
     * @param engine Filament Engine to use for loading resources.
     * @param normalizeSkinningWeights Whether to normalize skinning weights to [0, 1] range.
     */
    constructor(engine: Engine, normalizeSkinningWeights: Boolean = false) {
        val loader = FilaResourceLoader_create(engine.nativeObject, normalizeSkinningWeights)
        nativeHandle = loader

        // Register the stb/ktx2 texture providers up front, as filament-android's ResourceLoader does.
        val stbProvider = FilaResourceLoader_createStbProvider(engine.nativeObject)
        if (stbProvider != NullPointer) {
            "image/jpeg".useCString { FilaResourceLoader_addTextureProvider(loader, it, stbProvider) }
            "image/png".useCString { FilaResourceLoader_addTextureProvider(loader, it, stbProvider) }
            providers.add(stbProvider)
        }

        val ktx2Provider = FilaResourceLoader_createKtx2Provider(engine.nativeObject)
        if (ktx2Provider != NullPointer) {
            "image/ktx2".useCString { FilaResourceLoader_addTextureProvider(loader, it, ktx2Provider) }
            providers.add(ktx2Provider)
        }
    }

    /**
     * Destroys this loader and frees its internal caches. Must happen on the same thread that
     * calls `Renderer.render()`, because the loader listens to buffer callbacks to know when
     * CPU-side data blobs can be freed.
     */
    fun destroy() {
        if (nativeHandle != NullPointer) FilaResourceLoader_destroy(nativeHandle)
        nativeHandle = NullPointer
        providers.forEach { FilaResourceLoader_destroyTextureProvider(it) }
        providers.clear()
        freeResourceCopies()
    }

    /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
    override fun close() = destroy()

    /**
     * Feeds the binary content of an external resource into the loader's URI cache.
     *
     * Every resource returned by [FilamentAsset.getResourceUris] should be added before calling
     * [loadResources] or [asyncBeginLoad]. Self-contained GLB files typically need no calls.
     */
    fun addResourceData(url: String, data: ByteArray) {
        val copy = resources.toInterop(data)
        url.useCString { FilaResourceLoader_addResourceData(nativeHandle, it, copy, data.size) }
    }

    /** Checks whether the given resource URI has already been added via [addResourceData]. */
    fun hasResourceData(url: String): Boolean = url.useCString { FilaResourceLoader_hasResourceData(nativeHandle, it) }

    /**
     * Synchronously loads resources for [asset] from the URI cache and finalizes the asset:
     * transforms vertex data if necessary, decodes images, and supplies tangent data.
     *
     * @return false if resources were already loaded, or if one or more could not be loaded.
     * @see asyncBeginLoad
     */
    fun loadResources(asset: FilamentAsset): Boolean {
        return FilaResourceLoader_loadResources(nativeHandle, asset.nativeHandle)
    }

    /**
     * Starts an asynchronous resource load (texture decoding may use worker threads).
     * Requires periodic calls to [asyncUpdateLoad] until [asyncGetLoadProgress] reaches 1.0.
     *
     * @return false if the loading process could not start.
     */
    fun asyncBeginLoad(asset: FilamentAsset): Boolean {
        return FilaResourceLoader_asyncBeginLoad(nativeHandle, asset.nativeHandle)
    }

    /** Gets the status of an asynchronous load as a percentage in `[0, 1]`. */
    fun asyncGetLoadProgress(): Float = FilaResourceLoader_asyncGetLoadProgress(nativeHandle)

    /**
     * Performs any pending main-thread work of an asynchronous load. Call periodically until
     * [asyncGetLoadProgress] returns 1.0; harmless after that.
     */
    fun asyncUpdateLoad() {
        FilaResourceLoader_asyncUpdateLoad(nativeHandle)
    }

    /**
     * Cancels pending decoder jobs, frees all CPU-side texel data, and flushes the Engine.
     * Only needed if [asyncBeginLoad] was used and cancellation is required before completion.
     */
    fun asyncCancelLoad() {
        FilaResourceLoader_asyncCancelLoad(nativeHandle)
    }

    /**
     * Frees memory by evicting the URI cache populated via [addResourceData]. Call only after a
     * model is fully loaded or loading has been cancelled.
     */
    fun evictResourceData() {
        FilaResourceLoader_evictResourceData(nativeHandle)
        freeResourceCopies()
    }

    private fun freeResourceCopies() {
        resources.release()
    }
}

@ExternalSymbolName("FilaResourceLoader_create")
private external fun FilaResourceLoader_create(engine: NativePointer, normalizeSkinningWeights: Boolean): NativePointer

@ExternalSymbolName("FilaResourceLoader_createStbProvider")
private external fun FilaResourceLoader_createStbProvider(engine: NativePointer): NativePointer

@ExternalSymbolName("FilaResourceLoader_addTextureProvider")
private external fun FilaResourceLoader_addTextureProvider(loader: NativePointer, mimeType: NativePointer, provider: NativePointer)

@ExternalSymbolName("FilaResourceLoader_createKtx2Provider")
private external fun FilaResourceLoader_createKtx2Provider(engine: NativePointer): NativePointer

@ExternalSymbolName("FilaResourceLoader_destroy")
private external fun FilaResourceLoader_destroy(loader: NativePointer)

@ExternalSymbolName("FilaResourceLoader_destroyTextureProvider")
private external fun FilaResourceLoader_destroyTextureProvider(provider: NativePointer)

@ExternalSymbolName("FilaResourceLoader_addResourceData")
private external fun FilaResourceLoader_addResourceData(loader: NativePointer, uri: NativePointer, buffer: NativePointer, bufferByteCount: Int)

@ExternalSymbolName("FilaResourceLoader_hasResourceData")
private external fun FilaResourceLoader_hasResourceData(loader: NativePointer, uri: NativePointer): Boolean

@ExternalSymbolName("FilaResourceLoader_loadResources")
private external fun FilaResourceLoader_loadResources(loader: NativePointer, asset: NativePointer): Boolean

@ExternalSymbolName("FilaResourceLoader_asyncBeginLoad")
private external fun FilaResourceLoader_asyncBeginLoad(loader: NativePointer, asset: NativePointer): Boolean

@ExternalSymbolName("FilaResourceLoader_asyncGetLoadProgress")
private external fun FilaResourceLoader_asyncGetLoadProgress(loader: NativePointer): Float

@ExternalSymbolName("FilaResourceLoader_asyncUpdateLoad")
private external fun FilaResourceLoader_asyncUpdateLoad(loader: NativePointer)

@ExternalSymbolName("FilaResourceLoader_asyncCancelLoad")
private external fun FilaResourceLoader_asyncCancelLoad(loader: NativePointer)

@ExternalSymbolName("FilaResourceLoader_evictResourceData")
private external fun FilaResourceLoader_evictResourceData(loader: NativePointer)
