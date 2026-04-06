package dev.filament.kmp

import com.google.android.filament.SkinningBuffer as AndroidSkinningBuffer
import java.nio.Buffer

actual class SkinningBuffer internal constructor(
    internal var androidSkinningBuffer: AndroidSkinningBuffer?,
) {
    actual fun setBonesAsMatrices(engine: Engine, matrices: Any, boneCount: Int, offset: Int) {
        val skinningBuffer = requireNotNull(androidSkinningBuffer) { "Calling method on destroyed SkinningBuffer" }
        val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
        skinningBuffer.setBonesAsMatrices(androidEngine, matrices as Buffer, boneCount, offset)
    }

    actual fun setBonesAsQuaternions(engine: Engine, quaternions: Any, boneCount: Int, offset: Int) {
        val skinningBuffer = requireNotNull(androidSkinningBuffer) { "Calling method on destroyed SkinningBuffer" }
        val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
        skinningBuffer.setBonesAsQuaternions(androidEngine, quaternions as Buffer, boneCount, offset)
    }

    actual fun getBoneCount(): Int {
        val skinningBuffer = requireNotNull(androidSkinningBuffer) { "Calling method on destroyed SkinningBuffer" }
        return skinningBuffer.boneCount
    }

    actual val nativeObject: Long
        get() {
        val skinningBuffer = requireNotNull(androidSkinningBuffer) { "Calling method on destroyed SkinningBuffer" }
        return skinningBuffer.nativeObject
    }

    actual internal fun invalidate() {
        androidSkinningBuffer = null
    }

    actual class Builder {
        private val androidBuilder = AndroidSkinningBuffer.Builder()

        actual fun boneCount(boneCount: Int): Builder {
            androidBuilder.boneCount(boneCount)
            return this
        }

        actual fun initialize(initialize: Boolean): Builder {
            androidBuilder.initialize(initialize)
            return this
        }

        actual fun build(engine: Engine): SkinningBuffer {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return SkinningBuffer(androidBuilder.build(androidEngine))
        }
    }
}

