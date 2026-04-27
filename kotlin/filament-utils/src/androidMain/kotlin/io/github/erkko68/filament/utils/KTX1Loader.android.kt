package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.IndirectLight
import io.github.erkko68.filament.Skybox
import io.github.erkko68.filament.Texture
import java.nio.ByteBuffer

actual object KTX1Loader {

    init { com.google.android.filament.utils.Utils.init() }

    actual class Options {
        actual var srgb: Boolean = false
    }

    actual class IndirectLightBundle actual constructor(
        actual val indirectLight: IndirectLight?,
        actual val cubemap: Texture?
    )

    actual class SkyboxBundle actual constructor(
        actual val skybox: Skybox?,
        actual val cubemap: Texture?
    )

    actual fun createTexture(engine: Engine, buffer: ByteArray, options: Options): Texture? {
        val javaOptions = com.google.android.filament.utils.KTX1Loader.Options()
        javaOptions.srgb = options.srgb
        val byteBuffer = ByteBuffer.wrap(buffer)
        return Texture(com.google.android.filament.utils.KTX1Loader.createTexture(
            engine.nativeEngine,
            byteBuffer,
            javaOptions
        ))
    }

    actual fun createIndirectLight(engine: Engine, buffer: ByteArray, options: Options): IndirectLightBundle {
        val javaOptions = com.google.android.filament.utils.KTX1Loader.Options()
        javaOptions.srgb = options.srgb
        val byteBuffer = ByteBuffer.wrap(buffer)
        val javaBundle = com.google.android.filament.utils.KTX1Loader.createIndirectLight(
            engine.nativeEngine,
            byteBuffer,
            javaOptions
        )
        return IndirectLightBundle(
            javaBundle.indirectLight?.let { IndirectLight(it) },
            javaBundle.cubemap?.let { Texture(it) }
        )
    }

    actual fun createSkybox(engine: Engine, buffer: ByteArray, options: Options): SkyboxBundle {
        val javaOptions = com.google.android.filament.utils.KTX1Loader.Options()
        javaOptions.srgb = options.srgb
        val byteBuffer = ByteBuffer.wrap(buffer)
        val javaBundle = com.google.android.filament.utils.KTX1Loader.createSkybox(
            engine.nativeEngine,
            byteBuffer,
            javaOptions
        )
        return SkyboxBundle(
            javaBundle.skybox?.let { Skybox(it) },
            javaBundle.cubemap?.let { Texture(it) }
        )
    }

    actual fun getSphericalHarmonics(buffer: ByteArray): FloatArray? {
        val byteBuffer = ByteBuffer.wrap(buffer)
        return com.google.android.filament.utils.KTX1Loader.getSphericalHarmonics(byteBuffer)
    }
}
