#include <jni.h>
#include <filament/Color.h>
#include <math/vec3.h>

using namespace filament;

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Colors_nCct(JNIEnv* env, jclass, jfloat temperature, jfloatArray color) {
    math::float3 rgb = Color::cct(temperature);
    env->SetFloatArrayRegion(color, 0, 3, (jfloat*) &rgb);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Colors_nIlluminantD(JNIEnv* env, jclass, jfloat temperature, jfloatArray color) {
    math::float3 rgb = Color::illuminantD(temperature);
    env->SetFloatArrayRegion(color, 0, 3, (jfloat*) &rgb);
}
