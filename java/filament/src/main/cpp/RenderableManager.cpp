#include <jni.h>
#include <filament/RenderableManager.h>
#include <filament/Engine.h>
#include <filament/VertexBuffer.h>
#include <filament/IndexBuffer.h>
#include <filament/MaterialInstance.h>
#include <filament/Box.h>
#include <filament/MorphTargetBuffer.h>
#include <filament/SkinningBuffer.h>
#include <utils/Entity.h>

using namespace filament;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_RenderableManager_00024Builder_nCreateBuilder(JNIEnv* env, jclass, jint count) {
    return (jlong) new RenderableManager::Builder((size_t) count);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_RenderableManager_00024Builder_nDestroyBuilder(JNIEnv* env, jclass, jlong nativeBuilder) {
    delete (RenderableManager::Builder*) nativeBuilder;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_RenderableManager_00024Builder_nBuilderGeometry(JNIEnv* env, jclass, jlong nativeBuilder, jint index, jint type, jlong nativeVb, jlong nativeIb) {
    ((RenderableManager::Builder*) nativeBuilder)->geometry((size_t) index, (RenderableManager::PrimitiveType) type, (VertexBuffer*) nativeVb, (IndexBuffer*) nativeIb);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_RenderableManager_00024Builder_nBuilderBoundingBox(JNIEnv* env, jclass, jlong nativeBuilder, jfloat cx, jfloat cy, jfloat cz, jfloat ex, jfloat ey, jfloat ez) {
    ((RenderableManager::Builder*) nativeBuilder)->boundingBox({ {cx, cy, cz}, {ex, ey, ez} });
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_RenderableManager_00024Builder_nBuilderMaterial(JNIEnv* env, jclass, jlong nativeBuilder, jint index, jlong nativeMi) {
    ((RenderableManager::Builder*) nativeBuilder)->material((size_t) index, (MaterialInstance*) nativeMi);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_RenderableManager_00024Builder_nBuilderBuild(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeEngine, jint entity) {
    ((RenderableManager::Builder*) nativeBuilder)->build(*(Engine*) nativeEngine, (utils::Entity&) entity);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_RenderableManager_nHasComponent(JNIEnv* env, jclass, jlong nativeRenderableManager, jint entity) {
    return (jboolean) ((RenderableManager*) nativeRenderableManager)->hasComponent(utils::Entity::import(entity));
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_RenderableManager_nSetLayerMask(JNIEnv* env, jclass, jlong nativeManager, jint instance, jint select, jint values) {
    ((RenderableManager*) nativeManager)->setLayerMask((RenderableManager::Instance) instance, (uint8_t) select, (uint8_t) values);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_RenderableManager_nSetMorphTargetBuffer(JNIEnv* env, jclass, jlong nativeManager, jint instance, jlong nativeBuffer) {
    ((RenderableManager*) nativeManager)->setMorphTargetBuffer((RenderableManager::Instance) instance, (MorphTargetBuffer*) nativeBuffer);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_RenderableManager_nSetSkinningBuffer(JNIEnv* env, jclass, jlong nativeManager, jint instance, jlong nativeBuffer, jint count, jint offset) {
    ((RenderableManager*) nativeManager)->setSkinningBuffer((RenderableManager::Instance) instance, (SkinningBuffer*) nativeBuffer, (size_t) count, (size_t) offset);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_RenderableManager_nDestroy(JNIEnv* env, jclass, jlong nativeRenderableManager, jint entity) {
    ((RenderableManager*) nativeRenderableManager)->destroy((utils::Entity&) entity);
}
