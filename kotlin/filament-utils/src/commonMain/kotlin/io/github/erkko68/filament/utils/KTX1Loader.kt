package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.IndirectLight
import io.github.erkko68.filament.Skybox
import io.github.erkko68.filament.Texture

expect object KTX1Loader {
    class Options() {
        var srgb: Boolean
    }

    class IndirectLightBundle(
        indirectLight: IndirectLight?,
        cubemap: Texture?
    ) {
        val indirectLight: IndirectLight?
        val cubemap: Texture?
    }

    class SkyboxBundle(
        skybox: Skybox?,
        cubemap: Texture?
    ) {
        val skybox: Skybox?
        val cubemap: Texture?
    }

    fun createTexture(engine: Engine, buffer: ByteArray, options: Options): Texture?

    fun createIndirectLight(engine: Engine, buffer: ByteArray, options: Options): IndirectLightBundle

    fun createSkybox(engine: Engine, buffer: ByteArray, options: Options): SkyboxBundle

    fun getSphericalHarmonics(buffer: ByteArray): FloatArray?
}
