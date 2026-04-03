package dev.filament.kmp

actual class SkinningBuffer {
    actual fun setBonesAsMatrices(engine: Engine, matrices: Any, boneCount: Int, offset: Int) {
        TODO("Not yet implemented")
    }

    actual fun setBonesAsQuaternions(engine: Engine, quaternions: Any, boneCount: Int, offset: Int) {
        TODO("Not yet implemented")
    }

    actual fun getBoneCount(): Int = TODO("Not yet implemented")

    actual fun getNativeObject(): Long = TODO("Not yet implemented")

    actual internal fun invalidate() {
    }

    actual class Builder {
        actual fun boneCount(boneCount: Int): Builder = TODO("Not yet implemented")

        actual fun initialize(initialize: Boolean): Builder = TODO("Not yet implemented")

        actual fun build(engine: Engine): SkinningBuffer = TODO("Not yet implemented")
    }
}

