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

    actual val boneCount: Int get() = nativeSkinningBuffer.boneCount

    actual fun setBonesAsMatrices(engine: Engine, matrices: FloatArray, boneCount: Int, offset: Int) {
        val buffer = ByteBuffer.allocateDirect(matrices.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        buffer.put(matrices)
        buffer.rewind()
        nativeSkinningBuffer.setBonesAsMatrices(engine.nativeEngine, buffer, boneCount, offset)
    }

    actual fun setBonesAsQuaternions(engine: Engine, bones: FloatArray, boneCount: Int, offset: Int) {
        val buffer = ByteBuffer.allocateDirect(bones.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        buffer.put(bones)
        buffer.rewind()
        nativeSkinningBuffer.setBonesAsQuaternions(engine.nativeEngine, buffer, boneCount, offset)
    }
}
