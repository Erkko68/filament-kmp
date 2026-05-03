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

    actual var minFilter: MinFilter
        get() = MinFilter.fromJni(JniTextureSampler(nativeSampler).minFilter)
        set(value) { val jni = JniTextureSampler(nativeSampler); jni.setMinFilter(value.toJni()); nativeSampler = jni.sampler }

    actual var magFilter: MagFilter
        get() = MagFilter.fromJni(JniTextureSampler(nativeSampler).magFilter)
        set(value) { val jni = JniTextureSampler(nativeSampler); jni.setMagFilter(value.toJni()); nativeSampler = jni.sampler }

    actual var wrapModeS: WrapMode
        get() = WrapMode.fromJni(JniTextureSampler(nativeSampler).wrapModeS)
        set(value) { val jni = JniTextureSampler(nativeSampler); jni.setWrapModeS(value.toJni()); nativeSampler = jni.sampler }

    actual var wrapModeT: WrapMode
        get() = WrapMode.fromJni(JniTextureSampler(nativeSampler).wrapModeT)
        set(value) { val jni = JniTextureSampler(nativeSampler); jni.setWrapModeT(value.toJni()); nativeSampler = jni.sampler }

    actual var wrapModeR: WrapMode
        get() = WrapMode.fromJni(JniTextureSampler(nativeSampler).wrapModeR)
        set(value) { val jni = JniTextureSampler(nativeSampler); jni.setWrapModeR(value.toJni()); nativeSampler = jni.sampler }

    actual var anisotropy: Float
        get() = JniTextureSampler(nativeSampler).anisotropy
        set(value) { val jni = JniTextureSampler(nativeSampler); jni.setAnisotropy(value); nativeSampler = jni.sampler }

    actual var compareMode: CompareMode
        get() = CompareMode.fromJni(JniTextureSampler(nativeSampler).compareMode)
        set(value) { val jni = JniTextureSampler(nativeSampler); jni.setCompareMode(value.toJni()); nativeSampler = jni.sampler }

    actual var compareFunction: CompareFunction
        get() = CompareFunction.fromJni(JniTextureSampler(nativeSampler).compareFunction)
        set(value) { val jni = JniTextureSampler(nativeSampler); jni.setCompareFunction(value.toJni()); nativeSampler = jni.sampler }
}
