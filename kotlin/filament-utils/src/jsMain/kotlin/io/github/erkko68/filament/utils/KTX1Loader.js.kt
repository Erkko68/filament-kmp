package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.IndirectLight
import io.github.erkko68.filament.Skybox
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.js.Texture as JSTexture
import io.github.erkko68.filament.js.IndirectLight as JSIndirectLight
import io.github.erkko68.filament.js.Skybox as JSSkybox
import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.Int8Array

private fun ByteArray.toArrayBufferView(): ArrayBufferView {
    val int8 = Int8Array(size)
    forEachIndexed { i, b -> int8.asDynamic()[i] = b }
    return int8.unsafeCast<ArrayBufferView>()
}

actual object KTX1Loader {
    actual class Options actual constructor() {
        actual var srgb: Boolean = false
    }

    actual class IndirectLightBundle actual constructor(
        indirectLight: IndirectLight?,
        cubemap: Texture?
    ) {
        actual val indirectLight: IndirectLight? = indirectLight
        actual val cubemap: Texture? = cubemap
    }

    actual class SkyboxBundle actual constructor(
        skybox: Skybox?,
        cubemap: Texture?
    ) {
        actual val skybox: Skybox? = skybox
        actual val cubemap: Texture? = cubemap
    }

    actual fun createTexture(engine: Engine, buffer: ByteArray, options: Options): Texture? {
        return try {
            Texture(engine.jsEngine.createTextureFromKtx1(buffer.toArrayBufferView()))
        } catch (e: Exception) {
            null
        }
    }

    actual fun createIndirectLight(engine: Engine, buffer: ByteArray, options: Options): IndirectLightBundle {
        return try {
            val jsIbl = engine.jsEngine.createIblFromKtx1(buffer.toArrayBufferView())
            val indirectLight = IndirectLight(jsIbl)
            val cubemap = indirectLight.reflectionsTexture
            IndirectLightBundle(indirectLight, cubemap)
        } catch (e: Exception) {
            IndirectLightBundle(null, null)
        }
    }

    actual fun createSkybox(engine: Engine, buffer: ByteArray, options: Options): SkyboxBundle {
        return try {
            val jsSky = engine.jsEngine.createSkyFromKtx1(buffer.toArrayBufferView())
            val skybox = Skybox(jsSky)
            val cubemap = skybox.texture
            SkyboxBundle(skybox, cubemap)
        } catch (e: Exception) {
            SkyboxBundle(null, null)
        }
    }

    actual fun getSphericalHarmonics(buffer: ByteArray): FloatArray? {
        return try {
            val arrayBufferView = buffer.toArrayBufferView()
            val kbd = js("Filament.Buffer(arrayBufferView)")
            val ktx = js("new Filament.Ktx1Bundle(kbd)")
            val shString = ktx.getMetadata("sh").unsafeCast<String>()
            ktx.delete()
            kbd.delete()
            if (shString.isEmpty()) return null
            val parts = shString.trim().split(Regex("\\s+"))
            if (parts.size >= 27) {
                FloatArray(27) { i -> parts[i].toFloat() }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
