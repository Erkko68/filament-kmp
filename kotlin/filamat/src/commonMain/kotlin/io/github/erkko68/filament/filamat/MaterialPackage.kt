package io.github.erkko68.filament.filamat

/**
 * A compiled material (filamat's `Package`): the bytes `Material.Builder.payload` loads.
 * Named MaterialPackage, as on Android, so it doesn't shadow `java.lang.Package` on the JVM.
 *
 * Check [isValid] before use: [MaterialBuilder.build] returns an invalid package when compilation fails.
 *
 * @see MaterialBuilder
 */
class MaterialPackage private constructor(
    /** The package's bytes. */
    val data: ByteArray,
) {
    /** [size] zeroed bytes. */
    constructor(size: Int) : this(ByteArray(size))

    /** A copy of the first [size] bytes of [src]. */
    constructor(src: ByteArray, size: Int = src.size) : this(src.copyOf(size))

    /** Byte count of [data]. */
    val size: Int get() = data.size

    /** False when compilation failed. */
    var isValid: Boolean = true

    companion object {
        /** An empty package marked invalid. */
        fun invalidPackage(): MaterialPackage = MaterialPackage(0).apply { isValid = false }
    }
}
