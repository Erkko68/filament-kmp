#include <jni.h>
#include <filament/Engine.h>
#include <filament/Texture.h>
#include <filament-iblprefilter/IBLPrefilterContext.h>

using namespace filament;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_IBLPrefilterContext_nCreate(JNIEnv* env, jclass, jlong nativeEngine) {
    return (jlong) new IBLPrefilterContext(*(Engine*) nativeEngine);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_IBLPrefilterContext_nDestroy(JNIEnv* env, jclass, jlong nativeObject) {
    delete (IBLPrefilterContext*) nativeObject;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_IBLPrefilterContext_nCreateEquirectHelper(JNIEnv* env, jclass, jlong nativeContext) {
    return (jlong) new IBLPrefilterContext::EquirectangularToCubemap(*(IBLPrefilterContext*) nativeContext);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_IBLPrefilterContext_nEquirectHelperRun(JNIEnv* env, jclass, jlong nativeHelper, jlong nativeEquirect) {
    return (jlong) (*(IBLPrefilterContext::EquirectangularToCubemap*) nativeHelper)((Texture*) nativeEquirect);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_IBLPrefilterContext_nDestroyEquirectHelper(JNIEnv* env, jclass, jlong nativeObject) {
    delete (IBLPrefilterContext::EquirectangularToCubemap*) nativeObject;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_IBLPrefilterContext_nCreateSpecularFilter(JNIEnv* env, jclass, jlong nativeContext) {
    return (jlong) new IBLPrefilterContext::SpecularFilter(*(IBLPrefilterContext*) nativeContext);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_IBLPrefilterContext_nSpecularFilterRun(JNIEnv* env, jclass, jlong nativeHelper, jlong nativeSkybox) {
    return (jlong) (*(IBLPrefilterContext::SpecularFilter*) nativeHelper)((Texture*) nativeSkybox);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_IBLPrefilterContext_nDestroySpecularFilter(JNIEnv* env, jclass, jlong nativeObject) {
    delete (IBLPrefilterContext::SpecularFilter*) nativeObject;
}
