package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.IndirectLight
import io.github.erkko68.filament.Skybox
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.web.Texture as JSTexture
import io.github.erkko68.filament.web.IndirectLight as JSIndirectLight
import io.github.erkko68.filament.web.Skybox as JSSkybox
import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.Int8Array
import org.khronos.webgl.set

private fun ByteArray.toArrayBufferView(): ArrayBufferView {
    val int8 = Int8Array(size)
    forEachIndexed { i, b -> int8[i] = b }
    return int8.unsafeCast<ArrayBufferView>()
}

// SH extraction reads KTX1 metadata via Filament's Buffer + Ktx1Bundle globals. Declared as
// external interfaces + top-level js() constructors so the same code compiles on wasmJs.
private external interface FilamentBuffer : JsAny {
    fun delete()
}
private external interface Ktx1Bundle : JsAny {
    fun getMetadata(key: String): String
    fun delete()
}
private fun filamentBuffer(view: ArrayBufferView): FilamentBuffer = js("Filament.Buffer(view)")
private fun newKtx1Bundle(buf: FilamentBuffer): Ktx1Bundle = js("new Filament.Ktx1Bundle(buf)")

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
            val kbd = filamentBuffer(buffer.toArrayBufferView())
            val ktx = newKtx1Bundle(kbd)
            val shString = ktx.getMetadata("sh")
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
