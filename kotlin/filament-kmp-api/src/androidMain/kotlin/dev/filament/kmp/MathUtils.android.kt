package dev.filament.kmp

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
        com.google.android.filament.MathUtils.packTangentFrame(
            tangentX,
            tangentY,
            tangentZ,
            bitangentX,
            bitangentY,
            bitangentZ,
            normalX,
            normalY,
            normalZ,
            quaternion,
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
        com.google.android.filament.MathUtils.packTangentFrame(
            tangentX,
            tangentY,
            tangentZ,
            bitangentX,
            bitangentY,
            bitangentZ,
            normalX,
            normalY,
            normalZ,
            quaternion,
            offset,
        )
    }
}

