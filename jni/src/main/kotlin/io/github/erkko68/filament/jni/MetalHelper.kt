package io.github.erkko68.filament.jni

/** macOS-only helpers for sharing `MTLTexture`s between Filament and skiko. */
object MetalHelper {
    @JvmStatic external fun nCreateMetalTexture(devicePtr: Long, width: Int, height: Int): Long
    @JvmStatic external fun nIsSystemDefaultDevice(devicePtr: Long): Boolean
}
