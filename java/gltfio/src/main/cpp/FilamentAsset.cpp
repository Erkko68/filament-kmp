#include <jni.h>
#include <gltfio/FilamentAsset.h>
#include <gltfio/FilamentInstance.h>
#include <filament/Box.h>
#include <utils/Entity.h>
#include <math/vec3.h>
#include <string>
#include <vector>

using namespace filament;
using namespace filament::math;
using namespace gltfio;
using namespace utils;

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nGetRoot(JNIEnv* env, jclass, jlong nativeAsset) {
    return Entity::smuggle(((FilamentAsset*) nativeAsset)->getRoot());
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nPopRenderable(JNIEnv* env, jclass, jlong nativeAsset) {
    return Entity::smuggle(((FilamentAsset*) nativeAsset)->popRenderable());
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nPopRenderables(JNIEnv* env, jclass, jlong nativeAsset, jintArray result) {
    FilamentAsset* asset = (FilamentAsset*) nativeAsset;
    if (result == nullptr) {
        return (jint) asset->getRenderableEntityCount();
    }
    jsize count = env->GetArrayLength(result);
    jint* entities = env->GetIntArrayElements(result, nullptr);
    size_t popped = asset->popRenderables((Entity*) entities, (size_t) count);
    env->ReleaseIntArrayElements(result, entities, 0);
    return (jint) popped;
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nGetEntityCount(JNIEnv* env, jclass, jlong nativeAsset) {
    return (jint) ((FilamentAsset*) nativeAsset)->getEntityCount();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nGetEntities(JNIEnv* env, jclass, jlong nativeAsset, jintArray result) {
    jint* entities = env->GetIntArrayElements(result, nullptr);
    const Entity* assetEntities = ((FilamentAsset*) nativeAsset)->getEntities();
    size_t count = ((FilamentAsset*) nativeAsset)->getEntityCount();
    for (size_t i = 0; i < count; i++) {
        entities[i] = Entity::smuggle(assetEntities[i]);
    }
    env->ReleaseIntArrayElements(result, entities, 0);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nGetFirstEntityByName(JNIEnv* env, jclass, jlong nativeAsset, jstring name) {
    const char* nativeName = env->GetStringUTFChars(name, nullptr);
    Entity entity = ((FilamentAsset*) nativeAsset)->getFirstEntityByName(nativeName);
    env->ReleaseStringUTFChars(name, nativeName);
    return Entity::smuggle(entity);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nGetEntitiesByName(JNIEnv* env, jclass, jlong nativeAsset, jstring name, jintArray result) {
    const char* nativeName = env->GetStringUTFChars(name, nullptr);
    const FilamentAsset* asset = (const FilamentAsset*) nativeAsset;
    if (result == nullptr) {
        size_t count = asset->getEntitiesByName(nativeName, nullptr, 0);
        env->ReleaseStringUTFChars(name, nativeName);
        return (jint) count;
    }
    jsize count = env->GetArrayLength(result);
    jint* entities = env->GetIntArrayElements(result, nullptr);
    size_t found = asset->getEntitiesByName(nativeName, (Entity*) entities, (size_t) count);
    env->ReleaseIntArrayElements(result, entities, 0);
    env->ReleaseStringUTFChars(name, nativeName);
    return (jint) found;
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nGetEntitiesByPrefix(JNIEnv* env, jclass, jlong nativeAsset, jstring prefix, jintArray result) {
    const char* nativePrefix = env->GetStringUTFChars(prefix, nullptr);
    const FilamentAsset* asset = (const FilamentAsset*) nativeAsset;
    if (result == nullptr) {
        size_t count = asset->getEntitiesByPrefix(nativePrefix, nullptr, 0);
        env->ReleaseStringUTFChars(prefix, nativePrefix);
        return (jint) count;
    }
    jsize count = env->GetArrayLength(result);
    jint* entities = env->GetIntArrayElements(result, nullptr);
    size_t found = asset->getEntitiesByPrefix(nativePrefix, (Entity*) entities, (size_t) count);
    env->ReleaseIntArrayElements(result, entities, 0);
    env->ReleaseStringUTFChars(prefix, nativePrefix);
    return (jint) found;
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nGetLightEntityCount(JNIEnv* env, jclass, jlong nativeAsset) {
    return (jint) ((FilamentAsset*) nativeAsset)->getLightEntityCount();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nGetLightEntities(JNIEnv* env, jclass, jlong nativeAsset, jintArray result) {
    jint* entities = env->GetIntArrayElements(result, nullptr);
    const Entity* assetEntities = ((FilamentAsset*) nativeAsset)->getLightEntities();
    size_t count = ((FilamentAsset*) nativeAsset)->getLightEntityCount();
    for (size_t i = 0; i < count; i++) {
        entities[i] = Entity::smuggle(assetEntities[i]);
    }
    env->ReleaseIntArrayElements(result, entities, 0);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nGetRenderableEntityCount(JNIEnv* env, jclass, jlong nativeAsset) {
    return (jint) ((FilamentAsset*) nativeAsset)->getRenderableEntityCount();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nGetRenderableEntities(JNIEnv* env, jclass, jlong nativeAsset, jintArray result) {
    jint* entities = env->GetIntArrayElements(result, nullptr);
    const Entity* assetEntities = ((FilamentAsset*) nativeAsset)->getRenderableEntities();
    size_t count = ((FilamentAsset*) nativeAsset)->getRenderableEntityCount();
    for (size_t i = 0; i < count; i++) {
        entities[i] = Entity::smuggle(assetEntities[i]);
    }
    env->ReleaseIntArrayElements(result, entities, 0);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nGetCameraEntityCount(JNIEnv* env, jclass, jlong nativeAsset) {
    return (jint) ((FilamentAsset*) nativeAsset)->getCameraEntityCount();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nGetCameraEntities(JNIEnv* env, jclass, jlong nativeAsset, jintArray result) {
    jint* entities = env->GetIntArrayElements(result, nullptr);
    const Entity* assetEntities = ((FilamentAsset*) nativeAsset)->getCameraEntities();
    size_t count = ((FilamentAsset*) nativeAsset)->getCameraEntityCount();
    for (size_t i = 0; i < count; i++) {
        entities[i] = Entity::smuggle(assetEntities[i]);
    }
    env->ReleaseIntArrayElements(result, entities, 0);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nGetMorphTargetCount(JNIEnv* env, jclass, jlong nativeAsset, jint entity) {
    return (jint) ((FilamentAsset*) nativeAsset)->getMorphTargetCountAt(Entity::import(entity));
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nGetMorphTargetNames(JNIEnv* env, jclass, jlong nativeAsset, jint entity, jobjectArray result) {
    const FilamentAsset* asset = (const FilamentAsset*) nativeAsset;
    Entity e = Entity::import(entity);
    size_t count = asset->getMorphTargetCountAt(e);
    for (size_t i = 0; i < count; i++) {
        const char* name = asset->getMorphTargetNameAt(e, i);
        env->SetObjectArrayElement(result, (jsize) i, env->NewStringUTF(name));
    }
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nGetBoundingBox(JNIEnv* env, jclass, jlong nativeAsset, jfloatArray box) {
    Aabb aabb = ((FilamentAsset*) nativeAsset)->getBoundingBox();
    float3 center = (aabb.max + aabb.min) * 0.5f;
    float3 halfExtent = (aabb.max - aabb.min) * 0.5f;
    
    jfloat* data = env->GetFloatArrayElements(box, nullptr);
    data[0] = center.x;
    data[1] = center.y;
    data[2] = center.z;
    data[3] = halfExtent.x;
    data[4] = halfExtent.y;
    data[5] = halfExtent.z;
    env->ReleaseFloatArrayElements(box, data, 0);
}

extern "C" JNIEXPORT jstring JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nGetName(JNIEnv* env, jclass, jlong nativeAsset, jint entity) {
    return env->NewStringUTF(((FilamentAsset*) nativeAsset)->getName(Entity::import(entity)));
}

extern "C" JNIEXPORT jstring JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nGetExtras(JNIEnv* env, jclass, jlong nativeAsset, jint entity) {
    const char* extras = ((FilamentAsset*) nativeAsset)->getExtras(Entity::import(entity));
    return extras ? env->NewStringUTF(extras) : nullptr;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nGetInstance(JNIEnv* env, jclass, jlong nativeAsset) {
    return (jlong) ((FilamentAsset*) nativeAsset)->getInstance();
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nGetResourceUriCount(JNIEnv* env, jclass, jlong nativeAsset) {
    return (jint) ((FilamentAsset*) nativeAsset)->getResourceUriCount();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nGetResourceUris(JNIEnv* env, jclass, jlong nativeAsset, jobjectArray result) {
    const FilamentAsset* asset = (const FilamentAsset*) nativeAsset;
    size_t count = asset->getResourceUriCount();
    const char* const* uris = asset->getResourceUris();
    for (size_t i = 0; i < count; i++) {
        env->SetObjectArrayElement(result, (jsize) i, env->NewStringUTF(uris[i]));
    }
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_FilamentAsset_nReleaseSourceData(JNIEnv* env, jclass, jlong nativeAsset) {
    ((FilamentAsset*) nativeAsset)->releaseSourceData();
}
