package io.github.erkko68.filament.filamat

/**
 * MaterialPackage holds the binary data of a compiled material.
 */
expect class MaterialPackage {
    fun getBuffer(): ByteArray
    fun isValid(): Boolean
}
