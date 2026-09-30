package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.InternalFilamentApi
import io.github.erkko68.filament.interop.*
import io.github.erkko68.filament.utils.capi.*

/**
 * An in-memory representation of a KTX1 file: a hierarchy of blobs (miplevels, array elements,
 * cubemap faces) and metadata.
 *
 * @see Ktx1Reader
 */
class Ktx1Bundle @InternalFilamentApi constructor(nativeHandle: NativePointer) : AutoCloseable {
    internal var nativeHandle = nativeHandle
        private set

    /** Creates a hierarchy of empty texture blobs, to be filled before serializing. */
    constructor(numMipLevels: Int, arrayLength: Int, isCubemap: Boolean) :
        this(loaded { FilaImageKtx1Bundle_create_uint32_t_uint32_t_bool(numMipLevels, arrayLength, isCubemap) })

    /** Creates a hierarchy of blobs by parsing the contents of a KTX file; [bytes] are copied. */
    constructor(bytes: ByteArray) :
        this(loaded { bytes.usePinned { FilaImageKtx1Bundle_create_uint8_t_uint32_t(it, bytes.size) } })

    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /** Frees the bundle, unless [Ktx1Reader.createTexture] took it. */
    fun destroy() {
        if (nativeHandle != NullPointer) FilaImageKtx1Bundle_destroy(nativeHandle)
        nativeHandle = NullPointer
    }

    /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
    override fun close() = destroy()

    /** The bundle belongs to Filament now, which frees it. */
    internal fun release(): NativePointer = nativeHandle.also { nativeHandle = NullPointer }

    /** Writes out the KTX file into [destination], which must hold [serializedLength] bytes; false if it's too small. */
    fun serialize(destination: ByteArray): Boolean = destination.usePinned { FilaImageKtx1Bundle_serialize(nativeHandle, it, destination.size) }

    /** The size of the KTX file [serialize] writes. */
    val serializedLength: Int get() = FilaImageKtx1Bundle_getSerializedLength(nativeHandle)

    /** The KTX metadata value of [key], or null if it has none. */
    fun getMetadata(key: String): String? =
        stringFromInterop(key.useCString { FilaImageKtx1Bundle_getMetadata(nativeHandle, it, NullPointer) })

    /** Adds a key/value pair to the KTX metadata. */
    fun setMetadata(key: String, value: String) =
        key.useCString { k -> value.useCString { v -> FilaImageKtx1Bundle_setMetadata(nativeHandle, k, v) } }

    /**
     * Parses the key="sh" metadata into [result]: 3 bands of spherical harmonics, 9 RGB
     * coefficients (27 floats).
     *
     * @return false if the metadata is missing or malformed
     */
    fun getSphericalHarmonics(result: FloatArray): Boolean = result.usePinned { FilaImageKtx1Bundle_getSphericalHarmonics(nativeHandle, it) }

    /** The number of miplevels. */
    val numMipLevels: Int get() = FilaImageKtx1Bundle_getNumMipLevels(nativeHandle)

    /** The number of array elements, which is 1 for a plain texture. */
    val arrayLength: Int get() = FilaImageKtx1Bundle_getArrayLength(nativeHandle)

    /** Whether the bundle holds a cubemap (6 faces per miplevel and array element). */
    val isCubemap: Boolean get() = FilaImageKtx1Bundle_isCubemap(nativeHandle)
}

/** [create]'s result once the native library is loaded: bundles are usable before any Engine exists. */
private inline fun loaded(create: () -> NativePointer): NativePointer {
    Filament.init()
    return create()
}
