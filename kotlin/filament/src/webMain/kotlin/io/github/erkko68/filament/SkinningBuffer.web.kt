package io.github.erkko68.filament

import io.github.erkko68.filament.web.interop.emptyJsObject
import io.github.erkko68.filament.web.interop.jsNumbers
import io.github.erkko68.filament.web.interop.toJsArray
import io.github.erkko68.filament.web.SkinningBuffer as JSSkinningBuffer

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual class SkinningBuffer internal constructor(
    internal val jsSkinningBuffer: JSSkinningBuffer,
) {
    actual val boneCount: Int
        get() = jsSkinningBuffer.getBoneCount().toInt()

    actual fun setBonesAsMatrices(
        engine: Engine,
        matrices: FloatArray,
        boneCount: Int,
        offset: Int
    ) {
        val jsMatrices = List(boneCount) { i ->
            val m = i * 16
            jsNumbers(
                matrices[m + 0], matrices[m + 1], matrices[m + 2], matrices[m + 3],
                matrices[m + 4], matrices[m + 5], matrices[m + 6], matrices[m + 7],
                matrices[m + 8], matrices[m + 9], matrices[m + 10], matrices[m + 11],
                matrices[m + 12], matrices[m + 13], matrices[m + 14], matrices[m + 15],
            )
        }
        jsSkinningBuffer.setBonesFromMatrices(engine.jsEngine, jsMatrices.toJsArray(), offset.toDouble())
    }

    actual fun setBonesAsQuaternions(
        engine: Engine,
        bones: FloatArray,
        boneCount: Int,
        offset: Int
    ) {
        // 7 floats per bone: unit quaternion (x, y, z, w) followed by translation (x, y, z).
        val jsBones = List(boneCount) { i ->
            val b = i * 7
            val bone = emptyJsObject().unsafeCast<io.github.erkko68.filament.web.RenderableManager_Bone>()
            bone.unitQuaternion = jsNumbers(bones[b + 0], bones[b + 1], bones[b + 2], bones[b + 3])
            bone.translation = jsNumbers(bones[b + 4], bones[b + 5], bones[b + 6])
            bone
        }
        jsSkinningBuffer.setBones(engine.jsEngine, jsBones.toJsArray(), offset.toDouble())
    }

    actual class Builder {
        private val jsBuilder = JSSkinningBuffer.Builder()

        actual fun boneCount(boneCount: Int): Builder {
            jsBuilder.boneCount(boneCount.toDouble())
            return this
        }

        actual fun initialize(initialize: Boolean): Builder {
            jsBuilder.initialize(initialize)
            return this
        }

        actual fun build(engine: Engine): SkinningBuffer =
            SkinningBuffer(jsBuilder.build(engine.jsEngine))
    }
}
