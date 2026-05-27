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
#include <filament/RenderableManager.h>
#include <filament/MaterialInstance.h>
#include <filament/VertexBuffer.h>
#include <filament/IndexBuffer.h>
#include <filament/MorphTargetBuffer.h>
#include <filament/SkinningBuffer.h>
#include <filament/Engine.h>
#include <utils/Entity.h>
#include <math/mat4.h>

#include "common/NioUtils.h"

using namespace filament;
using namespace utils;

extern "C" {

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nHasComponent(JNIEnv*, jclass, jlong nativeRenderableManager, jint entity) {
    RenderableManager* rm = (RenderableManager*) nativeRenderableManager;
    return (jboolean) rm->hasComponent((Entity&) entity);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nGetInstance(JNIEnv*, jclass, jlong nativeRenderableManager, jint entity) {
    RenderableManager* rm = (RenderableManager*) nativeRenderableManager;
    return (jint) rm->getInstance((Entity&) entity);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nDestroy(JNIEnv*, jclass, jlong nativeRenderableManager, jint entity) {
    RenderableManager* rm = (RenderableManager*) nativeRenderableManager;
    rm->destroy((Entity&) entity);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nCreateBuilder(JNIEnv*, jclass, jint count) {
    return (jlong) new RenderableManager::Builder((size_t) count);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nDestroyBuilder(JNIEnv*, jclass, jlong nativeBuilder) {
    delete (RenderableManager::Builder*) nativeBuilder;
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderGeometry(JNIEnv*, jclass, jlong nativeBuilder, jint index, jint type, jlong nativeVb, jlong nativeIb, jint offset, jint minIndex, jint maxIndex, jint count) {
    ((RenderableManager::Builder*) nativeBuilder)->geometry((size_t) index, (RenderableManager::PrimitiveType) type, (VertexBuffer*) nativeVb, (IndexBuffer*) nativeIb, (size_t) offset, (size_t) minIndex, (size_t) maxIndex, (size_t) count);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderGeometryShort(JNIEnv*, jclass, jlong nativeBuilder, jint index, jint type, jlong nativeVb, jlong nativeIb, jint offset, jint count) {
    ((RenderableManager::Builder*) nativeBuilder)->geometry((size_t) index, (RenderableManager::PrimitiveType) type, (VertexBuffer*) nativeVb, (IndexBuffer*) nativeIb, (size_t) offset, (size_t) count);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderGeometryNone(JNIEnv*, jclass, jlong nativeBuilder, jint index, jint type, jlong nativeVb, jlong nativeIb) {
    ((RenderableManager::Builder*) nativeBuilder)->geometry((size_t) index, (RenderableManager::PrimitiveType) type, (VertexBuffer*) nativeVb, (IndexBuffer*) nativeIb);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderGeometryNonIndexed(JNIEnv*, jclass, jlong nativeBuilder, jint index, jint type, jlong nativeVb, jint offset, jint count) {
    ((RenderableManager::Builder*) nativeBuilder)->geometry((size_t) index, (RenderableManager::PrimitiveType) type, (VertexBuffer*) nativeVb, (size_t) offset, (size_t) count);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderGeometryNonIndexedNone(JNIEnv*, jclass, jlong nativeBuilder, jint index, jint type, jlong nativeVb) {
    ((RenderableManager::Builder*) nativeBuilder)->geometry((size_t) index, (RenderableManager::PrimitiveType) type, (VertexBuffer*) nativeVb);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderGeometryType(JNIEnv*, jclass, jlong nativeBuilder, jint type) {
    ((RenderableManager::Builder*) nativeBuilder)->geometryType((RenderableManager::Builder::GeometryType) type);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderMaterial(JNIEnv*, jclass, jlong nativeBuilder, jint index, jlong nativeMi) {
    ((RenderableManager::Builder*) nativeBuilder)->material((size_t) index, (MaterialInstance*) nativeMi);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderBlendOrder(JNIEnv*, jclass, jlong nativeBuilder, jint index, jint blendOrder) {
    ((RenderableManager::Builder*) nativeBuilder)->blendOrder((size_t) index, (uint16_t) blendOrder);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderGlobalBlendOrderEnabled(JNIEnv*, jclass, jlong nativeBuilder, jint index, jboolean enabled) {
    ((RenderableManager::Builder*) nativeBuilder)->globalBlendOrderEnabled((size_t) index, (bool) enabled);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderBoundingBox(JNIEnv*, jclass, jlong nativeBuilder, jfloat cx, jfloat cy, jfloat cz, jfloat ex, jfloat ey, jfloat ez) {
    ((RenderableManager::Builder*) nativeBuilder)->boundingBox({{cx, cy, cz}, {ex, ey, ez}});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderLayerMask(JNIEnv*, jclass, jlong nativeBuilder, jint select, jint value) {
    ((RenderableManager::Builder*) nativeBuilder)->layerMask((uint8_t) select, (uint8_t) value);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderPriority(JNIEnv*, jclass, jlong nativeBuilder, jint priority) {
    ((RenderableManager::Builder*) nativeBuilder)->priority((uint8_t) priority);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderChannel(JNIEnv*, jclass, jlong nativeBuilder, jint channel) {
    ((RenderableManager::Builder*) nativeBuilder)->channel((uint8_t) channel);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderCulling(JNIEnv*, jclass, jlong nativeBuilder, jboolean enabled) {
    ((RenderableManager::Builder*) nativeBuilder)->culling((bool) enabled);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderLightChannel(JNIEnv*, jclass, jlong nativeBuilder, jint channel, jboolean enable) {
    ((RenderableManager::Builder*) nativeBuilder)->lightChannel((uint32_t) channel, (bool) enable);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderInstances(JNIEnv*, jclass, jlong nativeBuilder, jint instanceCount) {
    ((RenderableManager::Builder*) nativeBuilder)->instances((size_t) instanceCount);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderCastShadows(JNIEnv*, jclass, jlong nativeBuilder, jboolean enabled) {
    ((RenderableManager::Builder*) nativeBuilder)->castShadows((bool) enabled);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderReceiveShadows(JNIEnv*, jclass, jlong nativeBuilder, jboolean enabled) {
    ((RenderableManager::Builder*) nativeBuilder)->receiveShadows((bool) enabled);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderScreenSpaceContactShadows(JNIEnv*, jclass, jlong nativeBuilder, jboolean enabled) {
    ((RenderableManager::Builder*) nativeBuilder)->screenSpaceContactShadows((bool) enabled);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderEnableSkinningBuffers(JNIEnv*, jclass, jlong nativeBuilder, jboolean enabled) {
    ((RenderableManager::Builder*) nativeBuilder)->enableSkinningBuffers((bool) enabled);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderFog(JNIEnv*, jclass, jlong nativeBuilder, jboolean enabled) {
    ((RenderableManager::Builder*) nativeBuilder)->fog((bool) enabled);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderSkinningBuffer(JNIEnv*, jclass, jlong nativeBuilder, jlong nativeSkinningBuffer, jint boneCount, jint offset) {
    ((RenderableManager::Builder*) nativeBuilder)->skinning((SkinningBuffer*) nativeSkinningBuffer, (size_t) boneCount, (size_t) offset);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderSkinning(JNIEnv*, jclass, jlong nativeBuilder, jint boneCount) {
    ((RenderableManager::Builder*) nativeBuilder)->skinning((size_t) boneCount);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderSkinningBones(JNIEnv* env, jclass, jlong nativeBuilder, jint boneCount, jobject bones, jint remaining) {
    RenderableManager::Builder* builder = (RenderableManager::Builder*) nativeBuilder;
    AutoBuffer nioBuffer(env, bones, boneCount * 8);
    void* data = nioBuffer.getData();
    if (nioBuffer.getSize() > (size_t)(remaining << nioBuffer.getShift())) return -1;
    builder->skinning((size_t) boneCount, (RenderableManager::Bone const*) data);
    return 0;
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderMorphing(JNIEnv*, jclass, jlong nativeBuilder, jint targetCount) {
    ((RenderableManager::Builder*) nativeBuilder)->morphing((size_t) targetCount);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderMorphingStandard(JNIEnv*, jclass, jlong nativeBuilder, jlong nativeMorphTargetBuffer) {
    ((RenderableManager::Builder*) nativeBuilder)->morphing((MorphTargetBuffer*) nativeMorphTargetBuffer);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderSetMorphTargetBufferOffsetAtAt(JNIEnv*, jclass, jlong nativeBuilder, jint level, jint primitiveIndex, jint offset) {
    ((RenderableManager::Builder*) nativeBuilder)->morphing((uint8_t) level, (size_t) primitiveIndex, (size_t) offset);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nBuilderBuild(JNIEnv*, jclass, jlong nativeBuilder, jlong nativeEngine, jint entity) {
    RenderableManager::Builder* builder = (RenderableManager::Builder*) nativeBuilder;
    Engine* engine = (Engine*) nativeEngine;
    return (jboolean) (builder->build(*engine, (Entity&) entity) == RenderableManager::Builder::Success);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nSetLayerMask(JNIEnv*, jclass, jlong nativeManager, jint i, jint select, jint value) {
    ((RenderableManager*) nativeManager)->setLayerMask((RenderableManager::Instance) i, (uint8_t) select, (uint8_t) value);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nGetLayerMask(JNIEnv*, jclass, jlong nativeManager, jint i) {
    return (jint) ((RenderableManager*) nativeManager)->getLayerMask((RenderableManager::Instance) i);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nSetPriority(JNIEnv*, jclass, jlong nativeManager, jint i, jint priority) {
    ((RenderableManager*) nativeManager)->setPriority((RenderableManager::Instance) i, (uint8_t) priority);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nGetPriority(JNIEnv*, jclass, jlong nativeManager, jint i) {
    return (jint) ((RenderableManager*) nativeManager)->getPriority((RenderableManager::Instance) i);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nSetChannel(JNIEnv*, jclass, jlong nativeManager, jint i, jint channel) {
    ((RenderableManager*) nativeManager)->setChannel((RenderableManager::Instance) i, (uint8_t) channel);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nGetChannel(JNIEnv*, jclass, jlong nativeManager, jint i) {
    return (jint) ((RenderableManager*) nativeManager)->getChannel((RenderableManager::Instance) i);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nSetCulling(JNIEnv*, jclass, jlong nativeManager, jint i, jboolean enabled) {
    ((RenderableManager*) nativeManager)->setCulling((RenderableManager::Instance) i, (bool) enabled);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nIsCullingEnabled(JNIEnv*, jclass, jlong nativeManager, jint i) {
    return (jboolean) ((RenderableManager*) nativeManager)->isCullingEnabled((RenderableManager::Instance) i);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nSetFogEnabled(JNIEnv*, jclass, jlong nativeManager, jint i, jboolean enabled) {
    ((RenderableManager*) nativeManager)->setFogEnabled((RenderableManager::Instance) i, (bool) enabled);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nGetFogEnabled(JNIEnv*, jclass, jlong nativeManager, jint i) {
    return (jboolean) ((RenderableManager*) nativeManager)->getFogEnabled((RenderableManager::Instance) i);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nSetLightChannel(JNIEnv*, jclass, jlong nativeManager, jint i, jint channel, jboolean enable) {
    ((RenderableManager*) nativeManager)->setLightChannel((RenderableManager::Instance) i, (uint32_t) channel, (bool) enable);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nGetLightChannel(JNIEnv*, jclass, jlong nativeManager, jint i, jint channel) {
    return (jboolean) ((RenderableManager*) nativeManager)->getLightChannel((RenderableManager::Instance) i, (uint32_t) channel);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nSetCastShadows(JNIEnv*, jclass, jlong nativeManager, jint i, jboolean enabled) {
    ((RenderableManager*) nativeManager)->setCastShadows((RenderableManager::Instance) i, (bool) enabled);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nIsShadowCastingEnabled(JNIEnv*, jclass, jlong nativeManager, jint i) {
    return (jboolean) ((RenderableManager*) nativeManager)->isShadowCaster((RenderableManager::Instance) i);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nSetReceiveShadows(JNIEnv*, jclass, jlong nativeManager, jint i, jboolean enabled) {
    ((RenderableManager*) nativeManager)->setReceiveShadows((RenderableManager::Instance) i, (bool) enabled);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nIsShadowReceivingEnabled(JNIEnv*, jclass, jlong nativeManager, jint i) {
    return (jboolean) ((RenderableManager*) nativeManager)->isShadowReceiver((RenderableManager::Instance) i);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nSetScreenSpaceContactShadows(JNIEnv*, jclass, jlong nativeManager, jint i, jboolean enabled) {
    ((RenderableManager*) nativeManager)->setScreenSpaceContactShadows((RenderableManager::Instance) i, (bool) enabled);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nSetMaterialInstanceAt(JNIEnv*, jclass, jlong nativeManager, jint i, jint primitiveIndex, jlong nativeMi) {
    ((RenderableManager*) nativeManager)->setMaterialInstanceAt((RenderableManager::Instance) i, (size_t) primitiveIndex, (MaterialInstance*) nativeMi);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nGetMaterialInstanceAt(JNIEnv*, jclass, jlong nativeManager, jint i, jint primitiveIndex) {
    return (jlong) ((RenderableManager*) nativeManager)->getMaterialInstanceAt((RenderableManager::Instance) i, (size_t) primitiveIndex);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nSetBlendOrderAt(JNIEnv*, jclass, jlong nativeManager, jint i, jint primitiveIndex, jint blendOrder) {
    ((RenderableManager*) nativeManager)->setBlendOrderAt((RenderableManager::Instance) i, (size_t) primitiveIndex, (uint16_t) blendOrder);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nGetBlendOrderAt(JNIEnv*, jclass, jlong nativeManager, jint i, jint primitiveIndex) {
    return (jint) ((RenderableManager*) nativeManager)->getBlendOrderAt((RenderableManager::Instance) i, (size_t) primitiveIndex);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nGetEnabledAttributesAt(JNIEnv*, jclass, jlong nativeManager, jint i, jint primitiveIndex) {
    return (jint) ((RenderableManager*) nativeManager)->getEnabledAttributesAt((RenderableManager::Instance) i, (size_t) primitiveIndex).getValue();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nGetPrimitiveCount(JNIEnv*, jclass, jlong nativeManager, jint i) {
    return (jint) ((RenderableManager*) nativeManager)->getPrimitiveCount((RenderableManager::Instance) i);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nSetMorphWeights(JNIEnv* env, jclass, jlong nativeManager, jint i, jfloatArray weights, jint offset) {
    jfloat* vec = env->GetFloatArrayElements(weights, NULL);
    jsize count = env->GetArrayLength(weights);
    ((RenderableManager*) nativeManager)->setMorphWeights((RenderableManager::Instance) i, vec, (size_t) count, (size_t) offset);
    env->ReleaseFloatArrayElements(weights, vec, JNI_ABORT);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nSetMorphTargetBufferOffsetAt(JNIEnv*, jclass, jlong nativeManager, jint i, jint level, jint primitiveIndex, jint offset) {
    ((RenderableManager*) nativeManager)->setMorphTargetBufferOffsetAt((RenderableManager::Instance) i, (uint8_t) level, (size_t) primitiveIndex, (size_t) offset);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nGetMorphTargetCount(JNIEnv*, jclass, jlong nativeManager, jint i) {
    return (jint) ((RenderableManager*) nativeManager)->getMorphTargetCount((RenderableManager::Instance) i);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nSetAxisAlignedBoundingBox(JNIEnv*, jclass, jlong nativeManager, jint i, jfloat cx, jfloat cy, jfloat cz, jfloat ex, jfloat ey, jfloat ez) {
    ((RenderableManager*) nativeManager)->setAxisAlignedBoundingBox((RenderableManager::Instance) i, {{cx, cy, cz}, {ex, ey, ez}});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nSetSkinningBuffer(JNIEnv*, jclass, jlong nativeManager, jint i, jlong nativeBuffer, jint count, jint offset) {
    ((RenderableManager*) nativeManager)->setSkinningBuffer((RenderableManager::Instance) i, (SkinningBuffer*) nativeBuffer, (size_t) count, (size_t) offset);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nSetBonesAsMatrices(JNIEnv* env, jclass, jlong nativeManager, jint i, jobject matrices, jint remaining, jint boneCount, jint offset) {
    RenderableManager* rm = (RenderableManager*) nativeManager;
    AutoBuffer nioBuffer(env, matrices, boneCount * 16);
    void* data = nioBuffer.getData();
    if (nioBuffer.getSize() > (size_t)(remaining << nioBuffer.getShift())) return -1;
    rm->setBones((RenderableManager::Instance) i, (filament::math::mat4f const*) data, (size_t) boneCount, (size_t) offset);
    return 0;
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nSetBonesAsQuaternions(JNIEnv* env, jclass, jlong nativeManager, jint i, jobject quaternions, jint remaining, jint boneCount, jint offset) {
    RenderableManager* rm = (RenderableManager*) nativeManager;
    AutoBuffer nioBuffer(env, quaternions, boneCount * 8);
    void* data = nioBuffer.getData();
    if (nioBuffer.getSize() > (size_t)(remaining << nioBuffer.getShift())) return -1;
    rm->setBones((RenderableManager::Instance) i, (RenderableManager::Bone const*) data, (size_t) boneCount, (size_t) offset);
    return 0;
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nGetInstanceCount(JNIEnv*, jclass, jlong nativeManager, jint i) {
    return (jint) ((RenderableManager*) nativeManager)->getInstanceCount((RenderableManager::Instance) i);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nClearMaterialInstanceAt(JNIEnv*, jclass, jlong nativeManager, jint i, jint primitiveIndex) {
    ((RenderableManager*) nativeManager)->clearMaterialInstanceAt((RenderableManager::Instance) i, (size_t) primitiveIndex);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nSetGeometryAt(JNIEnv*, jclass, jlong nativeManager, jint i, jint primitiveIndex, jint primitiveType, jlong nativeVertexBuffer, jlong nativeIndexBuffer, jint offset, jint count) {
    RenderableManager* rm = (RenderableManager*) nativeManager;
    rm->setGeometryAt((RenderableManager::Instance) i, (size_t) primitiveIndex, (RenderableManager::PrimitiveType) primitiveType, (VertexBuffer*) nativeVertexBuffer, (IndexBuffer*) nativeIndexBuffer, (size_t) offset, (size_t) count);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nSetGeometryAtNonIndexed(JNIEnv*, jclass, jlong nativeManager, jint i, jint primitiveIndex, jint primitiveType, jlong nativeVertexBuffer, jint offset, jint count) {
    RenderableManager* rm = (RenderableManager*) nativeManager;
    rm->setGeometryAt((RenderableManager::Instance) i, (size_t) primitiveIndex, (RenderableManager::PrimitiveType) primitiveType, (VertexBuffer*) nativeVertexBuffer, (size_t) offset, (size_t) count);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nIsShadowCaster(JNIEnv*, jclass, jlong nativeManager, jint i) {
    return (jboolean) ((RenderableManager*) nativeManager)->isShadowCaster((RenderableManager::Instance) i);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nIsShadowReceiver(JNIEnv*, jclass, jlong nativeManager, jint i) {
    return (jboolean) ((RenderableManager*) nativeManager)->isShadowReceiver((RenderableManager::Instance) i);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nIsScreenSpaceContactShadowsEnabled(JNIEnv*, jclass, jlong nativeManager, jint i) {
    return (jboolean) ((RenderableManager*) nativeManager)->isScreenSpaceContactShadowsEnabled((RenderableManager::Instance) i);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nGetAxisAlignedBoundingBox(JNIEnv* env, jclass, jlong nativeManager, jint i, jfloatArray center_, jfloatArray halfExtent_) {
    RenderableManager* rm = (RenderableManager*) nativeManager;
    jfloat* center = env->GetFloatArrayElements(center_, NULL);
    jfloat* halfExtent = env->GetFloatArrayElements(halfExtent_, NULL);
    Box const& aabb = rm->getAxisAlignedBoundingBox((RenderableManager::Instance) i);
    *reinterpret_cast<filament::math::float3*>(center) = aabb.center;
    *reinterpret_cast<filament::math::float3*>(halfExtent) = aabb.halfExtent;
    env->ReleaseFloatArrayElements(center_, center, 0);
    env->ReleaseFloatArrayElements(halfExtent_, halfExtent, 0);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nSetGlobalBlendOrderEnabledAt(JNIEnv*, jclass, jlong nativeManager, jint i, jint primitiveIndex, jboolean enabled) {
    ((RenderableManager*) nativeManager)->setGlobalBlendOrderEnabledAt((RenderableManager::Instance) i, (size_t) primitiveIndex, (bool) enabled);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_RenderableManager_nIsGlobalBlendOrderEnabledAt(JNIEnv*, jclass, jlong nativeManager, jint i, jint primitiveIndex) {
    return (jboolean) ((RenderableManager*) nativeManager)->isGlobalBlendOrderEnabledAt((RenderableManager::Instance) i, (size_t) primitiveIndex);
}

} // extern "C"
