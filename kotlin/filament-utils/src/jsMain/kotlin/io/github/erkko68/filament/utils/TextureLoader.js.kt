package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.js.Texture as JSTexture
import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.Int8Array

actual object TextureLoader {
    actual fun loadTexture(
        engine: Engine,
        buffer: ByteArray,
        type: TextureType
    ): Texture? {
        if (buffer.isEmpty()) return null

        val jsEngine = engine.jsEngine
        val int8 = Int8Array(buffer.size)
        buffer.forEachIndexed { i, b -> int8.asDynamic()[i] = b }
        val arrayBuffer = int8.unsafeCast<ArrayBufferView>()

        return try {
            val jsTexture: JSTexture? = when {
                isKtx1(buffer) -> jsEngine.createTextureFromKtx1(arrayBuffer)
                isPng(buffer) -> jsEngine.createTextureFromPng(arrayBuffer)
                isJpeg(buffer) -> jsEngine.createTextureFromJpeg(arrayBuffer)
                else -> null
            }
            jsTexture?.let { Texture(it) }
        } catch (e: Exception) {
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
