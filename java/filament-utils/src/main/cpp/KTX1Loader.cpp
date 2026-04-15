#include <jni.h>
#include <filament/Engine.h>
#include <filament/IndirectLight.h>
#include <filament/Skybox.h>
#include <filament/Texture.h>
#include <ktxreader/Ktx1Reader.h>
#include <math/vec3.h>
#include "common/NioUtils.h"

using namespace filament;
using namespace filament::math;
using namespace ktxreader;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_jni_KTX1Loader_nCreateKTXTexture(JNIEnv* env, jclass, jlong nativeEngine, jobject javaBuffer, jint remaining, jboolean srgb) {
    Engine* engine = (Engine*) nativeEngine;
    AutoBuffer buffer(env, javaBuffer, remaining);
    Ktx1Bundle* bundle = new Ktx1Bundle((const uint8_t*) buffer.getData(), buffer.getSize());
    return (jlong) Ktx1Reader::createTexture(engine, bundle, srgb);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_jni_KTX1Loader_nCreateIndirectLight(JNIEnv* env, jclass, jlong nativeEngine, jlong nativeTexture, jfloatArray sh) {
    Engine* engine = (Engine*) nativeEngine;
    Texture* cubemap = (Texture*) nativeTexture;
    jfloat* harmonics = env->GetFloatArrayElements(sh, nullptr);
    
    IndirectLight* indirectLight = IndirectLight::Builder()
            .reflections(cubemap)
            .irradiance(3, reinterpret_cast<const float3*>(harmonics))
            .build(*engine);
            
    env->ReleaseFloatArrayElements(sh, harmonics, JNI_ABORT);
    return (jlong) indirectLight;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_jni_KTX1Loader_nCreateSkybox(JNIEnv* env, jclass, jlong nativeEngine, jlong nativeTexture) {
    Engine* engine = (Engine*) nativeEngine;
    Texture* cubemap = (Texture*) nativeTexture;
    return (jlong) Skybox::Builder().environment(cubemap).build(*engine);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_utils_jni_KTX1Loader_nGetSphericalHarmonics(JNIEnv* env, jclass, jobject javaBuffer, jint remaining, jfloatArray outSh) {
    AutoBuffer buffer(env, javaBuffer, remaining);
    Ktx1Bundle bundle((const uint8_t*) buffer.getData(), buffer.getSize());

    jfloat* sh = env->GetFloatArrayElements(outSh, nullptr);
    bool success = bundle.getSphericalHarmonics(reinterpret_cast<float3*>(sh));
    env->ReleaseFloatArrayElements(outSh, sh, 0);

    return success ? JNI_TRUE : JNI_FALSE;
}
