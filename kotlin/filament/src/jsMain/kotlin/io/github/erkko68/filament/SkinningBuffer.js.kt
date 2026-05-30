package io.github.erkko68.filament

// TODO(js): SkinningBuffer is not bound in upstream jsbindings.cpp (v1.71.5); skinning
// runs through RenderableManager.Builder. The actual stores Builder inputs so
// common-tests reading `boneCount` pass.
actual class SkinningBuffer internal constructor(
    internal val jsSkinningBuffer: Any? = null,
    actual val boneCount: Int = 0,
) {
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
        // TODO(js): SkinningBuffer not bound in jsbindings.cpp — no-op on web
    }

    actual class Builder {
        private var boneCount: Int = 0

        actual fun boneCount(boneCount: Int): Builder {
            this.boneCount = boneCount
            return this
        }

        actual fun initialize(initialize: Boolean): Builder {
            return this
        }

        actual fun build(engine: Engine): SkinningBuffer {
            return SkinningBuffer(boneCount = boneCount)
        }
    }
}
