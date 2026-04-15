#include <jni.h>
#include <filament/ColorGrading.h>
#include <filament/Engine.h>
#include <filament/ToneMapper.h>
#include <math/vec3.h>
#include <math/vec4.h>

using namespace filament;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_ColorGrading_nCreateBuilder(JNIEnv* env, jclass) {
    return (jlong) new ColorGrading::Builder();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ColorGrading_nDestroyBuilder(JNIEnv* env, jclass, jlong nativeBuilder) {
    delete (ColorGrading::Builder*) nativeBuilder;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ColorGrading_nBuilderQuality(JNIEnv* env, jclass, jlong nativeBuilder, jint quality) {
    ((ColorGrading::Builder*) nativeBuilder)->quality((ColorGrading::QualityLevel) quality);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ColorGrading_nBuilderFormat(JNIEnv* env, jclass, jlong nativeBuilder, jint format) {
    ((ColorGrading::Builder*) nativeBuilder)->format((ColorGrading::LutFormat) format);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ColorGrading_nBuilderDimensions(JNIEnv* env, jclass, jlong nativeBuilder, jint dim) {
    ((ColorGrading::Builder*) nativeBuilder)->dimensions((uint32_t) dim);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ColorGrading_nBuilderToneMapper(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeToneMapper) {
    ((ColorGrading::Builder*) nativeBuilder)->toneMapper((ToneMapper*) nativeToneMapper);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ColorGrading_nBuilderLuminanceScaling(JNIEnv* env, jclass, jlong nativeBuilder, jboolean enabled) {
    ((ColorGrading::Builder*) nativeBuilder)->luminanceScaling((bool) enabled);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ColorGrading_nBuilderGamutMapping(JNIEnv* env, jclass, jlong nativeBuilder, jboolean enabled) {
    ((ColorGrading::Builder*) nativeBuilder)->gamutMapping((bool) enabled);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ColorGrading_nBuilderExposure(JNIEnv* env, jclass, jlong nativeBuilder, jfloat exposure) {
    ((ColorGrading::Builder*) nativeBuilder)->exposure((float) exposure);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ColorGrading_nBuilderNightAdaptation(JNIEnv* env, jclass, jlong nativeBuilder, jfloat adaptation) {
    ((ColorGrading::Builder*) nativeBuilder)->nightAdaptation((float) adaptation);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ColorGrading_nBuilderWhiteBalance(JNIEnv* env, jclass, jlong nativeBuilder, jfloat temperature, jfloat tint) {
    ((ColorGrading::Builder*) nativeBuilder)->whiteBalance((float) temperature, (float) tint);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ColorGrading_nBuilderChannelMixer(JNIEnv* env, jclass, jlong nativeBuilder, jfloatArray outRed, jfloatArray outGreen, jfloatArray outBlue) {
    jfloat* r = env->GetFloatArrayElements(outRed, nullptr);
    jfloat* g = env->GetFloatArrayElements(outGreen, nullptr);
    jfloat* b = env->GetFloatArrayElements(outBlue, nullptr);
    ((ColorGrading::Builder*) nativeBuilder)->channelMixer(*(math::float3*)r, *(math::float3*)g, *(math::float3*)b);
    env->ReleaseFloatArrayElements(outRed, r, JNI_ABORT);
    env->ReleaseFloatArrayElements(outGreen, g, JNI_ABORT);
    env->ReleaseFloatArrayElements(outBlue, b, JNI_ABORT);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ColorGrading_nBuilderShadowsMidtonesHighlights(JNIEnv* env, jclass, jlong nativeBuilder, jfloatArray shadows, jfloatArray midtones, jfloatArray highlights, jfloatArray ranges) {
    jfloat* s = env->GetFloatArrayElements(shadows, nullptr);
    jfloat* m = env->GetFloatArrayElements(midtones, nullptr);
    jfloat* h = env->GetFloatArrayElements(highlights, nullptr);
    jfloat* rg = env->GetFloatArrayElements(ranges, nullptr);
    ((ColorGrading::Builder*) nativeBuilder)->shadowsMidtonesHighlights(*(math::float4*)s, *(math::float4*)m, *(math::float4*)h, *(math::float4*)rg);
    env->ReleaseFloatArrayElements(shadows, s, JNI_ABORT);
    env->ReleaseFloatArrayElements(midtones, m, JNI_ABORT);
    env->ReleaseFloatArrayElements(highlights, h, JNI_ABORT);
    env->ReleaseFloatArrayElements(ranges, rg, JNI_ABORT);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ColorGrading_nBuilderSlopeOffsetPower(JNIEnv* env, jclass, jlong nativeBuilder, jfloatArray slope, jfloatArray offset, jfloatArray power) {
    jfloat* s = env->GetFloatArrayElements(slope, nullptr);
    jfloat* o = env->GetFloatArrayElements(offset, nullptr);
    jfloat* p = env->GetFloatArrayElements(power, nullptr);
    ((ColorGrading::Builder*) nativeBuilder)->slopeOffsetPower(*(math::float3*)s, *(math::float3*)o, *(math::float3*)p);
    env->ReleaseFloatArrayElements(slope, s, JNI_ABORT);
    env->ReleaseFloatArrayElements(offset, o, JNI_ABORT);
    env->ReleaseFloatArrayElements(power, p, JNI_ABORT);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ColorGrading_nBuilderContrast(JNIEnv* env, jclass, jlong nativeBuilder, jfloat contrast) {
    ((ColorGrading::Builder*) nativeBuilder)->contrast((float) contrast);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ColorGrading_nBuilderVibrance(JNIEnv* env, jclass, jlong nativeBuilder, jfloat vibrance) {
    ((ColorGrading::Builder*) nativeBuilder)->vibrance((float) vibrance);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ColorGrading_nBuilderSaturation(JNIEnv* env, jclass, jlong nativeBuilder, jfloat saturation) {
    ((ColorGrading::Builder*) nativeBuilder)->saturation((float) saturation);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_ColorGrading_nBuilderCurves(JNIEnv* env, jclass, jlong nativeBuilder, jfloatArray shadowGamma, jfloatArray midPoint, jfloatArray highlightScale) {
    jfloat* sg = env->GetFloatArrayElements(shadowGamma, nullptr);
    jfloat* mp = env->GetFloatArrayElements(midPoint, nullptr);
    jfloat* hs = env->GetFloatArrayElements(highlightScale, nullptr);
    ((ColorGrading::Builder*) nativeBuilder)->curves(*(math::float3*)sg, *(math::float3*)mp, *(math::float3*)hs);
    env->ReleaseFloatArrayElements(shadowGamma, sg, JNI_ABORT);
    env->ReleaseFloatArrayElements(midPoint, mp, JNI_ABORT);
    env->ReleaseFloatArrayElements(highlightScale, hs, JNI_ABORT);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_ColorGrading_nBuilderBuild(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeEngine) {
    return (jlong) ((ColorGrading::Builder*) nativeBuilder)->build(*(Engine*) nativeEngine);
}
