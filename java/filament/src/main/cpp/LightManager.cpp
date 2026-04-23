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
#include <filament/LightManager.h>
#include <filament/Engine.h>
#include <utils/Entity.h>
#include <math/vec4.h>
#include <math/vec3.h>

#include <algorithm>

using namespace filament;
using namespace utils;

extern "C" {

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nGetComponentCount(JNIEnv*, jclass, jlong nativeLightManager) {
    LightManager* lm = (LightManager*) nativeLightManager;
    return (jint) lm->getComponentCount();
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nHasComponent(JNIEnv*, jclass, jlong nativeLightManager, jint entity) {
    LightManager* lm = (LightManager*) nativeLightManager;
    return (jboolean) lm->hasComponent((Entity&) entity);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nGetInstance(JNIEnv*, jclass, jlong nativeLightManager, jint entity) {
    LightManager* lm = (LightManager*) nativeLightManager;
    return (jint) lm->getInstance((Entity&) entity);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nDestroy(JNIEnv*, jclass, jlong nativeLightManager, jint entity) {
    LightManager* lm = (LightManager*) nativeLightManager;
    lm->destroy((Entity&) entity);
}

static void fillShadowOptions(JNIEnv* env, LightManager::ShadowOptions& shadowOptions, jint mapSize, jint cascades, jfloatArray splitPositions, jfloat constantBias, jfloat normalBias, jfloat shadowFar, jfloat shadowNearHint, jfloat shadowFarHint, jboolean stable, jboolean lispsm, jfloat polygonOffsetConstant, jfloat polygonOffsetSlope, jboolean screenSpaceContactShadows, jint stepCount, jfloat maxShadowDistance, jboolean elvsm, jfloat blurWidth, jfloat shadowBulbRadius, jfloatArray transform) {
    shadowOptions.mapSize = (uint32_t) mapSize;
    shadowOptions.shadowCascades = (uint8_t) cascades;
    shadowOptions.constantBias = constantBias;
    shadowOptions.normalBias = normalBias;
    shadowOptions.shadowFar = shadowFar;
    shadowOptions.shadowNearHint = shadowNearHint;
    shadowOptions.shadowFarHint = shadowFarHint;
    shadowOptions.stable = (bool) stable;
    shadowOptions.lispsm = (bool) lispsm;
    shadowOptions.polygonOffsetConstant = polygonOffsetConstant;
    shadowOptions.polygonOffsetSlope = polygonOffsetSlope;
    shadowOptions.screenSpaceContactShadows = (bool) screenSpaceContactShadows;
    shadowOptions.stepCount = (uint8_t) stepCount;
    shadowOptions.maxShadowDistance = maxShadowDistance;
    shadowOptions.vsm.elvsm = (bool) elvsm;
    shadowOptions.vsm.blurWidth = blurWidth;
    shadowOptions.shadowBulbRadius = shadowBulbRadius;

    jfloat* nativeSplits = env->GetFloatArrayElements(splitPositions, NULL);
    const jsize splitCount = std::min((jsize) 3, env->GetArrayLength(splitPositions));
    std::copy_n(nativeSplits, splitCount, shadowOptions.cascadeSplitPositions);
    env->ReleaseFloatArrayElements(splitPositions, nativeSplits, JNI_ABORT);

    jfloat* nativeTransform = env->GetFloatArrayElements(transform, NULL);
    const jsize transformCount = std::min((jsize) 4, env->GetArrayLength(transform));
    std::copy_n(nativeTransform, transformCount, shadowOptions.transform.xyzw.v);
    env->ReleaseFloatArrayElements(transform, nativeTransform, JNI_ABORT);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nCreateBuilder(JNIEnv*, jclass, jint lightType) {
    return (jlong) new LightManager::Builder((LightManager::Type) lightType);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nDestroyBuilder(JNIEnv*, jclass, jlong nativeBuilder) {
    delete (LightManager::Builder*) nativeBuilder;
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nBuilderLightChannel(JNIEnv*, jclass, jlong nativeBuilder, jint channel, jboolean enable) {
    ((LightManager::Builder*) nativeBuilder)->lightChannel((uint32_t) channel, (bool) enable);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nBuilderCastShadows(JNIEnv*, jclass, jlong nativeBuilder, jboolean enabled) {
    ((LightManager::Builder*) nativeBuilder)->castShadows((bool) enabled);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nBuilderShadowOptions(JNIEnv* env, jclass, jlong nativeBuilder, jint mapSize, jint cascades, jfloatArray splitPositions, jfloat constantBias, jfloat normalBias, jfloat shadowFar, jfloat shadowNearHint, jfloat shadowFarHint, jboolean stable, jboolean lispsm, jfloat polygonOffsetConstant, jfloat polygonOffsetSlope, jboolean screenSpaceContactShadows, jint stepCount, jfloat maxShadowDistance, jboolean elvsm, jfloat blurWidth, jfloat shadowBulbRadius, jfloatArray transform) {
    LightManager::ShadowOptions options;
    fillShadowOptions(env, options, mapSize, cascades, splitPositions, constantBias, normalBias, shadowFar, shadowNearHint, shadowFarHint, stable, lispsm, polygonOffsetConstant, polygonOffsetSlope, screenSpaceContactShadows, stepCount, maxShadowDistance, elvsm, blurWidth, shadowBulbRadius, transform);
    ((LightManager::Builder*) nativeBuilder)->shadowOptions(options);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nBuilderCastLight(JNIEnv*, jclass, jlong nativeBuilder, jboolean enabled) {
    ((LightManager::Builder*) nativeBuilder)->castLight((bool) enabled);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nBuilderPosition(JNIEnv*, jclass, jlong nativeBuilder, jfloat x, jfloat y, jfloat z) {
    ((LightManager::Builder*) nativeBuilder)->position({x, y, z});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nBuilderDirection(JNIEnv*, jclass, jlong nativeBuilder, jfloat x, jfloat y, jfloat z) {
    ((LightManager::Builder*) nativeBuilder)->direction({x, y, z});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nBuilderColor(JNIEnv*, jclass, jlong nativeBuilder, jfloat r, jfloat g, jfloat b) {
    ((LightManager::Builder*) nativeBuilder)->color({r, g, b});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nBuilderIntensity(JNIEnv*, jclass, jlong nativeBuilder, jfloat intensity) {
    ((LightManager::Builder*) nativeBuilder)->intensity(intensity);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nBuilderIntensityWatts(JNIEnv*, jclass, jlong nativeBuilder, jfloat watts, jfloat efficiency) {
    ((LightManager::Builder*) nativeBuilder)->intensity(watts, efficiency);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nBuilderIntensityCandela(JNIEnv*, jclass, jlong nativeBuilder, jfloat intensity) {
    ((LightManager::Builder*) nativeBuilder)->intensityCandela(intensity);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nBuilderFalloff(JNIEnv*, jclass, jlong nativeBuilder, jfloat radius) {
    ((LightManager::Builder*) nativeBuilder)->falloff(radius);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nBuilderSpotLightCone(JNIEnv*, jclass, jlong nativeBuilder, jfloat inner, jfloat outer) {
    ((LightManager::Builder*) nativeBuilder)->spotLightCone(inner, outer);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nBuilderAngularRadius(JNIEnv*, jclass, jlong nativeBuilder, jfloat radius) {
    ((LightManager::Builder*) nativeBuilder)->sunAngularRadius(radius);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nBuilderHaloSize(JNIEnv*, jclass, jlong nativeBuilder, jfloat size) {
    ((LightManager::Builder*) nativeBuilder)->sunHaloSize(size);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nBuilderHaloFalloff(JNIEnv*, jclass, jlong nativeBuilder, jfloat falloff) {
    ((LightManager::Builder*) nativeBuilder)->sunHaloFalloff(falloff);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nBuilderBuild(JNIEnv*, jclass, jlong nativeBuilder, jlong nativeEngine, jint entity) {
    LightManager::Builder* builder = (LightManager::Builder*) nativeBuilder;
    Engine* engine = (Engine*) nativeEngine;
    return (jboolean) (builder->build(*engine, (Entity&) entity) == LightManager::Builder::Success);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nSetIntensity(JNIEnv*, jclass, jlong nativeLightManager, jint instance, jfloat intensity) {
    ((LightManager*) nativeLightManager)->setIntensity((LightManager::Instance) instance, intensity);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nSetIntensityCandela(JNIEnv*, jclass, jlong nativeLightManager, jint instance, jfloat intensity) {
    ((LightManager*) nativeLightManager)->setIntensityCandela((LightManager::Instance) instance, intensity);
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nGetIntensity(JNIEnv*, jclass, jlong nativeLightManager, jint instance) {
    return ((LightManager*) nativeLightManager)->getIntensity((LightManager::Instance) instance);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nSetColor(JNIEnv*, jclass, jlong nativeLightManager, jint instance, jfloat r, jfloat g, jfloat b) {
    ((LightManager*) nativeLightManager)->setColor((LightManager::Instance) instance, {r, g, b});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nGetColor(JNIEnv* env, jclass, jlong nativeLightManager, jint instance, jfloatArray out_) {
    jfloat* out = env->GetFloatArrayElements(out_, NULL);
    *(filament::math::float3*)out = ((LightManager*) nativeLightManager)->getColor((LightManager::Instance) instance);
    env->ReleaseFloatArrayElements(out_, out, 0);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nSetDirection(JNIEnv*, jclass, jlong nativeLightManager, jint instance, jfloat x, jfloat y, jfloat z) {
    ((LightManager*) nativeLightManager)->setDirection((LightManager::Instance) instance, {x, y, z});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nGetDirection(JNIEnv* env, jclass, jlong nativeLightManager, jint instance, jfloatArray out_) {
    jfloat* out = env->GetFloatArrayElements(out_, NULL);
    *(filament::math::float3*)out = ((LightManager*) nativeLightManager)->getDirection((LightManager::Instance) instance);
    env->ReleaseFloatArrayElements(out_, out, 0);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nSetPosition(JNIEnv*, jclass, jlong nativeLightManager, jint instance, jfloat x, jfloat y, jfloat z) {
    ((LightManager*) nativeLightManager)->setPosition((LightManager::Instance) instance, {x, y, z});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nGetPosition(JNIEnv* env, jclass, jlong nativeLightManager, jint instance, jfloatArray out_) {
    jfloat* out = env->GetFloatArrayElements(out_, NULL);
    *(filament::math::float3*)out = ((LightManager*) nativeLightManager)->getPosition((LightManager::Instance) instance);
    env->ReleaseFloatArrayElements(out_, out, 0);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nSetFalloff(JNIEnv*, jclass, jlong nativeLightManager, jint instance, jfloat falloff) {
    ((LightManager*) nativeLightManager)->setFalloff((LightManager::Instance) instance, falloff);
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nGetFalloff(JNIEnv*, jclass, jlong nativeLightManager, jint instance) {
    return ((LightManager*) nativeLightManager)->getFalloff((LightManager::Instance) instance);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nSetSpotLightCone(JNIEnv*, jclass, jlong nativeLightManager, jint instance, jfloat inner, jfloat outer) {
    ((LightManager*) nativeLightManager)->setSpotLightCone((LightManager::Instance) instance, inner, outer);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nSetSunAngularRadius(JNIEnv*, jclass, jlong nativeLightManager, jint instance, jfloat radius) {
    ((LightManager*) nativeLightManager)->setSunAngularRadius((LightManager::Instance) instance, radius);
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nGetSunAngularRadius(JNIEnv*, jclass, jlong nativeLightManager, jint instance) {
    return ((LightManager*) nativeLightManager)->getSunAngularRadius((LightManager::Instance) instance);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nSetSunHaloSize(JNIEnv*, jclass, jlong nativeLightManager, jint instance, jfloat size) {
    ((LightManager*) nativeLightManager)->setSunHaloSize((LightManager::Instance) instance, size);
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nGetSunHaloSize(JNIEnv*, jclass, jlong nativeLightManager, jint instance) {
    return ((LightManager*) nativeLightManager)->getSunHaloSize((LightManager::Instance) instance);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nSetSunHaloFalloff(JNIEnv*, jclass, jlong nativeLightManager, jint instance, jfloat falloff) {
    ((LightManager*) nativeLightManager)->setSunHaloFalloff((LightManager::Instance) instance, falloff);
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nGetSunHaloFalloff(JNIEnv*, jclass, jlong nativeLightManager, jint instance) {
    return ((LightManager*) nativeLightManager)->getSunHaloFalloff((LightManager::Instance) instance);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nSetLightChannel(JNIEnv*, jclass, jlong nativeLightManager, jint instance, jint channel, jboolean enable) {
    ((LightManager*) nativeLightManager)->setLightChannel((LightManager::Instance) instance, (uint32_t) channel, (bool) enable);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nGetLightChannel(JNIEnv*, jclass, jlong nativeLightManager, jint instance, jint channel) {
    return (jboolean) ((LightManager*) nativeLightManager)->getLightChannel((LightManager::Instance) instance, (uint32_t) channel);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nSetShadowOptions(JNIEnv* env, jclass, jlong nativeLightManager, jint instance, jint mapSize, jint cascades, jfloatArray splitPositions, jfloat constantBias, jfloat normalBias, jfloat shadowFar, jfloat shadowNearHint, jfloat shadowFarHint, jboolean stable, jboolean lispsm, jfloat polygonOffsetConstant, jfloat polygonOffsetSlope, jboolean screenSpaceContactShadows, jint stepCount, jfloat maxShadowDistance, jboolean elvsm, jfloat blurWidth, jfloat shadowBulbRadius, jfloatArray transform) {
    LightManager::ShadowOptions options;
    fillShadowOptions(env, options, mapSize, cascades, splitPositions, constantBias, normalBias, shadowFar, shadowNearHint, shadowFarHint, stable, lispsm, polygonOffsetConstant, polygonOffsetSlope, screenSpaceContactShadows, stepCount, maxShadowDistance, elvsm, blurWidth, shadowBulbRadius, transform);
    ((LightManager*) nativeLightManager)->setShadowOptions((LightManager::Instance) instance, options);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nSetCastShadows(JNIEnv*, jclass, jlong nativeLightManager, jint instance, jboolean enabled) {
    ((LightManager*) nativeLightManager)->setShadowCaster((LightManager::Instance) instance, (bool) enabled);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nIsCastingShadows(JNIEnv*, jclass, jlong nativeLightManager, jint instance) {
    return (jboolean) ((LightManager*) nativeLightManager)->isShadowCaster((LightManager::Instance) instance);
}


JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nGetType(JNIEnv*, jclass, jlong nativeLightManager, jint instance) {
    return (jint) ((LightManager*) nativeLightManager)->getType((LightManager::Instance) instance);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nIsDirectional(JNIEnv*, jclass, jlong nativeLightManager, jint instance) {
    return (jboolean) ((LightManager*) nativeLightManager)->isDirectional((LightManager::Instance) instance);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nIsPointLight(JNIEnv*, jclass, jlong nativeLightManager, jint instance) {
    return (jboolean) ((LightManager*) nativeLightManager)->isPointLight((LightManager::Instance) instance);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nIsSpotLight(JNIEnv*, jclass, jlong nativeLightManager, jint instance) {
    return (jboolean) ((LightManager*) nativeLightManager)->isSpotLight((LightManager::Instance) instance);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nComputeUniformSplits(JNIEnv* env, jclass, jfloatArray splitPositions, jint cascades) {
    jfloat* nativeSplits = env->GetFloatArrayElements(splitPositions, NULL);
    LightManager::ShadowCascades::computeUniformSplits(nativeSplits, (uint8_t) cascades);
    env->ReleaseFloatArrayElements(splitPositions, nativeSplits, 0);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nComputeLogSplits(JNIEnv* env, jclass, jfloatArray splitPositions, jint cascades, jfloat near, jfloat far) {
    jfloat* nativeSplits = env->GetFloatArrayElements(splitPositions, NULL);
    LightManager::ShadowCascades::computeLogSplits(nativeSplits, (uint8_t) cascades, near, far);
    env->ReleaseFloatArrayElements(splitPositions, nativeSplits, 0);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nComputePracticalSplits(JNIEnv* env, jclass, jfloatArray splitPositions, jint cascades, jfloat near, jfloat far, jfloat lambda) {
    jfloat* nativeSplits = env->GetFloatArrayElements(splitPositions, NULL);
    LightManager::ShadowCascades::computePracticalSplits(nativeSplits, (uint8_t) cascades, near, far, lambda);
    env->ReleaseFloatArrayElements(splitPositions, nativeSplits, 0);
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nGetInnerConeAngle(JNIEnv*, jclass, jlong nativeLightManager, jint instance) {
    return ((LightManager*) nativeLightManager)->getSpotLightInnerCone((LightManager::Instance) instance);
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_jni_LightManager_nGetOuterConeAngle(JNIEnv*, jclass, jlong nativeLightManager, jint instance) {
    return ((LightManager*) nativeLightManager)->getSpotLightOuterCone((LightManager::Instance) instance);
}

} // extern "C"
