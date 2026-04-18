package io.github.erkko68.filament

actual class SkinningBuffer(internal val jsSkinningBuffer: Any? = null) {
    actual fun getBoneCount(): Int {
        return 0
    }

    actual fun setBonesAsMatrices(
        engine: Engine,
        matrices: FloatArray,
        boneCount: Int,
        offset: Int
    ) {
        // In JS, skinning is handled via RenderableManager.Builder
    }

    actual class Builder {
        actual fun boneCount(boneCount: Int): Builder {
            return this
        }

        actual fun initialize(initialize: Boolean): Builder {
            return this
        }

        actual fun build(engine: Engine): SkinningBuffer {
            return SkinningBuffer()
        }
    }
}