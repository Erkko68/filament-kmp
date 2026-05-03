package io.github.erkko68.filament

actual class SkinningBuffer(internal val jsSkinningBuffer: Any? = null) {
    actual val boneCount: Int get() = 0 // not exposed in JS bindings

    actual fun setBonesAsMatrices(
        engine: Engine,
        matrices: FloatArray,
        boneCount: Int,
        offset: Int
    ) {
        // In JS, skinning is handled via RenderableManager.Builder
    }

    actual fun setBonesAsQuaternions(
        engine: Engine,
        bones: FloatArray,
        boneCount: Int,
        offset: Int
    ) {
        // Not exposed in JS bindings
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