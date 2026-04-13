#include <jni.h>
#include <filament/Stream.h>
#include <filament/Engine.h>

using namespace filament;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Stream_00024Builder_nCreateBuilder(JNIEnv* env, jclass) {
    return (jlong) new Stream::Builder();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Stream_00024Builder_nDestroyBuilder(JNIEnv* env, jclass, jlong nativeBuilder) {
    delete (Stream::Builder*) nativeBuilder;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Stream_00024Builder_nBuilderStream(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeTexture) {
    ((Stream::Builder*) nativeBuilder)->stream((void*) nativeTexture);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Stream_00024Builder_nBuilderWidth(JNIEnv* env, jclass, jlong nativeBuilder, jint width) {
    ((Stream::Builder*) nativeBuilder)->width((uint32_t) width);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Stream_00024Builder_nBuilderHeight(JNIEnv* env, jclass, jlong nativeBuilder, jint height) {
    ((Stream::Builder*) nativeBuilder)->height((uint32_t) height);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Stream_00024Builder_nBuilderBuild(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeEngine) {
    return (jlong) ((Stream::Builder*) nativeBuilder)->build(*(Engine*) nativeEngine);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Stream_nGetStreamType(JNIEnv* env, jclass, jlong nativeStream) {
    return (jint) ((Stream*) nativeStream)->getStreamType();
}
