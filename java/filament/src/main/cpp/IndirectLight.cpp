#include <jni.h>
#include <filament/IndirectLight.h>
#include <filament/Engine.h>
#include <filament/Texture.h>
#include <math/mat3.h>
#include <math/vec3.h>

using namespace filament;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_IndirectLight_nCreateBuilder(JNIEnv* env, jclass) {
    return (jlong) new IndirectLight::Builder();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_IndirectLight_nDestroyBuilder(JNIEnv* env, jclass, jlong nativeBuilder) {
    delete (IndirectLight::Builder*) nativeBuilder;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_IndirectLight_nBuilderReflections(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeTexture) {
    ((IndirectLight::Builder*) nativeBuilder)->reflections((Texture*) nativeTexture);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_IndirectLight_nIrradiance(JNIEnv* env, jclass, jlong nativeBuilder, jint bands, jfloatArray sh) {
    jfloat* nativeSh = env->GetFloatArrayElements(sh, nullptr);
    ((IndirectLight::Builder*) nativeBuilder)->irradiance((uint8_t) bands, (math::float3 const*) nativeSh);
    env->ReleaseFloatArrayElements(sh, nativeSh, JNI_ABORT);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_IndirectLight_nRadiance(JNIEnv* env, jclass, jlong nativeBuilder, jint bands, jfloatArray sh) {
    jfloat* nativeSh = env->GetFloatArrayElements(sh, nullptr);
    ((IndirectLight::Builder*) nativeBuilder)->radiance((uint8_t) bands, (math::float3 const*) nativeSh);
    env->ReleaseFloatArrayElements(sh, nativeSh, JNI_ABORT);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_IndirectLight_nIrradianceAsTexture(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeTexture) {
    ((IndirectLight::Builder*) nativeBuilder)->irradiance((Texture*) nativeTexture);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_IndirectLight_nIntensity(JNIEnv* env, jclass, jlong nativeBuilder, jfloat envIntensity) {
    ((IndirectLight::Builder*) nativeBuilder)->intensity((float) envIntensity);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_IndirectLight_nRotation(JNIEnv* env, jclass, jlong nativeBuilder, jfloat v0, jfloat v1, jfloat v2, jfloat v3, jfloat v4, jfloat v5, jfloat v6, jfloat v7, jfloat v8) {
    ((IndirectLight::Builder*) nativeBuilder)->rotation(math::mat3f(math::mat3f::row_major_init(v0, v1, v2, v3, v4, v5, v6, v7, v8)));
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_IndirectLight_nBuilderBuild(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeEngine) {
    return (jlong) ((IndirectLight::Builder*) nativeBuilder)->build(*(Engine*) nativeEngine);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_IndirectLight_nSetIntensity(JNIEnv* env, jclass, jlong nativeIndirectLight, jfloat intensity) {
    ((IndirectLight*) nativeIndirectLight)->setIntensity((float) intensity);
}

extern "C" JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_IndirectLight_nGetIntensity(JNIEnv* env, jclass, jlong nativeIndirectLight) {
    return (jfloat) ((IndirectLight*) nativeIndirectLight)->getIntensity();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_IndirectLight_nSetRotation(JNIEnv* env, jclass, jlong nativeIndirectLight, jfloat v0, jfloat v1, jfloat v2, jfloat v3, jfloat v4, jfloat v5, jfloat v6, jfloat v7, jfloat v8) {
    ((IndirectLight*) nativeIndirectLight)->setRotation(math::mat3f(math::mat3f::row_major_init(v0, v1, v2, v3, v4, v5, v6, v7, v8)));
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_IndirectLight_nGetRotation(JNIEnv* env, jclass, jlong nativeIndirectLight, jfloatArray outRotation_) {
    IndirectLight* indirectLight = (IndirectLight*) nativeIndirectLight;
    jfloat* outRotation = env->GetFloatArrayElements(outRotation_, nullptr);
    const math::mat3f m = indirectLight->getRotation();
    std::copy_n(&m[0][0], 9, outRotation);
    env->ReleaseFloatArrayElements(outRotation_, outRotation, 0);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_IndirectLight_nGetColorEstimate(JNIEnv* env, jclass, jlong nativeIndirectLight, jfloatArray outColor_, jfloat x, jfloat y, jfloat z) {
    IndirectLight* indirectLight = (IndirectLight*) nativeIndirectLight;
    jfloat* outColor = env->GetFloatArrayElements(outColor_, nullptr);
    *(math::float4*)outColor = indirectLight->getColorEstimate({x, y, z});
    env->ReleaseFloatArrayElements(outColor_, outColor, 0);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_IndirectLight_nGetDirectionEstimate(JNIEnv* env, jclass, jlong nativeIndirectLight, jfloatArray outDirection_) {
    IndirectLight* indirectLight = (IndirectLight*) nativeIndirectLight;
    jfloat* outDirection = env->GetFloatArrayElements(outDirection_, nullptr);
    *(math::float3*)outDirection = indirectLight->getDirectionEstimate();
    env->ReleaseFloatArrayElements(outDirection_, outDirection, 0);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_IndirectLight_nGetIrradianceTexture(JNIEnv* env, jclass, jlong nativeIndirectLight) {
    return (jlong) ((IndirectLight*) nativeIndirectLight)->getIrradianceTexture();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_IndirectLight_nGetReflectionsTexture(JNIEnv* env, jclass, jlong nativeIndirectLight) {
    return (jlong) ((IndirectLight*) nativeIndirectLight)->getReflectionsTexture();
}
