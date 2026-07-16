package io.github.erkko68.filament.utils
import io.github.erkko68.filament.FilamentPlatform
import io.github.erkko68.filament.PlatformGap

actual object HDRLoader {
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "throws UnsupportedOperationException — filament.js exposes no Radiance/RGBE decoder.")
    actual fun createTexture(
        engine: io.github.erkko68.filament.Engine,
        buffer: ByteArray,
        internalFormat: io.github.erkko68.filament.Texture.InternalFormat
    ): io.github.erkko68.filament.Texture? {
        throw UnsupportedOperationException(
            "HDRLoader.createTexture is not supported on the JS/Web target. Filament.js does not " +
            "expose Radiance/RGBE decoding. Convert your HDR files to KTX1 offline (e.g. with " +
            "`cmgen`) and load them via KTX1Loader.createTexture."
        )
    }
}