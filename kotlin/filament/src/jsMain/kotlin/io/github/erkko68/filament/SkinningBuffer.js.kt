package io.github.erkko68.filament

actual class SkinningBuffer {
    actual fun getBoneCount(): Int {
        TODO("Not yet implemented")
    }

    actual fun setBonesAsMatrices(
        engine: Engine,
        matrices: FloatArray,
        boneCount: Int,
        offset: Int
    ) {
    }

    actual class Builder {
        actual fun boneCount(boneCount: Int): Builder {
            TODO("Not yet implemented")
        }

        actual fun initialize(initialize: Boolean): Builder {
            TODO("Not yet implemented")
        }

        actual fun build(engine: Engine): SkinningBuffer {
            TODO("Not yet implemented")
        }
    }
}