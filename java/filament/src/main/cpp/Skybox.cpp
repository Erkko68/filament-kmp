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

#include <filament/Skybox.h>
#include <filament/Engine.h>
#include <filament/Texture.h>

#include <math/vec4.h>

using namespace filament;

extern "C" {

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Skybox_nCreateBuilder(JNIEnv*, jclass) {
    return (jlong) new Skybox::Builder();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Skybox_nDestroyBuilder(JNIEnv*, jclass, jlong nativeSkyBoxBuilder) {
    Skybox::Builder* builder = (Skybox::Builder*) nativeSkyBoxBuilder;
    delete builder;
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Skybox_nBuilderEnvironment(JNIEnv*, jclass, jlong nativeSkyBoxBuilder, jlong nativeTexture) {
    Skybox::Builder* builder = (Skybox::Builder*) nativeSkyBoxBuilder;
    Texture* texture = (Texture*) nativeTexture;
    builder->environment(texture);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Skybox_nBuilderShowSun(JNIEnv*, jclass, jlong nativeSkyBoxBuilder, jboolean show) {
    Skybox::Builder* builder = (Skybox::Builder*) nativeSkyBoxBuilder;
    builder->showSun(show);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Skybox_nBuilderIntensity(JNIEnv*, jclass, jlong nativeSkyBoxBuilder, jfloat intensity) {
    Skybox::Builder* builder = (Skybox::Builder*) nativeSkyBoxBuilder;
    builder->intensity(intensity);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Skybox_nBuilderColor(JNIEnv*, jclass, jlong nativeSkyBoxBuilder, jfloat r, jfloat g, jfloat b, jfloat a) {
    Skybox::Builder* builder = (Skybox::Builder*) nativeSkyBoxBuilder;
    builder->color({r, g, b, a});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Skybox_nBuilderPriority(JNIEnv*, jclass, jlong nativeSkyBoxBuilder, jint priority) {
    Skybox::Builder* builder = (Skybox::Builder*) nativeSkyBoxBuilder;
    builder->priority(uint8_t(priority));
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Skybox_nBuilderBuild(JNIEnv*, jclass, jlong nativeSkyBoxBuilder, jlong nativeEngine) {
    Skybox::Builder* builder = (Skybox::Builder*) nativeSkyBoxBuilder;
    Engine* engine = (Engine*) nativeEngine;
    return (jlong) builder->build(*engine);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Skybox_nSetLayerMask(JNIEnv*, jclass, jlong nativeSkybox, jint select, jint value) {
    Skybox* skybox = (Skybox*) nativeSkybox;
    skybox->setLayerMask((uint8_t) select, (uint8_t) value);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Skybox_nGetLayerMask(JNIEnv*, jclass, jlong nativeSkybox) {
    Skybox* skybox = (Skybox*) nativeSkybox;
    return static_cast<jint>(skybox->getLayerMask());
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_Skybox_nGetIntensity(JNIEnv*, jclass, jlong nativeSkybox) {
    Skybox* skybox = (Skybox*) nativeSkybox;
    return static_cast<jfloat>(skybox->getIntensity());
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Skybox_nSetColor(JNIEnv*, jclass, jlong nativeSkybox, jfloat r, jfloat g, jfloat b, jfloat a) {
    Skybox* skybox = (Skybox*) nativeSkybox;
    skybox->setColor({r, g, b, a});
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Skybox_nGetTexture(JNIEnv*, jclass, jlong nativeSkybox) {
    Skybox* skybox = (Skybox*) nativeSkybox;
    Texture const* tex = skybox->getTexture();
    return (jlong) tex;
}

} // extern "C"
