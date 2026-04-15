/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <jni.h>

#include <filament/Scene.h>
#include <filament/IndirectLight.h>
#include <filament/Skybox.h>
#include <utils/Entity.h>

using namespace filament;
using namespace utils;

extern "C" {

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Scene_nSetSkybox(JNIEnv* env, jclass, jlong nativeScene, jlong nativeSkybox) {
    Scene* scene = (Scene*) nativeScene;
    Skybox* skybox = (Skybox*) nativeSkybox;
    scene->setSkybox(skybox);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Scene_nSetIndirectLight(JNIEnv* env, jclass, jlong nativeScene, jlong nativeIndirectLight) {
    Scene* scene = (Scene*) nativeScene;
    IndirectLight* indirectLight = (IndirectLight*) nativeIndirectLight;
    scene->setIndirectLight(indirectLight);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Scene_nAddEntity(JNIEnv* env, jclass, jlong nativeScene, jint entity) {
    Scene* scene = (Scene*) nativeScene;
    scene->addEntity((Entity&) entity);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Scene_nAddEntities(JNIEnv* env, jclass, jlong nativeScene, jintArray entities) {
    Scene* scene = (Scene*) nativeScene;
    jint* nativeEntities = env->GetIntArrayElements(entities, nullptr);
    scene->addEntities((Entity*) nativeEntities, env->GetArrayLength(entities));
    env->ReleaseIntArrayElements(entities, nativeEntities, JNI_ABORT);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Scene_nRemove(JNIEnv* env, jclass, jlong nativeScene, jint entity) {
    Scene* scene = (Scene*) nativeScene;
    scene->remove((Entity&) entity);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Scene_nRemoveEntities(JNIEnv* env, jclass, jlong nativeScene, jintArray entities) {
    Scene* scene = (Scene*) nativeScene;
    jint* nativeEntities = env->GetIntArrayElements(entities, nullptr);
    scene->removeEntities((Entity*) nativeEntities, env->GetArrayLength(entities));
    env->ReleaseIntArrayElements(entities, nativeEntities, JNI_ABORT);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_Scene_nGetEntityCount(JNIEnv* env, jclass, jlong nativeScene) {
    Scene* scene = (Scene*) nativeScene;
    return (jint) scene->getEntityCount();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_Scene_nGetRenderableCount(JNIEnv* env, jclass, jlong nativeScene) {
    Scene* scene = (Scene*) nativeScene;
    return (jint) scene->getRenderableCount();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_Scene_nGetLightCount(JNIEnv* env, jclass, jlong nativeScene) {
    Scene* scene = (Scene*) nativeScene;
    return (jint) scene->getLightCount();
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Scene_nHasEntity(JNIEnv* env, jclass, jlong nativeScene, jint entityId) {
    Scene* scene = (Scene*) nativeScene;
    Entity entity = Entity::import(entityId);
    return (jboolean) scene->hasEntity(entity);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Scene_nGetEntities(JNIEnv* env, jclass, jlong nativeScene, jintArray outArray, jint length) {
    Scene const* const scene = (Scene*) nativeScene;
    if (length < scene->getEntityCount()) {
        return JNI_FALSE;
    }
    jint* out = env->GetIntArrayElements(outArray, nullptr);
    scene->forEach([out, length, i = 0](Entity entity) mutable {
        if (i < length) {
            out[i++] = (jint) entity.getId();
        }
    });
    env->ReleaseIntArrayElements(outArray, out, 0);
    return JNI_TRUE;
}

} // extern "C"
