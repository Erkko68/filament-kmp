package io.github.erkko68.filament

import io.github.erkko68.filament.jni.SkinningBuffer as JniSkinningBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

actual class SkinningBuffer(val nativeSkinningBuffer: JniSkinningBuffer) {
    actual class Builder actual constructor() {
        private val jni = JniSkinningBuffer.Builder()

        actual fun boneCount(boneCount: Int): Builder {
            jni.boneCount(boneCount)
            return this
        }

        actual fun initialize(initialize: Boolean): Builder {
            jni.initialize(initialize)
            return this
        }

        actual fun build(engine: Engine): SkinningBuffer =
            SkinningBuffer(jni.build(engine.nativeEngine))
    }

    actual fun getBoneCount(): Int = nativeSkinningBuffer.boneCount

    actual fun setBonesAsMatrices(engine: Engine, matrices: FloatArray, boneCount: Int, offset: Int) {
        val buffer = ByteBuffer.allocateDirect(matrices.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        buffer.put(matrices)
        buffer.rewind()
        nativeSkinningBuffer.setBonesAsMatrices(engine.nativeEngine, buffer, boneCount, offset)
    }
}
