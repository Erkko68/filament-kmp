package eric.bitria.samples

import io.github.erkko68.filament.jni.Texture as JniTexture

/**
 * JVM-only utility for managing shared textures between Filament and Skia.
 */
object TextureUtils {
    fun createSharedTexture(devicePtr: Long, physDevicePtr: Long, width: Int, height: Int): Long {
        return JniTexture.nCreateSharedTexture(devicePtr, physDevicePtr, width, height)
    }

    fun releaseSharedTexture(handle: Long) {
        if (handle != 0L) {
            JniTexture.nReleaseSharedTexture(handle)
        }
    }
}
