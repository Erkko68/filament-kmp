package dev.filament.kmp

import kotlinx.cinterop.*
import filament.*

actual object MathUtils {
    actual fun packTangentFrame(
        tangentX: Float,
        tangentY: Float,
        tangentZ: Float,
        bitangentX: Float,
        bitangentY: Float,
        bitangentZ: Float,
        normalX: Float,
        normalY: Float,
        normalZ: Float,
        quaternion: FloatArray,
    ) {
        packTangentFrame(
            tangentX, tangentY, tangentZ,
            bitangentX, bitangentY, bitangentZ,
            normalX, normalY, normalZ,
            quaternion, 0
        )
    }

    actual fun packTangentFrame(
        tangentX: Float,
        tangentY: Float,
        tangentZ: Float,
        bitangentX: Float,
        bitangentY: Float,
        bitangentZ: Float,
        normalX: Float,
        normalY: Float,
        normalZ: Float,
        quaternion: FloatArray,
        offset: Int,
    ) {
        val q = FloatArray(4)
        memScoped {
            val nativeQ = allocArray<FloatVar>(4)
            FilaMathUtils_packTangentFrame(
                tangentX, tangentY, tangentZ,
                bitangentX, bitangentY, bitangentZ,
                normalX, normalY, normalZ,
                nativeQ
            )
            for (i in 0 until 4) {
                quaternion[offset + i] = nativeQ[i]
            }
        }
    }
}
