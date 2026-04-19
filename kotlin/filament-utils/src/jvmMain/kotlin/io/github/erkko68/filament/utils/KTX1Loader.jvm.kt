package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.IndirectLight
import io.github.erkko68.filament.Skybox
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.utils.jni.KTX1Loader as JniKTX1Loader
import java.nio.ByteBuffer
import java.nio.ByteOrder

actual object KTX1Loader {
    actual class Options actual constructor() {
        val jni = JniKTX1Loader.Options()
        actual var srgb: Boolean get() = jni.srgb; set(v) { jni.srgb = v }
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
        val byteBuffer = ByteBuffer.allocateDirect(buffer.size).order(ByteOrder.nativeOrder())
        byteBuffer.put(buffer)
        byteBuffer.rewind()
        
        val jniTexture = JniKTX1Loader.createTexture(engine.nativeEngine, byteBuffer, options.jni)
        return jniTexture?.let { Texture(it) }
    }

    actual fun createIndirectLight(engine: Engine, buffer: ByteArray, options: Options): IndirectLightBundle {
        val byteBuffer = ByteBuffer.allocateDirect(buffer.size).order(ByteOrder.nativeOrder())
        byteBuffer.put(buffer)
        byteBuffer.rewind()
        
        val bundle = JniKTX1Loader.createIndirectLight(engine.nativeEngine, byteBuffer, options.jni)
        return if (bundle != null) {
            IndirectLightBundle(
                bundle.indirectLight?.let { IndirectLight(it) },
                bundle.cubemap?.let { Texture(it) }
            )
        } else {
            IndirectLightBundle(null, null)
        }
    }

    actual fun createSkybox(engine: Engine, buffer: ByteArray, options: Options): SkyboxBundle {
        val byteBuffer = ByteBuffer.allocateDirect(buffer.size).order(ByteOrder.nativeOrder())
        byteBuffer.put(buffer)
        byteBuffer.rewind()
        
        val bundle = JniKTX1Loader.createSkybox(engine.nativeEngine, byteBuffer, options.jni)
        return if (bundle != null) {
            SkyboxBundle(
                bundle.skybox?.let { Skybox(it) },
                bundle.cubemap?.let { Texture(it) }
            )
        } else {
            SkyboxBundle(null, null)
        }
    }

    actual fun getSphericalHarmonics(buffer: ByteArray): FloatArray? {
        val byteBuffer = ByteBuffer.allocateDirect(buffer.size).order(ByteOrder.nativeOrder())
        byteBuffer.put(buffer)
        byteBuffer.rewind()
        
        return JniKTX1Loader.getSphericalHarmonics(byteBuffer)
    }
}
