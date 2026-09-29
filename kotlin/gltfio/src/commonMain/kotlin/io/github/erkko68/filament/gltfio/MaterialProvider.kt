package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.InternalFilamentApi
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.VertexBuffer
import io.github.erkko68.filament.gltfio.capi.*
import io.github.erkko68.filament.interop.*

/**
 * MaterialProvider supplies materials to glTF assets during loading: the ubershader provider picks
 * from a pre-compiled set of materials, see [createUbershaderProvider].
 *
 * @see AssetLoader
 */
class MaterialProvider @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) : AutoCloseable {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * Creates or fetches a compiled Filament material, then creates an instance from it.
     *
     * @param config Properties of the glTF material; may be mutated to trim unsupported features.
     * @param uvmap Mapping from glTF texcoord sets to Filament UV sets, written by the provider.
     * @param label Debug name for the material instance.
     * @param extras glTF extras as stringified JSON (not part of the cache key).
     */
    fun createMaterialInstance(config: MaterialKey, uvmap: UvMap, label: String? = "material", extras: String? = null): MaterialInstance? =
        config.useNative { k ->
            uvmap.useNative { u, n ->
                label.useCString { l -> extras.useCString { e -> FilaGltfioMaterialProvider_createMaterialInstance(nativeHandle, k, u, n, l, e) } }
            }
        }.takeIf { it != NullPointer }?.let { MaterialInstance(it) }

    /** Creates or fetches the compiled Filament material corresponding to [config], without instancing it. */
    fun getMaterial(config: MaterialKey, uvmap: UvMap, label: String? = "material"): Material? =
        config.useNative { k ->
            uvmap.useNative { u, n -> label.useCString { l -> FilaGltfioMaterialProvider_getMaterial(nativeHandle, k, u, n, l) } }
        }.takeIf { it != NullPointer }?.let { Material(it) }

    /** The provider's cache of compiled materials (weak references). */
    val materials: List<Material>
        get() = readPointers(FilaGltfioMaterialProvider_getMaterials(nativeHandle), materialsCount).map { Material(it) }

    val materialsCount: Int get() = FilaGltfioMaterialProvider_getMaterialsCount(nativeHandle)

    /**
     * Destroys all cached materials. NOT called by [destroy], which lets clients take ownership of
     * the cache if desired.
     */
    fun destroyMaterials() = FilaGltfioMaterialProvider_destroyMaterials(nativeHandle)

    /**
     * Returns true if the given vertex attribute must be present. Some providers (e.g. ubershader)
     * require dummy attribute values when the glTF model does not provide them.
     */
    fun needsDummyData(attrib: VertexBuffer.VertexAttribute): Boolean = FilaGltfioMaterialProvider_needsDummyData(nativeHandle, attrib.ordinal)

    /** Frees the provider itself; cached materials survive unless [destroyMaterials] was called. */
    fun destroy() {
        FilaGltfioMaterialProvider_destroy(nativeHandle)
        nativeHandle = NullPointer
    }

    /** Same as [destroy]; lets this be used with `use { }`. */
    override fun close() = destroy()
}

/**
 * Decodes the textures [ResourceLoader] hands it for the MIME types it's registered under with
 * [ResourceLoader.addTextureProvider]. Destroy it after the loaders using it.
 */
class TextureProvider @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) : AutoCloseable {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    fun destroy() {
        FilaGltfioTextureProvider_destroy(nativeHandle)
        nativeHandle = NullPointer
    }

    /** Same as [destroy]; lets this be used with `use { }`. */
    override fun close() = destroy()
}

/** A provider over gltfio's default pre-compiled ubershader materials. */
fun createUbershaderProvider(engine: Engine): MaterialProvider =
    MaterialProvider(FilaGltfio_createUbershaderProvider(engine.nativeObject, FilaGltfio_getUberarchiveData(), FilaGltfio_getUberarchiveSize()))

/** A provider decoding PNG and JPEG with stb_image; register it for `image/png` and `image/jpeg`. */
fun createStbProvider(engine: Engine): TextureProvider = TextureProvider(FilaGltfio_createStbProvider(engine.nativeObject))

/** A provider transcoding KTX2 (Basis Universal); register it for `image/ktx2`. */
fun createKtx2Provider(engine: Engine): TextureProvider = TextureProvider(FilaGltfio_createKtx2Provider(engine.nativeObject))

/** A provider decoding WebP, or null where this build has no WebP support ([isWebpSupported]); register it for `image/webp`. */
fun createWebpProvider(engine: Engine): TextureProvider? =
    FilaGltfio_createWebpProvider(engine.nativeObject).takeIf { it != NullPointer }?.let { TextureProvider(it) }

/** Whether this build of gltfio can decode WebP textures. */
fun isWebpSupported(): Boolean = FilaGltfio_isWebpSupported()
