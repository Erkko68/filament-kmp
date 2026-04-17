package io.github.erkko68.filament

actual class TextureSampler {
    actual fun getMinFilter(): MinFilter {
        TODO("Not yet implemented")
    }

    actual fun setMinFilter(filter: MinFilter) {
    }

    actual fun getMagFilter(): MagFilter {
        TODO("Not yet implemented")
    }

    actual fun setMagFilter(filter: MagFilter) {
    }

    actual fun getWrapModeS(): WrapMode {
        TODO("Not yet implemented")
    }

    actual fun setWrapModeS(mode: WrapMode) {
    }

    actual fun getWrapModeT(): WrapMode {
        TODO("Not yet implemented")
    }

    actual fun setWrapModeT(mode: WrapMode) {
    }

    actual fun getWrapModeR(): WrapMode {
        TODO("Not yet implemented")
    }

    actual fun setWrapModeR(mode: WrapMode) {
    }

    actual fun getAnisotropy(): Float {
        TODO("Not yet implemented")
    }

    actual fun setAnisotropy(anisotropy: Float) {
    }

    actual fun getCompareMode(): CompareMode {
        TODO("Not yet implemented")
    }

    actual fun setCompareMode(mode: CompareMode) {
    }

    actual fun getCompareFunction(): CompareFunction {
        TODO("Not yet implemented")
    }

    actual fun setCompareFunction(function: CompareFunction) {
    }

    actual enum class WrapMode { CLAMP_TO_EDGE, REPEAT, MIRRORED_REPEAT }
    actual enum class MinFilter { NEAREST, LINEAR, NEAREST_MIPMAP_NEAREST, LINEAR_MIPMAP_NEAREST, NEAREST_MIPMAP_LINEAR, LINEAR_MIPMAP_LINEAR }
    actual enum class MagFilter { NEAREST, LINEAR }
    actual enum class CompareMode { NONE, COMPARE_TO_TEXTURE }
    actual enum class CompareFunction { LESS_EQUAL, GREATER_EQUAL, LESS, GREATER, EQUAL, NOT_EQUAL, ALWAYS, NEVER }

    actual constructor() {
        TODO("Not yet implemented")
    }

    actual constructor(minMag: MagFilter) {
        TODO("Not yet implemented")
    }

    actual constructor(
        minMag: MagFilter,
        wrap: WrapMode
    ) {
        TODO("Not yet implemented")
    }

    actual constructor(
        min: MinFilter,
        mag: MagFilter,
        wrap: WrapMode
    ) {
        TODO("Not yet implemented")
    }

    actual constructor(
        min: MinFilter,
        mag: MagFilter,
        s: WrapMode,
        t: WrapMode,
        r: WrapMode
    ) {
        TODO("Not yet implemented")
    }

    actual constructor(mode: CompareMode) {
        TODO("Not yet implemented")
    }

    actual constructor(
        mode: CompareMode,
        function: CompareFunction
    ) {
        TODO("Not yet implemented")
    }
}