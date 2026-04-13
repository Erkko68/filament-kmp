package io.github.erkko68

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import com.google.android.filament.SkinningBuffer as AndroidSkinningBuffer

actual class SkinningBuffer internal constructor(val nativeSkinningBuffer: AndroidSkinningBuffer) {
    actual class Builder actual constructor() {
        private val nativeBuilder = AndroidSkinningBuffer.Builder()

        actual fun boneCount(boneCount: Int): Builder {
            nativeBuilder.boneCount(boneCount)
            return this
        }

        actual fun initialize(initialize: Boolean): Builder {
            nativeBuilder.initialize(initialize)
            return this
        }

        actual fun build(engine: Engine): SkinningBuffer {
            return SkinningBuffer(nativeBuilder.build(engine.nativeEngine))
        }
    }

    actual fun getBoneCount(): Int = nativeSkinningBuffer.boneCount

    actual fun setBonesAsMatrices(engine: Engine, matrices: FloatArray, boneCount: Int, offset: Int) {
        val buffer = ByteBuffer.allocateDirect(matrices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        buffer.put(matrices)
        buffer.flip()
        nativeSkinningBuffer.setBonesAsMatrices(engine.nativeEngine, buffer, boneCount, offset)
    }
}
