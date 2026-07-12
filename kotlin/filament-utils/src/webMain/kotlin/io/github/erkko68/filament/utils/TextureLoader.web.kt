package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.web.Texture as JSTexture
import io.github.erkko68.filament.web.interop.emptyJsObject
import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.Int8Array
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.set

// Texture::Usage bits (filament/backend/DriverEnums.h). DEFAULT = UPLOADABLE | SAMPLEABLE.
private const val UPLOADABLE = 0x0008
private const val SAMPLEABLE = 0x0010
private const val GEN_MIPMAPPABLE = 0x0200

// Options bag passed to Filament.js createTextureFrom* (a JS object literal).
private external interface TextureCreateOptions : JsAny {
    var srgb: Boolean
    var usage: Int
}

actual object TextureLoader {
    actual fun loadTexture(
        engine: Engine,
        buffer: ByteArray,
        type: TextureType
    ): Texture? {
        if (buffer.isEmpty()) return null

        val jsEngine = engine.jsEngine
        // Filament's embind decoders expect a Uint8Array view (a raw Int8Array is rejected with a
        // native BindingError); match the Uint8Array idiom used across the JS bindings.
        val int8 = Int8Array(buffer.size)
        buffer.forEachIndexed { i, b -> int8[i] = b }
        val arrayBuffer = Uint8Array(int8.buffer).unsafeCast<ArrayBufferView>()

        // The JS helper builds the texture then calls generateMipmaps(), but unlike the native loader
        // it doesn't set GEN_MIPMAPPABLE usage, so generateMipmaps() aborts with a native exception.
        // Pass the same usage mask the native path uses (DEFAULT | GEN_MIPMAPPABLE).
        // Workaround for an upstream Filament JS bug; see js/patches/upstream/0002-*.patch.
        // Decode COLOR textures as sRGB so albedo maps match the other platforms' srgb path.
        val options = emptyJsObject().unsafeCast<TextureCreateOptions>()
        options.srgb = type == TextureType.COLOR
        options.usage = UPLOADABLE or SAMPLEABLE or GEN_MIPMAPPABLE

        return try {
            val jsTexture: JSTexture? = when {
                isKtx1(buffer) -> jsEngine.createTextureFromKtx1(arrayBuffer)
                isKtx2(buffer) -> jsEngine.createTextureFromKtx2(arrayBuffer)
                isPng(buffer) -> jsEngine.createTextureFromPng(arrayBuffer, options)
                isJpeg(buffer) -> jsEngine.createTextureFromJpeg(arrayBuffer, options)
                else -> null
            }
            jsTexture?.let { Texture(it) }
        } catch (e: Throwable) {
            // Embind throws raw native values (e.g. a number) on bad data; swallow them so a
            // corrupt image never crashes the app.
            null
        }
    }

    private fun isKtx1(buffer: ByteArray): Boolean {
        return buffer.size >= 12 &&
               buffer[0] == 0xAB.toByte() &&
               buffer[1] == 0x4B.toByte() &&
               buffer[2] == 0x54.toByte() &&
               buffer[3] == 0x58.toByte() &&
               buffer[4] == 0x20.toByte() &&
               buffer[5] == 0x31.toByte()
    }

    private fun isKtx2(buffer: ByteArray): Boolean {
        return buffer.size >= 12 &&
               buffer[0] == 0xAB.toByte() &&
               buffer[1] == 0x4B.toByte() &&
               buffer[2] == 0x54.toByte() &&
               buffer[3] == 0x58.toByte() &&
               buffer[4] == 0x20.toByte() &&
               buffer[5] == 0x32.toByte() &&
               buffer[6] == 0x30.toByte()
    }

    private fun isPng(buffer: ByteArray): Boolean {
        return buffer.size >= 8 &&
               buffer[0] == 0x89.toByte() &&
               buffer[1] == 0x50.toByte() &&
               buffer[2] == 0x4E.toByte() &&
               buffer[3] == 0x47.toByte()
    }

    private fun isJpeg(buffer: ByteArray): Boolean {
        return buffer.size >= 2 &&
               buffer[0] == 0xFF.toByte() &&
               buffer[1] == 0xD8.toByte()
    }

    actual enum class TextureType { COLOR, NORMAL, DATA }
}
