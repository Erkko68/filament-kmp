package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.InternalFilamentApi
import io.github.erkko68.filament.VertexBuffer
import io.github.erkko68.filament.interop.*

/**
 * MaterialProvider supplies materials to glTF assets during loading.
 *
 * Implementations determine how glTF materials are rendered:
 * - UbershaderProvider: Uses pre-compiled ubershader materials (recommended)
 * - Custom providers: Can implement custom material mapping strategies
 *
 * @see UbershaderProvider
 * @see AssetLoader
 */
interface MaterialProvider : AutoCloseable {
    /**
     * Creates or fetches a compiled Filament material, then creates an instance from it.
     *
     * @param config Properties of the glTF material; may be mutated to trim unsupported features.
     * @param uvmap Output: mapping from glTF texcoord sets to Filament UV sets, written by the provider.
     * @param label Optional debug name for the material instance.
     * @param extras Optional glTF extras as stringified JSON (not part of the cache key).
     */
    fun createMaterialInstance(config: MaterialKey, uvmap: IntArray, label: String? = null, extras: String? = null): io.github.erkko68.filament.MaterialInstance?

    /** Creates or fetches the compiled Filament material corresponding to [config], without instancing it. */
    fun getMaterial(config: MaterialKey, uvmap: IntArray, label: String? = null): io.github.erkko68.filament.Material?

    /** Gets the provider's cache of compiled materials (weak references). */
    val materials: List<io.github.erkko68.filament.Material>

    /**
     * Returns true if the given vertex attribute must be present. Some providers (e.g.
     * ubershader) require dummy attribute values when the glTF model does not provide them.
     */
    fun needsDummyData(attrib: VertexBuffer.VertexAttribute): Boolean

    /**
     * Destroys all cached materials. NOT called automatically on [destroy], which lets clients
     * take ownership of the cache if desired.
     */
    fun destroyMaterials()

    /** Frees the provider itself (cached materials survive unless [destroyMaterials] was called). */
    fun destroy()

    /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
    override fun close()

    /** The native provider, for interop with code calling the Fila* C API directly. */
    @InternalFilamentApi
    val nativeObject: NativePointer
}

/**
 * UbershaderProvider uses pre-compiled ubershader materials.
 *
 * This is the recommended MaterialProvider for most use cases. It uses a small set of
 * pre-compiled, flexible materials that cover most glTF 2.0 features, avoiding the overhead
 * of JIT compilation while maintaining broad compatibility.
 *
 * @see MaterialProvider
 */
class UbershaderProvider(engine: Engine) : MaterialProvider {
    private var nativeHandle: NativePointer = FilaMaterialProvider_createUbershaderProvider(engine.nativeObject, NullPointer, 0)

    @InternalFilamentApi
    override val nativeObject: NativePointer get() = nativeHandle

    override fun createMaterialInstance(config: MaterialKey, uvmap: IntArray, label: String?, extras: String?): io.github.erkko68.filament.MaterialInstance? =
        withKey(config, uvmap) { key, uv ->
            label.useCString { l -> extras.useCString { e -> FilaMaterialProvider_createMaterialInstance(nativeHandle, key, uv, l, e) } }
        }.takeIf { it != NullPointer }?.let { io.github.erkko68.filament.MaterialInstance(it) }

    override fun getMaterial(config: MaterialKey, uvmap: IntArray, label: String?): io.github.erkko68.filament.Material? =
        withKey(config, uvmap) { key, uv -> label.useCString { l -> FilaMaterialProvider_getMaterial(nativeHandle, key, uv, l) } }
            .takeIf { it != NullPointer }?.let { io.github.erkko68.filament.Material(it) }

    override val materials: List<io.github.erkko68.filament.Material>
        get() = List(FilaMaterialProvider_getMaterialsCount(nativeHandle)) { io.github.erkko68.filament.Material(FilaMaterialProvider_getMaterialAt(nativeHandle, it)) }

    override fun needsDummyData(attrib: VertexBuffer.VertexAttribute): Boolean = FilaMaterialProvider_needsDummyData(nativeHandle, attrib.ordinal)

    override fun destroyMaterials() = FilaMaterialProvider_destroyMaterials(nativeHandle)

    override fun destroy() {
        FilaMaterialProvider_destroy(nativeHandle)
        nativeHandle = NullPointer
    }

    override fun close() = destroy()
}

private inline fun <R> withKey(config: MaterialKey, uvmap: IntArray, block: (key: NativePointer, uvmap: NativePointer) -> R): R {
    val uv = ByteArray(8) { uvmap.getOrElse(it) { 0 }.toByte() }
    return config.toInts().usePinned { k -> uv.usePinned { u -> block(k, u) } }
}

@ExternalSymbolName("FilaMaterialProvider_createUbershaderProvider")
private external fun FilaMaterialProvider_createUbershaderProvider(engine: NativePointer, archive: NativePointer, archiveByteCount: Int): NativePointer

@ExternalSymbolName("FilaMaterialProvider_createMaterialInstance")
private external fun FilaMaterialProvider_createMaterialInstance(provider: NativePointer, key: NativePointer, uvmap: NativePointer, label: NativePointer, extras: NativePointer): NativePointer

@ExternalSymbolName("FilaMaterialProvider_getMaterial")
private external fun FilaMaterialProvider_getMaterial(provider: NativePointer, key: NativePointer, uvmap: NativePointer, label: NativePointer): NativePointer

@ExternalSymbolName("FilaMaterialProvider_getMaterialsCount")
private external fun FilaMaterialProvider_getMaterialsCount(provider: NativePointer): Int

@ExternalSymbolName("FilaMaterialProvider_getMaterialAt")
private external fun FilaMaterialProvider_getMaterialAt(provider: NativePointer, index: Int): NativePointer

@ExternalSymbolName("FilaMaterialProvider_needsDummyData")
private external fun FilaMaterialProvider_needsDummyData(provider: NativePointer, attrib: Int): Boolean

@ExternalSymbolName("FilaMaterialProvider_destroyMaterials")
private external fun FilaMaterialProvider_destroyMaterials(provider: NativePointer)

@ExternalSymbolName("FilaMaterialProvider_destroy")
private external fun FilaMaterialProvider_destroy(provider: NativePointer)
