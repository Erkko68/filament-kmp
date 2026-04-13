#include <jni.h>
#include <gltfio/FilamentInstance.h>
#include <gltfio/Animator.h>
#include <filament/MaterialInstance.h>
#include <utils/Entity.h>
#include <vector>

using namespace filament;
using namespace gltfio;
using namespace utils;

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentInstance_nGetRoot(JNIEnv* env, jclass, jlong nativeInstance) {
    return Entity::smuggle(((FilamentInstance*) nativeInstance)->getRoot());
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentInstance_nGetEntityCount(JNIEnv* env, jclass, jlong nativeInstance) {
    return (jint) ((FilamentInstance*) nativeInstance)->getEntityCount();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentInstance_nGetEntities(JNIEnv* env, jclass, jlong nativeInstance, jintArray result) {
    jint* entities = env->GetIntArrayElements(result, nullptr);
    const Entity* assetEntities = ((FilamentInstance*) nativeInstance)->getEntities();
    size_t count = ((FilamentInstance*) nativeInstance)->getEntityCount();
    for (size_t i = 0; i < count; i++) {
        entities[i] = Entity::smuggle(assetEntities[i]);
    }
    env->ReleaseIntArrayElements(result, entities, 0);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentInstance_nGetAnimator(JNIEnv* env, jclass, jlong nativeInstance) {
    return (jlong) ((FilamentInstance*) nativeInstance)->getAnimator();
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentInstance_nGetMaterialInstanceCount(JNIEnv* env, jclass, jlong nativeInstance) {
    return (jint) ((FilamentInstance*) nativeInstance)->getMaterialInstanceCount();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentInstance_nGetMaterialInstances(JNIEnv* env, jclass, jlong nativeInstance, jlongArray nativeResults) {
    FilamentInstance* instance = (FilamentInstance*) nativeInstance;
    size_t count = instance->getMaterialInstanceCount();
    MaterialInstance* const* materialInstances = instance->getMaterialInstances();
    
    jlong* data = env->GetLongArrayElements(nativeResults, nullptr);
    for (size_t i = 0; i < count; i++) {
        data[i] = (jlong) materialInstances[i];
    }
    env->ReleaseLongArrayElements(nativeResults, data, 0);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentInstance_nApplyMaterialVariant(JNIEnv* env, jclass, jlong nativeInstance, jint variantIndex) {
    ((FilamentInstance*) nativeInstance)->applyMaterialVariant((size_t) variantIndex);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentInstance_nGetMaterialVariantCount(JNIEnv* env, jclass, jlong nativeInstance) {
    return (jint) ((FilamentInstance*) nativeInstance)->getMaterialVariantCount();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentInstance_nGetMaterialVariantNames(JNIEnv* env, jclass, jlong nativeInstance, jobjectArray result) {
    FilamentInstance* instance = (FilamentInstance*) nativeInstance;
    size_t count = instance->getMaterialVariantCount();
    for (size_t i = 0; i < count; i++) {
        env->SetObjectArrayElement(result, (jsize) i, env->NewStringUTF(instance->getMaterialVariantName(i)));
    }
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentInstance_nGetJointsAt(JNIEnv* env, jclass, jlong nativeInstance, jint skinIndex, jintArray result) {
    FilamentInstance* instance = (FilamentInstance*) nativeInstance;
    const Entity* joints = instance->getJointsAt((size_t) skinIndex);
    size_t count = instance->getJointCountAt((size_t) skinIndex);
    jint* data = env->GetIntArrayElements(result, nullptr);
    for (size_t i = 0; i < count; i++) {
        data[i] = Entity::smuggle(joints[i]);
    }
    env->ReleaseIntArrayElements(result, data, 0);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentInstance_nGetSkinCount(JNIEnv* env, jclass, jlong nativeInstance) {
    return (jint) ((FilamentInstance*) nativeInstance)->getSkinCount();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentInstance_nGetSkinNames(JNIEnv* env, jclass, jlong nativeInstance, jobjectArray result) {
    FilamentInstance* instance = (FilamentInstance*) nativeInstance;
    size_t count = instance->getSkinCount();
    for (size_t i = 0; i < count; i++) {
        env->SetObjectArrayElement(result, (jsize) i, env->NewStringUTF(instance->getSkinNameAt(i)));
    }
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentInstance_nGetJointCountAt(JNIEnv* env, jclass, jlong nativeInstance, jint skinIndex) {
    return (jint) ((FilamentInstance*) nativeInstance)->getJointCountAt((size_t) skinIndex);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentInstance_nAttachSkin(JNIEnv* env, jclass, jlong nativeInstance, jint skinIndex, jint entity) {
    ((FilamentInstance*) nativeInstance)->attachSkin((size_t) skinIndex, Entity::import(entity));
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentInstance_nDetachSkin(JNIEnv* env, jclass, jlong nativeInstance, jint skinIndex, jint entity) {
    ((FilamentInstance*) nativeInstance)->detachSkin((size_t) skinIndex, Entity::import(entity));
}
