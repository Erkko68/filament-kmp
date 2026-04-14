#include <jni.h>
#include <filament/RenderTarget.h>
#include <filament/Engine.h>
#include <filament/Texture.h>

using namespace filament;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_RenderTarget_nCreateBuilder(JNIEnv* env, jclass) {
    return (jlong) new RenderTarget::Builder();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_RenderTarget_nDestroyBuilder(JNIEnv* env, jclass, jlong nativeBuilder) {
    delete (RenderTarget::Builder*) nativeBuilder;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_RenderTarget_nBuilderTexture(JNIEnv* env, jclass, jlong nativeBuilder, jint attachment, jlong nativeTexture) {
    ((RenderTarget::Builder*) nativeBuilder)->texture((RenderTarget::AttachmentPoint) attachment, (Texture*) nativeTexture);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_RenderTarget_nBuilderMipLevel(JNIEnv* env, jclass, jlong nativeBuilder, jint attachment, jint level) {
    ((RenderTarget::Builder*) nativeBuilder)->mipLevel((RenderTarget::AttachmentPoint) attachment, (uint8_t) level);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_RenderTarget_nBuilderFace(JNIEnv* env, jclass, jlong nativeBuilder, jint attachment, jint face) {
    ((RenderTarget::Builder*) nativeBuilder)->face((RenderTarget::AttachmentPoint) attachment, (Texture::CubemapFace) face);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_RenderTarget_nBuilderLayer(JNIEnv* env, jclass, jlong nativeBuilder, jint attachment, jint layer) {
    ((RenderTarget::Builder*) nativeBuilder)->layer((RenderTarget::AttachmentPoint) attachment, (uint32_t) layer);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_RenderTarget_nBuilderBuild(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeEngine) {
    return (jlong) ((RenderTarget::Builder*) nativeBuilder)->build(*(Engine*) nativeEngine);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_RenderTarget_nGetMipLevel(JNIEnv* env, jclass, jlong nativeTarget, jint attachment) {
    return (jint) ((RenderTarget*) nativeTarget)->getMipLevel((RenderTarget::AttachmentPoint) attachment);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_RenderTarget_nGetFace(JNIEnv* env, jclass, jlong nativeTarget, jint attachment) {
    return (jint) ((RenderTarget*) nativeTarget)->getFace((RenderTarget::AttachmentPoint) attachment);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_RenderTarget_nGetLayer(JNIEnv* env, jclass, jlong nativeTarget, jint attachment) {
    return (jint) ((RenderTarget*) nativeTarget)->getLayer((RenderTarget::AttachmentPoint) attachment);
}
