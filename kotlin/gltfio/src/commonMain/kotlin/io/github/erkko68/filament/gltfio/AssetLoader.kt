package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.EntityManager
import io.github.erkko68.filament.*
import io.github.erkko68.filament.interop.*
import io.github.erkko68.filament.InternalFilamentApi

/**
 * AssetLoader consumes glTF 2.0 content and produces FilamentAsset objects.
 *
 * AssetLoader parses a blob of glTF 2.0 content (either JSON or GLB format) and produces a
 * FilamentAsset object, which is a bundle of Filament textures, vertex buffers, index buffers,
 * and entities. An asset is composed of one or more FilamentInstance objects containing the
 * loaded scene hierarchy.
 *
 * **Clients must use AssetLoader to:**
 * - Create and destroy FilamentAsset objects (similar to how Engine creates core objects)
 * - Not fetch external buffer data or create textures (use ResourceLoader for this)
 *
 * **Material providers:**
 * AssetLoader uses MaterialProvider to determine how materials are created:
 * - UbershaderProvider: Uses a pre-compiled set of materials (recommended for performance)
 * - JIT-compiled materials: Generated on-the-fly using Filamat (more flexible)
 *
 * @see FilamentAsset
 * @see FilamentInstance
 * @see MaterialProvider
 * @see ResourceLoader
 */
class AssetLoader @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    companion object {
        /**
         * Create an AssetLoader instance.
         *
         * @param engine Filament Engine to use for creating buffers and textures.
         * @param materials MaterialProvider for supplying materials to the asset.
         * @param entities Optional EntityManager override (uses singleton if not provided).
         * @return A new AssetLoader instance.
         */
        fun create(engine: Engine, materials: MaterialProvider, entities: EntityManager? = null): AssetLoader {
            val handle = FilaAssetLoader_create(
                engine.nativeObject,
                materials.nativeObject,
                entities?.nativeObject ?: NullPointer
            )
            return AssetLoader(handle)
        }

        /**
         * Destroy an AssetLoader and release all internal resources.
         *
         * @param loader The AssetLoader to destroy.
         */
        fun destroy(loader: AssetLoader) {
            FilaAssetLoader_destroy(loader.nativeHandle)
            loader.nativeHandle = NullPointer
        }
    }

    /**
     * Create a FilamentAsset from glTF 2.0 data.
     *
     * Parses the glTF content and creates an asset with entities, materials, and buffers.
     * External resources (textures, buffer data) must be loaded separately using ResourceLoader.
     *
     * @param buffer ByteArray containing glTF 2.0 data (JSON or GLB format).
     * @return A new FilamentAsset, or null if parsing failed.
     */
    fun createAsset(buffer: ByteArray): FilamentAsset? {
        val handle = buffer.usePinned { pinned ->
            FilaAssetLoader_createAsset(nativeHandle, pinned, buffer.size)
        }
        return handle.takeIf { it != NullPointer }?.let { FilamentAsset(it) }
    }

    /**
     * Create a FilamentAsset with pre-allocated instances.
     *
     * Similar to createAsset(), but allows specifying pre-built FilamentInstance objects
     * to avoid repeated instantiation of the same asset.
     *
     * @param buffer ByteArray containing glTF 2.0 data.
     * @param instances Array of pre-created FilamentInstance objects.
     * @return A new FilamentAsset with the provided instances, or null if parsing failed.
     */
    fun createInstancedAsset(buffer: ByteArray, instances: Array<FilamentInstance>): FilamentAsset? {
        val handle = buffer.usePinned { FilaAssetLoader_createInstancedAsset(nativeHandle, it, buffer.size, instances.size) }
        if (handle == NullPointer) return null
        val asset = FilamentAsset(handle)
        for (i in instances.indices) {
            instances[i].nativeHandle = FilaFilamentAsset_getAssetInstanceAt(handle, i)
        }
        return asset
    }

    /**
     * Create a FilamentInstance from an existing FilamentAsset.
     *
     * Instances share material and geometry data but have independent entity hierarchies
     * and animations, allowing multiple renderings of the same asset with different transforms.
     *
     * @param asset The FilamentAsset to instantiate.
     * @return A new FilamentInstance, or null if creation failed.
     */
    fun createInstance(asset: FilamentAsset): FilamentInstance? {
        val handle = FilaAssetLoader_createInstance(nativeHandle, asset.nativeHandle).takeIf { it != NullPointer } ?: return null
        return FilamentInstance(handle)
    }

    /**
     * Enable diagnostic output for asset loading.
     *
     * When enabled, the loader produces verbose output useful for debugging glTF parsing issues.
     *
     * @param enable true to enable diagnostics, false to disable.
     */
    fun enableDiagnostics(enable: Boolean) {
        FilaAssetLoader_enableDiagnostics(nativeHandle, enable)
    }

    /**
     * Destroy a FilamentAsset and release all associated resources.
     *
     * @param asset The FilamentAsset to destroy.
     */
    fun destroyAsset(asset: FilamentAsset) {
        FilaAssetLoader_destroyAsset(nativeHandle, asset.nativeHandle)
        asset.nativeHandle = NullPointer
    }

    /**
     * Reclaims unused entities and components across all assets owned by this loader.
     */
    fun gc() {
        FilaAssetLoader_gc(nativeHandle)
    }
}

@ExternalSymbolName("FilaAssetLoader_create")
private external fun FilaAssetLoader_create(engine: NativePointer, materialProvider: NativePointer, entityManager: NativePointer): NativePointer

@ExternalSymbolName("FilaAssetLoader_destroy")
private external fun FilaAssetLoader_destroy(loader: NativePointer)

@ExternalSymbolName("FilaAssetLoader_createAsset")
private external fun FilaAssetLoader_createAsset(loader: NativePointer, buffer: NativePointer, bufferByteCount: Int): NativePointer

@ExternalSymbolName("FilaAssetLoader_createInstancedAsset")
private external fun FilaAssetLoader_createInstancedAsset(loader: NativePointer, buffer: NativePointer, bufferByteCount: Int, instanceCount: Int): NativePointer

@ExternalSymbolName("FilaAssetLoader_createInstance")
private external fun FilaAssetLoader_createInstance(loader: NativePointer, asset: NativePointer): NativePointer

@ExternalSymbolName("FilaAssetLoader_enableDiagnostics")
private external fun FilaAssetLoader_enableDiagnostics(loader: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaAssetLoader_destroyAsset")
private external fun FilaAssetLoader_destroyAsset(loader: NativePointer, asset: NativePointer)

@ExternalSymbolName("FilaAssetLoader_gc")
private external fun FilaAssetLoader_gc(loader: NativePointer)
