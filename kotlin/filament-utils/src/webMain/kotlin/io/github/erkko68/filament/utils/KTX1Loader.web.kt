package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.IndirectLight
import io.github.erkko68.filament.Skybox
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.wasm.*
import io.github.erkko68.filament.nativeObject

actual object KTX1Loader {
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
        val handle = buffer.usePinned { pinned ->
            FilaKTX1Loader_createTexture(
                engine.nativeObject,
                pinned,
                buffer.size,
                options.srgb
            )
        }
        return handle.takeIf { it != 0 }?.let { Texture(it) }
    }

    actual fun createIndirectLight(engine: Engine, buffer: ByteArray, options: Options): IndirectLightBundle {
        val sh = getSphericalHarmonics(buffer) ?: return IndirectLightBundle(null, null)
        val tex = createTexture(engine, buffer, options) ?: return IndirectLightBundle(null, null)
        
        val ilHandle = sh.usePinned { pinned ->
            FilaKTX1Loader_createIndirectLight(
                engine.nativeObject,
                tex.nativeObject,
                pinned
            )
        }
        return IndirectLightBundle(ilHandle.takeIf { it != 0 }?.let { IndirectLight(it) }, tex)
    }

    actual fun createSkybox(engine: Engine, buffer: ByteArray, options: Options): SkyboxBundle {
        val tex = createTexture(engine, buffer, options) ?: return SkyboxBundle(null, null)
        
        val skyboxHandle = FilaKTX1Loader_createSkybox(
            engine.nativeObject,
            tex.nativeObject
        )
        return SkyboxBundle(skyboxHandle.takeIf { it != 0 }?.let { Skybox(it) }, tex)
    }

    actual fun getSphericalHarmonics(buffer: ByteArray): FloatArray? {
        val sh = FloatArray(9 * 3)
        val success = buffer.usePinned { pinnedBuffer ->
            sh.usePinned { pinnedSh ->
                FilaKTX1Loader_getSphericalHarmonics(
                    pinnedBuffer,
                    buffer.size,
                    pinnedSh
                )
            }
        }
        return if (success) sh else null
    }
}
