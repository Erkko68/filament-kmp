package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.EntityManager
import io.github.erkko68.filament.InternalFilamentApi
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.gltfio.capi.*
import io.github.erkko68.filament.interop.*

/**
 * Construction parameters for an [AssetLoader].
 *
 * @property engine Filament Engine to use for creating buffers and textures.
 * @property materials Supplies the assets' materials.
 * @property entities Optional EntityManager override (the engine's singleton when null).
 */
class AssetConfiguration(
    var engine: Engine,
    var materials: MaterialProvider,
    var entities: EntityManager? = null,
)

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
 * AssetLoader uses a MaterialProvider to create materials, e.g. [createUbershaderProvider]'s
 * pre-compiled set.
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
        /** Creates an AssetLoader; destroy it with [destroy]. */
        fun create(config: AssetConfiguration): AssetLoader {
            val c = FilaGltfioAssetConfiguration_create()
            try {
                FilaGltfioAssetConfiguration_setEngine(c, config.engine.nativeObject)
                FilaGltfioAssetConfiguration_setMaterials(c, config.materials.nativeObject)
                FilaGltfioAssetConfiguration_setEntities(c, config.entities?.nativeObject ?: NullPointer)
                return AssetLoader(FilaGltfioAssetLoader_create(c))
            } finally {
                FilaGltfioAssetConfiguration_destroy(c)
            }
        }

        /**
         * Destroy an AssetLoader and release all internal resources.
         *
         * @param loader The AssetLoader to destroy.
         */
        fun destroy(loader: AssetLoader) {
            interopScope { FilaGltfioAssetLoader_destroy(toInterop(listOf(loader.nativeHandle))) }
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
        val handle = buffer.usePinned { FilaGltfioAssetLoader_createAsset(nativeHandle, it, buffer.size) }
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
        val handle = interopScope {
            val slots = toInterop(List(instances.size) { NullPointer })
            buffer.usePinned { FilaGltfioAssetLoader_createInstancedAsset(nativeHandle, it, buffer.size, slots, instances.size) }
                .also { if (it != NullPointer) readPointers(slots, instances.size).forEachIndexed { i, p -> instances[i].nativeHandle = p } }
        }
        return handle.takeIf { it != NullPointer }?.let { FilamentAsset(it) }
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
        val handle = FilaGltfioAssetLoader_createInstance(nativeHandle, asset.nativeHandle).takeIf { it != NullPointer } ?: return null
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
        FilaGltfioAssetLoader_enableDiagnostics(nativeHandle, enable)
    }

    /**
     * Destroy a FilamentAsset and release all associated resources.
     *
     * @param asset The FilamentAsset to destroy.
     */
    fun destroyAsset(asset: FilamentAsset) {
        FilaGltfioAssetLoader_destroyAsset(nativeHandle, asset.nativeHandle)
        asset.nativeHandle = NullPointer
    }

    /**
     * Reclaims unused entities and components across all assets owned by this loader.
     */
    fun gc() {
        FilaGltfioAssetLoader_gc(nativeHandle)
    }

    /** The materials this loader's assets use, across all its assets. */
    val materials: List<Material>
        get() = readPointers(FilaGltfioAssetLoader_getMaterials(nativeHandle), materialsCount).map { Material(it) }

    val materialsCount: Int get() = FilaGltfioAssetLoader_getMaterialsCount(nativeHandle)

    /** The provider this loader was configured with. */
    val materialProvider: MaterialProvider get() = MaterialProvider(FilaGltfioAssetLoader_getMaterialProvider(nativeHandle))
}
