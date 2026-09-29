package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.InternalFilamentApi
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.interop.*
import io.github.erkko68.filament.utils.capi.*

/**
 * Allows clients to create Filament textures from KTX2 containers.
 *
 * Contains a cache of the transcoding state. Call [requestFormat] at least once before [load].
 *
 * @param quiet don't log errors
 */
class Ktx2Reader(engine: Engine, quiet: Boolean = false) : AutoCloseable {
    internal val nativeHandle = FilaKtxreaderKtx2Reader_create(engine.nativeObject, quiet)

    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /** How [requestFormat], [load] and the async transcoding went. */
    enum class Result {
        SUCCESS,
        COMPRESSED_TRANSCODE_FAILURE,
        UNCOMPRESSED_TRANSCODE_FAILURE,
        FORMAT_UNSUPPORTED,
        FORMAT_ALREADY_REQUESTED,
    }

    /** The transfer function the KTX2 data must declare in its metadata. */
    enum class TransferFunction { LINEAR, sRGB }

    /** Frees the reader and its transcoding cache. */
    fun destroy() = FilaKtxreaderKtx2Reader_destroy(nativeHandle)

    /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
    override fun close() = destroy()

    /**
     * Enables a given texture format, for [load] to consider; formats requested early are
     * preferred. This must be called at least once before [load].
     */
    fun requestFormat(format: Texture.InternalFormat): Result = Result.entries[FilaKtxreaderKtx2Reader_requestFormat(nativeHandle, format.ordinal)]

    /** Removes a format from the list [requestFormat] builds. */
    fun unrequestFormat(format: Texture.InternalFormat) = FilaKtxreaderKtx2Reader_unrequestFormat(nativeHandle, format.ordinal)

    /**
     * Creates and loads a texture from a KTX2 blob, or null if none of the requested formats
     * can be extracted from it.
     *
     * The first requested format the platform supports and the transcoder can produce is used,
     * after lossless (zstd) decompression. [transfer] must match the blob's metadata, and
     * filters the final internal format.
     */
    fun load(data: ByteArray, transfer: TransferFunction): Texture? =
        data.usePinned { FilaKtxreaderKtx2Reader_load(nativeHandle, it, data.size, transfer.ordinal) }.takeIf { it != NullPointer }?.let { Texture(it) }

    /**
     * Creates a texture without transcoding it yet, so mipmap levels can be populated
     * asynchronously; [data] is copied. Returns null if none of the requested formats can be
     * extracted from it.
     *
     * ```kotlin
     * val async = reader.asyncCreate(data, TransferFunction.LINEAR)!!
     * val texture = async.texture
     * async.doTranscoding()      // from any thread
     * async.uploadImages()       // from the foreground thread
     * reader.asyncDestroy(async)
     * ```
     */
    fun asyncCreate(data: ByteArray, transfer: TransferFunction): Async? =
        data.usePinned { FilaKtxreaderKtx2Reader_asyncCreate(nativeHandle, it, data.size, transfer.ordinal) }.takeIf { it != NullPointer }?.let { Async(it) }

    /** Frees [async] and the source data it copied, but not its texture. */
    fun asyncDestroy(async: Async) = interopScope {
        FilaKtxreaderKtx2Reader_asyncDestroy(nativeHandle, toInterop(listOf(async.nativeHandle)))
    }

    /** A texture whose mipmap levels [asyncCreate] leaves to transcode and upload. */
    class Async internal constructor(internal val nativeHandle: NativePointer) {
        /** The texture, created synchronously by [asyncCreate]. */
        val texture: Texture get() = Texture(FilaKtxreaderKtx2ReaderAsync_getTexture(nativeHandle))

        /** Transcodes all miplevels into a staging area; safe to call from a background thread. */
        fun doTranscoding(): Result = Result.entries[FilaKtxreaderKtx2ReaderAsync_doTranscoding(nativeHandle)]

        /**
         * Uploads the miplevels transcoded so far; safe while [doTranscoding] still runs in
         * another thread. Call it from the foreground thread (it calls Texture.setImage).
         */
        fun uploadImages() = FilaKtxreaderKtx2ReaderAsync_uploadImages(nativeHandle)
    }
}
