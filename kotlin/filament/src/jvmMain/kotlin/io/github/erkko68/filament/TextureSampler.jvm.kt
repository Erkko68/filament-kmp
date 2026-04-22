package io.github.erkko68.filament

import io.github.erkko68.filament.jni.TextureSampler as JniTextureSampler

actual class TextureSampler(var nativeSampler: Long) {
    actual enum class WrapMode {
        CLAMP_TO_EDGE, REPEAT, MIRRORED_REPEAT;
        internal fun toJni() = JniTextureSampler.WrapMode.values()[ordinal]
        companion object {
            internal fun fromJni(jni: JniTextureSampler.WrapMode) = values()[jni.ordinal]
        }
    }

    actual enum class MinFilter {
        NEAREST, LINEAR, NEAREST_MIPMAP_NEAREST, LINEAR_MIPMAP_NEAREST, NEAREST_MIPMAP_LINEAR, LINEAR_MIPMAP_LINEAR;
        internal fun toJni() = JniTextureSampler.MinFilter.values()[ordinal]
        companion object {
            internal fun fromJni(jni: JniTextureSampler.MinFilter) = values()[jni.ordinal]
        }
    }

    actual enum class MagFilter {
        NEAREST, LINEAR;
        internal fun toJni() = JniTextureSampler.MagFilter.values()[ordinal]
        companion object {
            internal fun fromJni(jni: JniTextureSampler.MagFilter) = values()[jni.ordinal]
        }
    }

    actual enum class CompareMode {
        NONE, COMPARE_TO_TEXTURE;
        internal fun toJni() = JniTextureSampler.CompareMode.values()[ordinal]
        companion object {
            internal fun fromJni(jni: JniTextureSampler.CompareMode) = values()[jni.ordinal]
        }
    }

    actual enum class CompareFunction {
        LESS_EQUAL, GREATER_EQUAL, LESS, GREATER, EQUAL, NOT_EQUAL, ALWAYS, NEVER;
        internal fun toJni() = JniTextureSampler.CompareFunction.values()[ordinal]
        companion object {
            internal fun fromJni(jni: JniTextureSampler.CompareFunction) = values()[jni.ordinal]
        }
    }

    private constructor(jni: JniTextureSampler) : this(jni.sampler)

    actual constructor() : this(JniTextureSampler())
    actual constructor(minMag: MagFilter) : this(JniTextureSampler(minMag.toJni()))
    actual constructor(minMag: MagFilter, wrap: WrapMode) : this(JniTextureSampler(minMag.toJni(), wrap.toJni()))
    actual constructor(min: MinFilter, mag: MagFilter, wrap: WrapMode) : this(JniTextureSampler(min.toJni(), mag.toJni(), wrap.toJni()))
    actual constructor(min: MinFilter, mag: MagFilter, s: WrapMode, t: WrapMode, r: WrapMode) : this(JniTextureSampler(min.toJni(), mag.toJni(), s.toJni(), t.toJni(), r.toJni()))
    actual constructor(mode: CompareMode) : this(JniTextureSampler(mode.toJni()))
    actual constructor(mode: CompareMode, function: CompareFunction) : this(JniTextureSampler(mode.toJni(), function.toJni()))

    actual fun getMinFilter(): MinFilter = MinFilter.fromJni(JniTextureSampler(nativeSampler).minFilter)
    actual fun setMinFilter(filter: MinFilter) {
        val jni = JniTextureSampler(nativeSampler)
        jni.setMinFilter(filter.toJni())
        nativeSampler = jni.sampler
    }

    actual fun getMagFilter(): MagFilter = MagFilter.fromJni(JniTextureSampler(nativeSampler).magFilter)
    actual fun setMagFilter(filter: MagFilter) {
        val jni = JniTextureSampler(nativeSampler)
        jni.setMagFilter(filter.toJni())
        nativeSampler = jni.sampler
    }

    actual fun getWrapModeS(): WrapMode = WrapMode.fromJni(JniTextureSampler(nativeSampler).wrapModeS)
    actual fun setWrapModeS(mode: WrapMode) {
        val jni = JniTextureSampler(nativeSampler)
        jni.setWrapModeS(mode.toJni())
        nativeSampler = jni.sampler
    }

    actual fun getWrapModeT(): WrapMode = WrapMode.fromJni(JniTextureSampler(nativeSampler).wrapModeT)
    actual fun setWrapModeT(mode: WrapMode) {
        val jni = JniTextureSampler(nativeSampler)
        jni.setWrapModeT(mode.toJni())
        nativeSampler = jni.sampler
    }

    actual fun getWrapModeR(): WrapMode = WrapMode.fromJni(JniTextureSampler(nativeSampler).wrapModeR)
    actual fun setWrapModeR(mode: WrapMode) {
        val jni = JniTextureSampler(nativeSampler)
        jni.setWrapModeR(mode.toJni())
        nativeSampler = jni.sampler
    }

    actual fun getAnisotropy(): Float = JniTextureSampler(nativeSampler).anisotropy
    actual fun setAnisotropy(anisotropy: Float) {
        val jni = JniTextureSampler(nativeSampler)
        jni.setAnisotropy(anisotropy)
        nativeSampler = jni.sampler
    }

    actual fun getCompareMode(): CompareMode = CompareMode.fromJni(JniTextureSampler(nativeSampler).compareMode)
    actual fun setCompareMode(mode: CompareMode) {
        val jni = JniTextureSampler(nativeSampler)
        jni.setCompareMode(mode.toJni())
        nativeSampler = jni.sampler
    }

    actual fun getCompareFunction(): CompareFunction = CompareFunction.fromJni(JniTextureSampler(nativeSampler).compareFunction)
    actual fun setCompareFunction(function: CompareFunction) {
        val jni = JniTextureSampler(nativeSampler)
        jni.setCompareFunction(function.toJni())
        nativeSampler = jni.sampler
    }
}
