#include <jni.h>
#include <filament/TextureSampler.h>

using namespace filament;

static inline jlong pack(backend::SamplerParams params) {
    return (jlong) *reinterpret_cast<uint32_t*>(&params);
}

static inline backend::SamplerParams unpack(jlong packed) {
    backend::SamplerParams params;
    *reinterpret_cast<uint32_t*>(&params) = (uint32_t) packed;
    return params;
}

namespace filament::JniUtils {
    TextureSampler from_long(jlong params) noexcept {
        return TextureSampler(unpack(params));
    }
}

extern "C" {

// Creates a sampler with full filter/wrap specification. Returns packed params as long.
JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_TextureSampler_nCreateSampler(
        JNIEnv*, jclass, jint min, jint mag, jint s, jint t, jint r) {
    TextureSampler sampler(
            (TextureSampler::MinFilter) min, (TextureSampler::MagFilter) mag,
            (TextureSampler::WrapMode) s, (TextureSampler::WrapMode) t,
            (TextureSampler::WrapMode) r);
    return pack(sampler.getSamplerParams());
}

// Creates a comparison sampler.
JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_TextureSampler_nCreateCompareSampler(
        JNIEnv*, jclass, jint mode, jint function) {
    TextureSampler sampler(
            (TextureSampler::CompareMode) mode,
            (TextureSampler::CompareFunc) function);
    return pack(sampler.getSamplerParams());
}

// ---- Getters ----

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_TextureSampler_nGetMinFilter(JNIEnv*, jclass, jlong sampler) {
    backend::SamplerParams p = unpack(sampler);
    return (jint) TextureSampler(p).getMinFilter();
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_TextureSampler_nSetMinFilter(JNIEnv*, jclass, jlong sampler, jint filter) {
    backend::SamplerParams p = unpack(sampler);
    TextureSampler s(p);
    s.setMinFilter((TextureSampler::MinFilter) filter);
    return pack(s.getSamplerParams());
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_TextureSampler_nGetMagFilter(JNIEnv*, jclass, jlong sampler) {
    backend::SamplerParams p = unpack(sampler);
    return (jint) TextureSampler(p).getMagFilter();
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_TextureSampler_nSetMagFilter(JNIEnv*, jclass, jlong sampler, jint filter) {
    backend::SamplerParams p = unpack(sampler);
    TextureSampler s(p);
    s.setMagFilter((TextureSampler::MagFilter) filter);
    return pack(s.getSamplerParams());
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_TextureSampler_nGetWrapModeS(JNIEnv*, jclass, jlong sampler) {
    backend::SamplerParams p = unpack(sampler);
    return (jint) TextureSampler(p).getWrapModeS();
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_TextureSampler_nSetWrapModeS(JNIEnv*, jclass, jlong sampler, jint mode) {
    backend::SamplerParams p = unpack(sampler);
    TextureSampler s(p);
    s.setWrapModeS((TextureSampler::WrapMode) mode);
    return pack(s.getSamplerParams());
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_TextureSampler_nGetWrapModeT(JNIEnv*, jclass, jlong sampler) {
    backend::SamplerParams p = unpack(sampler);
    return (jint) TextureSampler(p).getWrapModeT();
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_TextureSampler_nSetWrapModeT(JNIEnv*, jclass, jlong sampler, jint mode) {
    backend::SamplerParams p = unpack(sampler);
    TextureSampler s(p);
    s.setWrapModeT((TextureSampler::WrapMode) mode);
    return pack(s.getSamplerParams());
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_TextureSampler_nGetWrapModeR(JNIEnv*, jclass, jlong sampler) {
    backend::SamplerParams p = unpack(sampler);
    return (jint) TextureSampler(p).getWrapModeR();
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_TextureSampler_nSetWrapModeR(JNIEnv*, jclass, jlong sampler, jint mode) {
    backend::SamplerParams p = unpack(sampler);
    TextureSampler s(p);
    s.setWrapModeR((TextureSampler::WrapMode) mode);
    return pack(s.getSamplerParams());
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_jni_TextureSampler_nGetAnisotropy(JNIEnv*, jclass, jlong sampler) {
    backend::SamplerParams p = unpack(sampler);
    return (jfloat) TextureSampler(p).getAnisotropy();
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_TextureSampler_nSetAnisotropy(JNIEnv*, jclass, jlong sampler, jfloat anisotropy) {
    backend::SamplerParams p = unpack(sampler);
    TextureSampler s(p);
    s.setAnisotropy(anisotropy);
    return pack(s.getSamplerParams());
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_TextureSampler_nGetCompareMode(JNIEnv*, jclass, jlong sampler) {
    backend::SamplerParams p = unpack(sampler);
    return (jint) TextureSampler(p).getCompareMode();
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_TextureSampler_nSetCompareMode(JNIEnv*, jclass, jlong sampler, jint mode) {
    backend::SamplerParams p = unpack(sampler);
    TextureSampler s(p);
    s.setCompareMode((TextureSampler::CompareMode) mode);
    return pack(s.getSamplerParams());
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_TextureSampler_nGetCompareFunction(JNIEnv*, jclass, jlong sampler) {
    backend::SamplerParams p = unpack(sampler);
    return (jint) TextureSampler(p).getCompareFunc();
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_TextureSampler_nSetCompareFunction(JNIEnv*, jclass, jlong sampler, jint function) {
    backend::SamplerParams p = unpack(sampler);
    TextureSampler s(p);
    s.setCompareMode(s.getCompareMode(), (TextureSampler::CompareFunc) function);
    return pack(s.getSamplerParams());
}

} // extern "C"
