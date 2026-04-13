#include <jni.h>
#include <utils/EntityManager.h>
#include <utils/Entity.h>

using namespace utils;

extern "C" {

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_EntityManager_nGetEntityManager(JNIEnv* env, jclass clazz) {
    return (jlong) &EntityManager::get();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_EntityManager_nCreate(JNIEnv* env, jclass clazz, jlong nativeEntityManager) {
    EntityManager* em = (EntityManager*) nativeEntityManager;
    return (jint) em->create().id();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_EntityManager_nDestroy(JNIEnv* env, jclass clazz, jlong nativeEntityManager, jint entity) {
    EntityManager* em = (EntityManager*) nativeEntityManager;
    em->destroy(Entity::import(entity));
}

} // extern "C"
