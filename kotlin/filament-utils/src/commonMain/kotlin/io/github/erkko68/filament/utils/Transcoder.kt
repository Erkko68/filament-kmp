package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.InternalFilamentApi
import io.github.erkko68.filament.interop.*
import io.github.erkko68.filament.utils.capi.*

/** A vertex attribute component type [Transcoder] reads. */
enum class ComponentType {
    /** Normalized, maps [-127, 127] to [-1, +1]. */
    BYTE,
    /** Normalized, maps [0, 255] to [0, +1]. */
    UBYTE,
    /** Normalized, maps [-32767, 32767] to [-1, +1]. */
    SHORT,
    /** Normalized, maps [0, 65535] to [0, +1]. */
    USHORT,
    /** 1 sign bit, 5 exponent bits, and 5 mantissa bits. */
    HALF,
    /** Standard 32-bit float. */
    FLOAT,
}

/**
 * Converts vertex attribute data into tightly packed floats: useful for 3-component formats not
 * every backend supports (the Vulkan minspec includes float3 but not short3).
 *
 * ```kotlin
 * val transcode = Transcoder(Transcoder.Config(ComponentType.BYTE, normalized = true, componentCount = 3))
 * transcode(output, input, count)
 * ```
 *
 * Signed normalized data follows Vulkan and OpenGL ES 3.0+: a byte of -127 maps exactly to -1.
 */
class Transcoder(config: Config) : AutoCloseable {
    internal val nativeHandle: NativePointer = config.useNative { FilaGeometryTranscoder_create(it) }

    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * The format of all input data.
     *
     * @property inputStrideBytes 0 means tightly packed
     */
    class Config(
        var componentType: ComponentType,
        var normalized: Boolean,
        var componentCount: Int,
        var inputStrideBytes: Int = 0,
    )

    /** Frees the transcoder. */
    fun destroy() = FilaGeometryTranscoder_destroy(nativeHandle)

    /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
    override fun close() = destroy()

    /**
     * Converts up to [count] items (e.g. float3 values, not bytes) of [source] into packed floats
     * in [target], or only measures them when [target] is null.
     *
     * @return the number of bytes [count] items take as packed floats
     */
    operator fun invoke(target: FloatArray?, source: ByteArray, count: Int): Int = interopScope {
        val t = toInterop(target)
        FilaGeometryTranscoder_invoke(nativeHandle, t, toInterop(source), count).also { if (target != null) t.fromInterop(target) }
    }
}

private inline fun <R> Transcoder.Config.useNative(block: (NativePointer) -> R): R {
    Filament.init() // CPU-only, usable before any Engine exists
    val c = FilaGeometryTranscoderConfig_create()
    try {
        FilaGeometryTranscoderConfig_setComponentType(c, componentType.ordinal)
        FilaGeometryTranscoderConfig_setNormalized(c, normalized)
        FilaGeometryTranscoderConfig_setComponentCount(c, componentCount)
        FilaGeometryTranscoderConfig_setInputStrideBytes(c, inputStrideBytes)
        return block(c)
    } finally {
        FilaGeometryTranscoderConfig_destroy(c)
    }
}
