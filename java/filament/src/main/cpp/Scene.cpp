#include <jni.h>
#include <filament/Scene.h>
#include <utils/Entity.h>

using namespace filament;
using namespace utils;

extern "C" {

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Scene_nAddEntity(JNIEnv* env, jclass clazz, jlong nativeScene, jint entity) {
    ((Scene*) nativeScene)->addEntity(Entity::import(entity));
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Scene_nRemoveEntity(JNIEnv* env, jclass clazz, jlong nativeScene, jint entity) {
    ((Scene*) nativeScene)->removeEntity(Entity::import(entity));
}

} // extern "C"
