#include <jni.h>
#include <utils/EntityManager.h>
#include <utils/Entity.h>

using namespace utils;

extern "C" {

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_EntityManager_nGetEntityManager(JNIEnv* env, jclass clazz) {
    return (jlong) &EntityManager::get();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_EntityManager_nCreate(JNIEnv* env, jclass clazz, jlong nativeEntityManager) {
    EntityManager* em = (EntityManager*) nativeEntityManager;
    return (jint) em->create().getId();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_EntityManager_nDestroy(JNIEnv* env, jclass clazz, jlong nativeEntityManager, jint entity) {
    EntityManager* em = (EntityManager*) nativeEntityManager;
    em->destroy(Entity::import(entity));
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_EntityManager_nIsAlive(JNIEnv* env, jclass clazz, jlong nativeEntityManager, jint entity) {
    EntityManager* em = (EntityManager*) nativeEntityManager;
    return (jboolean) em->isAlive(Entity::import(entity));
}

} // extern "C"
